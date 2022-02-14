package com.example.android.treasurefactory.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.treasurefactory.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

//TODO revisit this and consider seperating entities from data class: https://jacquessmuts.github.io/post/modularization_room/

private const val DATABASE_NAME = "treasure-database"

/**
 * Singleton database for entire app.
 */
@Database(
    entities = [
        Hoard::class,
        GemTemplate::class,
        MagicItemTemplate::class,
        SpellTemplate::class,
        GemEntity::class,
        ArtObjectEntity::class,
        MagicItemEntity::class,
        SpellCollectionEntity::class],
    version = 1)
@TypeConverters(HoardTypeConverters::class)
abstract class TreasureDatabase : RoomDatabase() {

    abstract fun hoardDao(): HoardDao
    abstract fun gemDao(): GemDao
    abstract fun artDao(): ArtDao
    abstract fun magicItemDao(): MagicItemDao
    abstract fun spellCollectionDao(): SpellCollectionDao

    private class InitialPopulationCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {

                    // Get app version
                    val appVersionCode : Long = context.packageManager
                        .getPackageInfo(context.packageName,0)
                        .longVersionCode

                    // Populate Template db tables
                    populateGemsByCSV(database.gemDao())
                    populateItemsByCSV(database.magicItemDao())
                    populateSpellsByCSV(database.spellCollectionDao())

                    // Populate icon ID directory after templates are populated


                }
            }
        }

        // Since it was so hard to find, https://discuss.kotlinlang.org/t/why-would-using-coroutines-be-slower-than-sequential-for-a-big-file/7698/7

        /**
         * Populates gem template table using hardcoded CSV file.
         */
        suspend fun populateGemsByCSV(gemDao: GemDao) {

            val FILEPATH = "src/main/res/raw/seed_gem_v01.csv"

            File(FILEPATH)
                .inputStream()
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

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
        }

        /**
         * Populates magic item template table using hardcoded CSV file.
         */
        suspend fun populateItemsByCSV(magicItemDao: MagicItemDao) {

            val FILEPATH = "src/main/res/raw/seed_magicitems_v01.csv"

            File(FILEPATH)
                .inputStream()
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

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

                    magicItemDao.addMagicItemTemplate(
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
        }

        /**
         * Populates spell template table using hardcoded CSV file.
         */
        suspend fun populateSpellsByCSV(spellDao: SpellCollectionDao) {

            // Seed spell templates
            File("src/main/res/raw/seed_spell_v01.csv")
                .inputStream()
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

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

    companion object {

        @Volatile
        private var INSTANCE: TreasureDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TreasureDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance: TreasureDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    TreasureDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(InitialPopulationCallback(context, scope))
                    .build()
                INSTANCE = instance

                instance
            }
        }

    }

}

//region [ Data Access Objects ]

@Dao
interface HoardDao{

    @Query("SELECT * FROM hackmaster_hoard_table")
    fun getHoards(): LiveData<List<Hoard>>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<Hoard?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHoard(hoard: Hoard)

    @Update
    fun updateHoard(hoard: Hoard)
}

@Dao
interface GemDao {

    @Query("SELECT * FROM hackmaster_gem_reference WHERE type=(:type) ORDER BY ordinal")
    fun getGemTableByType(type: String): LiveData<List<GemTemplate>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGemTemplate(entry: GemTemplate)

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGems(hoardID: Int): LiveData<List<Gem>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    fun getGem(id: Int): LiveData<Gem?>

    // Add a gem to the hoard TODO

    // Update this gem in the hoard TODO

    // Remove this gem from hoard TODO

}

@Dao
interface ArtDao {

    @Query("SELECT * FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtObjects(hoardID: Int): LiveData<List<ArtObject>>

    @Query("SELECT * FROM hackmaster_art_table WHERE artID=(:id)")
    fun getArtObject(id: Int): LiveData<ArtObject?>

    // Add art object to the hoard TODO

    // Update this art object in the hoard TODO

    // Remove this art object from hoard TODO
}

@Dao
interface MagicItemDao {

    /**
     * Pulls all item entries lacking a parent belonging to a given type as a LimitedMagicItemTemplate.
     *
     * @param type String to match in table_type column
     */
    @Query("SELECT ref_id, wt FROM hackmaster_magic_item_reference WHERE table_type=(:type) AND parent_id=0")
    fun getBaseLimItemTempsByType(type: String): LiveData<List<LimitedMagicItemTemplate>>

    /**
     * Pulls all item entries with given ref_id as a LimitedMagicItemTemplate.
     *
     * @param parentID Integer primary key id number of parent entry.
     */
    @Query("SELECT ref_id, wt FROM hackmaster_magic_item_reference WHERE table_type=(:parentID)")
    fun getChildLimItemTempsByParent(parentID: Int): LiveData<List<LimitedMagicItemTemplate>>

    /**
     * Pulls item entry matching given ref_id as MagicItemTemplate.
     *
     * @param itemID Integer primary key ID number of entry to pull.
     */
    @Query("SELECT * FROM hackmaster_magic_item_reference WHERE ref_id=(:itemID) LIMIT 1")
    suspend fun getItemTemplateByID(itemID: Int): MagicItemTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMagicItemTemplate(entry: MagicItemTemplate)

    // Get magic items from hoard TODO
    @Query("SELECT * FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    fun getMagicItems(hoardID: Int): LiveData<List<MagicItem>>

    // Add magic item to hoard TODO

    // Update this magic item in hoard TODO

    // Remove this magic item from hoard TODO

}

@Dao
interface SpellCollectionDao{

    // Pull all spell collections from a hoard TODO
    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollections(hoardID: Int): LiveData<List<SpellCollection>>

    // Pull specific spell collection from hoard with given ID TODO
    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE sCollectID=(:id)")
    fun getSpellCollection(id: Int): LiveData<SpellCollection?>

    // Add spell collection to hoard TODO

    // Update a spell collection in hoard TODO

    // Pull specific spell template by ID
    @Query("SELECT * FROM hackmaster_spell_reference WHERE ref_id=(:id)")
    suspend fun getSpellTempByID(id: Int): SpellTemplate?

    // Pull all spell IDs of a level and magical discipline (excluding restricted spells)
    @Query("SELECT ref_id FROM hackmaster_spell_reference WHERE type=(:type) AND level=(:level) AND restricted_to=''")
    suspend fun getSpellsOfLevelType(type: Int, level: Int): List<Int>

    // Pull all spell IDs of a level and magical discipline
    @Query("SELECT ref_id FROM hackmaster_spell_reference WHERE type=(:type) AND level=(:level)")
    suspend fun getAllSpellsOfLevelType(type: Int, level: Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSpellTemplate(entry: SpellTemplate)
}

/*
/**
 * Data access object for models unrelated to generated hoards
 */
@Dao
interface UtilityDao{

    @Insert(onConflict = REPLACE)
    suspend fun addIDTuple(entry: IconIDTuple)

    @Insert(onConflict = REPLACE)
    suspend fun addIDTuples(entries: List<IconIDTuple>)

    @Update
    suspend fun updateIDTuple(entry: IconIDTuple)

    @Query("SELECT resID FROM icon_id_int_directory WHERE stringID=(:stringID) LIMIT 1")
    suspend fun getIconResID(stringID: String): Int

    @Query("DELETE FROM icon_id_int_directory")
    suspend fun deleteAllIDTuples()

    @Query("SELECT icon_id FROM hackmaster_hoard_table UNION " +
            "SELECT icon_id FROM hackmaster_gem_reference UNION " +
            "SELECT iconID FROM hackmaster_gem_table UNION " +
            "SELECT icon_id FROM hackmaster_art_table UNION " +
            "SELECT icon_ref FROM hackmaster_magic_item_reference UNION " +
            "SELECT icon_id FROM hackmaster_magic_item_table UNION " +
            "SELECT icon_id FROM hackmaster_spell_collection_table")
    suspend fun getAllUniqueIconIDs(): List<String>

    //TODO add query for pulling all entries with a certain appVersionCode
}
 */

//endregion

