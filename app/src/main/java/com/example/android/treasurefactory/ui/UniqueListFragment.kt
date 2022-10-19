package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
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
import com.example.android.treasurefactory.databinding.LayoutUniqueListBinding
import com.example.android.treasurefactory.databinding.UniqueListItemBinding
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.viewmodel.UniqueListViewModel
import com.example.android.treasurefactory.viewmodel.UniqueListViewModelFactory
import java.text.DecimalFormat
import java.text.NumberFormat

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

    val safeArgs : UniqueListFragmentArgs by navArgs()

    private var shortAnimationDuration = 0
    private var isContentFrameAnimating = false

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
        val itemType: UniqueItemType = safeArgs.listType

        uniqueListViewModel.loadHoardInfo(hoardID, itemType)

        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        _binding = LayoutUniqueListBinding.inflate(inflater, container, false)
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

        uniqueListViewModel.exposedHoardLiveData.observe(viewLifecycleOwner) { readyHoard ->

            if (readyHoard != null) {
                parentHoard = readyHoard

                val hoardID: Int = safeArgs.hoardID
                val itemType: UniqueItemType = safeArgs.listType

                uniqueListViewModel.updateUniqueItems(hoardID, itemType)
            }
        }

        uniqueListViewModel.uniqueItemsLiveData.observe(viewLifecycleOwner) { itemList ->

            updateUI(itemList)

            if (itemList.isEmpty()) {

                binding.uniqueListWhenemptyGroup.visibility = View.VISIBLE
                binding.uniqueListRecycler.visibility = View.GONE

            } else {

                binding.uniqueListWhenemptyGroup.visibility = View.GONE
                binding.uniqueListRecycler.visibility = View.VISIBLE
            }
        }

        uniqueListViewModel.isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

            if (isRunningAsync) {

                binding.uniqueListRecycler.isEnabled = false

                if (binding.uniqueListContentFrame.visibility == View.VISIBLE &&
                    !isContentFrameAnimating) {

                    hideContentFrameCrossfade()

                } else {

                    binding.uniqueListContentFrame.visibility = View.GONE
                    binding.uniqueListProgressIndicator.visibility = View.VISIBLE
                    isContentFrameAnimating = false
                }

            } else {

                binding.uniqueListRecycler.isEnabled = true

                if (binding.uniqueListProgressIndicator.visibility == View.VISIBLE &&
                    !isContentFrameAnimating) {

                    showContentFrameCrossfade()

                } else {

                    binding.uniqueListContentFrame.visibility = View.VISIBLE
                    binding.uniqueListProgressIndicator.visibility = View.GONE
                    isContentFrameAnimating = false
                }
            }
        }

        uniqueListViewModel.textToastHolderLiveData.observe(viewLifecycleOwner) { pendingAlert ->

            if (pendingAlert != null) {

                // Show the pending toast
                Toast.makeText(context,pendingAlert.first,pendingAlert.second).show()

                // Clear the livedata for pending toasts
                uniqueListViewModel.textToastHolderLiveData.value = null
            }
        }

        // Set up toolbar
        binding.uniqueListToolbar.apply {
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
            subtitle = parentHoard.name
            navigationIcon = AppCompatResources.getDrawable(context,R.drawable.clipart_back_vector_icon)
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

                uniqueListItemframeForeground.setImageResource(
                    when (uniqueItem.iFrameFlavor) {
                        ItemFrameFlavor.NORMAL -> R.drawable.itemframe_foreground
                        ItemFrameFlavor.CURSED -> R.drawable.itemframe_foreground_cursed
                        ItemFrameFlavor.GOLDEN -> R.drawable.itemframe_foreground_golden
                    }
                )

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

                uniqueListItemTypeLabel.text = uniqueItem.endIconStr
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
                    // Hide badge
                    binding.uniqueListItemframeBadge.visibility = View.GONE
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
            Log.d("selectAllHoards()","Called")
            if (items.isNotEmpty()) {
                val allHoardIndices = items.indices.toList()
                Log.d("selectAllItems() | Indices", allHoardIndices.joinToString())
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

    private inner class ActionModeCallback : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {

            //TODO implement as separate menus depending on item list type

            // Inflate menu
            mode.menuInflater.inflate(R.menu.unique_list_action_menu,menu)

            // Get the color to change the status bar background to
            val typedValue = TypedValue()
            requireActivity().theme.resolveAttribute(R.attr.colorSecondaryVariant,typedValue,true)
            @ColorInt
            val newStatusBarColor = typedValue.data

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

                    // TODO unimplemented!

                    Toast.makeText(context,"Move action clicked (Unimplemented).",
                        Toast.LENGTH_SHORT)
                        .show()

                    true
                }

                R.id.action_select_all_items -> {

                    (binding.uniqueListRecycler.adapter as UniqueAdapter).selectAllItems()

                    true
                }

                R.id.action_copy_items -> {

                    // TODO unimplemented!

                    Toast.makeText(context,"Duplicate action clicked (Unimplemented).",
                        Toast.LENGTH_SHORT)
                        .show()

                    true
                }

                R.id.action_sell_items -> {

                    // TODO unimplemented!

                    Toast.makeText(context,"Sell action clicked (Unimplemented).",
                        Toast.LENGTH_SHORT)
                        .show()

                    true
                }

                R.id.action_delete_items -> {

                    // TODO unimplemented!

                    Toast.makeText(context,"Delete action clicked (Unimplemented).",
                        Toast.LENGTH_SHORT)
                        .show()

                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {

            // Clear all selections first
            (binding.uniqueListRecycler.adapter as MultiselectRecyclerAdapter).clearAllSelections()

            // Get the color to change the status bar background to
            val typedValue = TypedValue()
            requireActivity().theme.resolveAttribute(R.attr.colorPrimaryDark,typedValue,true)
            @ColorInt
            val newStatusBarColor = typedValue.data

            // Change the status bar's color
            requireActivity().window.apply{
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = newStatusBarColor
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

    private fun showContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.uniqueListContentFrame.apply {

            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.uniqueListProgressIndicator.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.uniqueListProgressIndicator.visibility = View.GONE
                        isContentFrameAnimating = false
                    }
                })
        }
    }

    private fun hideContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.uniqueListProgressIndicator.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.uniqueListContentFrame.apply {

            alpha = 1f
            visibility = View.VISIBLE

            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isContentFrameAnimating = false
                        binding.uniqueListContentFrame.visibility = View.GONE
                    }
                })
        }
    }

    // endregion

    companion object{
        fun newInstance(): UniqueListFragment {
            return UniqueListFragment()
        }
    }
}