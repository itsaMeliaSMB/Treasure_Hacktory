package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.capitalized
import com.example.android.treasurefactory.databinding.HoardEventItemBinding
import com.example.android.treasurefactory.databinding.HoardEventTagLayoutBinding
import com.example.android.treasurefactory.databinding.LayoutHoardEventLogBinding
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.viewmodel.HoardEventLogViewModel
import com.example.android.treasurefactory.viewmodel.HoardEventLogViewModelFactory
import com.google.android.flexbox.*
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_HOARD_ID = "hoard_id"

class HoardEventLogFragment : Fragment() {

    val safeArgs : HoardEventLogFragmentArgs by navArgs()

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

        val activeHoardID: Int = safeArgs.activeHoardID

        hoardEventLogViewModel.updateHoardID(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        // Set up listeners
        hoardEventLogViewModel.eventsLiveData.observe(viewLifecycleOwner) { events ->
            updateUI(events)
        }

        // Set up toolbar
        binding.hoardEventLogToolbar.apply{
            title = getString(R.string.hoard_event_log_fragment_title)
            subtitle = hoardEventLogViewModel.hoardNameLiveData.value
            navigationIcon = AppCompatResources.getDrawable(context,R.drawable.clipart_back_vector_icon)
            //TODO add menu and means of adding notes to toolbar
        }
    }
    // endregion

    // region [ Inner classes ]

    private inner class TagAdapter(val rawTags: List<String>): RecyclerView.Adapter<TagAdapter.TagHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
            val binding = HoardEventTagLayoutBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return TagHolder(binding)
        }

        override fun onBindViewHolder(holder: TagHolder, position: Int) {

            val rawTag = rawTags[position]

            holder.bind(rawTag)
        }

        override fun getItemCount(): Int = rawTags.size

        private inner class TagHolder(val binding: HoardEventTagLayoutBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var rawTag : String

            fun bind(newTag: String) {

                rawTag = newTag
                @ColorInt
                val tintOnTag = R.attr.colorOnSecondary

                binding.hoardEventTagIcon.apply {
                    setImageResource(rawTag.toDrawableResID())
                    drawable.setTint(tintOnTag)
                }

                binding.hoardEventTagText.apply{
                    text = rawTag.refine()
                    setTextColor(tintOnTag)
                }
            }

            @DrawableRes
            private fun String.toDrawableResID() : Int {

                return when (this){
                    "art-object" ->         R.drawable.clipart_painting_vector_icon
                    "coinage" ->            R.drawable.clipart_coinbag_vector_icon
                    "creation" ->           R.drawable.clipart_new_vector_icon
                    "deletion" ->           R.drawable.clipart_delete_vector_icon
                    "duplication" ->        R.drawable.clipart_copy_vector_icon
                    "gemstone" ->           R.drawable.clipart_gem_vector_icon
                    "homebrew" ->           R.drawable.clipart_house_vector_icon
                    "magic-item" ->         R.drawable.clipart_magicwand_vector_icon
                    "merge" ->              R.drawable.clipart_merge_vector_icon
                    "modification" ->       R.drawable.clipart_edit_vector_icon
                    "note" ->               R.drawable.clipart_extranotes_vector_icon
                    "sale" ->               R.drawable.clipart_bag_vector_icon
                    "spell-collection"->    R.drawable.clipart_spellbook_vector_icon
                    "system" ->             R.drawable.clipart_android_vector_icon
                    "user" ->               R.drawable.clipart_user_vector_icon
                    "verbose" ->            R.drawable.clipart_info_vector_icon
                    else ->                 R.drawable.clipart_circle_vector_icon
                }
            }

            private fun String.refine(): String {

                val newTag = StringBuilder()

                this.lowercase().trim().split("-").forEachIndexed { index, oldString ->

                    val newString = (if (index != 0) " " else "") + oldString.capitalized()
                    newTag.append(newString)
                }

                return newTag.toString()
            }
        }
    }

    private inner class EventAdapter(val events: List<HoardEvent>):
        RecyclerView.Adapter<EventAdapter.EventHolder>() {

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

        inner class EventHolder(val binding: HoardEventItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var event : HoardEvent

            fun bind(newEvent: HoardEvent){

                event = newEvent

                val tagLayoutManager = FlexboxLayoutManager(context).apply { // alt context = this.binding.hoardEventTagRecyclerView.context TODO
                    flexDirection = FlexDirection.ROW
                    alignItems = AlignItems.FLEX_START
                    flexWrap = FlexWrap.WRAP
                }

                val tagSpacingDecoration = FlexboxItemDecoration(context).apply {
                    setDrawable(ResourcesCompat
                        .getDrawable(resources,R.drawable.tag_label_divider,context?.theme))
                    setOrientation(FlexboxItemDecoration.BOTH)
                }

                binding.hoardEventListPosition.text = "# ${adapterPosition + 1}"
                binding.hoardEventTimestamp.text = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                    .format(Date(event.timestamp))
                binding.hoardEventDescription.text = event.description
                binding.hoardEventTagRecyclerView.apply {
                    //setHasFixedSize(true) TODO remove if tests better without
                    layoutManager = tagLayoutManager
                    adapter = TagAdapter(event.tag.getRawTags())
                    addItemDecoration(tagSpacingDecoration)
                    //(layoutParams as FlexboxLayoutManager.LayoutParams).flexGrow = 0.1f TODO
                }
            }

            private fun String.getRawTags(): List<String> = this.lowercase().split('|')
        }
    }
    // endregion

    //region [ Helper functions ]

    private fun updateUI(events: List<HoardEvent>) {
        eventAdapter = EventAdapter(events)
        binding.hoardEventLogRecyclerView.adapter = eventAdapter
    }
    //endregion
}