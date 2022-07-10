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

class HoardListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}