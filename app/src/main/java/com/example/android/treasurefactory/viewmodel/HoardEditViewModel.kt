package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.LootMutator
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.HoardUniqueItemBundle
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HoardEditViewModel(private val repository: HMRepository): ViewModel() {

    private val hoardIDLiveData = MutableLiveData<Int>()

    var hoardLiveData: LiveData<Hoard?> = Transformations.switchMap(hoardIDLiveData) { hoardID ->
        repository.getHoard(hoardID)
    }

    val iconsReadyLiveData = MutableLiveData(false)

    var preferredIconStr = "container_chest"
        private set
    var mostValuableGemStr = "loot_lint"
        private set
    var mostValuableArtStr = "loot_lint"
        private set
    var mostValuableMagicStr = "loot_lint"
        private set
    var mostValuableSpellStr = "loot_lint"
        private set
    var coinMixStr = "loot_lint"
        private set

    fun loadHoard(hoardID: Int) {
        hoardIDLiveData.value = hoardID
    }

    fun saveHoard(hoard: Hoard) {

        viewModelScope.launch { repository.updateHoard(hoard) }
    }

    // region [ Icon lookup functions ]

    fun getMostValuableItemStrings() {

        viewModelScope.launch {

            val capturedHoardID = hoardIDLiveData.value
            val capturedHoard = hoardLiveData.value ?: Hoard()
            val iconFetcher = LootMutator()

            val deferredGems = async {
                return@async if (capturedHoardID != null) {
                    repository.getGemsOnce(capturedHoardID)
                } else emptyList()
            }
            val deferredArtObjects = async {
                return@async if (capturedHoardID != null) {
                    repository.getArtObjectsOnce(capturedHoardID)
                } else emptyList()
            }
            val deferredMagicItems = async {
                return@async if (capturedHoardID != null) {
                    repository.getMagicItemsOnce(capturedHoardID)
                } else emptyList()
            }
            val deferredSpellCollections = async {
                return@async if (capturedHoardID != null) {
                    repository.getSpellCollectionsOnce(capturedHoardID)
                } else emptyList()
            }

            val capturedUniqueItemBundle = HoardUniqueItemBundle(
                deferredGems.await(), deferredArtObjects.await(),
                deferredMagicItems.await(), deferredSpellCollections.await()
            )

            preferredIconStr =
                iconFetcher.selectHoardIconByValue(capturedHoard,capturedUniqueItemBundle)
            mostValuableGemStr = iconFetcher.getMostValuableGemInfo(
                capturedUniqueItemBundle.hoardGems).first?.iconID ?: "loot_lint"
            mostValuableArtStr = iconFetcher.getMostValuableArtObjectInfo(
                capturedUniqueItemBundle.hoardArt).first?.getArtTypeAsIconString() ?: "loot_lint"
            mostValuableMagicStr = iconFetcher.getMostValuableMagicItemInfo(
                capturedUniqueItemBundle.hoardItems).first?.iconID ?: "loot_lint"
            mostValuableSpellStr = iconFetcher.getMostValuableSpellCollectionInfo(
                capturedUniqueItemBundle.hoardSpellCollections).first?.iconID ?: "loot_lint"
            coinMixStr = iconFetcher.getPureCoinageIcon(capturedHoard)

            iconsReadyLiveData.postValue(true)
        }
    }

    // endregion
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