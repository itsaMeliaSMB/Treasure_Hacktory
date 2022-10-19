package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.*
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.viewmodel.UniqueDetailsViewModel
import com.example.android.treasurefactory.viewmodel.UniqueDetailsViewModelFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class UniqueDetailsFragment() : Fragment() {

    // region [ Property declarations ]
    private val backCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {

            if (uniqueDetailsViewModel.isRunningAsyncLiveData.value != true) {

                findNavController().popBackStack()

            } else {

                Toast.makeText(context,"Cannot navigate back; processes still running.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var shortAnimationDuration = 0
    private var isContentFrameAnimating = false

    private lateinit var parentHoard: Hoard
    private lateinit var viewedItem : ViewableItem

    private val safeArgs : UniqueDetailsFragmentArgs by navArgs()

    private var _binding: LayoutUniqueDetailsBinding? = null
    private val binding get() = _binding!!

    private var parentAdapter : ParentListAdapter? = ParentListAdapter(emptyList())

    private val uniqueDetailsViewModel: UniqueDetailsViewModel by viewModels {
        UniqueDetailsViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }
    // endregion

    // region [ Overridden functions ]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentHoard = Hoard()

        safeArgs.let{
            uniqueDetailsViewModel.loadItemArgs(it.itemID,it.itemType,it.hoardID)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = LayoutUniqueDetailsBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.uniqueDetailsRecycler.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = parentAdapter
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // region [ Listeners ]

        uniqueDetailsViewModel.apply{

            isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

                if (isRunningAsync) {

                    binding.uniqueDetailsViewableGroup.isEnabled = false

                    if (binding.uniqueDetailsContentFrame.visibility == View.VISIBLE &&
                        !isContentFrameAnimating) {

                        hideContentFrameCrossfade()

                    } else {

                        binding.uniqueDetailsContentFrame.visibility = View.GONE
                        binding.uniqueDetailsProgressIndicator.visibility = View.VISIBLE
                        isContentFrameAnimating = false
                    }

                } else {

                    binding.uniqueDetailsViewableGroup.isEnabled = true

                    if (binding.uniqueDetailsProgressIndicator.visibility == View.VISIBLE &&
                        !isContentFrameAnimating) {

                        showContentFrameCrossfade()

                    } else {

                        binding.uniqueDetailsContentFrame.visibility = View.VISIBLE
                        binding.uniqueDetailsProgressIndicator.visibility = View.GONE
                        isContentFrameAnimating = false
                    }
                }
            }

            textToastHolderLiveData.observe(viewLifecycleOwner) { pendingAlert ->

                if (pendingAlert != null) {

                    // Show the pending toast
                    Toast.makeText(context,pendingAlert.first,pendingAlert.second).show()

                    // Clear the livedata for pending toasts
                    uniqueDetailsViewModel.textToastHolderLiveData.value = null
                }
            }

            exposedHoardLiveData.observe(viewLifecycleOwner) { hoard ->

                if (hoard != null) {

                    parentHoard = hoard

                    safeArgs.let{
                        updateViewedItem(it.itemID,it.itemType,it.hoardID)
                    }
                }
            }

            viewedItemLiveData.observe(viewLifecycleOwner) { newItem ->

                if (newItem != null){

                    viewedItem = newItem

                    updateUI()

                } else setNullUI()

            }
        }
        // endregion

        // region [ Toolbar ]
        binding.uniqueDetailsToolbar.apply {

            // Get themed color attribute for Toolbar's title
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val titleTextColor = typedValue.data

            inflateMenu(R.menu.hoard_overview_toolbar_menu)
            title = getString(R.string.item_details)
            setTitleTextColor(titleTextColor)
            setNavigationIcon(R.drawable.clipart_back_vector_icon)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.action_edit_item      -> {

                        //TODO implement

                        Toast.makeText(context, "Not yet implemented, sorry!",
                            Toast.LENGTH_SHORT)

                        true
                    }

                    R.id.action_copy_item_text    -> {

                        //TODO implement

                        Toast.makeText(context, "Not yet implemented, sorry!",
                            Toast.LENGTH_SHORT)

                        true
                    }

                    else    -> false
                }
            }
        }
        // endregion
    }

    // endregion

    // region [ Inner classes ]

    private inner class DetailEntryAdapter(val detailEntries : List<DetailEntry>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

        private val PLAIN_TEXT = 0
        private val LABELLED_QUALITY = 1
        private val SIMPLE_SPELL = 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType) {

                PLAIN_TEXT -> {

                    val binding = UniqueDetailsItemSimpleBinding
                        .inflate(LayoutInflater.from(parent.context),parent,false)

                    PlainTextHolder(binding)
                }

                LABELLED_QUALITY -> {

                    val binding = UniqueDetailsItemLabelledBinding
                        .inflate(LayoutInflater.from(parent.context),parent,false)

                    LabelledQualityHolder(binding)
                }

                SIMPLE_SPELL -> {
                    val binding = UniqueDetailsItemSpellBinding
                        .inflate(LayoutInflater.from(parent.context),parent,false)

                    SimpleSpellHolder(binding)
                }

                else -> {

                    val binding = UniqueDetailsItemSimpleBinding
                        .inflate(LayoutInflater.from(parent.context),parent,false)

                    PlainTextHolder(binding)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
           when (holder.itemViewType) {

               PLAIN_TEXT -> {

                   val entry = detailEntries[position] as PlainTextEntry

                   (holder as PlainTextHolder).bind(entry)
               }

               LABELLED_QUALITY -> {

                   val entry = detailEntries[position] as LabelledQualityEntry

                   (holder as LabelledQualityHolder).bind(entry)
               }

               SIMPLE_SPELL -> {

                   val entry = detailEntries[position] as SimpleSpellEntry

                   (holder as SimpleSpellHolder).bind(entry,position)
               }

               else -> {
                   (holder as PlainTextHolder).bind(PlainTextEntry("Oops! There was an error " +
                           "loading this detail."))
               }
           }
        }

        override fun getItemCount(): Int = detailEntries.size

        override fun getItemViewType(position: Int): Int {
            return when (detailEntries[position]){
                is PlainTextEntry   -> PLAIN_TEXT
                is LabelledQualityEntry -> LABELLED_QUALITY
                is SimpleSpellEntry -> SIMPLE_SPELL
            }
        }

        private inner class PlainTextHolder(val binding: UniqueDetailsItemSimpleBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var entry : PlainTextEntry

            fun bind(newEntry: PlainTextEntry) {

                entry = newEntry

                binding.uniqueDetailListSimpleTextview.text = entry.message
            }
        }

        private inner class LabelledQualityHolder(val binding: UniqueDetailsItemLabelledBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var entry : LabelledQualityEntry

            fun bind(newEntry: LabelledQualityEntry) {

                entry = newEntry

                binding.apply{
                    uniqueDetailsListItemLabel.text = entry.caption
                    uniqueDetailsListItemValue.text = entry.value
                }
            }
        }

        private inner class SimpleSpellHolder(val binding: UniqueDetailsItemSpellBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var entry : SimpleSpellEntry

            fun bind(newEntry: SimpleSpellEntry, position: Int) {

                entry = newEntry

                binding.apply{

                    "#${position + 1}".also { spellItemIndexLabel.text = it }

                    if (entry.subclass.isNotBlank()) {
                        when (entry.subclass) {
                            "Vengeance" -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.sanguine,
                                            context?.theme))
                                spellItemSubclassText.setTextColor(
                                    resources.getColor(R.color.white,
                                        context?.theme))
                            }
                            "Woeful" -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.rust,
                                            context?.theme))
                                spellItemSubclassText.setTextColor(
                                    resources.getColor(R.color.white,
                                        context?.theme))
                            }
                            "Wild" -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.white,
                                            context?.theme))
                                spellItemSubclassText.apply{
                                    setTextColor(
                                        resources.getColor(R.color.amethyst,
                                            context?.theme))
                                    typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        }
                    }

                    spellItemTypeBackdrop.apply{
                        setImageDrawable(
                            ResourcesCompat.getDrawable(resources,
                                if(entry.isUsed){

                                    when (entry.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient_used
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient_used
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient_used
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }

                                } else {
                                    when (entry.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient_used
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient_used
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient_used
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }
                                }, context?.theme)
                        )
                        if (entry.discipline == SpCoDiscipline.ALL_MAGIC) alpha = 0.5f
                    }

                    spellItemName.text = entry.name

                    spellItemLevel.text = if (entry.level == 0) {
                        "Cantrip"
                    } else { "Level ${entry.level}" }

                    when (entry.schools.size) {

                        0   -> {
                            spellItemSchoolCard1.visibility = View.GONE
                            spellItemSchoolCard2.visibility = View.GONE
                            spellItemSchoolCard3.visibility = View.GONE
                        }

                        1   -> {

                            spellItemSchoolCard1.visibility = View.VISIBLE

                            spellItemSchoolImage1.setImageResource(
                                when (entry.schools[0]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )

                            spellItemSchoolCard2.visibility = View.GONE

                            spellItemSchoolCard3.visibility = View.GONE

                        }

                        2   -> {

                            spellItemSchoolCard1.visibility = View.VISIBLE

                            spellItemSchoolImage1.setImageResource(
                                when (entry.schools[0]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )

                            spellItemSchoolCard2.visibility = View.VISIBLE

                            spellItemSchoolImage2.setImageResource(
                                when (entry.schools[1]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )

                            spellItemSchoolCard3.visibility = View.GONE

                        }

                        else-> {

                            spellItemSchoolCard1.visibility = View.VISIBLE

                            spellItemSchoolImage1.setImageResource(
                                when (entry.schools[0]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )

                            spellItemSchoolCard2.visibility = View.VISIBLE

                            spellItemSchoolImage2.setImageResource(
                                when (entry.schools[1]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )

                            spellItemSchoolCard3.visibility = View.VISIBLE

                            spellItemSchoolImage3.setImageResource(
                                when (entry.schools[2]) {
                                    SpellSchool.ABJURATION -> R.drawable.spell_school_abjuration
                                    SpellSchool.ALTERATION -> R.drawable.spell_school_alteration
                                    SpellSchool.CONJURATION -> R.drawable.spell_school_conjuration
                                    SpellSchool.DIVINATION -> R.drawable.spell_school_divination
                                    SpellSchool.ENCHANTMENT -> R.drawable.spell_school_enchantment
                                    SpellSchool.EVOCATION -> R.drawable.spell_school_evocation
                                    SpellSchool.ILLUSION -> R.drawable.spell_school_illusion
                                    SpellSchool.NECROMANCY -> R.drawable.spell_school_necromancy
                                }
                            )
                        }
                    }

                    spellItemSource.text = entry.sourceString

                    spellItemLayout.setOnClickListener {

                        //TODO placeholder toast. Should launch spell dialog.
                        Toast.makeText(context,"\"${entry.name}\" from ${viewedItem.name} " +
                                "[id: ${viewedItem.itemID}] clicked.",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private inner class ParentListAdapter(val parentLists: List<Pair<String,List<DetailEntry>>>) : RecyclerView.Adapter<ParentListAdapter.ParentListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentListHolder {
            val binding = UniqueDetailsItemParentBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return ParentListHolder(binding)
        }

        override fun onBindViewHolder(holder: ParentListHolder, position: Int) {

            val parentList = parentLists[position]

            holder.bind(parentList)
        }

        override fun getItemCount(): Int = parentLists.size

        inner class ParentListHolder(val binding: UniqueDetailsItemParentBinding) : RecyclerView.ViewHolder(binding.root) {

            private lateinit var parentList: Pair<String,List<DetailEntry>>

            fun bind(newParent: Pair<String,List<DetailEntry>>){

                parentList = newParent

                val innerLayoutManager = LinearLayoutManager(context)
                val innerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

                binding.apply{

                    uniqueDetailParentIndicator.rotation = 0f
                    uniqueDetailParentTitle.text = parentList.first
                    uniqueDetailParentHeaderIcon.setImageResource(
                        when (parentList.first) {
                            "Artifact particulars"          -> R.drawable.clipart_artifact_crown_vector_icon
                            "Artwork details"               -> R.drawable.clipart_painting_vector_icon
                            "Intelligent weapon info"       -> R.drawable.clipart_winged_sword_vector_icon
                            "Map details"                   -> R.drawable.clipart_map_vector_icon
                            "Potion flavor text"            -> R.drawable.clipart_potion_vector_icon
                            "Spell collection properties"   -> R.drawable.clipart_spellbook_vector_icon
                            "Spell list"                    -> R.drawable.badge_hoard_magic
                            "Stone details"                 -> R.drawable.clipart_gem_vector_icon
                            "Your memos"                    -> R.drawable.clipart_user_vector_icon
                            else                            -> R.drawable.clipart_extranotes_vector_icon
                        }
                    )
                    uniqueDetailParentRecycler.apply{
                        visibility = View.GONE
                        layoutManager = innerLayoutManager
                        adapter = DetailEntryAdapter(parentList.second)
                        addItemDecoration(innerDecoration)
                    }
                    uniqueDetailParentHeader.setOnClickListener {
                        if (binding.uniqueDetailParentRecycler.isVisible){

                            binding.apply{
                                uniqueDetailParentRecycler.visibility = View.VISIBLE
                                uniqueDetailParentIndicator.rotation = 0f
                            }

                        } else {

                            binding.apply{
                                uniqueDetailParentRecycler.visibility = View.VISIBLE
                                uniqueDetailParentIndicator.rotation = 90f
                            }
                        }
                    }
                }
            }
        }
    }

    // endregion

    // region [ Helper functions ]

    @SuppressLint("SimpleDateFormat")
    private fun updateUI() {
        binding.apply{

            // Toolbar
            uniqueDetailsToolbar.subtitle = parentHoard.name

            // Header
            uniqueDetailsFrameForeground.setImageResource(
                when (viewedItem.iFrameFlavor){
                    ItemFrameFlavor.NORMAL -> R.drawable.itemframe_foreground
                    ItemFrameFlavor.CURSED -> R.drawable.itemframe_foreground_cursed
                    ItemFrameFlavor.GOLDEN -> R.drawable.itemframe_foreground_golden
                }
            )

            try {

                uniqueDetailsThumbnail
                    .setImageResource(resources
                        .getIdentifier(viewedItem.iconStr,"drawable",view?.context?.packageName))

            } catch (e: Exception) {

                uniqueDetailsThumbnail
                    .setImageResource(R.drawable.loot_lint)
            }

            uniqueDetailsNameLabel.text = viewedItem.name

            uniqueDetailsSubtitle.text = viewedItem.subtitle

            // Static details
            uniqueDetailsFullNameValue.text = viewedItem.name

            uniqueDetailsCreationDateValue.text = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                .format(viewedItem.creationTime)

            uniqueDetailsSourceValue.text = viewedItem.source

            "Page ${viewedItem.sourcePage}".also { uniqueDetailsSourcePage.text = it }

            (NumberFormat.getNumberInstance().format(viewedItem.xpValue) +
                    " xp").also {uniqueDetailsXpValue.text = it }

            (DecimalFormat("#,##0.0#")
                .format(viewedItem.gpValue)
                .removeSuffix(".0") + " gp")
                .also{ uniqueDetailsGpValue.text = it }

            // Usable-By group
            when (viewedItem) {
                is ViewableMagicItem -> {

                    uniqueDetailsUsableLabel.visibility = View.VISIBLE

                    uniqueDetailsFighter.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Fighter"] == true) {
                            setImageResource(R.drawable.class_fighter_colored)
                            tooltipText = context.getString(R.string.usable_by_fighters)
                            1f
                        } else {
                            setImageResource(R.drawable.class_fighter_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsMagicUser.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Magic-user"] == true) {
                            setImageResource(R.drawable.class_magic_user_colored)
                            tooltipText = context.getString(R.string.usable_by_magic_users)
                            1f
                        } else {
                            setImageResource(R.drawable.class_magic_user_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsThief.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Thief"] == true) {
                            setImageResource(R.drawable.class_thief_colored)
                            tooltipText = context.getString(R.string.usable_by_thieves)
                            1f
                        } else {
                            setImageResource(R.drawable.class_thief_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsCleric.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Cleric"] == true) {
                            setImageResource(R.drawable.class_cleric_colored)
                            tooltipText = context.getString(R.string.usable_by_clerics)
                            1f
                        } else {
                            setImageResource(R.drawable.class_cleric_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsDruid.apply{
                        
                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Druid"] == true) {
                            setImageResource(R.drawable.class_druid_colored)
                            tooltipText = context.getString(R.string.usable_by_druids)
                            1f
                        } else {
                            setImageResource(R.drawable.class_druid_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }

                }
                is ViewableSpellCollection -> {

                    uniqueDetailsUsableLabel.visibility = View.VISIBLE
                    uniqueDetailsFighter.visibility = View.GONE
                    uniqueDetailsThief.visibility = View.GONE

                    uniqueDetailsMagicUser.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Magic-user"] == true) {
                            setImageResource(R.drawable.class_magic_user_colored)
                            tooltipText = context.getString(R.string.usable_by_arcane_casters)
                            1f
                        } else {
                            setImageResource(R.drawable.class_magic_user_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsCleric.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Cleric"] == true) {
                            setImageResource(R.drawable.class_cleric_colored)
                            tooltipText = context.getString(R.string.usable_by_divine_casters)
                            1f
                        } else {
                            setImageResource(R.drawable.class_cleric_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                    uniqueDetailsDruid.apply{

                        alpha = if ((viewedItem as ViewableMagicItem).mgcClassUsability["Druid"] == true) {
                            setImageResource(R.drawable.class_druid_colored)
                            tooltipText = context.getString(R.string.usable_by_natural_casters)
                            1f
                        } else {
                            setImageResource(R.drawable.class_druid_outline)
                            0.25f
                        }

                        visibility = View.VISIBLE
                    }
                }
                else -> {

                    uniqueDetailsUsableLabel.visibility = View.GONE
                    uniqueDetailsFighter.visibility = View.GONE
                    uniqueDetailsMagicUser.visibility = View.GONE
                    uniqueDetailsThief.visibility = View.GONE
                    uniqueDetailsCleric.visibility = View.GONE
                    uniqueDetailsDruid.visibility = View.GONE
                }
            }

            // Item detail group
            if (viewedItem.details.isNotEmpty()) {

                uniqueDetailsDivider.visibility = View.VISIBLE
                uniqueDetailsListLabel.visibility = View.VISIBLE

                parentAdapter = ParentListAdapter(viewedItem.details)

                uniqueDetailsRecycler.apply{
                    adapter = parentAdapter
                    visibility = View.VISIBLE
                }
            } else {

                uniqueDetailsDivider.visibility = View.GONE
                uniqueDetailsListLabel.visibility = View.GONE
                uniqueDetailsRecycler.visibility = View.GONE
            }

            // Reveal viewable group only after UI fully updated
            uniqueDetailsWhenemptyGroup.visibility = View.GONE
            uniqueDetailsViewableGroup.visibility = View.VISIBLE
        }
    }

    private fun setNullUI() {
        binding.apply{
            uniqueDetailsToolbar.subtitle = parentHoard.name
            uniqueDetailsViewableGroup.visibility = View.GONE
            uniqueDetailsWhenemptyGroup.visibility = View.VISIBLE
        }
    }

    private fun showContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.uniqueDetailsContentFrame.apply {

            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.uniqueDetailsProgressIndicator.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.uniqueDetailsProgressIndicator.visibility = View.GONE
                        isContentFrameAnimating = false
                    }
                })
        }
    }

    private fun hideContentFrameCrossfade() {

        isContentFrameAnimating = true

        binding.uniqueDetailsProgressIndicator.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.uniqueDetailsContentFrame.apply {

            alpha = 1f
            visibility = View.VISIBLE

            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isContentFrameAnimating = false
                        binding.uniqueDetailsContentFrame.visibility = View.GONE
                    }
                })
        }
    }

    // endregion
}