package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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
    * Required interface for hosting activities, allows callbacks to hosting activity.
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

            if (selectionMode) {

                // Toggle HoardViewHolder's selection status
                (this@HoardListFragment.binding.hoardListRecycler.adapter
                        as MultiselectRecyclerAdapter).toggleSelection(adapterPosition)
            } else {

                callbacks?.onHoardSelected(v,hoard.hoardID)
                Log.d(TAG,"callback on hoard.hoardID (${hoard.hoardID})")
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (!selectionMode) {

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
            endSelectionMode()
        }

        fun selectAllHoards() {
            if (hoards.isNotEmpty()) {
                val allHoardIndices = hoards.indices.toList()
                setPositions(allHoardIndices,true)
            }
            setActionModeFromCount()
        }

        private fun setActionModeFromCount() {

            if (selectedCount == 0) {

                actionMode?.finish()

            } else {

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
            mode.menuInflater.inflate(R.menu.master_list_action_menu,menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            TODO("Not yet implemented")
            //if there's only one selected item, disable merge
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            return when (item.itemId) {

                R.id.action_delete -> {
                    //TODO implement
                    mode.finish()
                    true
                }

                R.id.action_duplicate -> {
                    //TODO implement
                    mode.finish()
                    true
                }

                R.id.action_merge -> {
                    //TODO implement
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            (binding.hoardListRecycler.adapter as MultiselectRecyclerAdapter).clearAllSelections()
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

    private fun startSelectionMode() { //TODO factor out once ActionMode is implemented

        selectionMode = true

        // Toggle visibility/enabled-state of menu items
        binding.hoardListToolbar.menu.apply {

            findItem(R.id.action_new_hoard).apply {
                isVisible = false
                isEnabled = false
            }
            findItem(R.id.action_select_all_main).apply {
                isVisible = false
                isEnabled = false
            }

            findItem(R.id.action_settings).apply {
                isVisible = false
                isEnabled = false
            }

            //val multiselectGroupID = R.id.group_hoard_multiselect_options
            //setGroupVisible(multiselectGroupID,true)
            //setGroupEnabled(multiselectGroupID, true)
        }

        // Update UI elements of Toolbar
        binding.hoardListToolbar.apply {
            // Change Status bar color, TODO is there a way to handle this in Activity with callback?
            // Change Toolbar color TODO
                //TODO Left off here. Adapter code, in theory, should be able to handle all possible
                // triggers in multiselect state. What remains is being able to programmatically
                // change StatusBar and Toolbar background colors and adding a utility for hoard
                // manipulation (cloning, merging) and adding selection mode action item
                // functionality to DAO and ViewModel. Also need to test, of course.

                //TODO After that, remove unused elements from spell generation and decide on how to
                // handle asterisks in getSpellByTheBook(). Viewer functionality afterward.
            // Add Navigation icon
            navigationIcon = ResourcesCompat
                .getDrawable(resources,R.drawable.clipart_close_vector_icon,null)
            setNavigationOnClickListener {
                (this@HoardListFragment.binding.hoardListRecycler.adapter
                    as MultiselectRecyclerAdapter).clearAllSelections() }
        }
    }

    private fun updateSelectionToolbar(newCount: Int){
        val newTitle = "$newCount Selected"

        binding.hoardListToolbar.title = newTitle
    }

    private fun endSelectionMode() {

        selectionMode = false
        //do the opposite of startSelectionMode, once the code is typed up.

        //change toolbar title back to "Your hoards"

        //be sure to call this before navigating away from this fragment.
    }
    // endregion

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }
}