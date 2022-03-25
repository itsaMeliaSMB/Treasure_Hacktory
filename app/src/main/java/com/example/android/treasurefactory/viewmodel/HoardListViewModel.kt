package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.treasurefactory.repository.HMRepository

class HoardListViewModel(private val repository: HMRepository) : ViewModel() {

    val hoardListLiveData = repository.getHoards()
}

class HoardListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}