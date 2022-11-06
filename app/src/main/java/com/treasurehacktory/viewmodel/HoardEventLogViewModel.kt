package com.treasurehacktory.viewmodel

import androidx.lifecycle.*
import com.treasurehacktory.model.HoardEvent
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.launch

class HoardEventLogViewModel(private val repository: HMRepository) : ViewModel() {

    val hoardIDLiveData = MutableLiveData<Int>()

    var eventsLiveData = MutableLiveData<List<HoardEvent>>()

    var hoardNameLiveData: LiveData<String> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoardName(hoardID)
    }

    // region [ Helper functions ]

    fun updateHoardID(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun updateEventsNormally(hoardID: Int) {
        viewModelScope.launch{
            eventsLiveData.postValue(repository.getHoardEventsOnce(hoardID))
        }
    }

    fun updateEventsWithFilters(includedTags: List<String>, excludedTags: List<String>,
                                        hoardID: Int) {
        viewModelScope.launch{

            val allEvents = repository.getHoardEventsOnce(hoardID)
            val validEvents = ArrayList<HoardEvent>()

            allEvents.forEach { event ->
                val splitTags = event.tag.split("|")
                if ((includedTags.isEmpty() || splitTags.containsAll(includedTags)) &&
                    (excludedTags.isEmpty() || splitTags.any { excludedTags.contains(it) }.not())) {

                    validEvents.add(event)
                }
            }

            eventsLiveData.postValue(validEvents.toList())
        }
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