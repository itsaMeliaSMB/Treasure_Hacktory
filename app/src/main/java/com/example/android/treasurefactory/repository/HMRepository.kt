package com.example.android.treasurefactory.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.android.treasurefactory.database.*
import com.example.android.treasurefactory.model.Hoard
import java.util.concurrent.Executors

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

    //TODO move over/rename Dao references per db/repo refactor

    private val executor        = Executors.newSingleThreadExecutor()

    // region [ Hoard Functions ]

    fun getHoards(): LiveData<List<Hoard>> = hoardDao.getHoards()

    fun getHoard(hoardID: Int): LiveData<Hoard?> = hoardDao.getHoard(hoardID)

    @WorkerThread
    suspend fun addHoard(hoard: Hoard) : Long {
        return hoardDao.addHoard(hoard)
    }

    //TODO update per db/repo refactor
    fun updateHoard(hoard: Hoard) {
        executor.execute {
            hoardDao.updateHoard(hoard)
        }
    }

    @WorkerThread
    suspend fun getIdByRowId(rowID: Long) : Int = hoardDao.getIdByRowId(rowID)

    // endregion

    // region [ Gem functions ]

    //fun getGemTableByType(type: String) : LiveData<List<GemTemplate>> = gemDao.getGemTableByType(type)
    //fun getHoardGems(hoardID: Int) : LiveData<List<Gem>> = gemDao.getGems(hoardID)
    //fun getGemByID(id: Int): LiveData<Gem?> = gemDao.getGem(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addGemTemplate(gemTemplate: GemTemplate){
        gemDao.addGemTemplate(gemTemplate)
    }

    // endregion

    //region [ Art object functions ]

    //fun getHoardArt(hoardID: Int) : LiveData<List<ArtObject>> = artDao.getArtObjects(hoardID)
    //fun getArtById(id: Int): LiveData<ArtObject?> = artDao.getArtObject(id)

    // endregion

    //region [ Magic item functions ]

    //fun getHoardMagicItems(hoardID: Int) : LiveData<List<MagicItem>> = magicItemDao.getMagicItems(hoardID)
    //fun getLimitedTableByType(type: String) : LiveData<List<LimitedMagicItemTemplate>> = magicItemDao.getBaseLimItemTempsByType(type)
    //fun getLimitedTableByParent(parentID: Int) : LiveData<List<LimitedMagicItemTemplate>> = magicItemDao.getChildLimItemTempsByParent(parentID)
    //suspend fun getItemTemplateByID(itemID: Int) : MagicItemTemplate? = magicItemDao.getItemTemplateByID(itemID)

    @WorkerThread
    suspend fun addMagicItemTemplate(magicItemTemplate: MagicItemTemplate) {
        magicItemDao.addMagicItemTemplate(magicItemTemplate)
    }
    // endregion

    //region [ Spell collection functions ]

    //fun getHoardSpellCollections(hoardID: Int) : LiveData<List<SpellCollection>> = spellCollectionDao.getSpellCollections(hoardID)
    //fun getSpellCollectionByID(collectionID: Int) : LiveData<SpellCollection?> = spellCollectionDao.getSpellCollection(collectionID)
    //suspend fun getSpellTempByID(templateID: Int) : SpellTemplate? = spellCollectionDao.getSpellTempByID(templateID)

    @WorkerThread
    suspend fun addSpellTemplate(spellTemplate: SpellTemplate) {
        spellCollectionDao.addSpellTemplate(spellTemplate)
    }

    // endregion
}