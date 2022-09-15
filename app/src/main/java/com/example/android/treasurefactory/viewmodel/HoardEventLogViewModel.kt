package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.repository.HMRepository

class HoardEventLogViewModel(private val repository: HMRepository) : ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    var eventsLiveData: LiveData<List<HoardEvent>> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoardEvents(hoardID)
    }

    var hoardNameLiveData: LiveData<String> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoardName(hoardID)
    }

    // region [ Helper functions ]

    fun updateHoardID(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }
    // endregion
}

class HoardEventLogViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardEventLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardEventLogViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}