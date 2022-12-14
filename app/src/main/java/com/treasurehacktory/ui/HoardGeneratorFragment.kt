package com.treasurehacktory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.animation.addListener
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.treasurehacktory.R
import com.treasurehacktory.TreasureHacktoryApplication
import com.treasurehacktory.database.LetterCode
import com.treasurehacktory.databinding.DialogGenerationOptionsBinding
import com.treasurehacktory.databinding.LayoutGeneratorFragmentBinding
import com.treasurehacktory.databinding.LetterRecyclerItemBinding
import com.treasurehacktory.model.*
import com.treasurehacktory.viewmodel.HoardGeneratorViewModel
import com.treasurehacktory.viewmodel.HoardGeneratorViewModelFactory
import com.treasurehacktory.viewmodel.MAXIMUM_LETTER_QTY
import com.treasurehacktory.viewmodel.MINIMUM_LETTER_QTY
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class HoardGeneratorFragment : Fragment() {

    // region [ Property declarations ]
    private var appVersion = 0

    private var shortAnimationDuration: Int = 0

    private var isButtonGroupAnimating = false

    private var _binding: LayoutGeneratorFragmentBinding? = null
    private val binding get() = _binding!!

    private val generatorViewModel: HoardGeneratorViewModel by viewModels {
        HoardGeneratorViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    // Adapters for letter code RecyclerViews
    private var lairAdapter: LetterAdapter = LetterAdapter(true)
    private var smallAdapter: LetterAdapter = LetterAdapter(false)

    private val spLvlRangeShortLabels: Array<String> by lazy {
        resources.getStringArray(R.array.spell_level_labels_condensed) }
    private val spLvlRangeLongLabels: Array<String> by lazy {
        resources.getStringArray(R.array.spell_level_labels) }

    // region [ Callbacks ]

    private val backCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {

            if (generatorViewModel.isRunningAsyncLiveData.value != true) {

                findNavController().popBackStack()

            } else {

                Toast.makeText(context,"Cannot navigate back; still generating treasure.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    // endregion

    // region [ Overridden functions ]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = LayoutGeneratorFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // region [ Prepare Letter Code views ]
        binding.generatorLairRecyclerview.apply{
            // Set up By-Letter recyclerview
            layoutManager = LinearLayoutManager(context)
            adapter = lairAdapter
            setHasFixedSize(true)
            visibility = View.GONE //Start off collapsed
        }
        binding.generatorSmallRecyclerview.apply{
            // Set up By-Letter recyclerview
            layoutManager = LinearLayoutManager(context)
            adapter = smallAdapter
            setHasFixedSize(true)
            visibility = View.GONE //Start off collapsed
        }
        // endregion

        // region [ Prepare button views ]

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        // Immediately hide buttons in Async task is indicated to be running
        if (generatorViewModel.isRunningAsyncLiveData.value == true) {

            binding.apply{
                generatorBottomButtonGroup.visibility = View.GONE
                generatorResetButton.isEnabled = false
                generatorGenerateButton.isEnabled = false
                generatorBottomWaitingGroup.visibility = View.VISIBLE
            }
        } else {

            binding.apply{
                generatorBottomButtonGroup.visibility = View.VISIBLE
                generatorResetButton.isEnabled = true
                generatorGenerateButton.isEnabled = true
                generatorBottomWaitingGroup.visibility = View.GONE
            }
        }
        // endregion

        // Return inflated view
        return view
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        generatorViewModel.apply {

            lairListLiveData.observe(viewLifecycleOwner) { newLairList ->
                lairAdapter.submitList(newLairList)
            }

            smallListLiveData.observe(viewLifecycleOwner) { newSmallList ->
                smallAdapter.submitList(newSmallList)
            }

            isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

                if (isRunningAsync) {

                    binding.generatorResetButton.isEnabled = false
                    binding.generatorGenerateButton.isEnabled = false

                    if (binding.generatorBottomButtonGroup.visibility == View.VISIBLE &&
                        !isButtonGroupAnimating) {

                        hideButtonsCrossfade()

                    } else {

                        // Set properties without animation
                        binding.generatorBottomButtonGroup.visibility = View.GONE
                        binding.generatorBottomWaitingGroup.visibility = View.VISIBLE
                        isButtonGroupAnimating = false
                    }

                } else {

                    binding.generatorResetButton.isEnabled = true
                    binding.generatorGenerateButton.isEnabled = true

                    if (binding.generatorBottomWaitingGroup.visibility == View.VISIBLE &&
                            !isButtonGroupAnimating) {

                        showButtonsCrossfade()

                    } else {

                        // Set properties without animation
                        binding.generatorBottomButtonGroup.visibility = View.VISIBLE
                        binding.generatorBottomWaitingGroup.visibility = View.GONE
                        isButtonGroupAnimating = false
                    }
                }
            }

            generatedHoardLiveData.observe(viewLifecycleOwner) { newHoard ->

                if (newHoard != null) {

                    // Show the dialog
                    val dialogView = layoutInflater
                        .inflate(R.layout.dialog_new_hoard_generated,null).apply {

                            findViewById<TextView>(R.id.hoard_generated_text)
                                .apply{
                                    val fullMessage = getString(R.string.new_hoard_generated_msg_template)
                                        .replace("=",newHoard.name)
                                    text = fullMessage
                                }
                            findViewById<ImageView>(R.id.hoard_list_item_list_icon)
                                .apply{
                                    try {
                                        setImageResource(resources.getIdentifier(
                                            newHoard.iconID,"drawable",
                                            view.context?.packageName))
                                    } catch (e: Exception) {
                                        setImageResource(R.drawable.clipart_default_image)
                                    }
                                }
                            findViewById<ImageView>(R.id.hoard_list_item_list_badge)
                                .apply {
                                    visibility = if (newHoard.badge != HoardBadge.NONE) {
                                        try{
                                            setImageResource(resources
                                                .getIdentifier(newHoard.badge.resString,
                                                    "drawable", view.context?.packageName))
                                            View.VISIBLE
                                        } catch (e: Exception){
                                            setImageResource(R.drawable.badge_hoard_broken)
                                            View.VISIBLE
                                        }
                                    } else {
                                        View.INVISIBLE
                                    }
                                }
                            findViewById<TextView>(R.id.hoard_list_item_name)
                                .apply {
                                    text = newHoard.name
                                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        R.drawable.clipart_new_vector_icon,0,0,0)
                                }
                            findViewById<TextView>(R.id.hoard_list_item_date)
                                .apply {
                                    text = SimpleDateFormat("MM/dd/yyyy")
                                        .format(newHoard.creationDate)
                                }
                            findViewById<TextView>(R.id.hoard_list_item_gp_value)
                                .apply{
                                    ("Worth ${
                                        DecimalFormat("#,##0.0#")
                                        .format(newHoard.gpTotal)
                                        .removeSuffix(".0")} gp").also { this.text = it }
                                }
                            findViewById<TextView>(R.id.hoard_list_item_gem_count)
                                .apply{
                                    text = String.format("%03d",newHoard.gemCount)
                                }
                            findViewById<TextView>(R.id.hoard_list_item_art_count)
                                .apply{
                                    text = String.format("%03d",newHoard.artCount)
                                }
                            findViewById<TextView>(R.id.hoard_list_item_magic_count)
                                .apply{
                                    text = String.format("%03d",newHoard.magicCount)
                                }
                            findViewById<TextView>(R.id.hoard_list_item_spell_count)
                                .apply{
                                    text = String.format("%03d",newHoard.spellsCount)
                                }
                            findViewById<ImageView>(R.id.hoard_list_item_favorited)
                                .apply{
                                    visibility = View.GONE
                                }

                        }

                    AlertDialog.Builder(context).setView(dialogView)
                        .setPositiveButton(R.string.action_view_new_hoard) { _, _ ->

                                val action =
                                    HoardGeneratorFragmentDirections.hoardGeneratorToOverviewAction(
                                        newHoard.hoardID
                                    )

                                findNavController().navigate(action)

                            }
                        .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                        .show()

                    // Clear the livedata
                    this.generatedHoardLiveData.value = null
                }
            }

            letterCodeHolderLiveData.observe(viewLifecycleOwner) { pendingLetterCode ->

                if (pendingLetterCode != null) {
                    letterCodeHolderLiveData.value = null
                    showLetterCodeOddsDialog(pendingLetterCode)
                }
            }
        }

        binding.hoardGeneratorToolbar.apply {

            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val colorOnPrimary = typedValue.data

            inflateMenu(R.menu.generator_options_toolbar_menu)
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

                if (generatorViewModel.isRunningAsyncLiveData.value != true) {

                    findNavController().popBackStack()

                } else {

                    Toast.makeText(context,"Cannot navigate back; still generating treasure.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            title = getString(R.string.hoard_generator_fragment_title)
            setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.action_generator_options   -> {

                        val gemSliderStringValues  = resources.getStringArray(R.array.range_slider_gem_label)
                        val artSliderStringValues = resources.getStringArray(R.array.range_slider_art_label)

                        val options = generatorViewModel.generatorOptions

                        val checkedItemTypes = options.allowedMagic.toMutableSet()

                        val dialogBinding: DialogGenerationOptionsBinding =
                            DialogGenerationOptionsBinding.inflate(layoutInflater)

                        fun setMagicErrorVisibility() {
                            dialogBinding.generatorOptionMagicError.visibility =
                                if (checkedItemTypes.isEmpty()) View.VISIBLE
                                else View.GONE
                        }

                        // Bind views
                        dialogBinding.apply{

                            generatorOptionGemSlider.apply {
                                stepSize = 1.0f
                                valueFrom = 0.0f
                                valueTo = 17.0f
                                values = listOf(options.gemMin.toFloat(),options.gemMax.toFloat())
                                stepSize = 1.0f
                                setLabelFormatter { value : Float ->
                                    val intValue = value.toInt()
                                    gemSliderStringValues.getOrNull(intValue) ?: "???"
                                }
                                addOnChangeListener { slider, _, _ ->
                                    val newValues = slider.values.map { it.toInt() }
                                    dialogBinding.generatorOptionGemMinValue.text =
                                        gemSliderStringValues.getOrNull(newValues[0]) ?: "???"
                                    dialogBinding.generatorOptionGemMaxValue.text =
                                        gemSliderStringValues.getOrNull(newValues[1]) ?: "???"
                                }

                                generatorOptionGemMinValue.text =
                                    gemSliderStringValues.getOrNull(values.first().toInt()) ?: "??? gp"
                                generatorOptionGemMaxValue.text =
                                    gemSliderStringValues.getOrNull(values.last().toInt()) ?: "??? gp"
                            }

                            generatorOptionArtSlider.apply {
                                stepSize = 1.0f
                                valueFrom = 0.0f
                                valueTo = 50.0f
                                values = listOf(options.artMin.toFloat() + 19f,options.artMax.toFloat() + 19f)
                                stepSize = 1.0f
                                setLabelFormatter { value : Float ->
                                    val intValue = value.toInt()
                                    artSliderStringValues.getOrNull(intValue) ?: "???"
                                }
                                addOnChangeListener { slider, _, _ ->
                                    val newValues = slider.values.map { it.toInt() }
                                    dialogBinding.generatorOptionArtMinValue.text =
                                        artSliderStringValues.getOrNull(newValues[0]) ?: "???"
                                    dialogBinding.generatorOptionArtMaxValue.text =
                                        artSliderStringValues.getOrNull(newValues[1]) ?: "???"
                                }

                                dialogBinding.generatorOptionArtMinValue.text =
                                    artSliderStringValues.getOrNull(values.first().toInt()) ?: "???"
                                dialogBinding.generatorOptionArtMaxValue.text =
                                    artSliderStringValues.getOrNull(values.last().toInt()) ?: "???"
                            }

                            generatorOptionMapRawEdit.apply{
                                setText(options.mapBase.toString())
                                addTextChangedListener { input ->

                                    val parsedValue = input.toString().toIntOrNull()

                                    generatorOptionMapRawEdit.error =
                                        when {
                                            parsedValue == null -> "No value"
                                            parsedValue < 0     -> "Too low"
                                            parsedValue > 100   -> "Too high"
                                            else                -> null
                                        }
                                }
                            }
                            generatorOptionMapPaperEdit.apply{
                                setText(options.mapPaper.toString())
                                addTextChangedListener { input ->

                                    val parsedValue = input.toString().toIntOrNull()

                                    generatorOptionMapPaperEdit.error =
                                        when {
                                            parsedValue == null -> "No value"
                                            parsedValue < 0     -> "Too low"
                                            parsedValue > 100   -> "Too high"
                                            else                -> null
                                        }
                                }
                            }
                            generatorOptionMapScrollEdit.apply{
                                setText(options.mapScroll.toString())
                                addTextChangedListener { input ->

                                    val parsedValue = input.toString().toIntOrNull()

                                    generatorOptionMapScrollEdit.error =
                                        when {
                                            parsedValue == null -> "No value"
                                            parsedValue < 0     -> "Too low"
                                            parsedValue > 100   -> "Too high"
                                            else                -> null
                                        }

                                    generatorOptionAllowedScrollError.visibility =
                                        if (generatorOptionSpellScrollCheckbox.isChecked ||
                                            generatorOptionUtilityScrollCheckbox.isChecked ||
                                            ((generatorOptionMapScrollEdit.text.toString()
                                                .toIntOrNull() ?: 0) > 0)
                                        ) {
                                            View.GONE
                                        } else View.VISIBLE
                                }
                            }
                            generatorOptionMapFalseSwitch.isChecked = options.falseMapsOK
                            generatorOptionAllowedScrollError.visibility = View.GONE
                            generatorOptionSpellScrollCheckbox.apply{
                                isChecked = options.spellOk
                                setOnCheckedChangeListener { _, _ ->
                                    generatorOptionAllowedScrollError.visibility =
                                        if (generatorOptionSpellScrollCheckbox.isChecked ||
                                            generatorOptionUtilityScrollCheckbox.isChecked ||
                                            ((generatorOptionMapScrollEdit.text.toString()
                                                .toIntOrNull() ?: 0) > 0)
                                        ) {
                                            View.GONE
                                        } else View.VISIBLE
                                }
                            }
                            generatorOptionUtilityScrollCheckbox.apply{
                                isChecked = options.spellOk
                                setOnCheckedChangeListener { _, _ ->
                                    generatorOptionAllowedScrollError.visibility =
                                        if (generatorOptionSpellScrollCheckbox.isChecked ||
                                            generatorOptionUtilityScrollCheckbox.isChecked ||
                                            ((generatorOptionMapScrollEdit.text.toString()
                                                .toIntOrNull() ?: 0) > 0)
                                        ) {
                                            View.GONE
                                        } else View.VISIBLE
                                }
                            }
                            generatorOptionMagicError.visibility = View.GONE
                            generatorOptionPotionCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A2)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A2)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A2)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionScrollCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A3)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A3)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A3)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionRingCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A4)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A4)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A4)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionRodCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A5)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A5)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A5)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionStaffCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A6)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A6)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A6)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionWandCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A7)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A7)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A7)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionBookCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A8)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A8)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A8)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionJewelryCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A9)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A9)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A9)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionRobeCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A10)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A10)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A10)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionBootCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A11)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A11)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A11)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionBeltCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A12)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A12)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A12)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionContainerCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A13)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A13)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A13)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionDustCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A14)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A14)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A14)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionHouseholdCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A15)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A15)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A15)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionMusicCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A16)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A16)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A16)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionWeirdCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A17)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A17)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A17)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionArmorCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A18) &&
                                        checkedItemTypes.contains(MagicItemType.A20)

                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A18)
                                        checkedItemTypes.add(MagicItemType.A20)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A18)
                                        checkedItemTypes.remove(MagicItemType.A20)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionWeaponCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A21) &&
                                        checkedItemTypes.contains(MagicItemType.A23)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A21)
                                        checkedItemTypes.add(MagicItemType.A23)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A21)
                                        checkedItemTypes.remove(MagicItemType.A23)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionArtifactCheckbox.apply {
                                isChecked = checkedItemTypes.contains(MagicItemType.A24)
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        checkedItemTypes.add(MagicItemType.A24)
                                    } else {
                                        checkedItemTypes.remove(MagicItemType.A24)
                                    }
                                    setMagicErrorVisibility()
                                }
                            }
                            generatorOptionCursedSwitch.isChecked = options.cursedOk
                            generatorOptionIntelligentSwitch.isChecked = options.intelOk
                            when (options.spellDisciplinePos) {
                                0   -> {
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = true
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                                1   -> {
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = true
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                                2   -> {
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = true
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                                else-> {
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = true
                                }
                            }
                            generatorOptionSpellDisciplineArcaneRadio.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                            }
                            generatorOptionSpellDisciplineDivineRadio.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                            }
                            generatorOptionSpellDisciplineNaturalRadio.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineAllRadio.isChecked = false
                                }
                            }
                            generatorOptionSpellDisciplineAllRadio.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    generatorOptionSpellDisciplineArcaneRadio.isChecked = false
                                    generatorOptionSpellDisciplineDivineRadio.isChecked = false
                                    generatorOptionSpellDisciplineNaturalRadio.isChecked = false
                                }
                            }
                            generatorOptionSpellMethodGroup.check(
                                if (options.spellMethod == SpCoGenMethod.BY_THE_BOOK) {
                                    R.id.generator_option_spell_method_book_radio
                                } else R.id.generator_option_spell_method_random_radio
                            )
                            generatorOptionSpellCurseGroup.check(
                                when (options.spellCurses) {
                                    SpCoCurses.STRICT_GMG       -> R.id.generator_option_spell_curse_example_radio
                                    SpCoCurses.OFFICIAL_ONLY    -> R.id.generator_option_spell_curse_official_radio
                                    SpCoCurses.ANY_CURSE        -> R.id.generator_option_spell_curse_homebrew_radio
                                    SpCoCurses.NONE             -> R.id.generator_option_spell_curse_none_radio
                                }
                            )
                            generatorOptionRerollChoiceSwitch.isChecked = options.spellReroll
                            generatorOptionSpecialistSwitch.isChecked = options.spellReroll
                            generatorOptionReferenceSplatbookMagicCheckbox.isChecked =
                                options.allowedItemSources.splatbooksOK
                            generatorOptionReferenceHackjournalMagicCheckbox.isChecked =
                                options.allowedItemSources.hackJournalsOK
                            generatorOptionReferenceModulesMagicCheckbox.isChecked =
                                options.allowedItemSources.modulesOK
                            generatorOptionReferenceSplatbookSpellCheckbox.isChecked =
                                options.allowedSpellSources.splatbooksOK
                            generatorOptionReferenceHackjournalSpellCheckbox.isChecked =
                                options.allowedSpellSources.hackJournalsOK
                            generatorOptionReferenceModulesSpellCheckbox.isChecked =
                                options.allowedSpellSources.modulesOK
                        }

                        val dialog = AlertDialog.Builder(context).setView(dialogBinding.root)
                            .setCancelable(true)
                            .setPositiveButton(R.string.ok_affirmative, null)
                            .setNeutralButton("Revert") { dialog, _ ->
                                generatorViewModel.generatorOptions = GeneratorOptions()
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.action_cancel) {dialog, _ ->
                                dialog.cancel()
                            }
                            .create().apply{
                                setOnShowListener { dialog ->

                                    getButton(AlertDialog.BUTTON_POSITIVE)
                                        .setOnClickListener {

                                            // Validate
                                            if (
                                                dialogBinding.generatorOptionMapRawEdit.error == null &&
                                                dialogBinding.generatorOptionMapPaperEdit.error == null &&
                                                dialogBinding.generatorOptionMapScrollEdit.error == null &&
                                                dialogBinding.generatorOptionAllowedScrollError.visibility == View.GONE &&
                                                dialogBinding.generatorOptionMagicError.visibility == View.GONE) {

                                                // Update
                                                generatorViewModel.generatorOptions = GeneratorOptions(
                                                    gemMin = dialogBinding.generatorOptionGemSlider.values[0].toInt(),
                                                    gemMax = dialogBinding.generatorOptionGemSlider.values[1].toInt(),
                                                    artMin = dialogBinding.generatorOptionArtSlider.values[0].toInt() - 19,
                                                    artMax = dialogBinding.generatorOptionArtSlider.values[1].toInt() - 19,
                                                    mapBase = (dialogBinding.generatorOptionMapRawEdit.text.toString().toIntOrNull() ?: 0).coerceIn(0..100),
                                                    mapPaper = (dialogBinding.generatorOptionMapPaperEdit.text.toString().toIntOrNull() ?: 0).coerceIn(0..100),
                                                    mapScroll = (dialogBinding.generatorOptionMapScrollEdit.text.toString().toIntOrNull() ?: 0).coerceIn(0..100),
                                                    falseMapsOK = dialogBinding.generatorOptionMapFalseSwitch.isChecked,
                                                    allowedMagic = checkedItemTypes,
                                                    spellOk = dialogBinding.generatorOptionSpellScrollCheckbox.isChecked,
                                                    utilityOk = dialogBinding.generatorOptionUtilityScrollCheckbox.isChecked,
                                                    cursedOk = dialogBinding.generatorOptionCursedSwitch.isChecked,
                                                    intelOk = dialogBinding.generatorOptionIntelligentSwitch.isChecked,
                                                    spellDisciplinePos = when {
                                                        dialogBinding.generatorOptionSpellDisciplineArcaneRadio.isChecked -> 0
                                                        dialogBinding.generatorOptionSpellDisciplineDivineRadio.isChecked -> 1
                                                        dialogBinding.generatorOptionSpellDisciplineNaturalRadio.isChecked -> 2
                                                        else -> 3
                                                    },
                                                    spellMethod = when (dialogBinding.generatorOptionSpellMethodGroup.checkedRadioButtonId) {
                                                        R.id.generator_option_spell_method_book_radio -> SpCoGenMethod.BY_THE_BOOK
                                                        else -> SpCoGenMethod.TRUE_RANDOM
                                                    },
                                                    spellCurses = when (dialogBinding.generatorOptionSpellCurseGroup.checkedRadioButtonId) {
                                                        R.id.generator_option_spell_curse_official_radio-> SpCoCurses.OFFICIAL_ONLY
                                                        R.id.generator_option_spell_curse_homebrew_radio-> SpCoCurses.ANY_CURSE
                                                        R.id.generator_option_spell_curse_none_radio    -> SpCoCurses.NONE
                                                        else -> SpCoCurses.STRICT_GMG
                                                    },
                                                    spellReroll = dialogBinding.generatorOptionRerollChoiceSwitch.isChecked,
                                                    restrictedOk = dialogBinding.generatorOptionSpecialistSwitch.isChecked,
                                                    allowedItemSources = SpCoSources(
                                                        splatbooksOK = dialogBinding.generatorOptionReferenceSplatbookMagicCheckbox.isChecked,
                                                        hackJournalsOK = dialogBinding.generatorOptionReferenceHackjournalMagicCheckbox.isChecked,
                                                        modulesOK = dialogBinding.generatorOptionReferenceModulesMagicCheckbox.isChecked
                                                    ),
                                                    allowedSpellSources = SpCoSources(
                                                        splatbooksOK = dialogBinding.generatorOptionReferenceSplatbookSpellCheckbox.isChecked,
                                                        hackJournalsOK = dialogBinding.generatorOptionReferenceHackjournalSpellCheckbox.isChecked,
                                                        modulesOK = dialogBinding.generatorOptionReferenceModulesSpellCheckbox.isChecked
                                                    )
                                                )

                                                //Dismiss
                                                dialog.dismiss()
                                            }
                                        }
                                }
                            }

                            dialog.show()

                            true
                        }
                        
                        else    -> false
                    }
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {

        super.onStart()

        setCheckGenRadioCheckedFromVM()

        // Get version code from context
            appVersion = requireContext().packageManager
            .getPackageInfo(requireContext().packageName,0).versionCode
        
        // Prepare UI
        binding.generatorNameEdit.addTextChangedListener { input ->

            generatorViewModel.hoardName = input.toString()
        }

        binding.generatorMethodGroup.setOnCheckedChangeListener { _, _ ->

            when (binding.generatorMethodGroup.checkedRadioButtonId){

                R.id.generator_method_lettercode-> {

                    binding.generatorAnimatorFrame.displayedChild = 0

                }

                R.id.generator_method_specific  -> {

                    binding.generatorAnimatorFrame.displayedChild = 1
                }
            }

            generatorViewModel
                .setGeneratorMethodPos(binding.generatorAnimatorFrame.displayedChild)
        }

        // region [ Letter Code method groups ]

        // region ( Headers )
        binding.generatorLairHeader.setOnClickListener {

            if (binding.generatorLairRecyclerview.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorLairIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorLairHeader)
                    start() }

                // Hide the recycler view
                TransitionManager.beginDelayedTransition(binding.generatorLairCard,AutoTransition())
                binding.generatorLairRecyclerview.visibility = View.GONE

            } else {

                // Collapse Small-type card view if it is visible
                if (binding.generatorSmallRecyclerview.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorSmallIndicator, View.ROTATION, 90f, 0f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 250
                        disableViewDuringAnimation(binding.generatorSmallHeader)
                        start() }

                    // Hide the recycler view
                    TransitionManager.beginDelayedTransition(binding.generatorSmallCard,AutoTransition())
                    binding.generatorSmallRecyclerview.visibility = View.GONE
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorLairIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorLairHeader)
                    start() }

                // Reveal the recycler view
                TransitionManager.beginDelayedTransition(binding.generatorLairCard,AutoTransition())
                binding.generatorLairRecyclerview.visibility = View.VISIBLE
            }
        }

        binding.generatorSmallHeader.setOnClickListener {

            // Collapse Lair-type card view if it is visible
            if (binding.generatorSmallRecyclerview.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorSmallIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorSmallHeader)
                    start() }

                // Hide the recycler view
                TransitionManager.beginDelayedTransition(binding.generatorSmallCard,AutoTransition())
                binding.generatorSmallRecyclerview.visibility = View.GONE

            } else {

                if (binding.generatorLairRecyclerview.visibility == View.VISIBLE) {
                    val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorLairIndicator, View.ROTATION, 90f, 0f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 250
                        disableViewDuringAnimation(binding.generatorLairHeader)
                        start() }

                    // Hide the recycler view
                    TransitionManager.beginDelayedTransition(binding.generatorLairCard,AutoTransition())
                    binding.generatorLairRecyclerview.visibility = View.GONE
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorSmallIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorSmallHeader)
                    start() }

                // Reveal the recycler view
                TransitionManager.beginDelayedTransition(binding.generatorSmallCard,AutoTransition())
                binding.generatorSmallRecyclerview.visibility = View.VISIBLE
            }
        }
        // endregion

        // endregion

        // region [ Specific Quantity method groups ]

        // region ( Headers )
        binding.generatorCoinageHeader.setOnClickListener {

            if (binding.generatorCoinageLayout.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorCoinageIndicator)
                    start() }

                // Hide the coinage layout
                TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                binding.generatorCoinageLayout.visibility = View.GONE

            } else {

                // Collapse any other treasure layouts if there are expanded
                when {
                    (binding.generatorGemLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorGemIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorGemIndicator)
                            start()
                        }

                        // Hide the gem layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorGemCard,
                            AutoTransition()
                        )
                        binding.generatorGemLayout.visibility = View.GONE
                    }
                    (binding.generatorArtLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorArtIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorArtIndicator)
                            start()
                        }

                        // Hide the art layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorArtCard,
                            AutoTransition()
                        )
                        binding.generatorArtLayout.visibility = View.GONE
                    }
                    (binding.generatorMagicLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorMagicIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorMagicIndicator)
                            start()
                        }

                        // Hide the magic layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorMagicCard,
                            AutoTransition()
                        )
                        binding.generatorMagicLayout.visibility = View.GONE
                    }
                    (binding.generatorSpellLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorSpellIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorSpellIndicator)
                            start()
                        }

                        // Hide the spell layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorSpellCard,
                            AutoTransition()
                        )
                        binding.generatorSpellLayout.visibility = View.GONE
                    }
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorCoinageHeader)
                    start() }

                // Reveal the coinage layout
                TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                binding.generatorCoinageLayout.visibility = View.VISIBLE
            }

            // Scroll to top of card
            binding.generatorNestedScroll.scrollTo(0,binding.generatorCoinageCard.top)
        }

        binding.generatorGemHeader.setOnClickListener {
            if (binding.generatorGemLayout.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorGemIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorGemIndicator)
                    start() }

                // Hide the gem layout
                TransitionManager.beginDelayedTransition(binding.generatorGemCard,AutoTransition())
                binding.generatorGemLayout.visibility = View.GONE

            } else {

                // Collapse any other treasure layouts if there are expanded
                when {
                    (binding.generatorCoinageLayout.visibility == View.VISIBLE) -> {
                        val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 90f, 0f)

                        // Rotate the indicator
                        collapseAnimator.apply{
                            duration = 250
                            disableViewDuringAnimation(binding.generatorCoinageIndicator)
                            start() }

                        // Hide the coinage layout
                        TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                        binding.generatorCoinageLayout.visibility = View.GONE
                    }
                    (binding.generatorArtLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorArtIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorArtIndicator)
                            start()
                        }

                        // Hide the art layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorArtCard,
                            AutoTransition()
                        )
                        binding.generatorArtLayout.visibility = View.GONE
                    }
                    (binding.generatorMagicLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorMagicIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorMagicIndicator)
                            start()
                        }

                        // Hide the magic layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorMagicCard,
                            AutoTransition()
                        )
                        binding.generatorMagicLayout.visibility = View.GONE
                    }
                    (binding.generatorSpellLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorSpellIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorSpellIndicator)
                            start()
                        }

                        // Hide the recycler view
                        TransitionManager.beginDelayedTransition(
                            binding.generatorSpellCard,
                            AutoTransition()
                        )
                        binding.generatorSpellLayout.visibility = View.GONE
                    }
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorGemIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorGemHeader)
                    start() }

                // Reveal the gem layout
                TransitionManager.beginDelayedTransition(binding.generatorGemCard,AutoTransition())
                binding.generatorGemLayout.visibility = View.VISIBLE
            }

            // Scroll to top of card
            binding.generatorNestedScroll.scrollTo(0,binding.generatorGemCard.top)
        }

        binding.generatorArtHeader.setOnClickListener {
            if (binding.generatorArtLayout.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorArtIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorArtIndicator)
                    start() }

                // Hide the art layout
                TransitionManager.beginDelayedTransition(binding.generatorArtCard,AutoTransition())
                binding.generatorArtLayout.visibility = View.GONE

            } else {

                // Collapse any other treasure layouts if there are expanded
                when {
                    (binding.generatorCoinageLayout.visibility == View.VISIBLE) -> {
                        val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 90f, 0f)

                        // Rotate the indicator
                        collapseAnimator.apply{
                            duration = 250
                            disableViewDuringAnimation(binding.generatorCoinageIndicator)
                            start() }

                        // Hide the coinage layout
                        TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                        binding.generatorCoinageLayout.visibility = View.GONE
                    }
                    (binding.generatorGemLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorGemIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorGemIndicator)
                            start()
                        }

                        // Hide the gem layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorGemCard,
                            AutoTransition()
                        )
                        binding.generatorGemLayout.visibility = View.GONE
                    }
                    (binding.generatorMagicLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorMagicIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorMagicIndicator)
                            start()
                        }

                        // Hide the magic layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorMagicCard,
                            AutoTransition()
                        )
                        binding.generatorMagicLayout.visibility = View.GONE
                    }
                    (binding.generatorSpellLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorSpellIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorSpellIndicator)
                            start()
                        }

                        // Hide the spell layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorSpellCard,
                            AutoTransition()
                        )
                        binding.generatorSpellLayout.visibility = View.GONE
                    }
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorArtIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorArtHeader)
                    start() }

                // Reveal the gem layout
                TransitionManager.beginDelayedTransition(binding.generatorArtCard,AutoTransition())
                binding.generatorArtLayout.visibility = View.VISIBLE
            }

            // Scroll to top of card
            binding.generatorNestedScroll.scrollTo(0,binding.generatorArtCard.top)
        }

        binding.generatorMagicHeader.setOnClickListener {
            if (binding.generatorMagicLayout.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorMagicIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorMagicHeader)
                    start() }

                // Hide the magic item layout
                TransitionManager.beginDelayedTransition(binding.generatorMagicCard,AutoTransition())
                binding.generatorMagicLayout.visibility = View.GONE

            } else {

                // Collapse any other treasure layouts if there are expanded
                when {
                    (binding.generatorCoinageLayout.visibility == View.VISIBLE) -> {
                        val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 90f, 0f)

                        // Rotate the indicator
                        collapseAnimator.apply{
                            duration = 250
                            disableViewDuringAnimation(binding.generatorCoinageIndicator)
                            start() }

                        // Hide the coinage layout
                        TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                        binding.generatorCoinageLayout.visibility = View.GONE
                    }
                    (binding.generatorGemLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorGemIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorGemIndicator)
                            start()
                        }

                        // Hide the gem layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorGemCard,
                            AutoTransition()
                        )
                        binding.generatorGemLayout.visibility = View.GONE
                    }
                    (binding.generatorArtLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorArtIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorArtIndicator)
                            start()
                        }

                        // Hide the magic layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorArtCard,
                            AutoTransition()
                        )
                        binding.generatorArtLayout.visibility = View.GONE
                    }
                    (binding.generatorSpellLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorSpellIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorSpellIndicator)
                            start()
                        }

                        // Hide the spell layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorSpellCard,
                            AutoTransition()
                        )
                        binding.generatorSpellLayout.visibility = View.GONE
                    }
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorMagicIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorMagicHeader)
                    start() }

                // Reveal the magic item layout
                TransitionManager.beginDelayedTransition(binding.generatorMagicCard,AutoTransition())
                binding.generatorMagicLayout.visibility = View.VISIBLE
            }

            // Scroll to top of card
            binding.generatorNestedScroll.scrollTo(0,binding.generatorMagicCard.top)
        }

        binding.generatorSpellHeader.setOnClickListener {
            if (binding.generatorSpellLayout.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorSpellIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorSpellHeader)
                    start() }

                // Hide the spell layout
                TransitionManager.beginDelayedTransition(binding.generatorSpellCard,AutoTransition())
                binding.generatorSpellLayout.visibility = View.GONE

            } else {

                // Collapse any other treasure layouts if there are expanded
                when {
                    (binding.generatorCoinageLayout.visibility == View.VISIBLE) -> {
                        val collapseAnimator = ObjectAnimator.ofFloat(binding.generatorCoinageIndicator, View.ROTATION, 90f, 0f)

                        // Rotate the indicator
                        collapseAnimator.apply{
                            duration = 250
                            disableViewDuringAnimation(binding.generatorCoinageIndicator)
                            start() }

                        // Hide the coinage layout
                        TransitionManager.beginDelayedTransition(binding.generatorCoinageCard,AutoTransition())
                        binding.generatorCoinageLayout.visibility = View.GONE
                    }
                    (binding.generatorGemLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorGemIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorGemIndicator)
                            start()
                        }

                        // Hide the gem layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorGemCard,
                            AutoTransition()
                        )
                        binding.generatorGemLayout.visibility = View.GONE
                    }
                    (binding.generatorArtLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorArtIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorArtIndicator)
                            start()
                        }

                        // Hide the art layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorArtCard,
                            AutoTransition()
                        )
                        binding.generatorArtLayout.visibility = View.GONE
                    }
                    (binding.generatorMagicLayout.visibility == View.VISIBLE) -> {

                        val collapseAnimator = ObjectAnimator.ofFloat(
                            binding.generatorMagicIndicator,
                            View.ROTATION,
                            90f,
                            0f
                        )

                        // Rotate the indicator
                        collapseAnimator.apply {
                            duration = 250
                            disableViewDuringAnimation(binding.generatorMagicIndicator)
                            start()
                        }

                        // Hide the magic layout
                        TransitionManager.beginDelayedTransition(
                            binding.generatorMagicCard,
                            AutoTransition()
                        )
                        binding.generatorMagicLayout.visibility = View.GONE
                    }
                }

                val expandAnimator = ObjectAnimator.ofFloat(binding.generatorSpellIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(binding.generatorSpellHeader)
                    start() }

                // Reveal the spell layout
                TransitionManager.beginDelayedTransition(binding.generatorSpellCard,AutoTransition())
                binding.generatorSpellLayout.visibility = View.VISIBLE
            }

            // Scroll to top of card
            binding.generatorNestedScroll.scrollTo(0,binding.generatorSpellCard.top)
        }
        // endregion

        // region ( Listeners )
        binding.apply {

            // region ( Coinage listeners )
            generatorCoinageAllowedCp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.cpChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageAllowedSp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.spChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageAllowedEp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.epChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageAllowedGp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.gpChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageAllowedHsp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.hspChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageAllowedPp.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.ppChecked = isChecked
                validateCoinMinAndCheckboxes()
            }
            generatorCoinageMinimumEdit.addTextChangedListener { input ->

                generatorCoinageMinimumEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.COIN_MINIMUM,
                        input.toString())

                if (generatorCoinageMinimumEdit.error == null) validateCoinMinAndCheckboxes()

                generatorCoinageMaximumEdit.error =
                    generatorViewModel.validateCoinageMaximum()

            }
            generatorCoinageMaximumEdit.addTextChangedListener { input ->

                generatorCoinageMaximumEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.COIN_MAXIMUM,
                        input.toString())
            }
            // endregion

            // region ( Gem listeners )
            generatorGemQtyAuto.addTextChangedListener { input ->
                generatorGemQtyAuto.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.GEM_QTY,
                        input.toString())
            }
            // endregion

            // region ( Art object listeners )
            generatorArtQtyEdit.addTextChangedListener { input ->
                generatorArtQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.ART_QTY,
                        input.toString())
            }
            // endregion

            // region ( Magic item listeners )
            generatorMagicPotionQtyEdit.addTextChangedListener { input ->
                generatorMagicPotionQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.POTION_QTY,
                        input.toString())
            }
            generatorMagicScrollQtyEdit.addTextChangedListener { input ->
                generatorMagicScrollQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.SCROLL_QTY,
                        input.toString())

                if (generatorMagicScrollQtyEdit.error == null) validateScrollQtyAndOptions()
            }
            generatorMagicWeaponQtyEdit.addTextChangedListener { input ->
                generatorMagicWeaponQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.WEAPON_ARMOR_QTY,
                        input.toString())
            }
            generatorMagicNonweaponQtyEdit.addTextChangedListener { input ->
                generatorMagicNonweaponQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.ANY_BUT_WEAP_QTY,
                        input.toString())
            }
            generatorMagicAnyQtyEdit.addTextChangedListener { input ->
                generatorMagicAnyQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.ANY_MAGIC_QTY,
                        input.toString())
            }
            // endregion

            // region ( Spell collection listeners )
            generatorSpellQtyEdit.addTextChangedListener { input ->
                generatorSpellQtyEdit.error =
                    generatorViewModel.setValueFromEditText(
                        GenEditTextTag.SPELL_CO_QTY,
                        input.toString())
            }

            generatorSpellLevelSlider.apply{
                valueFrom = 0.0f
                valueTo = 9.0f
                stepSize = 1.0f
                values = listOf(
                    generatorViewModel.spellLevelRange.first.toFloat(),
                    generatorViewModel.spellLevelRange.last.toFloat()
                )
                setLabelFormatter { value ->
                    val intValue = value.toInt()
                    if (intValue in spLvlRangeShortLabels.indices) {
                        spLvlRangeShortLabels[intValue]
                    } else "???"
                }
                addOnChangeListener { slider, _, _ ->
                    val intValues = slider.values.first().toInt() to slider.values.last().toInt()
                    generatorViewModel.spellLevelRange = IntRange(intValues.first,intValues.second)
                    binding.generatorSpellLevelMinValue.text =
                        if (intValues.first in spLvlRangeLongLabels.indices) {
                            spLvlRangeLongLabels[intValues.first]
                        } else "???"
                    binding.generatorSpellLevelMaxValue.text =
                        if (intValues.second in spLvlRangeLongLabels.indices) {
                            spLvlRangeLongLabels[intValues.second]
                        } else "???"
                }

                binding.generatorSpellLevelMinValue.text =
                    if (values.first().toInt() in spLvlRangeLongLabels.indices) {
                        spLvlRangeLongLabels[values.first().toInt()]
                    } else "???"
                binding.generatorSpellLevelMaxValue.text =
                    if (values.last().toInt() in spLvlRangeLongLabels.indices) {
                        spLvlRangeLongLabels[values.last().toInt()]
                    } else "???"
            }

            generatorSpellPerQtySlider.apply{
                valueFrom = 1.0f
                valueTo = 20.0f
                stepSize = 1.0f
                values = listOf(
                    generatorViewModel.spellsPerRange.first.toFloat(),
                    generatorViewModel.spellsPerRange.last.toFloat()
                )
                setLabelFormatter { it.toInt().toString() }
                addOnChangeListener { slider, _, _ ->
                    val intValues = slider.values.first().toInt() to slider.values.last().toInt()
                    generatorViewModel.spellsPerRange = IntRange(intValues.first,intValues.second)
                    binding.generatorSpellPerQtyMinValue.text =
                        intValues.first.toString()
                    binding.generatorSpellPerQtyMaxValue.text =
                        intValues.second.toString()
                }

                binding.generatorSpellPerQtyMinValue.text =
                    values.first().toInt().toString()
                binding.generatorSpellPerQtyMaxValue.text =
                    values.last().toInt().toString()
            }
         // endregion
        }
        // endregion

        // endregion

        // region [ Buttons ]
        binding.generatorResetButton.setOnClickListener {

                if (binding.generatorMethodLettercode.isChecked){

                    // Zero out type counters
                    generatorViewModel.resetLairCount()
                    generatorViewModel.resetSmallCount()

                    // Update recycler views
                    binding.generatorLairRecyclerview.adapter?.notifyDataSetChanged()
                    binding.generatorSmallRecyclerview.adapter?.notifyDataSetChanged()

                } else {
                    generatorViewModel.resetSpecificQtyValues()
                    binding.setFieldsFromViewModel()
                }
        }

        binding.generatorGenerateButton.setOnClickListener {

            // Generate hoard
            when (binding.generatorMethodGroup.checkedRadioButtonId) {

                R.id.generator_method_lettercode-> {

                    if (generatorViewModel.validateLetterCodeValues()){

                       generatorViewModel.generateHoard(true, appVersion)

                    } else {

                        Toast.makeText(context,
                            "Please indicate at least one treasure type to generate.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.generator_method_specific  ->{

                    if (validateSpecificQtyValues()){

                        generatorViewModel.generateHoard(false, appVersion)

                    } else {

                        Toast.makeText(context,"Specific quantity validation unsuccessful",Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    Toast.makeText(context,"Generator button pressed (No method?)",Toast.LENGTH_SHORT).show()
                }
            }

        }
        // endregion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

    // region [ Inner classes ]

    private inner class LetterAdapter(private val isLairAdapter: Boolean)
        : ListAdapter<Pair<String,Int>, LetterAdapter.LetterHolder>(LetterDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val binding = LetterRecyclerItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return LetterHolder(binding, isLairAdapter)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) {

            val currentItem = getItem(position)

            holder.bind(currentItem)
        }

        private inner class LetterHolder (val binding: LetterRecyclerItemBinding, val isLairHolder: Boolean)
            : RecyclerView.ViewHolder(binding.root) {

            fun bind(entry: Pair<String, Int>){

                val typeString = "${getString(R.string.odds_ref_treasure_type)} ${entry.first}"
                val qtyString = "x ${entry.second}"

                binding.apply {

                    letterItemInfodot.setOnClickListener {
                        generatorViewModel.fetchLetterCode(entry.first)
                    }

                    letterItemText.text = typeString

                    letterItemButton.apply {
                        text = qtyString
                        setOnClickListener {

                        val dialogString =
                            "${getString(R.string.letter_qty_picker_dialog_message)} ${entry.first}:"

                        val qtyDialogView = layoutInflater
                            .inflate(R.layout.dialog_letter_number_picker, null)

                        qtyDialogView.findViewById<TextView>(R.id.dialog_letter_message).text =
                            dialogString
                        val qtyPicker = qtyDialogView
                            .findViewById<NumberPicker>(R.id.dialog_letter_qty_picker).apply{
                                minValue = MINIMUM_LETTER_QTY
                                maxValue = MAXIMUM_LETTER_QTY
                                value = entry.second
                                wrapSelectorWheel = false
                            }
                        qtyDialogView.findViewById<TextView>(R.id.dialog_letter_qty_current).text =
                            entry.second.toString()

                            // Build and show dialog
                            AlertDialog.Builder(context).setView(qtyDialogView)
                                .setTitle(getString(R.string.new_qty_title))
                                .setPositiveButton(R.string.action_submit_qty_update) { _, _ ->
                                    if (qtyPicker.value != entry.second) {
                                        if (isLairHolder) {
                                            generatorViewModel.updateLairEntry(adapterPosition,
                                                qtyPicker.value)
                                        } else {
                                            generatorViewModel.updateSmallEntry(adapterPosition,
                                                qtyPicker.value)
                                        }
                                    } }
                                .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                    dialog.cancel() }
                                .setCancelable(true)
                                .show()
                    }
                    }
                }
            }
        }
    }

    private class LetterDiffCallback : DiffUtil.ItemCallback<Pair<String,Int>>() {

        override fun areItemsTheSame(
            oldItem: Pair<String,Int>,
            newItem: Pair<String,Int>
        ) = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String,Int>,
            newItem: Pair<String,Int>
        ) = oldItem.second == newItem.second
    }
    //endregion

    //region [ Helper functions ]
    private fun validateSpecificQtyValues() : Boolean {

        // Validate coinage checkboxes
        validateCoinMinAndCheckboxes()

        // Check for errors on coinage fields
        if ((binding.generatorCoinageMinimumEdit.error != null) ||
                (binding.generatorCoinageMinimumEdit.error != null)||
                (generatorViewModel.validateCoinageMaximum() != null)) {

            return false
        }

        // Check for errors on gem fields
        if (binding.generatorGemQtyAuto.error != null) {

            return false
        }

        // Check for errors on art fields
        if (binding.generatorArtQtyEdit.error != null) {

            return false
        }

        // Validate scroll checkboxes
        validateScrollQtyAndOptions()

        // Check for errors on magic item fields
        if ((binding.generatorMagicPotionQtyEdit.error != null)||
            (binding.generatorMagicScrollQtyEdit.error != null)||
            (binding.generatorMagicWeaponQtyEdit.error != null)||
            (binding.generatorMagicNonweaponQtyEdit.error != null)||
            (binding.generatorMagicAnyQtyEdit.error != null)) {

            return false
        }

        // Check for errors on spell collection fields
        if (binding.generatorSpellQtyEdit.error != null) {

            return false
        }

        return true
    }

    private fun validateCoinMinAndCheckboxes() {

        if (binding.generatorCoinageMinimumEdit.text.toString().toDoubleOrNull() != null) {

            val coinMinimum = binding.generatorCoinageMinimumEdit.text.toString().toDouble()

            val denominationsSum =
                (if (binding.generatorCoinageAllowedCp.isChecked) 0.01 else 0.0) +
                        (if (binding.generatorCoinageAllowedSp.isChecked) 0.1 else 0.0) +
                        (if (binding.generatorCoinageAllowedEp.isChecked) 0.5 else 0.0) +
                        (if (binding.generatorCoinageAllowedGp.isChecked) 1.0 else 0.0) +
                        (if (binding.generatorCoinageAllowedHsp.isChecked) 2.0 else 0.0) +
                        (if (binding.generatorCoinageAllowedPp.isChecked) 5.0 else 0.0)

            binding.generatorCoinageMinimumEdit.error = if (coinMinimum > 0.00) {

                when {

                    (denominationsSum == 0.00) -> "No denominations selected"
                    (coinMinimum < denominationsSum) -> "Invalid denomination combination"
                    else -> null
                }

            } else {

                if (denominationsSum > 0.00) {
                    "Sum of denominations cannot be greater than minimum"
                } else {
                    null
                }
            }

        } else {

            binding.generatorCoinageMinimumEdit.error = "Invalid input"
        }
    }

    private fun validateScrollQtyAndOptions() {

        if (binding.generatorMagicScrollQtyEdit.text.toString().toIntOrNull() != null) {

            val scrollQty = binding.generatorMagicScrollQtyEdit.text.toString().toInt()

            binding.generatorMagicScrollQtyEdit.error = if ((scrollQty > 0) &&
                (!generatorViewModel.generatorOptions.spellOk &&
                        !generatorViewModel.generatorOptions.utilityOk &&
                        generatorViewModel.generatorOptions.mapScroll == 0)) {
                Log.e(
                    "validateScrollQtyAndOptions | generatorMagicScrollQtyEdit",
                    "More than zero scrolls entered, but no types checked."
                )
                "No scrolls enabled"
            } else null

        } else {

            binding.generatorMagicScrollQtyEdit.error = "Invalid input"
        }
    }

    private fun setCheckGenRadioCheckedFromVM() {

        if (generatorViewModel.generationMethodPos == 0) {

            binding.generatorMethodGroup.check(R.id.generator_method_lettercode)
            binding.generatorAnimatorFrame.displayedChild = 0

        } else {

            binding.generatorMethodGroup.check(R.id.generator_method_specific)
            binding.generatorAnimatorFrame.displayedChild = 1
        }
    }

    private fun showButtonsCrossfade() {

        isButtonGroupAnimating = true

        binding.generatorBottomButtonGroup.apply {

            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.generatorResetButton.isEnabled = true
                        binding.generatorGenerateButton.isEnabled = true
                    }
                })
        }

        binding.generatorBottomWaitingGroup.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.generatorBottomWaitingGroup.visibility = View.GONE
                        isButtonGroupAnimating = false
                    }
                })
        }
    }

    private fun hideButtonsCrossfade() {

        isButtonGroupAnimating = true

        binding.generatorBottomWaitingGroup.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        binding.generatorBottomButtonGroup.apply {

            binding.generatorResetButton.isEnabled = false
            binding.generatorGenerateButton.isEnabled = false
            alpha = 1f
            visibility = View.VISIBLE

            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.generatorBottomButtonGroup.visibility = View.GONE
                        isButtonGroupAnimating = false
                    }
                })
        }
    }

    private fun showLetterCodeOddsDialog(letterCode: LetterCode) {

        val oddsView = layoutInflater.inflate(R.layout.dialog_odds_reference,null)

        val dialogBuilder = AlertDialog.Builder(context).setView(oddsView)

        oddsView.apply {
            findViewById<TextView>(R.id.odds_ref_treasure_type_letter).text =
                letterCode.letterID

            // Copper pieces
            if (letterCode.cpChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_cp_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_cp_odds_info).text =
                    letterCode.cpChance.toString()
                (letterCode.cpMin.formatWithCommas() + " - " +
                        letterCode.cpMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_cp_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_cp_card).visibility = View.GONE
            }

            // Silver pieces
            if (letterCode.spChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_sp_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_sp_odds_info).text =
                    letterCode.spChance.toString()
                (letterCode.spMin.formatWithCommas() + " - " +
                        letterCode.spMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_sp_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_sp_card).visibility = View.GONE
            }

            // Electrum pieces
            if (letterCode.epChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_ep_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_ep_odds_info).text =
                    letterCode.epChance.toString()
                (letterCode.epMin.formatWithCommas() + " - " +
                        letterCode.epMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_ep_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_ep_card).visibility = View.GONE
            }

            // Gold pieces
            if (letterCode.gpChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_gp_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_gp_odds_info).text =
                    letterCode.gpChance.toString()
                (letterCode.gpMin.formatWithCommas() + " - " +
                        letterCode.gpMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_gp_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_gp_card).visibility = View.GONE
            }

            // Hard silver pieces
            if (letterCode.hspChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_hsp_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_hsp_odds_info).text =
                    letterCode.hspChance.toString()
                (letterCode.hspMin.formatWithCommas() + " - " +
                        letterCode.hspMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_hsp_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_hsp_card).visibility = View.GONE
            }

            // Platinum pieces
            if (letterCode.ppChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_pp_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_pp_odds_info).text =
                    letterCode.ppChance.toString()
                (letterCode.ppMin.formatWithCommas() + " - " +
                        letterCode.ppMax.formatWithCommas()).also {
                    findViewById<TextView>(R.id.odds_ref_pp_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_pp_card).visibility = View.GONE
            }

            // Gemstones / Jewels
            if (letterCode.gemChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_gems_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_gems_odds_info).text =
                    letterCode.gemChance.toString()
                (letterCode.gemMin.toString() + " - " + letterCode.gemMax.toString()).also {
                    findViewById<TextView>(R.id.odds_ref_gems_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_gems_card).visibility = View.GONE
            }

            // Art objects
            if (letterCode.artChance > 0) {
                findViewById<MaterialCardView>(R.id.odds_ref_art_card).visibility = View.VISIBLE
                findViewById<TextView>(R.id.odds_ref_art_odds_info).text =
                    letterCode.artChance.toString()
                (letterCode.artMin.toString() + " - " + letterCode.artMax.toString()).also {
                    findViewById<TextView>(R.id.odds_ref_art_amount_info).text = it }
            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_art_card).visibility = View.GONE
            }

            // Magic item(s)
            if (letterCode.potionChance > 0 || letterCode.scrollChance > 0 ||
                letterCode.weaponChance > 0 || letterCode.noWeaponChance > 0 ||
                    letterCode.anyChance > 0) {

                findViewById<MaterialCardView>(R.id.odds_ref_magic_card).visibility = View.VISIBLE

                if (letterCode.anyChance > 0) {

                    findViewById<TextView>(R.id.odds_ref_magic_odds_info).text =
                        letterCode.anyChance.toString()

                    findViewById<TextView>(R.id.odds_ref_magic_amount_info).text =
                        letterCode.anyMin.toString()

                    findViewById<TextView>(R.id.odds_ref_magic_qualifier).text =
                        getString(R.string.generator_label_any_magic)

                    findViewById<ImageView>(R.id.odds_ref_magic_backdrop)
                        .setImageResource(R.drawable.book_runes)

                    when {

                        letterCode.potionChance > 0 -> {

                            findViewById<View>(R.id.odds_ref_magic_add_on_divider).visibility =
                                View.VISIBLE
                            findViewById<LinearLayout>(R.id.odds_ref_magic_add_on_layout).visibility =
                                View.VISIBLE
                            findViewById<TextView>(R.id.odds_ref_magic_add_on_label).text =
                                getString(R.string.extra_potion_label)
                            findViewById<ImageView>(R.id.odds_ref_magic_add_on_icon)
                                .setImageResource(R.drawable.clipart_potion_vector_icon)
                        }

                        letterCode.scrollChance > 0 -> {

                            findViewById<View>(R.id.odds_ref_magic_add_on_divider).visibility =
                                View.VISIBLE
                            findViewById<LinearLayout>(R.id.odds_ref_magic_add_on_layout).visibility =
                                View.VISIBLE
                            findViewById<TextView>(R.id.odds_ref_magic_add_on_label).text =
                                getString(R.string.extra_scroll_label)
                            findViewById<ImageView>(R.id.odds_ref_magic_add_on_icon)
                                .setImageResource(R.drawable.clipart_scroll_vector_icon)
                        }

                        else -> {
                            findViewById<View>(R.id.odds_ref_magic_add_on_divider).visibility =
                                View.GONE
                            findViewById<LinearLayout>(R.id.odds_ref_magic_add_on_layout).visibility =
                                View.GONE
                        }
                    }

                } else {

                    when {

                        letterCode.potionChance > 0 ->  {

                            findViewById<TextView>(R.id.odds_ref_magic_odds_info).text =
                                letterCode.potionChance.toString()

                            findViewById<ImageView>(R.id.odds_ref_magic_backdrop)
                                .setImageResource(R.drawable.potion_treasure)

                            findViewById<TextView>(R.id.odds_ref_magic_qualifier).text =
                                getString(R.string.generator_label_potions)

                            if (letterCode.potionMin == letterCode.potionMax) {

                                findViewById<TextView>(R.id.odds_ref_magic_amount_info).text =
                                    letterCode.potionMin.toString()

                            } else {
                                (letterCode.potionMin.toString() + " - " +
                                        letterCode.potionMax.toString()).also {
                                    findViewById<TextView>(R.id.odds_ref_magic_amount_info)
                                        .text = it }
                            }
                        }

                        letterCode.scrollChance > 0 ->  {

                            findViewById<TextView>(R.id.odds_ref_magic_odds_info).text =
                                letterCode.scrollChance.toString()

                            findViewById<ImageView>(R.id.odds_ref_magic_backdrop)
                                .setImageResource(R.drawable.scroll_base)

                            findViewById<TextView>(R.id.odds_ref_magic_qualifier).text =
                                getString(R.string.generator_label_scrolls)

                            if (letterCode.scrollMin == letterCode.potionMax) {

                                findViewById<TextView>(R.id.odds_ref_magic_amount_info).text =
                                    letterCode.scrollMin.toString()

                            } else {
                                (letterCode.scrollMin.toString() + " - " +
                                        letterCode.scrollMax.toString()).also {
                                    findViewById<TextView>(R.id.odds_ref_magic_amount_info)
                                        .text = it }
                            }
                        }

                        letterCode.weaponChance > 0 ->  {

                            findViewById<TextView>(R.id.odds_ref_magic_odds_info).text =
                                letterCode.weaponChance.toString()

                            findViewById<ImageView>(R.id.odds_ref_magic_backdrop)
                                .setImageResource(R.drawable.weapon_sword_fire)

                            findViewById<TextView>(R.id.odds_ref_magic_qualifier).text =
                                getString(R.string.generator_label_armor_weapons)

                            findViewById<TextView>(R.id.odds_ref_magic_amount_info).text =
                                letterCode.weaponMin.toString()
                        }

                        letterCode.noWeaponChance > 0 ->  {

                            findViewById<TextView>(R.id.odds_ref_magic_odds_info).text =
                                letterCode.noWeaponChance.toString()

                            findViewById<ImageView>(R.id.odds_ref_magic_backdrop)
                                .setImageResource(R.drawable.shield_charged)

                            findViewById<TextView>(R.id.odds_ref_magic_qualifier).text =
                                getString(R.string.generator_label_except_weapons)

                            findViewById<TextView>(R.id.odds_ref_magic_amount_info).text =
                                letterCode.noWeaponMin.toString()
                        }
                    }
                }

            } else {
                findViewById<MaterialCardView>(R.id.odds_ref_magic_card).visibility = View.GONE
            }

        }

        dialogBuilder.setPositiveButton(R.string.ok_affirmative) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }

    private fun Int.formatWithCommas() : String = NumberFormat.getNumberInstance().format(this)

    private fun LayoutGeneratorFragmentBinding.setFieldsFromViewModel() {

        // Coinage
        generatorCoinageAllowedCp.isChecked = generatorViewModel.cpChecked
        generatorCoinageAllowedSp.isChecked = generatorViewModel.spChecked
        generatorCoinageAllowedEp.isChecked = generatorViewModel.epChecked
        generatorCoinageAllowedGp.isChecked = generatorViewModel.gpChecked
        generatorCoinageAllowedHsp.isChecked = generatorViewModel.hspChecked
        generatorCoinageAllowedPp.isChecked = generatorViewModel.ppChecked

        // Gems
        generatorGemQtyAuto.setText( generatorViewModel.gemQty.toString() )

        // Art objects
        generatorArtQtyEdit.setText( generatorViewModel.artQty.toString() )

        // Magic items
        generatorMagicPotionQtyEdit.setText( generatorViewModel.potionQty.toString() )
        generatorMagicScrollQtyEdit.setText( generatorViewModel.scrollQty.toString() )
        generatorMagicWeaponQtyEdit.setText( generatorViewModel.armWepQty.toString() )
        generatorMagicNonweaponQtyEdit.setText( generatorViewModel.anyButQty.toString() )
        generatorMagicAnyQtyEdit.setText( generatorViewModel.anyMgcQty.toString() )

        // Spell collections
        generatorSpellQtyEdit.setText( generatorViewModel.spCoQty.toString() )

        generatorSpellLevelSlider.values = listOf(
            generatorViewModel.spellLevelRange.first.toFloat(),
            generatorViewModel.spellLevelRange.last.toFloat()
        )
        generatorSpellPerQtySlider.values = listOf(
            generatorViewModel.spellsPerRange.first.toFloat(),
            generatorViewModel.spellsPerRange.last.toFloat()
        )
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

        addListener(onStart = { _ ->
                view.isEnabled = false },
            onEnd = { _ ->
                view.isEnabled = (view.visibility == View.VISIBLE) })
    }

    //endregion

    companion object {

        fun newInstance(): HoardGeneratorFragment {
            return HoardGeneratorFragment()
        }
    }
}

enum class GenEditTextTag() {
    HOARD_NAME,
    COIN_MINIMUM,
    COIN_MAXIMUM,
    GEM_QTY,
    ART_QTY,
    POTION_QTY,
    SCROLL_QTY,
    WEAPON_ARMOR_QTY,
    ANY_BUT_WEAP_QTY,
    ANY_MAGIC_QTY,
    SPELL_CO_QTY
}