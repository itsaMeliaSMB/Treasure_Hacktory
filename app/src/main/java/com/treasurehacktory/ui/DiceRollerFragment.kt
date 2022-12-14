package com.treasurehacktory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.treasurehacktory.PenDiceRoll
import com.treasurehacktory.R
import com.treasurehacktory.databinding.DiceRollListItemBinding
import com.treasurehacktory.databinding.LayoutDiceRollerBinding
import com.treasurehacktory.viewmodel.DiceRollerViewModel
import com.treasurehacktory.viewmodel.DiceRollerViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

class DiceRollerFragment : Fragment() {

    private var _binding: LayoutDiceRollerBinding? = null
    private val binding get() = _binding!!

    private var shortAnimationDuration = 0
    private var isWaitingCardAnimating = false

    private val diceRollerViewModel: DiceRollerViewModel by viewModels {
        DiceRollerViewModelFactory()
    }

    // region [ Overridden functions ]
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = LayoutDiceRollerBinding.inflate(inflater,container,false)
        val view = binding.root //TODO crash occurs here when trying to inflate xml line 364. 12/14/2022

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // region [ Listeners ]

        diceRollerViewModel.apply{

            isRunningAsyncLiveData.observe(viewLifecycleOwner) { isRunningAsync ->

                if (isRunningAsync) {

                    if (binding.diceRollerWaitingCard.waitingCard.visibility == View.GONE &&
                        !isWaitingCardAnimating) {

                        fadeInWaitingCard()

                    } else {

                        binding.diceRollerWaitingCard.waitingCard.visibility = View.VISIBLE
                        isWaitingCardAnimating = false
                    }

                } else {

                    if (binding.diceRollerWaitingCard.waitingCard.visibility == View.VISIBLE &&
                        !isWaitingCardAnimating) {

                        fadeOutWaitingCard()

                    } else {

                        binding.diceRollerWaitingCard.waitingCard.visibility = View.GONE
                        isWaitingCardAnimating = false
                    }
                }
            }

            latestRollLiveData.observe(viewLifecycleOwner) { newRoll ->

                if (newRoll != null){

                    updateUI(newRoll.first,newRoll.second)

                } else {

                    setNullUI()
                }

            }
        }
        // endregion

        // region [ Toolbar ]

        binding.diceRollerToolbar.apply {

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

                    R.id.action_view_history -> {

                        val recentRolls = diceRollerViewModel.getRecentRolls()

                        val dialogRecycler = RecyclerView(requireContext())

                        dialogRecycler.apply{
                            layoutManager = LinearLayoutManager(context)
                            adapter = RollAdapter(recentRolls)
                            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                            isNestedScrollingEnabled = true
                        }

                        val rollsDialog = AlertDialog.Builder(context)
                            .setTitle("Last ${recentRolls.size} roll${
                                if (recentRolls.size != 1) "s" else ""}")
                            .setView(dialogRecycler)
                            .setPositiveButton(context.getString(R.string.action_close)) { dialog, _ ->
                                dialog.dismiss() }
                            .create()

                        rollsDialog.show()

                        true
                    }

                    R.id.action_copy_roll -> {

                        val latestRoll = diceRollerViewModel.latestRollLiveData.value

                        if (latestRoll != null) {
                            copyRollToClipboard(latestRoll.first, latestRoll.second)
                        } else {
                            Toast.makeText(context,"No roll to copy.", Toast.LENGTH_SHORT)
                                .show()
                        }

                        true
                    }

                    else    -> false
                }
            }
        }
        // endregion
    }

    override fun onStart() {
        super.onStart()

        binding.apply {

            // region [ Listeners ]

            diceRollerRollButton.setOnClickListener {

                if (diceRollerDieCountEdit.error == null && diceRollerDieSidesEdit.error == null &&
                    diceRollerDieModEdit.error == null && diceRollerPenetrationUpwardEdit.error == null
                    && diceRollerPenetrationDownwardEdit.error == null) {

                    // region ( Set new button label )
                    var newButtonLabel: String

                    do {
                        newButtonLabel = listOf(
                            "Alea iacta est",
                            "Baby needs a new pair of shoes",
                            "Big bucks, no whammies",
                            "Chuck-a-luck",
                            "Dicequake!",
                            "Escalators, escalators, escalators!",
                            "Hard Eighter from Decatur,\ncounty seat of Wise",
                            "High and winner, got a hot hand",
                            "Hoody hoo!",
                            "I'm feeling lucky",
                            "Keep it rollin'",
                            "Let 'er rip",
                            "Make it the hard way",
                            "Now, do it again",
                            "Pay the price",
                            "Roll dem bones",
                            "Roll the dice",
                            "Yacht, see?"
                        ).random()

                    } while (diceRollerRollButton.text == newButtonLabel)

                    diceRollerRollButton.text = newButtonLabel

                    // endregion

                    //region [ Roll new penetrating roll ]
                    //TODO valid text fields first
                    with(binding) {
                        diceRollerViewModel.rollNewRoll(
                            this.diceRollerDieCountEdit.text.toString().toIntOrNull() ?: 1,
                            this.diceRollerDieSidesEdit.text.toString().toIntOrNull() ?: 2,
                            (this.diceRollerDieModEdit.text.toString().toIntOrNull() ?: 0) *
                                    (if (diceRollerDieModSignButton.text == "-") -1 else 1),
                            this.diceRollerPenetrationUpwardEdit.text.toString().toIntOrNull() ?: 1,
                            this.diceRollerPenetrationDownwardEdit.text.toString().toIntOrNull() ?: 0,
                            this.diceRollerHonorModEdit.text.toString().toIntOrNull() ?: 0,
                            this.diceRollerPenetrationAutoSwitch.isChecked
                        )
                    }

                    // endregion

                } else {


                }

            }

            //TODO implement all text fields with validation below the roll button
            //TODO remember to update left-side die icon upon updating sides field

            diceRollerDieCountEdit.apply{
                addTextChangedListener { input ->

                    val parsedValue = input.toString().toIntOrNull()

                    diceRollerDieCountEdit.error =
                        when {
                            parsedValue == null -> "No value"
                            parsedValue < 1     -> "Too low"
                            parsedValue > 100   -> "Too high"
                            else                -> null
                        }
                }
            }

            diceRollerDieSidesEdit.apply{
                addTextChangedListener { input ->

                    val parsedValue = input.toString().toIntOrNull()

                    if (parsedValue != null) {
                        diceRollerDiceIcon.setImageResource( when (parsedValue){
                            2   -> R.drawable.clipart_coin_vector_icon
                            4   -> R.drawable.clipart_d4_vector_icon
                            6   -> R.drawable.clipart_d6_vector_icon
                            8   -> R.drawable.clipart_d8_vector_icon
                            10, 100, 1000, 10000, 100000  -> R.drawable.clipart_d10_vector_icon
                            12  -> R.drawable.clipart_d12_vector_icon
                            20  -> R.drawable.clipart_d20_vector_icon
                            else-> R.drawable.clipart_dice_cup_vector_icon
                        })
                    }

                    diceRollerDieSidesEdit.error =
                        when {
                            parsedValue == null     -> "No value"
                            parsedValue < 2         -> "Too low"
                            parsedValue > 100000    -> "Too high"
                            else                    -> null
                        }
                }
            }

            diceRollerDieModEdit.apply{
                addTextChangedListener { input ->

                    val parsedValue = input.toString().toIntOrNull()

                    diceRollerDieModEdit.error =
                        when {
                            parsedValue == null -> "No value"
                            parsedValue < 0     -> "Too low"
                            parsedValue > 10000 -> "Too high"
                            else                -> null
                        }
                }
            }

            diceRollerPenetrationUpwardEdit.apply{
                addTextChangedListener { input ->

                    val parsedValue = input.toString().toIntOrNull()

                    val currentSides = diceRollerDieSidesEdit.text.toString().toIntOrNull() ?: 0

                    diceRollerPenetrationUpwardEdit.error =
                        when {
                            parsedValue == null -> "No value"
                            parsedValue < 0     -> "Too low"
                            parsedValue > 100 || parsedValue >= currentSides   -> "Too high"
                            else                -> null
                        }
                }
            }

            diceRollerPenetrationDownwardEdit.apply{
                addTextChangedListener { input ->

                    val parsedValue = input.toString().toIntOrNull()

                    val currentSides = diceRollerDieSidesEdit.text.toString().toIntOrNull() ?: 0

                    diceRollerPenetrationDownwardEdit.error =
                        when {
                            parsedValue == null -> "No value"
                            parsedValue < 0     -> "Too low"
                            parsedValue > 100 || parsedValue >= currentSides   -> "Too high"
                            else                -> null
                        }
                }
            }

            diceRollerDieModSignButton.setOnClickListener {
                if (diceRollerDieModSignButton.text == "-"){
                    diceRollerDieModSignButton.text = "+"
                } else {
                    diceRollerDieModSignButton.text = "-"
                }
            }

            diceRollerAdvancedHeader.setOnClickListener {

                if (diceRollerAdvancedBody.visibility == View.VISIBLE) {
                    diceRollerAdvancedBody.visibility = View.GONE
                    diceRollerAdvancedIndicator.rotation = 0f
                } else {
                    diceRollerAdvancedBody.visibility = View.VISIBLE
                    diceRollerAdvancedIndicator.rotation = 90f
                }
            }

            diceRollerPenetrationInfodot.setOnClickListener {
                AlertDialog.Builder(context).setMessage(R.string.penetration_infodot_message)
                    .setPositiveButton(R.string.ok_affirmative) { dialog, _ ->
                        dialog.dismiss() }
                    .show()
            }

            diceRollerHonorModInfodot.setOnClickListener {
                AlertDialog.Builder(context).setMessage(R.string.honor_infodot_message)
                    .setPositiveButton(R.string.ok_affirmative) { dialog, _ ->
                        dialog.dismiss() }
                    .show()
            }
            // endregion
        }
    }

    // endregion

    // region [ Inner classes ]
    private inner class RollAdapter(val rollList: List<Pair<PenDiceRoll,Long>>)
        : RecyclerView.Adapter<RollAdapter.RollHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RollHolder {
            val binding = DiceRollListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return RollHolder(binding)
        }

        override fun onBindViewHolder(holder: RollHolder, position: Int) {

            val rollPair = rollList[position]

            holder.bind(rollPair)
        }

        override fun getItemCount(): Int = rollList.size

        inner class RollHolder(val binding: DiceRollListItemBinding)
            : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SimpleDateFormat")
            fun bind(newRoll: Pair<PenDiceRoll,Long>){

                val roll = newRoll.first
                val timestamp = newRoll.second

                binding.apply{

                    diceRollIcon.setImageResource( when (roll.numberOfSides) {

                        2   -> R.drawable.clipart_coin_vector_icon
                        4   -> R.drawable.clipart_d4_vector_icon
                        6   -> R.drawable.clipart_d6_vector_icon
                        8   -> R.drawable.clipart_d8_vector_icon
                        10,100,1000,10000,100000  -> R.drawable.clipart_d10_vector_icon
                        12  -> R.drawable.clipart_d12_vector_icon
                        20  -> R.drawable.clipart_d20_vector_icon
                        else-> R.drawable.clipart_dice_cup_vector_icon
                    })

                    "${roll.getDiceDescription()}=".also { diceRollOverviewLabel.text = it }

                    "${NumberFormat.getNumberInstance().format(roll.getRollTotal())}=".also {
                        diceRollOverviewLabel.text = it }

                    (SimpleDateFormat("hh:mm:ss aaa z")
                        .format(timestamp)).also { diceRollOverviewTimestamp.text = it }

                    diceRollClipboard.setOnClickListener {
                        copyRollToClipboard(roll, timestamp)
                    }

                    "Standard ${if (roll.standardRolls.size == 1) "roll" else "rolls"} (${
                        NumberFormat.getNumberInstance().format(roll.standardRolls.sum())
                    } over ${NumberFormat.getNumberInstance().format(roll.standardRolls.size)}" +
                            " ${if (roll.standardRolls.size == 1) "die" else "dice"}):\n".also { diceRollStandardLabel.text = it }

                    val standardBuilder = StringBuilder()
                    roll.standardRolls.forEachIndexed { index, subRoll ->
                        standardBuilder.append(
                            if (index > 0) { if (subRoll >= 0) " + " else " - " } else {""} +
                                    NumberFormat.getNumberInstance().format(subRoll.absoluteValue)
                        )}

                    diceRollStandardValues.text = standardBuilder.toString()

                    ("\nPenetration ${if (roll.extraRolls.size == 1) "roll" else "rolls"} (${
                        NumberFormat.getNumberInstance().format(
                            roll.extraRolls.fold(0) {total, roll -> total + roll - 1 })
                    } before penalty over ${NumberFormat.getNumberInstance().format(roll.extraRolls.size)}" +
                            " ${if (roll.extraRolls.size == 1) "die" else "dice"}):"
                            ).also { diceRollExtraLabel.text = it }

                    val extraBuilder = StringBuilder()
                    if (roll.extraRolls.isNotEmpty()) {
                        roll.extraRolls.forEachIndexed { index, subRoll ->
                            extraBuilder.append(
                                if (index > 0) { if (subRoll >= 0) " + " else " - " } else {""} +
                                        NumberFormat.getNumberInstance().format(subRoll.absoluteValue)
                            )}
                    } else {
                        extraBuilder.append("None")
                    }

                    diceRollExtraValues.text = extraBuilder.toString()

                    diceRollIndicator.setOnClickListener {

                        if (diceRollDetailsGroup.visibility == View.VISIBLE) {

                            diceRollDetailsGroup.visibility = View.GONE
                            diceRollIndicator.rotation = 90f

                        } else {

                            diceRollDetailsGroup.visibility = View.VISIBLE
                            diceRollIndicator.rotation = -90f
                        }
                    }
                }
            }
        }
    }

    // endregion

    // region [ Helper functions ]

    @SuppressLint("SimpleDateFormat")
    private fun updateUI(latestRoll: PenDiceRoll, timestamp: Long) {

         binding.apply{

             "Result (${latestRoll.getDiceDescription()}):".also{
                 diceRollerResultTopLabel.text = it
             }

             diceRollerResultTotal.text =
                 NumberFormat.getNumberInstance().format(latestRoll.getRollTotal())

             ("Standard total (${
                 NumberFormat.getNumberInstance().format(latestRoll.standardRolls.sum())
             } over ${NumberFormat.getNumberInstance().format(latestRoll.standardRolls.size)}" +
                     " ${if (latestRoll.standardRolls.size == 1) "roll" else "rolls"}):"
                     ).also { diceRollerResultStandardLabel.text = it }

             latestRoll.standardRolls.joinToString(", ", limit = 200).also{
                 diceRollerResultStandardValues.text = it
             }

             ("Penetration total (${
                 NumberFormat.getNumberInstance().format(
                     latestRoll.extraRolls.fold(0) {total, roll -> total + roll - 1 })
             } before penalty over ${NumberFormat.getNumberInstance().format(latestRoll.extraRolls.size)}" +
                     " ${if (latestRoll.extraRolls.size == 1) "roll" else "rolls"}):"
                     ).also { diceRollerResultExtraLabel.text = it }

             (if (latestRoll.extraRolls.isNotEmpty()) {
                 latestRoll.extraRolls.joinToString(", ", limit = 200)
             } else { "none" }).also{
                 diceRollerResultExtraValues.text = it
             }

             ("Rolled at " + SimpleDateFormat("hh:mm:ss aaa z")
                 .format(timestamp)).also { diceRollerResultTimestamp.apply{
                 text = it
                 visibility = View.VISIBLE
             } }
         }
    }

    private fun setNullUI() {
        binding.apply{

            "Result:".also { diceRollerResultTopLabel.text = it }

            diceRollerResultTotal.text = "?"

            "Standard total:".also { diceRollerResultStandardLabel.text = it }

            "none".also { diceRollerResultStandardValues.text = it }

            "Penetration total:".also { diceRollerResultStandardLabel.text = it }

            "none".also { diceRollerResultExtraValues.text = it }

            diceRollerResultTimestamp.visibility = View.GONE
        }
    }

    private fun copyRollToClipboard(roll: PenDiceRoll, timestamp: Long) {

        @SuppressLint("SimpleDateFormat")
        fun getRollAsClipboardText(): String {

            val result = StringBuilder()

            result.append(roll.getDiceDescription() + "=\n")

            result.append(roll.getRollTotal())

            result.append("\nStandard ${if (roll.standardRolls.size == 1) "roll" else "rolls"} (${
                NumberFormat.getNumberInstance().format(roll.standardRolls.sum())
            } over ${NumberFormat.getNumberInstance().format(roll.standardRolls.size)}" +
                    " ${if (roll.standardRolls.size == 1) "die" else "dice"})\n"
            )

            result.append("=" + roll.standardRolls.forEachIndexed { index, subRoll ->
                if (index > 0) { if (subRoll >= 0) " + " else " - " } else { if (index < 0) "-" else "" } +
                        NumberFormat.getNumberInstance().format(subRoll.absoluteValue)})

            if (roll.extraRolls.isNotEmpty()) {
                result.append("\nPenetration ${if (roll.extraRolls.size == 1) "roll" else "rolls"} (${
                    NumberFormat.getNumberInstance().format(
                        roll.extraRolls.fold(0) {total, roll -> total + roll - 1 })
                } before penalty over ${NumberFormat.getNumberInstance().format(roll.extraRolls.size)}" +
                        " ${if (roll.extraRolls.size == 1) "die" else "dice"})"
                        )
            }

            result.append("=" + roll.extraRolls.forEachIndexed { index, subRoll ->
                if (index > 0) { if (subRoll >= 0) " + " else " - " } else { if (index < 0) "-" else "" } +
                        NumberFormat.getNumberInstance().format(subRoll.absoluteValue)})

            if (roll.honorModifier != 0) { result.append("\nTotal honor modifier: ${
                roll.honorModifier * (roll.standardRolls.size + roll.extraRolls.size)}") }

            result.append("\nRolled at " + SimpleDateFormat("hh:mm:ss aaa z"))

            return result.toString()
        }

        val textToCopy = getRollAsClipboardText()
        val clipboardManager = requireContext().getSystemService(ClipboardManager::class.java)
        val clipData = ClipData.newPlainText("text", textToCopy)

        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Roll copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun fadeOutWaitingCard() {

        isWaitingCardAnimating = true

        binding.diceRollerWaitingCard.waitingCard.apply {
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

        binding.diceRollerWaitingCard.waitingCard.apply {
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

    // endregion
    //TODO implement all of the fragment, the history dialog, the alert dialogs, and the viewmodel

    //TODO also, implement "refresh template tables" in Hoard List
}