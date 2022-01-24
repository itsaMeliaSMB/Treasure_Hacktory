package com.example.android.treasurefactory.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.treasurefactory.database.*
import com.example.android.treasurefactory.model.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

private const val DATABASE_NAME = "treasure-database"

/**
* Repository for all HM classes in this app.
*/
class HMRepository private constructor(context: Context, scope: CoroutineScope) {

    private val database : TreasureDatabase = Room.databaseBuilder(
        context.applicationContext,
        TreasureDatabase::class.java,
        DATABASE_NAME
    ).addCallback(InitialPopulationCallback(scope))
        .build()

    private val hmHoardDao      = database.hoardDao()
    private val hmGemDao        = database.gemDao()
    private val hmArtDao        = database.artDao()
    private val hmMagicItemDao  = database.magicItemDao()
    private val hmSpCollectDao  = database.spellCollectionDao()
    
    private val executor        = Executors.newSingleThreadExecutor()

    private class InitialPopulationCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val gemDao = database.hmGemDao
                    val magicDao = database.hmMagicItemDao
                    val spellDao = database.hmSpCollectDao

                    var csvFile : File

                    // TODO: Check if template tables are empty before populating

                    // Seed gem templates
                    csvFile = csvReader().open("res/raw/")
                    
                    // Seed magic item templates

                    // Seed spell templates


                }
            }
        }
    }

    // region [ Hoard Functions ]

    fun getHoards(): LiveData<List<Hoard>> = hmHoardDao.getHoards()

    fun getHoard(hoardID: Int): LiveData<Hoard?> = hmHoardDao.getHoard(hoardID)

    fun addHoard(hoard: Hoard) {
        executor.execute {
            hmHoardDao.addHoard(hoard)
        }
    }

    fun updateHoard(hoard: Hoard) {
        executor.execute {
            hmHoardDao.updateHoard(hoard)
        }
    }

    // endregion

    // region [ Gem functions ]

    fun getGemTableByType(type: String) : LiveData<List<GemTemplate>> = hmGemDao.getGemTableByType(type)

    fun getHoardGems(hoardID: Int) : LiveData<List<Gem>> = hmGemDao.getGems(hoardID)

    fun getGemByID(id: Int): LiveData<Gem?> = hmGemDao.getGem(id)

    // endregion

    //region [ Art object functions ]

    fun getHoardArt(hoardID: Int) : LiveData<List<ArtObject>> = hmArtDao.getArtObjects(hoardID)

    fun getArtById(id: Int): LiveData<ArtObject?> = hmArtDao.getArtObject(id)

    // endregion

    //region [ Magic item functions ]

    fun getHoardMagicItems(hoardID: Int) : LiveData<List<MagicItem>> = hmMagicItemDao.getMagicItems(hoardID)

    fun getLimitedTableByType(type: String) : LiveData<List<LimitedMagicItemTemplate>> = hmMagicItemDao.getBaseLimItemTempsByType(type)

    fun getLimitedTableByParent(parentID: Int) : LiveData<List<LimitedMagicItemTemplate>> = hmMagicItemDao.getChildLimItemTempsByParent(parentID)

    suspend fun getItemTemplateByID(itemID: Int) : MagicItemTemplate? = hmMagicItemDao.getItemTemplateByID(itemID)

    // endregion

    //region [ Spell collection functions ]

    fun getHoardSpellCollections(hoardID: Int) : LiveData<List<SpellCollection>> = hmSpCollectDao.getSpellCollections(hoardID)

    fun getSpellCollectionByID(collectionID: Int) : LiveData<SpellCollection?> = hmSpCollectDao.getSpellCollection(collectionID)

    suspend fun getSpellTempByID(templateID: Int) : SpellTemplate? = hmSpCollectDao.getSpellTempByID(templateID)

    // endregion

    companion object {
        private var INSTANCE: HMRepository? = null

        fun initialize(context: Context, scope: CoroutineScope) { if (INSTANCE == null) INSTANCE = HMRepository(context,scope) }

        fun get(): HMRepository = INSTANCE ?: throw IllegalStateException("HMRepository must be initialized")
    }
}