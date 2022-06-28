package com.example.android.treasurefactory

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.android.treasurefactory", appContext.packageName)
    }
}
/*
@RunWith(AndroidJUnit4::class)
class DatabaseTemplatesTest {

    //https://developer.android.com/kotlin/coroutines/test
    //https://developer.android.com/training/data-storage/room/testing-db
    //https://developer.android.com/training/data-storage/room/prepopulate

    val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var testDB: TreasureDatabase
    private lateinit var testRepo: HMRepository

    @Before
    fun initDb() {
        testDB = Room.inMemoryDatabaseBuilder(
            appContext, TreasureDatabase::class.java
        ).allowMainThreadQueries().build() //TODO still needs pre-population or DIs for specific tests
    }

    // Populate desired templates
    /** Populates gem template table using hardcoded CSV file. */
    suspend fun populateGemsByCSV(gemDao: GemDao) {

        // val csvFilePath = "src/main/res/raw/seed_gem_v01.csv"
        var iterationCount = 0

        val inputStream = appContext.resources.openRawResource(
            appContext.resources.getIdentifier("seed_gem_v01","raw",appContext.packageName))

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

        var iterationCount = 0

        val inputStream = appContext.resources.openRawResource(
            appContext.resources.getIdentifier("seed_magicitems_v02","raw",appContext.packageName))

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

        val inputStream = appContext.resources.openRawResource(
            appContext.resources.getIdentifier("seed_spell_v01","raw",appContext.packageName))

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

    @After
    fun closeDb(){
        testDB.close()
    }

    //TODO write tests
    fun magicItemFromTemplate_childless_returnsSameTemplateID(){
    }

    fun magicItemFromTemplate_child_allChildrenValid(){
    }



    //
}*/