package com.example.android.treasurefactory.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private var adapter: HoardAdapter? = HoardAdapter(emptyList())

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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.treasure_viewer_list, container, false)

        hoardRecyclerView = view.findViewById(R.id.treasure_viewer_list_recycler) as RecyclerView

        // Give RecyclerView a Layout manager [required]
        hoardRecyclerView.layoutManager = LinearLayoutManager(context)

        hoardRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        hoardListViewModel.hoardListLiveData.observe(viewLifecycleOwner, //Updates whenever the list of hoards is updated per BNR 238
            Observer { hoards -> hoards?.let{

                Log.i(TAG,"Got ${hoards.size} treasure hoards")
                updateUI(hoards)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(hoards: List<Hoard>) {

        adapter = HoardAdapter(hoards)
        hoardRecyclerView.adapter = adapter
    }

    // TODO: read up on inner classes
    private inner class HoardHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var hoard: Hoard

        // seperate concerns from viewholder
        private val nameTextView: TextView = itemView.findViewById(R.id.treasure_viewer_list_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.treasure_viewer_list_date)
        private val favImageView: ImageView = itemView.findViewById(R.id.treasure_viewer_list_star)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(hoard: Hoard) {

            this.hoard = hoard
            nameTextView.text = this.hoard.getName()
            dateTextView.text = this.hoard.getCreationDate().toString()

            if (hoard.getFavorite()) {
                favImageView.setImageResource(R.drawable.clipart_filledstar_vector_icon)
            } else {
                favImageView.setImageResource(R.drawable.clipart_unfilledstar_vector_icon)
            }

        }

        override fun onClick(v: View) {

            //TODO: Change to go to hoard viewer
            callbacks?.onHoardSelected(hoard.hoardID)
        }
    }

    private inner class HoardAdapter(var hoards: List<Hoard>)
        : RecyclerView.Adapter<HoardHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoardHolder {

            val view = layoutInflater.inflate(R.layout.treasure_viewer_list_item, parent, false)
            return HoardHolder(view)
        }

        override fun getItemCount() = hoards.size

        // BNR says always be efficient with this for scrolling smoothness
        override fun onBindViewHolder(holder: HoardHolder, position: Int) {

            val hoard = hoards[position]

            holder.bind(hoard)
        }
    }
}