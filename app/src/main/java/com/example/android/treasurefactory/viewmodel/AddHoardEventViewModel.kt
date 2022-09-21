package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.launch

class AddHoardEventViewModel(private val repository: HMRepository): ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    fun loadHoard(hoardID: Int){
        hoardIDLiveData.value = hoardID
    }

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