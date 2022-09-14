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
    // endregion

    // region [ Functions ]

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    /**
     * Returns the total gp value of the Gems, Art Objects, Magic Items, and Spell Collections of
     * the matching the provided hoardID.
     */
    fun getTotalItemValues(hoardID: Int) : DoubleArray {

        val totalsArray = DoubleArray(4)

        viewModelScope.launch {
            totalsArray[0] = repository.getGemValueTotalOnce(hoardID)
            totalsArray[1] = repository.getArtValueTotalOnce(hoardID)
            totalsArray[2] = repository.getMagicItemValueTotalOnce(hoardID)
            totalsArray[3] = repository.getSpellCollectionValueTotalOnce(hoardID)
        }

        return totalsArray
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