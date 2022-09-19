package com.example.android.treasurefactory.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.android.treasurefactory.model.CoinType
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import kotlin.math.roundToInt

class CoinageEditViewModel(private val repository: HMRepository): ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    val newCpLiveData = MutableLiveData(0)
    val newSpLiveData = MutableLiveData(0)
    val newEpLiveData = MutableLiveData(0)
    val newGpLiveData = MutableLiveData(0)
    val newHspLiveData = MutableLiveData(0)
    val newPpLiveData = MutableLiveData(0)

    val coinTotalMediator = MediatorLiveData<Pair<Int,Double>>().apply{
        value = 0 to 0.0
        addSource(newCpLiveData) { value = combineCoinTotals() }
        addSource(newSpLiveData) { value = combineCoinTotals() }
        addSource(newEpLiveData) { value = combineCoinTotals() }
        addSource(newGpLiveData) { value = combineCoinTotals() }
        addSource(newHspLiveData) { value = combineCoinTotals() }
        addSource(newPpLiveData) { value = combineCoinTotals() }
    }

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun updateHoard(newHoard: Hoard, oldHoard: Hoard, memo : String = "") {

        fun getCoinageChangeString() : String? {

            val builder = StringBuilder()

            if (newHoard.cp != oldHoard.cp) {
                builder.append("\n\t${oldHoard.cp.formatWithCommas()} cp -> " +
                        "${newHoard.cp.formatWithCommas()} cp " +
                        "(${(newHoard.cp - oldHoard.cp).formatWithCommas()} cp / " +
                        "${(newHoard.cp - oldHoard.cp).asGpValue(CoinType.CP)} change)")
            }
            if (newHoard.sp != oldHoard.sp) {
                builder.append("\n\t${oldHoard.sp.formatWithCommas()} sp -> " +
                        "${newHoard.sp.formatWithCommas()} sp " +
                        "(${(newHoard.sp - oldHoard.sp).formatWithCommas()} sp / " +
                        "${(newHoard.sp - oldHoard.sp).asGpValue(CoinType.SP)} change)")
            }
            if (newHoard.ep != oldHoard.sp) {
                builder.append("\n\t${oldHoard.ep.formatWithCommas()} ep -> " +
                        "${newHoard.ep.formatWithCommas()} ep " +
                        "(${(newHoard.ep - oldHoard.ep).formatWithCommas()} ep / " +
                        "${(newHoard.ep - oldHoard.ep).asGpValue(CoinType.EP)} change)")
            }
            if (newHoard.gp != oldHoard.gp) {
                builder.append("\n\t${oldHoard.gp.formatWithCommas()} gp -> " +
                        "${newHoard.gp.formatWithCommas()} gp " +
                        "(${(newHoard.gp - oldHoard.gp).asGpValue(CoinType.GP)} change)")
            }
            if (newHoard.hsp != oldHoard.hsp) {
                builder.append("\n\t${oldHoard.hsp.formatWithCommas()} hsp -> " +
                        "${newHoard.hsp.formatWithCommas()} hsp " +
                        "(${(newHoard.hsp - oldHoard.hsp).formatWithCommas()} hsp / " +
                        "${(newHoard.hsp - oldHoard.hsp).asGpValue(CoinType.HSP)} change)")
            }
            if (newHoard.pp != oldHoard.pp) {
                builder.append("\n\t${oldHoard.pp.formatWithCommas()} ep -> " +
                        "${newHoard.pp.formatWithCommas()} ep " +
                        "(${(newHoard.pp - oldHoard.pp).formatWithCommas()} ep / " +
                        "${(newHoard.pp - oldHoard.pp).asGpValue(CoinType.PP)} change)")
            }

            return if (builder.isNotEmpty()) {

                builder.append("\nMEMO:\n\t$memo")

                "Coinage values updated: $builder"
            } else {
                null
            }
        }

        viewModelScope.launch {

            val coinageChangeString = getCoinageChangeString()

            if (coinageChangeString != null) {

                val coinageChangeEvent = HoardEvent(
                    eventID = 0,
                    hoardID = newHoard.hoardID,
                    timestamp = System.currentTimeMillis(),
                    description = coinageChangeString,
                    tag = "modification|coinage" + if (memo.isNotBlank()) "|note" else ""
                )

                repository.updateHoard(newHoard)
                repository.addHoardEvent(coinageChangeEvent)
            }
        }
    }

    // region [ Helper functions ]
    fun combineCoinTotals() : Pair<Int,Double>{

        val cpQty = newCpLiveData.value!!
        val spQty = newSpLiveData.value!!
        val epQty = newSpLiveData.value!!
        val gpQty = newGpLiveData.value!!
        val hspQty = newHspLiveData.value!!
        val ppQty = newPpLiveData.value!!

        val qtyTotal = cpQty + spQty + epQty + gpQty + hspQty + ppQty
        val valueTotal = ( (cpQty * 0.01) + (spQty * 0.1) + (epQty * 0.5) + (gpQty * 1.0) +
                (hspQty * 2.0) + (ppQty * 5.0) * 100.00).roundToInt() / 100.00

        return qtyTotal to valueTotal
    }

    fun setValueFromEditText(coinType: CoinType, capturedString: String) : String? {

        var errorString : String? = null
        val parsedValue = capturedString.trim().toIntOrNull()

        if (parsedValue != null) {

            val coinValue = parsedValue.asGpValue(coinType)

            when (coinType) {

                CoinType.CP  -> if (parsedValue != newCpLiveData.value) {

                    newCpLiveData.value = parsedValue
                }

                CoinType.SP  -> if (parsedValue != newSpLiveData.value) {

                    newSpLiveData.value = parsedValue
                }

                CoinType.EP  -> if (parsedValue != newEpLiveData.value) {

                    newEpLiveData.value = parsedValue
                }

                CoinType.GP  -> if (parsedValue != newGpLiveData.value) {

                    newGpLiveData.value = parsedValue
                }

                CoinType.HSP  -> if (parsedValue != newHspLiveData.value) {

                    newHspLiveData.value = parsedValue
                }

                CoinType.PP  -> if (parsedValue != newPpLiveData.value) {

                    newPpLiveData.value = parsedValue
                }
            }

            errorString = when {

                (parsedValue < 0)    -> "Cannot be negative"
                (coinValue > MAXIMUM_COINAGE_AMOUNT)-> "Exceeds maximum gp value"
                else -> errorString
            }

        } else {

            Log.e("setValueFromEditText | validateAsInt",
                "No parsable integer in string. No value changed.")
        }

        return errorString

    }

    private fun Int.asGpValue(coinType: CoinType) : Double {
        return (this * coinType.gpValue * 100.00).roundToInt() / 100.00
    }

    private fun Int.formatWithCommas() : String = NumberFormat.getNumberInstance().format(this)
    // endregion

}

class CoinageEditViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoinageEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoinageEditViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}