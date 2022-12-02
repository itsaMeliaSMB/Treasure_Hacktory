package com.treasurehacktory.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.treasurehacktory.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "treasure-database"

/** Singleton database for entire app. */
@Database(
    entities = [
        Hoard::class,
        HoardEvent::class,
        GemTemplate::class,
        MagicItemTemplate::class,
        Gem::class,
        GemEvaluation::class,
        ArtObject::class,
        MagicItem::class,
        Spell::class,
        SpellCollection::class,
        CommandWord::class,
        LetterCode::class],
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
                    // Populate Template db tables
                    populateLetterCodesByCSV(database.hoardDao())
                    populateGemsByCSV(database.gemDao())
                    populateItemsByCSV(database.magicItemDao())
                    populateSpellsByCSV(database.spellCollectionDao())
                    populateCommandWords(database.spellCollectionDao())
                }
            }
        }

        /** Populates letter code table using hardcoded CSV file. */
        suspend fun populateLetterCodesByCSV(hoardDao: HoardDao) {

            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_letter_codes","raw",context.packageName))

            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split(',')

                    val _letterID: String = lineData[0].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _cpChance: Int = lineData[1].toIntOrNull() ?: 0
                    val _cpMin: Int = lineData[2].toIntOrNull() ?: 0
                    val _cpMax: Int = lineData[3].toIntOrNull() ?: 0
                    val _spChance: Int = lineData[4].toIntOrNull() ?: 0
                    val _spMin: Int = lineData[5].toIntOrNull() ?: 0
                    val _spMax: Int = lineData[6].toIntOrNull() ?: 0
                    val _epChance: Int = lineData[7].toIntOrNull() ?: 0
                    val _epMin: Int = lineData[8].toIntOrNull() ?: 0
                    val _epMax: Int = lineData[9].toIntOrNull() ?: 0
                    val _gpChance: Int = lineData[10].toIntOrNull() ?: 0
                    val _gpMin: Int = lineData[11].toIntOrNull() ?: 0
                    val _gpMax: Int = lineData[12].toIntOrNull() ?: 0
                    val _hspChance: Int = lineData[13].toIntOrNull() ?: 0
                    val _hspMin: Int = lineData[14].toIntOrNull() ?: 0
                    val _hspMax: Int = lineData[15].toIntOrNull() ?: 0
                    val _ppChance: Int = lineData[16].toIntOrNull() ?: 0
                    val _ppMin: Int = lineData[17].toIntOrNull() ?: 0
                    val _ppMax: Int = lineData[18].toIntOrNull() ?: 0
                    val _gemChance: Int = lineData[19].toIntOrNull() ?: 0
                    val _gemMin: Int = lineData[20].toIntOrNull() ?: 0
                    val _gemMax: Int = lineData[21].toIntOrNull() ?: 0
                    val _artChance: Int = lineData[22].toIntOrNull() ?: 0
                    val _artMin: Int = lineData[23].toIntOrNull() ?: 0
                    val _artMax: Int = lineData[24].toIntOrNull() ?: 0
                    val _potionChance: Int = lineData[25].toIntOrNull() ?: 0
                    val _potionMin: Int = lineData[26].toIntOrNull() ?: 0
                    val _potionMax: Int = lineData[27].toIntOrNull() ?: 0
                    val _scrollChance: Int = lineData[28].toIntOrNull() ?: 0
                    val _scrollMin: Int = lineData[29].toIntOrNull() ?: 0
                    val _scrollMax: Int = lineData[30].toIntOrNull() ?: 0
                    val _weaponChance: Int = lineData[31].toIntOrNull() ?: 0
                    val _weaponMin: Int = lineData[32].toIntOrNull() ?: 0
                    val _weaponMax: Int = lineData[33].toIntOrNull() ?: 0
                    val _noWeaponChance: Int = lineData[34].toIntOrNull() ?: 0
                    val _noWeaponMin: Int = lineData[35].toIntOrNull() ?: 0
                    val _noWeaponMax: Int = lineData[36].toIntOrNull() ?: 0
                    val _anyChance: Int = lineData[37].toIntOrNull() ?: 0
                    val _anyMin: Int = lineData[38].toIntOrNull() ?: 0
                    val _anyMax: Int = lineData[39].toIntOrNull() ?: 0

                    hoardDao.addLetterCode(
                        LetterCode(
                            _letterID,
                            _cpChance, _cpMin, _cpMax,
                            _spChance, _spMin, _spMax,
                            _epChance, _epMin, _epMax,
                            _gpChance, _gpMin, _gpMax,
                            _hspChance, _hspMin, _hspMax,
                            _ppChance, _ppMin, _ppMax,
                            _gemChance, _gemMin, _gemMax,
                            _artChance, _artMin, _artMax,
                            _potionChance, _potionMin, _potionMax,
                            _scrollChance, _scrollMin, _scrollMax,
                            _weaponChance, _weaponMin, _weaponMax,
                            _noWeaponChance, _noWeaponMin, _noWeaponMax,
                            _anyChance, _anyMin, _anyMax,
                        )
                    )
                    iterationCount ++
                }

        }

        /** Populates gem template table using hardcoded CSV file. */
        suspend fun populateGemsByCSV(gemDao: GemDao) {

            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_gems","raw",context.packageName))

            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split('¤')

                    val _refId: Int = lineData[0].toIntOrNull() ?: 0
                    val _type: Int = lineData[1].toIntOrNull() ?: 0
                    val _name: String = lineData[2].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _ordinal: Int = lineData[3].toIntOrNull() ?: 0
                    val _opacity: Int = lineData[4].toIntOrNull() ?: 0
                    val _description: String = lineData[5].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _iconID : String = lineData[6].trim('"').takeUnless { it.isBlank() } ?: ""

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
                    iterationCount ++
                }

        }

        /** Populates magic item template table using hardcoded CSV file. */
        suspend fun populateItemsByCSV(magicItemDao: MagicItemDao) {

            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_magic_items","raw",context.packageName))

            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split('¤')

                    val _refId: Int = lineData[0].toIntOrNull() ?: 0
                    val _wt: Int = lineData[1].toIntOrNull() ?: 0
                    val _name: String = lineData[2].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _refType: Int =  lineData[3].toIntOrNull() ?: 0
                    val _source: String = lineData[4].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _page: Int = lineData[5].toIntOrNull() ?: 0
                    val _xpValue: Int = lineData[6].toIntOrNull() ?: 0
                    val _gpValue: Int  = lineData[7].toIntOrNull() ?: 0
                    val _multiType: Int = lineData[8].toIntOrNull() ?: 0
                    val _notes: String  = lineData[9].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _dieCount: Int = lineData[10].toIntOrNull() ?: 0
                    val _dieSides: Int = lineData[11].toIntOrNull() ?: 0
                    val _dieMod: Int = lineData[12].toIntOrNull() ?: 0
                    val _tableType: String = lineData[13].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _iconRef: String = lineData[14].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _fUsable: Int =  lineData[15].toIntOrNull() ?: 0
                    val _tUsable: Int = lineData[16].toIntOrNull() ?: 0
                    val _cUsable: Int = lineData[17].toIntOrNull() ?: 0
                    val _mUsable: Int = lineData[18].toIntOrNull() ?: 0
                    val _dUsable: Int = lineData[19].toIntOrNull() ?: 0
                    val _hasChild: Int = lineData[20].toIntOrNull() ?: 0
                    val _parentID: Int = lineData[21].toIntOrNull() ?: 0
                    // 22 intentionally dropped. parent_row in csv
                    val _imitationKeyword: String = lineData[23].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _isCursed: Int  = lineData[24].toIntOrNull() ?: 0
                    val _commandWord: String = lineData[25].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _intelChance: Int = lineData[26].toIntOrNull() ?: 0
                    val _alignment: String = lineData[27].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _iPower: Int = lineData[28].toIntOrNull() ?: 0
                    val _iiPower: Int = lineData[29].toIntOrNull() ?: 0
                    val _iiiPower: Int = lineData[30].toIntOrNull() ?: 0
                    val _ivPower: Int = lineData[31].toIntOrNull() ?: 0
                    val _vPower: Int = lineData[32].toIntOrNull() ?: 0
                    val _viPower: Int = lineData[33].toIntOrNull() ?: 0

                    magicItemDao.addMagicItemTemplate(
                        MagicItemTemplate(
                            refId = _refId,
                            wt = _wt,
                            name = _name,
                            refType = try {
                                enumValues<ReferenceType>()[_refType]
                            } catch(e: Exception) { ReferenceType.OTHER_HOMEBREW },
                            source = _source,
                            page = _page,
                            xpValue = _xpValue,
                            gpValue = _gpValue,
                            multiType = _multiType,
                            notes = _notes,
                            dieCount = _dieCount,
                            dieSides = _dieSides,
                            dieMod = _dieMod,
                            tableType = _tableType,
                            iconRef = _iconRef,
                            fUsable = _fUsable,
                            tUsable = _tUsable,
                            cUsable = _cUsable,
                            mUsable = _mUsable,
                            dUsable = _dUsable,
                            hasChild = _hasChild,
                            parentID = _parentID,
                            imitationKeyword = _imitationKeyword,
                            isCursed = _isCursed,
                            commandWord = _commandWord,
                            intelChance = _intelChance,
                            alignment = _alignment,
                            iPower = _iPower,
                            iiPower = _iiPower,
                            iiiPower = _iiiPower,
                            ivPower = _ivPower,
                            vPower = _vPower,
                            viPower = _viPower
                        )
                    )

                    iterationCount ++
                }

        }

        /** Populates spell table using hardcoded CSV file. */
        suspend fun populateSpellsByCSV(spellDao: SpellCollectionDao) {

            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier(
                    "seed_spells","raw",context.packageName))

            fun String.toSchoolList() : List<SpellSchool> {
                return this.lowercase().split("/").mapNotNull{ entry ->
                    when (entry) {
                        "abj"   -> SpellSchool.ABJURATION
                        "alt"   -> SpellSchool.ALTERATION
                        "con"   -> SpellSchool.CONJURATION
                        "div"   -> SpellSchool.DIVINATION
                        "enc"   -> SpellSchool.ENCHANTMENT
                        "evo"   -> SpellSchool.EVOCATION
                        "ill"   -> SpellSchool.ILLUSION
                        "nec"   -> SpellSchool.NECROMANCY
                        else    -> null
                    }
                }
            }

            fun String.toSphereList() : List<ClericalSphere> {
                return this.lowercase().split("/").mapNotNull{ entry ->
                    when (entry) {
                        "air"           -> ClericalSphere.AIR
                        "animal"        -> ClericalSphere.ANIMAL
                        "charm"         -> ClericalSphere.CHARM
                        "combat"        -> ClericalSphere.COMBAT
                        "creation"      -> ClericalSphere.CREATION
                        "devotional"    -> ClericalSphere.DEVOTIONAL
                        "divination"    -> ClericalSphere.DIVINATION
                        "earth"         -> ClericalSphere.EARTH
                        "fire"          -> ClericalSphere.FIRE
                        "healing"       -> ClericalSphere.HEALING
                        "hurting"       -> ClericalSphere.HURTING
                        "necromantic"   -> ClericalSphere.NECROMANTIC
                        "plant"         -> ClericalSphere.PLANT
                        "summoning"     -> ClericalSphere.SUMMONING
                        "sun"           -> ClericalSphere.SUN
                        "traveler"      -> ClericalSphere.TRAVELER
                        "warding"       -> ClericalSphere.WARDING
                        "water"         -> ClericalSphere.WATER
                        "weather"       -> ClericalSphere.WEATHER
                        else            -> null
                    }
                }
            }

            fun String.toSpecialistList() : List<ArcaneSpecialist> {
                return this.lowercase().split("/").mapNotNull{ entry ->
                    when (entry) {
                        "abjurer"               -> ArcaneSpecialist.ABJURER
                        "ds_abjurer"            -> ArcaneSpecialist.ABJURER_DS
                        "battle_mage"           -> ArcaneSpecialist.BATTLE_MAGE
                        "blood_mage"            -> ArcaneSpecialist.BLOOD_MAGE
                        "conjurer"              -> ArcaneSpecialist.CONJURER
                        "ds_conjurer"           -> ArcaneSpecialist.CONJURER_DS
                        "diviner"               -> ArcaneSpecialist.DIVINER
                        "ds_diviner"            -> ArcaneSpecialist.DIVIDER_DS
                        "fire_elementalist"     -> ArcaneSpecialist.ELEMENTALIST_FIRE
                        "water_elementalist"    -> ArcaneSpecialist.ELEMENTALIST_WATER
                        "air_elementalist"      -> ArcaneSpecialist.ELEMENTALIST_AIR
                        "earth_elementalist"    -> ArcaneSpecialist.ELEMENTALIST_EARTH
                        "enchanter"             -> ArcaneSpecialist.ENCHANTER
                        "ds_enchanter"          -> ArcaneSpecialist.ENCHANTER_DS
                        "illusionist"           -> ArcaneSpecialist.ILLUSIONIST
                        "ds_illusionist"        -> ArcaneSpecialist.ILLUSIONIST_DS
                        "invoker"               -> ArcaneSpecialist.INVOKER
                        "ds_invoker"            -> ArcaneSpecialist.INVOKER_DS
                        "necromancer"           -> ArcaneSpecialist.NECROMANCER
                        "ds_necromancer"        -> ArcaneSpecialist.NECROMANCER_DS
                        "painted_mage"          -> ArcaneSpecialist.PAINTED_MAGE
                        "transmuter"            -> ArcaneSpecialist.TRANSMUTER
                        "ds_transmuter"         -> ArcaneSpecialist.TRANSMUTER_DS
                        "wild_mage"             -> ArcaneSpecialist.WILD_MAGE
                        "anti-mage"             -> ArcaneSpecialist.ANTI_MAGE
                        "guardian"              -> ArcaneSpecialist.GUARDIAN
                        "constructor"           -> ArcaneSpecialist.CONSTRUCTOR
                        "metamorpher"           -> ArcaneSpecialist.METAMORPHER
                        "transporter"           -> ArcaneSpecialist.TRANSPORTER
                        "sp_conjurer"           -> ArcaneSpecialist.CONJURER_SP
                        "power_speaker"         -> ArcaneSpecialist.POWER_SPEAKER
                        "summoner"              -> ArcaneSpecialist.SUMMONER
                        "detective"             -> ArcaneSpecialist.DETECTIVE
                        "seer"                  -> ArcaneSpecialist.SEER
                        "itemist"               -> ArcaneSpecialist.ITEMIST
                        "puppeteer"             -> ArcaneSpecialist.PUPPETEER
                        "hypnotist"             -> ArcaneSpecialist.HYPNOTIST
                        "shadow_weaver"         -> ArcaneSpecialist.SHADOW_WEAVER
                        "demolitionist"         -> ArcaneSpecialist.DEMOLITIONIST
                        "icer"                  -> ArcaneSpecialist.ICER
                        "pyrotechnician"        -> ArcaneSpecialist.PYROTECHNICIAN
                        "sniper"                -> ArcaneSpecialist.SNIPER
                        "animator"              -> ArcaneSpecialist.ANIMATOR
                        "exterminator"          -> ArcaneSpecialist.EXTERMINATOR
                        else                    -> null
                    }
                }
            }

            // Seed spell templates
            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split('¤')

                    val _refId: Int = lineData[0].toIntOrNull() ?: 0
                    val _name: String = lineData[1].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _reverse: Boolean = lineData[2].toIntOrNull() == 1
                    val _refType: Int = lineData[3].toIntOrNull() ?: 3
                    val _source: String = lineData[4].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _page: Int = lineData[5].toIntOrNull() ?: 0
                    val _type: Int = lineData[6].toIntOrNull() ?: 0
                    val _level: Int = lineData[7].toIntOrNull() ?: 0
                    val _schools: String = lineData[8].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _restrictions: String = lineData[9].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _spellSpheres: String = lineData[10].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _subclass: String = lineData[11].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _note: String = lineData[12].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _choiceString: String = lineData[13].trim('"').takeUnless { it.isBlank() } ?: ""

                    spellDao.addSpell(
                        Spell(
                            spellID = _refId,
                            name = _name,
                            reverse = _reverse,
                            refType = try {
                                enumValues<ReferenceType>()[_refType]
                            } catch(e: Exception) { ReferenceType.OTHER_HOMEBREW },
                            type = try {
                                enumValues<SpCoDiscipline>()[_type]
                            } catch(e: Exception) { SpCoDiscipline.ALL_MAGIC },
                            spellLevel = _level,
                            sourceText = _source,
                            sourcePage = _page,
                            schools = _schools.toSchoolList(),
                            spheres = _spellSpheres.toSphereList(),
                            subclass = _subclass,
                            restrictions = _restrictions.toSpecialistList(),
                            note = _note,
                            choiceString = _choiceString
                            )
                    )
                    iterationCount ++
                }

        }

        suspend fun populateCommandWords(spellDao: SpellCollectionDao) {

            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier(
                    "seed_command_words","raw",context.packageName))

            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { txtLine ->

                    val lineData = txtLine.split(';').let{

                        val themeWord = it.first()
                        val commandWords = it.last().split(",")

                        commandWords.forEach { commandWord ->
                            spellDao.addCommandWord(CommandWord(commandWord,themeWord)) }
                    }

                    iterationCount ++
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
interface HoardDao {

    // region ( Hoard )
    @Query("SELECT * FROM hackmaster_hoard_table")
    fun getHoards(): LiveData<List<Hoard>>

    @Query("SELECT * FROM hackmaster_hoard_table")
    suspend fun getHoardsOnce(): List<Hoard>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<Hoard?>

    @Query("SELECT name FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoardName(id: Int): LiveData<String?>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    suspend fun getHoardOnce(id: Int): Hoard?

    @Query("SELECT effortRating FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    suspend fun getHoardEffortRatingOnce(id: Int): Double?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoard(hoard: Hoard) : Long

    @Update
    suspend fun updateHoard(hoardToUpdate: Hoard)

    @Update
    suspend fun updateHoards(hoardsToUpdate: List<Hoard>) : Int

    @Delete
    suspend fun deleteHoard(hoardToDelete: Hoard)

    @Query("SELECT hoardID FROM hackmaster_hoard_table WHERE ROWID=(:hoardRowID)")
    suspend fun getIdByRowId(hoardRowID: Long) : Int

    @Transaction
    suspend fun deleteHoardAndChildren(hoardToDelete: Hoard) {
        val parentHoardID = hoardToDelete.hoardID
        deleteHoard(hoardToDelete)
        deleteHoardGems(parentHoardID)
        deleteHoardArtObjects(parentHoardID)
        deleteHoardMagicItems(parentHoardID)
        deleteHoardSpellCollections(parentHoardID)
        deleteHoardEvents(parentHoardID)
    }

    @Transaction
    suspend fun deleteAllHoardsAndItems() {
        deleteAllHoards()
        deleteAllGems()
        deleteAllArtObjects()
        deleteAllMagicItems()
        deleteAllSpellCollections()
        deleteAllEvents()
    }
    // endregion

    // region ( HoardEvent )
    @Query("SELECT * FROM hoard_events_log WHERE hoardID=(:parentHoardID)")
    fun getHoardEvents(parentHoardID: Int) : LiveData<List<HoardEvent>>

    @Query("SELECT * FROM hoard_events_log WHERE hoardID=(:parentHoardID)")
    suspend fun getHoardEventsOnce(parentHoardID: Int) : List<HoardEvent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvent(newEvent: HoardEvent)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvents(newEvents: List<HoardEvent>)
    // endregion

    // region ( LetterCode )
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLetterCode(entry: LetterCode)

    @Query("SELECT * FROM hackmaster_letter_codes WHERE letterID=(:letterID)")
    suspend fun getLetterCodeOnce(letterID: String) : LetterCode?

    @Query("SELECT * FROM hackmaster_letter_codes")
    suspend fun getLetterCodesOnce() : List<LetterCode>
    // endregion

    // region [ Child deletion ]

    @Query("DELETE FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    suspend fun deleteHoardGems(hoardID: Int)

    @Query("DELETE FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    suspend fun deleteHoardArtObjects(hoardID: Int)

    @Query("DELETE FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    suspend fun deleteHoardMagicItems(hoardID: Int)

    @Query("DELETE FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    suspend fun deleteHoardSpellCollections(hoardID: Int)

    @Query("DELETE FROM hoard_events_log WHERE hoardID=(:hoardID)")
    suspend fun deleteHoardEvents(hoardID: Int)

    @Query("DELETE FROM hackmaster_hoard_table")
    suspend fun deleteAllHoards()

    @Query("DELETE FROM hackmaster_gem_table")
    suspend fun deleteAllGems()

    @Query("DELETE FROM hackmaster_art_table")
    suspend fun deleteAllArtObjects()

    @Query("DELETE FROM hackmaster_magic_item_table")
    suspend fun deleteAllMagicItems()

    @Query("DELETE FROM hackmaster_spell_collection_table")
    suspend fun deleteAllSpellCollections()

    @Query("DELETE FROM hoard_events_log")
    suspend fun deleteAllEvents()
    // endregion
}

@Dao
interface GemDao {

    // region ( GemTemplate )
    @Query("SELECT * FROM hackmaster_gem_reference WHERE type=(:type) ORDER BY ordinal")
    suspend fun getGemTemplatesByType(type: Int) : List<GemTemplate>

    @Query("SELECT * FROM hackmaster_gem_reference WHERE ref_id=(:templateID)")
    suspend fun getGemTemplate(templateID: Int) : GemTemplate?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGemTemplate(entry: GemTemplate)
    // endregion

    // region ( Gem )
    @Query("SELECT COUNT(*) FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGemCount(hoardID: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    suspend fun getGemCountOnce(hoardID: Int): Int

    @Query("SELECT IFNULL(SUM(currentGPValue), 0.0) FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGemValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT IFNULL(SUM(currentGPValue), 0.0) FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    suspend fun getGemValueTotalOnce(hoardID: Int): Double

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGems(hoardID: Int): LiveData<List<Gem>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    suspend fun getGemsOnce(hoardID: Int): List<Gem>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    fun getGem(id: Int): LiveData<Gem?>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    suspend fun getGemOnce(id: Int): Gem

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGem(newGem: Gem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGems(newGems: List<Gem>)

    @Update
    suspend fun updateGems(gemsToUpdate: List<Gem>) : Int

    @Delete
    suspend fun deleteGems(gemToDelete: List<Gem>) : Int
    //endregion

    @Update
    suspend fun updateRelevantHoards(hoards: List<Hoard>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvents(newEvents: List<HoardEvent>)

    @Transaction
    suspend fun updateGemsAndHoards(gems: List<Gem>, hoards: List<Hoard>,
                                    events: List<HoardEvent>) {
        updateGems(gems)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
    @Transaction
    suspend fun deleteGemsUpdateHoards(gems: List<Gem>, hoards: List<Hoard>,
                                       events: List<HoardEvent>) {
        deleteGems(gems)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
}

@Dao
interface ArtDao {

    // region ( ArtObject )
    @Query("SELECT COUNT(*) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtCount(hoardID: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    suspend fun getArtCountOnce(hoardID: Int): Int

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    suspend fun getArtValueTotalOnce(hoardID: Int): Double

    @Query("SELECT * FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtObjects(hoardID: Int): LiveData<List<ArtObject>>

    @Query("SELECT * FROM hackmaster_art_table WHERE artID=(:artId)")
    fun getArtObject(artId: Int): LiveData<ArtObject?>

    @Query("SELECT * FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    suspend fun getArtObjectsOnce(hoardID: Int): List<ArtObject>

    @Query("SELECT * FROM hackmaster_art_table WHERE artID=(:artId)")
    suspend fun getArtObjectOnce(artId: Int): ArtObject?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addArtObject(newArt: ArtObject)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addArtObjects(newArt: List<ArtObject>)

    @Update
    suspend fun updateArtObjects(artToUpdate: List<ArtObject>) : Int

    @Delete
    suspend fun deleteArtObjects(artToDelete: List<ArtObject>) : Int
    //endregion

    @Update
    suspend fun updateRelevantHoards(hoards: List<Hoard>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvents(newEvents: List<HoardEvent>)

    @Transaction
    suspend fun updateArtObjectsAndHoards(art: List<ArtObject>, hoards: List<Hoard>,
                                          events: List<HoardEvent>) {
        updateArtObjects(art)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
    @Transaction
    suspend fun deleteArtObjectsUpdateHoards(art: List<ArtObject>, hoards: List<Hoard>,
                                             events: List<HoardEvent>) {
        updateArtObjects(art)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
}

@Dao
interface MagicItemDao {

    // region ( Templates )
    /**
     * Pulls all item entries lacking a parent belonging to a given type as a LimitedItemTemplate
     *
     * @param type String to match in table_type column
     */
    @Query("SELECT ref_id, wt, is_cursed, ref_type FROM hackmaster_magic_item_reference WHERE table_type=(:type) AND parent_id=0")
    suspend fun getBaseLimItemTempsByType(type: String): List<LimitedItemTemplate>

    @Query("SELECT * FROM hackmaster_magic_item_reference WHERE table_type=(:type) AND parent_id=0")
    suspend fun getBaseItemTempsByType(type: String): List<MagicItemTemplate>

    @Query("SELECT name FROM hackmaster_magic_item_reference WHERE ref_id=(:itemID)")
    suspend fun getNameOfTemplateOnce(itemID: Int): String?

    @Query("SELECT parent_id FROM hackmaster_magic_item_reference WHERE ref_id=(:itemID)")
    suspend fun getParentIDOfItemOnce(itemID: Int): Int?

    /**
     * Pulls all item entries with given parent ref_id as a LimitedItemTemplate
     *
     * @param parentID Integer primary key id number of parent entry.
     */
    @Query("SELECT ref_id, wt, is_cursed, ref_type FROM hackmaster_magic_item_reference WHERE parent_id=(:parentID)")
    suspend fun getChildLimItemTempsByParent(parentID: Int): List<LimitedItemTemplate>

    /**
     * Pulls item entry matching given ref_id as MagicItemTemplate.
     *
     * @param itemID Integer primary key ID number of entry to pull.
     */
    @Query("SELECT * FROM hackmaster_magic_item_reference WHERE ref_id=(:itemID) LIMIT 1")
    suspend fun getMagicItemTemplate(itemID: Int): MagicItemTemplate?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMagicItemTemplate(entry: MagicItemTemplate)

    @Query("SELECT name FROM hackmaster_magic_item_reference WHERE name LIKE (:keyword)")
    suspend fun getNamesToImitate(keyword: String) : List<String>
    // endregion

    //region ( MagicItem )
    @Query("SELECT COUNT(*) FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    fun getMagicItemCount(hoardID: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    suspend fun getMagicItemCountOnce(hoardID: Int): Int

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    fun getMagicItemValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    suspend fun getMagicItemValueTotalOnce(hoardID: Int): Double

    @Query("SELECT IFNULL(SUM(xpValue), 0) FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    suspend fun getMagicItemXPTotalOnce(hoardID: Int): Int

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    fun getMagicItems(hoardID: Int): LiveData<List<MagicItem>>

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE mItemID=(:itemID)")
    fun getMagicItem(itemID: Int): LiveData<MagicItem?>

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    suspend fun getMagicItemsOnce(hoardID: Int): List<MagicItem>

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE mItemID=(:itemID)")
    suspend fun getMagicItemOnce(itemID: Int): MagicItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMagicItem(newItem: MagicItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMagicItems(newItems: List<MagicItem>)

    @Update
    suspend fun updateMagicItems(itemsToUpdate: List<MagicItem>) : Int

    @Delete
    suspend fun deleteMagicItems(itemsToDelete: List<MagicItem>) : Int
    //endregion

    @Update
    suspend fun updateRelevantHoards(hoards: List<Hoard>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvents(newEvents: List<HoardEvent>)

    @Transaction
    suspend fun updateMagicItemsAndHoards(items: List<MagicItem>, hoards: List<Hoard>,
                                          events: List<HoardEvent>) {
        updateMagicItems(items)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
    @Transaction
    suspend fun deleteMagicItemsUpdateHoards(items: List<MagicItem>, hoards: List<Hoard>,
                                             events: List<HoardEvent>) {
        updateMagicItems(items)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
}

@Dao
interface SpellCollectionDao{

    // region ( Spell )
    @Query("SELECT * FROM hackmaster_spell_table WHERE spell_id=(:spellID)")
    suspend fun getSpell(spellID: Int): Spell?

    @Query("SELECT * FROM hackmaster_spell_table WHERE name=(:spellName) AND type=(:discipline) AND spellLevel=(:level)")
    suspend fun getSpellByNmDsLv(spellName: String, discipline: Int, level: Int): Spell?

    @Query("SELECT * FROM hackmaster_spell_table WHERE type=(:discipline) AND spellLevel=(:level)")
    suspend fun getSpellsByDsLv(discipline: Int, level: Int): List<Spell>

    @Query("SELECT spell_id FROM hackmaster_spell_table WHERE type=(:discipline) " +
            "AND spellLevel=(:level)")
    suspend fun getSpellIDs(discipline: Int, level: Int): List<Int>

    @Query("SELECT * FROM hackmaster_spell_table WHERE spellLevel=(:level) AND (schools " +
            "LIKE '%' || :schOrdStr || '%' AND choiceString LIKE '%' || :choiceStr || '%')")
    suspend fun getLevelChoiceSpells(level: Int, schOrdStr: String, choiceStr: String): List<Spell>

    @Query("SELECT * FROM hackmaster_spell_table WHERE spellLevel=1 AND (choiceString LIKE " +
            "'%' || :choiceStr || '%')")
    suspend fun getInitialChoiceSpells(choiceStr: String): List<Spell>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpell(entry: Spell)
    // endregion

    // region ( SpellCollection )
    @Query("SELECT COUNT(*) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollectionCount(hoardID: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    suspend fun getSpellCollectionCountOnce(hoardID: Int): Int

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollectionValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT IFNULL(SUM(gpValue), 0.0) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    suspend fun getSpellCollectionValueTotalOnce(hoardID: Int): Double

    @Query("SELECT IFNULL(SUM(xpValue), 0.0) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    suspend fun getSpellCollectionXPTotalOnce(hoardID: Int): Int

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollections(hoardID: Int): LiveData<List<SpellCollection>>

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE sCollectID=(:spCoId)")
    fun getSpellCollection(spCoId: Int): LiveData<SpellCollection?>

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    suspend fun getSpellCollectionsOnce(hoardID: Int): List<SpellCollection>

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE sCollectID=(:spCoId)")
    suspend fun getSpellCollectionOnce(spCoId: Int): SpellCollection?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpellCollection(newSpellCollection: SpellCollection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpellCollections(newSpellCollections: List<SpellCollection>)

    @Update
    suspend fun updateSpellCollections(spellCollectionsToUpdate: List<SpellCollection>) : Int

    @Delete
    suspend fun deleteSpellCollections(spellCollectionsToDelete: List<SpellCollection>) : Int
    // endregion

    // region ( CommandWord )
    @Query("SELECT commandWord FROM command_word_suggestions WHERE themeWord=(:theme)")
    suspend fun getThemedCommandWords(theme: String): List<String>

    @Query("SELECT commandWord FROM command_word_suggestions")
    suspend fun getAllCommandWords(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCommandWord(wordToAdd: CommandWord)
    // endregion

    @Update
    suspend fun updateRelevantHoards(hoards: List<Hoard>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvents(newEvents: List<HoardEvent>)

    @Transaction
    suspend fun updateSpellCollectionsAndHoards(spellCollections: List<SpellCollection>,
                                                hoards: List<Hoard>, events: List<HoardEvent>) {
        updateSpellCollections(spellCollections)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }

    @Transaction
    suspend fun deleteSpellCollectionsUpdateHoards(spellCollections: List<SpellCollection>,
                                                   hoards: List<Hoard>, events: List<HoardEvent>) {
        deleteSpellCollections(spellCollections)
        updateRelevantHoards(hoards)
        addHoardEvents(events)
    }
}
//endregion
