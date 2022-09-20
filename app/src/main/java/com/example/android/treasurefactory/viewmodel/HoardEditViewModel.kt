package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.launch

class HoardEditViewModel(private val repository: HMRepository): ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {

        viewModelScope.launch { repository.updateHoard(hoard) }
    }
}

class HoardEditViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardEditViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}