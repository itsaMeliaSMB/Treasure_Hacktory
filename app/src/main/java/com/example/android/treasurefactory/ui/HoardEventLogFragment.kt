package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardEventItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardEventLogBinding
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.viewmodel.HoardEventLogViewModel
import com.example.android.treasurefactory.viewmodel.HoardEventLogViewModelFactory

private const val ARG_HOARD_ID = "hoard_id"

class HoardEventLogFragment : Fragment() {

    private var _binding: LayoutHoardEventLogBinding? = null
    private val binding get() = _binding!!

    private var eventAdapter: EventAdapter? = EventAdapter(emptyList())

    private val hoardEventLogViewModel: HoardEventLogViewModel by viewModels {
        HoardEventLogViewModelFactory(
            (this.requireActivity().application as TreasureHacktoryApplication).repository)
    }


    // region [ Overridden functions ]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activeHoardID: Int = arguments?.getSerializable(ARG_HOARD_ID) as Int

        hoardEventLogViewModel.updateHoardID(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = LayoutHoardEventLogBinding.inflate(inflater, container, false)
        val view = binding.root

        // Give RecyclerView a Layout manager [required]
        binding.hoardEventLogRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    // endregion

    // region [ Inner classes ]

    private inner class EventHolder(binding: HoardEventItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: HoardEvent){
            //TODO Left off here. Layouts for hoard events made. Finish implementing this, handle
            // the nested Recyclerview in the CardView, add a menu and toolbar info for this
            // fragment, add it to the navigation graph, and have it accessible by user before
            // moving on to written refactor checklist.
        }

    }

    private inner class EventAdapter(val events: List<HoardEvent>):
        RecyclerView.Adapter<EventHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
            val binding = HoardEventItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return EventHolder(binding)
        }

        override fun onBindViewHolder(holder: EventHolder, position: Int) {

            val event = events[position]

            holder.bind(event)
        }

        override fun getItemCount(): Int = events.size

    }
    // endregion

    //region [ Helper functions ]

    //endregion
}