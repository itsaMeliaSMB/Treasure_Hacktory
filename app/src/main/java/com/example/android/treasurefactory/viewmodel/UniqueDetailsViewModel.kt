package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.treasurefactory.repository.HMRepository

class UniqueDetailsViewModel(private val repository: HMRepository) : ViewModel() {

}

class UniqueDetailsViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniqueDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniqueDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}