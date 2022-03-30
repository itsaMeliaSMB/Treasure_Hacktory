package com.example.android.treasurefactory.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardListItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardListBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardListViewModel
import com.example.android.treasurefactory.viewmodel.HoardListViewModelFactory
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

private const val TAG = "HoardListFragment"

class HoardListFragment : Fragment() {

    /**
    * Required interface for hosting activities, allows callbacks to hosting activity.
    */
    interface Callbacks{
        fun onHoardSelected(view: View, hoardID: Int)
    }

    private var callbacks: Callbacks? = null

    private var _binding: LayoutHoardListBinding? = null
    private val binding get() = _binding!!

    private var hoardAdapter: HoardAdapter? = HoardAdapter(emptyList())
    //TODO add bindings after renaming layout IDs

    // https://darrenfinch.com/how-to-get-a-view-model-with-viewmodelprovider-property-delegates/
    // Modified from BNR pg 178 because of depreciated class
    private val hoardListViewModel: HoardListViewModel by viewModels {
        HoardListViewModelFactory((this.requireActivity().application as TreasureHacktoryApplication).repository)
    }

    /*by lazy {

        ViewModelProvider(this).get(hoardListViewModel::class.java)
    }*/

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_new_hoard   -> {
                val actionID = R.id.action_hoardListFragment_to_hoardGeneratorFragment
                findNavController().navigate(actionID)
                true
            }

            R.id.action_settings    -> {
                Toast.makeText(context, "Settings option selected.", Toast.LENGTH_SHORT).show()
                true
            }

            else    -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        _binding = LayoutHoardListBinding.inflate(inflater, container, false)
        val view = binding.root

        // Give RecyclerView a Layout manager [required]
        binding.hoardListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hoardAdapter
        }

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
            }

            // Show placeholder view when the hoard list is empty
            binding.hoardListWhenemptyGroup.visibility = if (hoards.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate((R.menu.list_toolbar_menu),menu)
    }

    private fun updateUI(hoards: List<Hoard>) {
        hoardAdapter = HoardAdapter(hoards)
        binding.hoardListRecycler.adapter = hoardAdapter
    }

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
            binding.hoardListItemDate.text = hoard.creationDesc.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

            // Set text for gp value counter
            ("Worth ${DecimalFormat("#,##0.0#")
                .format(hoard.gpTotal)
                .removeSuffix(".0")} gp").also { binding.hoardListItemGpValue.text = it }

            // Set text for unique item counters
            binding.hoardListItemGemCount.text = String.format("%03d",hoard.gemCount)
            binding.hoardListItemArtCount.text = String.format("%03d",hoard.artCount)
            binding.hoardListItemMagicCount.text = String.format("%03d",hoard.magicCount)
            binding.hoardListItemSpellCount.text = String.format("%03d",hoard.spellsCount)

            // Set visibility of hoard leftover icon
            binding.hoardListItemLeftoverIcon.visibility = if (hoard.leftover.isNotEmpty()) {
                View.VISIBLE
            } else View.GONE

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
}