package com.example.android.treasurefactory.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HoardListViewModel(private val repository: HMRepository) : ViewModel() {

    private var isRunningAsync = false

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val hoardListLiveData = repository.getHoards()

    fun deleteAllHoards() {

        viewModelScope.launch {

            setRunningAsync(true)

            repository.deleteAllHoards()

            delay(1000L)

            setRunningAsync(false)
        }
    }

    fun waitThreeSeconds() {

        Log.d("HoardListViewModel","waitThreeSeconds() called.")

        viewModelScope.launch {

            Log.d("HoardListViewModel","viewModelScope launched.")

            setRunningAsync(true)

            delay(3000L)

            setRunningAsync(false)

            Log.d("HoardListViewModel","viewModelScope ending...")
        }
    }

    // region [ Helper functions ]

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }
    // endregion
}

class HoardListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}