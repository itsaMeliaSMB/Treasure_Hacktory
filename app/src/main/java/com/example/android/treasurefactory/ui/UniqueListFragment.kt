package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.MultiselectRecyclerAdapter
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardListItemBinding
import com.example.android.treasurefactory.databinding.LayoutUniqueListBinding
import com.example.android.treasurefactory.databinding.UniqueListItemBinding
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.viewmodel.UniqueListViewModel
import com.example.android.treasurefactory.viewmodel.UniqueListViewModelFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class UniqueListFragment : Fragment() {

    interface Callbacks{
        fun onUniqueSelected(view: View, itemID: Int, itemType: UniqueItemType, hoardID: Int)
    }

    private val backCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {

            if (uniqueListViewModel.isRunningAsyncLiveData.value != true) {

                actionMode?.finish()

                findNavController().popBackStack()

            } else {

                Toast.makeText(context,"Cannot navigate back; still manipulating treasure.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // region [ Property declarations ]

    private lateinit var parentHoard : Hoard
    private lateinit var itemType : UniqueItemType

    val safeArgs : UniqueListFragmentArgs by navArgs()

    private var shortAnimationDuration = 0
    private var isWaitingCardAnimating = false

    private var callbacks: Callbacks? = null

    private var _binding: LayoutUniqueListBinding? = null
    private val binding get() = _binding!!

    private var uniqueAdapter: UniqueAdapter? = UniqueAdapter(emptyList())

    private val uniqueListViewModel: UniqueListViewModel by viewModels {
        UniqueListViewModelFactory((this.requireActivity().application as TreasureHacktoryApplication).repository)
    }

    private var actionMode : ActionMode? = null
    private val actionModeCallback = ActionModeCallback()

    // endregion

    // region [ Overridden functions ]

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentHoard = Hoard()

        val hoardID: Int = safeArgs.hoardID
        itemType = safeArgs.listType

        uniqueListViewModel.loadHoardInfo(hoardID, itemType)

        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        @StyleRes
        val itemTheme : Int
        @ColorInt
        val newStatusBarColor : Int

        when(itemType){
            UniqueItemType.GEM -> {
                itemTheme = R.style.GemSubStyle
                newStatusBarColor = R.color.gemPrimaryDark
            }
            UniqueItemType.ART_OBJECT -> {
                itemTheme = R.style.ArtObjectSubStyle
                newStatusBarColor = R.color.artPrimaryDark
            }
            UniqueItemType.MAGIC_ITEM -> {
                itemTheme = R.style.MagicItemSubStyle
                newStatusBarColor = R.color.magicPrimaryDark
            }
            UniqueItemType.SPELL_COLLECTION -> {
                itemTheme = R.style.SpellCollectionSubStyle
                newStatusBarColor = R.color.spellPrimaryDark
            }
        }

        requireActivity().window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(newStatusBarColor,null)
        }

        val contextThemeWrapper = ContextThemeWrapper(context,itemTheme)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)

        // Inflate the layout for this fragment
        _binding = LayoutUniqueListBinding.inflate(localInflater, container, false)
        val view = binding.root

        // Give RecyclerView a Layout manager [required]
        binding.uniqueListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = uniqueAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listeners
        uniqueListViewModel.apply{

            exposedHoardLiveData.observe(viewLifecycleOwner) { readyHoard ->

                if (readyHoard != null) {
                    parentHoard = readyHoard

                    val hoardID: Int = safeArgs.hoardID
                    val itemType: UniqueItemType = safeArgs.listType

                    uniqueListViewModel.updateUniqueItems(hoardID, itemType)
                }
            }

            uniqueItemsLiveData.observe(viewLifecycleOwner) { itemList ->

                updateUI(itemList)

                if (itemList.isEmpty()) {

                    binding.uniqueListWhenemptyGroup.visibility = View.VISIBLE
                    binding.uniqueListRecycler.visibility = View.GONE

                } else {

                    binding.uniqueListWhenemptyGroup.visibility = View.GONE
                    binding.uniqueListRecycler.visibility = View.VISIBLE
                }
            }

            isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

                if (isRunningAsync) {

                    // binding.uniqueDetailsViewableGroup.isEnabled = false

                    if (binding.uniqueListWaitingCard.waitingCard.visibility == View.GONE &&
                        !isWaitingCardAnimating
                    ) {

                        fadeInWaitingCard()

                    } else {

                        binding.uniqueListWaitingCard.waitingCard.visibility = View.VISIBLE
                        isWaitingCardAnimating = false
                    }

                } else {

                    //binding.uniqueDetailsViewableGroup.isEnabled = true

                    if (binding.uniqueListWaitingCard.waitingCard.visibility == View.VISIBLE &&
                        !isWaitingCardAnimating
                    ) {

                        fadeOutWaitingCard()

                    } else {

                        binding.uniqueListWaitingCard.waitingCard.visibility = View.GONE
                        isWaitingCardAnimating = false
                    }
                }
            }

            textToastHolderLiveData.observe(viewLifecycleOwner) { pendingAlert ->

                if (pendingAlert != null) {

                    // Show the pending toast
                    Toast.makeText(context, pendingAlert.first, pendingAlert.second).show()

                    // Clear the livedata for pending toasts
                    uniqueListViewModel.textToastHolderLiveData.value = null
                }
            }

            hoardsLiveData.observe(viewLifecycleOwner) { fetchedHoards ->

                if (fetchedHoards != null) {

                    showTargetHoardDialog(fetchedHoards)

                    hoardsLiveData.value = null
                }
            }
        }

        // Set up toolbar
        binding.uniqueListToolbar.apply {

            // Get themed color attribute for Toolbar's title
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val colorOnPrimary = typedValue.data

            inflateMenu(R.menu.unique_list_toolbar_menu)
            title = when(safeArgs.listType){
                UniqueItemType.GEM  ->
                    resources.getString(R.string.viewer_gem_card_title)
                UniqueItemType.ART_OBJECT ->
                    resources.getString(R.string.viewer_art_object_cart_title)
                UniqueItemType.MAGIC_ITEM ->
                    resources.getString(R.string.viewer_magic_item_card_title)
                UniqueItemType.SPELL_COLLECTION ->
                    resources.getString(R.string.viewer_spell_collections_card_title)
            }
            setTitleTextColor(colorOnPrimary)
            setSubtitleTextColor(colorOnPrimary)
            subtitle = parentHoard.name
            setNavigationIcon(R.drawable.clipart_back_vector_icon)
            navigationIcon?.apply {
                setTint(colorOnPrimary)
            }
            overflowIcon?.apply{
                setTint(colorOnPrimary)
            }
            setNavigationOnClickListener {

                if (uniqueListViewModel.isRunningAsyncLiveData.value != true) {

                    actionMode?.finish()

                    findNavController().popBackStack()

                } else {

                    Toast.makeText(context,"Cannot navigate back; still manipulating treasure.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            setOnMenuItemClickListener { item ->

                // https://developer.android.com/guide/fragments/appbar#fragment-click
                when (item.itemId){

                    R.id.action_select_all_unique  -> {

                        (binding.uniqueListRecycler.adapter as UniqueListFragment.UniqueAdapter).selectAllItems()

                        true
                    }

                    // Default case
                    else    -> false
                }
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

    // region [ Inner classes ]

    private inner class UniqueViewHolder(val binding: UniqueListItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener{

        private lateinit var uniqueItem: ListableItem

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(newUniqueItem: ListableItem, selected: Boolean) {

            uniqueItem = newUniqueItem

            // Toggle background color of list item if selected
            binding.layoutUniqueListItem.isSelected = selected

            // Bind elements inferred from Listable
            binding.apply {

                when (uniqueItem.iFrameFlavor){
                    ItemFrameFlavor.NORMAL -> {
                        uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground)
                        uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_gray)
                    }
                    ItemFrameFlavor.CURSED -> {
                        uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground_cursed)
                        uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_cursed)
                    }
                    ItemFrameFlavor.GOLDEN -> {
                        uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground_golden)
                        uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_golden)
                    }
                }

                try{
                    uniqueListItemThumbnail.apply{
                        setImageResource(resources
                            .getIdentifier(uniqueItem.iconStr,
                                "drawable",view?.context?.packageName))
                        visibility = View.VISIBLE
                    }
                } catch (e: Exception){
                    uniqueListItemThumbnail.apply{
                        setImageResource(R.drawable.container_chest)
                        visibility = View.VISIBLE
                    }
                }

                uniqueListItemName.text = uniqueItem.name

                ("${
                    DecimalFormat("#,##0.0#")
                        .format(uniqueItem.gpValue)
                        .removeSuffix(".0")} gp")
                    .also {uniqueListItemGp.text = it}

                try{
                    uniqueListItemTypeIcon.apply{
                        setImageResource(resources
                            .getIdentifier(uniqueItem.endIconStr,
                                "drawable",view?.context?.packageName))
                        visibility = View.VISIBLE
                    }

                } catch (e: Exception){
                    uniqueListItemTypeIcon.apply{
                        setImageResource(R.drawable.clipart_prohibited_vector_icon)
                        visibility = View.VISIBLE
                    }
                }

                uniqueListItemTypeLabel.text = uniqueItem.endLabel
            }

            //Bind elements requiring type specificity
            when (uniqueItem){
                is ListableGem  -> {
                    // Set xp value
                    (NumberFormat.getNumberInstance().format(
                        (uniqueItem.gpValue / parentHoard.effortRating).toInt()) + " xp")
                        .also { binding.uniqueListItemXp.text = it }
                    // Hide badge
                    binding.uniqueListItemframeBadge.visibility = View.GONE
                }
                is ListableArtObject -> {
                    // Set xp value
                    (NumberFormat.getNumberInstance().format(
                        (uniqueItem.gpValue / parentHoard.effortRating).toInt()) + " xp")
                        .also { binding.uniqueListItemXp.text = it }
                    // Set badge
                    try{
                        binding.uniqueListItemframeBadge.apply{
                            setImageResource(resources
                                .getIdentifier(
                                    (uniqueItem as ListableArtObject).badgeStr,
                                    "drawable",view?.context?.packageName))
                            visibility = View.VISIBLE
                        }
                    } catch (e: Exception){
                        binding.uniqueListItemframeBadge.apply{
                            setImageResource(R.drawable.badge_hoard_broken)
                            visibility = View.VISIBLE
                        }
                    }
                }
                is ListableMagicItem -> {
                    // Set xp value
                    (NumberFormat.getNumberInstance().format(
                        (uniqueItem as ListableMagicItem).xpValue) + " xp")
                        .also { binding.uniqueListItemXp.text = it }
                    // Hide badge
                    binding.uniqueListItemframeBadge.visibility = View.GONE
                }
                is ListableSpellCollection -> {
                    // Set xp value
                    (NumberFormat.getNumberInstance().format(
                        (uniqueItem as ListableSpellCollection).xpValue) + " xp")
                        .also { binding.uniqueListItemXp.text = it }
                    // Set badge
                    binding.uniqueListItemframeBadge.apply{
                        when ((uniqueItem as ListableSpellCollection).discipline) {
                            SpCoDiscipline.ARCANE -> {
                                setImageResource(R.drawable.class_magic_user_colored)
                                visibility = View.VISIBLE
                            }
                            SpCoDiscipline.DIVINE -> {
                                setImageResource(R.drawable.class_cleric_colored)
                                visibility = View.VISIBLE
                            }
                            SpCoDiscipline.NATURAL -> {
                                setImageResource(R.drawable.class_druid_colored)
                                visibility = View.VISIBLE
                            }
                            SpCoDiscipline.ALL_MAGIC -> {
                                setImageResource(R.drawable.badge_hoard_magic)
                                visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        override fun onClick(v: View) {
            if (actionMode != null) {

                // Toggle HoardViewHolder's selection status
                (this@UniqueListFragment.binding.uniqueListRecycler.adapter
                        as MultiselectRecyclerAdapter).toggleSelection(adapterPosition)

            } else {

                callbacks?.onUniqueSelected(v,uniqueItem.id, when (uniqueItem){
                    is ListableGem -> UniqueItemType.GEM
                    is ListableArtObject -> UniqueItemType.ART_OBJECT
                    is ListableMagicItem -> UniqueItemType.MAGIC_ITEM
                    is ListableSpellCollection -> UniqueItemType.SPELL_COLLECTION
                },safeArgs.hoardID)
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (actionMode == null) {

                (this@UniqueListFragment.binding.uniqueListRecycler.adapter
                        as MultiselectRecyclerAdapter).toggleSelection(adapterPosition)

                return true
            }

            return false // Run onClick() instead
        }
    }

    private inner class UniqueAdapter(var items: List<ListableItem>)
        : MultiselectRecyclerAdapter<UniqueViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniqueViewHolder {
            val binding = UniqueListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return UniqueViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: UniqueViewHolder, position: Int) {
            val uniqueItem = items[position]

            viewHolder.bind(uniqueItem, isSelected(position))
        }

        override fun getItemCount(): Int = items.size

        // Functions affecting selectedItems
        override fun toggleSelection(position: Int) {
            super.toggleSelection(position)
            setActionModeFromCount()
        }

        override fun setPositions(positions: List<Int>, isNowSelected: Boolean) {
            super.setPositions(positions, isNowSelected)
            setActionModeFromCount()
        }

        override fun clearAllSelections() {
            super.clearAllSelections()
            actionMode?.finish()
        }

        fun selectAllItems() {
            if (items.isNotEmpty()) {
                val allHoardIndices = items.indices.toList()
                setPositions(allHoardIndices,true)
            }
            setActionModeFromCount()
        }

        /**
         * Returns a list of all ListableItems currently selected in the adapter.
         */
        fun getSelectedAsListableItems(): List<ListableItem> {
            // May be more resource-intensive to grab from adapter, but this ensures intended
            // items are affected.
            return items.filterIndexed { index, _ -> getSelectedPositions().contains(index) }
        }

        private fun setActionModeFromCount() {

            if (selectedCount == 0) {

                actionMode?.finish()

            } else {

                // Start action mode if not already active
                if (actionMode == null) {
                    actionMode = requireActivity().startActionMode(actionModeCallback)
                }

                actionMode?.title = "$selectedCount selected"

                // Refresh the action bar
                actionMode?.invalidate()
            }
        }
    }

    private inner class TargetHoardAdapter(val hoards : List<Hoard>) : RecyclerView.Adapter<TargetHoardAdapter.TargetHoardHolder>() {

        var selectedPos = -1
            private set
        private var lastSelectedPos = -1
        var selectedID = -1
            private set

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetHoardHolder{
            val binding = HoardListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return TargetHoardHolder(binding)
        }

        override fun onBindViewHolder(holder: TargetHoardHolder, position: Int) {
            val hoard = hoards[position]

            holder.bind(hoard, position)
        }

        override fun getItemCount(): Int = hoards.size

        inner class TargetHoardHolder(val binding: HoardListItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var hoard: Hoard

            @SuppressLint("SimpleDateFormat")
            fun bind(newHoard: Hoard, position: Int) {

                hoard = newHoard

                if (hoard.badge != HoardBadge.NONE) {
                    try{
                        binding.hoardListItemListBadge.apply{
                            setImageResource(resources
                                .getIdentifier(hoard.badge.resString,
                                    "drawable",view?.context?.packageName))
                            visibility = View.VISIBLE
                        }
                    } catch (e: Exception){
                        binding.hoardListItemListBadge.apply{
                            setImageResource(R.drawable.badge_hoard_broken)
                            visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.hoardListItemListBadge.visibility = View.INVISIBLE
                }

                // Toggle background color of list item if selected
                binding.layoutHoardListItem.apply {
                    isSelected = position == selectedPos
                    setOnClickListener {
                        if (position == selectedPos) {
                            // Deselect if selected
                            updateSelectedItem(-1, -1)
                        } else {
                            updateSelectedItem(position, hoard.hoardID)
                        }
                    }
                }

                // Set hoard name and [ NEW ] visibility
                binding.hoardListItemName.apply {
                    text = hoard.name
                    if (hoard.isNew) {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.clipart_new_vector_icon,0,0,0)
                    } else {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
                    }
                }

                // Set hoard date
                binding.hoardListItemDate.text =
                    SimpleDateFormat("MM/dd/yyyy").format(hoard.creationDate)

                // Set text for gp value counter
                ("Worth ${DecimalFormat("#,##0.0#")
                    .format(hoard.gpTotal)
                    .removeSuffix(".0")} gp").also { binding.hoardListItemGpValue.text = it }

                // Set text for unique item counters
                binding.hoardListItemGemCount.text = String.format("%03d",hoard.gemCount)
                binding.hoardListItemArtCount.text = String.format("%03d",hoard.artCount)
                binding.hoardListItemMagicCount.text = String.format("%03d",hoard.magicCount)
                binding.hoardListItemSpellCount.text = String.format("%03d",hoard.spellsCount)

                // Set icon for hoard
                try {

                    binding.hoardListItemListIcon
                        .setImageResource(resources
                            .getIdentifier(hoard.iconID,"drawable",view?.context?.packageName))

                } catch (e: Exception) {

                    binding.hoardListItemListIcon
                        .setImageResource(R.drawable.clipart_default_image)
                }

                binding.hoardListItemFavorited.visibility = View.GONE
            }
        }

        fun updateSelectedItem(selectionPos: Int, selectedHoardID: Int){

            lastSelectedPos = selectedPos
            selectedPos = selectionPos
            selectedID = selectedHoardID

            if (selectedPos in hoards.indices) {
                notifyItemChanged(selectedPos)
            }

            if (lastSelectedPos in hoards.indices) {
                notifyItemChanged(lastSelectedPos)
            }
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {

            // Inflate menu
            mode.menuInflater.inflate(R.menu.unique_list_action_menu,menu)

            @ColorInt
            val newStatusBarColor = resources.getColor(R.color.actionModePrimaryDark,null)

            // Change the status bar's color
            requireActivity().window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = newStatusBarColor
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {


                R.id.action_move_items -> {

                    uniqueListViewModel.fetchHoardsForDialog(parentHoard.hoardID)

                    true
                }

                R.id.action_select_all_items -> {

                    (binding.uniqueListRecycler.adapter as UniqueAdapter).selectAllItems()

                    true
                }

                R.id.action_sell_items -> {

                    val targetCount = (binding.uniqueListRecycler.adapter as UniqueAdapter)
                        .selectedCount

                    AlertDialog.Builder(requireContext())
                        .setMessage("$targetCount item(s) will be converted into coinage. " +
                                "Are you sure you want to proceed?")
                        .setPositiveButton(R.string.action_sell) { dialog, _ ->

                            uniqueListViewModel.sellSelectedItems(
                                (binding.uniqueListRecycler.adapter as UniqueAdapter)
                                    .getSelectedAsListableItems(),parentHoard)

                            actionMode?.finish()
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()

                    true
                }

                R.id.action_delete_items -> {

                    val targetCount = (binding.uniqueListRecycler.adapter as UniqueAdapter)
                        .selectedCount

                    AlertDialog.Builder(requireContext())
                        .setMessage("$targetCount item(s) will be deleted. " +
                                "Are you sure you want to proceed?")
                        .setPositiveButton(R.string.action_delete) { dialog, _ ->

                            uniqueListViewModel.deleteSelectedItems(
                                (binding.uniqueListRecycler.adapter as UniqueAdapter)
                                    .getSelectedAsListableItems(),parentHoard.hoardID)

                            actionMode?.finish()
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()

                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {

            // Clear all selections first
            (binding.uniqueListRecycler.adapter as MultiselectRecyclerAdapter).clearAllSelections()

            // Set status bar color back to proper one for theme
            @ColorInt
            val newStatusBarColor : Int = when(itemType){
                UniqueItemType.GEM -> R.color.gemPrimaryDark
                UniqueItemType.ART_OBJECT -> R.color.artPrimaryDark
                UniqueItemType.MAGIC_ITEM -> R.color.magicPrimaryDark
                UniqueItemType.SPELL_COLLECTION -> R.color.spellPrimaryDark
            }

            requireActivity().window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = resources.getColor(newStatusBarColor,null)
            }

            // Fully close actionMode
            actionMode = null
        }

    }
    // endregion

    // region [ Helper functions ]

    private fun updateUI(listables: List<ListableItem>) {
        uniqueAdapter = UniqueAdapter(listables)
        binding.uniqueListRecycler.adapter = uniqueAdapter
        binding.uniqueListToolbar.subtitle = parentHoard.name
    }

    private fun fadeOutWaitingCard() {

        isWaitingCardAnimating = true

        binding.uniqueListWaitingCard.waitingCard.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        this@apply.visibility = View.GONE
                        isWaitingCardAnimating = false
                    }
                })
        }
    }

    private fun fadeInWaitingCard() {

        isWaitingCardAnimating = true

        binding.uniqueListWaitingCard.waitingCard.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isWaitingCardAnimating = false
                    }
                })
        }
    }

    private fun showTargetHoardDialog(hoards: List<Hoard>) {

        val dialogRecycler = RecyclerView(requireContext())

        dialogRecycler.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = TargetHoardAdapter(hoards)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            isNestedScrollingEnabled = true
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogRecycler)
            .setTitle(getString(R.string.unique_list_target_hoard_dialog_title))
            .setPositiveButton(R.string.ok_affirmative, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create().apply {
                setOnShowListener { dialog ->

                    getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {

                            if ((dialogRecycler.adapter as TargetHoardAdapter)
                                    .selectedPos in hoards.indices) {

                                val targetID = (dialogRecycler.adapter as TargetHoardAdapter)
                                    .selectedID

                                if ((binding.uniqueListRecycler.adapter as UniqueAdapter)
                                        .getSelectedAsListableItems()
                                        .isNotEmpty() && targetID > 0){

                                    val triplesToMove = (binding.uniqueListRecycler.adapter as UniqueAdapter)
                                        .getSelectedAsListableItems()
                                        .map{
                                            Triple(it.id, when (it){
                                                is ListableGem -> UniqueItemType.GEM
                                                is ListableArtObject -> UniqueItemType.ART_OBJECT
                                                is ListableMagicItem -> UniqueItemType.MAGIC_ITEM
                                                is ListableSpellCollection -> UniqueItemType.SPELL_COLLECTION
                                            }, parentHoard.hoardID) }

                                    uniqueListViewModel.moveSelectedItems(triplesToMove,targetID)
                                }

                                actionMode?.finish()
                                dialog.dismiss()
                            }}

                    getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener { dialog.cancel() }
                }
            }

        dialog.show()
    }

    // endregion

    companion object{
        fun newInstance(): UniqueListFragment {
            return UniqueListFragment()
        }
    }
}