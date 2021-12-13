package com.example.android.treasurefactory.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.treasurefactory.database.GemTemplate
import com.example.android.treasurefactory.database.MagicItemTemplate
import com.example.android.treasurefactory.database.TreasureDatabase
import com.example.android.treasurefactory.model.*

private const val DATABASE_NAME = "treasure-database"

/**
* Repository for all HM classes in this app.
*/
class HMRepository private constructor(context: Context) {

    private val database : TreasureDatabase = Room.databaseBuilder(
        context.applicationContext,
        TreasureDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val hmHoardDao      = database.hoardDao()
    private val hmGemDao        = database.gemDao()
    private val hmArtDao        = database.artDao()
    private val hmMagicItemDao  = database.magicItemDao()
    private val hmSpCollectDao  = database.spellCollectionDao()
    //TODO add other Daos as they are made


    // region [[ Hoard Functions ]]

    fun getHoards() : LiveData<List<Hoard>> = hmHoardDao.getHoards()

    fun getHoard(hoardID: Int) : LiveData<Hoard?> = hmHoardDao.getHoard(hoardID)

    // endregion

    // region [[ Gem functions ]]

    fun getGemTableByType(type: String) : LiveData<List<GemTemplate>> = hmGemDao.getGemTableByType(type)

    fun getHoardGems(hoardID: Int) : LiveData<List<Gem>> = hmGemDao.getGems(hoardID)

    fun getGemByID(id: Int): LiveData<Gem?> = hmGemDao.getGem(id)

    // endregion

    //region [ Art object functions ]

    fun getHoardArt(hoardID: Int) : LiveData<List<ArtObject>> = hmArtDao.getArtObjects(hoardID)

    fun getArtById(id: Int): LiveData<ArtObject?> = hmArtDao.getArtObject(id)

    // endregion

    //region [ Magic item functions ]

    fun getLimitedTableByType(type: String) : LiveData<List<LimitedMagicItemTemplate>> = hmMagicItemDao.getBaseLimItemTempsByType(type)

    fun getLimitedTableByParent(parentID: Int) : LiveData<List<LimitedMagicItemTemplate>> = hmMagicItemDao.getChildLimItemTempsByParent(parentID)

    fun getItemTemplateByID(itemID: Int) : LiveData<MagicItemTemplate> = hmMagicItemDao.getItemTemplateByID(itemID)

    // endregion

    //region [ Spell collection functions ]

    fun getHoardSpellCollections(hoardID: Int) : LiveData<List<SpellCollection>> = hmSpCollectDao.getSpellCollections(hoardID)

    fun getSpellCollectionByID(collectionID: Int) : LiveData<SpellCollection?> = hmSpCollectDao.getSpellCollection(collectionID)

    // endregion

    companion object {
        private var INSTANCE: HMRepository? = null

        fun initialize(context: Context) { if (INSTANCE == null) INSTANCE = HMRepository(context) }

        fun get(): HMRepository = INSTANCE ?: throw IllegalStateException("HMRepository must be initialized")
    }
}