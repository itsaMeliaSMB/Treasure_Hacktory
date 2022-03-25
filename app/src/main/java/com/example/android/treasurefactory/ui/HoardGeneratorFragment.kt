package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.databinding.LayoutGeneratorFragmentBinding
import com.example.android.treasurefactory.databinding.LettercodeItemBinding
import com.example.android.treasurefactory.model.HoardOrder
import com.example.android.treasurefactory.model.LetterEntry
import com.example.android.treasurefactory.viewmodel.HoardGeneratorViewModel

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
        lairAdapter = LetterAdapter(generatorViewModel.lairList, true)
        smallAdapter = LetterAdapter(generatorViewModel.smallList, false)

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
        val dropdownSpellTypesBoth = resources.getStringArray(R.array.dropdown_spell_type_both)
        val dropdownSpellTypesMU = resources.getStringArray(R.array.dropdown_spell_type_arcane)
        val dropdownSpellTypesCl = resources.getStringArray(R.array.dropdown_spell_type_divine)

        // Prepare adapters for dropdowns
        val dropdownGemValuesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, dropdownGemValues)
        val dropdownArtValuesAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownArtValues)
        val dropdownSpellDisciplinesAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellDisciplines)
        val dropdownSpellTypeAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellTypesBoth)
        val dropdownSpellLevelsAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellLevels)

        // Apply adapters for dropdowns
        (binding.generatorGemMinimumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownGemValuesAdapter)
        (binding.generatorGemMaximumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownGemValuesAdapter)
        (binding.generatorArtMinimumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownArtValuesAdapter)
        (binding.generatorArtMaximumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownArtValuesAdapter)
        (binding.generatorSpellDisciplineInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownSpellDisciplinesAdapter)
        (binding.generatorSpellTypeInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownSpellTypeAdapter)
        (binding.generatorSpellMinimumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownSpellLevelsAdapter)
        (binding.generatorSpellMaximumInput.editText as? AutoCompleteTextView)?.setAdapter(dropdownSpellLevelsAdapter)
        // endregion

        // Return inflated view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO Not yet implemented - for use in EditText/ViewModel binding.
        // https://syrop.github.io/jekyll/update/2019/01/17/TextInputEditText-and-LiveData.html
    }

    override fun onStart() {

        super.onStart()

        // Apply widget properties TODO animate between two LinearLayouts
        binding.generatorMethodGroup.apply{
            setOnCheckedChangeListener { _, checkedId ->

                debugReportMethodVisibility("Before: ")

                // TODO Disable during transition
                when (checkedRadioButtonId){
                    R.id.generator_method_lettercode-> {
                        if (binding.generatorAnimatorFrame.displayedChild != 0){

                            binding.generatorAnimatorFrame.displayedChild = 0
                            // TODO + If it doesn't do so, animate transition
                            // TODO + Disable Viewgroups during animation
                            Log.d("generatorMethodGroup", "By-letter method activated.")
                        } else {
                            Log.d("generatorMethodGroup", "By-letter method was already set.")
                        }
                    }

                    R.id.generator_method_specific  -> {
                        if (binding.generatorAnimatorFrame.displayedChild != 1){

                            binding.generatorAnimatorFrame.displayedChild = 1
                            // + If it doesn't do so, animate transition
                            // + Disable Viewgroups during animation
                            Log.d("generatorMethodGroup", "Specific-quantity method activated.")
                        } else {
                            Log.d("generatorMethodGroup", "Specific-quantity method was already set.")
                        }
                    }

                    else -> {
                        Log.d("generatorMethodGroup","No active method set.")
                    }
                }

                debugReportMethodVisibility("After: ")

                generatorViewModel
                    .setGeneratorMethodPos(binding.generatorAnimatorFrame.displayedChild)
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

        // Coinage

        // endregion

        // region [ Buttons ]
        binding.generatorResetButton.apply{

            setOnClickListener {
                if (binding.generatorMethodLettercode.isChecked){
                    // Zero out type counters
                    generatorViewModel.clearLairCount()
                    generatorViewModel.clearSmallCount()
                    // Update recyclerviews
                    updateLetterAdapters()
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
                when (binding.generatorMethodGroup.checkedRadioButtonId) {
                    R.id.generator_method_lettercode-> {
                        Toast.makeText(context,"Generator button pressed (Letter method)",Toast.LENGTH_SHORT).show()
                        Log.d("generatorGenerateButton","Procedure for generating hoard order by letter method called.")
                        // hoardOrder = generatorViewModel.compileLetterCodeHoardOrder()
                    }
                    R.id.generator_method_specific  ->{
                        Toast.makeText(context,"Generator button pressed (Specific method)",Toast.LENGTH_SHORT).show()
                        Log.d("generatorGenerateButton","Procedure for generating hoard order by specific method called.")
                        // val (coinMin,coinMax,coinSet) = getSpecificCoinageParams()

                        // hoardOrder = generatorViewModel.compileSpecificQtyHoardOrder()
                    }
                    else -> {
                        Toast.makeText(context,"Generator button pressed (No method?)",Toast.LENGTH_SHORT).show()
                        Log.d("generatorGenerateButton","No method specified.")
                    }
                }

                // Display contents in debug log
                // TODO dummied out - reportHoardOrderToDebug(hoardOrder)

                // Toast in main app UI
                Toast.makeText(context,"Order generated. Check debug logs.",Toast.LENGTH_SHORT).show()

                // TODO send hoard order to actual treasure factory (also, "Treasure Hacktory"?)
            }
        }
        // endregion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region [ Inner classes ]

    // TODO Simplify with custom callback once app is navigable
    /** Updates the adapter of the letter RecyclerView to reflect the ViewModel's copy. */
    private fun updateLetterAdapter(targetLairAdapter: Boolean) {
        if (targetLairAdapter) {
            lairAdapter!!.submitList(generatorViewModel.lairList)
        } else {
            smallAdapter!!.submitList(generatorViewModel.smallList)
        }
    }

    /** Updates adapter of both the letter RecyclerViews to reflect the ViewModel's copies. */
    private fun updateLetterAdapters() {
        lairAdapter!!.submitList(generatorViewModel.lairList)
        smallAdapter!!.submitList(generatorViewModel.smallList)
    }

    private inner class LetterAdapter(
        val letterEntries: ArrayList<LetterEntry>, private val isLairAdapter: Boolean)
        : ListAdapter<LetterEntry,LetterAdapter.LetterHolder>(LetterDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val binding = LettercodeItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return LetterHolder(binding)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) { //TODO consider replacing position calls with holder.adapterPosition

            val entry = getItem(position)

            // Prepare literals
            val oddsToast = Toast.makeText(context,entry.oddsDesc,Toast.LENGTH_LONG)

            // Bind items
            holder.bind(entry)

            //TODO fix UI update if it doesn't work when tested

            // Apply onClick listeners
            holder.binding.apply {
                lettercodeItemInfodot.setOnClickListener {

                    oddsToast.show()
                }
                lettercodeItemDecrementButton.setOnClickListener {

                    generatorViewModel.decrementLetterQty(holder.adapterPosition,isLairAdapter)
                    updateLetterAdapter(isLairAdapter)
                    //notifyItemChanged(position)
                }
                lettercodeItemIncrementButton.setOnClickListener {

                    generatorViewModel.incrementLetterQty(holder.adapterPosition,isLairAdapter)
                    updateLetterAdapter(isLairAdapter)
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
        ): Boolean = oldItem.quantity == newItem.quantity
    }
    //endregion

    //region [ Helper functions ]
    private fun debugReportMethodVisibility(prefix: String = "", suffix: String =""){
        Log.d("generatorMethodGroup",prefix + "1st child (id ${
            binding.generatorAnimatorFrame.getChildAt(0).id
        }) is ${
            when (binding.generatorAnimatorFrame.getChildAt(0).visibility){
                View.VISIBLE    -> "visible"
                View.INVISIBLE  -> "invisible"
                View.GONE       -> "gone"
                else            -> "unknown"
            }}." + suffix)
        Log.d("generatorMethodGroup",prefix + "2nd child (id ${
            binding.generatorAnimatorFrame.getChildAt(1).id
        }) is ${
            when (binding.generatorAnimatorFrame.getChildAt(1).visibility){
                View.VISIBLE    -> "visible"
                View.INVISIBLE  -> "invisible"
                View.GONE       -> "gone"
                else            -> "unknown"
            }}." + suffix)
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
                view.isEnabled = (view.visibility == View.VISIBLE)
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
    }
}