package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogCoinageEditBinding
import com.example.android.treasurefactory.model.CoinType
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.CoinageEditViewModel
import com.example.android.treasurefactory.viewmodel.CoinageEditViewModelFactory
import com.example.android.treasurefactory.viewmodel.MAXIMUM_HOARD_VALUE
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class CoinageEditDialog(): DialogFragment() {

    private lateinit var activeHoard: Hoard

    private val safeArgs : CoinageEditDialogArgs by navArgs()

    private val coinageEditViewModel: CoinageEditViewModel by viewModels {
        CoinageEditViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    private var _binding: DialogCoinageEditBinding? = null
    private val binding get() = _binding!!

    // region [ Overridden functions ]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = safeArgs.activeHoardID

        coinageEditViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogCoinageEditBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observers
        coinageEditViewModel.apply {
            hoardLiveData.observe(viewLifecycleOwner) { hoard ->

                hoard?.let {

                    activeHoard = hoard

                    setInitialUI()
                }
            }

            newCpLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.CP)
            }
            newSpLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.SP)
            }
            newEpLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.EP)
            }
            newGpLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.GP)
            }
            newHspLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.HSP)
            }
            newPpLiveData.observe(viewLifecycleOwner) {
                updateUI(CoinType.PP)
            }
        }

        // Listeners
        binding.apply{
            coinageEditCpQtyEdit.addTextChangedListener { input ->

                coinageEditCpQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.CP,
                        input.toString())
            }
            coinageEditSpQtyEdit.addTextChangedListener { input ->

                coinageEditSpQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.SP,
                        input.toString())
            }
            coinageEditEpQtyEdit.addTextChangedListener { input ->

                coinageEditEpQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.EP,
                        input.toString())
            }
            coinageEditGpQtyEdit.addTextChangedListener { input ->

                coinageEditGpQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.GP,
                        input.toString())
            }
            coinageEditHspQtyEdit.addTextChangedListener { input ->

                coinageEditHspQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.HSP,
                        input.toString())
            }
            coinageEditPpQtyEdit.addTextChangedListener { input ->

                coinageEditPpQtyEdit.error =
                    coinageEditViewModel.setValueFromEditText(CoinType.PP,
                        input.toString())
            }
        }

        // Toolbar
        binding.hoardOverviewToolbar.apply{
            inflateMenu(R.menu.coinage_toolbar_menu)
            title = getString(R.string.edit_coinage_values)
            navigationIcon = AppCompatResources.getDrawable(context,R.drawable.clipart_close_vector_icon)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_save_coinage -> {

                        if (validateNewValues()) {

                            val newHoardGPTotal = activeHoard.gpTotal -
                                    activeHoard.getTotalCoinageValue() +
                                    coinageEditViewModel.getCoinTotalValue()

                            coinageEditViewModel.updateHoard(
                                activeHoard.copy(
                                    gpTotal = newHoardGPTotal,
                                    cp = coinageEditViewModel.newCpLiveData.value!!,
                                    sp = coinageEditViewModel.newSpLiveData.value!!,
                                    ep = coinageEditViewModel.newEpLiveData.value!!,
                                    gp = coinageEditViewModel.newGpLiveData.value!!,
                                    hsp = coinageEditViewModel.newHspLiveData.value!!,
                                    pp = coinageEditViewModel.newPpLiveData.value!!),
                                activeHoard,
                                binding.coinageEditMemoEdit.text?.trim()?.take(50).toString()
                            )

                            Toast.makeText(context,R.string.coinage_updated,Toast.LENGTH_SHORT).show()

                            findNavController().popBackStack()

                        } else {

                            Toast.makeText(context,R.string.cannot_change_coinage_error,Toast.LENGTH_SHORT).show()
                        }

                        true
                    }
                    else -> false
                }
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

    // region [ Helper functions ]

    private fun setInitialUI() {

        Log.d("setInitialUI()", "Initializing UI elements.")
        binding.apply{


            val totalCoinQty = activeHoard.cp + activeHoard.sp + activeHoard.ep +
                    activeHoard.gp + activeHoard.hsp + activeHoard.pp

            Log.d("setInitialUI()", "Setting current coinage quantities")
            coinageEditCpLabel.text = activeHoard.cp.formatWithCommas()
            coinageEditSpLabel.text = activeHoard.sp.formatWithCommas()
            coinageEditEpLabel.text = activeHoard.ep.formatWithCommas()
            coinageEditGpLabel.text = activeHoard.gp.formatWithCommas()
            coinageEditHspLabel.text = activeHoard.hsp.formatWithCommas()
            coinageEditPpLabel.text = activeHoard.pp.formatWithCommas()
            coinageEditTotalLabel.text = totalCoinQty.formatWithCommas()

            Log.d("setInitialUI()", "Setting current/new coinage value totals")
            ((activeHoard.cp * 0.01 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditCpCurrentGpValue.text = it
                    coinageEditCpNewGpValue.text = it
                }
            ((activeHoard.sp * 0.1 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditSpCurrentGpValue.text = it
                    coinageEditSpNewGpValue.text = it
                }
            ((activeHoard.ep * 0.5 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditEpCurrentGpValue.text = it
                    coinageEditEpNewGpValue.text = it
                }
            ((activeHoard.gp * 1.0 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditGpCurrentGpValue.text = it
                    coinageEditGpNewGpValue.text = it
                }
            ((activeHoard.hsp * 2.0 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditHspCurrentGpValue.text = it
                    coinageEditHspNewGpValue.text = it
                }
            ((activeHoard.pp * 5.0 * 100.00).roundToInt() / 100.00).formatAsGp()
                .also {
                    coinageEditPpCurrentGpValue.text = it
                    coinageEditPpNewGpValue.text = it
                }
            activeHoard.getTotalCoinageValue().formatAsGp()
                .also {
                    coinageEditTotalCurrentTotalValue.text = it
                    coinageEditTotalCurrentTotalValue.text = it
                }

            Log.d("setInitialUI()", "Setting new coinage quantities in text inputs")
            coinageEditCpQtyEdit.setText(activeHoard.cp.toString())
            coinageEditSpQtyEdit.setText(activeHoard.sp.toString())
            coinageEditEpQtyEdit.setText(activeHoard.ep.toString())
            coinageEditGpQtyEdit.setText(activeHoard.gp.toString())
            coinageEditHspQtyEdit.setText(activeHoard.hsp.toString())
            coinageEditPpQtyEdit.setText(activeHoard.pp.toString())

            // Initial difference label values
            val coinsString = resources.getQuantityString(R.plurals.coinString,0)
            val diffString = "± 0 gp\n(0 $coinsString)"
            val diffTint = resources.getColor(R.color.gray, context?.theme)

            coinageEditCpDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditSpDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditEpDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditGpDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditHspDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditPpDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
            coinageEditTotalDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }
        }
        Log.d("setInitialUI()", "Initial UI elements set.")
    }

    private fun updateUI(coinType: CoinType) {

        val coinQty : Int
        val coinValue : Double
        val qtyDiff : Int
        val valueDiff : Double
        val diffString : String
        val coinsString : String
        @ColorInt
        val diffTint : Int

        Log.d("updateUI(CoinType.${coinType.name})","Updating UI for ${coinType.longName}")

        when (coinType){

            CoinType.CP -> {

                coinQty = coinageEditViewModel.newCpLiveData.value!!
                coinValue = coinQty * 0.01
                qtyDiff = coinQty - activeHoard.cp
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 0.01

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditCpNewGpValue.text = coinValue.formatAsGp()
                    coinageEditCpDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }

            CoinType.SP -> {

                coinQty = coinageEditViewModel.newSpLiveData.value!!
                coinValue = coinQty * 0.1
                qtyDiff = coinQty - activeHoard.sp
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 0.1

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditSpNewGpValue.text = coinValue.formatAsGp()
                    coinageEditSpDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }

            CoinType.EP -> {

                coinQty = coinageEditViewModel.newEpLiveData.value!!
                coinValue = coinQty * 0.5
                qtyDiff = coinQty - activeHoard.ep
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 0.5

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditEpNewGpValue.text = coinValue.formatAsGp()
                    coinageEditEpDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }

            CoinType.GP -> {

                coinQty = coinageEditViewModel.newGpLiveData.value!!
                coinValue = coinQty * 1.0
                qtyDiff = coinQty - activeHoard.gp
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 1.0

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditGpNewGpValue.text = coinValue.formatAsGp()
                    coinageEditGpDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }

            CoinType.HSP -> {

                coinQty = coinageEditViewModel.newHspLiveData.value!!
                coinValue = coinQty * 2.0
                qtyDiff = coinQty - activeHoard.hsp
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 2.0

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditHspNewGpValue.text = coinValue.formatAsGp()
                    coinageEditHspDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }

            CoinType.PP -> {

                coinQty = coinageEditViewModel.newPpLiveData.value!!
                coinValue = coinQty * 5.0
                qtyDiff = coinQty - activeHoard.pp
                coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
                valueDiff = qtyDiff * 5.0

                when {
                    (qtyDiff > 0)   -> {
                        diffString = "+ ${valueDiff.formatAsGp()}\n" +
                                "(${qtyDiff.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.emerald, this.context?.theme)
                    }
                    (qtyDiff < 0)   -> {
                        diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                                "(${qtyDiff.absoluteValue.formatWithCommas()} $coinsString)"
                        diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
                    }
                    else            -> {
                        diffString = "± 0 gp\n(0 $coinsString)"
                        diffTint = resources.getColor(R.color.gray, this.context?.theme)
                    }
                }

                binding.apply{
                    coinageEditPpNewGpValue.text = coinValue.formatAsGp()
                    coinageEditPpDifference.apply{
                        text = diffString
                        setTextColor(diffTint)
                    }
                }
            }
        }

        updateTotals()
    }

    private fun updateTotals() {

        // TODO Left off here. Coinage value functionality almost fully implemented.
        //  Totals add up incorrectly, however. Go over math again, test the hoard event addition,
        //  and add "add user event" functionality. This should be doable in one day, if you stay
        //  on it.

        val coinTotalQty = coinageEditViewModel.getCoinTotalQty()
        val coinTotalValue = coinageEditViewModel.getCoinTotalValue()
        val qtyDiff = coinTotalQty - (activeHoard.cp + activeHoard.sp + activeHoard.ep + activeHoard.gp +
                activeHoard.hsp + activeHoard.pp)
        val valueDiff = coinTotalValue - activeHoard.getTotalCoinageValue()
        val coinsString = resources.getQuantityString(R.plurals.coinString,qtyDiff)
        val diffString : String
        @ColorInt
        val diffTint : Int

        when {
            (valueDiff > 0.0)   -> {
                diffString = "+ ${valueDiff.formatAsGp()}\n" +
                        "(${qtyDiff.formatWithCommas()} $coinsString)"
                diffTint = resources.getColor(R.color.emerald, this.context?.theme)
            }
            (valueDiff < 0.0)   -> {
                diffString = "- ${valueDiff.absoluteValue.formatAsGp()}\n" +
                        "(${qtyDiff.formatWithCommas()} $coinsString)"
                diffTint = resources.getColor(R.color.scarlet, this.context?.theme)
            }
            else            -> {
                diffString = "± 0 gp\n(0 $coinsString)"
                diffTint = resources.getColor(R.color.gray, this.context?.theme)
            }
        }

        Log.d("updateTotals()","coinTotalQty = $coinTotalQty; coinTotalValue = " +
                "$coinTotalValue;\nqtyDiff = coinTotalQty($coinTotalQty) - ( " +
                "activeHoard.cp(${activeHoard.cp}) + activeHoard.sp(${activeHoard.sp}) + " +
                "activeHoard.ep(${activeHoard.ep}) + activeHoard.gp(${activeHoard.gp}) + " +
                "activeHoard.hsp(${activeHoard.hsp}) + activeHoard.pp(${activeHoard.pp}) [${
                    activeHoard.cp + activeHoard.sp + activeHoard.ep + activeHoard.gp +
                            activeHoard.hsp + activeHoard.pp
                }]) = $qtyDiff;\nvalueDiff = coinTotalValue($coinTotalValue) - " +
                "activeHoard.getTotalCoinageValue()(${activeHoard.getTotalCoinageValue()}) = " +
                "$valueDiff;\ndiffString = {\n$diffString\n}")

        binding.apply{
            coinageEditTotalNewQty.text = coinTotalQty.formatWithCommas()
            coinageEditTotalNewTotalValue.text = coinTotalValue.formatAsGp()
            coinageEditTotalDifference.apply{
                text = diffString
                setTextColor(diffTint)
            }

            if (activeHoard.gpTotal - activeHoard.getTotalCoinageValue() + coinTotalValue >
                MAXIMUM_HOARD_VALUE) {

                coinageEditTotalNewTotalIcon.isEnabled = false
                coinageEditTotalNewTotalValue.isEnabled = false
                binding.coinageEditTotalError.visibility = View.VISIBLE
            } else {
                coinageEditTotalNewTotalIcon.isEnabled = true
                coinageEditTotalNewTotalValue.isEnabled = true
                binding.coinageEditTotalError.visibility = View.INVISIBLE
            }
        }
    }

    private fun validateNewValues() : Boolean {

        return (binding.coinageEditCpQtyEdit.error == null &&
                binding.coinageEditSpQtyEdit.error == null &&
                binding.coinageEditEpQtyEdit.error == null &&
                binding.coinageEditGpQtyEdit.error == null &&
                binding.coinageEditHspQtyEdit.error == null &&
                binding.coinageEditPpQtyEdit.error == null &&
                binding.coinageEditTotalError.visibility != View.VISIBLE)
    }

    private fun Double.formatAsGp() : String = "${DecimalFormat("#,##0.0#")
            .format(this)
            .removeSuffix(".0")} gp"

    private fun Int.formatWithCommas() : String = NumberFormat.getNumberInstance().format(this)

    // endregion
}