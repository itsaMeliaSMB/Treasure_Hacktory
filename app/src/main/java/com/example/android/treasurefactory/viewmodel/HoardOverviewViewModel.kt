package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.launch

class HoardOverviewViewModel(private val repository: HMRepository): ViewModel() {

    // region << Values, variables, and containers >>

    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    var gemValueLiveData = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getGemValueTotal(hoardID)
    }
    var artValueLiveData = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getArtValueTotal(hoardID)
    }
    var magicValueLiveData = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getMagicItemValueTotal(hoardID)
    }
    var spellValueLiveData = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getSpellCollectionValueTotal(hoardID)
    }
    // endregion

    // region [ Functions ]

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {

        viewModelScope.launch { repository.updateHoard(hoard) }
    }
    // endregion
}

class HoardOverviewViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardOverviewViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}