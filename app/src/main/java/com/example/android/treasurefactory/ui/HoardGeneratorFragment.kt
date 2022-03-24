package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.databinding.LayoutGeneratorFragmentBinding
import com.example.android.treasurefactory.databinding.LettercodeItemBinding
import com.example.android.treasurefactory.model.HoardOrder
import com.example.android.treasurefactory.model.LetterEntry
import com.example.android.treasurefactory.viewmodel.HoardGeneratorViewModel
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

class HoardGeneratorFragment : Fragment() {

    /*/ *** Fragment ViewModel ***
    private val hoardGeneratorViewModel: HoardGeneratorViewModel by lazy {
        ViewModelProvider(this).get(HoardGeneratorViewModel::class.java)
    }
    //TODO continue from page 178, potentially remove*/

    //TODO add Specific quantity generation method after completing MVP

    //region [ Property declarations ]

    private var _binding: LayoutGeneratorFragmentBinding? = null
    private val binding get() = _binding!!

    private val generatorViewModel: HoardGeneratorViewModel by lazy {

        ViewModelProvider(this).get(HoardGeneratorViewModel::class.java)
    }

    private var lairAdapter: LetterAdapter? = null

    private var smallAdapter: LetterAdapter? = null

    private var lairList = getLetterArrayList(true, defaultSplitKey)
    private var smallList= getLetterArrayList(false,defaultSplitKey)

    // TODO continue porting over logic from here to viewmodel
    // TODO continue to refactor for viewbinding

    //endregion

    //region [ Overridden functions ]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = LayoutGeneratorFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // region [ Prepare Letter Code views ]
        // Define the letter adapters
        lairAdapter = LetterAdapter(lairList, true)
        smallAdapter = LetterAdapter(smallList, false)

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

        // region [ Prepare Specific Quantity views ]
        // Prepare arrays for dropdowns
        val dropdownGemValues = resources.getStringArray(R.array.dropdown_gem_values)
        val dropdownArtValues = resources.getStringArray(R.array.dropdown_art_values)
        val dropdownSpellDisciplines = resources.getStringArray(R.array.dropdown_spell_disciplines)
        val dropdownSpellLevels = resources.getStringArray(R.array.dropdown_spell_levels)
        val dropdownSpellType = resources.getStringArray(R.array.dropdown_spell_type_both)

        // Prepare adapters for dropdowns

        // endregion

        // Return inflated view
        return view
    }

    override fun onStart() {

        super.onStart()

        // Apply widget properties TODO animate between two LinearLayouts
        binding.generatorMethodLettercode.apply{
            setOnCheckedChangeListener { _, isChecked ->
                Toast.makeText(context,"By-Letter method is ${if (isChecked) "en" else "dis"}abled.",Toast.LENGTH_SHORT).show()
            }
        }

        // region [ Letter Code method groups ]

        binding.generatorLairHeader.setOnClickListener {

            // TODO Move functions outside listener.

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

            // TODO Move functions outside listener.

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

        // region [ Specific Quantity method groups ]

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

                        // Hide the recycler view
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
                binding.generatorGemLayout.visibility = View.VISIBLE
            }
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
                        binding.generatorArtLayout.visibility = View.GONE
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
        }

        //Coinage

        // endregion

        binding.generatorResetButton.apply{

            setOnClickListener {
                if (binding.generatorMethodLettercode.isChecked){
                    // Zero out type counters
                    generatorViewModel.clearLairCount()
                    generatorViewModel.clearSmallCount()
                    // Update recyclerviews
                    lairAdapter = LetterAdapter(lairList, true)
                    smallAdapter = LetterAdapter(smallList, false)
                    binding.generatorLairRecyclerview.adapter = lairAdapter
                    binding.generatorSmallRecyclerview.adapter = smallAdapter
                } else {
                    //TODO unimplemented
                }
            }
        }

        binding.generatorGenerateButton.apply {

            isEnabled = true //TODO only enable button when valid input is available.
            setOnClickListener {

                val hoardOrder: HoardOrder

                // Generate hoard order
                hoardOrder = if (binding.generatorMethodLettercode.isChecked) {
                    generatorViewModel.compileLetterCodeHoardOrder()
                } else {
                    val (coinMin,coinMax,coinSet) = getSpecificCoinageParams()

                    generatorViewModel.compileSpecificQtyHoardOrder(
                        coinMin,coinMax,coinSet,)
                }

                // Display contents in debug log
                reportHoardOrderToDebug(hoardOrder)

                // Toast in main app UI
                Toast.makeText(context,"Order generated. Check debug logs.",Toast.LENGTH_SHORT).show()

                // TODO send hoard order to actual treasure factory (also, "Treasure Hacktory"?)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //endregion

    //region [ Inner classes ]

    private inner class LetterAdapter(
        val letterEntries: ArrayList<LetterEntry>, private val isLairAdapter: Boolean)
        : ListAdapter<LetterEntry,LetterAdapter.LetterHolder>(LetterDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val binding = LettercodeItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return LetterHolder(binding)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) { //TODO consider replacing position calls with holder.adapterPosition

            val entry = letterEntries[position]

            // Prepare literals
            val oddsToast = letterEntries[position].oddsDesc

            // Bind items
            holder.bind(entry)

            //TODO fix UI update if it doesn't work when tested

            // Apply onClick listeners
            holder.binding.apply {
                lettercodeItemInfodot.setOnClickListener {

                    Toast.makeText(context,oddsToast,Toast.LENGTH_LONG).show()
                }
                lettercodeItemDecrementButton.setOnClickListener {

                    generatorViewModel.decrementLetterQty(holder.adapterPosition,isLairAdapter)
                    if(isLairAdapter){
                        submitList(generatorViewModel.lairList)
                    } else {
                        submitList(generatorViewModel.smallList)
                    }
                    //TODO add Eatrhbound-like animation on counter textview
                    notifyItemChanged(position)
                }
                lettercodeItemIncrementButton.setOnClickListener {

                    generatorViewModel.incrementLetterQty(holder.adapterPosition,isLairAdapter)
                    if(isLairAdapter){
                        submitList(generatorViewModel.lairList)
                    } else {
                        submitList(generatorViewModel.smallList)
                    }
                    notifyItemChanged(position)
                }
            }
        }

        private inner class LetterHolder (val binding: LettercodeItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            private lateinit var typeString: String

            /*
            val infoDot     = view.findViewById(R.id.lettercode_item_infodot) as ImageView
            val typeLabel   = view.findViewById(R.id.lettercode_item_name) as TextView
            val minusButton = view.findViewById(R.id.lettercode_item_decrement_button) as Button
            val quantityText= view.findViewById(R.id.lettercode_item_counter) as TextView
            val plusButton  = view.findViewById(R.id.letterdode_item_increment_button) as Button
            */

            fun bind(letterEntry: LetterEntry){

                typeString = "Type ${letterEntry.letterCode}"

                binding.lettercodeItemName.text = typeString
                binding.lettercodeItemCounter.text  = letterEntry.quantity.toString()
            }

        }

        fun getAdapterLetterEntries() : List<LetterEntry> = letterEntries

        @SuppressLint("NotifyDataSetChanged")
        fun resetLetterCounter(){
            if (isLairAdapter) generatorViewModel.clearLairCount() else generatorViewModel.clearSmallCount()
            notifyDataSetChanged()
        }
    }

    inner class LetterDiffCallback : DiffUtil.ItemCallback<LetterEntry>() {

        override fun areItemsTheSame(
            oldItem: LetterEntry,
            newItem: LetterEntry
        ): Boolean = oldItem.letterCode == newItem.letterCode

        override fun areContentsTheSame(
            oldItem: LetterEntry,
            newItem: LetterEntry
        ): Boolean = oldItem == newItem
    }

    //endregion

    //region [ Helper functions ]

    private fun convertLetterToHoardOrder() : HoardOrder {

        fun rollEntry(oddsArray: IntArray): Int {

            if (oddsArray[0] != 0) {

                // If number rolled is below target odds number,
                if (Random.nextInt(101) <= oddsArray[0]){

                    // Return random amount within range
                    return Random.nextInt(oddsArray[1],oddsArray[2] + 1)

                    // Otherwise, add nothing for this entry.
                } else return 0

            } else return 0
        }

        val letterMap = mutableMapOf<String,Int>()

        // Put values for every letter key
        lairAdapter!!.getAdapterLetterEntries().forEach{ letterMap[it.letter] = it.quantity }
        smallAdapter!!.getAdapterLetterEntries().forEach{ letterMap[it.letter] = it.quantity}

        val initialDescription = "Initial composition: "
        val lettersStringBuffer = StringBuffer(initialDescription)

        val newOrder = HoardOrder()

        newOrder.hoardName = hoardTitleField.text.toString()

        // Roll for each non-empty entry TODO: move to non-UI thread
        letterMap.forEach { (key, value) ->
            if ((oddsTable.containsKey(key))&&(value > 0)) {

                // Roll for each type of treasure
                repeat (value) {
                    newOrder.copperPieces       += rollEntry(oddsTable[key]?.get(0)!!)
                    newOrder.silverPieces       += rollEntry(oddsTable[key]?.get(1)!!)
                    newOrder.electrumPieces     += rollEntry(oddsTable[key]?.get(2)!!)
                    newOrder.goldPieces         += rollEntry(oddsTable[key]?.get(3)!!)
                    newOrder.hardSilverPieces   += rollEntry(oddsTable[key]?.get(4)!!)
                    newOrder.platinumPieces     += rollEntry(oddsTable[key]?.get(5)!!)
                    newOrder.gems               += rollEntry(oddsTable[key]?.get(6)!!)
                    newOrder.artObjects         += rollEntry(oddsTable[key]?.get(7)!!)
                    newOrder.potions            += rollEntry(oddsTable[key]?.get(8)!!)
                    newOrder.scrolls            += rollEntry(oddsTable[key]?.get(9)!!)
                    newOrder.armorOrWeapons     += rollEntry(oddsTable[key]?.get(10)!!)
                    newOrder.anyButWeapons      += rollEntry(oddsTable[key]?.get(11)!!)
                    newOrder.anyMagicItems      += rollEntry(oddsTable[key]?.get(12)!!)
                }

                // Log letter type in the StringBuffer
                if (!(lettersStringBuffer.equals(initialDescription))) {
                    // Add a comma if not the first entry TODO fix this
                    lettersStringBuffer.append(", ")
                }
                // Add letter times quantity
                    lettersStringBuffer.append("${key}x$value")
            }
        }

        // Update description log
        newOrder.creationDescription = lettersStringBuffer.toString()

        // Return result
        return newOrder
    }

    private fun reportHoardOrderToDebug(order: HoardOrder){
        Log.d("convertLetterToHoardOrder","- - - NEW ORDER - - -")
        Log.d("convertLetterToHoardOrder",order.creationDescription)
        Log.d("convertLetterToHoardOrder","COINAGE: ${order.copperPieces} cp, " +
                "${order.silverPieces} sp, " + "${order.electrumPieces} ep, " +
                "${order.goldPieces} gp, " + "${order.hardSilverPieces} hsp, " +
                "and $order.platinumPieces} pp")
        Log.d("convertLetterToHoardOrder","OBJECTS: ${order.gems} gems and " +
                "${order.artObjects} pieces of artwork")
        Log.d("convertLetterToHoardOrder","MAGIC ITEMS: ${order.potions} potions, " +
                "${order.scrolls} scrolls, ${order.armorOrWeapons} armor/weapons, " +
                "${order.anyButWeapons} magic items (non-weapon), and " +
                "${order.anyMagicItems} magic items of any type")
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

        // This extension method listens for start/end events on an animation and disables
        // the given view for the entirety of that animation.
        // Taken from https://developer.android.com/codelabs/advanced-android-kotlin-training-property-animation

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    private fun getSpecificCoinageParams(): Triple<Double,Double,Set<Pair<Double,String>>> {

        val minimumInput = binding.generatorCoinageMinimumEdit.text.toString()
            .toDoubleOrNull() ?: 0.0
        val maximumInput = binding.generatorCoinageMaximumEdit.text.toString()
            .toDoubleOrNull() ?: 0.0
        val allowedCoins = mutableSetOf<Pair<Double,String>>()

        if (binding.generatorCoinageAllowedCp.isChecked) allowedCoins.plusAssign(0.01 to "cp")
        if (binding.generatorCoinageAllowedSp.isChecked) allowedCoins.plusAssign(0.1 to "sp")
        if (binding.generatorCoinageAllowedEp.isChecked) allowedCoins.plusAssign(0.5 to "ep")
        if (binding.generatorCoinageAllowedGp.isChecked) allowedCoins.plusAssign(1.0 to "gp")
        if (binding.generatorCoinageAllowedHsp.isChecked) allowedCoins.plusAssign(2.0 to "hsp")
        if (binding.generatorCoinageAllowedPp.isChecked) allowedCoins.plusAssign(5.0 to "pp")

        return Triple(minimumInput,maximumInput,allowedCoins.toSet())
    }

    //private fun getSpecificSpellCoParams():



    //endregion

    companion object {

        fun newInstance(): HoardGeneratorFragment {
            return HoardGeneratorFragment()
        }

        private val treasureLabels = listOf<String>(
            "copper piece(s)",
            "silver piece(s)",
            "electrum piece(s)",
            "gold piece(s)",
            "hard silver piece(s)",
            "platinum piece(s)",
            "gem(s)",
            "art object(s)",
            "potion(s)/oil(s)",
            "scroll(s)",
            "magic weapon(s)/armor",
            "non-weapon magic item(s)",
            "magic item(s)")
    }
}