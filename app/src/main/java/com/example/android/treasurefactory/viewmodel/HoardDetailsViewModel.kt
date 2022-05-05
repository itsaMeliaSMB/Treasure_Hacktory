package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.launch

class HoardDetailsViewModel(private val repository: HMRepository) : ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    // Helpful viewing for LiveData: https://www.youtube.com/watch?v=suC0OM5gGAA



    // Likely will need to use a MediatorLiveData to observe all lists associated with hoard at once
    // https://www.reddit.com/r/androiddev/comments/b1dx5c/livedata_is_a_godsend/
    // https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7


    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {

        viewModelScope.launch { repository.updateHoard(hoard) }
    }
}