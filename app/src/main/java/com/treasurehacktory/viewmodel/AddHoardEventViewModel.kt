package com.treasurehacktory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.treasurehacktory.model.HoardEvent
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.launch

class AddHoardEventViewModel(private val repository: HMRepository): ViewModel() {

    fun saveEvent(event: HoardEvent) {

        viewModelScope.launch { repository.addHoardEvent(event) }
    }
}

class AddHoardEventViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddHoardEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddHoardEventViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}