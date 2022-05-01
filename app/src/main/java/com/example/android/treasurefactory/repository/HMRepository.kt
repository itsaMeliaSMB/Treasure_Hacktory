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

    /*
    private val executor        = Executors.newSingleThreadExecutor()
    executor.execute {
        hoardDao.updateHoard(hoard)
    }*/

    // region [ Hoard Functions ]

    // region ( Hoard )
    fun getHoards(): LiveData<List<Hoard>> = hoardDao.getHoards()

    fun getHoard(hoardID: Int): LiveData<Hoard?> = hoardDao.getHoard(hoardID)

    suspend fun addHoard(hoard: Hoard) : Long {
        return hoardDao.addHoard(hoard)
    }

    suspend fun updateHoard(hoardToUpdate: Hoard) = hoardDao.updateHoard(hoardToUpdate)

    suspend fun deleteHoard(hoardToDelete: Hoard) = hoardDao.deleteHoard(hoardToDelete)

    suspend fun deleteAllHoards() = hoardDao.deleteAllHoards()

    suspend fun getHoardIdByRowId(rowID: Long) : Int = hoardDao.getIdByRowId(rowID)
    // endregion

    // region ( HoardEvent )
    fun getHoardEvents(parentHoardId: Int) : LiveData<List<HoardEvent>> = hoardDao.getHoardEvents(parentHoardId)

    suspend fun addHoardEvent(newEvent: HoardEvent) = hoardDao.addHoardEvent(newEvent)
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

    fun getGemValueTotal(hoardID: Int): LiveData<Double> = gemDao.getGemValueTotal(hoardID)

    fun getGems(hoardId: Int): LiveData<List<Gem>> = gemDao.getGems(hoardId)

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

    fun getArtValueTotal(hoardID: Int): LiveData<Double> = artDao.getArtValueTotal(hoardID)

    fun getArtObjects(hoardId: Int): LiveData<List<ArtObject>> = artDao.getArtObjects(hoardId)

    fun getArtObject(artId: Int): LiveData<ArtObject?> = artDao.getArtObject(artId)

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
     * Pulls all item entries lacking a parent belonging to a given type as a Pair<Int,Int>.
     *
     * @param type String to match in table_type column
     * @return Pair of the (1) the primary key of the entry and (2) its probability weight
     */
    suspend fun getBaseLimItemTempsByType(type: String): List<Pair<Int,Int>> =
        magicItemDao.getBaseLimItemTempsByType(type)

    /**
     * Pulls all item entries with given ref_id as a Pair<Int,Int>.
     *
     * @param parentId Integer primary key id number of parent entry.
     * @return Pair of the (1) the primary key of the entry and (2) its probability weight
     */
    suspend fun getChildLimItemTempsByParent(parentId: Int): List<Pair<Int,Int>> =
        magicItemDao.getChildLimItemTempsByParent(parentId)

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

    fun getMagicItemValueTotal(hoardID: Int): LiveData<Double> =
        magicItemDao.getMagicItemValueTotal(hoardID)

    fun getMagicItems(hoardId: Int): LiveData<List<MagicItem>> = magicItemDao.getMagicItems(hoardId)

    fun getMagicItem(itemId: Int): LiveData<MagicItem?> = magicItemDao.getMagicItem(itemId)

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

    // region ( SpellTemplate )

    suspend fun getSpellTemplate(spellId: Int): SpellTemplate? =
        spellCollectionDao.getSpellTemplate(spellId)

    suspend fun getSpellTemplateByName(spellName: String, discipline: Int, level: Int): SpellTemplate? =
        spellCollectionDao.getSpellTemplateByName(spellName, discipline, level)

    suspend fun getSpellTemplateIDs(discipline: Int, level: Int): List<Int> =
        spellCollectionDao.getSpellTemplateIDs(discipline, level)

    suspend fun addSpellTemplate(entry: SpellTemplate) {
        spellCollectionDao.addSpellTemplate(entry)
    }
    // endregion

    // region ( SpellCollection )

    fun getSpellCollectionCount(hoardID: Int): LiveData<Int> =
        spellCollectionDao.getSpellCollectionCount(hoardID)

    fun getSpellCollectionValueTotal(hoardID: Int) : LiveData<Double> =
        spellCollectionDao.getSpellCollectionValueTotal(hoardID)

    fun getSpellCollections(hoardId: Int): LiveData<List<SpellCollection>> =
        spellCollectionDao.getSpellCollections(hoardId)

    fun getSpellCollection(spCoId: Int): LiveData<SpellCollection?> =
        spellCollectionDao.getSpellCollection(spCoId)

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