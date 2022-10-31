package com.example.android.treasurefactory.viewmodel

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
                        "${(newHoard.cp - oldHoard.cp).asGpValue(CoinType.CP)} gp change)")
            }
            if (newHoard.sp != oldHoard.sp) {
                builder.append("\n\t${oldHoard.sp.formatWithCommas()} sp -> " +
                        "${newHoard.sp.formatWithCommas()} sp " +
                        "(${(newHoard.sp - oldHoard.sp).formatWithCommas()} sp / " +
                        "${(newHoard.sp - oldHoard.sp).asGpValue(CoinType.SP)} gp change)")
            }
            if (newHoard.ep != oldHoard.ep) {
                builder.append("\n\t${oldHoard.ep.formatWithCommas()} ep -> " +
                        "${newHoard.ep.formatWithCommas()} ep " +
                        "(${(newHoard.ep - oldHoard.ep).formatWithCommas()} ep / " +
                        "${(newHoard.ep - oldHoard.ep).asGpValue(CoinType.EP)} gp change)")
            }
            if (newHoard.gp != oldHoard.gp) {
                builder.append("\n\t${oldHoard.gp.formatWithCommas()} gp -> " +
                        "${newHoard.gp.formatWithCommas()} gp " +
                        "(${(newHoard.gp - oldHoard.gp).asGpValue(CoinType.GP)} gp change)")
            }
            if (newHoard.hsp != oldHoard.hsp) {
                builder.append("\n\t${oldHoard.hsp.formatWithCommas()} hsp -> " +
                        "${newHoard.hsp.formatWithCommas()} hsp " +
                        "(${(newHoard.hsp - oldHoard.hsp).formatWithCommas()} hsp / " +
                        "${(newHoard.hsp - oldHoard.hsp).asGpValue(CoinType.HSP)} gp change)")
            }
            if (newHoard.pp != oldHoard.pp) {
                builder.append("\n\t${oldHoard.pp.formatWithCommas()} pp -> " +
                        "${newHoard.pp.formatWithCommas()} pp " +
                        "(${(newHoard.pp - oldHoard.pp).formatWithCommas()} pp / " +
                        "${(newHoard.pp - oldHoard.pp).asGpValue(CoinType.PP)} gp change)")
            }

            return if (builder.isNotEmpty()) {

                if (memo.isNotBlank()) {
                    builder.append("\nMEMO:\n\t$memo")
                }

                "Coinage values updated: $builder"

            } else {
                null
            }
        }

        viewModelScope.launch {

            val coinageChangeString = getCoinageChangeString()

            if (coinageChangeString != null) {

                val coinageChangeEvent = HoardEvent(
                    hoardID = hoardIDLiveData.value!!,
                    timestamp = System.currentTimeMillis(),
                    description = coinageChangeString,
                    tag = "modification|coinage" + if (memo.isNotBlank()) "|note" else ""
                )

                repository.addHoardEvent(coinageChangeEvent)
                repository.updateHoard(newHoard)
            }
        }
    }

    // region [ Helper functions ]
    fun getCoinTotalQty(): Int {

        val cpQty = newCpLiveData.value!!
        val spQty = newSpLiveData.value!!
        val epQty = newEpLiveData.value!!
        val gpQty = newGpLiveData.value!!
        val hspQty = newHspLiveData.value!!
        val ppQty = newPpLiveData.value!!

        return cpQty + spQty + epQty + gpQty + hspQty + ppQty
    }

    fun getCoinTotalValue() : Double{

        val cpValue = newCpLiveData.value!!.asGpValue(CoinType.CP)
        val spValue = newSpLiveData.value!!.asGpValue(CoinType.SP)
        val epValue = newEpLiveData.value!!.asGpValue(CoinType.EP)
        val gpValue = newGpLiveData.value!!.asGpValue(CoinType.GP)
        val hspValue = newHspLiveData.value!!.asGpValue(CoinType.HSP)
        val ppValue = newPpLiveData.value!!.asGpValue(CoinType.PP)

        val valueTotal = cpValue + spValue + epValue + gpValue + hspValue + ppValue

        return valueTotal
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