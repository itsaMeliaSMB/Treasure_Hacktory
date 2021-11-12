package com.example.android.treasurefactory

import android.graphics.drawable.Drawable
import com.example.android.treasurefactory.database.HMMagicItemTemplate
import com.example.android.treasurefactory.model.HMArtObject
import com.example.android.treasurefactory.model.HMMagicItem
import java.util.*
import kotlin.random.Random

class HMTreasureFactory {

    companion object {

        val ANY_MAGIC_ITEM_LIST = listOf(
            "A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16",
            "A17","A18","A21","A24")

        val SAMPLE_MAGIC_ITEM_TEMPLATE = HMMagicItemTemplate(
            0,5,"Robe of Scintillating Colors","GameMaster's Guide",263,
            1250, 1500, 0, "",0,0,0,
            "A10","",0,0,1,1,0,0,
            "","",0,"",0,"",
            0,0,0,0,0,0
        )

        /* NOTE TO SELF: If I'm going to be using generic types for the rules, use the "is" operator
           to confirm a given setting is properly typed. https://kotlinlang.org/docs/typecasts.html */

        //TODO Add discrimination logic for specified gem ranges
        fun createGem(parentHoardID: Int, genRules: Map<String,Any>) {


        }

        /**
         * Returns an art object.
         *
         */
        fun createArtObject(parentHoardID: Int, genRules: Map<String,Any>) : HMArtObject {

            var temporaryRank:  Int
            var ageInYears:     Int
            var ageModifier:    Int

            val artType:        HMArtObject.ArtType
            val renown:         HMArtObject.Renown
            val size:           HMArtObject.Size
            val condition:      HMArtObject.Condition
            val materials:      HMArtObject.Materials
            val quality:        HMArtObject.Quality
            val subject:        HMArtObject.Subject

            val newArt:         HMArtObject

            // --- Roll for type of art object ---

            artType = when (Random.nextInt(1,101)) {

                in 1..5     -> HMArtObject.ArtType.PAPER
                in 6..15    -> HMArtObject.ArtType.FABRIC
                in 16..30   -> HMArtObject.ArtType.FURNISHING
                in 31..45   -> HMArtObject.ArtType.PAINTING
                in 46..60   -> HMArtObject.ArtType.WOOD
                in 61..70   -> HMArtObject.ArtType.CERAMIC
                in 71..80   -> HMArtObject.ArtType.GLASS
                in 81..90   -> HMArtObject.ArtType.STONE
                in 91..99   -> HMArtObject.ArtType.METAL
                else        -> HMArtObject.ArtType.MAGICAL
            }

            //TODO: get resource ID as string for given item type

            // --- Roll for the renown of the artist ---

            renown = when (Random.nextInt(1,101)) {

                in 1..15    -> HMArtObject.Renown.UNKNOWN
                in 16..30   -> HMArtObject.Renown.OBSCURE
                in 31..45   -> HMArtObject.Renown.CITY_RENOWNED
                in 46..65   -> HMArtObject.Renown.REGIONALLY_RENOWNED
                in 66..85   -> HMArtObject.Renown.NATIONALLY_RENOWNED
                in 86..95   -> HMArtObject.Renown.CONTINENTALLY_RENOWNED
                in 96..99   -> HMArtObject.Renown.WORLDLY_RENOWNED
                else        -> HMArtObject.Renown.MOVEMENT_LEADER
            }

            // --- Roll for size of art object ---

            size = when(Random.nextInt(1,101)) {

                in 1..5     -> HMArtObject.Size.TINY
                in 6..25    -> HMArtObject.Size.VERY_SMALL
                in 26..45   -> HMArtObject.Size.SMALL
                in 46..65   -> HMArtObject.Size.AVERAGE
                in 66..85   -> HMArtObject.Size.LARGE
                in 86..90   -> HMArtObject.Size.VERY_LARGE
                in 91..96   -> HMArtObject.Size.HUGE
                in 97..99   -> HMArtObject.Size.MASSIVE
                else        -> HMArtObject.Size.GARGANTUAN
            }

            // --- Roll for quality of materials used ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + renown.valueMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            materials = when (temporaryRank) {

                0       -> HMArtObject.Materials.AWFUL
                1       -> HMArtObject.Materials.POOR
                2       -> HMArtObject.Materials.BELOW_AVERAGE
                3       -> HMArtObject.Materials.AVERAGE
                4       -> HMArtObject.Materials.ABOVE_AVERAGE
                5       -> HMArtObject.Materials.GOOD
                6       -> HMArtObject.Materials.EXCELLENT
                7       -> HMArtObject.Materials.FINEST
                else    -> HMArtObject.Materials.UNIQUE
            }

            // --- Roll for quality of work done ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + renown.valueMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            quality = when (temporaryRank) {

                0       -> HMArtObject.Quality.AWFUL
                1       -> HMArtObject.Quality.POOR
                2       -> HMArtObject.Quality.BELOW_AVERAGE
                3       -> HMArtObject.Quality.AVERAGE
                4       -> HMArtObject.Quality.ABOVE_AVERAGE
                5       -> HMArtObject.Quality.GOOD
                6       -> HMArtObject.Quality.EXCELLENT
                7       -> HMArtObject.Quality.BRILLIANT
                else    -> HMArtObject.Quality.MASTERPIECE
            }

            // --- Roll for age of artwork ---

            ageInYears =
                rollPenetratingDice(5,20,0).getRollTotal() *       // 5d20 x 1d4, penetrate on all rolls
                    rollPenetratingDice(1,4,0).getRollTotal()

            if (ageInYears < 0) { ageInYears = 0 }

            ageModifier = when (ageInYears) {

                in 0..25        -> -2
                in 26..75       -> -1
                in 76..150      -> 0
                in 151..300     -> 1
                in 301..600     -> 2
                in 601..1500    -> 3
                in 1500..3000   -> 4
                else            -> 5
            } + artType.ageMod

            if (ageModifier < -2) { ageModifier = -2 } else
                if (ageModifier > 5) { ageModifier = 5 }

            // --- Roll for condition of art object ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + artType.conditionMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            condition = when (temporaryRank) {

                0       -> HMArtObject.Condition.BADLY_DAMAGED
                1       -> HMArtObject.Condition.DAMAGED
                2       -> HMArtObject.Condition.WORN
                3       -> HMArtObject.Condition.AVERAGE
                4       -> HMArtObject.Condition.GOOD
                5       -> HMArtObject.Condition.EXCELLENT
                6       -> HMArtObject.Condition.NEAR_PERFECT
                7       -> HMArtObject.Condition.PERFECT
                else    -> HMArtObject.Condition.FLAWLESS
            }

            // --- Roll for subject matter of art object ---

            subject = when (Random.nextInt(1,101)) {

                in 1..10    -> HMArtObject.Subject.ABSTRACT
                in 11..20   -> HMArtObject.Subject.MONSTER
                in 21..30   -> HMArtObject.Subject.HUMAN
                in 31..50   -> HMArtObject.Subject.NATURAL
                in 51..70   -> HMArtObject.Subject.HISTORICAL
                in 71..90   -> HMArtObject.Subject.RELIGIOUS
                in 91..99   -> HMArtObject.Subject.NOBLE
                else        -> HMArtObject.Subject.ROYALTY
            }

            // ---Generate and return new art object ---

            return HMArtObject(0, parentHoardID,"",artType,renown,size,condition,materials,quality,ageInYears,
                subject,(renown.valueMod + size.valueMod + materials.valueMod + quality.valueMod +
                        ageModifier + condition.valueMod + subject.valueMod))
        }

        /**
         * Returns a magic item. Returns a "Nothing" item if an error is encountered.
         *
         * @param givenTemplate Primary key to query for a specific item. Negative values are ignored.
         * @param _allowedTypes Tables that are allowed to be queried to pick an item.
         * @param mapSubChance Percentage chance of replacing a scroll with a treasure map. Can generate only maps and no scrolls.
         */
        fun createMagicItem(parentHoardID: Int, givenTemplate: Int = -1, providedTypes: List<String> = ANY_MAGIC_ITEM_LIST, mapSubChance :Int = 0,
                            genRules: Map<String,Any>) {

            val VALID_TABLE_TYPES = linkedSetOf<String>(
                "A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16",
                "A17","A18","A21","A24")
            val EXCEPTIONAL_ITEMS = setOf("Ring of Spell Storing", "Gut Stones", "Ioun Stone")

            var template:       HMMagicItemTemplate = SAMPLE_MAGIC_ITEM_TEMPLATE

            val baseTemplateID: Int       // Container for primary key of the first template drawn
            var itemType:       String
            var itemCharges=    0
            var gmChoice =      false

            var currentRoll:    Int

            val notesLists=     mutableMapOf("names" to arrayListOf<String>())

            // region Magic item detail holders

            val mTemplateID: Int
            val mHoardID = 0 // TODO Inline this later; hoard ID can be added outside this function
            var mIconID: String
            val mTypeOfItem: String
            val mName: String
            val mSourceText: String
            val mSourcePage: Int
            var mXpValue: Int
            var mGpValue: Double
            val mClassUsability: Map<String,Boolean>
            val mIsCursed: Boolean
            val mAlignment: String
            val mNotes: List<List<String>>
            val mUserNotes = emptyList<String>()
            val mDrawable: Drawable

            // endregion

            // region [ Generate Weighted Table A1 list with only allowed item types ]

            val allowedTypes = if (mapSubChance > 0) {
                VALID_TABLE_TYPES.filter { providedTypes.contains(it) } + listOf("A3")
            } else {
                VALID_TABLE_TYPES.filter { providedTypes.contains(it) }
            }

            fun getWeightedProbabilityTable(): List<IntRange> {

                val TABLE_A1_WEIGHT = mapOf(
                    "A2" to 20,
                    "A3" to 15,
                    "A4" to 5,
                    "A5" to 1,
                    "A6" to 1,
                    "A7" to 3,
                    "A8" to 1,
                    "A9" to 2,
                    "A10" to 2,
                    "A11" to 2,
                    "A12" to 1,
                    "A13" to 2,
                    "A14" to 1,
                    "A15" to 1,
                    "A16" to 1,
                    "A17" to 2,
                    "A18" to 15,
                    "A21" to 24,
                    "A24" to 1)

                val listBuilder = mutableListOf<IntRange>()

                var lastMax = 0
                var weight = 0

                allowedTypes.forEach { allowedKey ->

                    weight = TABLE_A1_WEIGHT[allowedKey]!!

                    listBuilder.add(IntRange(lastMax+1, lastMax + weight))

                    lastMax += weight
                }

                return listBuilder.toList()
            }

            val allowedProbabilities = getWeightedProbabilityTable()

            //endregion

            // region [ Determine type of magic item ]

            if (allowedTypes.isNotEmpty()) {

                currentRoll = Random.nextInt(1,allowedProbabilities.last().last + 1)

                itemType =
                    allowedTypes[ allowedProbabilities.indexOfFirst{ it.contains(currentRoll) } ]

            } else {

                itemType = "INVALID"
            }

            // endregion

            // region [ If applicable, check for special generation rules ]

            fun generateSpellScrollDetails(inheritedRoll:Int,
                                           inheritedAffinity: Boolean? = null): ArrayList<String> {

                val useArcane = inheritedAffinity ?: Random.nextBoolean()
                val halvedIndex: Int = if (inheritedRoll < 2) 1 else inheritedRoll / 2
                val spellCount : Int
                val spellRange: IntRange

                //TODO move all of this over to spell collection generator and replace this with inherited roll

                when (halvedIndex) {

                    1 -> { spellCount = 1
                        spellRange = IntRange(1,4)}

                    2 -> { spellCount = 1
                        spellRange = IntRange(1,6)}

                    3 -> { spellCount = 1
                        spellRange = if (useArcane) IntRange(2,9) else IntRange(2,7)}

                    4 -> { spellCount = 2
                        spellRange = IntRange(1,4)}

                    5 -> { spellCount = 2
                        spellRange = if (useArcane) IntRange(2,9) else IntRange(2,7)}

                    6 -> { spellCount = 3
                        spellRange = IntRange(1,4)}

                    7 -> { spellCount = 3
                        spellRange = if (useArcane) IntRange(2,9) else IntRange(2,7)}

                    8 -> { spellCount = 4
                        spellRange = IntRange(1,6)}

                    9 -> { spellCount = 4
                        spellRange = if (useArcane) IntRange(1,8) else IntRange(1,6)}

                    10 -> { spellCount = 5
                        spellRange = IntRange(1,6)}

                    11 -> { spellCount = 5
                        spellRange = if (useArcane) IntRange(1,8) else IntRange(1,6)}

                    12 -> { spellCount = 6
                        spellRange = IntRange(1,6)}

                    13 -> { spellCount = 6
                        spellRange = if (useArcane) IntRange(3,8) else IntRange(3,6)}

                    14 -> { spellCount = 7
                        spellRange = IntRange(1,8)}

                    15 -> { spellCount = 7
                        spellRange = if (useArcane) IntRange(2,9) else IntRange(2,7)}

                    16 -> { spellCount = 7
                        spellRange = if (useArcane) IntRange(4,9) else IntRange(4,7)}

                    else -> { spellCount = Random.nextInt(1,8)
                        spellRange = if (useArcane) IntRange(0,9) else IntRange(1,7)}
                }

                return arrayListOf(
                    "spell type = ${if (useArcane) "Magic-User" else "Cleric"}",
                    "number of spells = $spellCount",
                    "spell level range = ${spellRange.first} to ${spellRange.last}")
            }

            when (itemType) {

                "A2" -> { if (Random.nextInt(1, 101) == 100) gmChoice = true }

                "A3" -> {

                    if ((mapSubChance > 0) &&
                        (Random.nextInt(1, 101) <= mapSubChance)
                    ) {

                        itemType = "Map"
                        //TODO call map generation instructions function

                    } else {

                        if ((providedTypes.contains("A3"))) {

                            currentRoll = Random.nextInt(1, 101)

                            if (currentRoll <= 33) {

                                //Spell scroll result
                                itemType = "SpellScroll"
                                notesLists["scroll details"] = generateSpellScrollDetails(currentRoll) //TODO implement

                            } else {

                                if (currentRoll in 85..91) {

                                    itemType = "Cursed Scroll"
                                    //TODO add complete generation of cursed scroll generation

                                } else if (currentRoll >= 96) {

                                    gmChoice = true

                                }
                            }
                        }
                    }
                }

                "A4" -> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A5" -> { if (Random.nextInt(1, 21) == 20) gmChoice = true }

                "A6" -> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A7" -> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A8" -> { if (Random.nextInt(1, 21) >= 19) gmChoice = true }

                "A9" -> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A10"-> { if (Random.nextInt(1, 101) >= 98) gmChoice = true }

                "A11"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A12"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A13"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A14"-> { if (Random.nextInt(1, 101) >= 98) gmChoice = true }

                "A15"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A16"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A17"-> { if (Random.nextInt(1, 101) >= 99) gmChoice = true }

                "A18"-> {

                    if (Random.nextInt(1, 21) == 20){

                        itemType = "A20"

                        if (Random.nextInt(1, 21) == 20) gmChoice = true
                    }
                }

                "A20" -> { if (Random.nextInt(1, 21) == 20) gmChoice = true }

                "A21"-> {

                    if (Random.nextInt(1, 101) >= 99){

                        itemType = "A22"

                        if (Random.nextInt(1, 1001) >= 997) gmChoice = true
                    }
                }

                "A23"-> { if (Random.nextInt(1, 1001) >= 997) gmChoice = true }

                "A24"-> { if (Random.nextInt(1, 101) >= 97) gmChoice = true }

                else -> itemType = "INVALID"
            }

            // endregion

            // region [ Pull template from database ] TODO

            if ((itemType != "INVALID")&&(VALID_TABLE_TYPES.contains(itemType))&&!(gmChoice)){

                // Pull base entry from appropriate table. TODO

                // Set pulled template's refID as parent reference ID
                baseTemplateID = template.refId

                // Pull child entry if they exist TODO
                while (template.hasChild != 0) {

                    // Try to pull child listing. TODO
                    // On catch, return base listing, but with hasChild = 0 TODO

                }

            } else { // otherwise, pull proper presets for magic items without templates TODO

                baseTemplateID = -1

            }

            // endregion

            // region [ Generate valid magic item ] TODO

            if ((baseTemplateID != -1)&&!(EXCEPTIONAL_ITEMS.contains(template.name))) {

                // Use template to populate magic item's details

                mName = template.name //TODO add exception/replace for polearm
                mSourceText = template.source
                mSourcePage = template.page
                mTypeOfItem = template.tableType
                mIsCursed = (template.isCursed == 1)
                mClassUsability= mapOf(
                    "Fighter" to (template.fUsable == 1),
                    "Thief" to (template.tUsable == 1),
                    "Cleric" to (template.cUsable == 1),
                    "Magic-User" to (template.mUsable == 1),
                    "Druid" to (template.dUsable == 1)
                )
                mIconID = template.iconRef //TODO add db table for looking up resource ID from this keyword
                mAlignment = template.alignment //TODO convert from abbreviation

                // region [ Roll charges/uses, if applicable ]

                if (template.dieSides > 0){

                    repeat (template.dieCount) {
                        itemCharges += Random.nextInt(1,template.dieSides + 1)
                    }
                }

                itemCharges += template.dieMod

                // endregion

                // region [ Determine XP and GP value of item ]

                when (template.multiType) {

                    0 -> { mXpValue = template.xpValue
                        mGpValue = template.gpValue.toDouble() }

                    1 -> { mXpValue = (template.xpValue * itemCharges)
                        mGpValue = template.gpValue.toDouble() }

                    2 -> { mXpValue = (template.xpValue * itemCharges)
                        mGpValue = (template.gpValue * itemCharges).toDouble() }

                    3 -> { mXpValue = (template.xpValue * itemCharges)
                        mGpValue = (template.gpValue * itemCharges).toDouble() }

                    else-> { mXpValue = template.xpValue
                        mGpValue = template.gpValue.toDouble() }
                }

                // endregion

                // region [ Roll up "Additional Notes" ]

                if (template.imitationKeyword.isNotBlank()){

                    val keywords = template.imitationKeyword.split(";")

                    val imitationTemplate: HMMagicItemTemplate

                    // Query DB for item imitate TODO
                    // keywords[Random.nextInt(0,keywords.size)]
                    imitationTemplate = SAMPLE_MAGIC_ITEM_TEMPLATE

                    val useAn : Boolean = setOf("a","e","i","o","u")
                        .contains(imitationTemplate.name.substring(0,endIndex = 1))

                    notesLists["Additional notes"]?.plusAssign(
                        "Appears to be ${if (useAn) "an" else "a"} ${imitationTemplate.name}"
                    )
                }

                if (template.notes.isNotBlank()) {

                    val splitNotes = template.notes.split("¶")

                    splitNotes.forEach { notesLists["Additional notes"]?.plusAssign(it) }
                }

                if ((itemCharges > 0)&&(itemType != "A2")) {

                    notesLists["Additional notes"]?.plusAssign("Found with $itemCharges charges/uses remaining")

                } else {

                    if (itemType == "A2") {

                        notesLists["Additional notes"]?.plusAssign("Check \"Potion " +
                                "flavor text\" for remaining doses.")
                    }
                }

                if (template.commandWord.isNotBlank()){

                    val keywords = template.commandWord.split(";")

                    notesLists["Additional notes"]?.plusAssign("Has ${keywords.size} command word" +
                            (if (keywords.size > 1) {
                                "s. Here are some randomly-generated suggestions:"
                            } else {"Here is a randomly-generated suggestion:"})
                    )

                    // Pull a word from API for random generation TODO
                    keywords.forEach { "${it.uppercase()} (based on \"$it\")" }
                }

                // endregion

                // region [ Roll up "Potion flavor text" ]

                if (itemType == "A2") {

                    val COLOR_MAP =  mapOf<Int,String>(
                        1 to "amber",
                        2 to "amethyst",
                        3 to "apricot",
                        4 to "aquamarine",
                        5 to "auburn",
                        6 to "azure blue",
                        7 to "black",
                        8 to "blue",
                        9 to "bone [white]",
                        10 to "bronze",
                        11 to "brass",
                        12 to "brown",
                        13 to "buff [leather]",
                        14 to "carmine",
                        15 to "cerise",
                        16 to "cerulean",
                        17 to "cherry",
                        18 to "chestnut",
                        19 to "chocolate",
                        20 to "cinnabar",
                        21 to "citrine",
                        22 to "colorless",
                        23 to "copper",
                        24 to "coral",
                        25 to "cream",
                        26 to "crimson",
                        27 to "dove",
                        28 to "dun [tan]",
                        29 to "ebony",
                        30 to "ecru [beige]",
                        31 to "emerald",
                        32 to "fallow brown",
                        33 to "fawn",
                        34 to "flame",
                        35 to "flaxen",
                        36 to "fog",
                        37 to "fuchsia",
                        38 to "ginger",
                        39 to "gold",
                        40 to "golden",
                        41 to "grassy",
                        42 to "gray/grey",
                        43 to "green",
                        44 to "heliotrope [purple]",
                        45 to "henna [red-brown]",
                        46 to "indigo",
                        47 to "inky",
                        48 to "iron",
                        49 to "ivory",
                        50 to "jade",
                        51 to "lake",
                        52 to "lavender",
                        53 to "lilac",
                        54 to "lime",
                        55 to "madder",
                        56 to "magenta",
                        57 to "\"mahawgany\"",
                        58 to "maroon",
                        59 to "mauve",
                        60 to "neutral",
                        61 to "ochre",
                        62 to "olive",
                        63 to "orange",
                        64 to "parchment",
                        65 to "peach",
                        66 to "pearl",
                        67 to "pewter",
                        68 to "pink",
                        69 to "pitch black", // nice.
                        70 to "plum",
                        71 to "purple",
                        72 to "purple",
                        73 to "red",
                        74 to "rose",
                        75 to "ruby",
                        76 to "russet",
                        77 to "rust",
                        78 to "sable",
                        79 to "saffron",
                        80 to "salmon",
                        81 to "sand",
                        82 to "sanguine",
                        83 to "sapphire",
                        84 to "scarlet",
                        85 to "silver",
                        86 to "sky",
                        87 to "soot",
                        88 to "sorrel [chestnut]",
                        89 to "steel",
                        90 to "straw",
                        91 to "tan",
                        92 to "tawny",
                        93 to "teal",
                        94 to "terra cotta",
                        95 to "turquoise",
                        96 to "ultramarine",
                        97 to "vermilion",
                        98 to "white",
                        99 to "woolen",
                        100 to "yellow"
                    )

                    var isRoundBulb = false
                    val material =   if (Random.nextInt(1,11) <= 4) { "Crystal" }
                        else { "Glass" }
                    var isSealed = false

                    fun checkIfNullified(chance: Int): String {

                        return if (Random.nextInt(1,101) <= chance) {
                            "FAILED"
                        } else {
                            "PASSED"
                        }
                    }

                    fun checkAlreadyQuaffedDoses(): Int {

                        return if (Random.nextInt(1,101) <= 75) {Random.nextInt(1,4)} else { 0 }
                    }

                    fun getSubstanceColor(): String? = COLOR_MAP[Random.nextInt(1,101)]

                    // --- Roll for potion container ---

                    when (Random.nextInt(1,101)) {

                        in 1..2     -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Porcelain Jar")
                        }

                        in 3..4     -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Alabaster")
                        }

                        in 5..12    -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Round, long container")
                        }

                        in 13..14   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Square, long container")
                        }

                        in 15..19   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Conical bulb")

                            isRoundBulb = true
                        }

                        in 20..48   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Spherical bulb")

                            isRoundBulb = true
                        }

                        in 49..56   -> {
                            notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Oval, 'squashed' bulb")

                            isRoundBulb = true
                        }

                        in 57..60   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Tapering neck, Conical flask")
                        }

                        in 61..65   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Triangular bulb")
                        }

                        in 66..69 -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("$material - Triangular, long container")
                        }

                        in 70..73   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("$material - Twisted, almost misshapen neck, small diamond bulb")
                        }

                        in 74..76   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Test-tube style")
                        }

                        in 77..78   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Lone wine bottle (Green glass):")

                            itemCharges = Random.nextInt(1,4)

                            notesLists["Potion flavor text"]
                                ?.plusAssign(
                                    "This container holds $itemCharges dose(s) of " +
                                            "potion - all f the same type. Liquid color is " +
                                            "not immediately discernible. [1]")
                        }

                        in 79..80   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Rectangular bulb")
                        }

                        in 81..84   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Hexagonal, long container")
                        }

                        in 85..86 -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Curved, swirl bulb")

                            isRoundBulb = true
                        }

                        in 87..88   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Rectangular, long container")
                        }

                        89          -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: $material - Long necked bottle " +
                                        "with ${Random.nextInt(2,5)} round bulbs, " +
                                        "decreasing in size as they go up the bottle, all " +
                                        "totalling one dose of potion.")

                            itemCharges = 1

                            isRoundBulb = true
                        }

                        90          -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Metal - Sealed tankard:")
                        }

                        in 91..92   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Metal - Flask")

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Unless opened, these containers " +
                                        "prevent the potion from being viewed. [2]")
                        }

                        in 93..95   -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Special - Unusually shaped ${material.lowercase()} " +
                                    "(heart, skull, Apple, Standing Nymph, etc.)")
                        }

                        96          -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Special - Berry Form:")

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Imported from the desert Wurld of " +
                                        "Hackthas, these sparkling preserved berries are " +
                                        "often found preserved within glass bottles, and " +
                                        "must be fully consumed in order for their magic to " +
                                        "take effect. For some reason, these forms are " +
                                        "mainly prunes or dusty-tasting peach-like fruits. [3]")
                        }

                        97          -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: Special - Wooden or ${material.lowercase()} " +
                                        "charm hanging from a chain:")

                            isSealed = true

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Aside from the liquid within, these " +
                                        "necklaces are non-magical, but must be snapped in " +
                                        "order to access the potion within. Any number of " +
                                        "these may be worn around the neck without " +
                                        "interference with either each other or any other " +
                                        "magical pendants. These types of containers are " +
                                        "designed to be worn around a character’s neck. [4]")
                        }

                        else        -> {
                            notesLists["Potion flavor text"]
                                ?.plusAssign("Container: GM's option")
                        }
                    }

                    // --- Roll for ridges ---

                    if (Random.nextInt(1,101) <= 30) {

                        notesLists["Potion flavor text"]
                            ?.plusAssign("Has ${Random.nextInt(1,7)} ridge(s) " +
                                    "incorporated into their shape – either ribbed as vertical " +
                                    "ridges running parallel with one another, or small, fancy " +
                                    "decorative layers on top of the bulb")
                    }

                    // --- Roll for potion container sealant ---

                    when (Random.nextInt(if (isSealed) 12 else 1,101)) {

                        1           -> {

                            notesLists["Potion flavor text"]
                            ?.plusAssign("Sealant: No seal:")

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Potions liquid has dried up – the residue " +
                                        "can be re-hydrated, but only for half strength; 30% " +
                                        "chance all magic is nullified. (This one " +
                                        "${checkIfNullified(30)} its check)")
                        }

                        in 2..11    -> {

                            notesLists["Potion flavor text"]
                            ?.plusAssign("Sealant: Seal broken:")

                            notesLists["Potion flavor text"]
                                ?.plusAssign("70% chance 1-3 doses of the potion (if the " +
                                        "container holds more than one) have already been " +
                                        "quaffed (Result: ${checkAlreadyQuaffedDoses()} already " +
                                        "used). 5% chance all magic is nullified. (This one " +
                                        "${checkIfNullified(5)} its check)")
                        }

                        in 12..62   -> {

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Sealant: Corked")
                        }

                        in 63..82   -> {

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Sealant: Corked and waxed")
                        }

                        in 83..88   -> {

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Sealant: Glass stopper")
                        }

                        in 87..93   -> {

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Sealant: Corked with a chain that is attached to the bottle")
                        }

                        else        -> {

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Sealant: Sealed glass neck:")

                            notesLists["Potion flavor text"]
                                ?.plusAssign("Neck must be broken before use, one-chance " +
                                        "only before becoming useless. Characters drinking " +
                                        "straight from a roughly shattered neck suffer 1d3-1 hit " +
                                        "points of damage. Speed Penalty - an extra 2 segments " +
                                        "in combat must be spent opening a bottle like this. " +
                                        "(See footnote for Table HJ21-B)")

                        }

                    }

                    // --- Roll for bottle embellishments ---

                    if (Random.nextInt(1,101) <= Random.nextInt(10,16)) {

                        do {

                            currentRoll = Random.nextInt(1,21)      // Reroll until applicable embellishment is rolled

                        } while ((currentRoll == 16)&&!(isRoundBulb))

                        when (currentRoll) {

                            1       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: A minute but detailed " +
                                            "etching of a grand battle runs around the base of " +
                                            "the bottle")
                            }

                            2       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Potion bottle constructed of " +
                                            "four parallel, differently colored glasses")
                            }

                            3       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: All of the bulb/neck, " +
                                            "except the base, is sheathed in fine/once-fine silk " +
                                            "held in place with tiny silver studs")
                            }

                            4       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: " +
                                            "${Random.nextInt(1,9)} fancy beads hang " +
                                            "down an inch from the bottleneck base on golden " +
                                            "threads")
                            }

                            5       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: The " +
                                            "${material.lowercase()} for the bottle appears " +
                                            "spider-webbed with cracks, but is as strong as a " +
                                            "normal ${material.lowercase()} bottle")
                            }

                            6       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: A name and ‘title’ " +
                                            "(e.g. Magnuson the Flamehard) are found etched in " +
                                            "the glass in fine cursive script")
                            }

                            7       -> {

                                val addlGems = Random.nextInt(1,5)

                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: $addlGems 20gp gems are " +
                                            "studded into the bottle in a symmetrical style " +
                                            "around it")

                                mGpValue += (addlGems * 20.0)
                            }

                            8       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Arcane looking " +
                                            "(but harmless) runes encircle the top of the bulb – " +
                                            "actually spells out a domestic, household item in a " +
                                            "long-extinct language")
                            }

                            9       -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: A series of metallic " +
                                            "carved runes strung together on a silver wire run " +
                                            "down 3 parts of the bottle")
                            }

                            10      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Potion bottle has a " +
                                            "small handle in the shape of a swan between the top " +
                                            "of bulb and neck")
                            }

                            11      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Small wisps of smoke " +
                                            "can be constantly seen emanating from the bottle, " +
                                            "which itself is always cold to the touch with a " +
                                            "thin layer of ice on its surface")
                            }

                            12      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Bottle constructed of " +
                                            "extremely bright and vastly differently " +
                                            "psychedelically colored glasses in swirls, stars " +
                                            "and odd patches")
                            }

                            13      -> {

                                fun getInitial(): String {

                                    val alphabet = listOf("A","B","C","D","E","F","G","H","I","J",
                                        "K","L","M","N","O","P","Q","R","S","T","U","V","W","X",
                                        "Y","Z")

                                    return alphabet[Random.nextInt(0,alphabet.size)]
                                }

                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: 3 prominent initials " +
                                            "molded into the bottle glass during creation: " +
                                            "\"${getInitial()}.${getInitial()}.${getInitial()}.\"")
                            }

                            14      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Bottle held within 2 " +
                                            "thing bands of gold, along with small legs at base " +
                                            "of bulb")
                            }

                            15      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: A Gawd’s symbol is " +
                                            "inscribed within the glass or pulled out of the " +
                                            "glass during creation (most commonly a key, the " +
                                            "symbol of Hokalas)")
                            }

                            16      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: An outline of the " +
                                            "continent is etched around the bulb, with a tiny X " +
                                            "somewhere on it.")
                                // Consider making this have map properties TODO
                            }

                            17      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Top of the bottle neck " +
                                            "and cork are coated in a patterned silver metal")
                            }

                            18      -> {
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: The potion bottle is " +
                                            "actually a test-tube style one inside the result " +
                                            "rolled, the outer one filled with a liquid of a " +
                                            "[potentially] very different color " +
                                            "(${getSubstanceColor()}) to the potion within")
                            }

                            19      ->{
                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: Bottle has a stopper in " +
                                            "the shape of a butterfly in flight, dragonfly, or " +
                                            "similar unusual design")
                            }

                            else    -> {

                                val addlGems = 4 + Random.nextInt(1,9)

                                notesLists["Potion flavor text"]
                                    ?.plusAssign("Embellishment: $addlGems small gems " +
                                            "(10gp ea.) adorn the bottle on one side in a " +
                                            "geometric pattern")

                                mGpValue += (addlGems * 10.0)
                            }
                        }
                    }

                    // --- Roll potion consistency ---

                    notesLists["Potion flavor text"]
                        ?.plusAssign("Substance consistency: " +
                                when (Random.nextInt(1,101)) {

                        in 1..19    -> "Bubbling"
                        in 20..29   -> "Cloudy"
                        in 30..39   -> "Effervescent"
                        in 40..49   -> "Fuming"
                        in 50..54   -> "Oily"
                        in 55..64   -> "Smoky"
                        in 65..74   -> "Syrupy"
                        in 75..79   -> "Vaporous"
                        in 80..84   -> "Viscous"
                        else        -> "Watery"

                        })

                    // --- Roll potion appearance/color(s) ---

                    notesLists["Potion flavor text"]
                        ?.plusAssign("Appearance (unless entry says otherwise): " +
                                when (Random.nextInt(1,101)) {

                        in 1..29    -> "Clear (transparent)"
                        in 30..34   -> "Flecked (transparent ${getSubstanceColor()} with " +
                                "${getSubstanceColor()} flecks)"
                        in 35..39   -> "Layered (${getSubstanceColor()} to ${getSubstanceColor()})"
                        in 40..54   -> "Luminous (${getSubstanceColor()}, " +
                                "~${Random.nextInt(0,21) * 5}% opacity)"
                        in 55..59   -> "Opaline (glowing)"
                        in 60..69   -> "Phosphorescent (${getSubstanceColor()}, " +
                                "~${Random.nextInt(0,21) * 5}% opacity)"
                        in 70..79   -> "Rainbowed (transparent)"
                        in 80..84   -> "Ribboned (${getSubstanceColor()}, " +
                                "~${Random.nextInt(0,21) * 5}% opacity)"
                        in 85..94   -> "Translucent (${getSubstanceColor()})"
                        else        -> "Varigated (${getSubstanceColor()}, " +
                                "${getSubstanceColor()}, and maybe some ${getSubstanceColor()})"
                    } )

                    // --- Roll potion taste/odor ---

                    notesLists["Potion flavor text"]
                        ?.plusAssign( "Taste and/or Odor (unless entry says otherwise): " +
                                when (Random.nextInt(1,101)) {

                        in 1..3     -> "Acidic"
                        in 4..5     -> "Billious"
                        in 6..10    -> "Bitter"
                        in 11..14   -> "Bland"
                        in 15..16   -> "Burning/biting"
                        in 17..18   -> "Buttery"
                        in 19..20   -> "Dusty"
                        in 21..22   -> "Earthy"
                        in 23..26   -> "Fiery"
                        in 27..29   -> "Fishy"
                        in 30..32   -> "Greasy"
                        in 33..34   -> "Herbal"
                        in 35..39   -> "Honeyed"
                        in 40..42   -> "Lemony"
                        in 43..46   -> "Meaty"
                        in 47..49   -> "Metallic"
                        in 50..51   -> "Milky"
                        in 52..53   -> "Musty"
                        in 54..56   -> "Oniony"
                        in 57..60   -> "Peppery"
                        in 61..62   -> "Perfumy"
                        in 63..65   -> "Pickled"
                        in 66..69   -> "Rotten"
                        in 70..72   -> "Salty"
                        in 73..75   -> "Smoked"
                        in 76..80   -> "Soothing/sugary"
                        in 81..83   -> "Sour"
                        in 84..88   -> "Spicy"
                        in 89..92   -> "Sweet"
                        in 93..95   -> "Tart"
                        in 96..97   -> "Vinegary"
                        in 98..100  -> "Watery"
                        else        -> "I dunno, a bit like strawberries?"

                    } )

                    // --- Add dosages remaining ---

                    notesLists["Potion flavor text"]
                            ?.plusAssign("Found with $itemCharges dose(s) remaining")

                }

                // endregion

                // region [ Roll "Intelligent weapon info" ]

                // endregion

                // region [ Roll artifact powers and effects ]

                if (itemType == "A24") {

                    if (template.multiType == 4) {
                        notesLists["Artifact particulars"]?.plusAssign(
                            "THIS IS A HACKMASTER-CLASS ITEM.")
                    }

                    // Roll up artifact powers and effects TODO
                }

                // endregion

            } else {

                // Generate magic item details for outliers and exceptional items TODO

            }

            //endregion

            // region [ Convert mapped lists to nested list ] TODO

            // endregion
        }

        /**
         * Converts a provided HMMagicItem into a SpellCollection based on the item's notes[2] value.
         */
        fun convertItemToSpellCollection(inputItem: HMMagicItem) {

            //TODO not yet implemented. Should use provided xp and int values unless otherwise instructed.
        }

        fun createSpellCollection(constrainedRules: Boolean = false) {}
    }
}
