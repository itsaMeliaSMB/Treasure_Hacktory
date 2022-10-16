package com.example.android.treasurefactory.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.treasurefactory.database.*
import com.example.android.treasurefactory.model.*

/**
* Repository for all HM classes in this app.
*/
class HMRepository (
    private val hoardDao: HoardDao,
    private val gemDao: GemDao,
    private val artDao: ArtDao,
    private val magicItemDao: MagicItemDao,
    private val spellCollectionDao: SpellCollectionDao,
) {

    // region [ Hoard Functions ]

    // region ( Hoard )
    fun getHoards(): LiveData<List<Hoard>> = hoardDao.getHoards()

    fun getHoard(hoardID: Int): LiveData<Hoard?> = hoardDao.getHoard(hoardID)

    fun getHoardName(hoardID: Int): LiveData<String?> = hoardDao.getHoardName(hoardID)

    suspend fun getHoardOnce(hoardID: Int): Hoard? = hoardDao.getHoardOnce(hoardID)

    suspend fun getHoardEffortRatingOnce(hoardID: Int): Double =
        hoardDao.getHoardEffortRatingOnce(hoardID) ?: 5.0

    suspend fun addHoard(hoard: Hoard) : Long {
        return hoardDao.addHoard(hoard)
    }

    suspend fun updateHoard(hoardToUpdate: Hoard) = hoardDao.updateHoard(hoardToUpdate)

    suspend fun deleteHoardAndChildren(hoardToDelete: Hoard) = hoardDao.deleteHoardAndChildren(hoardToDelete)

    suspend fun deleteAllHoardsAndItems() = hoardDao.deleteAllHoardsAndItems()

    suspend fun deleteHoardsAndChildren(hoardsToDelete: List<Hoard>) {

        hoardsToDelete.forEach { hoardDao.deleteHoardAndChildren(it) }
    }

    suspend fun getHoardIdByRowId(rowID: Long) : Int = hoardDao.getIdByRowId(rowID)
    // endregion

    // region ( HoardEvent )
    fun getHoardEvents(parentHoardId: Int) : LiveData<List<HoardEvent>> = hoardDao.getHoardEvents(parentHoardId)

    suspend fun getHoardEventsOnce(parentHoardId: Int) : List<HoardEvent> = hoardDao.getHoardEventsOnce(parentHoardId)

    suspend fun addHoardEvent(newEvent: HoardEvent) = hoardDao.addHoardEvent(newEvent)
    // endregion

    // region ( LetterCode )
    suspend fun getLetterCodeOnce(letterID: String): LetterCode? = hoardDao.getLetterCodeOnce(letterID)

    suspend fun getLetterCodesOnce(): List<LetterCode> = hoardDao.getLetterCodesOnce()
    // endregion

    // endregion

    // region [ Gem functions ]

    // region ( GemTemplate )
    suspend fun getGemTemplatesByType(type: Int) : List<GemTemplate> = gemDao.getGemTemplatesByType(type)

    suspend fun getGemTemplate(templateID: Int) : GemTemplate? = gemDao.getGemTemplate(templateID)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addGemTemplate(gemTemplate: GemTemplate){
        gemDao.addGemTemplate(gemTemplate)
    }
    // endregion

    // region ( Gem )

    fun getGemCount(hoardID: Int): LiveData<Int> = gemDao.getGemCount(hoardID)

    suspend fun getGemCountOnce(hoardID: Int): Int = gemDao.getGemCountOnce(hoardID)

    fun getGemValueTotal(hoardID: Int): LiveData<Double> = gemDao.getGemValueTotal(hoardID)

    suspend fun getGemValueTotalOnce(hoardID: Int): Double = gemDao.getGemValueTotalOnce(hoardID)

    fun getGems(hoardId: Int): LiveData<List<Gem>> = gemDao.getGems(hoardId)

    suspend fun getGemsOnce(hoardID: Int): List<Gem> = gemDao.getGemsOnce(hoardID)

    fun getGem(gemId: Int): LiveData<Gem?> = gemDao.getGem(gemId)

    suspend fun addGem(newGem: Gem) {
        gemDao.addGem(newGem)
    }

    suspend fun updateGem(gemToUpdate: Gem) {
        gemDao.updateGem(gemToUpdate)
    }

    suspend fun deleteGem(gemToDelete: Gem) {
        gemDao.deleteGem(gemToDelete)
    }
    // endregion
    // endregion

    //region [ Art object functions ]

    // region ( ArtObject )

    fun getArtCount(hoardID: Int): LiveData<Int> = artDao.getArtCount(hoardID)

    suspend fun getArtCountOnce(hoardID: Int): Int = artDao.getArtCountOnce(hoardID)

    fun getArtValueTotal(hoardID: Int): LiveData<Double> = artDao.getArtValueTotal(hoardID)

    suspend fun getArtValueTotalOnce(hoardID: Int): Double = artDao.getArtValueTotalOnce(hoardID)

    fun getArtObjects(hoardId: Int): LiveData<List<ArtObject>> = artDao.getArtObjects(hoardId)

    fun getArtObject(artId: Int): LiveData<ArtObject?> = artDao.getArtObject(artId)

    suspend fun getArtObjectsOnce(hoardId: Int): List<ArtObject> = artDao.getArtObjectsOnce(hoardId)

    suspend fun addArtObject(newArt: ArtObject) {
        artDao.addArtObject(newArt)
    }

    suspend fun updateArtObject(artToUpdate: ArtObject) {
        artDao.updateArtObject(artToUpdate)
    }

    suspend fun deleteArtObject(artToDelete: ArtObject) {
        artDao.deleteArtObject(artToDelete)
    }
    // endregion

    // endregion

    //region [ Magic item functions ]

    // region ( MagicItemTemplate )

    /**
     * Pulls all item entries lacking a parent belonging to a given type as a LimitedItemTemplate.
     *
     * @param type String to match in table_type column
     * @return Pair of the (1) the primary key of the entry and (2) its probability weight
     */
    suspend fun getBaseLimItemTempsByType(type: String, allowCursed: Boolean): List<Pair<Int,Int>> =
        magicItemDao.getBaseLimItemTempsByType(type)
            .dropWhile { it.isCursed == 1 && !allowCursed }
            .map { it -> it.templateID to it.weight }

    /**
     * Pulls all item entries with given ref_id as a LimitedItemTemplate.
     *
     * @param parentId Integer primary key id number of parent entry.
     * @return Pair of the (1) the primary key of the entry and (2) its probability weight
     */
    suspend fun getChildLimItemTempsByParent(parentId: Int, allowCursed: Boolean): List<Pair<Int,Int>> =
        magicItemDao.getChildLimItemTempsByParent(parentId)
            .dropWhile { it.isCursed == 1 && !allowCursed }
            .map { it -> it.templateID to it.weight }

    /**
     * Pulls item entry matching given ref_id as MagicItemTemplate.
     *
     * @param templateID Integer primary key ID number of entry to pull.
     */
    suspend fun getMagicItemTemplate(templateId: Int): MagicItemTemplate? =
        magicItemDao.getMagicItemTemplate(templateId)

    @WorkerThread
    suspend fun addMagicItemTemplate(magicItemTemplate: MagicItemTemplate) {
        magicItemDao.addMagicItemTemplate(magicItemTemplate)
    }

    suspend fun getNamesToImitate(keyword: String) : List<String> = magicItemDao.getNamesToImitate(keyword)
    // endregion

    // region ( MagicItem )

    fun getMagicItemCount(hoardID: Int): LiveData<Int> = magicItemDao.getMagicItemCount(hoardID)

    suspend fun getMagicItemCountOnce(hoardID: Int): Int = magicItemDao.getMagicItemCountOnce(hoardID)

    fun getMagicItemValueTotal(hoardID: Int): LiveData<Double> =
        magicItemDao.getMagicItemValueTotal(hoardID)

    suspend fun getMagicItemValueTotalOnce(hoardID: Int): Double =
        magicItemDao.getMagicItemValueTotalOnce(hoardID)

    suspend fun getMagicItemXPTotalOnce(hoardID: Int): Int =
        magicItemDao.getMagicItemXPTotalOnce(hoardID)

    fun getMagicItems(hoardId: Int): LiveData<List<MagicItem>> = magicItemDao.getMagicItems(hoardId)

    fun getMagicItem(itemId: Int): LiveData<MagicItem?> = magicItemDao.getMagicItem(itemId)

    suspend fun getMagicItemsOnce(hoardId: Int): List<MagicItem> =
        magicItemDao.getMagicItemsOnce(hoardId)

    suspend fun addMagicItem(newItem: MagicItem) {
        magicItemDao.addMagicItem(newItem)
    }

    suspend fun updateMagicItem(itemToUpdate: MagicItem) {
        magicItemDao.updateMagicItem(itemToUpdate)
    }

    suspend fun deleteMagicItem(itemToDelete: MagicItem) {
        magicItemDao.deleteMagicItem(itemToDelete)
    }
    // endregion

    // endregion

    //region [ Spell collection functions ]

    // region ( Spell )

    suspend fun getSpell(spellId: Int): Spell? =
        spellCollectionDao.getSpell(spellId)

    suspend fun getSpellByName(spellName: String, discipline: Int, level: Int): Spell? =
        spellCollectionDao.getSpellByNmDsLv(spellName, discipline, level)

    suspend fun getSpellIDs(discipline: Int, level: Int): List<Int> =
        spellCollectionDao.getSpellIDs(discipline, level)

    suspend fun addSpell(entry: Spell) {
        spellCollectionDao.addSpell(entry)
    }
    // endregion

    // region ( SpellCollection )

    fun getSpellCollectionCount(hoardID: Int): LiveData<Int> =
        spellCollectionDao.getSpellCollectionCount(hoardID)

    suspend fun getSpellCollectionCountOnce(hoardID: Int): Int =
        spellCollectionDao.getSpellCollectionCountOnce(hoardID)

    fun getSpellCollectionValueTotal(hoardID: Int) : LiveData<Double> =
        spellCollectionDao.getSpellCollectionValueTotal(hoardID)

    suspend fun getSpellCollectionValueTotalOnce(hoardID: Int): Double =
        spellCollectionDao.getSpellCollectionValueTotalOnce(hoardID)

    suspend fun getSpellCollectionXPTotalOnce(hoardID: Int): Int =
        spellCollectionDao.getSpellCollectionXPTotalOnce(hoardID)

    fun getSpellCollections(hoardId: Int): LiveData<List<SpellCollection>> =
        spellCollectionDao.getSpellCollections(hoardId)

    fun getSpellCollection(spCoId: Int): LiveData<SpellCollection?> =
        spellCollectionDao.getSpellCollection(spCoId)

    suspend fun getSpellCollectionsOnce(hoardId: Int): List<SpellCollection> =
        spellCollectionDao.getSpellCollectionsOnce(hoardId)

    suspend fun addSpellCollection(newSpellCollection: SpellCollection) {
        spellCollectionDao.addSpellCollection(newSpellCollection)
    }

    suspend fun updateSpellCollection(spellCollectionToUpdate: SpellCollection) {
        spellCollectionDao.updateSpellCollection(spellCollectionToUpdate)
    }

    suspend fun deleteSpellCollection(spellCollectionToDelete: SpellCollection) {
        spellCollectionDao.deleteSpellCollection(spellCollectionToDelete)
    }
    // endregion

    // region ( CommandWord )
    suspend fun getThemedCommandWords(theme: String): List<String> =
        spellCollectionDao.getThemedCommandWords(theme)

    suspend fun getAllCommandWords(): List<String> =
        spellCollectionDao.getAllCommandWords()

    suspend fun addCommandWord(wordToAdd: CommandWord) =
        spellCollectionDao.addCommandWord(wordToAdd)
    // endregion

    // endregion
}