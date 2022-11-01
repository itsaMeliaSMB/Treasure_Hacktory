package com.treasurehacktory.ui

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.treasurehacktory.R
import com.treasurehacktory.TreasureHacktoryApplication
import com.treasurehacktory.capitalized
import com.treasurehacktory.databinding.HoardEventItemBinding
import com.treasurehacktory.databinding.HoardEventTagLayoutBinding
import com.treasurehacktory.databinding.LayoutHoardEventLogBinding
import com.treasurehacktory.model.HoardEvent
import com.treasurehacktory.viewmodel.HoardEventLogViewModel
import com.treasurehacktory.viewmodel.HoardEventLogViewModelFactory
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
        hoardEventLogViewModel.hoardNameLiveData.observe(viewLifecycleOwner) { hoardName ->
            if (hoardName.isNotBlank()) {
                binding.hoardEventLogToolbar.subtitle = hoardName
            }
        }

        // Set up toolbar
        binding.hoardEventLogToolbar.apply{

            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val colorOnPrimary = typedValue.data

            inflateMenu(R.menu.event_log_toolbar_menu)
            title = getString(R.string.hoard_event_log_fragment_title)
            setTitleTextColor(colorOnPrimary)
            setSubtitleTextColor(colorOnPrimary)
            setNavigationIcon(R.drawable.clipart_back_vector_icon)
            navigationIcon?.apply {
                setTint(colorOnPrimary)
            }
            overflowIcon?.apply{
                setTint(colorOnPrimary)
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->
                when (item.itemId){

                    R.id.action_add_user_event -> {

                        val action =
                            HoardEventLogFragmentDirections.eventLogToAddHoardEventDialogAction(
                                safeArgs.activeHoardID
                            )

                        findNavController().navigate(action)

                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

                val bgTint = rawTag.getBackgroundColorList()
                val fgTint = rawTag.getForegroundColor()

                binding.hoardEventTagIcon.apply {
                    setImageResource(rawTag.toDrawableResID())
                    drawable.setTint(fgTint)
                }

                binding.hoardEventTagText.apply{
                    text = rawTag.refine()
                    setTextColor(fgTint)
                }

                binding.hoardEventTagLayout.apply{
                    backgroundTintList = bgTint
                    backgroundTintMode = PorterDuff.Mode.SRC_ATOP
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
                    "map" ->                R.drawable.clipart_map_vector_icon
                    "merge" ->              R.drawable.clipart_merge_vector_icon
                    "modification" ->       R.drawable.clipart_edit_vector_icon
                    "note" ->               R.drawable.clipart_extranotes_vector_icon
                    "sale" ->               R.drawable.clipart_sale_vector_icon
                    "spell-collection"->    R.drawable.clipart_spellbook_vector_icon
                    "system" ->             R.drawable.clipart_android_vector_icon
                    "user" ->               R.drawable.clipart_user_vector_icon
                    "verbose" ->            R.drawable.clipart_info_vector_icon
                    else ->                 R.drawable.clipart_tag_vector_icon
                }
            }

            private fun String.getBackgroundColorList() : ColorStateList {

                return when (this){
                    "art-object" ->         ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.artPrimary,null))
                    )
                    "coinage" -> ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.silver,null))
                    )
                    "gemstone" ->           ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.gemPrimary,null))
                    )
                    "homebrew" ->           ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.orange,null))
                    )
                    "magic-item" ->         ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.gemPrimary,null))
                    )
                    "spell-collection"->    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.spellPrimary,null))
                    )
                    "system" ->             ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.emerald,null))
                    )
                    "user" ->               ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.azure_blue,null))
                    )
                    "verbose" ->            ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.gray,null))
                    )
                    else ->                 ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(resources.getColor(R.color.defaultSecondary,null))
                    )
                }
            }

            @ColorInt
            private fun String.getForegroundColor() : Int {

                return when (this){
                    "art-object" ->         R.color.artOnPrimary
                    "coinage" ->            R.color.black
                    "gemstone" ->           R.color.gemOnPrimary
                    "homebrew" ->           R.color.white
                    "magic-item" ->         R.color.magicOnPrimary
                    "spell-collection"->    R.color.spellOnPrimary
                    "system" ->             R.color.white
                    "user" ->               R.color.white
                    "verbose" ->            R.color.iron
                    else ->                 R.color.defaultOnSecondary
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