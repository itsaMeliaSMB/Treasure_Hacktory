package com.example.android.treasurefactory

import com.example.android.treasurefactory.database.HMMagicItemTemplate
import com.example.android.treasurefactory.model.HMArtObject
import com.example.android.treasurefactory.model.HMMagicItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

const val ORDER_LABEL_STRING = "order_details"

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
         * @param providedTypes Tables that are allowed to be queried to pick an item.
         * @param mapSubChance Percentage chance of replacing a scroll with a treasure map. Can generate maps even when A3 is disallowed.
         */
        fun createMagicItem(parentHoardID: Int, givenTemplate: Int = -1, providedTypes: List<String> = ANY_MAGIC_ITEM_LIST, mapSubChance :Int = 0,
                            genRules: Map<String,Any>) : HMMagicItem {

            val VALID_TABLE_TYPES = linkedSetOf<String>(
                "A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16",
                "A17","A18","A21","A24")
            val EXCEPTIONAL_ITEMS = setOf("Ring of Spell Storing", "Gut Stones", "Ioun Stones")
            val ALIGNMENT_MAP = mapOf(
                "CG" to "Chaotic Good",
                "CN" to "Chaotic Neutral",
                "CE" to "Chaotic Evil",
                "NE" to "Neutral Evil",
                "LE" to "Lawful Evil",
                "LG" to "Lawful Good",
                "LN" to "Lawful Neutral",
                "TN" to "True Neutral",
                "NG" to "Neutral Good"
            )

            /**
             * Returns full alignment string from valid abbreviations
             *
             * @param input two-letter abbreviation for alignment. Starting with 'A' stands for "Any".
             */
            fun abbrevToAlignment(input: String): String{

                var result = input

                if (input.isNotBlank()){

                    if (input.startsWith('A')) {

                        when (input.last()) {

                            'C'     -> {

                                result = when (Random.nextInt(1,5)){

                                    1   -> "CG"
                                    2   -> "CN"
                                    3   -> "CN"
                                    else-> "CE"
                                }
                            }

                            'E'     -> {

                                result = when (Random.nextInt(1,4)){

                                    1   -> "CE"
                                    2   -> "NE"
                                    else-> "LE"
                                }
                            }

                            'G'     -> {

                                result = when (Random.nextInt(1,11)){

                                    1   -> "CG"
                                    2   -> "LG"
                                    3   -> "LG"
                                    4   -> "LG"
                                    5   -> "LG"
                                    6   -> "LG"
                                    else-> "NG"
                                }
                            }

                            'L'     -> {

                                result = when (Random.nextInt(1,7)){

                                    1   -> "LE"
                                    2   -> "LN"
                                    else-> "TN"
                                }
                            }

                            'N'     -> {

                                result = when (Random.nextInt(1,13)){

                                    1   -> "CN"
                                    2   -> "CN"
                                    3   -> "NE"
                                    4   -> "LN"
                                    5   -> "TN"
                                    6   -> "TN"
                                    7   -> "TN"
                                    8   -> "TN"
                                    else-> "NG"
                                }
                            }

                            else    -> {

                                result = when (Random.nextInt(1,101)){

                                    in 1..5     -> "CG"
                                    in 6..15    -> "CN"
                                    in 16..20   -> "CE"
                                    in 21..25   -> "NE"
                                    in 26..30   -> "LE"
                                    in 31..55   -> "LG"
                                    in 56..60   -> "LN"
                                    in 61..80   -> "TN"
                                    else        -> "NG"
                                }
                            }
                        }
                    }

                    result = if (ALIGNMENT_MAP.keys.contains(result)) {

                        ALIGNMENT_MAP[result]!!

                    } else { "" }
                }

                return result
            }

            var template:       HMMagicItemTemplate = SAMPLE_MAGIC_ITEM_TEMPLATE //TODO

            var baseTemplateID: Int       // Container for primary key of the first template drawn. -1 indicates a template-less item
            var itemType:       String
            var itemCharges=    0
            var gmChoice =      false

            var currentRoll:    Int

            val notesLists=     LinkedHashMap<String,ArrayList<String>>()

            // region Magic item detail holders

            var mTemplateID: Int
            val mHoardID = parentHoardID // TODO Inline this later; hoard ID can be added outside this function
            var mIconID: String
            var mName = ""
            var mSourceText: String
            var mSourcePage: Int
            var mXpValue: Int
            var mGpValue: Double
            val mClassUsability: Map<String,Boolean>
            var mIsCursed: Boolean
            var mAlignment: String
            val mNotes: List<List<String>>
            val mUserNotes = emptyList<String>()

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
                baseTemplateID = -1
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

                "A2" -> { if (Random.nextInt(1, 101) == 100) gmChoice = true  }

                "A3" -> {

                    if ((mapSubChance > 0) &&
                        (Random.nextInt(1, 101) <= mapSubChance)
                    ) {

                        itemType = "Map"
                        mName = "Treasure Map"
                        baseTemplateID = -1

                    } else {

                        if ((providedTypes.contains("A3"))) {

                            currentRoll = Random.nextInt(1, 101)

                            if (currentRoll <= 33) {

                                //Spell scroll result
                                itemType = "Spell Scroll"
                                mName = "Spell Scroll"
                                notesLists[ORDER_LABEL_STRING] = generateSpellScrollDetails(currentRoll)
                                baseTemplateID = -1

                            } else {

                                if (currentRoll in 85..91) {

                                    itemType = "Spell Scroll"
                                    mName = "Spell Scroll"
                                    notesLists[ORDER_LABEL_STRING] =
                                        generateSpellScrollDetails(Random.nextInt(1,34))
                                    baseTemplateID = -1
                                    mIsCursed = true
                                    // TODO Add discrimination to magic-item-to-scroll function for cursed

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

                else -> {
                    itemType = "INVALID"
                    baseTemplateID = -1
                }
            }

            if (gmChoice) {
                baseTemplateID = -1
                mName = "GM's Choice"
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

            if (baseTemplateID != -1) {

                // Use template to populate magic item's details

                mName = template.name
                mSourceText = template.source
                mSourcePage = template.page
                itemType = template.tableType
                mIsCursed = (template.isCursed == 1)
                mClassUsability= mapOf(
                    "Fighter" to (template.fUsable == 1),
                    "Thief" to (template.tUsable == 1),
                    "Cleric" to (template.cUsable == 1),
                    "Magic-User" to (template.mUsable == 1),
                    "Druid" to (template.dUsable == 1)
                )
                mIconID = template.iconRef //TODO add db table for looking up resource ID from this keyword
                mAlignment = abbrevToAlignment(template.alignment)

                // region [ Modify name, if applicable ]

                if (template.multiType == 3) mName = "${itemCharges}x $mName"

                if (mName.startsWith("Pole Arm")) {

                    val poleArmList = listOf(

                        "Awl pike","Bardiche","Bec de corbin","Bill-guisarme","Fauchard",
                        "Fauchard-fork","Glaive","Glaive-guisarme", "Guisarme","Guisarme-voulge",
                        "Halberd","Hook fauchard","Lucern hammer","Military fork","Partisan",
                        "Ranseur","Spetum","Voulge"
                    )

                    mName = mName.replace("Pole Arm",poleArmList[Random.nextInt(poleArmList.size)])
                }

                // endregion

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

                    if (template.multiType != 3) {

                        notesLists["Additional notes"]?.plusAssign("Found with $itemCharges charges/uses remaining")
                    }

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

                        val dosesUsed : Int

                        if (Random.nextInt(1,101) <= 70) {

                            dosesUsed = Random.nextInt(1,4)

                            itemCharges -= dosesUsed

                            if (itemCharges < 0) itemCharges = 0

                        } else {

                            dosesUsed = 0
                        }

                        return dosesUsed
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

                if (Random.nextInt(1,101) <= template.intel_chance) {

                    var wIntelligence           = 12
                    val wAlignment:             String
                    val weaponEgo:              Int
                    var wCommLevel              = 0
                    var wLanguagesKnown         = 0
                    var wPrimaryAbilities       = 1
                    var wExtraPrimaryRolls      = 0
                    var wExtraordinaryPowers    = 0
                    var wExtraExtraordinaryRolls= 0
                    var wSpecialPurpose         = ""
                    var wSpecialPurposePower    = ""
                    var wSpecialReadingPower    = ""

                    val effectiveWeaponMod : Int = if (template.name.contains("+")) {

                        if (template.isCursed == 0) {

                            if ((template.notes.isNotBlank())){

                                if (template.notes.contains("+")) {

                                        template.notes
                                            .substring(template.name.lastIndexOf("+"))
                                            .toIntOrNull() ?: 1

                                    } else {

                                        2 * (template.name
                                            .substring(template.name.lastIndexOf("+"))
                                            .toIntOrNull() ?: 1)
                                    }

                            } else {

                                2 * (template.name
                                    .substring(template.name.lastIndexOf("+"))
                                    .toIntOrNull() ?: 1)
                            }

                        } else {

                            template.name
                                .substring(template.name.lastIndexOf("+"))
                                .toIntOrNull() ?: 1
                        }

                    } else { 0 }

                    val primaryAbilityList =    mutableListOf<String>()
                    val extraordinaryPowerList= mutableListOf<String>()

                    val COMM_LEVEL_LIST = listOf(
                        "Semi-empathy*","Empathy","Speech**","Speech and Telepathy***"
                    )
                    val OPPOSITE_ALIGNMENT = mapOf(

                        "Chaotic Good" to "Lawful Evil",
                        "Chaotic Neutral" to "Lawful Neutral",
                        "Chaotic Evil" to "Lawful Good",
                        "Neutral Evil" to "Neutral Good",
                        "Lawful Evil" to "Chaotic Good",
                        "Lawful Good" to "Chaotic Evil",
                        "Lawful Neutral" to "Chaotic Neutral",
                        "True Neutral" to "any extremely-aligned (LG/CG/CE/LE)",
                        "Neutral Good" to "Neutral Evil"
                    )

                    fun setInitialProperties(inputIntelligence:Int) {

                        when (inputIntelligence) {

                            12      -> {
                                wIntelligence            = 12
                                wPrimaryAbilities        = 1
                                wExtraordinaryPowers     = 0
                                wCommLevel               = 0
                            }

                            13      -> {
                                wIntelligence            = 13
                                wPrimaryAbilities        = 2
                                wExtraordinaryPowers     = 0
                                wCommLevel               = 1
                            }

                            14      -> {
                                wIntelligence           = 14
                                wPrimaryAbilities       = 2
                                wExtraordinaryPowers    = 0
                                wCommLevel              = 2
                            }

                            15      -> {
                                wIntelligence           = 15
                                wPrimaryAbilities       = 3
                                wExtraordinaryPowers    = 0
                                wCommLevel              = 2
                            }

                            16      -> {
                                wPrimaryAbilities       = 3
                                wExtraordinaryPowers    = 0
                                wCommLevel              = 2
                                wSpecialReadingPower    = "The weapon can also read languages/maps of any non-magical type"
                            }

                            else    -> {
                                wPrimaryAbilities       = 3
                                wExtraordinaryPowers    = 1
                                wCommLevel              = 3
                                wSpecialReadingPower    = "The weapon can read languages as well as magical writings." }
                        }
                    }

                    fun getLanguageNumber(firstRoll: Boolean): Int = when
                        (Random.nextInt(1,if (firstRoll) 101 else 100)) {

                            in 1..40    -> 1
                            in 41..70   -> 2
                            in 71..85   -> 3
                            in 86..95   -> 4
                            in 96..99   -> 5
                            else        -> 6
                        }

                    // region [ Determine intelligence ]

                    wIntelligence = when (Random.nextInt(1,101)) {

                        in 1..34    -> 12
                        in 35..59   -> 13
                        in 60..79   -> 14
                        in 80..91   -> 15
                        in 92..97   -> 16
                        else        -> 17
                    }

                    setInitialProperties(wIntelligence)

                    // endregion

                    // region [ Determine alignment ]

                    wAlignment = if (mAlignment.isBlank()){

                         abbrevToAlignment( when (Random.nextInt(1,101)){

                                in 1..5     -> "CG"
                                in 6..15    -> "CN"
                                in 16..20   -> "CE"
                                in 21..25   -> "NE"
                                in 26..30   -> "LE"
                                in 31..55   -> "LG"
                                in 56..60   -> "LN"
                                in 61..80   -> "TN"
                                else        -> "NG"
                            })

                    } else {

                        abbrevToAlignment(mAlignment)
                    }

                    // endregion

                    // region [ Determine primary abilities ]

                    while ((wPrimaryAbilities > 0)||(wExtraPrimaryRolls > 0)) {

                        when (Random.nextInt(1, if ( wPrimaryAbilities > 0 ) 101 else 93)) {

                            in 1..11    -> primaryAbilityList.add("Detect \"elevator\"/shifting rooms/walls in ten-foot radius")
                            in 12..22   -> primaryAbilityList.add("Detect sloping passages in a ten-foot radius")
                            in 23..33   -> primaryAbilityList.add("Detect traps of large size in a ten-foot radius")
                            in 34..44   -> primaryAbilityList.add("Detect evil or good in a ten-foot radius")
                            in 45..55   -> primaryAbilityList.add("Detect precious metals (type and amount) in a 20-foot radius")
                            in 56..66   -> primaryAbilityList.add("Detect gems (type and number) in a five-foot radius")
                            in 67..77   -> primaryAbilityList.add("Detect Magic in a ten-foot radius")
                            in 78..82   -> primaryAbilityList.add("Detect secret doors in a five-foot radius")
                            in 83..87   -> primaryAbilityList.add("Detect invisible objects in a ten-foot radius")
                            in 88..92   -> primaryAbilityList.add("Locate Object in a 120-foot radius")
                            in 93..94   -> wExtraPrimaryRolls += 2
                            else        -> wExtraordinaryPowers++
                        }

                        if (wPrimaryAbilities>0) wPrimaryAbilities -- else wExtraPrimaryRolls --
                    }

                    // endregion

                    // region [ Determine extraordinary powers ]

                    while ((wExtraordinaryPowers > 0)||(wExtraExtraordinaryRolls > 0)) {

                        when (Random.nextInt(1,101)) {

                            in 1..7     -> extraordinaryPowerList.add("Charm Person on contact - " +
                                    "three times per day")

                            in 8..15    -> extraordinaryPowerList.add("Clairaudience, 30 yards " +
                                    "range - three times per day, one round per use")

                            in 16..22   -> extraordinaryPowerList.add("Clairvoyance, 30 yards " +
                                    "range - three times/day, one round per use")

                            in 23..28   -> extraordinaryPowerList.add("Determine direction and " +
                                    "depth - twice/day")

                            in 29..34   -> extraordinaryPowerList.add("ESP, 30 yards range - " +
                                    "three times per day, one round per use")

                            in 35..41   -> extraordinaryPowerList.add("Fly, 120 feet/turn - one " +
                                    "hour/day")

                            in 42..47   -> extraordinaryPowerList.add("Cure-All - one time/day")

                            in 48..54   -> extraordinaryPowerList.add("Illusion, 120 yards " +
                                    "range - twice/day, as Wand of Illusion")

                            in 55..61   -> extraordinaryPowerList.add("Levitation, one-turn " +
                                    "duration - three times/day, as 6th level magic user")

                            in 62..67   -> extraordinaryPowerList.add("Strength - one time/day " +
                                    "(upon wielder only)")

                            in 68..75   -> extraordinaryPowerList.add("Telekinesis, 250 pounds " +
                                    "maximum - twice/day, one round each per use")

                            in 76..81   -> extraordinaryPowerList.add("Telepathy, 60 yards range " +
                                    "- twice/day")

                            in 82..88   -> extraordinaryPowerList.add("Teleportation - one " +
                                    "time/day, 600 pounds maximum, casting time two segments")

                            in 89..94   -> extraordinaryPowerList.add("X-ray vision, 40 yards " +
                                    "range - twice/day, one turn per use")

                            in 95..97   -> wExtraExtraordinaryRolls +=
                                if ( wExtraordinaryPowers > 0 ) 2 else 1

                            in 98..99   -> extraordinaryPowerList.add("GM may choose one power " +
                                    "from Table B114 on GMG pg. 275")

                            else        -> {

                                extraordinaryPowerList.add("GM may choose one power from Table " +
                                        "B114 on GMG pg. 275")

                                if (wSpecialPurpose.isBlank()) {wSpecialPurpose = "Roll me"}
                            }
                        }

                        if (wExtraordinaryPowers > 0) {wExtraordinaryPowers --} else {wExtraExtraordinaryRolls --}
                    }

                    // endregion

                    // region [ Determine special purpose ]

                    if (wSpecialPurpose == "Roll me") {

                        wSpecialPurpose = when (Random.nextInt(1,101)) {

                            in 1..10    -> "Defeat/slay diametrically opposed alignment"
                            in 11..20   -> "Defeat clerics (of a particular type)"
                            in 21..30   -> "Defeat fighters"
                            in 31..40   -> "Defeat magic-users"
                            in 41..50   -> "Defeat thieves"
                            in 51..55   -> "Defeat bards"
                            in 56..65   -> "Overthrow law and/or chaos"
                            in 66..75   -> "Defeat good and/or evil"
                            in 76..95   -> "Defeat nonhuman monsters"
                            else        -> "Other"
                        }

                        when (Random.nextInt(1,101)) {

                            in 1..10    -> wSpecialPurposePower = "Blindness for 2d6 rounds " +
                                    "(Upon scoring a with w/ weapon, unless opponent saves vs. " +
                                    "Spell)"

                            in 11..20   -> wSpecialPurposePower = "Confusion for 2d6 rounds " +
                                    "(Upon scoring a with w/ weapon, unless opponent saves vs. " +
                                    "Spell)"

                            in 21..25   -> wSpecialPurposePower = "Disintegrate (Upon scoring a " +
                                    "with w/ weapon, unless opponent saves vs. Spell)"

                            in 26..55   -> wSpecialPurposePower = "Fear for 1d4 rounds (Upon " +
                                    "scoring a with w/ weapon, unless opponent saves vs. Spell)"

                            in 56..65   -> wSpecialPurposePower = "Zarba's Sphere of Insanity " +
                                    "for 1d4 rounds (Upon scoring a with w/ weapon, unless " +
                                    "opponent saves vs. Spell)"

                            in 66..80   -> wSpecialPurposePower = "Paralyzation for 1d4 rounds " +
                                    "(Upon scoring a with w/ weapon, unless opponent saves " +
                                    "vs. Spell)"

                            else        -> wSpecialPurposePower = "+2 to all wielder's saving " +
                                    "throws, -1 to each die of damage sustained"
                        }

                    }

                    // endregion

                    // region [ Determine languages known ]

                    if (wCommLevel > 1) { wLanguagesKnown = getLanguageNumber(true) }

                    if ( wLanguagesKnown == 6 ){

                        // If max result was rolled, roll twice more on B117, sum them, and take higher result.

                        wLanguagesKnown = (getLanguageNumber(false) +
                                getLanguageNumber(false))

                        if (wLanguagesKnown < 6) wLanguagesKnown = 6
                    }

                    // endregion

                    // region [ Calculate ego ]

                    weaponEgo =
                        effectiveWeaponMod +
                            primaryAbilityList.size +
                            (extraordinaryPowerList.size * 2) +
                            ( if (wSpecialPurpose.isNotEmpty()) {5} else {0} ) +
                            wLanguagesKnown +
                            ( when (wIntelligence) {
                                16  -> 1
                                17  -> 4
                                else-> 0
                            })

                    // endregion

                    // region [ Record info on table ]

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Ego rating: $weaponEgo")

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Intelligence: $wIntelligence")

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Alignment: $wAlignment")

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Languages known: $wLanguagesKnown")

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Communication mode: ${COMM_LEVEL_LIST[wCommLevel]}")

                    if (wSpecialReadingPower.isNotEmpty()) {

                        notesLists["Intelligent weapon info"]
                            ?.plusAssign(wSpecialReadingPower)
                    }

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("Please refer to GMG pgs. 275-276 for more details.")

                    if (primaryAbilityList.isNotEmpty()) {

                        notesLists["Intelligent weapon info"]
                            ?.plusAssign("[ PRIMARY ABILIT" +
                                    "${if (primaryAbilityList.size == 1) "Y" else "IES"} ]")

                        primaryAbilityList.forEachIndexed { index, entry ->

                            notesLists["Intelligent weapon info"]
                                ?.plusAssign("${index + 1}) $entry")
                        }
                    }

                    if (extraordinaryPowerList.isNotEmpty()) {

                        notesLists["Intelligent weapon info"]
                            ?.plusAssign("[ EXTRAORDINARY POWER" +
                                    "${if (extraordinaryPowerList.size == 1) "" else "S"} ]")

                        extraordinaryPowerList.forEachIndexed { index, entry ->

                            notesLists["Intelligent weapon info"]
                                ?.plusAssign("${index + 1}) $entry")
                        }
                    }

                    if (wSpecialPurpose.isNotEmpty()) {

                        notesLists["Intelligent weapon info"]
                            ?.plusAssign("[ SPECIAL PURPOSE INFO ]")
                        notesLists["Intelligent weapon info"]
                            ?.plusAssign("Special purpose: $wSpecialPurpose")
                        notesLists["Intelligent weapon info"]
                            ?.plusAssign("Spec. purpose power: $wSpecialPurposePower")

                    }

                    // endregion

                } else if ( template.intel_chance == -1 ) {

                    notesLists["Intelligent weapon info"]
                        ?.plusAssign("This weapon has a specific profile outlined in the " +
                                "source text. As such, please refer to this item's entry for" +
                                " specifics.")
                }

                // endregion

                // region [ "Roll Artifact Powers/Effects" ]

                if (itemType == "A24") {

                    if (template.multiType == 4) notesLists["Artifact particulars"]?.plusAssign(
                            "THIS IS A HACKMASTER-CLASS ITEM.")

                    // region [ Roll up artifact powers and effects ]

                    // Minor benign effects
                    if (template.iPower in 1..46){

                        val minorBenignEffects = listOf(
                            "A. Adds 1 point to possessor's major attribute",
                            "B. Animate Dead (1 creature by touch) 7 times/week",
                            "C. Audible Glamer upon command 3 times/day",
                            "D. Bless (by touch)",
                            "E. Clairaudience (when touched to ear)",
                            "F. Clairvoyance (when touched to eyes)",
                            "G. Color Spray (3 times/day)",
                            "H. Comprehend Languages when held",
                            "I. Create Food and Water (1 time/day)",
                            "J. Cure Light Wounds (7 times/day)",
                            "K. Darkness (5', 10', or 15' radius) 3 times/day",
                            "L. Detect Charm (3 times/day)",
                            "M. Detect Evil/Good when held or ordered",
                            "N. Detect invisibility when ordered",
                            "O. Detect Magic (3 times/day)",
                            "R. Find Traps (3 times/day)",
                            "S. Fly when held and ordered (1 time/day)",
                            "T. Hypnotic Pattern (when moved) 3 times/day",
                            "U. Infravision when held or worn",
                            "V. Improved invisibility (3 times/day)",
                            "W. Know alignment when held and ordered (1 time/day)",
                            "X. Levitate when held and ordered (3 times/day)",
                            "Y. Light (7 times/day)",
                            "Z. Mind Blank (3 times/day)",
                            "AA. Obscurement (1 time/day)",
                            "BB. Pass without Trace (1 time/day)",
                            "CC. Possessor immune to disease",
                            "DD. Possessor immunne to fear",
                            "EE. Possessor immune to gas of any type",
                            "FF. Possessor need not eat/drink for up to 1 week",
                            "II. Sanctuary when held or worn (1 time/day)",
                            "JJ. Shield, when held or worn (1 time/day)",
                            "KK. Speak with animals (3 times/day)",
                            "LL. Speak to the dead (1 time/day)",
                            "MM. Speak with plants (7 times/week)",
                            "NN. Tongues when held or worn and commanded",
                            "OO. Ultravision when held or worn",
                            "PP. Ventriloquism upon command (3 times/day)",
                            "QQ. Water Breathing upon command",
                            "RR. Water Walk at will",
                            "SS. Wearer immune to Charm and Hold spells",
                            "TT. Wearer immune to Magic Missiles",
                            "UU. Web (1 time/day)",
                            "VV. Wizard Lock (7 times/week)",
                            "WW. Write (1 time/day)",
                            "XX. Zombie Animation (1 time/week)"
                        )
                        val typeIList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ I. MINOR BENIGN EFFECTS" +
                                    "${if (template.iPower > 1)"S" else ""} ]")

                        repeat (template.iPower) {

                            var newEffect: Int

                            // Roll effect, re-rolling duplicates
                            do {

                                newEffect = Random.nextInt(minorBenignEffects.size)

                            } while ((typeIList.contains(newEffect))||(newEffect < 0))

                            // Add unique effect to list
                            typeIList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeIList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${minorBenignEffects[entry]}")
                        }
                    }

                    // Major benign effects
                    if (template.iiPower in 1..50){

                        val majorBenignEffects = listOf(
                            "A. Animal Summoning (II or III) 2 times/day",
                            "B. Animate Object upon command (1 time/day)",
                            "C. +2 to AC of possessor or AC 0, whichever is better",
                            "D. Cause Serious Wounds by touch",
                            "E. Charm monster (2 times/day)",
                            "F. Charm person (7 times/week)",
                            "G. Confusion (1 time/day)",
                            "H. Cure-All (1 time/day)",
                            "I. Cure Blindness by touch",
                            "J. Cure Disease by touch",
                            "K. Dimension Door (2 times/day)",
                            "L. Disintegrate (1 time/day)",
                            "M. Dispel Illusions (automatically) upon command (2 times/day)",
                            "N. Dispel Magic upon command (2 times/day)",
                            "O. Double movement speed (on foot)",
                            "P. Emotion (2 times/day)",
                            "Q. Explosive Runes (1 time/month)",
                            "R. Fear by touch or gaze",
                            "S. Fireball (12-15 dice) 2 times/day",
                            "T. Fire Shield (2 times/day)",
                            "U. Giant strength (determine type randomly) for 2 turns 2 times/day",
                            "V. Haste (1 time/day)",
                            "W. Hold Animal (1 time/day)",
                            "X. Hold Monster (1 time/day)",
                            "Y. Hold Person (1 time/day)",
                            "Z. Lightning Bolt (12-15 dice) 2 times/day",
                            "AA. Lyggl's Cone of Cold (12-15 dice) 2 times/day",
                            "BB. Minor Globe of Invulnerability (1 time/day)",
                            "CC. Paralyzation by touch",
                            "DD. Phantasmal Killer (1 time/day)",
                            "EE. Polymorph Self (7 times/week)",
                            "FF. Regenerate 2 hp/turn (but not if killed)",
                            "GG. Remove Curse by touch (7 times/week)",
                            "HH. Slow (1 time/day)",
                            "II. Speak with Monster (2 times/day)",
                            "JJ. Stone to Flesh (1 time/day)",
                            "KK. Suggestion (2 times/day)",
                            "LL. Telekinesis (100-600 pounds weight) 2 times/day",
                            "MM. Teleport Without Error (2 times/day)",
                            "NN. Transmute Stone to Mud (2 times/day)",
                            "OO. True Seeing (1 time/day)",
                            "PP. Turn Wood (1 time/day)",
                            "QQ. Wall of Fire (2 times/day)",
                            "RR. Wall of Ice (2 times/day)",
                            "SS. Wall of Thorns (2 times/day)",
                            "TT. Wall Passage (2 times/day)",
                            "UU. Weapon damage is +2 per hit",
                            "VV. Wind Walk (1 time/day)",
                            "WW. Wizard Eye (2 times/day)",
                            "XX. Word of Recall (1 time/day)"
                        )
                        val typeIIList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ II. MAJOR BENIGN EFFECT" +
                                    "${if (template.iiPower > 1)"S" else ""} ]")

                        repeat (template.iiPower) {

                            var newEffect = -1

                            // Roll effect, re-rolling duplicates
                            do {

                                newEffect = Random.nextInt(majorBenignEffects.size)

                            } while ((typeIIList.contains(newEffect))||(newEffect<0))

                            // Add unique effect to list
                            typeIIList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeIIList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${majorBenignEffects[entry]}")
                        }
                    }

                    // Minor malevolent effects
                    if (template.iiiPower in 1..25){

                        val minorMalevolentEffects = listOf(
                            "A. Acne on possessor's face",
                            "B. Blindness for 1-4 rounds when first used against an enemy",
                            "C. Body odor noticeable at distance of ten feet",
                            "D. Deafness for 1-4 turns when first used against an enemy",
                            "E. Gems or jewelry found never increase in value",
                            "F. Holy water within 10' of item becomes polluted",
                            "G. Lose 1-4 points of Charisma for 1-4 days when major power is used",
                            "H. Possessor loses interest in sex",
                            "I. Possessor has satyriasis",
                            "J. Possessor's hair turns white",
                            "K. Saving throws versus spells are at -1",
                            "L. Saving throw versus poison are at -2",
                            "M. Sense of smell lost for 2-8 hours when used against an enemy",
                            "N. Small fires (torches, et al.) extinguished when major powers are used",
                            "O. Small items of wood rot from possessor's touch (any item up to normal door size, 1-7 days time)",
                            "P. Touch of possessor kills green plants",
                            "Q. User causes hostility towards himself in all mammals within 60 yards",
                            "R. User loses 1 point of Comeliness permanently",
                            "S. User must eat and drink 6 times the normal amount due to the item's drain upon him or her",
                            "T. User's sex changes",
                            "U. Wart appears on possessor's nose",
                            "V. Weight gain of 10-40 pounds",
                            "W. Weight loss of 5-30 pounds",
                            "X. Yearning for item forces possessor to never be away from it for more than 1 day if at all possible",
                            "Y. Yelling becomes necessary to invoke spells with verbal components"
                        )
                        val typeIIIList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ III. MINOR MALEVOLENT EFFECT" +
                                    "${if (template.iiiPower > 1)"S" else ""} ]")

                        repeat (template.iiiPower) {

                            var newEffect : Int

                            // Roll effect, re-rolling duplicates
                            do {

                                newEffect = Random.nextInt(minorMalevolentEffects.size)

                            } while ((typeIIIList.contains(newEffect))||(newEffect<0))

                            // Add unique effect to list
                            typeIIIList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeIIIList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${minorMalevolentEffects[entry]}")
                        }
                    }

                    // Major malevolent effects
                    if (template.ivPower in 1..34){

                        val majorMalevolentEffects = listOf(
                            "A. Body rot [see Table B126] is 10% likely cumulative whenever a primary power is used, and part of the body is lost permanently",
                            "B. Capricious alignment change each time a primary power is used",
                            "C. Geas/Quest [see Table B126] placed upon possessor",
                            "D. Item contains the life force of a person — after a set number of uses, the possessor's life force is drawn into it and the former soul is released (see GMG pg 285)",
                            "E. Item has power to affect its possessor when a primary power is used if the character has not followed the alignment of the artifact/relic",
                            "F. Item is a prison for for a powerful being — there is a 1%-4% cumulative chance per usage that it will [see Table B126]",
                            "G. Item is itself a living, sentient being forced to serve; but each usage of a primary power gives it a 1%-3% cumulative chance the spell will be broken and the being will [see Table B126]",
                            "H. Item is powerless against and hates 1-2 species of creatures [see Table B126] — when within 100 feet of any such creatures it forces possessor to attack",
                            "I. Item releases a gas which renders all creatures [see Table B126], within 20 feet, including the wielder, powerless to move for 5-20 rounds",
                            "J. Lose 1 point of Charisma permanently",
                            "K. Lose 1 point of Constitution permanently",
                            "L. Lose 1 point of Dexterity permanently",
                            "M. Lose 1 hit point permanently",
                            "N. Lose 1 point of Intelligence permanently",
                            "O. Lose 1 point of Strength permanently",
                            "P. Lose 1 point of Wisdom permanently",
                            "Q. Magic drained from most powerful magic item (other than artifact or relic) within 20 feet of user",
                            "R. Reverse alignment permanently",
                            "S. Sacrifice a certain animal [see Table B126] to activate item for 1 day",
                            "T. Sacrifice a human or player character to activate item for 1 day",
                            "U. Sacrifice 10,000-60,000 gp worth of gems/jewelry to activate item for 1 day",
                            "V. User becomes berserk and attacks creatures [see Table B126] within 20 feet randomly (check each round) for 5-20 rounds",
                            "W. User goes insane for 1-4 days",
                            "X. User grows 3 inches taller each time primary power is used",
                            "Y. User instantly killed (but may be Raised or Resurrected)",
                            "Z. User loses 1 level of experience",
                            "AA. User receives 2-20 points of damage",
                            "BB. User receives 5-30 points of damage",
                            "CC. User required to slay a certain type of creature [see Table B126] to activate item, and slaying another set type will de-activate item",
                            "DD. User shrinks 3 inches each time primary power is used",
                            "EE. User transformed into a powerful but minor being from another plane [see Table B126]",
                            "FF. User withers and ages 3-30 years [see Table B126] each time primary power is used, eventually the possessor becomes a withered Zombie guardian of the item",
                            "GG. Utterance of a spell causes complete loss of voice for one day",
                            "HH. Yearning to be worshipped is uncontrollable; those failing to bow and scrape to the artifact's possessor will be subject to instant attack"
                        )
                        val typeIVList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ IV. MAJOR MALEVOLENT EFFECT" +
                                    "${if (template.ivPower > 1)"S" else ""} ]")

                        repeat (template.ivPower) {

                            var newEffect: Int

                            // Roll effect, re-rolling duplicates
                            do {

                                newEffect = when (Random.nextInt(1,101)){

                                    in 1..3     -> 0
                                    in 4..6     -> 1
                                    in 7..9     -> 2
                                    in 10..12   -> 3
                                    in 13..15   -> 4
                                    in 16..18   -> 5
                                    in 19..21   -> 6
                                    in 22..24   -> 7
                                    in 25..27   -> 8
                                    in 28..30   -> 9
                                    in 31..33   -> 10
                                    in 34..36   -> 11
                                    in 37..39   -> 12
                                    in 40..42   -> 13
                                    in 43..45   -> 14
                                    in 46..48   -> 15
                                    in 49..51   -> 16
                                    in 52..54   -> 17
                                    in 55..57   -> 18
                                    in 58..60   -> 19
                                    in 61..63   -> 20
                                    in 64..66   -> 21
                                    in 67..69   -> 22
                                    in 70..72   -> 23
                                    in 73..75   -> 24
                                    in 76..78   -> 25
                                    in 79..81   -> 26
                                    in 82..84   -> 27
                                    in 85..87   -> 28
                                    in 88..90   -> 29
                                    in 91..93   -> 30
                                    in 94..96   -> 31
                                    in 97..98   -> 32
                                    in 99..100  -> 33
                                    else        -> -1
                                }

                            } while ((typeIVList.contains(newEffect))||(newEffect < 0))

                            // Add unique effect to list
                            typeIVList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeIVList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${majorMalevolentEffects[entry]}")
                        }
                    }

                    // Prime powers
                    if (template.vPower in 1..37){

                        val primePowers = listOf(
                            "A. All of possessor's ability totals permanently raised by 2 points each upon pronouncement of a command word (18 maximum)",
                            "B. All of the possessor's ability scores are raised to 18 each upon pronouncement of a command word",
                            "C. Bones/exoskeleton/cartilage of opponent turned to jelly - 1 time/day",
                            "D. Cacodemon-like power summons a Demon Lord, Arch-Devil, or Daemon Prince — 1 time/month",
                            "E. Creeping Doom - 1 time/day",
                            "F. Death Ray equal to a Finger of Death with no saving throw - 1 time/day",
                            "G. Death Spell power of 110%-200% effectiveness with respect to number os levels/Hit Dice affected - 1 time/day",
                            "H. Gate spell 1 time/day",
                            "I. Imprisonment spell - 1 time/week",
                            "J. Magic resistance (lasting 12 turns) of 50-75% for possessor upon command word - 1 time/day",
                            "K. Major attribute permanently raised to 19 upon command word",
                            "L. Meteor Swarm - 1 time/day",
                            "M. Monster Summoning VIII - 2 times/day",
                            "N. Plane Shift - 1 time/day",
                            "O. Polymorph Any Object - 1 time/day",
                            "P. Power Word: Blind, Kill, or Stun - 1 time/day",
                            "Q. Premonition of death or serious hard to possessor",
                            "R. Prismatic spray - 1 time/day",
                            "S. Restoration - 1 time/day",
                            "T. Resurrection - 7 times/week",
                            "U. Shades - 2 times/day",
                            "V. Shape Change - 2 times/day",
                            "W. Spell absorption, 19-24 levels - 1 time/week",
                            "X. Summon 1 of each type of elemental, 16 Hit Dice each, automatic control - 1 time/week",
                            "Y. Summon Djinn or Efreet Lord (8 hp/dice, +2 to-hit and +4 to damage) for 1 day of service - 1 time/week",
                            "Z. Super Sleep spell affects double number of creatures plus up to two 5th or 6th and one 7th or 8th level creature",
                            "AA. Temporal Stasis, no saving throw, upon touch - 1 time/month",
                            "BB. The item enables the possessor to Legend Lore, Commune, or Contact Other Plane - 1 time/week",
                            "CC. Time Stop of twice normal duration - 1 time/week",
                            "DD. Total fire/heat resistance for all creatures within 20 feet of the item",
                            "EE. Total immunity to all forms of mental and psionic attacks",
                            "FF. Total immunity to all forms of cold",
                            "GG. Trap the Soul with 90% effectiveness - 1 time/month",
                            "HH. User can cast combination spells (if a spell caster) with no chance of failure or mishap, as follows:" +
                                    "\n1) 1st and 2nd level spells simultaneously",
                            "HH. User can cast combination spells (if a spell caster) with no chance of failure or mishap, as follows:" +
                                    "\n2) 2nd and 3rd level spells simultaneously",
                            "HH. User can cast combination spells (if a spell caster) with no chance of failure or mishap, as follows:" +
                                    "\n3) 3rd and 4th level spells simultaneously",
                            "HH. User can cast combination spells (if a spell caster) with no chance of failure or mishap, as follows:" +
                                    "\n4) 1st, 2nd, and 3rd level spells simultaneously",
                            "II. Vanish - 2 times/day",
                            "JJ. Vision - 1 time/day",
                            "KK. Wish - 1 time/day",
                            "LL. Youth restored to creature touched [see Table B127] - 1 time/month"
                        )
                        val typeVList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ V. PRIME POWER " +
                                    "${if (template.vPower > 1)"S" else ""} ]")

                        repeat (template.vPower) {

                            var newEffect = -1

                            // Roll effect, re-rolling duplicates
                            do {

                                when (Random.nextInt(1,101)){

                                    in 1..2     -> newEffect = 0
                                    in 3..5     -> newEffect = 1
                                    in 6..7     -> newEffect = 2
                                    in 8..10    -> newEffect = 3
                                    in 11..12   -> newEffect = 4
                                    in 13..15   -> newEffect = 5
                                    in 16..17   -> newEffect = 6
                                    in 18..20   -> newEffect = 7
                                    in 21..22   -> newEffect = 8
                                    in 23..25   -> newEffect = 9
                                    in 26..27   -> newEffect = 10
                                    in 28..29   -> newEffect = 11
                                    in 30..32   -> newEffect = 12
                                    in 33..35   -> newEffect = 13
                                    in 36..38   -> newEffect = 14
                                    in 39..41   -> newEffect = 15
                                    in 42..44   -> newEffect = 16
                                    in 45..46   -> newEffect = 17
                                    in 47..48   -> newEffect = 18
                                    in 49..50   -> newEffect = 19
                                    in 51..53   -> newEffect = 20
                                    in 54..55   -> newEffect = 21
                                    in 56..58   -> newEffect = 22
                                    in 59..60   -> newEffect = 23
                                    in 61..62   -> newEffect = 24
                                    in 63..65   -> newEffect = 25
                                    in 66..68   -> newEffect = 26
                                    in 69..71   -> newEffect = 27
                                    in 72..73   -> newEffect = 28
                                    in 74..76   -> newEffect = 29
                                    in 77..79   -> newEffect = 30
                                    in 80..82   -> newEffect = 31
                                    in 83..85   -> newEffect = 32
                                    in 86..89   -> {

                                        when (Random.nextInt(1,5)){

                                            1   -> newEffect = 33
                                            2   -> newEffect = 34
                                            3   -> newEffect = 35
                                            4   -> newEffect = 36
                                        }
                                    }
                                    in 90..92   -> newEffect = 37
                                    in 93..95   -> newEffect = 38
                                    in 96..97   -> newEffect = 39
                                    in 98..100  -> newEffect = 40
                                }

                            } while ((typeVList.contains(newEffect))||(newEffect < 0))

                            // Add unique effect to list
                            typeVList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeVList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${primePowers[entry]}")
                        }
                    }

                    // Side effects
                    if (template.viPower in 1..18){

                        val sideEffects = listOf(
                            "A. Alignment of possessor permanently changed to that of of item",
                            "B. Charisma of possessor reduced to 3 as long as item is owned",
                            "C. Fear reaction possible in any creature within 20 feet of the item whenever a major or primary power is used; all, including the possessor, must save versus spells or flee in panic",
                            "D. Fumble reaction possible in any creature within 20 feet of the item whenever a major or primary power is used; all, including the possessor, must save versus spells or be affected as the spell",
                            "E. Greed and covetousness reaction in all intelligent creatures viewing the item [see Table B128]",
                            "F. Lycanthropy inflicted upon the possessor, type according to the alignment of item, change to animal form involuntary and 50% likely (1 check only) whenever confronted and attacked by an enemy",
                            "G. Treasure within five-foot radius of mineral nature (metal or gems) of nonmagical type is reduced by 20%-80% as item consumes it to sustain its power",
                            "H. User becomes ethereal whenever major or primary power of the item is activated [see Table B128]",
                            "I. User becomes fantastically strong (19, or +1 if already 19) but ver clumsy [see Table B128]",
                            "J. User cannot touch or be touched by any (even magical) metal; metal simply passes through his body as if it did not exist and has no effect",
                            "K. User has a poison touch which requires that humans and man-sized humanoids (but not undead) save versus poison or die whenever touched",
                            "L. User has limited omniscience and may request the GM to answer 1 question per game day [see Table B128]",
                            "M. User has short-duration charismatic effect upon creatures of similar alignment [see Table B128]",
                            "N. Whenever any power of the item is used, temperature within a 60-foot radius is raised 20-50 degrees Fahrenheit for 2-12 turns (moves with item)",
                            "O. Whenever the major or prime power of the item is used, temperature within a 6-foot radius is lowered 20-80 degrees Fahrenheit for 2-12 turns (moves with item)",
                            "P. Whenever the prime power is used the possessor must save versus spells or lose 1 level of experience",
                            "Q. Whenever the prime power is used, those creatures friendly to the user within 20 feet, excluding the user, will sustain 5-20 points of damage",
                            "R. Whenever this item is used as a weapon to strike an enemy, it does double normal damage to the opponent but the wielder takes (normal) damage just as if he had been struck by the item"
                        )
                        val typeVIList = arrayListOf<Int>()

                        notesLists["Artifact particulars"]
                            ?.plusAssign("[ VI. SIDE EFFECT " +
                                    "${if (template.vPower > 1)"S" else ""} ]")

                        repeat (template.vPower) {

                            var newEffect : Int

                            // Roll effect, re-rolling duplicates
                            do {

                                newEffect = when (Random.nextInt(1,101)){

                                    in 1..5     -> 0
                                    in 6..11    -> 1
                                    in 12..16   -> 2
                                    in 17..21   -> 3
                                    in 22..27   -> 4
                                    in 28..32   -> 5
                                    in 33..38   -> 6
                                    in 39..43   -> 7
                                    in 44..48   -> 8
                                    in 49..54   -> 9
                                    in 55..60   -> 10
                                    in 61..66   -> 11
                                    in 67..71   -> 12
                                    in 72..77   -> 13
                                    in 78..82   -> 14
                                    in 83..88   -> 15
                                    in 89..94   -> 16
                                    in 95..100  -> 17
                                    else        -> -1
                                }

                            } while ((typeVIList.contains(newEffect))||(newEffect < 0))

                            // Add unique effect to list
                            typeVIList.add(newEffect)
                        }

                        // Add effects once all are generated
                        typeVIList.forEachIndexed {index, entry ->
                            notesLists["Artifact particulars"]
                                ?.plusAssign("${index + 1}) ${sideEffects[entry]}")
                        }
                    }

                    // Add footnote if artifact has any additional effects whatsoever
                    if ((template.iPower > 0)||(template.iiPower > 0)||(template.iiiPower > 0)||
                        (template.ivPower > 0)||(template.vPower > 0)||(template.viPower > 0)) {
                        notesLists["Artifact particulars"]?.plusAssign(
                            "For more information, see GMG pages 284-286")
                    }

                    // endregion
                }

                // endregion

            } else {

                val USABLE_BY_ALL = mapOf(
                    "fighter" to true,
                    "thief" to true,
                    "cleric" to true,
                    "magic-user" to true,
                    "druid" to true)

                // Generate magic item details for outliers and exceptional items TODO
                when (mName) {

                    "Treasure map"  -> {

                        val isRealMap: Boolean
                        val distanceRoll= Random.nextInt(1,21)

                        // Roll type of map
                        when (Random.nextInt(1,11)) {

                            1       -> {
                                isRealMap = false
                                notesLists["Map details"]?.plusAssign("False map " +
                                        "(No treasure or already looted")
                            }

                            in 2..7 -> {
                                isRealMap = true
                                notesLists["Map details"]?.plusAssign("Map to monetary treasure " +
                                        "(0% chance of art objects or magic items")
                            }

                            in 8..9 -> {
                                isRealMap = true
                                notesLists["Map details"]?.plusAssign("Map to magical treasure " +
                                        "(0% chance of coin)")
                            }

                            else    -> {
                                isRealMap = true
                                notesLists["Map details"]?.plusAssign("Map to combined treasure")
                            }
                        }

                        // Roll direction of treasure location
                        when (Random.nextInt(1,9)){
                            1 -> notesLists["Map details"]?.plusAssign("Located north")
                            2 -> notesLists["Map details"]?.plusAssign("Located northeast")
                            3 -> notesLists["Map details"]?.plusAssign("Located east")
                            4 -> notesLists["Map details"]?.plusAssign("Located southeast")
                            5 -> notesLists["Map details"]?.plusAssign("Located south")
                            6 -> notesLists["Map details"]?.plusAssign("Located southwest")
                            7 -> notesLists["Map details"]?.plusAssign("Located west")
                            8 -> notesLists["Map details"]?.plusAssign("Located northwest")
                        }

                        // Roll distance of treasure
                        when (distanceRoll) {

                            in 1..2 -> notesLists["Map details"]?.plusAssign("Hoard located in " +
                                    "labyrinth of caves found in lair")

                            in 3..6 -> notesLists["Map details"]?.plusAssign("Hoard located " +
                                    "outdoors, 5-8 miles distant")

                            in 7..9 -> notesLists["Map details"]?.plusAssign("Hoard located " +
                                    "outdoors, 10-40 miles distant")

                            else    -> notesLists["Map details"]?.plusAssign("Hoard located " +
                                    "outdoors, 50-500 miles distant")
                        }

                        if (distanceRoll > 2) {

                            when (Random.nextInt(1,11)) {

                                1       -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "buried and unguarded")
                                2       -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "hidden in water")
                                in 3..7 -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "guarded in a lair")
                                8       -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "somewhere in ruins")
                                9       -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "in a burial crypt")
                                else    -> notesLists["Map details"]?.plusAssign("Treasure shown " +
                                        "secreted in a town")
                            }
                        }

                        // Roll type of treasure
                        if (isRealMap) {

                            val treasureTypesList = listOf(
                                "I","G","H","F","A","B","C","D","E","Z","A and Z","A and H"
                            )

                            notesLists["Map details"]?.plusAssign("Treasure present: " +
                                    treasureTypesList[Random.nextInt(1,13)])
                        }

                        mSourceText =   "GameMaster's Guide"
                        mSourcePage =   182
                        mXpValue =      0
                        mGpValue =      0.0
                        mClassUsability = USABLE_BY_ALL
                        mIsCursed = !(isRealMap)
                        //mIconRef TODO
                    }

                    "Spell Scroll"  -> {
                        mSourceText =   "GameMaster's Guide"
                        mSourcePage =   225
                        mXpValue =      0
                        mGpValue =      0.0
                        mClassUsability = USABLE_BY_ALL
                        //mIconID TODO
                    }

                    "GM's Choice"   -> {
                        mSourceText =   "GameMaster's Guide"
                        mSourcePage =   213
                        mXpValue =      0
                        mGpValue =      0.0
                        mClassUsability = USABLE_BY_ALL
                        //mIconID TODO
                    }
                }
            }

            if (EXCEPTIONAL_ITEMS.contains(mName)) {

                when (mName) {

                    "Ring of Spell Storing" -> {

                        val useArcane = Random.nextBoolean()
                        val maxLevel = 6 + if (useArcane) 2 else 0

                        var rollHolder : Int

                        notesLists[ORDER_LABEL_STRING]?.plusAssign(
                           "spell type = ${if (useArcane) "Magic-User" else "Cleric"}"
                        )

                        repeat(Random.nextInt(2,6)) {

                            rollHolder = Random.nextInt(1,maxLevel+1)

                            if (rollHolder == maxLevel) rollHolder = Random.nextInt(1,maxLevel-1)

                            notesLists[ORDER_LABEL_STRING]?.plusAssign(
                                "level = $rollHolder}"
                            )
                        }
                    }

                    "Gut Stones"    -> {
                        notesLists[ORDER_LABEL_STRING]?.plusAssign(
                        "count = $itemCharges")
                    }

                    "Ioun Stones"    -> {
                        notesLists[ORDER_LABEL_STRING]?.plusAssign(
                            "count = $itemCharges")
                    }
                }
            }

            //endregion

            // region [ Convert mapped lists to nested list ] TODO

            fun convertMapToNestedLists(input: LinkedHashMap<String,ArrayList<String>>) : List<List<String>> {

                val listHolder = ArrayList<List<String>>()
                val nameHolder = ArrayList<String>()

                listHolder[0] = listOf("") // Placeholder for parent list

                input.onEachIndexed { index, entry ->

                    nameHolder.add(entry.key)
                    listHolder.add(index + 1,entry.value.toList())
                }

                listHolder[0] = nameHolder.toList()

                return listHolder.toList()
            }

            mNotes = convertMapToNestedLists(notesLists)

            // endregion

            // region [ Assign dummy values to any unassigned fields ] TODO

            // endregion

            return HMMagicItem(
                0,
                mTemplateID,
                mHoardID,
                mIconID,
                itemType,
                mName,
                mSourceText,
                mSourcePage,
                mXpValue,
                mGpValue,
                mClassUsability,
                mIsCursed,
                mAlignment,
                mNotes,
                mUserNotes)
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