package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.LayoutGeneratorFragmentBinding
import com.example.android.treasurefactory.databinding.LettercodeItemBinding
import com.example.android.treasurefactory.model.LetterEntry
import com.example.android.treasurefactory.viewmodel.HoardGeneratorViewModel
import com.example.android.treasurefactory.viewmodel.HoardGeneratorViewModelFactory

class HoardGeneratorFragment : Fragment() {

    //region [ Property declarations ]
    private var appVersion = 0

    private var shortAnimationDuration: Int = 0

    private var isButtonGroupAnimating = false

    //https://www.rockandnull.com/jetpack-viewmodel-initialization/
    //https://developer.android.com/codelabs/android-room-with-a-view-kotlin#9

    private val generatorViewModel: HoardGeneratorViewModel by viewModels {
        HoardGeneratorViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    // String arrays for dropdown menus
    private val dropdownGemValues by lazy { resources.getStringArray(R.array.dropdown_gem_values) }
    private val dropdownArtValues by lazy { resources.getStringArray(R.array.dropdown_art_values) }
    private val dropdownSpellDisciplines by lazy { resources.getStringArray(R.array.dropdown_spell_disciplines) }
    private val dropdownSpellLevels by lazy { resources.getStringArray(R.array.dropdown_spell_levels) }
    private val dropdownSpellTypes by lazy { resources.getStringArray(R.array.dropdown_spell_types) }
    private val dropdownSpellTypesEnabled by lazy { BooleanArray(dropdownSpellTypes.size)  { index -> (index < 2) } }
    private val dropdownSpellCurses by lazy { resources.getStringArray(R.array.dropdown_spell_curses) }

    // Adapters for letter code RecyclerViews
    private val lairAdapter: LetterAdapter = LetterAdapter(true)
    private val smallAdapter: LetterAdapter = LetterAdapter(false)

    // Adapters for dropdown AutoCompleteTextViews
    private val dropdownGemValuesAdapter by lazy { DropdownAdapter(requireContext(), R.layout.dropdown_menu_item, dropdownGemValues,null) }
    private val dropdownArtValuesAdapter by lazy { DropdownAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownArtValues, null) }
    private val dropdownSpellDisciplinesAdapter by lazy { DropdownAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellDisciplines, null) }
    private val dropdownSpellTypeAdapter by lazy { DropdownAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellTypes, null) }
    private val dropdownSpellLevelsAdapter by lazy { DropdownAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellLevels, dropdownSpellTypesEnabled) }
    private val dropdownSpellCursesAdapter by lazy { DropdownAdapter(requireContext(),R.layout.dropdown_menu_item,dropdownSpellCurses, null) }
    //endregion

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

        // region [ Prepare Specific Quantity views ]

        // Apply adapters for dropdowns

        binding.generatorGemMinimumAuto.initializeAsDropdown(dropdownGemValuesAdapter,generatorViewModel.getGemMinPos())
        binding.generatorGemMaximumAuto.initializeAsDropdown(dropdownGemValuesAdapter,generatorViewModel.getGemMaxPos())

        binding.generatorArtMinimumAuto.initializeAsDropdown(dropdownArtValuesAdapter, generatorViewModel.getArtMinPos())
        binding.generatorArtMaximumAuto.initializeAsDropdown(dropdownArtValuesAdapter, generatorViewModel.getArtMaxPos())

        binding.generatorSpellDisciplineAuto.initializeAsDropdown(dropdownSpellDisciplinesAdapter, generatorViewModel.getSplDisciplinePos())
        binding.generatorSpellTypeAuto.initializeAsDropdown(dropdownSpellTypeAdapter, generatorViewModel.getGenMethodPos(),true)
        binding.generatorSpellMinimumAuto.initializeAsDropdown(dropdownSpellLevelsAdapter, generatorViewModel.getSpLvlMinPos())
        binding.generatorSpellMaximumAuto.initializeAsDropdown(dropdownSpellLevelsAdapter, generatorViewModel.getSpLvlMaxPos())
        binding.generatorSpellCursesAuto.initializeAsDropdown(dropdownSpellCursesAdapter, generatorViewModel.getSpCoCursesPos())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        generatorViewModel.apply {

            lairListLiveData.observe(viewLifecycleOwner) { newLairList ->
                lairAdapter.submitList(newLairList)
                Log.d("HoardGeneratorFragment","lairListLiveData on ViewModel observed.")
            }
            smallListLiveData.observe(viewLifecycleOwner) { newSmallList ->
                smallAdapter.submitList(newSmallList)
                Log.d("HoardGeneratorFragment","smallListLiveData on ViewModel observed.")
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
        }

        binding.hoardGeneratorToolbar.apply {

            navigationIcon = AppCompatResources.getDrawable(context,R.drawable.clipart_back_vector_icon)

            setNavigationOnClickListener {

                if (generatorViewModel.isRunningAsyncLiveData.value != true) {

                    findNavController().popBackStack()

                } else {

                    Toast.makeText(context,"Cannot navigate back; still generating treasure.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            title = getString(R.string.hoard_generator_fragment_title)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {

        super.onStart()

        setCheckGenRadioCheckedFromVM()

        // Get version code from context
            appVersion = requireContext().packageManager
            .getPackageInfo(requireContext().packageName,0).versionCode
        
        // Apply widget properties TODO animate between two LinearLayouts
        binding.generatorNameEdit.addTextChangedListener { input ->

            generatorViewModel.setHoardName(input.toString())
        }

        binding.generatorMethodGroup.setOnCheckedChangeListener { _, _ ->

            when (binding.generatorMethodGroup.checkedRadioButtonId){

                R.id.generator_method_lettercode-> {

                    binding.generatorAnimatorFrame.displayedChild = 0

                    Log.d("generatorMethodGroup", "By-letter method activated.")

                    /*if (binding.generatorAnimatorFrame.displayedChild != 0){

                        binding.generatorAnimatorFrame.displayedChild = 0
                        // TODO + If it doesn't do so, animate transition
                        // TODO + Disable Viewgroups during animation
                        Log.d("generatorMethodGroup", "By-letter method activated.")
                    } else {
                        Log.d("generatorMethodGroup", "By-letter method was already set.")
                    }*/
                }

                R.id.generator_method_specific  -> {

                    binding.generatorAnimatorFrame.displayedChild = 1

                    Log.d("generatorMethodGroup", "Specific-quantity method activated.")

                    /*if (binding.generatorAnimatorFrame.displayedChild != 1){

                        binding.generatorAnimatorFrame.displayedChild = 1
                        // + If it doesn't do so, animate transition
                        // + Disable Viewgroups during animation
                        Log.d("generatorMethodGroup", "Specific-quantity method activated.")
                    } else {
                        Log.d("generatorMethodGroup", "Specific-quantity method was already set.")
                    }*/
                }

                else -> {
                    Log.d("generatorMethodGroup","No active method set.")
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
            binding.generatorScrollview.scrollTo(0,binding.generatorCoinageCard.top)
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
            binding.generatorScrollview.scrollTo(0,binding.generatorGemCard.top)
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
            binding.generatorScrollview.scrollTo(0,binding.generatorArtCard.top)
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
            binding.generatorScrollview.scrollTo(0,binding.generatorMagicCard.top)
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
            binding.generatorScrollview.scrollTo(0,binding.generatorSpellCard.top)
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
                    generatorViewModel.setValueFromEditText(GenEditTextTag.COIN_MINIMUM,
                        input.toString())

                if (generatorCoinageMinimumEdit.error == null) validateCoinMinAndCheckboxes()

                generatorCoinageMaximumEdit.error =
                    generatorViewModel.validateCoinageMaximum()

            }
            generatorCoinageMaximumEdit.addTextChangedListener { input ->

                generatorCoinageMaximumEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.COIN_MAXIMUM,
                        input.toString())
            }
            // endregion

            // region ( Gem listeners )
            generatorGemQtyAuto.addTextChangedListener { input ->
                generatorGemQtyAuto.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.GEM_QTY,
                        input.toString())
            }
            generatorGemMinimumAuto.setOnItemClickListener { parent, view, position, id ->

                val _maxDropdownAdapterPos = (generatorGemMaximumAuto.adapter as DropdownAdapter<*>)
                    .getSelectedPosition()

                (generatorGemMinimumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorGemMinimumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.GEM_MINIMUM,position,true)

                // Validate maximum
                generatorGemMaximumAuto.error =
                    generatorViewModel.validateDropdownMaximum(GenDropdownTag.GEM_MAXIMUM,
                        _maxDropdownAdapterPos)
            }
            generatorGemMaximumAuto.setOnItemClickListener {  parent, view, position, id ->

                (generatorGemMaximumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorGemMaximumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.GEM_MAXIMUM,position,true)
            }
            // endregion

            // region ( Art object listeners )
            generatorArtQtyEdit.addTextChangedListener { input ->
                generatorArtQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.ART_QTY,
                        input.toString())
            }
            generatorArtMinimumAuto.setOnItemClickListener { parent, view, position, id ->

                val _maxDropdownAdapterPos = (generatorArtMaximumAuto.adapter as DropdownAdapter<*>)
                    .getSelectedPosition()

                (generatorArtMaximumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorArtMinimumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.ART_MINIMUM,position,true)

                // Validate maximum
                generatorArtMaximumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.ART_MAXIMUM,_maxDropdownAdapterPos,true)
            }
            generatorArtMaximumAuto.setOnItemClickListener {  parent, view, position, id ->

                (generatorArtMaximumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorArtMaximumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.ART_MAXIMUM,position,true)
            }
            generatorArtSwitchMaps.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.artMapChecked = isChecked
            }
            // endregion

            // region ( Magic item listeners )
            generatorMagicPotionQtyEdit.addTextChangedListener { input ->
                generatorMagicPotionQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.POTION_QTY,
                        input.toString())
            }
            generatorMagicScrollQtyEdit.addTextChangedListener { input ->
                generatorMagicScrollQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.SCROLL_QTY,
                        input.toString())

                if (generatorMagicScrollQtyEdit.error == null) validateScrollQtyAndCheckboxes()
            }
            generatorScrollSpellCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.spellScrollChecked = isChecked
                validateScrollQtyAndCheckboxes()
            }
            generatorScrollNonspellCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.nonSpScrollChecked = isChecked
                validateScrollQtyAndCheckboxes()
            }
            generatorScrollMapCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.scrollMapChecked = isChecked
                validateScrollQtyAndCheckboxes()
            }
            generatorMagicWeaponQtyEdit.addTextChangedListener { input ->
                generatorMagicWeaponQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.WEAPON_ARMOR_QTY,
                        input.toString())
            }
            generatorWeaponSwitch.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.intWepChecked = isChecked
            }
            generatorMagicNonweaponQtyEdit.addTextChangedListener { input ->
                generatorMagicNonweaponQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.ANY_BUT_WEAP_QTY,
                        input.toString())
            }
            generatorMagicAnyQtyEdit.addTextChangedListener { input ->
                generatorMagicAnyQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.ANY_MAGIC_QTY,
                        input.toString())
            }
            generatorCursedSwitch.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.cursedChecked = isChecked
            }
            generatorArtifactSwitch.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.relicsChecked = isChecked
            }
            // endregion

            // region ( Spell collection listeners )
            generatorSpellDisciplineAuto.setOnItemClickListener { parent, view, position, id ->

                val _typeDropdownAdapterPos = (generatorSpellTypeAuto.adapter as DropdownAdapter<*>)
                    .getSelectedPosition()

                (generatorSpellDisciplineAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorSpellDisciplineAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_DISCIPLINE,position,true)

                Log.d("generatorSpellDisciplineAuto.setOnItemClickListener",
                    "dropdownSpellTypes.size = ${dropdownSpellTypes.size}")

                // Programmatically set enabled items based on allowed disciplines
                val itemsToEnable =
                    generatorViewModel.setEnabledItemsByDiscipline(position, dropdownSpellTypes.size)

                Log.d("generatorSpellDisciplineAuto.setOnItemClickListener","itemsToEnable = " +
                        itemsToEnable)

                Log.d("generatorSpellDisciplineAuto.setOnItemClickListener",
                    "generatorSpellTypeAuto.(adapter as DropdownAdapter<*>).getEnabledItemsArray() = " +
                            (generatorSpellTypeAuto.adapter as DropdownAdapter<*>)
                                .getEnabledItemsArray())

                generatorSpellTypeAuto.apply{
                    (adapter as DropdownAdapter<*>).setEnabledByArray(itemsToEnable)
                    error = if (!(adapter.isEnabled(_typeDropdownAdapterPos))) {
                        "Invalid method for chosen discipline."
                    } else null
                }
            }
            generatorSpellTypeAuto.setOnItemClickListener { parent, view, position, id ->
                generatorSpellTypeAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_TYPE,position,
                        generatorSpellTypeAuto.adapter.isEnabled(position))
            }
            generatorSpellQtyEdit.addTextChangedListener { input ->
                generatorSpellQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.SPELL_CO_QTY,
                        input.toString())
            }
            generatorSpellPerQtyEdit.addTextChangedListener { input ->
                generatorSpellPerQtyEdit.error =
                    generatorViewModel.setValueFromEditText(GenEditTextTag.MAX_SPELL_PER,
                        input.toString())
            }
            generatorSpellMinimumAuto.setOnItemClickListener { parent, view, position, id ->

                val _maxDropdownAdapterPos = (generatorSpellMaximumAuto.adapter as DropdownAdapter<*>)
                    .getSelectedPosition()

                (generatorSpellMinimumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorSpellMinimumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_MINIMUM,position,true)

                // Validate maximum
                generatorArtMaximumAuto.error =
                    generatorViewModel.validateDropdownMaximum(GenDropdownTag.SPELL_MAXIMUM, _maxDropdownAdapterPos)
            }
            generatorSpellMaximumAuto.setOnItemClickListener {  parent, view, position, id ->

                (generatorSpellMaximumAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorSpellMaximumAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_MAXIMUM,position,true)
            }
            generatorSpellSourceSplatCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.splatChecked = isChecked
            }
            generatorSpellSourceHjCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.hackJChecked = isChecked
            }
            generatorSpellSourceModulesCheckbox.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.otherChecked = isChecked
            }
            generatorRestrictedSwitch.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.restrictChecked = isChecked
            }
            generatorChoiceRerollSwitch.setOnCheckedChangeListener { _, isChecked ->
                generatorViewModel.rerollChecked = isChecked
            }
            generatorSpellCursesAuto.setOnItemClickListener {  parent, view, position, id ->

                Log.d("generatorSpellCursesAuto.setOnItemClickListener",
                    "Position = $position " +
                            "(${generatorSpellCursesAuto.adapter.getItem(position)})")

                (generatorSpellCursesAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

                generatorSpellCursesAuto.error =
                    generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_CURSES,position,true)
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
                    // Update recyclerviews TODO see why DiffUtil doesn't automatically update
                    binding.generatorLairRecyclerview.adapter?.notifyDataSetChanged()
                    binding.generatorSmallRecyclerview.adapter?.notifyDataSetChanged()
                } else {
                    generatorViewModel.resetSpecificQtyValues()
                    binding.setFieldsFromViewModel()
                }
        }

        binding.generatorGenerateButton.setOnClickListener {

            //TODO find out why soft keyboard botches order processing.
            //https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2

            // Generate hoard order
            when (binding.generatorMethodGroup.checkedRadioButtonId) {

                R.id.generator_method_lettercode-> {

                    Log.d("generatorGenerateButton","Procedure for generating hoard order by letter method called.")

                    if (generatorViewModel.validateLetterCodeValues()){

                        // TEMPORARILY house letter code order here TODO
                        val letterOrder = generatorViewModel.compileLetterCodeHoardOrder()

                        generatorViewModel.generateHoard(letterOrder,appVersion)

                    } else {

                        Toast.makeText(context,"Please indicate at least one treasure type to generate.",Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.generator_method_specific  ->{

                    Log.d("generatorGenerateButton","Procedure for generating hoard order by specific method called.")

                    if (validateSpecificQtyValues()){
                        
                        // TEMPORARILY house specific quantity order here TODO
                        val specQtyOrder = generatorViewModel.compileSpecificQtyHoardOrder()

                        generatorViewModel.generateHoard(specQtyOrder,appVersion)

                    } else {

                        Toast.makeText(context,"Specific quantity validation unsuccessful",Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    Toast.makeText(context,"Generator button pressed (No method?)",Toast.LENGTH_SHORT).show()
                    Log.d("generatorGenerateButton","No method specified.")
                }
            }

            // TODO add Dialog for completion

        }
        // endregion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region [ Inner classes ]

    private inner class LetterAdapter(
        //val letterEntries: ArrayList<LetterEntry>,
        private val isLairAdapter: Boolean)
        : ListAdapter<LetterEntry,LetterAdapter.LetterHolder>(LetterDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val binding = LettercodeItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return LetterHolder(binding)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) { //TODO consider replacing position calls with holder.adapterPosition

            val currentItem = getItem(position)

            holder.bind(currentItem)
        }

        private inner class LetterHolder (val binding: LettercodeItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            fun bind(letterEntry: LetterEntry){

                val typeString = "Type ${letterEntry.letterCode}"
                val oddsToast = Toast.makeText(context,letterEntry.oddsDesc,Toast.LENGTH_LONG)

                //TODO move OnClickListeners back to OnBindViewHolder if new binding schema fails
                binding.apply {
                    lettercodeItemName.text = typeString
                    lettercodeItemInfodot.setOnClickListener {

                        oddsToast.show()
                    }
                    lettercodeItemDecrementButton.setOnClickListener {

                        generatorViewModel.incrementLetterQty(adapterPosition,false, isLairAdapter)
                        //updateLetterAdapter(isLairAdapter)
                        notifyItemChanged(adapterPosition)
                    }
                    lettercodeItemIncrementButton.setOnClickListener {

                        generatorViewModel.incrementLetterQty(adapterPosition,true, isLairAdapter)
                        //updateLetterAdapter(isLairAdapter)
                        notifyItemChanged(adapterPosition)
                    }
                    lettercodeItemCounter.text  = letterEntry.quantity.toString()
                }
            }
        }
    }

    private class LetterDiffCallback : DiffUtil.ItemCallback<LetterEntry>() {

        override fun areItemsTheSame(
            oldItem: LetterEntry,
            newItem: LetterEntry
        ) = oldItem.letterCode == newItem.letterCode

        override fun areContentsTheSame(
            oldItem: LetterEntry,
            newItem: LetterEntry
        ) = oldItem.quantity == newItem.quantity
    }

    /**
     * Extended [ArrayAdapter]<[String]> that disables filtering, keeps track of last selected item, and has holder array for enabled items.
     *
     * @param _enabledItemsArray [BooleanArray] that should be the same size as [values]. If it is not or is null, all items will be initially enabled.
     */
    private inner class DropdownAdapter<String>(context: Context, layout: Int,
                                                var values: Array<String>,
                                                _enabledItemsArray: BooleanArray?) :
        ArrayAdapter<String>(context, layout, values) {

        //https://rmirabelle.medium.com/there-is-no-material-design-spinner-for-android-3261b7c77da8

        private val emptyFilter = object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = values
                results.count = values.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }

        private var selectedPosition = 0

        private var enabledItemsArray : BooleanArray =
            if ((_enabledItemsArray != null) && (_enabledItemsArray.size == values.size)) {
                _enabledItemsArray
            } else {
                BooleanArray(values.size) { true }
            }

        fun getSelectedPosition() = selectedPosition

        fun setSelectedPosition(newPosition: Int) {

            if ((newPosition in values.indices) && (selectedPosition != newPosition)) {

                selectedPosition = newPosition
                Log.d("DropdownAdapter.setSelectedPosition()","Set adapter position to " +
                        selectedPosition + " (${values[selectedPosition]})")

            } else {

                Log.d("DropdownAdapter.setSelectedPosition()","Adapter position remains " +
                        "unchanged at " + selectedPosition + " (${values[selectedPosition]})")
            }
        }

        fun getEnabledItemsArray() = enabledItemsArray

        override fun getFilter(): Filter = emptyFilter

        override fun isEnabled(position: Int): Boolean {
            return enabledItemsArray[position]
        }

        fun setEnabled(position: Int, newValue: Boolean) {

            if (position in values.indices){

                enabledItemsArray[position] = newValue
                Log.d("DropdownAdapter","values[$position] \"${values[position]}\" is ${
                    if (enabledItemsArray[position]) "ENABLED" else "DISABLED"}.")
                notifyDataSetChanged()

            } else {

                Log.e("DropdownAdapter","Position $position is out of bounds, cannot set to $newValue.")
            }
        }

        fun setEnabledByArray(newValueArray: BooleanArray?) {

            if (newValueArray != null){

                if ((newValueArray.size == values.size)){

                    enabledItemsArray = newValueArray
                    enabledItemsArray.forEachIndexed { index, b ->
                        Log.d("DropdownAdapter","values[$index] \"${values[index]}\" is ${
                            if (enabledItemsArray[index]) "ENABLED" else "DISABLED"}.")
                    }
                    notifyDataSetChanged()

                } else {

                    Log.e(
                        "DropdownAdapter", "newValueArray $newValueArray is not the " +
                                "same size as enabledItemsArray $enabledItemsArray. (" +
                                newValueArray.size + " vs " + enabledItemsArray.size + ")")
                }
            } else {

                Log.e("DropdownAdapter","newValueArray is null; enableItemsArray " +
                        "remains as $enabledItemsArray.")
            }
        }
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

            Log.d("validateSpecificQtyValues",
                "Failed validation at coinage check")

            return false
        }

        // Check for errors on gem fields
        if ((binding.generatorGemQtyAuto.error != null)||
            (binding.generatorGemMinimumAuto.error != null)||
            (binding.generatorGemMaximumAuto.error != null)) {

            Log.d("validateSpecificQtyValues",
                "Failed validation at gem check")

            return false
        }

        // Check for errors on art fields
        if ((binding.generatorArtQtyEdit.error != null)||
            (binding.generatorArtMinimumAuto.error != null)||
            (binding.generatorArtMaximumAuto.error != null)) {

            Log.d("validateSpecificQtyValues",
                "Failed validation at art object check")

            return false
        }

        // Validate scroll checkboxes
        validateScrollQtyAndCheckboxes()

        // Check for errors on magic item fields
        if ((binding.generatorMagicPotionQtyEdit.error != null)||
            (binding.generatorMagicScrollQtyEdit.error != null)||
            (binding.generatorMagicWeaponQtyEdit.error != null)||
            (binding.generatorMagicNonweaponQtyEdit.error != null)||
            (binding.generatorMagicAnyQtyEdit.error != null)) {

            Log.d("validateSpecificQtyValues",
                "Failed validation at magic item check")

            return false
        }

        // Check for errors on spell collection fields
        if((binding.generatorSpellQtyEdit.error != null)||
            (binding.generatorSpellPerQtyEdit.error != null)||
            (binding.generatorSpellDisciplineAuto.error != null)||
            (binding.generatorSpellTypeAuto.error != null)||
            (binding.generatorSpellMinimumAuto.error != null)||
            (binding.generatorSpellMaximumAuto.error != null)) {

            Log.d("validateSpecificQtyValues",
                "Failed validation at spell collection check")

            return false
        }

        Log.d("validateSpecificQtyValues",
            "Passed validation")

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

            Log.d("validateCoinMinAndCheckboxes.denominationsSum", "= $denominationsSum")

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

    private fun validateScrollQtyAndCheckboxes() {
        if (binding.generatorMagicScrollQtyEdit.text.toString().toIntOrNull() != null) {

            val scrollQty = binding.generatorMagicScrollQtyEdit.text.toString().toInt()

            binding.generatorMagicScrollQtyEdit.error = if ((scrollQty > 0) &&
                (((binding.generatorScrollSpellCheckbox.isChecked) ||
                        (binding.generatorScrollNonspellCheckbox.isChecked) ||
                        (binding.generatorScrollMapCheckbox.isChecked)).not())
            ) {
                Log.e(
                    "validateSpecificQtyValues | generatorMagicScrollQtyEdit",
                    "More than zero scrolls entered, but no types checked."
                )
                "No types checked"
            } else null

        } else {

            binding.generatorMagicScrollQtyEdit.error = "Invalid input"
        }
    }

    private fun setCheckGenRadioCheckedFromVM() {

        if (generatorViewModel.getGenerationMethodPos() == 0) {

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

    private fun LayoutGeneratorFragmentBinding.setFieldsFromViewModel() {

        // Coinage
        generatorCoinageAllowedCp.isChecked = generatorViewModel.cpChecked
        generatorCoinageAllowedSp.isChecked = generatorViewModel.spChecked
        generatorCoinageAllowedEp.isChecked = generatorViewModel.epChecked
        generatorCoinageAllowedGp.isChecked = generatorViewModel.gpChecked
        generatorCoinageAllowedHsp.isChecked = generatorViewModel.hspChecked
        generatorCoinageAllowedPp.isChecked = generatorViewModel.ppChecked
        generatorCoinageMinimumEdit.setText( generatorViewModel.getCoinMin().toString() )
        generatorCoinageMaximumEdit.setText( generatorViewModel.getCoinMax().toString() )

        // Gems
        generatorGemQtyAuto.setText( generatorViewModel.getGemQty().toString() )
        generatorGemMinimumAuto.apply {

            val position = generatorViewModel.getGemMinPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.GEM_MINIMUM,position,true)
        }
        generatorGemMaximumAuto.apply {

            val position = generatorViewModel.getGemMinPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(generatorViewModel.getGemMaxPos())

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.GEM_MAXIMUM,position,true)
        }

        // Art objects
        generatorArtQtyEdit.setText( generatorViewModel.getArtQty().toString() )
        generatorArtMinimumAuto.apply {

            val position = generatorViewModel.getArtMinPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.ART_MINIMUM,position,true)
        }
        generatorArtMaximumAuto.apply {

            val position = generatorViewModel.getArtMaxPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.ART_MINIMUM,position,true)
        }
        generatorArtSwitchMaps.isChecked = generatorViewModel.artMapChecked

        // Magic items
        generatorMagicPotionQtyEdit.setText( generatorViewModel.getPotionQty().toString() )
        generatorMagicScrollQtyEdit.setText( generatorViewModel.getScrollQty().toString() )
        generatorScrollSpellCheckbox.isChecked = generatorViewModel.spellScrollChecked
        generatorScrollNonspellCheckbox.isChecked = generatorViewModel.nonSpScrollChecked
        generatorScrollMapCheckbox.isChecked = generatorViewModel.scrollMapChecked
        generatorMagicWeaponQtyEdit.setText( generatorViewModel.getArmWepQty().toString() )
        generatorWeaponSwitch.isChecked = generatorViewModel.intWepChecked
        generatorMagicNonweaponQtyEdit.setText( generatorViewModel.getAnyButQty().toString() )
        generatorMagicAnyQtyEdit.setText( generatorViewModel.getAnyMgcQty().toString() )
        generatorCursedSwitch.isChecked = generatorViewModel.cursedChecked
        generatorArtifactSwitch.isChecked = generatorViewModel.relicsChecked

        // Spell collections
        generatorSpellDisciplineAuto.apply {

            val position = generatorViewModel.getSplDisciplinePos()

            (generatorSpellDisciplineAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_DISCIPLINE,position,true)
        }
        generatorSpellTypeAuto.apply{

            val position = generatorViewModel.getGenMethodPos()

            (generatorSpellTypeAuto.adapter as DropdownAdapter<*>).apply {
                setSelectedPosition(position)
                setEnabledByArray(generatorViewModel.getEnabledGenMetArray())
            }

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_TYPE,position,
                    generatorSpellTypeAuto.adapter.isEnabled(position))
        }
        generatorSpellQtyEdit.setText( generatorViewModel.getSpCoQty().toString() )
        generatorSpellPerQtyEdit.setText( generatorViewModel.getMaxSpellsPerSpCo().toString() )
        generatorSpellMinimumAuto.apply {

            val position = generatorViewModel.getSpLvlMinPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_MINIMUM,position,true)
        }
        generatorSpellMaximumAuto.apply {

            val position = generatorViewModel.getSpLvlMaxPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_MAXIMUM,position,true)
        }
        generatorSpellSourceSplatCheckbox.isChecked = generatorViewModel.splatChecked
        generatorSpellSourceHjCheckbox.isChecked = generatorViewModel.hackJChecked
        generatorSpellSourceModulesCheckbox.isChecked = generatorViewModel.otherChecked
        generatorRestrictedSwitch.isChecked = generatorViewModel.restrictChecked
        generatorChoiceRerollSwitch.isChecked = generatorViewModel.rerollChecked
        generatorSpellCursesAuto.apply {

            val position = generatorViewModel.getSpCoCursesPos()

            (adapter as DropdownAdapter<*>).setSelectedPosition(position)

            setText((adapter as DropdownAdapter<*>).getItem(position).toString())

            error =
                generatorViewModel.setValueFromDropdown(GenDropdownTag.SPELL_CURSES,position,true)
        }
    }

    private fun AutoCompleteTextView.initializeAsDropdown(dropdownAdapter: DropdownAdapter<*>,
                                                          defaultPos: Int,
                                                          isSpellTypeDropdown: Boolean = false) {
        this.setAdapter(dropdownAdapter)
        this.setText((adapter as DropdownAdapter<*>).values[defaultPos].toString(),false)
        (adapter as DropdownAdapter<*>).setSelectedPosition(defaultPos)
        if (isSpellTypeDropdown) {
            (adapter as DropdownAdapter<*>).setEnabledByArray(generatorViewModel.getEnabledGenMetArray())
        }
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

    //endregion

    companion object {

        fun newInstance(): HoardGeneratorFragment {
            return HoardGeneratorFragment()
        }
    }
}

enum class GenDropdownTag() {
    GEM_MINIMUM,
    GEM_MAXIMUM,
    ART_MINIMUM,
    ART_MAXIMUM,
    SPELL_DISCIPLINE,
    SPELL_TYPE,
    SPELL_MINIMUM,
    SPELL_MAXIMUM,
    SPELL_CURSES
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
    SPELL_CO_QTY,
    MAX_SPELL_PER
}