package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository

class HoardDetailsViewModel(): ViewModel() {

    private val hmRepository = HMRepository.get()
    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        hmRepository.getHoard(hoardID)
    }

    //TODO re-evaluate [[[[ val/var ]]]] and LiveData/MutableLiveData usage
    var hGemsLiveData: LiveData<List<Gem>> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        hmRepository.getHoardGems(hoardID)
    }
    var hArtLiveData: LiveData<List<ArtObject>> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        hmRepository.getHoardArt(hoardID)
    }
    var hMagicItemsLiveData: LiveData<List<MagicItem>> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        hmRepository.getHoardMagicItems(hoardID)
    }
    var hSpellCollectionsLiveData: LiveData<List<SpellCollection>> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        hmRepository.getHoardSpellCollections(hoardID)
    }


    // Likely will need to use a MediatorLiveData to observe all lists associated with hoard at once
    // https://www.reddit.com/r/androiddev/comments/b1dx5c/livedata_is_a_godsend/
    // https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7


    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {
        hmRepository.updateHoard(hoard)
    }
}