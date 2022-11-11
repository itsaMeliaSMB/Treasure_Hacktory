package com.treasurehacktory.viewmodel

import androidx.lifecycle.*
import com.treasurehacktory.model.Hoard
import com.treasurehacktory.model.HoardEvent
import com.treasurehacktory.model.HoardUniqueItemBundle
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    /** Calculated separately from item lists due to variable hoard effort ratings. */
    var hoardTotalXPLiveData = MutableLiveData(0)
        private set

    /** Bundle of data observed to generate an exported hoard report. */
    val reportInfoLiveData = MutableLiveData<Triple<Hoard, HoardUniqueItemBundle,List<HoardEvent>>?>(null)
    // endregion

    // region [ Functions ]

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {

        viewModelScope.launch { repository.updateHoard(hoard) }
    }

    /**
     * Calculates total experience value for all items with the given parent hoard ID and updates
     * hoardTotalXPLiveData with result.
     * */
    fun updateXPTotal(hoardID: Int) {

        viewModelScope.launch{

            val dependentJob = async {
                val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                val coinageTotal = (repository.getHoardOnce(hoardID)?.getTotalCoinageValue()) ?: 0.0
                val gemTotal = repository.getGemValueTotalOnce(hoardID)
                val artTotal = repository.getArtValueTotalOnce(hoardID)

                return@async ((coinageTotal + gemTotal + artTotal) / effortRating).roundToInt()
            }

            val independentJob = async {
                val magicItemTotal = repository.getMagicItemXPTotalOnce(hoardID)
                val spellCollectionTotal = repository.getSpellCollectionXPTotalOnce(hoardID)

                return@async magicItemTotal + spellCollectionTotal
            }

            (dependentJob.await() + independentJob.await()).let { xpTotal ->
                hoardTotalXPLiveData.postValue(xpTotal)
            }
        }
    }

    fun clearReportInfo() { reportInfoLiveData.value = null }

    fun fetchReportInfo(hoardID: Int) {

        viewModelScope.launch{

            try {

                val hoard = repository.getHoardOnce(hoardID)

                val deferredGems = repository.getGemsOnce(hoardID)
                val deferredArt = repository.getArtObjectsOnce(hoardID)
                val deferredItems = repository.getMagicItemsOnce(hoardID)
                val deferredSpCos = repository.getSpellCollectionsOnce(hoardID)

                val hoardItems = HoardUniqueItemBundle(deferredGems, deferredArt, deferredItems, deferredSpCos)
                val events = repository.getHoardEventsOnce(hoardID)

                if (hoard != null) {
                    reportInfoLiveData.postValue(Triple(hoard, hoardItems, events))
                }

            } catch(e: Exception){
                e.printStackTrace()
            }
        }
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