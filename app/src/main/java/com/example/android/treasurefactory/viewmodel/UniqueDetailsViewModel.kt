package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.capitalized
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class UniqueDetailsViewModel(private val repository: HMRepository) : ViewModel() {

    // region [ Properties ]
    private var isRunningAsync = false

    private val itemArgsLiveData = MutableLiveData<Triple<Int,UniqueItemType,Int>>()

    val exposedHoardLiveData: LiveData<Hoard?> = Transformations.switchMap(itemArgsLiveData) { (_, _, hoardID) ->
        repository.getHoard(hoardID)
    }

    var viewedItemLiveData = MutableLiveData<ViewableItem?>()

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val textToastHolderLiveData = MutableLiveData<Pair<String,Int>?>(null)

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }
    // endregion

    fun loadItemArgs(itemID: Int, type: UniqueItemType, hoardID: Int) {
        itemArgsLiveData.value = Triple(itemID,type,hoardID)
    }

    fun saveViewedItem(itemToUpdate: ViewableItem, parentHoard: Hoard) {
        //TODO unimplemented.
        // - Map itemToUpdate to a RoomEntity
        // - Get and compare against row at key if it exists
        // - Update itemToUpdate's entry in database
        // - Correct count on hoard, if applicable.
    }

    fun updateViewedItem(itemID: Int, itemType: UniqueItemType, hoardID: Int){

        viewModelScope.launch{

            setRunningAsync(true)

            val viewableItem = when (itemType){
                UniqueItemType.GEM -> {

                    val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                        .takeUnless { it == 0.0 } ?: 5.0

                    repository.getGemOnce(itemID)?.let{ gem ->

                        ViewableGem(
                            gem.gemID,
                            gem.hoardID,
                            gem.name,
                            "${gem.getSizeAsString().capitalized()}, " +
                                    "${gem.getQualityAsString()} ${gem.getTypeAsString()}",
                            gem.creationTime,
                            gem.iconID,
                            when {
                                gem.currentGPValue < 0.0        -> ItemFrameFlavor.CURSED
                                gem.currentGPValue >= 1000000.0 -> ItemFrameFlavor.GOLDEN
                                else                            -> ItemFrameFlavor.NORMAL },
                            "GameMaster's Guide",
                            178,
                            gem.currentGPValue,
                            (gem.currentGPValue / effortRating).roundToInt().coerceAtLeast(0),
                            UniqueItemType.GEM,
                            listOf(gem.getFlavorTextAsDetailsList()), //TODO add gem evaluation history when implemented
                            gem.type,
                            gem.quality,
                            gem.opacity,
                            gem.description
                        )
                    }
                }
                UniqueItemType.ART_OBJECT -> {

                    val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                        .takeUnless { it == 0.0 } ?: 5.0

                    repository.getArtObjectOnce(itemID)?.let{ art ->

                        ViewableArtObject(
                            art.artID,
                            art.hoardID,
                            art.name,
                            "${art.getSizeAsString().capitalized()}, " +
                                    "${art.getSubjectAsString()} ${art.getArtTypeAsString()}",
                            art.creationTime,
                            art.getArtTypeAsIconString(),
                            when {
                                art.valueLevel > 27 -> ItemFrameFlavor.GOLDEN
                                // if forgeries are added, indicate with CURSED
                                else -> ItemFrameFlavor.NORMAL },
                            "HackJournal #6",
                            2,
                            art.gpValue,
                            (art.gpValue / effortRating).roundToInt().coerceAtLeast(0),
                            UniqueItemType.ART_OBJECT,
                            listOf(art.getFlavorTextAsDetailsList()),
                            art.artType,
                            art.renown,
                            art.size,
                            art.condition,
                            art.materials,
                            art.quality,
                            art.age,
                            art.subject,
                            art.valueLevel
                        )
                    }
                }
                UniqueItemType.MAGIC_ITEM -> {

                    repository.getMagicItemOnce(itemID)?.let{ mgc ->

                        ViewableMagicItem(
                            mgc.mItemID,
                            mgc.hoardID,
                            mgc.name,
                            mgc.typeOfItem.tableLabel +
                                    (if (mgc.typeOfItem.ordinal <= 20)
                                        "[${mgc.typeOfItem.name}]" else ""),
                            mgc.creationTime,
                            mgc.iconID,
                            when {
                                mgc.typeOfItem == MagicItemType.A24 -> ItemFrameFlavor.GOLDEN
                                mgc.isCursed -> ItemFrameFlavor.CURSED
                                else -> ItemFrameFlavor.NORMAL },
                            mgc.sourceText,
                            mgc.sourcePage,
                            mgc.gpValue,
                            mgc.xpValue,
                            UniqueItemType.MAGIC_ITEM,
                            mgc.getNotesAsDetailsLists(),
                            mgc.templateID,
                            mgc.typeOfItem,
                            mgc.classUsability,
                            mgc.isCursed,
                            mgc.alignment,
                            mgc.notes
                        )
                    }
                }
                UniqueItemType.SPELL_COLLECTION -> {

                    repository.getSpellCollectionOnce(itemID)?.let{ spCo ->

                        ViewableSpellCollection(
                            spCo.sCollectID,
                            spCo.hoardID,
                            spCo.name,
                            spCo.getSubtitle(),
                            spCo.creationTime,
                            spCo.iconID,
                            when {
                                spCo.curse.isNotEmpty() -> ItemFrameFlavor.CURSED
                                spCo.type == SpCoType.ALLOTMENT -> ItemFrameFlavor.GOLDEN
                                else -> ItemFrameFlavor.NORMAL
                            },
                            when (spCo.type){
                                SpCoType.SCROLL -> "GameMaster's Guide"
                                SpCoType.BOOK -> "Spellslinger's Guide to Wurld Domination"
                                SpCoType.ALLOTMENT -> "Zealot’s Guide to Wurld Conversion"
                                SpCoType.RING -> "GameMaster's Guide"
                                SpCoType.OTHER -> "Unspecified"
                            },
                            when (spCo.type){
                                SpCoType.SCROLL -> 225
                                SpCoType.BOOK -> 82
                                SpCoType.ALLOTMENT -> 6
                                SpCoType.RING -> 231
                                SpCoType.OTHER -> 0
                            },
                            spCo.gpValue,
                            spCo.xpValue,
                            UniqueItemType.SPELL_COLLECTION,
                            spCo.getFlavorTextAndSpellsAsDetailsLists(repository),
                            spCo.type,
                            spCo.properties,
                            spCo.spells,
                            spCo.curse,
                        )
                    }
                }
            }

            viewedItemLiveData.postValue(viewableItem)

            delay(100)

            setRunningAsync(false)
        }
    }

}

class UniqueDetailsViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniqueDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniqueDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}