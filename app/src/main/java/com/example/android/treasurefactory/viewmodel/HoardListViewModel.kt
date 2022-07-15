package com.example.android.treasurefactory.viewmodel

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

            repository.deleteAllHoardsAndItems()

            delay(1000L)

            setRunningAsync(false)
        }
    }

    // region [ Helper functions ]

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    fun setSelectedPos(adapterPos: Int, newValue: Boolean) {

        if (adapterPos in 0..hoardListLiveData.value!!.size) {}
    }
    // endregion
}

//TODO left off "here". Still need to pick palette for main app and selection mode. If I can't add
// a dark theme easily tomorrow, we're skipping it for launch build. Finish adding color, theme,
// and menu values tomorrow and complete the implementation of actionmode menu on HoardListFragment.
// Skip checking spell gen for Spec.Quant. for now as well; just comment out unimplemented options.

class HoardListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}