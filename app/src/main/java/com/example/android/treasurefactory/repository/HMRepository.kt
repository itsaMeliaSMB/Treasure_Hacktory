package com.example.android.treasurefactory.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.android.treasurefactory.database.*
import com.example.android.treasurefactory.model.*
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
    private val utilityDao: UtilityDao
) {

    //TODO move over/rename Dao references per db/repo refactor

    private val executor        = Executors.newSingleThreadExecutor()

    // region [ Hoard Functions ]

    fun getHoards(): LiveData<List<Hoard>> = hoardDao.getHoards()

    fun getHoard(hoardID: Int): LiveData<Hoard?> = hoardDao.getHoard(hoardID)

    @WorkerThread
    suspend fun addHoard(hoard: Hoard) {
        hoardDao.addHoard(hoard)
    }

    //TODO update per db/repo refactor
    fun updateHoard(hoard: Hoard) {
        executor.execute {
            hoardDao.updateHoard(hoard)
        }
    }

    // endregion

    // region [ Gem functions ]

    fun getGemTableByType(type: String) : LiveData<List<GemTemplate>> = gemDao.getGemTableByType(type)

    fun getHoardGems(hoardID: Int) : LiveData<List<Gem>> = gemDao.getGems(hoardID)

    fun getGemByID(id: Int): LiveData<Gem?> = gemDao.getGem(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addGemTemplate(gemTemplate: GemTemplate){
        gemDao.addGemTemplate(gemTemplate)
    }

    // endregion

    //region [ Art object functions ]

    fun getHoardArt(hoardID: Int) : LiveData<List<ArtObject>> = artDao.getArtObjects(hoardID)

    fun getArtById(id: Int): LiveData<ArtObject?> = artDao.getArtObject(id)

    // endregion

    //region [ Magic item functions ]

    fun getHoardMagicItems(hoardID: Int) : LiveData<List<MagicItem>> = magicItemDao.getMagicItems(hoardID)

    fun getLimitedTableByType(type: String) : LiveData<List<LimitedMagicItemTemplate>> = magicItemDao.getBaseLimItemTempsByType(type)

    fun getLimitedTableByParent(parentID: Int) : LiveData<List<LimitedMagicItemTemplate>> = magicItemDao.getChildLimItemTempsByParent(parentID)

    suspend fun getItemTemplateByID(itemID: Int) : MagicItemTemplate? = magicItemDao.getItemTemplateByID(itemID)

    @WorkerThread
    suspend fun addMagicItemTemplate(magicItemTemplate: MagicItemTemplate) {
        magicItemDao.addMagicItemTemplate(magicItemTemplate)
    }
    // endregion

    //region [ Spell collection functions ]

    fun getHoardSpellCollections(hoardID: Int) : LiveData<List<SpellCollection>> = spellCollectionDao.getSpellCollections(hoardID)

    fun getSpellCollectionByID(collectionID: Int) : LiveData<SpellCollection?> = spellCollectionDao.getSpellCollection(collectionID)

    suspend fun getSpellTempByID(templateID: Int) : SpellTemplate? = spellCollectionDao.getSpellTempByID(templateID)

    @WorkerThread
    suspend fun addSpellTemplate(spellTemplate: SpellTemplate) {
        spellCollectionDao.addSpellTemplate(spellTemplate)
    }

    // endregion

    // region [ Utility functions TODO remove if needed ]

    /*
    @WorkerThread
    suspend fun addIDTuples(entries: List<IconIDTuple>) { utilityDao.addIDTuples(entries) }

    @WorkerThread
    suspend fun addIDTuple(entry: IconIDTuple) { utilityDao.addIDTuple(entry) }

    @WorkerThread
    suspend fun updateIDTuple(entry: IconIDTuple) { utilityDao.updateIDTuple(entry) }

    //TODO add error handling for non-existent entries
    @WorkerThread
    suspend fun getIconResID(stringID: String): Int = utilityDao.getIconResID(stringID)

    @WorkerThread
    suspend fun deleteAllIDTuples() { utilityDao.deleteAllIDTuples() }

    @WorkerThread
    suspend fun getAllUniqueIconIDs(): List<String> = utilityDao.getAllUniqueIconIDs()
    */
    // endregion
}

/*private class InitialPopulationCallback(
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
}*/