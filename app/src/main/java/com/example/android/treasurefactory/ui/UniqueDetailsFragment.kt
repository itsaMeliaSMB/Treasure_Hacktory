package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.Typeface.BOLD_ITALIC
import android.graphics.Typeface.DEFAULT_BOLD
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.HACKMASTER_CLASS_ITEM_TEXT
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
    private var isWaitingCardAnimating = false

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

                   // binding.uniqueDetailsViewableGroup.isEnabled = false

                    if (binding.uniqueDetailsWaitingCard.waitingCard.visibility == View.GONE &&
                        !isWaitingCardAnimating) {

                        fadeInWaitingCard()

                    } else {

                        binding.uniqueDetailsWaitingCard.waitingCard.visibility = View.VISIBLE
                        isWaitingCardAnimating = false
                    }

                } else {

                    //binding.uniqueDetailsViewableGroup.isEnabled = true

                    if (binding.uniqueDetailsWaitingCard.waitingCard.visibility == View.VISIBLE &&
                        !isWaitingCardAnimating) {

                        fadeOutWaitingCard()

                    } else {

                        binding.uniqueDetailsWaitingCard.waitingCard.visibility = View.GONE
                        isWaitingCardAnimating = false
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

                } else {

                    setNullUI()
                }

            }

            dialogSpellInfoLiveData.observe(viewLifecycleOwner) { spellPair ->

                if (spellPair != null){

                    spellPair.let{ (spell, entry) ->

                        if (spell != null) {



                        } else {

                            Toast.makeText(context, "Could not find details for #${
                                entry.spellsPos}: \"${entry.name}\" (id: ${entry.spellID})",
                                Toast.LENGTH_LONG)
                        }
                    }

                    dialogSpellInfoLiveData.value = null
                }
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

            inflateMenu(R.menu.unique_details_toolbar_menu)
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

                binding.uniqueDetailListSimpleTextview.apply {
                    when {
                        //TODO left off here. cheeky formatting. Good luck getting everything done!
                        (entry.message.startsWith("[ ") && entry.message.endsWith(" ]")) -> {
                            text = entry.message.removeSurrounding("[ "," ]")
                            setTypeface(DEFAULT_BOLD)
                        }
                        (entry.message == HACKMASTER_CLASS_ITEM_TEXT) -> {
                            setTypeface(null, BOLD_ITALIC)
                            textSize = 18f
                            setCompoundDrawablesRelative(
                                getDrawable(resources,R.drawable.clipart_winged_sword_vector_icon,context?.theme),
                                null,
                                getDrawable(resources,R.drawable.clipart_winged_sword_vector_icon,context?.theme),
                                null
                            )
                            gravity = CENTER
                            for (drawable in this.compoundDrawables) {
                                if (drawable != null) {
                                    drawable.colorFilter =
                                        PorterDuffColorFilter(
                                            ContextCompat.getColor(
                                                this.context,
                                                R.color.golden
                                            ), PorterDuff.Mode.SRC_IN
                                        )
                                }
                            }
                            setTextColor(resources.getColor(R.color.golden,context.theme))
                        }
                        //TODO if you ever figure out spanned text, add check for colors for potion flavor text
                        else    -> {
                            text = entry.message
                        }
                    }
                }

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

                        spellItemSubclassCard.visibility = View.VISIBLE

                        when (entry.subclass) {
                            "Vengeance" -> {
                                spellItemSubclassCard.setCardBackgroundColor(
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
                            "Choice" -> {

                                if (entry.name.startsWith("GM")){

                                    spellItemSubclassCard
                                        .setCardBackgroundColor(
                                            resources.getColor(R.color.ultramarine,
                                                context?.theme))
                                    spellItemSubclassText.setTextColor(
                                        resources.getColor(R.color.white,
                                            context?.theme))

                                } else {
                                    spellItemSubclassCard
                                        .setCardBackgroundColor(
                                            resources.getColor(R.color.teal,
                                                context?.theme))
                                    spellItemSubclassText.setTextColor(
                                        resources.getColor(R.color.white,
                                            context?.theme))
                                }
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
                            getDrawable(resources,
                                if(entry.isUsed){

                                    when (entry.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient_used
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient_used
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient_used
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }

                                } else {
                                    when (entry.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }
                                }, context?.theme)
                        )
                        if (entry.discipline == SpCoDiscipline.ALL_MAGIC) {

                            alpha = if (entry.isUsed) { 0.25f } else { 0.5f }
                        }
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

                            spellItemSchoolCard1.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[0]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                            spellItemSchoolCard1.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[0]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                            spellItemSchoolCard2.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[1]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                            spellItemSchoolCard1.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[0]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                            spellItemSchoolCard2.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[1]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                            spellItemSchoolCard3.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (entry.schools[2]) {
                                    SpellSchool.ABJURATION ->
                                        getString(R.string.spell_school_abjuration_l)
                                    SpellSchool.ALTERATION ->
                                        getString(R.string.spell_school_alteration_l)
                                    SpellSchool.CONJURATION ->
                                        getString(R.string.spell_school_conjuration_l)
                                    SpellSchool.DIVINATION ->
                                        getString(R.string.spell_school_divination_l)
                                    SpellSchool.ENCHANTMENT ->
                                        getString(R.string.spell_school_enchantment_l)
                                    SpellSchool.EVOCATION ->
                                        getString(R.string.spell_school_evocation_l)
                                    SpellSchool.ILLUSION ->
                                        getString(R.string.spell_school_illusion_l)
                                    SpellSchool.NECROMANCY ->
                                        getString(R.string.spell_school_necromancy_l)
                                }
                            }

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

                        if (uniqueDetailsViewModel.isRunningAsyncLiveData.value != true) {
                            entry.requestSpellForDialog()
                        }
                    }
                }
            }
        }
    }

    private inner class ParentListAdapter(val parentLists: List<Pair<String,List<DetailEntry>>>)
        : RecyclerView.Adapter<ParentListAdapter.ParentListHolder>() {

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

        inner class ParentListHolder(val binding: UniqueDetailsItemParentBinding)
            : RecyclerView.ViewHolder(binding.root) {

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
                                uniqueDetailParentRecycler.visibility = View.GONE
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
            when (viewedItem.iFrameFlavor){
                ItemFrameFlavor.NORMAL -> {
                    uniqueDetailsFrameForeground.setImageResource(R.drawable.itemframe_foreground)
                    uniqueDetailsFrameBackground.setImageResource(R.drawable.itemframe_background_gray)
                }
                ItemFrameFlavor.CURSED -> {
                    uniqueDetailsFrameForeground.setImageResource(R.drawable.itemframe_foreground_cursed)
                    uniqueDetailsFrameBackground.setImageResource(R.drawable.itemframe_background_cursed)
                }
                ItemFrameFlavor.GOLDEN -> {
                    uniqueDetailsFrameForeground.setImageResource(R.drawable.itemframe_foreground_golden)
                    uniqueDetailsFrameBackground.setImageResource(R.drawable.itemframe_background_golden)
                }
            }

            try {

                uniqueDetailsThumbnail
                    .setImageResource(resources
                        .getIdentifier(viewedItem.iconStr,"drawable",view?.context?.packageName))

            } catch (e: Exception) {

                uniqueDetailsThumbnail
                    .setImageResource(R.drawable.loot_lint)
            }

            if(viewedItem.name.endsWith("*")){

                uniqueDetailsNameLabel.apply{
                    text = viewedItem.name.removeSuffix("*")
                    setTextColor(resources.getColor(R.color.ultramarine,context.theme))
                }

            } else {
                uniqueDetailsNameLabel.text = viewedItem.name
            }

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


                    uniqueDetailsFighter.visibility = View.GONE
                    uniqueDetailsThief.visibility = View.GONE

                    when ((viewedItem as ViewableSpellCollection).spCoDiscipline) {
                        SpCoDiscipline.ARCANE -> {

                            uniqueDetailsUsableLabel.visibility = View.VISIBLE

                            uniqueDetailsMagicUser.apply{
                                alpha = 1f
                                setImageResource(R.drawable.class_magic_user_colored)
                                tooltipText = context.getString(R.string.usable_by_arcane_casters)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsCleric.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_cleric_outline)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsDruid.apply{

                                alpha = 0.25f
                                setImageResource(R.drawable.class_druid_outline)
                                visibility = View.VISIBLE
                            }
                        }
                        SpCoDiscipline.DIVINE -> {
                            uniqueDetailsUsableLabel.visibility = View.VISIBLE

                            uniqueDetailsMagicUser.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_magic_user_outline)
                                tooltipText = context.getString(R.string.usable_by_arcane_casters)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsCleric.apply{
                                alpha = 1f
                                setImageResource(R.drawable.class_cleric_colored)
                                tooltipText = context.getString(R.string.usable_by_divine_casters)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsDruid.apply{

                                alpha = 0.25f
                                setImageResource(R.drawable.class_druid_outline)
                                visibility = View.VISIBLE
                            }
                        }
                        SpCoDiscipline.NATURAL -> {
                            uniqueDetailsUsableLabel.visibility = View.VISIBLE

                            uniqueDetailsMagicUser.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_magic_user_outline)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsCleric.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_cleric_outline)
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsDruid.apply{

                                alpha = 1f
                                setImageResource(R.drawable.class_druid_colored)
                                tooltipText = context.getString(R.string.usable_by_natural_casters)
                                visibility = View.VISIBLE
                            }
                        }
                        SpCoDiscipline.ALL_MAGIC -> {
                            uniqueDetailsUsableLabel.visibility = View.GONE
                            uniqueDetailsMagicUser.visibility = View.GONE
                            uniqueDetailsCleric.visibility = View.GONE
                            uniqueDetailsDruid.visibility = View.GONE
                        }
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

            Log.d("updateUI()","About to reveal viewable group")

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
        Log.d("setNullUI()","nullUI set.")
    }

    private fun fadeOutWaitingCard() {

        isWaitingCardAnimating = true

        binding.uniqueDetailsWaitingCard.waitingCard.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        this@apply.visibility = View.GONE
                        isWaitingCardAnimating = false
                    }
                })
        }
    }

    private fun fadeInWaitingCard() {

        isWaitingCardAnimating = true

        binding.uniqueDetailsWaitingCard.waitingCard.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isWaitingCardAnimating = false
                    }
                })
        }
    }

    private fun SimpleSpellEntry.requestSpellForDialog() {

        uniqueDetailsViewModel.fetchSpellForDialog(this)
    }

    private fun showSpellDialog(spell: Spell, entry: SimpleSpellEntry) {

        fun SpCoDiscipline.getClassString() : String = when (this) {
            SpCoDiscipline.ARCANE   -> "Magic-User"
            SpCoDiscipline.DIVINE   -> "Cleric"
            SpCoDiscipline.NATURAL  -> "Druid"
            SpCoDiscipline.ALL_MAGIC-> "indeterminate"
        }

        val dialogBinding: DialogSpellDetailsBinding =
            DialogSpellDetailsBinding.inflate(layoutInflater)

        dialogBinding.apply {

            // Spell school icons
            when (spell.schools.size) {
                0   -> {

                    spellDialogSchoolCard1.visibility = View.GONE
                    spellDialogSchoolCard2.visibility = View.GONE
                    spellDialogSchoolCard3.visibility = View.GONE
                }

                1   -> {

                    spellDialogSchoolCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[0].getLongName(context)
                    }
                    spellDialogSchool1.setImageResource(
                        spell.schools[0].getDrawableResID(requireContext()))

                    spellDialogSchoolCard2.visibility = View.GONE
                    spellDialogSchoolCard3.visibility = View.GONE
                }

                2   -> {

                    spellDialogSchoolCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[1].getLongName(context)
                    }
                    spellDialogSchool1.setImageResource(
                        spell.schools[1].getDrawableResID(requireContext()))

                    spellDialogSchoolCard2.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[1].getLongName(context)
                    }
                    spellDialogSchool2.setImageResource(
                        spell.schools[1].getDrawableResID(requireContext()))

                    spellDialogSchoolCard3.visibility = View.GONE
                }

                else-> {

                    spellDialogSchoolCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[1].getLongName(context)
                    }
                    spellDialogSchool1.setImageResource(
                        spell.schools[1].getDrawableResID(requireContext()))

                    spellDialogSchoolCard2.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[1].getLongName(context)
                    }
                    spellDialogSchool2.setImageResource(
                        spell.schools[1].getDrawableResID(requireContext()))

                    spellDialogSchoolCard3.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.schools[2].getLongName(context)
                    }
                    spellDialogSchool3.setImageResource(
                        spell.schools[2].getDrawableResID(requireContext()))

                }
            }

            if (entry.isUsed){
                spellDialogName.apply{
                    alpha = 0.45f
                    text = spell.name
                }
                spellDialogUsedLabel.visibility = View.VISIBLE

            } else {
                spellDialogName.apply{
                    alpha = 1.0f
                    text = spell.name
                }
                spellDialogUsedLabel.visibility = View.GONE
            }

            spellDialogChooseButton.visibility = if (spell.name.contains(" Choice ")) {
                View. VISIBLE
            } else { View.GONE }

            spellDialogLeveltype.text= when (spell.spellLevel) {
                0   -> "Magic-User Cantrip"
                1   -> {
                    "1st Level " + spell.type.getClassString() + " spell"
                }
                2   -> {
                    "2nd Level " + spell.type.getClassString() + " spell"
                }
                3   -> {
                    "3rd Level " + spell.type.getClassString() + " spell"
                }
                else-> {
                    "${spell.spellLevel}th Level " + spell.type.getClassString() + " spell"
                }
            }

            spellDialogReversible.apply{

                visibility = if (spell.reverse) {
                    if (spell.type == SpCoDiscipline.ARCANE) {
                        getString(R.string.spell_is_reversible)
                    } else {
                        getString(R.string.spell_is_reversed)
                    }

                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            spellDialogSourceValue.text = spell.sourceText

            "Page ${spell.sourcePage}".also { spellDialogSourcePage.text = it }

            spellDialogSubclassLabel.visibility = if (spell.subclass.isNotBlank()) {

                spellDialogSubclassValue.apply{
                    text = spell.subclass
                    visibility = View.VISIBLE
                }
                View.VISIBLE
            } else {

                spellDialogSubclassValue.visibility = View.GONE
                View.GONE
            }

            spellDialogSpheresLabel.visibility = when (spell.spheres.size) {

                1   -> {
                    spellDialogSphereCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[0].getNameString(context)
                    }
                    spellDialogSphere1.setImageResource(
                        spell.spheres[0].getDrawableResID(requireContext()))

                    spellDialogSphereCard2.visibility = View.GONE
                    spellDialogSphereCard3.visibility = View.GONE
                    spellDialogSphereCard4.visibility = View.GONE
                    View.VISIBLE
                }
                2   -> {
                    spellDialogSphereCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[0].getNameString(context)
                    }
                    spellDialogSphere1.setImageResource(
                        spell.spheres[0].getDrawableResID(requireContext()))

                    spellDialogSphereCard2.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[1].getNameString(context)
                    }
                    spellDialogSphere2.setImageResource(
                        spell.spheres[1].getDrawableResID(requireContext()))

                    spellDialogSphereCard3.visibility = View.GONE
                    spellDialogSphereCard4.visibility = View.GONE
                    View.VISIBLE
                }
                3   -> {
                    spellDialogSphereCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[0].getNameString(context)
                    }
                    spellDialogSphere1.setImageResource(
                        spell.spheres[0].getDrawableResID(requireContext()))

                    spellDialogSphereCard2.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[1].getNameString(context)
                    }
                    spellDialogSphere2.setImageResource(
                        spell.spheres[1].getDrawableResID(requireContext()))

                    spellDialogSphereCard3.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[2].getNameString(context)
                    }
                    spellDialogSphere3.setImageResource(
                        spell.spheres[2].getDrawableResID(requireContext()))

                    spellDialogSphereCard4.visibility = View.GONE
                    View.VISIBLE
                }
                4   -> {
                    spellDialogSphereCard1.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[0].getNameString(context)
                    }
                    spellDialogSphere1.setImageResource(
                        spell.spheres[0].getDrawableResID(requireContext()))

                    spellDialogSphereCard2.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[1].getNameString(context)
                    }
                    spellDialogSphere2.setImageResource(
                        spell.spheres[1].getDrawableResID(requireContext()))

                    spellDialogSphereCard3.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[2].getNameString(context)
                    }
                    spellDialogSphere3.setImageResource(
                        spell.spheres[2].getDrawableResID(requireContext()))

                    spellDialogSphereCard4.apply{
                        visibility = View.VISIBLE
                        tooltipText = spell.spheres[3].getNameString(context)
                    }
                    spellDialogSphere4.setImageResource(
                        spell.spheres[3].getDrawableResID(requireContext()))


                    View.VISIBLE
                }
                else-> {
                    spellDialogSphereCard1.visibility = View.GONE
                    spellDialogSphereCard2.visibility = View.GONE
                    spellDialogSphereCard3.visibility = View.GONE
                    spellDialogSphereCard4.visibility = View.GONE
                    View.GONE
                }
            }

            spellDialogRestrictionsLabel.visibility = if (spell.restrictions.isNotEmpty()) {

                spellDialogRestrictionsValue.apply{
                    text = spell.restrictions
                        .joinToString(separator = "\n") { it.getFullName(requireContext()) }
                    visibility = View.VISIBLE
                }

                View.VISIBLE

            } else {
                spellDialogRestrictionsValue.visibility = View.GONE
                View.GONE
            }

            spellDialogNoteLabel.visibility = if (spell.note.isNotBlank()) {

                spellDialogNoteValue.apply{
                    visibility = View.VISIBLE
                    text = spell.note
                }

                View.VISIBLE
            } else {
                spellDialogNoteValue.visibility = View.GONE
                View.GONE
            }

            spellDialogFlagButton.text = if (entry.isUsed) {
                getString(R.string.unflag_as_used_button)
            } else {
                getString(R.string.flag_as_used_button)
            }
        }

        val spellDialogBuiler = AlertDialog.Builder(requireContext(),R.style.SpellCollectionSubStyle)
            .setView(dialogBinding.root)

        val spellDialog = spellDialogBuiler.create()

        // Set onClickListeners
        dialogBinding.apply{

            spellDialogClipboardButton.setOnClickListener{

                val textToCopy = spell.getClipboardText(requireContext())
                val clipboardManager = requireContext().getSystemService(ClipboardManager::class.java)
                val clipData = ClipData.newPlainText("text", textToCopy)

                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), "Spell information copied to clipboard", Toast.LENGTH_LONG).show()

                spellDialog.dismiss()
            }

            spellDialogFlagButton.setOnClickListener{
                Toast.makeText(requireContext(),"Flag button clicked.",Toast.LENGTH_LONG).show()
                //TODO implement
            }

            spellDialogChooseButton.setOnClickListener{
                Toast.makeText(requireContext(),"Choose button clicked.",Toast.LENGTH_LONG).show()
                //TODO implement
            }

            spellDialogCloseButton.setOnClickListener{
                spellDialog.dismiss()
            }
        }

        //if (viewedItem is ViewableSpellCollection)
    }

    // endregion
}