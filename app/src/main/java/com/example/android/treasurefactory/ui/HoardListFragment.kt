package com.example.android.treasurefactory.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardListViewModel

private const val TAG = "HoardListFragment"

class HoardListFragment : Fragment() {

    /**
    * Required interface for hosting activities
    */
    interface Callbacks{
        fun onHoardSelected(hoardID: Int)
    }

    private var callbacks: Callbacks? = null

    private lateinit var hoardRecyclerView: RecyclerView
    private lateinit var whenEmptyView: View
    private var adapter: HoardAdapter? = HoardAdapter(emptyList())
    //TODO add bindings after renaming layout IDs

    // Modified from BNR pg 178 because of depreciated class
    private val hoardListViewModel: HoardListViewModel by lazy {

        ViewModelProvider(this).get(hoardListViewModel::class.java)
    }

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

        val view = inflater.inflate(R.layout.layout_hoard_list, container, false)
        hoardRecyclerView = view.findViewById(R.id.hoard_list_recycler) as RecyclerView
        whenEmptyView = view.findViewById(R.id.hoard_list_whenempty_group) as ConstraintLayout

        // Give RecyclerView a Layout manager [required]
        hoardRecyclerView.layoutManager = LinearLayoutManager(context)

        hoardRecyclerView.adapter = adapter

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
            whenEmptyView.visibility = if (hoards.isEmpty()) View.VISIBLE else View.GONE
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

        adapter = HoardAdapter(hoards)
        hoardRecyclerView.adapter = adapter
    }

    private inner class HoardViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var hoard: Hoard

        // Define views in layout
        private val nameTextView: TextView = itemView.findViewById(R.id.hoard_list_item_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.hoard_list_item_date)
        private val favImageView: ImageView = itemView.findViewById(R.id.hoard_list_item_favorited)
        private val iconImageView: ImageView = itemView.findViewById(R.id.hoard_list_item_list_icon)
        private val gpTextView: TextView = itemView.findViewById(R.id.hoard_list_item_gp_value)
        private val gemTextView: TextView = itemView.findViewById(R.id.hoard_list_item_gem_count)
        private val artTextView: TextView = itemView.findViewById(R.id.hoard_list_item_art_count)
        private val mgcTextView: TextView = itemView.findViewById(R.id.hoard_list_item_magic_count)
        private val splTextView: TextView = itemView.findViewById(R.id.hoard_list_item_spell_count)
        private val leftoverIcon: ImageView = itemView.findViewById(R.id.hoard_list_item_leftover_icon)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(hoard: Hoard) {

            this.hoard = hoard

            // Set hoard name and [ NEW ] visibility
            nameTextView.text = this.hoard.name
            if (hoard.isNew) {
                nameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.clipart_new_vector_icon,0,0,0)
            } else {
                nameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            }

            // Set hoard date
            dateTextView.text = this.hoard.creationDate.toString()

            // Set text for gp value counter
            "Worth " + String.format("%.2f",hoard.gpTotal) + " gp".also { gpTextView.text = it }

            // Set text for unique item counters
            gemTextView.text = String.format("%03d",hoard.gemCount)
            artTextView.text = String.format("%03d",hoard.artCount)
            mgcTextView.text = String.format("%03d",hoard.magicCount)
            splTextView.text = String.format("%03d",hoard.spellsCount)

            // Set visibility of hoard leftover icon
            leftoverIcon.visibility = if (hoard.leftover.isNotEmpty()) View.VISIBLE else View.GONE

            // Set icon for hoard TODO set icon as most valuable item (by gp value) in hoard
            try {

                iconImageView.setImageResource(resources.getIdentifier(hoard.iconID,"drawable",view?.context?.packageName))

            } catch (e: Exception) {

                iconImageView.setImageResource(R.drawable.clipart_default_image)
            }

            // Set filled status of favorite star icon
            if (hoard.isFavorite) {
                favImageView.setImageResource(R.drawable.clipart_filledstar_vector_icon)
            } else {
                favImageView.setImageResource(R.drawable.clipart_unfilledstar_vector_icon)
            }

        }

        override fun onClick(v: View) {

            callbacks?.onHoardSelected(hoard.hoardID)
            Log.d(TAG,"callback on hoard.hoardID (${hoard.hoardID})")
        }
    }

    private inner class HoardAdapter(var hoards: List<Hoard>)
        : RecyclerView.Adapter<HoardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoardViewHolder {

            val view = layoutInflater.inflate(R.layout.hoard_list_item, parent, false)
            return HoardViewHolder(view)
        }

        override fun getItemCount() = hoards.size

        // BNR says always be efficient with this for scrolling smoothness
        override fun onBindViewHolder(viewHolder: HoardViewHolder, position: Int) {

            val hoard = hoards[position]

            viewHolder.bind(hoard)
        }
    }
}