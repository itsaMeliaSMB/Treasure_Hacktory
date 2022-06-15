package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardListItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardListBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardListViewModel
import com.example.android.treasurefactory.viewmodel.HoardListViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

                    fadeInProgressBar()

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

                    R.id.action_new_hoard   -> {

                        if (hoardListViewModel.isRunningAsyncLiveData.value != true) {

                            val actionID = R.id.action_hoardListFragment_to_hoardGeneratorFragment
                            findNavController().navigate(actionID)
                        }

                        true
                    }

                    R.id.action_wait_three_seconds -> {

                        Toast.makeText(context,"Waiting 3 seconds...",Toast.LENGTH_SHORT).show()

                        hoardListViewModel.waitThreeSeconds()

                        true
                    }

                    R.id.action_delete_all_hoards -> {

                        // Confirm via Alert Dialog
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete all hoards?")
                            .setMessage("This action cannot be undone.")
                            .setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->

                                Log.d("onOptionsItemSelected","Delete option selected.")

                                fadeInProgressBar()

                                hoardListViewModel.deleteAllHoards()
                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                                Log.d("onOptionsItemSelected","Cancel option selected.")
                            }).show()

                        true
                    }

                    R.id.action_settings    -> {
                        Toast.makeText(context, "Settings option selected.", Toast.LENGTH_SHORT).show()
                        true
                    }

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
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var hoard: Hoard

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(newHoard: Hoard) {

            hoard = newHoard

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

            callbacks?.onHoardSelected(v,hoard.hoardID)
            Log.d(TAG,"callback on hoard.hoardID (${hoard.hoardID})")
        }
    }

    private inner class HoardAdapter(var hoards: List<Hoard>)
        : RecyclerView.Adapter<HoardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoardViewHolder {

            val binding = HoardListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return HoardViewHolder(binding)
        }

        override fun getItemCount() = hoards.size

        // BNR says always be efficient with this for scrolling smoothness
        override fun onBindViewHolder(viewHolder: HoardViewHolder, position: Int) {

            val hoard = hoards[position]

            viewHolder.bind(hoard)
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

    private fun fadeInProgressBar() {

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
    // endregion

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }
}