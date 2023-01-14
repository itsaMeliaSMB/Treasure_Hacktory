package com.treasurehacktory.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.treasurehacktory.PenDiceRoll
import com.treasurehacktory.rollPenetratingDice
import kotlinx.coroutines.launch

class DiceRollerViewModel : ViewModel() {

    //TODO add livedata for observable roll pairs

    private var isRunningAsync = false
    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val latestRollLiveData = MutableLiveData<Pair<PenDiceRoll,Long>?>(null)

    private val recentRolls = mutableListOf<Pair<PenDiceRoll,Long>>()

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    fun getRecentRolls() = recentRolls.toList()

    fun rollNewRoll(_numberOfDice: Int, _numberOfSides: Int, dieModifier: Int,
                    _highThreshold: Int, _lowThreshold: Int, honorModifier: Int,
                            willAutoPenetrate: Boolean) {

        val currentTime = System.currentTimeMillis()

        setRunningAsync(true)

        viewModelScope.launch {
            val numberOfDice = _numberOfDice.coerceIn(1,100)
            val numberOfSides = _numberOfSides.coerceIn(2,100000)
            val highThreshold = _highThreshold.coerceIn(0,numberOfSides - 1)
            val lowThreshold = _lowThreshold.coerceIn(0,numberOfSides - highThreshold)

            val newRoll = rollPenetratingDice(numberOfDice, numberOfSides, dieModifier,
                highThreshold, lowThreshold, honorModifier, willAutoPenetrate)

            recentRolls.add(0,newRoll to currentTime)
            while(recentRolls.size > 100) { recentRolls.removeLast() }

            latestRollLiveData.postValue(newRoll to currentTime)
        }

        setRunningAsync(false)
    }

}

class DiceRollerViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiceRollerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiceRollerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}