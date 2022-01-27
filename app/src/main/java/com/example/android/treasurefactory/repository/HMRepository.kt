package com.example.android.treasurefactory.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.treasurefactory.database.*
import com.example.android.treasurefactory.model.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
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

                    // TODO: Check if template tables are empty before populating

                    // Seed gem templates

                    File("src/main/res/raw/seed_gem_v01.csv")
                        .inputStream()
                        .bufferedReader()
                        .forEachLine { csvLine ->

                        val lineData = csvLine.split('¤')

                        val _refId: Int = lineData[0].toIntOrNull() ?: 0
                        val _type: String = lineData[1]
                        val _name: String = lineData[2]
                        val _ordinal: Int = lineData[3].toIntOrNull() ?: 0
                        val _opacity: Int = lineData[4].toIntOrNull() ?: 0
                        val _description: String = lineData[5]
                        val _iconID : String = lineData[6]

                        gemDao.addGemTemplate(
                            GemTemplate(
                                _refId,
                                _type,
                                _name,
                                _ordinal,
                                _opacity,
                                _description,
                                _iconID
                            )
                        )
                    }

                    // Seed magic item templates

                    File("src/main/res/raw/seed_magicitems_v01.csv")
                        .inputStream()
                        .bufferedReader()
                        .forEachLine { csvLine ->

                        val lineData = csvLine.split('¤')

                        val _refId: Int = lineData[0].toIntOrNull() ?: 0
                        val _wt: Int = lineData[1].toIntOrNull() ?: 0
                        val _name: String = lineData[2]
                        val _source: String = lineData[3]
                        val _page: Int = lineData[4].toIntOrNull() ?: 0
                        val _xpValue: Int = lineData[5].toIntOrNull() ?: 0
                        val _gpValue: Int  = lineData[6].toIntOrNull() ?: 0
                        val _multiType: Int = lineData[7].toIntOrNull() ?: 0
                        val _notes: String  = lineData[8]
                        val _dieCount: Int = lineData[9].toIntOrNull() ?: 0
                        val _dieSides: Int = lineData[10].toIntOrNull() ?: 0
                        val _dieMod: Int = lineData[11].toIntOrNull() ?: 0
                        val _tableType: String = lineData[12]
                        val _iconRef: String = lineData[13]
                        val _fUsable: Int =  lineData[14].toIntOrNull() ?: 0
                        val _tUsable: Int = lineData[15].toIntOrNull() ?: 0
                        val _cUsable: Int = lineData[16].toIntOrNull() ?: 0
                        val _mUsable: Int = lineData[17].toIntOrNull() ?: 0
                        val _dUsable: Int = lineData[18].toIntOrNull() ?: 0
                        val _hasChild: Int = lineData[19].toIntOrNull() ?: 0
                        val _parentID: Int = lineData[20].toIntOrNull() ?: 0
                        val _imitationKeyword: String = lineData[21]
                        val _isCursed: Int  = lineData[22].toIntOrNull() ?: 0
                        val _commandWord: String = lineData[23]
                        val _intelChance: Int = lineData[24].toIntOrNull() ?: 0
                        val _alignment: String = lineData[25]
                        val _iPower: Int = lineData[26].toIntOrNull() ?: 0
                        val _iiPower: Int = lineData[27].toIntOrNull() ?: 0
                        val _iiiPower: Int = lineData[28].toIntOrNull() ?: 0
                        val _ivPower: Int = lineData[29].toIntOrNull() ?: 0
                        val _vPower: Int = lineData[30].toIntOrNull() ?: 0
                        val _viPower: Int = lineData[31].toIntOrNull() ?: 0

                        magicDao.addMagicItemTemplate(
                            MagicItemTemplate(
                                _refId,
                                _wt,
                                _name,
                                _source,
                                _page,
                                _xpValue,
                                _gpValue,
                                _multiType,
                                _notes,
                                _dieCount,
                                _dieSides,
                                _dieMod,
                                _tableType,
                                _iconRef,
                                _fUsable,
                                _tUsable,
                                _cUsable,
                                _mUsable,
                                _dUsable,
                                _hasChild,
                                _parentID,
                                _imitationKeyword,
                                _isCursed,
                                _commandWord,
                                _intelChance,
                                _alignment,
                                _iPower,
                                _iiPower,
                                _iiiPower,
                                _ivPower,
                                _vPower,
                                _viPower
                            )
                        )
                    }

                    // Seed spell templates
                    File("src/main/res/raw/seed_spell_v01.csv")
                        .inputStream()
                        .bufferedReader()
                        .forEachLine { csvLine ->

                            val lineData = csvLine.split('¤')

                            val _refId: Int = lineData[0].toIntOrNull() ?: 0
                            val _name: String = lineData[1]
                            val _source: String = lineData[2]
                            val _page: Int = lineData[3].toIntOrNull() ?: 0
                            val _type: Int = lineData[4].toIntOrNull() ?: 0
                            val _level: Int = lineData[5].toIntOrNull() ?: 0
                            val _schools: String = lineData[6]
                            val _restrictions: String = lineData[7]
                            val _spellSpheres: String = lineData[8]
                            val _subclass: String = lineData[9]
                            val _note: String = lineData[10]

                            spellDao.addSpellTemplate(
                                SpellTemplate(
                                    _refId,
                                    _name,
                                    _source,
                                    _page,
                                    _type,
                                    _level,
                                    _schools,
                                    _restrictions,
                                    _spellSpheres,
                                    _subclass,
                                    _note
                                )
                            )
                        }
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