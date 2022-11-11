package com.treasurehacktory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Resources
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.DEFAULT_BOLD
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.treasurehacktory.HACKMASTER_CLASS_ITEM_TEXT
import com.treasurehacktory.R
import com.treasurehacktory.TreasureHacktoryApplication
import com.treasurehacktory.capitalized
import com.treasurehacktory.database.MagicItemTemplate
import com.treasurehacktory.databinding.*
import com.treasurehacktory.model.*
import com.treasurehacktory.viewmodel.UniqueDetailsViewModel
import com.treasurehacktory.viewmodel.UniqueDetailsViewModelFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class UniqueDetailsFragment : Fragment() {

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
    private lateinit var itemType: UniqueItemType
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
            itemType = it.itemType
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        @StyleRes
        val itemTheme : Int
        @ColorInt
        val newStatusBarColor : Int

        when(itemType){
            UniqueItemType.GEM -> {
                itemTheme = R.style.GemSubStyle
                newStatusBarColor = R.color.gemPrimaryDark
            }
            UniqueItemType.ART_OBJECT -> {
                itemTheme = R.style.ArtObjectSubStyle
                newStatusBarColor = R.color.artPrimaryDark
            }
            UniqueItemType.MAGIC_ITEM -> {
                itemTheme = R.style.MagicItemSubStyle
                newStatusBarColor = R.color.magicPrimaryDark
            }
            UniqueItemType.SPELL_COLLECTION -> {
                itemTheme = R.style.SpellCollectionSubStyle
                newStatusBarColor = R.color.spellPrimaryDark
            }
        }

        requireActivity().window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(newStatusBarColor,null)
        }

        val contextThemeWrapper = ContextThemeWrapper(context,itemTheme)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)

        _binding = LayoutUniqueDetailsBinding.inflate(localInflater,container,false)
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

                    if (binding.uniqueDetailsWaitingCard.waitingCard.visibility == View.GONE &&
                        !isWaitingCardAnimating) {

                        fadeInWaitingCard()

                    } else {

                        binding.uniqueDetailsWaitingCard.waitingCard.visibility = View.VISIBLE
                        isWaitingCardAnimating = false
                    }

                } else {

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

                            showSpellDialog(spell,entry)

                        } else {

                            Toast.makeText(context, "Could not find details for #${
                                entry.spellsPos}: \"${entry.name}\" (id: ${entry.spellID})",
                                Toast.LENGTH_LONG)
                        }
                    }

                    dialogSpellInfoLiveData.value = null
                }
            }

            dialogSpellsInfoLiveData.observe(viewLifecycleOwner) { spellsPair ->

                if (spellsPair != null){

                    spellsPair.let{ (spells, entry) ->

                        if (spells.isNotEmpty()) {

                            showChoiceSpellDialog(spells.map{
                                it.toSimpleSpellEntry(false, entry.spellsPos)}, entry)

                        } else {

                            Toast.makeText(context, "No spell list found for ${
                                entry.spellsPos}: \"${entry.name}\" (id: ${entry.spellID})",
                                Toast.LENGTH_LONG)
                        }
                    }

                    dialogSpellsInfoLiveData.value = null
                }
            }

            dialogItemTemplatesInfoLiveData.observe(viewLifecycleOwner) { templates ->

                if (templates != null){

                    showChoiceItemDialog(templates)

                    dialogItemTemplatesInfoLiveData.value = null
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
            val colorOnPrimary = typedValue.data

            inflateMenu(R.menu.unique_details_toolbar_menu)
            title = getString(R.string.item_details)
            setTitleTextColor(colorOnPrimary)
            setSubtitleTextColor(colorOnPrimary)
            navigationIcon?.apply {
                R.drawable.clipart_back_vector_icon
                setTint(colorOnPrimary)
            }
            overflowIcon?.apply{
                setTint(colorOnPrimary)
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    
                    R.id.action_copy_item_clipboard -> {

                        copyItemTextToClipboard()
                        
                        true
                    }

                    R.id.action_set_hoard_icon  -> {

                        uniqueDetailsViewModel.setItemAsHoardIcon(parentHoard.hoardID,viewedItem)

                        true
                    }

                    R.id.action_toggle_forgery  -> {

                        if (viewedItem is ViewableArtObject) {
                            uniqueDetailsViewModel
                                .toggleArtObjectAuthenticity(viewedItem as ViewableArtObject)
                        }

                        true
                    }

                    R.id.action_reroll_art_name  -> {

                        if (viewedItem is ViewableArtObject) {
                            uniqueDetailsViewModel
                                .rerollArtObjectName(viewedItem as ViewableArtObject)
                        }

                        true
                    }

                    R.id.action_rename_item     -> {

                        val renameBinding : DialogRenameItemBinding =
                            DialogRenameItemBinding.inflate(layoutInflater)

                        AlertDialog.Builder(requireContext())
                            .setTitle("Set item name/nickname")
                            .setView(renameBinding.root)
                            .setPositiveButton(R.string.save) { dialog, _ ->

                                val newName = renameBinding.renameDialogNameEdit.text.toString()
                                    .replace("¤","*").take(70)

                                if (newName.isNotBlank() && newName != viewedItem.name) {

                                    uniqueDetailsViewModel.renameItem(viewedItem, parentHoard,
                                        newName, false)

                                    Toast.makeText(context,"Item's name was changed to \"" +
                                            "$newName\".", Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(context,"Item's name was not changed.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                dialog.dismiss()
                            }
                            .setNeutralButton(context.getString(R.string.restore_original_value_label)) { dialog, _ ->
                                if (viewedItem.name != viewedItem.originalName){

                                    uniqueDetailsViewModel.renameItem(viewedItem, parentHoard,
                                        viewedItem.originalName, true)

                                    Toast.makeText(context,"Item's original name was restored.",
                                        Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(context,"Item's name was not changed.",
                                        Toast.LENGTH_SHORT).show()
                                }

                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                            .show()

                        true
                    }

                    R.id.action_reroll_item     -> {

                        AlertDialog.Builder(requireContext())
                            .setMessage("This will re-roll and overwrite \"${viewedItem.name}\"" +
                                    ", which cannot be undone. Are you sure you want to proceed?")
                            .setPositiveButton(R.string.action_reroll_item_condensed) { dialog, _ ->
                                uniqueDetailsViewModel.rerollItem(viewedItem)
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                            .show()

                        true
                    }

                    R.id.action_convert_to_choice -> {

                        if (viewedItem is ViewableMagicItem) {

                            AlertDialog.Builder(requireContext())
                                .setMessage("This will convert \"${viewedItem.name}\" into a " +
                                        "wildcard item, which cannot be undone. Are you sure you " +
                                        "want to proceed?")
                                .setPositiveButton(context.getString(R.string.action_convert_to_choice_condensed)) { dialog, _ ->
                                    uniqueDetailsViewModel.replaceItemAsGMChoice(
                                        viewedItem as ViewableMagicItem
                                    )
                                    dialog.dismiss()
                                }
                                .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                    dialog.cancel()
                                }
                                .show()
                        }

                        true
                    }


                    R.id.action_resolve_gm_choice -> {

                        if (viewedItem is ViewableMagicItem) {
                            uniqueDetailsViewModel.fetchItemTemplatesForDialog(
                                (viewedItem as ViewableMagicItem).mgcItemType)
                        }

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
                   (holder as PlainTextHolder).bind(
                       PlainTextEntry("Oops! There was an error " +
                           "loading this detail.")
                   )
               }
           }
        }

        override fun getItemCount(): Int = detailEntries.size

        override fun getItemViewType(position: Int): Int {
            return when (detailEntries[position]){
                is PlainTextEntry -> PLAIN_TEXT
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
                        (entry.message.startsWith("[ ") && entry.message.endsWith(" ]")) -> {
                            text = entry.message.removeSurrounding("[ "," ]")
                            setTypeface(DEFAULT_BOLD)
                        }
                        (entry.message == HACKMASTER_CLASS_ITEM_TEXT) -> {
                            setTypeface(DEFAULT_BOLD)
                            textSize = 18f
                        }
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
                        spellItemSubclassText.apply{
                            visibility = View.VISIBLE
                            text = entry.subclass
                        }

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
                                    typeface = DEFAULT_BOLD
                                }
                            }
                            else -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.defaultSecondary,
                                            context?.theme))
                                spellItemSubclassText.setTextColor(
                                        resources.getColor(R.color.defaultOnSecondary,
                                            context?.theme))
                            }
                        }

                    } else {
                        spellItemSubclassCard.visibility = View.GONE
                        spellItemSubclassText.visibility = View.GONE
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

    private inner class MagicItemChoiceAdapter(val itemTemplates: List<MagicItemTemplate>)
        : RecyclerView.Adapter<MagicItemChoiceAdapter.ItemTemplateHolder>() {

        var selectedPos = -1
            private set
        private var lastSelectedPos = -1
        var selectedID = -1
            private set

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTemplateHolder {
            val binding = UniqueListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return ItemTemplateHolder(binding)
        }

        override fun onBindViewHolder(holder: ItemTemplateHolder, position: Int) {
            val itemTemplate = itemTemplates[position]

            holder.bind(itemTemplate, position)
        }

        override fun getItemCount(): Int = itemTemplates.size

        inner class ItemTemplateHolder(val binding: UniqueListItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var template : MagicItemTemplate

            fun bind(newTemplate: MagicItemTemplate, position: Int) {

                template = newTemplate

                binding.apply {

                    fun String.asMagicItemType() : MagicItemType =
                        if (enumValues<MagicItemType>().map{it.name}.contains(this)) {
                            MagicItemType.valueOf(this)
                        } else {
                            MagicItemType.Mundane
                        }

                    fun MagicItemTemplate.getEndIconStr() : String {
                        return when (this.tableType.asMagicItemType()){
                            MagicItemType.A2 -> "clipart_potion_vector_icon"
                            MagicItemType.A3 -> "clipart_scroll_vector_icon"
                            MagicItemType.A4 -> "clipart_ring_vector_icon"
                            MagicItemType.A5 -> "clipart_rod_vector_icon"
                            MagicItemType.A6 -> "clipart_staff_vector_icon"
                            MagicItemType.A7 -> "clipart_wand_vector_icon"
                            MagicItemType.A8 -> "clipart_book_vector_icon"
                            MagicItemType.A9 -> "clipart_jewelry_vector_icon"
                            MagicItemType.A10 -> "clipart_robe_vector_icon"
                            MagicItemType.A11 -> "clipart_boot_vector_icon"
                            MagicItemType.A12 -> "clipart_belt_hat_vector_icon"
                            MagicItemType.A13 -> "clipart_bag_vector_icon"
                            MagicItemType.A14 -> "clipart_dust_vector_icon"
                            MagicItemType.A15 -> "clipart_toolbox_vector_icon"
                            MagicItemType.A16 -> "clipart_lyre_vector_icon"
                            MagicItemType.A17 -> "clipart_crystal_ball_vector_icon"
                            MagicItemType.A18 -> "clipart_armor_vector_icon"
                            MagicItemType.A20 -> "clipart_cape_armor_vector_icon"
                            MagicItemType.A21 -> "clipart_axe_sword_vector_icon"
                            MagicItemType.A23 -> "clipart_winged_sword_vector_icon"
                            MagicItemType.A24 -> "clipart_artifact_crown_vector_icon"
                            MagicItemType.Map -> "clipart_map_vector_icon"
                            MagicItemType.Mundane -> "clipart_crate_vector_icon"
                        }
                    }

                    fun MagicItemTemplate.getEndLabel() : String {
                        return when (this.tableType.asMagicItemType()){
                            MagicItemType.A2 -> "Potion A2"
                            MagicItemType.A3 -> "Scroll A3"
                            MagicItemType.A4 -> "Ring A4"
                            MagicItemType.A5 -> "Rod A5"
                            MagicItemType.A6 -> "Staff A6"
                            MagicItemType.A7 -> "Wand A7"
                            MagicItemType.A8 -> "Book A8"
                            MagicItemType.A9 -> "Jewelry A9"
                            MagicItemType.A10 -> "Robe, etc. A10"
                            MagicItemType.A11 -> "Boots,etc. A11"
                            MagicItemType.A12 -> "Hat, etc. A12"
                            MagicItemType.A13 -> "Container A13"
                            MagicItemType.A14 -> "Dust, etc. A14"
                            MagicItemType.A15 -> "Tools A15"
                            MagicItemType.A16 -> "Musical A16"
                            MagicItemType.A17 -> "Odd Stuff A17"
                            MagicItemType.A18 -> "Armor A18"
                            MagicItemType.A20 -> "Sp.Armor A20"
                            MagicItemType.A21 -> "Weapon A21"
                            MagicItemType.A23 -> "Sp.Weap. A23"
                            MagicItemType.A24 -> "Artifact A24"
                            MagicItemType.Map -> "Treasure Map"
                            MagicItemType.Mundane -> "Mundane"
                        }
                    }

                    layoutUniqueListItem.apply {

                        isSelected = (adapterPosition == selectedPos)

                        setOnClickListener {
                            if (position == selectedPos) {
                                // Deselect if selected
                                updateSelectedItem(-1, -1)
                            } else {
                                updateSelectedItem(position, template.refId)
                            }
                        }
                    }

                    when {
                        (template.isCursed == 1) -> {
                            uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground_cursed)
                            uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_cursed)
                        }
                        template.tableType == "A24" -> {
                            uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground_golden)
                            uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_golden)
                        }
                        else    -> {
                            uniqueListItemframeForeground.setImageResource(R.drawable.itemframe_foreground)
                            uniqueListItemframeBackground.setImageResource(R.drawable.itemframe_background_gray)
                        }
                    }

                    try{
                        uniqueListItemThumbnail.apply{
                            setImageResource(resources
                                .getIdentifier(template.iconRef,
                                    "drawable",view?.context?.packageName))
                            visibility = View.VISIBLE
                        }
                    } catch (e: Exception){
                        uniqueListItemThumbnail.apply{
                            setImageResource(R.drawable.container_chest)
                            visibility = View.VISIBLE
                        }
                    }

                    "[${template.refId}] ${template.name}".also { uniqueListItemName.text = it }

                    uniqueListItemName.apply{
                        maxLines = 2
                        textSize = 14f
                    }

                    ("${
                        DecimalFormat("#,##0.0#")
                            .format(template.gpValue)
                            .removeSuffix(".0")} gp")
                        .also {uniqueListItemGp.text = it}

                    uniqueListItemGp.minWidth = 48

                    try{
                        uniqueListItemTypeIcon.apply{
                            setImageResource(resources
                                .getIdentifier(template.getEndIconStr(),
                                    "drawable",view?.context?.packageName))
                            visibility = View.VISIBLE
                        }

                    } catch (e: Exception){
                        uniqueListItemTypeIcon.apply{
                            setImageResource(R.drawable.clipart_prohibited_vector_icon)
                            visibility = View.VISIBLE
                        }
                    }

                    uniqueListItemTypeLabel.apply{
                        text = template.getEndLabel()
                        visibility = View.INVISIBLE
                    }

                    // Set xp value
                    (NumberFormat.getNumberInstance().format(
                        template.xpValue) + " xp")
                        .also { uniqueListItemXp.text = it }

                    uniqueListItemXp.minWidth = 48

                    // Hide badge
                    uniqueListItemframeBadge.visibility = View.GONE
                }
            }
        }

        fun updateSelectedItem(selectionPos: Int, selectedTemplateID: Int){

            lastSelectedPos = selectedPos
            selectedPos = selectionPos
            selectedID = selectedTemplateID

            if (selectedPos in itemTemplates.indices) {
                notifyItemChanged(selectedPos)
            }

            if (lastSelectedPos in itemTemplates.indices) {
                notifyItemChanged(lastSelectedPos)
            }
        }
    }

    private inner class SpellChoiceAdapter(val spells: List<SimpleSpellEntry>)
        : RecyclerView.Adapter<SpellChoiceAdapter.SpellHolder>() {

        var selectedPos = -1
            private set
        private var lastSelectedPos = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellHolder {
            val binding = UniqueDetailsItemSpellBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return SpellHolder(binding)
        }

        override fun onBindViewHolder(holder: SpellHolder, position: Int) {
            val spell = spells[position]

            holder.bind(spell, position)
        }

        override fun getItemCount(): Int = spells.size

        inner class SpellHolder(val binding: UniqueDetailsItemSpellBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var spell : SimpleSpellEntry

            fun bind(newSpell: SimpleSpellEntry, position: Int) {

                spell = newSpell

                binding.apply{

                    spellItemLayout.apply{

                        isSelected = (position == selectedPos)

                        setOnClickListener {
                            if (position == selectedPos) {
                                // Deselect if selected
                                updateSelectedItem(-1)
                            } else {
                                updateSelectedItem(position)
                            }
                        }
                    }

                    "#${position + 1}".also { spellItemIndexLabel.text = it }

                    if (spell.subclass.isNotBlank()) {

                        spellItemSubclassCard.visibility = View.VISIBLE
                        spellItemSubclassText.apply {
                            visibility = View.VISIBLE
                            text = spell.subclass
                        }

                        when (spell.subclass) {
                            "Vengeance" -> {
                                spellItemSubclassCard.setCardBackgroundColor(
                                    resources.getColor(R.color.sanguine,
                                        context?.theme))
                                spellItemSubclassText.apply{
                                    setTextColor(resources.getColor(R.color.white, context?.theme))
                                    typeface = DEFAULT
                                }
                            }
                            "Woeful" -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.rust,
                                            context?.theme))
                                spellItemSubclassText.apply{
                                    setTextColor(resources.getColor(R.color.white, context?.theme) )
                                    typeface = DEFAULT
                                }
                            }
                            "Choice" -> {

                                if (spell.name.startsWith("GM")){

                                    spellItemSubclassCard
                                        .setCardBackgroundColor(
                                            resources.getColor(R.color.ultramarine,
                                                context?.theme))
                                    spellItemSubclassText.apply {
                                        setTextColor(resources.getColor(R.color.white, context?.theme))
                                        typeface = DEFAULT
                                    }

                                } else {

                                    spellItemSubclassCard
                                        .setCardBackgroundColor(
                                            resources.getColor(R.color.teal,
                                                context?.theme))
                                    spellItemSubclassText.apply{
                                        setTextColor(resources.getColor(R.color.white,
                                            context?.theme))
                                        typeface = DEFAULT
                                    }
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
                                    typeface = DEFAULT_BOLD
                                }
                            }
                            else -> {
                                spellItemSubclassCard
                                    .setCardBackgroundColor(
                                        resources.getColor(R.color.defaultSecondary,
                                            context?.theme))
                                spellItemSubclassText.apply{
                                    setTextColor(resources.getColor(R.color.defaultOnSecondary,
                                        context?.theme))
                                    typeface = DEFAULT
                                }
                            }
                        }

                    } else {

                        spellItemSubclassCard.visibility = View.GONE
                        spellItemSubclassText.visibility = View.GONE
                    }

                    spellItemTypeBackdrop.apply{
                        setImageDrawable(
                            getDrawable(resources,
                                if(spell.isUsed){

                                    when (spell.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient_used
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient_used
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient_used
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }

                                } else {
                                    when (spell.discipline) {
                                        SpCoDiscipline.ARCANE -> R.drawable.spell_item_arcane_backdrop_gradient
                                        SpCoDiscipline.DIVINE -> R.drawable.spell_item_divine_backdrop_gradient
                                        SpCoDiscipline.NATURAL -> R.drawable.spell_item_natural_backdrop_gradient
                                        SpCoDiscipline.ALL_MAGIC -> R.drawable.badge_hoard_magic
                                    }
                                }, context?.theme)
                        )
                        if (spell.discipline == SpCoDiscipline.ALL_MAGIC) {

                            alpha = if (spell.isUsed) { 0.25f } else { 0.5f }
                        }
                    }

                    spellItemName.text = spell.name

                    spellItemLevel.text = if (spell.level == 0) {
                        "Cantrip"
                    } else { "Level ${spell.level}" }

                    when (spell.schools.size) {

                        0   -> {
                            spellItemSchoolCard1.visibility = View.GONE
                            spellItemSchoolCard2.visibility = View.GONE
                            spellItemSchoolCard3.visibility = View.GONE
                        }

                        1   -> {

                            spellItemSchoolCard1.apply {
                                visibility = View.VISIBLE
                                tooltipText = when (spell.schools[0]) {
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
                                when (spell.schools[0]) {
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
                                tooltipText = when (spell.schools[0]) {
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
                                when (spell.schools[0]) {
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
                                tooltipText = when (spell.schools[1]) {
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
                                when (spell.schools[1]) {
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
                                tooltipText = when (spell.schools[0]) {
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
                                when (spell.schools[0]) {
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
                                tooltipText = when (spell.schools[1]) {
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
                                when (spell.schools[1]) {
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
                                tooltipText = when (spell.schools[2]) {
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
                                when (spell.schools[2]) {
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

                    spellItemSource.text = spell.sourceString
                }
            }
        }

        fun updateSelectedItem(selectionPos: Int){

            lastSelectedPos = selectedPos
            selectedPos = selectionPos

            if (selectedPos in spells.indices) {
                notifyItemChanged(selectedPos)
            }
            if (lastSelectedPos in spells.indices) {
                notifyItemChanged(lastSelectedPos)
            }
        }

    }

    // endregion

    // region [ Helper functions ]

    @SuppressLint("SimpleDateFormat")
    private fun updateUI() {
        binding.apply{

            // Toolbar
            uniqueDetailsToolbar.apply {
                menu.apply {

                    if (viewedItem is ViewableArtObject) {

                        findItem(R.id.action_toggle_forgery).apply {
                            isVisible = true
                            isEnabled = true
                        }
                        findItem(R.id.action_reroll_art_name).apply {
                            isVisible = true
                            isEnabled = true
                        }
                    }
                    if (viewedItem is ViewableMagicItem) {

                        if (viewedItem.name.endsWith(" Choice")
                        ) {

                            findItem(R.id.action_resolve_gm_choice).apply {
                                isVisible = true
                                isEnabled = true
                            }
                            findItem(R.id.action_convert_to_choice).apply {
                                isVisible = false
                                isEnabled = false
                            }
                        } else {

                            findItem(R.id.action_resolve_gm_choice).apply {
                                isVisible = false
                                isEnabled = false
                            }
                            findItem(R.id.action_convert_to_choice).apply {
                                isVisible = true
                                isEnabled = true
                            }
                        }
                    }
                }
                subtitle = parentHoard.name
            }

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

            uniqueDetailsNameLabel.text = viewedItem.name

            uniqueDetailsSubtitle.text = viewedItem.subtitle

            // Static details
            uniqueDetailsFullNameValue.text = viewedItem.name

            if (viewedItem.name != viewedItem.originalName) {
                uniqueDetailsOriginalNameLabel.visibility = View.VISIBLE
                uniqueDetailsOriginalNameValue.apply{
                    visibility = View.VISIBLE
                    text = viewedItem.originalName
                }

                @ColorInt
                val nicknameColor = when (viewedItem){
                    is ViewableGem -> getColor(resources,R.color.gemSecondaryDark,null)
                    is ViewableArtObject -> getColor(resources,R.color.artSecondaryDark,null)
                    is ViewableMagicItem -> getColor(resources,R.color.magicSecondaryDark,null)
                    is ViewableSpellCollection -> getColor(resources,R.color.spellSecondaryDark,null)
                }

                uniqueDetailsNameLabel.apply{
                    text = viewedItem.name.removeSuffix("*")
                    setTextColor(nicknameColor)
                }
            } else {
                uniqueDetailsOriginalNameLabel.visibility = View.GONE
                uniqueDetailsOriginalNameValue.visibility = View.GONE

                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorOnSurface,typedValue,true)
                @ColorInt
                val nameColor = typedValue.data

                uniqueDetailsNameLabel.setTextColor(nameColor)
            }

            "# ${ viewedItem.itemID }".also { uniqueDetailsIdValue.text = it }

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
                            tooltipText = null
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
                            tooltipText = null
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
                            tooltipText = null
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
                            tooltipText = null
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
                            tooltipText = null
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
                                tooltipText = null
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsDruid.apply{

                                alpha = 0.25f
                                setImageResource(R.drawable.class_druid_outline)
                                tooltipText = null
                                visibility = View.VISIBLE
                            }
                        }
                        SpCoDiscipline.DIVINE -> {
                            uniqueDetailsUsableLabel.visibility = View.VISIBLE

                            uniqueDetailsMagicUser.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_magic_user_outline)
                                tooltipText = null
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
                                tooltipText = null
                                visibility = View.VISIBLE
                            }
                        }
                        SpCoDiscipline.NATURAL -> {
                            uniqueDetailsUsableLabel.visibility = View.VISIBLE

                            uniqueDetailsMagicUser.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_magic_user_outline)
                                tooltipText = null
                                visibility = View.VISIBLE
                            }
                            uniqueDetailsCleric.apply{
                                alpha = 0.25f
                                setImageResource(R.drawable.class_cleric_outline)
                                tooltipText = null
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

            // Potion color preview group
            if (viewedItem is ViewableMagicItem) {

                when ((viewedItem as ViewableMagicItem).mgcPotionColors.size) {
                    0   -> {
                        uniqueDetailsColorLabel.visibility = View.GONE

                        uniqueDetailsColorPreview1.visibility = View.GONE
                        uniqueDetailsColorOutline1.visibility = View.GONE

                        uniqueDetailsColorPreview2.visibility = View.GONE
                        uniqueDetailsColorOutline2.visibility = View.GONE

                        uniqueDetailsColorPreview3.visibility = View.GONE
                        uniqueDetailsColorOutline3.visibility = View.GONE

                        uniqueDetailsColorPreview4.visibility = View.GONE
                        uniqueDetailsColorOutline4.visibility = View.GONE
                    }

                    1   -> {
                        uniqueDetailsColorLabel.visibility = View.VISIBLE

                        uniqueDetailsColorPreview1.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[0].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[0].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline1.visibility = View.VISIBLE

                        uniqueDetailsColorPreview2.visibility = View.GONE
                        uniqueDetailsColorOutline2.visibility = View.GONE

                        uniqueDetailsColorPreview3.visibility = View.GONE
                        uniqueDetailsColorOutline3.visibility = View.GONE

                        uniqueDetailsColorPreview4.visibility = View.GONE
                        uniqueDetailsColorOutline4.visibility = View.GONE
                    }
                    2   -> {
                        uniqueDetailsColorLabel.visibility = View.VISIBLE

                        uniqueDetailsColorPreview1.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[0].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[0].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline1.visibility = View.VISIBLE

                        uniqueDetailsColorPreview2.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[1].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[1].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline2.visibility = View.VISIBLE

                        uniqueDetailsColorPreview3.visibility = View.GONE
                        uniqueDetailsColorOutline3.visibility = View.GONE

                        uniqueDetailsColorPreview4.visibility = View.GONE
                        uniqueDetailsColorOutline4.visibility = View.GONE
                    }

                    3   -> {
                        uniqueDetailsColorLabel.visibility = View.VISIBLE

                        uniqueDetailsColorPreview1.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[0].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[0].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline1.visibility = View.VISIBLE

                        uniqueDetailsColorPreview2.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[1].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[1].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline2.visibility = View.VISIBLE

                        uniqueDetailsColorPreview3.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[2].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[2].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline3.visibility = View.VISIBLE

                        uniqueDetailsColorPreview4.visibility = View.GONE
                        uniqueDetailsColorOutline4.visibility = View.GONE
                    }

                    else-> {
                        uniqueDetailsColorLabel.visibility = View.VISIBLE

                        uniqueDetailsColorPreview1.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[0].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[0].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline1.visibility = View.VISIBLE

                        uniqueDetailsColorPreview2.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[1].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[1].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline2.visibility = View.VISIBLE

                        uniqueDetailsColorPreview3.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[2].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[2].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline3.visibility = View.VISIBLE

                        uniqueDetailsColorPreview4.apply{
                            visibility = View.VISIBLE
                            tooltipText = (viewedItem as ViewableMagicItem).mgcPotionColors[3].capitalized()
                            try {
                                @ColorInt
                                val newColor = resources.getColor(resources.getIdentifier(
                                    (viewedItem as ViewableMagicItem).mgcPotionColors[3].replace(" ","_"),
                                    "color",view?.context?.packageName),null)

                                setColorFilter(newColor)
                            } catch (e: Resources.NotFoundException){
                                e.printStackTrace()
                            }
                        }
                        uniqueDetailsColorOutline4.visibility = View.VISIBLE
                    }
                }

            } else {

                uniqueDetailsColorLabel.visibility = View.GONE

                uniqueDetailsColorPreview1.visibility = View.GONE
                uniqueDetailsColorOutline1.visibility = View.GONE

                uniqueDetailsColorPreview2.visibility = View.GONE
                uniqueDetailsColorOutline2.visibility = View.GONE

                uniqueDetailsColorPreview3.visibility = View.GONE
                uniqueDetailsColorOutline3.visibility = View.GONE

                uniqueDetailsColorPreview4.visibility = View.GONE
                uniqueDetailsColorOutline4.visibility = View.GONE
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

        val isChoiceSlot : Boolean

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
                        tooltipText = spell.schools[0].getLongName(context)
                    }
                    spellDialogSchool1.setImageResource(
                        spell.schools[0].getDrawableResID(requireContext()))

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
                        tooltipText = spell.schools[0].getLongName(context)
                    }
                    spellDialogSchool1.setImageResource(
                        spell.schools[0].getDrawableResID(requireContext()))

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

            spellDialogChooseButton.text = if (spell.name.contains(" Choice ")) {
                isChoiceSlot = true
                getString(R.string.button_resolve_spell_choice)
            } else {
                isChoiceSlot = false
                getString(R.string.choose_spell_button)
            }

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

                val toggledEntry = SimpleSpellEntry(
                    entry.spellID,
                    entry.name,
                    entry.level,
                    entry.discipline,
                    entry.schools,
                    entry.subclass,
                    entry.sourceString,
                    entry.isUsed.not(),
                    entry.spellsPos
                )

                uniqueDetailsViewModel.replaceSpellFromDialog(toggledEntry,
                    (viewedItem as ViewableSpellCollection).toSpellCollection())

                spellDialog.dismiss()
            }

            spellDialogChooseButton.setOnClickListener{
                uniqueDetailsViewModel.fetchSpellsForDialog(entry,isChoiceSlot)
                spellDialog.dismiss()
            }

            spellDialogCloseButton.setOnClickListener{
                spellDialog.dismiss()
            }
        }

        spellDialog.show()
    }

    private fun showChoiceSpellDialog(spellList: List<SimpleSpellEntry>, entry: SimpleSpellEntry) {

        val dialogRecycler = RecyclerView(requireContext())

        dialogRecycler.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = SpellChoiceAdapter(spellList)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            isNestedScrollingEnabled = true
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogRecycler)
            .setTitle(getString(R.string.gm_choice_spell_dialog_title))
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create().apply {
                setOnShowListener { dialog ->

                    getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {
                            if ((dialogRecycler.adapter as SpellChoiceAdapter)
                                    .selectedPos in spellList.indices) {

                                val spellEntry = spellList[
                                        (dialogRecycler.adapter as SpellChoiceAdapter).selectedPos]

                                if (viewedItem is ViewableSpellCollection &&
                                    spellEntry.spellID > 0){
                                    uniqueDetailsViewModel.replaceSpellFromDialog(spellEntry,
                                        (viewedItem as ViewableSpellCollection).toSpellCollection())
                                }

                                dialog.dismiss()
                            }}

                    getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener { dialog.cancel() }
                }
            }

        dialog.show()
    }

    private fun showChoiceItemDialog(itemList: List<MagicItemTemplate>) {

        val dialogRecycler = RecyclerView(requireContext())

        dialogRecycler.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = MagicItemChoiceAdapter(itemList)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            isNestedScrollingEnabled = true
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogRecycler)
            .setTitle(getString(R.string.gm_choice_item_dialog_title))
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create().apply {
                setOnShowListener { dialog ->

                    getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {
                            if ((dialogRecycler.adapter as MagicItemChoiceAdapter)
                                    .selectedPos in itemList.indices) {

                                val templateID = (dialogRecycler.adapter as MagicItemChoiceAdapter)
                                    .selectedID

                                if (viewedItem is ViewableMagicItem){

                                    if (templateID > 0) {
                                        uniqueDetailsViewModel.replaceItemFromTemplateID(
                                            templateID,
                                            (viewedItem as ViewableMagicItem)
                                        )
                                    } else {
                                        // Convert between special and standard tables
                                        uniqueDetailsViewModel.replaceItemAsGMChoice(

                                            (viewedItem as ViewableMagicItem).toMagicItem()
                                                .copy(
                                                    name= viewedItem.name + if (
                                                        (viewedItem as ViewableMagicItem)
                                                            .mgcItemType.ordinal in 16..19
                                                    ) "(true table: ${
                                                        (viewedItem as ViewableMagicItem)
                                                            .mgcItemType.name
                                                    })" else "",
                                                    typeOfItem = when(
                                                        (viewedItem as ViewableMagicItem)
                                                            .mgcItemType){

                                                        MagicItemType.A18 -> MagicItemType.A20
                                                        MagicItemType.A20 -> MagicItemType.A18
                                                        MagicItemType.A21 -> MagicItemType.A23
                                                        MagicItemType.A23 -> MagicItemType.A21
                                                        else -> {
                                                            (viewedItem as ViewableMagicItem)
                                                                .mgcItemType
                                                        }
                                                    }
                                                ).toViewableMagicItem()
                                        )
                                    }
                                }

                                dialog.dismiss()
                        }}

                    getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener { dialog.cancel() }
                }
            }

        dialog.show()
    }
    
    private fun copyItemTextToClipboard() {

        fun getViewedItemAsClipboardText(): String {
            
                val result = StringBuilder()

                (viewedItem.name + " [id:${viewedItem.itemID}]").let {
                    result.append(it)
                    result.append("\n")
                    result.append("-".repeat(it.length))
                    result.append("\n")
                }

                result.append(viewedItem.subtitle.capitalized() + "\n")

                result.append("Source: ${viewedItem.source}, pg ${viewedItem.sourcePage}\n")

                result.append("Worth ${
                    DecimalFormat("#,##0.0#")
                        .format(viewedItem.gpValue)
                        .removeSuffix(".0")} gp and " +
                        NumberFormat.getNumberInstance()
                            .format(viewedItem.xpValue) + " xp (when reported)\n"
                )

                if (viewedItem.originalName != viewedItem.name){
                    result.append("Originally: " + viewedItem.originalName + "\n")
                }

                result.append("Parent Hoard: " + parentHoard.name + "[id:" +
                        parentHoard.hoardID + "]")
                result.toString()

                return result.toString()
        }
        
        val textToCopy = getViewedItemAsClipboardText()
        val clipboardManager = requireContext().getSystemService(ClipboardManager::class.java)
        val clipData = ClipData.newPlainText("text", textToCopy)

        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Item information copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    // endregion
}