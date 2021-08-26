package com.example.android.treasurefactory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "HoardListFragment"

class HoardListFragment : Fragment() {

    private lateinit var hoardRecyclerView: RecyclerView
    private var adapter: HoardAdapter? = null

    // Modified from BNR pg 178 because of depreciated class
    private val hoardListViewModel: HoardListViewModel by lazy {

        ViewModelProvider(this).get(hoardListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.treasure_viewer_list, container, false)

        hoardRecyclerView = view.findViewById(R.id.treasure_viewer_list_recycler) as RecyclerView

        // Give RecyclerView a Layout manager [required]
        hoardRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    private fun updateUI() {

        val hoards = hoardListViewModel.hoards
        adapter = HoardAdapter(hoards)
        hoardRecyclerView.adapter = adapter
    }

    // TODO: read up on inner classes
    private inner class HoardHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var hoard: HMHoard

        // seperate concerns from viewholder
        private val nameTextView: TextView = itemView.findViewById(R.id.treasure_viewer_list_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.treasure_viewer_list_date)
        private val favImageView: ImageView = itemView.findViewById(R.id.treasure_viewer_list_star)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(hoard: HMHoard) {

            this.hoard = hoard
            nameTextView.text = this.hoard.hoardName
            dateTextView.text = this.hoard.creationDate.toString()

            if (hoard.favorited) {
                favImageView.setImageResource(R.drawable.clipart_filledstar_vector_icon)
            } else {
                favImageView.setImageResource(R.drawable.clipart_unfilledstar_vector_icon)
            }

        }

        override fun onClick(v: View) {

            //TODO: Change to go to hoard viewer
            Toast.makeText(context, "${hoard.hoardName} pressed. Viewer not implemented.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private inner class HoardAdapter(var hoards: List<HMHoard>)
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