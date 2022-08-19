package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.MultiselectRecyclerAdapter
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardListItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardListBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardListViewModel
import com.example.android.treasurefactory.viewmodel.HoardListViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat

private const val TAG = "HoardListFragment"

class HoardListFragment : Fragment() {

    /**
    * Required interface for the hosting activities, allows callbacks to hosting activity.
    */
    interface Callbacks{
        fun onHoardSelected(view: View, hoardID: Int)
    }

    // region [ Property declarations ]

    private var shortAnimationDuration = 0
    private var isContentFrameAnimating = false

    private var callbacks: Callbacks? = null

    private var _binding: LayoutHoardListBinding? = null
    private val binding get() = _binding!!

    private var hoardAdapter: HoardAdapter? = HoardAdapter(emptyList())

    private val hoardListViewModel: HoardListViewModel by viewModels {
        HoardListViewModelFactory((this.requireActivity().application as TreasureHacktoryApplication).repository)
    }

    private var actionMode : ActionMode? = null
    private val actionModeCallback = ActionModeCallback()

    private var selectionMode = false //TODO remove
    // endregion

    // region [ Overridden functions ]

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        _binding = LayoutHoardListBinding.inflate(inflater, container, false)
        val view = binding.root

        // Give RecyclerView a Layout manager [required]
        binding.hoardListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hoardAdapter
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        hoardListViewModel.hoardListLiveData.observe(viewLifecycleOwner
        ) //Updates whenever the list of hoards is updated per BNR 238
        { hoards ->
            hoards?.let {

                Log.i(TAG, "Got ${hoards.size} treasure hoards")
                //TODO Set new container for selected item positions in ViewModel
                updateUI(hoards)

                if (hoards.isEmpty()) {

                    binding.hoardListWhenemptyGroup.visibility = View.VISIBLE
                    binding.hoardListRecycler.visibility = View.GONE

                } else {

                    binding.hoardListWhenemptyGroup.visibility = View.GONE
                    binding.hoardListRecycler.visibility = View.VISIBLE
                }
            }
        }

        hoardListViewModel.isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

            if (isRunningAsync) {

                binding.hoardListRecycler.isEnabled = false

                if (binding.hoardListContentFrame.visibility == View.VISIBLE &&
                        !isContentFrameAnimating) {

                    hideContentFrameCrossfade()

                } else {

                    binding.hoardListContentFrame.visibility = View.GONE
                    binding.hoardListProgressIndicator.visibility = View.VISIBLE
                    isContentFrameAnimating = false
                }

            } else {

                binding.hoardListRecycler.isEnabled = true

                if (binding.hoardListProgressIndicator.visibility == View.VISIBLE &&
                    !isContentFrameAnimating) {

                    showContentFrameCrossfade()

                } else {

                    binding.hoardListContentFrame.visibility = View.VISIBLE
                    binding.hoardListProgressIndicator.visibility = View.GONE
                    isContentFrameAnimating = false
                }
            }
        }

        hoardListViewModel.textToastHolderLiveData.observe(viewLifecycleOwner) { pendingAlert ->

            //TODO Test that there's no weird infinite feedback loops. Remove this when done.
            if (pendingAlert != null) {

                // Show the pending toast
                Toast.makeText(context,pendingAlert.first,pendingAlert.second).show()

                // Clear the livedata for pending toasts
                hoardListViewModel.textToastHolderLiveData.value = null
            }
        }

        // Set up toolbar
        binding.hoardListToolbar.apply {
            inflateMenu(R.menu.list_toolbar_menu)
            title = getString(R.string.hoard_list_fragment_title)
            setOnMenuItemClickListener { item ->

                // https://developer.android.com/guide/fragments/appbar#fragment-click
                when (item.itemId){

                    // No-selection options
                    R.id.action_new_hoard   -> {

                        if (hoardListViewModel.isRunningAsyncLiveData.value != true) {

                            val actionID = R.id.action_hoardListFragment_to_hoardGeneratorFragment
                            findNavController().navigate(actionID)
                        }

                        true
                    }

                    R.id.action_select_all_main  -> {

                        //TODO implement
                        (binding.hoardListRecycler.adapter as HoardAdapter).selectAllHoards()

                        true
                    }

                    R.id.action_settings    -> {
                        Toast.makeText(context, "Settings option selected.", Toast.LENGTH_SHORT).show()
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

    // endregion

    // region [ Inner classes ]

    // For list item selection:
    // https://developer.android.com/guide/topics/ui/layout/recyclerview-custom#select

    private inner class HoardViewHolder(val binding: HoardListItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        private lateinit var hoard: Hoard

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(newHoard: Hoard, selected: Boolean) {

            hoard = newHoard

            // Toggle background color of list item if selected
            binding.layoutHoardListItem.isSelected = selected

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
            binding.hoardListItemDate.text = SimpleDateFormat("MM/dd/yyyy").format(hoard.creationDate)

            // Set text for gp value counter
            ("Worth ${DecimalFormat("#,##0.0#")
                .format(hoard.gpTotal)
                .removeSuffix(".0")} gp").also { binding.hoardListItemGpValue.text = it }

            // Set text for unique item counters
            binding.hoardListItemGemCount.text = String.format("%03d",hoard.gemCount)
            binding.hoardListItemArtCount.text = String.format("%03d",hoard.artCount)
            binding.hoardListItemMagicCount.text = String.format("%03d",hoard.magicCount)
            binding.hoardListItemSpellCount.text = String.format("%03d",hoard.spellsCount)

            // Set icon for hoard TODO set icon as most valuable item (by gp value) in hoard
            try {

                binding.hoardListItemListIcon
                    .setImageResource(resources
                        .getIdentifier(hoard.iconID,"drawable",view?.context?.packageName))

            } catch (e: Exception) {

                binding.hoardListItemListIcon
                    .setImageResource(R.drawable.clipart_default_image)
            }

            // Set filled status of favorite star icon
            binding.hoardListItemFavorited.setImageResource(
                if (hoard.isFavorite) {
                    R.drawable.clipart_filledstar_vector_icon
                } else {
                    R.drawable.clipart_unfilledstar_vector_icon
                }
            )
        }

        override fun onClick(v: View) {

            if (actionMode != null) {

                // Toggle HoardViewHolder's selection status
                (this@HoardListFragment.binding.hoardListRecycler.adapter
                        as MultiselectRecyclerAdapter).toggleSelection(adapterPosition)

            } else {

                callbacks?.onHoardSelected(v,hoard.hoardID)
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (actionMode == null) {

                (this@HoardListFragment.binding.hoardListRecycler.adapter
                        as MultiselectRecyclerAdapter).toggleSelection(adapterPosition)

                return true
            }

            return false // Run onClick() instead
        }
    }

    private inner class HoardAdapter(var hoards: List<Hoard>)
        : MultiselectRecyclerAdapter<HoardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoardViewHolder {

            val binding = HoardListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return HoardViewHolder(binding)
        }

        override fun getItemCount() = hoards.size

        // BNR says always be efficient with this for scrolling smoothness
        override fun onBindViewHolder(viewHolder: HoardViewHolder, position: Int) {

            val hoard = hoards[position]

            viewHolder.bind(hoard, isSelected(position))
        }

        //TODO fix overrides to work with ActionMode

        // functions affecting selectedItems
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

        fun selectAllHoards() {
            Log.d("selectAllHoards()","Called")
            if (hoards.isNotEmpty()) {
                val allHoardIndices = hoards.indices.toList()
                Log.d("selectAllHoards() | Indices", allHoardIndices.joinToString())
                setPositions(allHoardIndices,true)
            }
            setActionModeFromCount()
        }

        /**
         * Returns a list of all Hoards currently selected in the adapter.
         */
        fun getSelectedAsHoards(): List<Hoard> {
            // May be more resource-intensive to grab from adapter, but this ensures intended
            // items are affected.
            return hoards.filterIndexed { index, _ -> getSelectedPositions().contains(index) }
        }

        fun selectedContainsFavorites(): Boolean {
            return getSelectedAsHoards().any { it.isFavorite }
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

                // Disable merge if only one item is selected
                actionMode?.menu?.findItem(R.id.action_merge)?.isEnabled = ( selectedCount != 1 )

                // Refresh the action bar
                actionMode?.invalidate()
            }
        }
    }

    private inner class ActionModeCallback() : ActionMode.Callback {

        // TODO Left off here. Started cycling out custom implementation of "Selection Mode" and
        //  instead implementing ActionMode through host Activity. Need to pick colors for and
        //  implement styles/themes for light/dark mode, ActionMode, and the item type themes.
        //  Finish converting over to actionMode schema, add multi-hoard copy/delete functions,
        //  merge function if time, and test. List considered fully implemented for release if all
        //  these are met.

        // https://stackoverflow.com/questions/30814558/problems-with-implementing-contextual-action-mode-in-recyclerview-fragment TODO
        // https://enoent.fr/posts/recyclerview-basics/ TODO

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate menu
            mode.menuInflater.inflate(R.menu.master_list_action_menu,menu)

            // Get the color to change the status bar background to
            val typedValue = TypedValue()
            val thisTheme = context!!.theme
            thisTheme.resolveAttribute(R.attr.colorSecondaryVariant,TypedValue(),true)
            @ColorInt
            val newStatusBarColor = typedValue.data

            // Change the status bar's color
            requireActivity().window.run {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = newStatusBarColor
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {

            //if there's only one selected item, disable merge
            return if ((binding.hoardListRecycler.adapter as MultiselectRecyclerAdapter).selectedCount < 2){
                if (menu.findItem(R.id.action_merge).isEnabled) {
                    menu.findItem(R.id.action_merge).isEnabled = false
                    true
                } else {
                    false
                }
            } else {
                if (!(menu.findItem(R.id.action_merge).isEnabled)) {
                    menu.findItem(R.id.action_merge).isEnabled = true
                    true
                } else {
                    false
                }
            }
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            return when (item.itemId) {

                R.id.action_delete -> {

                    val selectedCount =
                        (binding.hoardListRecycler.adapter as HoardAdapter).selectedCount

                    val cancelToast = Toast
                        .makeText(context,"Deletion cancelled.", Toast.LENGTH_SHORT)

                    // TODO dialog confirming user wants to delete hoards
                    val deleteConfirmBuilder = AlertDialog.Builder(context)

                    var shouldContinue = false // flag for process not being cancelled (via negative button or normal cancellation) TODO add OnCancelListener

                    deleteConfirmBuilder
                        .setTitle("Confirm Deletion")
                        .setIcon(R.drawable.clipart_warning_vector_icon)
                        .setMessage("Are you sure you want to delete " +
                                (if (selectedCount != 1) "all $selectedCount hoards" else
                                    "this hoard") +
                            "? This cannot be undone!")
                        .setPositiveButton(R.string.action_delete,
                            DialogInterface.OnClickListener { _, _ ->

                                // Check if selection contains favorites
                                val selectedHasFavorites =
                                    (binding.hoardListRecycler.adapter as HoardAdapter)
                                        .selectedContainsFavorites()

                                if (selectedHasFavorites) {

                                    // Warn user and offer a second chance to back out.
                                    val hasFavoritesBuilder = AlertDialog.Builder(context)

                                    hasFavoritesBuilder
                                        .setTitle("Confirm Deletion")
                                        .setIcon(R.drawable.clipart_warning_vector_icon)
                                        .setMessage(
                                            "At least one of the selected hoards is " +
                                                    "marked as a favorite. Are you absolutely sure you " +
                                                    "want to delete?"
                                        )
                                        .setPositiveButton("Yes, delete even marked favorites.",
                                            DialogInterface.OnClickListener { _, _ ->
                                                shouldContinue = true
                                            })
                                        .setNegativeButton("Cancel",
                                            DialogInterface.OnClickListener { _, _ ->
                                                shouldContinue = false
                                                cancelToast.show()
                                            })
                                        .setCancelable(true)
                                        .setOnCancelListener {
                                            DialogInterface.OnCancelListener { shouldContinue = false }
                                            }
                                        .show()

                                } else { shouldContinue = true }

                                Log.v("onActionItemClicked | action_delete",
                                    "Checking if shouldContinue is true.")

                                if (shouldContinue) {

                                    // grab target hoards using getSelectedAsHoards()
                                    val targetHoards =
                                        (binding.hoardListRecycler.adapter as HoardAdapter)
                                            .getSelectedAsHoards()

                                    // delete using a vararg DAO function DeleteHoardsWithChildren
                                    hoardListViewModel.deleteSelectedHoards(targetHoards)

                                    // call update
                                    mode.finish()
                                }
                        })
                        .setNegativeButton(R.string.action_cancel, DialogInterface.OnClickListener { _, _ ->
                            cancelToast.show()
                        })
                        .show()

                    true
                }

                R.id.action_duplicate -> {
                    // TODO do as much of these planned commented steps on ViewModel as possible

                    // TODO Left off here. ActionMode works as intended and deletion/selection
                    //  functions appear to work as intended as well. Duplicate and Merge remain
                    //  untested and Merge needs custom layout for dialog. Should everything work as
                    //  intended and minor stylistic elements like status bar text and icon colors
                    //  be taken care of, main list will be considered feature complete for launch
                    //  and it will finally be time to tackle the hoard viewer.

                    // TODO Therefore, start with fully implementing the rest of the ActionMode and
                    //  then test everything.

                    Toast.makeText(context,"functionality disabled for testing",Toast.LENGTH_SHORT)
                    // dialog confirming user wants to duplicate hoards
                    // grab target hoards using getSelectedHoards()
                    // create renamed duplicates of hoards using HoardManipulator controller
                        // add copies to DB
                    // call update
                    //TODO mode.finish()
                    true
                }

                R.id.action_merge -> {
                    // TODO do as much of these planned commented steps on ViewModel as possible

                    Toast.makeText(context,"functionality disabled for testing",Toast.LENGTH_SHORT)

                    // grab target hoards using getSelectedHoards()
                    // check if merger is legal
                    // dialog confirming user wants to merge hoards, (displaying projected result?)
                    // merge using a HoardManipulator controller
                        // insert result into db
                        // delete old hoards
                    // all update
                    //TODO mode.finish()
                    true
                }

                R.id.action_select_all -> {

                    (binding.hoardListRecycler.adapter as HoardAdapter).selectAllHoards()

                    true
                }

                // TODO add Cancel item if icon cannot be added to ActionBar.

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            // Clear all selections first
            (binding.hoardListRecycler.adapter as MultiselectRecyclerAdapter).clearAllSelections()

            // Get the color to change the status bar background to
            // https://stackoverflow.com/questions/17277618/get-color-value-programmatically-when-its-a-reference-theme TODO
            val typedValue = TypedValue()
            val thisTheme = context!!.theme
            thisTheme.resolveAttribute(R.attr.colorPrimaryDark,TypedValue(),true)
            @ColorInt
            val newStatusBarColor = typedValue.data

            // Change the status bar's color
            requireActivity().window.run {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = newStatusBarColor
            }

            // Fully close actionMode
            actionMode = null
        }

    }
    // endregion

    // region [ Helper functions ]

    private fun updateUI(hoards: List<Hoard>) {
        hoardAdapter = HoardAdapter(hoards)
        binding.hoardListRecycler.adapter = hoardAdapter
    }

    private fun showContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.hoardListContentFrame.apply {

            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.hoardListProgressIndicator.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.hoardListProgressIndicator.visibility = View.GONE
                        isContentFrameAnimating = false
                    }
                })
        }
    }

    private fun hideContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.hoardListProgressIndicator.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.hoardListContentFrame.apply {

            alpha = 1f
            visibility = View.VISIBLE

            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isContentFrameAnimating = false
                        binding.hoardListContentFrame.visibility = View.GONE
                    }
                })
        }
    }

    private fun getSelectedHoards() : List<Hoard> {

        val targetHoards = arrayListOf<Hoard>()

        val indices = (binding.hoardListRecycler.adapter as HoardAdapter).getSelectedPositions()

        indices.forEach { index ->

            (binding.hoardListRecycler.adapter as HoardAdapter).hoards.getOrNull(index).let{
                if (it != null) targetHoards.add(it)
            }
        }

        return targetHoards.toList()
    }

    // endregion

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }
}