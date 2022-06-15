package com.example.android.treasurefactory.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.treasurefactory.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO revisit this and consider seperating entities from data class: https://jacquessmuts.github.io/post/modularization_room/

private const val DATABASE_NAME = "treasure-database"

/** Singleton database for entire app. */
@Database(
    entities = [
        Hoard::class,
        HoardEvent::class,
        GemTemplate::class,
        MagicItemTemplate::class,
        SpellTemplate::class,
        Gem::class,
        ArtObject::class,
        MagicItem::class,
        SpellCollection::class,
        CommandWord::class],
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

        // https://developer.android.com/codelabs/android-room-with-a-view-kotlin#13

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
                    populateCommandWords(database.spellCollectionDao())
                }
            }
        }

        // Since it was so hard to find, https://discuss.kotlinlang.org/t/why-would-using-coroutines-be-slower-than-sequential-for-a-big-file/7698/7

        /** Populates gem template table using hardcoded CSV file. */
        suspend fun populateGemsByCSV(gemDao: GemDao) {

            // val csvFilePath = "src/main/res/raw/seed_gem_v01.csv"
            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_gem_v01","raw",context.packageName))

            /*
            File(csvFilePath)
                .inputStream()
            */
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

            Log.d("InitialPopulationCallback","Gem template addition by CSV ran. " +
                    "[ Iteration count = $iterationCount ]")
        }

        /** Populates magic item template table using hardcoded CSV file. */
        suspend fun populateItemsByCSV(magicItemDao: MagicItemDao) {

            //val csvFilePath = "src/main/res/raw/seed_magicitems_v01.csv"
            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_magicitems_v02","raw",context.packageName))

            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split('¤')

                    val _refId: Int = lineData[0].toIntOrNull() ?: 0
                    val _wt: Int = lineData[1].toIntOrNull() ?: 0
                    val _name: String = lineData[2].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _source: String = lineData[3].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _page: Int = lineData[4].toIntOrNull() ?: 0
                    val _xpValue: Int = lineData[5].toIntOrNull() ?: 0
                    val _gpValue: Int  = lineData[6].toIntOrNull() ?: 0
                    val _multiType: Int = lineData[7].toIntOrNull() ?: 0
                    val _notes: String  = lineData[8].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _dieCount: Int = lineData[9].toIntOrNull() ?: 0
                    val _dieSides: Int = lineData[10].toIntOrNull() ?: 0
                    val _dieMod: Int = lineData[11].toIntOrNull() ?: 0
                    val _tableType: String = lineData[12].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _iconRef: String = lineData[13].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _fUsable: Int =  lineData[14].toIntOrNull() ?: 0
                    val _tUsable: Int = lineData[15].toIntOrNull() ?: 0
                    val _cUsable: Int = lineData[16].toIntOrNull() ?: 0
                    val _mUsable: Int = lineData[17].toIntOrNull() ?: 0
                    val _dUsable: Int = lineData[18].toIntOrNull() ?: 0
                    val _hasChild: Int = lineData[19].toIntOrNull() ?: 0
                    val _parentID: Int = lineData[20].toIntOrNull() ?: 0
                    val _imitationKeyword: String = lineData[21].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _isCursed: Int  = lineData[22].toIntOrNull() ?: 0
                    val _commandWord: String = lineData[23].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _intelChance: Int = lineData[24].toIntOrNull() ?: 0
                    val _alignment: String = lineData[25].trim('"').takeUnless { it.isBlank() } ?: ""
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

                    iterationCount ++
                }

            Log.d("InitialPopulationCallback","Magic item template addition by CSV ran. " +
                    "[ Iteration count = $iterationCount ]")
        }

        /** Populates spell template table using hardcoded CSV file. */
        suspend fun populateSpellsByCSV(spellDao: SpellCollectionDao) {

            //val csvFilePath = "src/main/res/raw/seed_spell_v01.csv"
            var iterationCount = 0

            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("seed_spell_v01","raw",context.packageName))

            // Seed spell templates
            inputStream
                .bufferedReader()
                .lineSequence()
                .forEach { csvLine ->

                    val lineData = csvLine.split('¤')

                    val _refId: Int = lineData[0].toIntOrNull() ?: 0
                    val _name: String = lineData[1].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _refType: Int = lineData[2].toIntOrNull() ?: 3
                    val _source: String = lineData[3].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _page: Int = lineData[4].toIntOrNull() ?: 0
                    val _type: Int = lineData[5].toIntOrNull() ?: 0
                    val _level: Int = lineData[6].toIntOrNull() ?: 0
                    val _schools: String = lineData[7].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _restrictions: String = lineData[8].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _spellSpheres: String = lineData[9].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _subclass: String = lineData[10].trim('"').takeUnless { it.isBlank() } ?: ""
                    val _note: String = lineData[11].trim('"').takeUnless { it.isBlank() } ?: ""

                    spellDao.addSpellTemplate(
                        SpellTemplate(
                            _refId,
                            _name,
                            _refType,
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
                    iterationCount ++
                }

            Log.d("InitialPopulationCallback","Spell template addition by CSV ran. " +
                    "[ Iteration count = $iterationCount ]")
        }

        suspend fun populateCommandWords(spellDao: SpellCollectionDao) {

            val commandWordLists = listOf(
                "absorb" to listOf(
                    "SPONGE",
                    "SLURP",
                    "DRAIN",
                    "ENSORBI",
                    "MITIIA"),
                "air" to listOf(
                    "EASY-BREEZY",
                    "DRAFT",
                    "MIENO",
                    "POVITRYA",
                    "HURU"),
                "alarm" to listOf(
                    "COCKADOODLEDOO",
                    "RINGALING",
                    "CLAXON",
                    "STARTLE",
                    "KONSTERNO"),
                "alternate" to listOf(
                    "WHAT-IF",
                    "ANOTHER",
                    "NEW-CHOICE",
                    "FAIRWEATHER",
                    "SWITCHAROO",
                    "SUROGATA"),
                "animal" to listOf(
                    "CRIKEY",
                    "FURRY",
                    "FUZZBALL",
                    "FLUFFY",
                    "CRITTERS",
                    "BESTO",
                    "ZOO",
                    "PAWPRINT",
                    "MENAGERIE"),
                "apology" to listOf(
                    "MEA-CULPA",
                    "GOMEN",
                    "WHOOPSIE",
                    "ACCIDENT",
                    "PENTI-PRI"),
                "blast" to listOf(
                    "KABLAM",
                    "KERPOW",
                    "EXPLOSION",
                    "DETONATE",
                    "EKSPLODBRUI"),
                "breach" to listOf(
                    "CHARGE",
                    "BANZAI",
                    "OH-YEAH",
                    "BOMBARD",
                    "ENROMPO"),
                "break" to listOf(
                    "CRASH",
                    "SHATTER",
                    "SMAAAAASH",
                    "SNAP",
                    "DISROMPI"),
                "burst" to listOf(
                    "GARDENHOSE",
                    "ANEURISMO",
                    "BALLOON",
                    "POP-POP",
                    "DILATI"),
                "cat" to listOf(
                    "PUSSYCAT",
                    "WHISKER",
                    "NEKO",
                    "HAIRBALL",
                    "NYAA",
                    "FELISO",
                    "KATINO"),
                "charm" to listOf(
                    "SWEETIE-PIE",
                    "HONEYDEW",
                    "CARMEGI",
                    "AMITIE",
                    "INCANTARE"),
                "command" to listOf("WOULD-YOU-KINDLY",
                        "KOMANDI",
                        "BEFEHLEN",
                        "ULTIMATUM",
                        "CEDI"),
                "cure" to listOf(
                    "HOWZER",
                    "QUE-QUE",
                    "HOPKINS",
                    "NIGHTINGALE",
                    "MEDIC",
                    "TAKE-TWO",
                    "MAYO",
                    "SANIGI",
                    "PANACEA",
                    "PHYSICK"),
                "demon" to listOf(
                    "DEMONIC",
                    "HELLSPAWN",
                    "DOMINATION",
                    "HORN",
                    "DIABLO",
                    "SIX-SIX-SIX",
                    "ASMODEUS"),
                "detect" to listOf(
                    "COLUMBO",
                    "HERLOCK",
                    "GUMSHOE",
                    "ZVARRI",
                    "WHODUNIT",
                    "HOWCATCHEM",
                    "SLEUTH",
                    "WATSON",
                    "MALKOVRO",
                    "SHOLMES"),
                "devastation" to listOf(
                    "CALAMITOUS",
                    "ANNIHILATE",
                    "SCORCHED",
                    "ERASED",
                    "GLASS-IT",
                    "RUINIGI"),
                "dog" to listOf(
                    "BARK-BARK",
                    "DOGGONE",
                    "GOODBOY",
                    "FIDO",
                    "VIRHUNDO",
                    "BALTO",
                    "UPDOG"),
                "elephant" to listOf(
                    "BARBAR",
                    "HANNIBAL",
                    "JAWAHARLAL",
                    "TRUNK",
                    "IVORY",
                    "PAKIDERMO"),
                "exorcism" to listOf(
                    "BEGONE",
                    "EGO-TE-ABSOLVO",
                    "GUTOR",
                    "EXORCIZAMUS-TE",
                    "VADE-RETRO",
                    "ELPELI-DEMONOJN",
                    "CONSTANTINE"),
                "extinguish" to listOf(
                    "SETON",
                    "ORR-N-ORR",
                    "MEREX",
                    "ESTINGAPARATO",
                    "TOOTHPASTE",
                    "SMOKY"),
                "fear" to listOf(
                    "SPOOKUM",
                    "BOO",
                    "GOOSEBUMPS",
                    "TERURI",
                    "MALKURAJI"),
                "fight" to listOf(
                    "SICCUM",
                    "MELEE",
                    "BOPPEM",
                    "GETTEM",
                    "AFFRAY",
                    "INTERBATIJI"),
                "fire" to listOf(
                    "ZIPPO",
                    "SIZZLE",
                    "INFERNO",
                    "COMBUST",
                    "ASHES",
                    "EKBRULIGI"),
                "flail" to listOf(
                    "BASTONEGO",
                    "USE-STICK",
                    "BALL-AND-CHAIN",
                    "KRIEGSFLEGEL",
                    "PYEONGON",
                    "HUSSITE"),
                "flight" to listOf(
                    "SOAR",
                    "WINGED",
                    "DELTA",
                    "TAKEOFF",
                    "VOLARE",
                    "PEANUTS",
                    "FLUGO"),
                "forest" to listOf(
                    "MUSHROOM",
                    "UPROOT",
                    "ARBARO",
                    "MONTEVERDE",
                    "HALLERBOS",
                    "BWINDI",
                    "WAIPOUA"),
                "gaze" to listOf(
                    "EYES-UP-HERE",
                    "AMORSPEKTEMULO",
                    "BOOBA",
                    "DOBONHONKEROS",
                    "PEEKING-TOM"),
                "genderbend" to listOf(
                    "EGGY",
                    "TIT-FOR-TAT",
                    "CISNT",
                    "PRONOUNS",
                    "TRANSSEKSULO"),
                "ghost" to listOf(
                    "FANTOMO",
                    "BOO-DIDDLY",
                    "MOGWAI",
                    "MONONOKE",
                    "DYBBUK",
                    "PEEK-A-BOO",
                    "GEIST"),
                "goat" to listOf(
                    "TIN-CAN",
                    "CAPRICORN",
                    "TANNGRISNIR",
                    "NANNY",
                    "BILLY",
                    "OREAMNOSO"),
                "grow" to listOf(
                    "EMBIGGEN",
                    "SUPER-SIZE",
                    "REDSHROOM",
                    "GRANDIJI",
                    "AMPLIAR",
                    "VERGROTEN"),
                "horse" to listOf(
                    "NEIGHBOR",
                    "GALLOP,",
                    "SADDLE",
                    "CEVALO",
                    "SEABISCUIT",
                    "CLIP-CLOP"),
                "ice" to listOf(
                    "CHILL",
                    "IGLOO",
                    "MAY-TAG",
                    "AMUNDSEN",
                    "SNOWFLAKE",
                    "GLACIO",
                    "FROSTBITE"),
                "insect" to listOf(
                    "CRAWLIES",
                    "HIVE",
                    "EUSOCIAL",
                    "INSEKTOJ",
                    "APHID",
                    "CHRYSALIS",
                    "KATYDID",
                    "TSETSE"),
                "jail" to listOf(
                    "GUANTANAMO",
                    "SING-SING",
                    "DUFFY",
                    "WILLOT",
                    "SHAWSHANK",
                    "SUPERMAX",
                    "GAOL",
                    "MALLIBEREJO"),
                "launch" to listOf(
                    "HOUSTON",
                    "GODDARD",
                    "SLINGSHOT",
                    "BOSSART",
                    "LIFTOFF",
                    "LANCHI"),
                "light" to listOf(
                    "MAG-LITE",
                    "FENIX",
                    "ROMER",
                    "CLAP-ON",
                    "ILUMINI",
                    "LUMIERE"),
                "lightning" to listOf(
                    "ZIP-ZAP",
                    "FARADAY",
                    "TESLA",
                    "COULOMB",
                    "AMPERE",
                    "CEI-U",
                    "FULMO"),
                "lord" to listOf(
                    "MILORD",
                    "LAIRD",
                    "THE-RIGHT-HONOURABLE-THE-LORDS-SPIRITUAL-AND-TEMPORAL-ASSEMBLED",
                    "PATRICIUS",
                    "FEUDESTRO"),
                "luxury" to listOf("GUCCI",
                        "ROYCE",
                        "CHANEL",
                        "LUKSA",
                        "MALSEVERE",
                        "WEELDE",
                        "CADILLAC"),
                "mace" to listOf("WHACKA",
                        "SHARUR",
                        "BAYEUX",
                        "PERNACH",
                        "SHISHPAR",
                        "BULAVA",
                        "KLABO"),
                "magic" to listOf(
                    "ALAKAZAM",
                    "ABRACADABRA",
                    "SIM-SALA-BIM",
                    "BIPPITY-BOPPITY",
                    "SKEDADDLE-SKIDOODLE",
                    "MECCA-LECCA-HI",
                    "SKADOOSH",
                    "PLEASE",
                    "MEDEA"),
                "metal" to listOf(
                    "ALLOY",
                    "CLANK",
                    "TROUVE",
                    "ANTIMONY",
                    "EUREKA",
                    "METALDETEKTILO"),
                "missile" to listOf(
                    "TOMAHAWK",
                    "PEACEKEEPER",
                    "HIROC",
                    "RUBEZH",
                    "SARMAT",
                    "HWASONG",
                    "SURYA",
                    "SKIFF",
                    "SIPER",
                    "THUNDERBIRD",
                    "MORFEY",
                    "VITYAZ",
                    "BOLIDE",
                    "AEGIS",
                    "ASPIDE",
                    "ASRAD"
                ),
                "nullify" to listOf(
                    "VOIDOUT",
                    "NO-SELL",
                    "NUH-UH",
                    "NONONO",
                    "NICE-TRY",
                    "ABROGATE",
                    "ANNULLIEREN",
                    "NULIGI"),
                "obey" to listOf(
                    "SIMON-SAYS",
                    "SKRIBORDONO",
                    "ERLASS",
                    "EDITTO",
                    "MOM-SAID"),
                "observe" to listOf(
                    "VIDI",
                    "LEMME-SEE",
                    "I-SCRY",
                    "ACTION",
                    "ETZPE"),
                "open" to listOf(
                    "SESAME",
                    "FBI",
                    "KNOCK-KNOCK",
                    "APERTI",
                    "AVATA"),
                "owl" to listOf(
                    "NIGHTJAR",
                    "MINERVA",
                    "LAKSHMI",
                    "KAEPORA",
                    "COO",
                    "STRIGO"),
                "passage" to listOf(
                    "SHORTCUT",
                    "TRAIREJO",
                    "QHAPAC",
                    "LIMINATE",
                    "SQUEEZIN"),
                "power" to listOf(
                    "BEEFY",
                    "STRONK",
                    "POTENCO",
                    "WHAMMY",
                    "CHECK-THIS-OUT",
                    "CASTMA",
                    "MALKATENI"),
                "protect" to listOf(
                    "KEVLAR",
                    "KIRASO",
                    "AEGIS",
                    "HEATER",
                    "BARDING",
                    "SUITUP",
                    "IRONCLAD",
                    "PANOPLY"),
                "resurrect" to listOf(
                    "REVIVIJI",
                    "PHOENIX",
                    "MULLIGAN",
                    "ANASTASIS",
                    "ASCLEPIUS",
                    "INDRA"),
                "seal" to listOf(
                    "ENKARCERIGI",
                    "PANDORA",
                    "TARTARUS",
                    "FETTER",
                    "LOCK-EM-UP"),
                "security" to listOf(
                    "GET-AWAY",
                    "PARADISO",
                    "PARADIZEO",
                    "SAFE-SPACE",
                    "NIRVANA",
                    "TIR-NA-NOG"),
                "shadow" to listOf(
                    "UMBRA",
                    "UMTHUNZI",
                    "IILIM",
                    "POURI",
                    "DIMMER",
                    "HADH"),
                "shelter" to listOf(
                    "XANADU",
                    "VERSAILLES",
                    "ALHAMBRA",
                    "TAJ-MAHUL",
                    "PETERHOF",
                    "MYSORE",
                    "POTALA"),
                "sling" to listOf(
                    "STONJETILO",
                    "LACROSSE",
                    "FUSTIBALUS",
                    "BALEAR",
                    "KESTROS"),
                "smite" to listOf(
                    "LUDD",
                    "FRAPI",
                    "PERCUTITE",
                    "TARAW",
                    "HAHAU"),
                "snake" to listOf(
                    "SERPENTETO",
                    "HISSSSS",
                    "ASP",
                    "ADDER",
                    "UROBOROS"),
                "spear" to listOf(
                    "AMENONUHOKO",
                    "TONBOKIRI",
                    "HOPLITE",
                    "PILUM",
                    "ITAGAKI",
                    "IAPETUS",
                    "GUNGNIR",
                    "LONGINUS"),
                "stone" to listOf(
                    "ARGILA",
                    "ANDESITE",
                    "FELDSPAR",
                    "GABBRO",
                    "GNEISS",
                    "LAHAR",
                    "MORAINE",
                    "SCHIST"),
                "strike" to listOf(
                    "BLOCK-THIS-OVERHEAD",
                    "WAPOW",
                    "ATAKO",
                    "KERSPLAT",
                    "ROOSEVELT",
                    "PELIDAE",
                    "THWACK"),
                "stun" to listOf(
                    "PARALIZI",
                    "DOVA",
                    "FLASHBANG",
                    "WHAZZAT",
                    "YOWZA",
                    "CHUCKLENUTS"),
                "summon" to listOf(
                    "SHOUKAN",
                    "FETCH",
                    "SUSAUKIMAS",
                    "HEED",
                    "NGUNDANG",
                    "TAGHAIRM",
                    "COMERE"),
                "teleport" to listOf(
                    "SHUNKAN-IDOU",
                    "WARPZONE",
                    "HERE-THERE",
                    "COCHRANE",
                    "BOP-BAM-BOOM",
                    "SALTO"),
                "transform" to listOf(
                    "DITTO",
                    "WILDCARD",
                    "FUNGE",
                    "DOPPLE",
                    "METAMORPHO",
                    "MUTE",
                    "ALIFORMI",
                    "CHEMOS",
                    "KAERU"),
                "travel" to listOf(
                    "EXPEDIUS",
                    "TRIPLE-A",
                    "PASSPORT",
                    "HERMES",
                    "YUKUE",
                    "VOYAJO",
                    "ODYSSEY"),
                "trick" to listOf(
                    "GOTCHA",
                    "BAZOOPLE",
                    "HACHACHA",
                    "WOKKA-WOKKA",
                    "JOKER",
                    "ITAZURA"),
                "viking" to listOf(
                    "EINHERJAR",
                    "BIFROST",
                    "ASGARD",
                    "VALHALLA",
                    "YGGDRASIL",
                    "BLUETOOTH"),
                "water" to listOf(
                    "SPLISH-SPLASH",
                    "BATH",
                    "NILE",
                    "AKVO",
                    "CULLIGAN",
                    "THAMES",
                    "DANUBE",
                    "YANGTZE",
                    "GULF"),
                "weather" to listOf(
                    "WINDCHILL",
                    "GULFSTREAM",
                    "VETERO",
                    "BARO",
                    "OVERCAST",
                    "ADVECTION",
                    "CORIOLIS",
                    "CUMULUS",
                    "KATABATIC",
                    "THUNDERHEAD"),
                "wind" to listOf(
                    "GALEFORCE",
                    "UPDRAFT",
                    "KAZE",
                    "SQUALL",
                    "WESTERLIES",
                    "VENTO"),
                "wither" to listOf(
                    "ERODE",
                    "SAP",
                    "VELKI",
                    "PUTRA",
                    "DESICCATE")
            )

            commandWordLists.forEach { (themeWord, commandList) ->

                commandList.forEach { commandWord ->

                    spellDao.addCommandWord(CommandWord(commandWord,themeWord))
                }
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

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<Hoard?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoard(hoard: Hoard) : Long

    @Update
    suspend fun updateHoard(hoardToUpdate: Hoard)

    @Delete
    suspend fun deleteHoard(hoardToDelete: Hoard)

    @Query("DELETE FROM hackmaster_hoard_table")
    suspend fun deleteAllHoards()

    @Query("SELECT hoardID FROM hackmaster_hoard_table WHERE ROWID=(:hoardRowID)")
    suspend fun getIdByRowId(hoardRowID: Long) : Int
    // endregion

    // region ( HoardEvent )
    @Query("SELECT * FROM hoard_events_log WHERE hoardID=(:parentHoardId)")
    fun getHoardEvents(parentHoardId: Int) : LiveData<List<HoardEvent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHoardEvent(newEvent: HoardEvent)
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

    @Query("SELECT SUM(currentGPValue) FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGemValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:hoardID)")
    fun getGems(hoardID: Int): LiveData<List<Gem>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    fun getGem(id: Int): LiveData<Gem?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGem(newGem: Gem)

    @Update
    suspend fun updateGem(gemToUpdate: Gem)

    @Delete
    suspend fun deleteGem(gemToDelete: Gem)
    //endregion
}

@Dao
interface ArtDao {

    // region ( ArtObject )
    @Query("SELECT COUNT(*) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtCount(hoardID: Int): LiveData<Int>

    @Query("SELECT SUM(gpValue) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT * FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getArtObjects(hoardID: Int): LiveData<List<ArtObject>>

    @Query("SELECT * FROM hackmaster_art_table WHERE artID=(:artId)")
    fun getArtObject(artId: Int): LiveData<ArtObject?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addArtObject(newArt: ArtObject)

    @Update
    suspend fun updateArtObject(artToUpdate: ArtObject)

    @Delete
    suspend fun deleteArtObject(artToDelete: ArtObject)
    // endregion
}

@Dao
interface MagicItemDao {

    // region ( Templates )
    /**
     * Pulls all item entries lacking a parent belonging to a given type as a LimitedItemTemplate
     *
     * @param type String to match in table_type column
     */
    @Query("SELECT ref_id, wt, is_cursed FROM hackmaster_magic_item_reference WHERE table_type=(:type) AND parent_id=0")
    suspend fun getBaseLimItemTempsByType(type: String): List<LimitedItemTemplate>

    /**
     * Pulls all item entries with given parent ref_id as a LimitedItemTemplate
     *
     * @param parentID Integer primary key id number of parent entry.
     */
    @Query("SELECT ref_id, wt, is_cursed FROM hackmaster_magic_item_reference WHERE table_type=(:parentID)")
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

    @Query("SELECT SUM(gpValue) FROM hackmaster_art_table WHERE hoardID=(:hoardID)")
    fun getMagicItemValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE hoardID=(:hoardID)")
    fun getMagicItems(hoardID: Int): LiveData<List<MagicItem>>

    @Query("SELECT * FROM hackmaster_magic_item_table WHERE mItemID=(:itemID)")
    fun getMagicItem(itemID: Int): LiveData<MagicItem?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMagicItem(newItem: MagicItem)

    @Update
    suspend fun updateMagicItem(itemToUpdate: MagicItem)

    @Delete
    suspend fun deleteMagicItem(itemToDelete: MagicItem)
    // endregion
}

@Dao
interface SpellCollectionDao{

    // region ( SpellTemplate )
    @Query("SELECT * FROM hackmaster_spell_reference WHERE ref_id=(:spellId)")
    suspend fun getSpellTemplate(spellId: Int): SpellTemplate?

    @Query("SELECT * FROM hackmaster_spell_reference WHERE name=(:spellName) AND type=(:discipline) AND level=(:level)")
    suspend fun getSpellTemplateByName(spellName: String, discipline: Int, level: Int): SpellTemplate?

    @Query("SELECT ref_id FROM hackmaster_spell_reference WHERE type=(:discipline) " +
            "AND level=(:level)")
    suspend fun getSpellTemplateIDs(discipline: Int, level: Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpellTemplate(entry: SpellTemplate)
    // endregion

    // region ( SpellCollection )
    @Query("SELECT COUNT(*) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollectionCount(hoardID: Int): LiveData<Int>

    @Query("SELECT SUM(gpValue) FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollectionValueTotal(hoardID: Int): LiveData<Double>

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE hoardID=(:hoardID)")
    fun getSpellCollections(hoardID: Int): LiveData<List<SpellCollection>>

    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE sCollectID=(:spCoId)")
    fun getSpellCollection(spCoId: Int): LiveData<SpellCollection?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpellCollection(newSpellCollection: SpellCollection)

    @Update
    suspend fun updateSpellCollection(spellCollectionToUpdate: SpellCollection)

    @Delete
    suspend fun deleteSpellCollection(spellCollectionToDelete: SpellCollection)
    // endregion

    // region ( CommandWord )
    @Query("SELECT commandWord FROM command_word_suggestions WHERE themeWord=(:theme)")
    suspend fun getThemedCommandWords(theme: String): List<String>

    @Query("SELECT commandWord FROM command_word_suggestions")
    suspend fun getAllCommandWords(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCommandWord(wordToAdd: CommandWord)
    // endregion
}
//endregion
