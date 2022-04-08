package com.example.android.treasurefactory

import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import com.example.android.treasurefactory.database.GemTemplate
import com.example.android.treasurefactory.database.MagicItemTemplate
import com.example.android.treasurefactory.database.SpellTemplate
import com.example.android.treasurefactory.model.*
import java.util.*
import kotlin.random.Random

class LootGeneratorAsync : BaseLootGenerator {

    override val ANY_MAGIC_ITEM_LIST = listOf(
        "A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16",
        "A17","A18","A21","A24")

    override val SAMPLE_ARCANE_SPELL = SpellTemplate(744,"Wildshield",1,"Spellslinger's Guide to Wurld Domination",125,0,6,"Con","Wild_Mage/Guardian","","Wild","")

    override val SAMPLE_DIVINE_SPELL = SpellTemplate(1146,"Exaction",0,"Player’s Handbook",273,1,7,"Evo/Alt","","Charm/Summoning","","")

    override val SAMPLE_GEM_TEMPLATE = GemTemplate(57,10,"ruby",5, 0,"clear red to deep crimson (Corundum)","gem_jewel_ruby")

    override val SAMPLE_MAGIC_ITEM_TEMPLATE = MagicItemTemplate(
        588,5,"Robe of Scintillating Colors","GameMaster's Guide",263,
        1250, 1500, 0, "",0,0,0,
        "A10","",0,0,1,1,0,0,
        0,"",0,"",0,"",
        0,0,0,0,0,0
    )

    override fun createGem(parentHoardID: Int, givenTemplate: Int) : Gem {

        // region [ Roll gem type ]

        val gemType : Int = if (!(givenTemplate in 1..58)) {

            when (Random.nextInt(1,101)) {

                in 1..25    -> 5
                in 26..50   -> 6
                in 51..70   -> 7
                in 71..90   -> 8
                in 91..99   -> 9
                100         -> 10
                else        -> 5
            }

        } else {

            //TODO get gem template's type from ID; will require callbacks
            5
        }

        // endregion

        // region [ Pull gem template ] TODO

        val gemTemplate = SAMPLE_GEM_TEMPLATE // ?: SAMPLE_GEM_TEMPLATE

        // endregion

        // region [ Roll size ]

        val gemSize : Int = when(Random.nextInt(1,101)) {

            in 1..5     -> -3
            in 6..25    -> -2
            in 26..45   -> -1
            in 46..65   -> 0
            in 66..85   -> 1
            in 86..90   -> 2
            in 91..96   -> 3
            in 97..99   -> 4
            else        -> 5
        }

        // endregion

        // region [ Roll quality ]

        val gemQuality : Int = when(Random.nextInt(1,101)) {

            in 1..5     -> -3
            in 6..25    -> -2
            in 26..45   -> -1
            in 46..65   -> 0
            in 66..85   -> 1
            in 86..90   -> 2
            in 91..96   -> 3
            in 97..99   -> 4
            else        -> 5
        }

        // endregion

        // region [ Get initial market value ]

        val initialGPValue = LootMutator.convertGemValueToGP(gemType + gemSize + gemQuality)

        // endregion

        // region [ Start value history list ]

        val valueHistory = listOf(
            Pair(Calendar.getInstance().timeInMillis,"Initial market value: $initialGPValue gp.")
        )

        // endregion

        return Gem(0,
            parentHoardID,gemTemplate.iconID,
            gemSize,
            gemType,
            gemQuality,
            0,
            gemTemplate.name,
            gemTemplate.opacity,
            gemTemplate.description,
            initialGPValue,
            valueHistory
        )
    }

    override fun createArtObject(parentHoardID: Int,
                        itemRestrictions: ArtRestrictions) : Pair<ArtObject,MagicItem?> {

        var temporaryRank:  Int
        var ageInYears:     Int
        var ageModifier:    Int
        var ageRank:        Int
        var subjectRank:    Int
        var condModifier:   Int

        val artType:        Int
        val renown:         Int
        val size:           Int
        val condition:      Int
        val materials:      Int
        val quality:        Int
        val subject:        Int

        // region [ Type of art ]

        when (Random.nextInt(1,101)) {

            in 1..5     -> { // Paper art
                artType = 0
                condModifier = -2
                ageModifier = -2
            }
            in 6..15    -> { //
                artType = 1
                condModifier = -2
                ageModifier = -2
            }
            in 16..30   -> { //
                artType = 2
                condModifier = -1
                ageModifier = -1
            }
            in 31..45   -> { //
                artType = 3
                condModifier = -1
                ageModifier = -1
            }
            in 46..60   -> { //
                artType = 4
                condModifier = -1
                ageModifier = -1
            }
            in 61..70   -> { //
                artType = 5
                condModifier = 0
                ageModifier = 0
            }
            in 71..80   -> { //
                artType = 6
                condModifier = 0
                ageModifier = 0
            }
            in 81..90   -> { //
                artType = 7
                condModifier = 1
                ageModifier = 0
            }
            in 91..99   -> { //
                artType = 8
                condModifier = 2
                ageModifier = 0
            }
            else        -> { // Magical
                artType = 3
                condModifier = 3
                ageModifier = 0
            }
        }

        // endregion

        // region [ Renown of the artist ]

        renown = when (Random.nextInt(1,101)) {

            in 1..15    -> -3
            in 16..30   -> -2
            in 31..45   -> -1
            in 46..65   -> 0
            in 66..85   -> 1
            in 86..95   -> 2
            in 96..99   -> 3
            else        -> 4
        }

        // endregion

        // region [ Size of artwork ]

        size = when(Random.nextInt(1,101)) {

            in 1..5     -> -3
            in 6..25    -> -2
            in 26..45   -> -1
            in 46..65   -> 0
            in 66..85   -> 1
            in 86..90   -> 2
            in 91..96   -> 3
            in 97..99   -> 4
            else        -> 5
        }

        // endregion

        // region [ Quality of materials used ]

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
        } + renown

        if (temporaryRank < 0) { temporaryRank = 0 }
        if (temporaryRank > 8) { temporaryRank = 8 }

        materials = temporaryRank - 3

        // endregion

        // region [ Quality of work done ]

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
        } + renown

        if (temporaryRank < 0) { temporaryRank = 0 }
        if (temporaryRank > 8) { temporaryRank = 8 }

        quality = temporaryRank - 3

        // endregion

        // region [ Age of artwork ]

        ageInYears =
            rollPenetratingDice(5,20,0).getRollTotal() *       // 5d20 x 1d4, penetrate on all rolls
                    rollPenetratingDice(1,4,0).getRollTotal()

        if (ageInYears < 5) { ageInYears = 5 }

        // Check age range of rolled value
        ageRank = when (ageInYears) {

            in 5..25        -> -2
            in 26..75       -> -1
            in 76..150      -> 0
            in 151..300     -> 1
            in 301..600     -> 2
            in 601..1500    -> 3
            in 1501..3000   -> 4
            else            -> 5
        }

        // Re-roll age in years by range if art type penalizes age rank
        when (ageRank + ageModifier){

            -4  -> {
                if (ageInYears !in 5..25) {

                    ageInYears = Random.nextInt(5,26)
                    ageRank = -2
                }
            }
            -3  -> {
                if (ageInYears !in 5..25) {

                    ageInYears = Random.nextInt(5,26)
                    ageRank = -2
                }
            }
            -2  -> {
                if (ageInYears !in 5..25) {

                    ageInYears = Random.nextInt(5,26)
                    ageRank = -2
                }
            }
            -1  -> {
                if (ageInYears !in 26..75) {

                    ageInYears = Random.nextInt(26,76)
                    ageRank = -1
                }
            }
            0   -> {
                if (ageInYears !in 76..150) {

                    ageInYears = Random.nextInt(76,151)
                    ageRank = 0
                }
            }
            1   -> {
                if (ageInYears !in 151..300) {

                    ageInYears = Random.nextInt(151,301)
                    ageRank = 1
                }
            }
            2   -> {
                if (ageInYears !in 301..600) {

                    ageInYears = Random.nextInt(301,601)
                    ageRank = 2
                }
            }
            3   -> {
                if (ageInYears !in 601..1500) {

                    ageInYears = Random.nextInt(601,1501)
                    ageRank = 3
                }
            }
            4   -> {
                if (ageInYears !in 1501..3000) {

                    ageInYears = Random.nextInt(1501,3000)
                    ageRank = 4
                }
            }
        }

        // Adjust age rank to fit at extremes of value range
        if (ageRank < -2) { ageRank = -2 } else
            if (ageRank > 5) { ageRank = 5 }

        // endregion

        // region [ Condition of artwork ]

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
        } + condModifier

        if (temporaryRank < 0) { temporaryRank = 0 }
        if (temporaryRank > 8) { temporaryRank = 8 }

        condition = temporaryRank - 3

        // endregion

        // region [ Subject matter of art object ]

        when (Random.nextInt(1,101)) {

            in 1..10    -> {
                subject = -2
                subjectRank = -2
            }
            in 11..20   -> {
                subject = -1
                subjectRank = -1
            }
            in 21..30   -> {
                subject = 0
                subjectRank = 0
            }
            in 31..50   -> {
                subject = 1
                subjectRank = 0
            }
            in 51..70   -> {
                subject = 2
                subjectRank = 0
            }
            in 71..90   -> {
                subject = 3
                subjectRank = 0
            }
            in 91..99   -> {
                subject = 4
                subjectRank = 1
            }
            else        -> {
                subject = 5
                subjectRank = 2
            }
        }

        //endregion

        // region [ Append treasure map, if indicated ]

        val paperTreasureMap = if ((artType == 0)||
            (Random.nextInt(1,101)<= itemRestrictions.paperMapChance)) {

            createTreasureMap(parentHoardID,"paper artwork")

        } else null

        // endregion

        // ---Generate and return new art object ---

        return ArtObject(0, parentHoardID, ArtObject.getRandomName(artType,subject),
            artType, renown, size, condition, materials, quality, ageInYears,
            subject, ( renown + size + condition + quality + subjectRank + ageRank )
        ) to paperTreasureMap
    }

    override fun createMagicItemTuple(parentHoardID: Int, givenTemplate: Int,
                                      providedTypes: List<String>,
                                      itemRestrictions: MagicItemRestrictions
    ) : NewMagicItemTuple{

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
        val USABLE_BY_ALL = mapOf(
            "fighter" to true,
            "thief" to true,
            "cleric" to true,
            "magic-user" to true,
            "druid" to true)

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

        /**
         *
         */
        fun getWeightedItemMap(limTemplates: List<LimitedMagicItemTemplate>): List<Pair<IntRange, Int>> {

            val listBuilder = mutableListOf<Pair<IntRange,Int>>()
            var lastMax     = 0
            var weight      : Int

            limTemplates.forEach {

                weight = it.itemWeight

                listBuilder.plusAssign(Pair(IntRange(lastMax+1, lastMax + weight),it.primaryKey))

                lastMax += weight
            }

            return listBuilder.toList()
        }

        var template:       MagicItemTemplate = SAMPLE_MAGIC_ITEM_TEMPLATE //TODO

        var baseTemplateID:     Int       // Container for primary key of the first template drawn. -1 indicates a template-less item
        var itemType:           String
        var itemCharges=        0
        var gmChoice =          false

        var currentRoll:        Int

        val notesLists=         LinkedHashMap<String,ArrayList<String>>()
        var specialItemType:    SpItType? = null
        var spellListOrder :    SpellCollectionOrder? = null
        var gemOrder:           GemOrder? = null

        // region Magic item detail holders

        var mTemplateID     = -1
        val mHoardID        = parentHoardID // TODO Inline this later; hoard ID can be added outside this function
        var mIconID         = "default"
        var mName           = ""
        var mSourceText     = "Source text"
        var mSourcePage     = 0
        var mXpValue        = 0
        var mGpValue        = 0.0
        var mClassUsability = USABLE_BY_ALL
        var mIsCursed       = false
        var mAlignment      = ""
        val mNotes          : List<List<String>>

        // endregion

        // region [ Generate Weighted Table A1 list with only allowed item types ]

        val allowedTypes = if (itemRestrictions.scrollMapChance > 0) {
            VALID_TABLE_TYPES.filter { providedTypes.contains(it) } + listOf("A3")
        } else {
            VALID_TABLE_TYPES.filter { providedTypes.contains(it) }
        }

        fun getWeightedProbabilityTable(): List<IntRange> { //TODO merge into getWeightedItemList()

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

        fun generateSpellScrollOrder(inheritedRoll:Int,
                                     isCursed: Boolean,
                                     inheritedDiscipline: SpCoDiscipline = SpCoDiscipline.ALL_MAGIC): SpellCollectionOrder {

            val useArcane = when (inheritedDiscipline){
                SpCoDiscipline.ARCANE -> true
                SpCoDiscipline.ALL_MAGIC -> Random.nextBoolean()
                else    -> false }
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

            return SpellCollectionOrder(
                SpCoType.SCROLL,
                if (useArcane) SpCoDiscipline.ARCANE else SpCoDiscipline.DIVINE,
                spellCount,
                spellRange,
                itemRestrictions.spellCoRestrictions.spellSources,
                itemRestrictions.spellCoRestrictions.allowRestricted,
                itemRestrictions.spellCoRestrictions.genMethod,
                isCursed,
                itemRestrictions.spellCoRestrictions.allowedCurses
            )
        }

        when (itemType) {

            "A2" -> { if (Random.nextInt(1, 101) == 100) gmChoice = true  }

            "A3" -> {

                if ((itemRestrictions.scrollMapChance > 0) &&
                    (Random.nextInt(1, 101) <= itemRestrictions.scrollMapChance)
                ) {

                    specialItemType = SpItType.TREASURE_MAP
                    itemType = "Map"
                    mName = "Treasure Map"
                    baseTemplateID = -1

                } else {

                    if ((providedTypes.contains("A3"))) {

                        currentRoll = Random.nextInt(1, 101)

                        if (currentRoll <= 33) {

                            //Spell scroll result
                            spellListOrder = generateSpellScrollOrder(currentRoll,false)

                            //TODO Streamline Magic item details for when special orders are indicated
                            itemType = "Spell Scroll"
                            mName = "Spell Scroll"
                            baseTemplateID = -1

                        } else {

                            if (currentRoll in 85..91) {

                                itemType = "Spell Scroll"
                                mName = "Spell Scroll"
                                spellListOrder =
                                    generateSpellScrollOrder(Random.nextInt(1,34),
                                        itemRestrictions.allowCursedItems)
                                baseTemplateID = -1
                                mIsCursed = true

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

            // TODO Replace when Daos are accessible
            val TEMPORARY_LIMITED_LIST = listOf(
                LimitedMagicItemTemplate(1,13),
                LimitedMagicItemTemplate(2,4),
                LimitedMagicItemTemplate(3,2),
                LimitedMagicItemTemplate(4,1)
            )

            var limitedTemplateList: List<LimitedMagicItemTemplate>
            var weightedItemTable: List<Pair<IntRange,Int>>

            // Get list of items of given type using Dao TODO
            limitedTemplateList = TEMPORARY_LIMITED_LIST

            //TODO error handling for empty entry

            // Pull primary key of magic item entry
            weightedItemTable = getWeightedItemMap(limitedTemplateList)

            var currentRoll: Int = Random.nextInt(weightedItemTable.first().first.first,
                weightedItemTable.last().first.last + 1)

            baseTemplateID = weightedItemTable[
                    weightedItemTable.indexOfFirst { it.first.contains(currentRoll) }
            ].second

            // Get base template by ID using Dao TODO
            template = SAMPLE_MAGIC_ITEM_TEMPLATE

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

            if (template.multiType == 3) mName = "${itemCharges}x $mName"

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

                val imitationTemplate: MagicItemTemplate

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
                    69 to "pitch black",
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
                        if (mIconID == "potion_empty") mIconID = "potion_alabaster"
                    }

                    in 3..4     -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: Alabaster")
                        if (mIconID == "potion_empty") mIconID = "potion_alabaster"
                    }

                    in 5..12    -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Round, long container")
                        if (mIconID == "potion_empty") mIconID = "potion_round_long"
                    }

                    in 13..14   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Square, long container")
                        if (mIconID == "potion_empty") mIconID = "potion_square_long"
                    }

                    in 15..19   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Conical bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_erlenmeyer"

                        isRoundBulb = true
                    }

                    in 20..48   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Spherical bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_round_short"

                        isRoundBulb = true
                    }

                    in 49..56   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Oval, 'squashed' bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_squashed"

                        isRoundBulb = true
                    }

                    in 57..60   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Tapering neck, Conical flask")
                        if (mIconID == "potion_empty") mIconID = "potion_fancy"
                    }

                    in 61..65   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Triangular bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_pointy"
                    }

                    in 66..69 -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("$material - Triangular, long container")
                        if (mIconID == "potion_empty") mIconID = "potion_hexagonal"
                    }

                    in 70..73   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("$material - Twisted, almost misshapen neck, small diamond bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_pointy"
                    }

                    in 74..76   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Test-tube style")
                        if (mIconID == "potion_empty") mIconID = "potion_tube"
                    }

                    in 77..78   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Lone wine bottle (Green glass):")
                        if (mIconID == "potion_empty") mIconID = "potion_winebottle"
                        itemCharges = Random.nextInt(1,4)

                        notesLists["Potion flavor text"]
                            ?.plusAssign(
                                "This container holds $itemCharges dose(s) of " +
                                        "potion - all of the same type. Liquid color is " +
                                        "not immediately discernible. [1]")
                    }

                    in 79..80   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Rectangular bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_square_long"
                    }

                    in 81..84   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Hexagonal, long container")
                        if (mIconID == "potion_empty") mIconID = "potion_hexagonal"
                    }

                    in 85..86 -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Curved, swirl bulb")
                        if (mIconID == "potion_empty") mIconID = "potion_fancy"

                        isRoundBulb = true
                    }

                    in 87..88   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Rectangular, long container")
                        if (mIconID == "potion_empty") mIconID = "potion_square_long"
                    }

                    89          -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: $material - Long necked bottle " +
                                    "with ${Random.nextInt(2,5)} round bulbs, " +
                                    "decreasing in size as they go up the bottle, all " +
                                    "totalling one dose of potion.")
                        if (mIconID == "potion_empty") mIconID = "potion_other"

                        itemCharges = 1

                        isRoundBulb = true
                    }

                    90          -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: Metal - Sealed tankard")
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Unless opened, these containers " +
                                    "prevent the potion from being viewed. [2]")
                        if (mIconID == "potion_empty") mIconID = "potion_tankard"
                    }

                    in 91..92   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: Metal - Flask")

                        notesLists["Potion flavor text"]
                            ?.plusAssign("Unless opened, these containers " +
                                    "prevent the potion from being viewed. [2]")

                        if (mIconID == "potion_empty") mIconID = "potion_metal_flask"
                    }

                    in 93..95   -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Special - Unusually shaped ${material.lowercase()} " +
                                    "(heart, skull, Apple, Standing Nymph, etc.)")
                        if (mIconID == "potion_empty") mIconID = "potion_unusual"
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

                        if (mIconID == "potion_empty") mIconID = "potion_berry"
                    }

                    97          -> {
                        notesLists["Potion flavor text"]
                            ?.plusAssign("Container: Special - Wooden or ${material.lowercase()} " +
                                    "charm hanging from a chain:")

                        if (mIconID == "potion_empty") mIconID = "potion_charm"

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
                        if (mIconID == "potion_empty") mIconID = "potion_other"
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
                            ?.plusAssign("Sealant: Sealed $material neck:")

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
                                else        -> "iunno, a bit like strawberries?"

                            } )

                // --- Add dosages remaining ---

                notesLists["Potion flavor text"]
                    ?.plusAssign("Found with $itemCharges dose(s) remaining")

            }

            // endregion

            // region [ Roll "Intelligent weapon info" ]

            if (Random.nextInt(1,101) <= template.intelChance) {

                var wIntelligence:          Int
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

                val wAlignment = if (mAlignment.isBlank()){

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

            } else if ( template.intelChance == -1 ) {

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

            // Generate magic item details for outliers and exceptional items
            // TODO Clean this chunk up in light of new Tuple schema
            when (mName) {

                "Treasure map"  -> { //TODO port to own function, scoped within this class

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
                    mIconID = "scroll_map"
                }

                "Spell Scroll"  -> {
                    mSourceText =   "GameMaster's Guide"
                    mSourcePage =   225
                    mXpValue =      0
                    mGpValue =      0.0
                    mClassUsability = USABLE_BY_ALL
                    mIconID = "scroll_base"
                }

                "GM's Choice"   -> {
                    mSourceText =   "GameMaster's Guide"
                    mSourcePage =   213
                    mXpValue =      0
                    mGpValue =      0.0
                    mClassUsability = USABLE_BY_ALL
                    mIconID = when (itemType){
                        "A2"    -> "potion_empty"
                        "A3"    -> "scroll_base"
                        "A4"    -> "ring_gold"
                        "A5"    -> "staff_ruby"
                        "A6"    -> "staff_iron"
                        "A7"    -> "wand_wood"
                        "A8"    -> "book_normal"
                        "A9"    -> "jewelry_box"
                        "A13"   -> "container_full"
                        "A14"   -> "dust_incense"
                        "A15"   -> "music_instument_wood"
                        "A24"   -> "artifact_box"
                        else    -> "container_chest"
                    }
                }
            }
        }

        if (EXCEPTIONAL_ITEMS.contains(mName)) {

            when (mName) {

                // TODO refactor this function to return SpellCollectionOrder
                "Ring of Spell Storing" -> {

                    specialItemType = SpItType.RING_OF_SPELL_STORING

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

                "Gut Stones"    -> gemOrder = GemOrder(GUT_STONE_KEY,itemCharges)

                "Ioun Stones"    -> specialItemType = SpItType.IOUN_STONES
            }
        }

        //endregion

        // region [ Convert mapped lists to nested list ]

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

        val specialItemOrder = if (specialItemType != null) {

            when (specialItemType) {

                SpItType.SPELL_SCROLL ->
                    if (spellListOrder != null)
                        SpecialItemOrder(mHoardID,SpItType.SPELL_SCROLL,spellListOrder)
                    else
                        null

                SpItType.RING_OF_SPELL_STORING ->
                    if (spellListOrder != null)
                        SpecialItemOrder(mHoardID,SpItType.RING_OF_SPELL_STORING,spellListOrder)
                    else
                        null

                SpItType.IOUN_STONES ->
                    SpecialItemOrder(mHoardID,SpItType.IOUN_STONES,null,itemCharges.coerceAtLeast(1))

                SpItType.TREASURE_MAP ->
                    SpecialItemOrder(mHoardID,SpItType.TREASURE_MAP,null)
            }

        } else null

        return NewMagicItemTuple(
            MagicItem(
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
                mNotes),
            null, gemOrder)
    }

    override fun convertOrderToSpellScroll(parentHoard : Int, order: SpellCollectionOrder): SpellCollection {

        // region < Local extension functions >

        fun SpCoGenMethod.fixGenerationMethod(inputType: SpCoDiscipline) : SpCoGenMethod {

            return when (inputType) {

                SpCoDiscipline.ARCANE -> {
                    if ((this == SpCoGenMethod.CHOSEN_ONE)||
                        (this == SpCoGenMethod.ANY_PHYSICAL)) {
                        SpCoGenMethod.TRUE_RANDOM
                    } else {
                        this
                    }
                }

                SpCoDiscipline.DIVINE -> {
                    if ((this == SpCoGenMethod.CHOSEN_ONE)||
                        (this == SpCoGenMethod.SPELL_BOOK)) {
                        SpCoGenMethod.TRUE_RANDOM
                    } else {
                        this
                    }

                }

                else                  -> SpCoGenMethod.TRUE_RANDOM
            }
        }

        fun IntRange.fixSpellRange(inputType: SpCoDiscipline = SpCoDiscipline.ALL_MAGIC) : IntRange {

            val fixedMinimum: Int
            val fixedMaximum: Int

            if (inputType == SpCoDiscipline.ARCANE){

                fixedMinimum = this.first.coerceIn(0,9)

                fixedMaximum = if (this.last > fixedMinimum) {
                    this.last.coerceIn(0,9)
                } else {
                    fixedMinimum
                }
            } else {

                fixedMinimum = this.first.coerceIn(1,7)

                fixedMaximum = if (this.last > fixedMinimum) {
                    this.last.coerceIn(1,7)
                } else {
                    fixedMinimum
                }
            }

            return IntRange(fixedMinimum,fixedMaximum)
        }

        fun SpCoDiscipline.asClassString() : String = when (this) {

            SpCoDiscipline.ARCANE   -> "Magic-User"
            SpCoDiscipline.DIVINE   -> "Cleric"
            SpCoDiscipline.NATURAL  -> "Druid"
            SpCoDiscipline.ALL_MAGIC-> "Assorted"
        }

        // endregion

        val spellList = ArrayList<Spell>()
        val propertiesList = ArrayList<Pair<String,Double>>()
        val itemName: String
        val iconID : String
        val spellType = order.spellType
        val generationMethod = order.genMethod.fixGenerationMethod(spellType)
        val spellCount = if ((generationMethod == SpCoGenMethod.TRUE_RANDOM)||
            (generationMethod == SpCoGenMethod.BY_THE_BOOK)) {

            order.spellCount.coerceIn(1..MAX_SPELLS_PER_SCROLL)

        } else {

            order.spellCount.coerceIn(0..MAX_SPELLS_PER_SCROLL)
        }
        val spellRange = order.spellLvRange.fixSpellRange(spellType)
        var curse = ""

        // region TODO [ Roll spells ] TODO

        //TODO Add genMethod discrimination and alternate methods. For now, only TRUE_RANDOM is effectively implemented.

        repeat(spellCount){

            //TODO query database instead of returning sample
            var spellTemplate = SAMPLE_ARCANE_SPELL

            // Add spell to running list
            spellList.add(convertTemplateToSpell(spellTemplate))
        }

        // endregion

        // region [ Roll non-spell details ]

        // Roll container
        propertiesList.plusAssign( when (Random.nextInt(1,7)) {

            1   -> "Container: Ivory tube" to 0.0
            2   -> "Container: Jade tube" to 0.0
            3   -> "Container: Leather tube" to 0.0
            4   -> "Container: Metal tube" to 0.0
            5   -> "Container: Wooden tube" to 0.0
            else-> "Container: None (found loose)" to 0.0
        })

        // Roll material
        propertiesList.plusAssign( when (Random.nextInt(1,11)){
            in 1..5 -> "Material: Vellum" to 0.0
            in 6..8 -> "Material: Parchment" to 0.0
            9       -> "Material: Papyrus" to 0.0
            else    -> "Material: Non-standard (GM's choice)" to 0.0
        })

        // endregion

        // region [ Add recommended curse (if applicable) ]

        // Roll to determine if erroneous scroll
        if ((Random.nextInt(1,101) in 1..Random.nextInt(5,11))||(order.allowedCurses != SpCoCurses.NONE)) {
            curse = "(GMG) Casting from this scroll will result in spell mishap (see GMG pg 212)."
        }

        // Add cursed effect from indicated sources.
        if ((order.isCursed)&&(curse.isNotBlank())) {

            /** Example curse list on GMG pgs 225-226 */
            val cursesExample = listOf(
                "(GMG) Bad luck (-1 on attacks and saving throws).",
                "(GMG) he character's beard grows one inch per minute.",
                "(GMG) The character is teleported away from the rest of the party.",
                "(GMG) Random monster appears and attacks (See GMG pg 319).",
                "(GMG) The character is polymorphed into a mouse.",
                "(GMG) The character shrinks to half his normal size.",
                "(GMG) This character is stricken with weakness, halving his Strength score.",
                "(GMG) The character falls into a deep sleep from which he cannot be roused.",
                "(GMG) The character develops an uncontrollable appetite.",
                "(GMG) The character must always talk in rhyme (preventing spell casting).",
                "(GMG) The character is stricken with cowardice and must make a morale check every time a monster is encountered.",
                "(GMG) The character's alignment is changed.",
                "(GMG) The character suffers amnesia.",
                "(GMG) The character feels compelled to give away all his belongings.",
                "(GMG) The character must save vs. paralyzation or suffer petrification.",
            )

            /** Curses extrapolated from official material, but not on example list */
            val cursesExtended = listOf(
                "[GMG+] The character suffers a spell mishap (see GMG pg 82, Table 7E).",
                "[GMG+] The character develops some form insanity (see GMG pg 86, Table 7H).",
                "[GMG+] The character suffers from a minor malevolent effect (see GMG pg 285, Table B125). Re-roll incompatible results.",
                "[SSG] The character suffers the effect of a Witch's Curse (see SSG pg 49, Table 5C).",
                "[SSG] The character experiences the effect of a Wild Surge (see SSG pg 38, Table 4L).",
                "[SSG] The character suffers from the effect of a Tattoo Effect (see SSG pg 35, Table 4G) for 1 week.",
                "[PHB] The character suffers the effect of Bestow Curse (see PHB page 215).",
            )

            /** Homebrew curses thought up by app developer */
            val cursesHomebrew = listOf(
                "[TrH] All reversible spells are reversed. Otherwise, minimum 50% chance of spell failure.",
                "[TrH] All spells inscribed on the scroll go off at once, as if a spell-jacked caster mis-casted.",
                "[TrH] The character loses access to one of their talents, determined at random.",
                "[TrH] Loud, embarrassing sound is produced on casting. User must save vs. apology or lose 5 honor.",
                "[TrH] The target of any beneficial spell (or, fail that, the caster) will suffer a Fumble on their next attack (see GMG pg 124, Table 8KK).",
                "[TrH] All magic items in character's possessions rendered unusable for 10 turns.",
                "[TrH] Spell effect is subject to potion miscibility effect as if imbibed (see GMG pg 221, Table B1).",
                "[TrH] Scroll explodes into a puff of gas (see GMG pg 335, Table F20) when used.",
                "[TrH] Trap materializes and activates, targeting user (see GMG pg 335, Table F19). Re-roll if nonsensical.",
                "[TrH] All active magical enhancements are dispelled on all friendly creatures within 10 ft of caster.",
                "[TrH] All active magical detriments are doubled (GM chooses if duration, magnitude, etc) on all friendly creatures within 10 ft of caster.",
                "[TrH] Effects of any spells cast from this scroll are purely illusory.",
                "[TrH] All ink on items in character's possession disappears for 24 hours.",
                "[TrH] The character immediately becomes one step more intoxicated (see GMG pgs 170-172).",
                "[TrH] The GM may use a single GM coupon, provided they do so immediately and target the caster."
            )

            val curseList = when (order.allowedCurses) {

                SpCoCurses.STRICT_GMG       -> cursesExample

                SpCoCurses.OFFICIAL_ONLY    -> listOf(cursesExample,cursesExtended).flatten()

                SpCoCurses.ANY_CURSE        -> listOf(cursesExample,cursesExtended,cursesHomebrew).flatten()

                SpCoCurses.NONE             -> listOf("")
            }

            val curseIndex = Random.nextInt(0,curseList.size)

            curse = curseList[curseIndex] + if (curseList[curseIndex].isNotBlank()) " {#${curseIndex}}" else ""
        }

        // endregion

        // region [ Get item name ]

        itemName = "${if (curse.isNotBlank()) "Cursed " else ""} $spellType Spell Scroll " +
                "(${spellCount}x Lv.${spellRange.first}-${spellRange.last})"

        // endregion

        // region [ Get Icon ID ]
        iconID = if (curse.isBlank()) {

            when (spellType) {
                SpCoDiscipline.ARCANE   -> "scroll_red"
                SpCoDiscipline.DIVINE   -> "scroll_blue"
                SpCoDiscipline.NATURAL  -> "scroll_green"
                else                    -> "scroll_base"
            }

        } else {

            "scroll_cursed"
        }
        // endregion

        // region [ Calculate GP and XP value ]

        var gpTotal = 0.0
        var xpTotal = 0

        if (propertiesList.isNotEmpty()) { propertiesList.forEach { (_, gpValue) -> gpTotal += gpValue} }

        if (spellList.isNotEmpty()) { spellList.forEach {
            gpTotal += if (it.spellLevel == 0) 75.0 else (300.0 * it.spellLevel)
            if (!(order.isCursed)) xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel)
        }}

        // endregion

        return SpellCollection(0,
            parentHoard,
            iconID,
            itemName,
            SpCoType.SCROLL,
            propertiesList.toList(),
            gpTotal,
            xpTotal,
            spellList.toList(),
            curse
        )
    }

    //TODO Add a NewMagicItemTuple processor for sorting valid magic items and special orders

    //TODO Add function or class for spell generation beyond true random

    override fun getSpellByLevelUp(_inputLevel: Int, enforcedSchool: String, rerollChoices: Boolean,
                                   useSSG: Boolean): Spell {

        val gmgSpellTablePgs = mapOf(
            "Div" to 77,
            "Ill" to 77,
            "Abj" to 78,
            "Enc" to 78,
            "Con" to 78,
            "Nec" to 78,
            "Alt" to 79,
            "Evo" to 79
        )
        val ssgSpellTablePgs = mapOf(
            "Div" to 13,
            "Ill" to 15,
            "Abj" to 8,
            "Enc" to 14,
            "Con" to 12,
            "Nec" to 18,
            "Alt" to 10,
            "Evo" to 17
        )

        val inputLevel = _inputLevel.coerceIn(1..9)
        var spellName: String
        var tempHolder = SAMPLE_ARCANE_SPELL
        var querySuccessful = false

        // Determine school of spell to roll.
        val school = if (Spell.checkIfValidSchool(enforcedSchool)) {

            enforcedSchool.lowercase().capitalized()

        } else {

            if (useSSG) { //Spellslinger's guide Table 1B, pg 7
                when (inputLevel) {
                    1   -> {
                        when(Random.nextInt(1,101)){
                            in 1..11    -> "Div"
                            in 12..22   -> "Ill"
                            in 23..27   -> "Abj"
                            in 28..37   -> "Enc"
                            in 38..44   -> "Con"
                            in 45..52   -> "Nec"
                            in 53..84   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    2   -> {
                        when(Random.nextInt(1,101)){
                            in 1..11    -> "Div"
                            in 12..23   -> "Ill"
                            in 24..30   -> "Abj"
                            in 31..39   -> "Enc"
                            in 40..49   -> "Con"
                            in 50..61   -> "Nec"
                            in 62..84   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    3   -> {
                        when(Random.nextInt(1,101)){
                            in 1..3     -> "Div"
                            in 4..15    -> "Ill"
                            in 16..26   -> "Abj"
                            in 27..33   -> "Enc"
                            in 34..42   -> "Con"
                            in 43..52   -> "Nec"
                            in 53..84   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    4   -> {
                        when(Random.nextInt(1,101)){
                            in 1..8     -> "Div"
                            in 9..16    -> "Ill"
                            in 17..23   -> "Abj"
                            in 24..31   -> "Enc"
                            in 32..40   -> "Con"
                            in 41..48   -> "Nec"
                            in 49..78   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    5   -> {
                        when(Random.nextInt(1,101)){
                            in 1..9     -> "Div"
                            in 10..19   -> "Ill"
                            in 20..26   -> "Abj"
                            in 27..34   -> "Enc"
                            in 35..44   -> "Con"
                            in 45..55   -> "Nec"
                            in 56..81   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    6   -> {
                        when(Random.nextInt(1,101)){
                            in 1..7     -> "Div"
                            in 8..18    -> "Ill"
                            in 19..27   -> "Abj"
                            in 28..35   -> "Enc"
                            in 36..46   -> "Con"
                            in 47..55   -> "Nec"
                            in 56..80   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    7   -> {
                        when(Random.nextInt(1,101)){
                            in 1..8     -> "Div"
                            in 9..18    -> "Ill"
                            in 19..25   -> "Abj"
                            in 26..36   -> "Enc"
                            in 37..53   -> "Con"
                            in 54..62   -> "Nec"
                            in 63..85   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    8   -> {
                        when(Random.nextInt(1,101)){
                            in 1..7     -> "Div"
                            in 8..14    -> "Ill"
                            in 15..21   -> "Abj"
                            in 22..32   -> "Enc"
                            in 33..49   -> "Con"
                            in 50..60   -> "Nec"
                            in 61..78   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    9   -> {
                        when(Random.nextInt(1,101)){
                            in 1..11    -> "Div"
                            in 12..19   -> "Ill"
                            in 20..27   -> "Abj"
                            in 28..36   -> "Enc"
                            in 37..51   -> "Con"
                            in 52..63   -> "Nec"
                            in 64..80   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    else-> Spell.validSchools.random()
                }
            } else { // GMG Table 7D pg 77
                when (inputLevel) {
                    1   -> {
                        when(Random.nextInt(1,101)){
                            in 1..6    -> "Div"
                            in 7..22   -> "Ill"
                            in 23..25   -> "Abj"
                            in 26..35   -> "Enc"
                            in 36..45   -> "Con"
                            46          -> "Nec"
                            in 47..88   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    2   -> {
                        when(Random.nextInt(1,101)){
                            in 1..12    -> "Div"
                            in 13..31   -> "Ill"
                            in 32..34   -> "Abj"
                            in 35..46   -> "Enc"
                            in 47..51   -> "Con"
                            52          -> "Nec"
                            in 53..85   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    3   -> {
                        when(Random.nextInt(1,101)){
                            in 1..3     -> "Div"
                            in 4..16    -> "Ill"
                            in 17..22   -> "Abj"
                            in 23..29   -> "Enc"
                            in 30..38   -> "Con"
                            in 39..47   -> "Nec"
                            in 48..84   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    4   -> {
                        when(Random.nextInt(1,101)){
                            in 1..3     -> "Div"
                            in 4..18    -> "Ill"
                            in 19..27   -> "Abj"
                            in 28..43   -> "Enc"
                            in 44..46   -> "Con"
                            in 47..51   -> "Nec"
                            in 52..82   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    5   -> {
                        when(Random.nextInt(1,101)){
                            in 1..4     -> "Div"
                            in 5..18    -> "Ill"
                            in 19..29   -> "Abj"
                            in 30..41   -> "Enc"
                            in 42..50   -> "Con"
                            in 51..57   -> "Nec"
                            in 58..79   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    6   -> {
                        when(Random.nextInt(1,101)){
                            in 1..4     -> "Div"
                            in 5..21    -> "Ill"
                            in 22..30   -> "Abj"
                            in 31..40   -> "Enc"
                            in 41..47   -> "Con"
                            in 48..51   -> "Nec"
                            in 52..81   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    7   -> {
                        when(Random.nextInt(1,101)){
                            in 1..3     -> "Div"
                            in 4..13    -> "Ill"
                            in 14..24   -> "Abj"
                            in 25..37   -> "Enc"
                            in 38..53   -> "Con"
                            in 54..58   -> "Nec"
                            in 59..84   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    8   -> {
                        when(Random.nextInt(1,101)){
                            in 1..3     -> "Div"
                            in 4..6     -> "Ill"
                            in 7..13    -> "Abj"
                            in 14..35   -> "Enc"
                            in 36..55   -> "Con"
                            in 56..58   -> "Nec"
                            in 59..77   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    9   -> {
                        when(Random.nextInt(1,101)){
                            in 1..4     -> "Div"
                            in 5..7     -> "Ill"
                            in 8..14    -> "Abj"
                            in 15..21   -> "Enc"
                            in 22..43   -> "Con"
                            in 44..54   -> "Nec"
                            in 55..82   -> "Alt"
                            else        -> "Evo"
                        }
                    }
                    else-> Spell.validSchools.random()
                }
            }
        }

        fun getSpellName(): String = when (school){

            "Abj"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG){
                            listOf("Alarm",
                                "Protection from Evil",
                                "Protection from Sunburn",
                                "Protective Amulet",
                                "Remove Fear",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Alarm",
                                "Protection from Evil"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG){
                            listOf(
                                "Filter",
                                "Magic Missile Reflection",
                                "Preserve",
                                "Protection from Cantrips",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Preserve",
                                "Protection from Cantrips"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG){
                            when (Random.nextInt(1,21)){
                                in 10..19   -> "GM Choice"
                                20          -> "Player Choice"
                                else        -> listOf(
                                    "Dispel Magic",
                                    "Dispel Silence",
                                    "Glyph of Ice",
                                    "Glyph of Sniping",
                                    "Non-Detection",
                                    "Proof from Teleportation",
                                    "Protection from Normal Missiles",
                                    "Quarantine" ).random()
                            }
                        } else {
                            listOf(
                                "Dispel Magic",
                                "Non-Detection",
                                "Protection from Normal Missiles",
                                "Ward Off Evil"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG){
                            listOf(
                                "Circle of Protection",
                                "Exploding Glyph",
                                "Fire Trap",
                                "Minor Globe of Invulnerability",
                                "Remove Curse",
                                "Wimpel's Dispelling Screen",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Minor Globe of Invulnerability",
                                "Remove Curse",
                                "Fire Trap"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG){
                            listOf(
                                "Avoidance",
                                "Dismissal",
                                "Jorrel's Private Sanctum",
                                "Spell Shield",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Containment",
                                "Dismissal",
                                "Avoidance"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG){
                            listOf(
                                "Anti-Animal Shell",
                                "Anti-Magic Shell",
                                "Break Hex",
                                "Globe of Invulnerability",
                                "Invulnerability to Magical Weapons",
                                "Repulsion",
                                "Spiritwrack",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Anti-Magic Shell",
                                "Break Hex",
                                "Globe of Invulnerability",
                                "Repulsion",
                                "Spiritwrack",
                                "GM Choice"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG){
                            listOf(
                                "Banishment",
                                "Sequester",
                                "Spell Turning",
                                "Volley",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Banishment",
                                "Spell Turning",
                                "Volley",
                                "Sequester"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG){
                            listOf(
                                "Dispel Enchantment",
                                "Gandle's Spell Immunity",
                                "Mind Blank",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Gandle's Spell Immunity",
                                "Mind Blank"
                            ).random()
                        }
                    }

                    else-> {
                        if (useSSG){
                            listOf(
                                "Elemental Aura",
                                "Immunity to Undeath",
                                "Imprisonment",
                                "Jebidiah's Ultimate Circle",
                                "Prismatic Sphere",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Prismatic Sphere",
                                "Imprisonment"
                            ).random()
                        }
                    }
                }
            }

            "Alt"   -> {

                when (inputLevel){

                    1   -> {
                        if (useSSG){
                            when (Random.nextInt(1,101)){
                                in 69..99   -> "GM Choice"
                                100         -> "Player Choice"
                                else        -> listOf(
                                    "Affect Normal Fires",
                                    "Burning Hands",
                                    "Chromatic Orb",
                                    "Color Spray",
                                    "Comprehend Languages",
                                    "Corpse Link",
                                    "Dancing Lights",
                                    "Enlarge",
                                    "Erase",
                                    "Evaporate",
                                    "Feather Fall",
                                    "Fireball, Barrage",
                                    "Firewater",
                                    "Fist of Stone",
                                    "Flutter Soft",
                                    "Gaze Reflection",
                                    "Hold Portal",
                                    "Jump",
                                    "Light",
                                    "Melt",
                                    "Mend",
                                    "Merge Coin Pile",
                                    "Message",
                                    "Metal Bug",
                                    "Minor Sphere of Perturbation",
                                    "Phantom Armor",
                                    "Pool Gold",
                                    "Precipitation",
                                    "Remove Thirst",
                                    "Resist Cold",
                                    "Resist Fire",
                                    "Shocking Grasp",
                                    "Spider Climb",
                                    "Wizard Mark"
                                ).random()
                            }
                        } else {
                            when (Random.nextInt(1,101)){
                                in 1..4     -> "Affect Normal Fires"
                                in 5..8     -> "Burning Hands"
                                in 9..11    -> "Chromatic Orb"
                                in 12..15   -> "Color Spray"
                                in 16..19   -> "Comprehend Languages"
                                in 20..23   -> "Dancing Lights"
                                in 24..27   -> "Enlarge"
                                in 28..30   -> "Erase"
                                in 31..34   -> "Feather Fall"
                                in 35..37   -> "Fireball, Barrage"
                                in 38..41   -> "Firewater"
                                in 42..45   -> "Flutter Soft"
                                in 46..49   -> "Gaze Reflection"
                                in 50..53   -> "Hold Portal"
                                in 54..57   -> "Jump"
                                in 58..61   -> "Light"
                                in 62..65   -> "Melt"
                                in 66..69   -> "Mend"
                                in 70..73   -> "Merge Coin Pile"
                                in 74..76   -> "Message"
                                in 77..79   -> "Minor Sphere of Perturbation"
                                in 80..82   -> "Phantom Armor"
                                in 83..87   -> "Pool Gold"
                                in 88..90   -> "Precipitation"
                                in 91..93   -> "Shocking Grasp"
                                in 94..96   -> "Spider Climb"
                                else        -> "Wizard Mark"
                            }
                        }
                    }

                    2   -> {

                        if (useSSG){

                            when(Random.nextInt(1,101)){
                                in 82..99  -> "GM Choice"
                                100     -> "Player Choice"
                                else    -> listOf(
                                    "Alter Self",
                                    "Chaotic Transformation",
                                    "Cheetah Speed",
                                    "Continual Light",
                                    "Darkness, 15’ Radius",
                                    "Deeppockets",
                                    "Elenwyd's Majestic Bosom",
                                    "Fire Telekinesis",
                                    "Fog Cloud",
                                    "Fool's Gold",
                                    "Fustis's Mnemonic Enhancer",
                                    "Galinor's Gender Reversal",
                                    "Irritation",
                                    "Knock",
                                    "Levitate",
                                    "Magic Missile Reflection",
                                    "Magic Mouth",
                                    "Pyrotechnics",
                                    "Rope Trick",
                                    "Shatter",
                                    "Strength",
                                    "Tattoo of Shame",
                                    "Telepathic Mute",
                                    "Vocalize",
                                    "Whispering Wind",
                                    "White Hot Metal",
                                    "Wizard Lock"
                                ).random()
                            }
                        } else {

                            when(Random.nextInt(1,101)){
                                in 1..5     -> "Deeppockets"
                                in 6..10    -> "Fool's Gold"
                                in 11..15   -> "Whispering Wind"
                                in 16..20   -> "Alter Self"
                                in 21..25   -> "Cheetah Speed"
                                in 26..30   -> "Continual Light"
                                in 31..35   -> "Darkness, 15’ Radius"
                                in 36..40   -> "Fire Telekinesis"
                                in 41..45   -> "Fog Cloud"
                                in 46..50   -> "Irritation"
                                in 51..55   -> "Knock"
                                in 56..60   -> "Levitate"
                                in 61..65   -> "Magic Mouth"
                                in 66..70   -> "Pyrotechnics"
                                in 71..75   -> "Rope Trick"
                                in 76..80   -> "Shatter"
                                in 81..84   -> "Strength"
                                in 85..88   -> "Tattoo of Shame"
                                in 89..92   -> "Telepathic Mute"
                                in 93..96   -> "White Hot Metal"
                                else        -> "Wizard Lock"
                            }
                        }
                    }

                    3   -> {

                        if (useSSG){

                            when (Random.nextInt(1,21)+Random.nextInt(1,21)){

                                2   -> "A Day in the Life"
                                3   -> "Airbolt"
                                4   -> "Arinathor's Dark Limbs"
                                5   -> "Blink"
                                6   -> "Cloudburst"
                                7   -> "Continual Darkness"
                                8   -> "Delude"
                                9   -> "Dispel Silence"
                                10  -> "Explosive Runes"
                                11  -> "Fireflow"
                                12  -> "Fly"
                                13  -> "Fool's Speech"
                                14  -> "Gandle's Humble Hut"
                                15  -> "Grow"
                                16  -> "Gust of Wind"
                                17  -> "Haste"
                                18  -> "Infravision"
                                19  -> "Item"
                                20  -> "Mericutyn's Grotesquely Distented Nose"
                                21  -> "Morton's Minute Meteors"
                                22  -> "Phantom Wind"
                                23  -> "Polymorph to Insect"
                                24  -> "Polymorph to Amphibian"
                                25  -> "Polymorph to Primate"
                                26  -> "Runes of Eyeball Implosion"
                                27  -> "Secret Page"
                                28  -> "Slow"
                                29  -> "Snapping Teeth"
                                30  -> "Tongues"
                                31  -> "Transmute Wood to Steel"
                                32  -> "Water Breathing"
                                33  -> "Wind Wall"
                                34  -> "Wraithform"
                                35  -> "Zargosa's Flaming Spheres of Torment"
                                40  -> "Player Choice"
                                else-> "GM Choice"
                            }
                        } else {

                            when(Random.nextInt(1,101)){
                                in 1..5     -> "Phantom Wind"
                                in 6..10    -> "Blink"
                                in 11..15   -> "Cloudburst"
                                in 16..20   -> "Continual Darkness"
                                in 21..25   -> "Delude"
                                in 26..30   -> "Explosive Runes"
                                in 31..35   -> "Fly"
                                in 36..40   -> "Gandle's Humble Hut"
                                in 41..45   -> "Grow"
                                in 46..50   -> "Gust of Wind"
                                in 51..55   -> "Haste"
                                in 56..60   -> "Infravision"
                                in 61..64   -> "Item"
                                in 65..68   -> "Polymorph to Amphibian"
                                in 69..72   -> "Polymorph to Primate"
                                in 73..78   -> "Runes of Eyeball Implosion"
                                in 79..81   -> "Secret Page"
                                in 82..84   -> "Slow"
                                in 85..88   -> "Tongues"
                                in 89..92   -> "Water Breathing"
                                in 93..96   -> "Wind Wall"
                                else        -> "Wraithform"
                            }
                        }
                    }

                    4   -> {
                        if (useSSG){

                            when(Random.nextInt(1,21)+Random.nextInt(1,11)){

                                2   -> "Close Portal"
                                3   -> "Dimension Door"
                                4   -> "Emergency Teleport at Random"
                                5   -> "Extension I"
                                6   -> "Fire Shield"
                                7   -> "Flying Familiar"
                                8   -> "Haarpang's Magnificent Sphere of Resiliency"
                                9   -> "Haarpang's Memory Kick"
                                10  -> "Hurl Animal"
                                11  -> "Massmorph"
                                12  -> "Perpetual Shocking Grasp"
                                13  -> "Pixie Wings"
                                14  -> "Plant Growth"
                                15  -> "Polymorph Other"
                                16  -> "Polymorph Self"
                                17  -> "Rainbow Pattern"
                                18  -> "Solid Fog"
                                19  -> "Stone Passage"
                                20  -> "Stoneskin"
                                21  -> "Tusks of the Oliphant"
                                22  -> "Ultravision"
                                23  -> "Vacancy"
                                24  -> "Wizard Eye"
                                25  -> "Zargosa's Lodge of Protection"
                                30  -> "Player Choice"
                                else-> "GM Choice"
                            }
                        } else {

                            when(Random.nextInt(1,101)){
                                in 1..5     -> "Rainbow Pattern"
                                in 6..10    -> "Vacancy"
                                in 11..15   -> "Haarpang's Magnificent Sphere of Resiliency"
                                in 16..20   -> "Zargosa's Lodge of Protection"
                                in 21..25   -> "Close Portal"
                                in 26..30   -> "Dimension Door"
                                in 31..35   -> "Emergency Teleport at Random"
                                in 36..40   -> "Extension I"
                                in 41..45   -> "Haarpang's Memory Kick"
                                in 46..50   -> "Hurl Animal"
                                in 51..55   -> "Massmorph"
                                in 56..60   -> "Perpetual Shocking Grasp"
                                in 61..65   -> "Plant Growth"
                                in 66..70   -> "Polymorph Other"
                                in 71..75   -> "Polymorph Self"
                                in 76..80   -> "Solid Fog"
                                in 81..84   -> "Stone Passage"
                                in 85..88   -> "Stoneskin"
                                in 89..92   -> "Ultravision"
                                in 93..96   -> "Wizard's Eye"
                                else        -> "Fire Shield"
                            }
                        }
                    }

                    5   -> {
                        if (useSSG){

                            when(Random.nextInt(1,101)) {
                                in 1..5     -> "Airy Water"
                                in 6..10    -> "Animal Growth"
                                in 11..15   -> "Avoidance"
                                in 16..20   -> "Breed Fusion"
                                in 21..25   -> "Centaur’s Gift, The"
                                in 26..30   -> "Distance Distortion"
                                in 31..35   -> "Drayton's Hidden Stash"
                                in 36..40   -> "Extension"
                                in 41..45   -> "Fabricate"
                                in 46..50   -> "Hiamohr's Unfortunate Incident"
                                in 51..55   -> "Jorrel's Private Sanctum"
                                in 56..60   -> "Manor's Mindsight"
                                in 61..65   -> "Polymorph Plant to Mammal"
                                in 66..70   -> "Stone Shape"
                                in 71..75   -> "Telekinesis"
                                in 76..80   -> "Teleport"
                                in 81..85   -> "Transmute Stone to Mud"
                                in 86..90   -> "Wall Passage"
                                in 91..95   -> "Wings of PanDemonium"
                                in 96..99   -> "GM Choice"
                                else        -> "Player Choice"
                            }
                        } else {

                            listOf(
                                "Avoidance",
                                "Drayton's Hidden Stash",
                                "Airy Water",
                                "Animal Growth",
                                "Distance Distortion",
                                "Extension II",
                                "Stone Shape",
                                "Telekinesis",
                                "Teleport",
                                "Transmute Rock to Mud",
                                "Wall Passage",
                                "Fabricate"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG){

                            when (Random.nextInt(1,101)){
                                in 89..99   -> "GM Choice"
                                100         -> "Player Choice"
                                else -> listOf(
                                    "Control Weather",
                                    "Cytogenesis",
                                    "Death Fog",
                                    "Disintegrate",
                                    "Extension III",
                                    "Glassee",
                                    "Guards and Wards",
                                    "Haarpang's Magnificent Sphere of Freezing",
                                    "Hyptor's Total Recall",
                                    "Karnaac's Tranformation",
                                    "Lower Water",
                                    "Mirage Arcana",
                                    "Move Earth",
                                    "Part Water",
                                    "Project Image",
                                    "Stone to Flesh",
                                    "Tentacles",
                                    "Transmute Water into Dust",
                                    "Transmute Metal to Water",
                                    "Velimurio’s Merger",
                                    "Zarba's Sphere of Personal Inclement Weather"
                                ).random()
                            }
                        } else {

                            listOf(
                                "Project Image",
                                "Haarpang's Magnificent Sphere of Freezing",
                                "Control Weather",
                                "Disintegrate",
                                "Extension III",
                                "Glassee",
                                "Hyptor's Total Recall",
                                "Lower Water",
                                "Move Earth",
                                "Part Water",
                                "Stone to Flesh",
                                "Transmute Water to Dust",
                                "Zarba's Sphere of Personal Inclement Weather",
                                "Karnaac's Transformation",
                                "Death Fog",
                                "Guards and Wards",
                                "Mirage Arcana",
                                "GM Choice"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG){

                            when(Random.nextInt(1,101)){
                                in 76..99   -> "GM Choice"
                                100         -> "Player Choice"
                                else        -> listOf(
                                    "Bone Javelin",
                                    "Command Element",
                                    "Create Shade",
                                    "Duo-Dimension",
                                    "Life Creation",
                                    "Phase Door",
                                    "Reverse Gravity",
                                    "Statue",
                                    "Teleport without Error",
                                    "Torment",
                                    "Transmute Rock to Lava",
                                    "Truename",
                                    "Tybalt's Planar Pacifier",
                                    "Vanish",
                                    "Zargosa's Opulent Manor House"
                                ).random()
                            }
                        } else {

                            listOf(
                                "Reverse Gravity",
                                "Duo-Dimension",
                                "Phase Door",
                                "Statue",
                                "Teleport without Error",
                                "Transmute Rock to Lava",
                                "Vanish",
                                "Zargosa's Opulent Manor House",
                                "Truename",
                                "Torment"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG){

                            when(Random.nextInt(1,13)){
                                1   -> "Glassteel"
                                2   -> "Haarpang's Magnificent Sphere of Telekinesis"
                                3   -> "Incendiary Cloud"
                                4   -> "Permanency"
                                5   -> "Polymorph Any Object"
                                6   -> "Sink"
                                12  -> "Player Choice"
                                else-> "GM Choice"
                            }
                        } else {

                            listOf(
                                "Incendiary Cloud",
                                "Glassteel",
                                "Permanency",
                                "Polymorph Any Object",
                                "Sink",
                                "Haarpang's Magnificent Sphere of Telekinesis"
                            ).random()
                        }
                    }

                    else   -> {

                        if (useSSG){

                            when(Random.nextInt(1,21)){
                                1   -> "Crystalbrittle"
                                2   -> "Hyptor's Disjunction"
                                3   -> "Ring of Swords"
                                4   -> "Shape Change"
                                5   -> "Succor"
                                6   -> "Teleport Intercampaignia"
                                7   -> "Teleport Intragenre"
                                8   -> "Tempestcone"
                                9   -> "Temporal Stasis"
                                10  -> "Time Stop"
                                20  -> "Player's Choice"
                                else-> "GM Choice"
                            }
                        } else {

                            listOf(
                                "Hyptor's Disjunction",
                                "Succor",
                                "Crystalbrittle",
                                "Shape Change",
                                "Teleport Intercampaignia",
                                "Teleport Intragenre",
                                "Temporal Stasis",
                                "Time Stop"
                            ).random()
                        }
                    }
                }
            }

            "Con"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG){
                            when (Random.nextInt(1,13)){
                                in 10..11   -> "GM Choice"
                                12          -> "Player Choice"
                                else        -> listOf(
                                    "Armor",
                                    "Conjure Mount",
                                    "Find Familiar",
                                    "Grease",
                                    "Power Word: Cartwheel",
                                    "Power Word: Moon",
                                    "Power Word: Summersault",
                                    "Push",
                                    "Unseen Servant"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Armor",
                                "Conjure Mount",
                                "Find Familiar",
                                "Grease",
                                "Push",
                                "Unseen Servant"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            listOf(
                                "Aname's Extra-Dimensional Mallet",
                                "Choke",
                                "Glitterdust",
                                "Munz's Bolt of Acid",
                                "Power Word: Belch",
                                "Power Word: Detect",
                                "Power Word: Light",
                                "Summon Swarm",
                                "Zed's Crystal Dagger",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Summon Swarm",
                                "Glitterdust",
                                "Munz's Bolt of Acid"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            listOf(
                                "Flame Arrow",
                                "Material",
                                "Monster Summoning I",
                                "Phantom Steed",
                                "Power Word: Attack",
                                "Power Word: Burn",
                                "Power Word: Chill",
                                "Sepia Snake Sigil",
                                "Zed's Crystal Dirk",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Phantom Steed",
                                "Flame Arrow",
                                "Sepia Snake Sigil",
                                "Monster Summoning I",
                                "Material",
                                "GM Choice"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            listOf(
                                "Monster Summoning II",
                                "Zargoza's Tentacled Fury",
                                "Duplicate",
                                "Power Word: Anosmitize",
                                "Power Word: Freeze",
                                "Power Word: Slow",
                                "Segwick's Tool Box",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Monster Summoning II",
                                "Zargosa’s Tentacled Fury"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            listOf(
                                "Conjure Elemental",
                                "Drayton's Hidden Stash",
                                "Hyptor's Faithful Bitch-Hound",
                                "Monster Summoning III",
                                "Power Word: Charm",
                                "Power Word: Fear",
                                "Power Word: Sleep",
                                "Summon Shadow",
                                "Wall of Bones",
                                "Water Bomb",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Drayton's Hidden Stash",
                                "Summon Shadow",
                                "Conjure Elemental",
                                "Hyptor’s Faithful Bitch-Hound",
                                "Monster Summoning III",
                                "GM Choice"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            listOf(
                                "Conjure Animals",
                                "Ensnarement",
                                "Fandango's Fiery Constrictor",
                                "Invisible Stalker",
                                "Monster Summoning IV",
                                "Power Word: Forget",
                                "Power Word: Silence",
                                "Tentacles",
                                "Wall of Thorns",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Ensnarement",
                                "Invisible Stalker",
                                "Conjure Animals",
                                "Monster Summoning IV"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Cacodemon",
                                "Limited Wish",
                                "Monster Summoning V",
                                "Power Word: Deafness",
                                "Power Word: Dispel",
                                "Power Word: Heal",
                                "Power Word: Stun",
                                "Zargosa's Instant Summons",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Limited Wish",
                                "Cacodemon",
                                "Monster Summoning V",
                                "Power Word: Stun",
                                "Prismatic Wall",
                                "Zargosa’s Instant Summons"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG) {
                            listOf(
                                "Grasping Death",
                                "Jonid's Jewel",
                                "Maze",
                                "Monster Summoning VI",
                                "Power Word: Banish",
                                "Power Word: Blind",
                                "Power Word: Terrify",
                                "Symbol",
                                "Trap the Soul",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Grasping Death",
                                "Maze",
                                "Monster Summoning VI",
                                "Power Word: Blind",
                                "Symbol",
                                "Trap the Soul"
                            ).random()
                        }
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Alter Reality",
                                "Demon Flame",
                                "Gate",
                                "Monster Summoning VII",
                                "Power Word: Annihilate",
                                "Power Word: Dance",
                                "Power Word: Kill",
                                "Prismatic Sphere",
                                "Wish",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Prismatic Sphere",
                                "Demon Flame",
                                "Gate",
                                "Power Word: Kill",
                                "Wish",
                                "Monster Summoning VII"
                            ).random()
                        }
                    }
                }
            }

            "Div"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG) {
                            when(Random.nextInt(1,9)+Random.nextInt(1,9)){
                                2   -> "Detect Disease"
                                3   -> "Detect Illusion"
                                4   -> "Detect Magic"
                                5   -> "Detect Phase"
                                6   -> "Detect Undead"
                                7   -> "Divining Rod"
                                8   -> "Fog Vision"
                                9   -> "Identify"
                                10  -> "Read Magic"
                                16  -> "Player Choice"
                                else-> "GM Choice"
                            }
                        } else {
                            listOf(
                                "Detect Magic",
                                "Detect Undead",
                                "Fog Vision",
                                "Identify"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) <= 8){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Death Recall",
                                    "Detect Charm",
                                    "Detect Evil",
                                    "Detect Invisibility",
                                    "Detect Life",
                                    "ESP",
                                    "Find Traps",
                                    "Know Alignment",
                                    "Locate Object",
                                    "Premonition",
                                    "Reveal Secret Portal",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Detect Evil",
                                "Detect Invisibility",
                                "ESP",
                                "Know Alignment",
                                "Locate Object",
                                "Premonition",
                                "Reveal Secret Portal",
                                "GM Choice"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            listOf(
                                "Clairaudience",
                                "Clairvoyance",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Clairaudience",
                                "Clairvoyance"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            listOf(
                                "Detect Lie",
                                "Detect Scrying",
                                "Divination Enhancement",
                                "Find Treasure",
                                "Magic Mirror",
                                "Omen",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Detect Scrying",
                                "Magic Mirror"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            listOf(
                                "Contact Other Plane",
                                "False Vision",
                                "Segwick's Seeking",
                                "Wizard's Oracle",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Contact Other Plane",
                                "False Vision"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            listOf(
                                "Detect Ulterior Motive",
                                "Legend Lore",
                                "Revelation",
                                "True Seeing",
                                "GM Choice",
                                "GM Choice"
                            ).random()
                        } else {
                            listOf(
                                "Legend Lore",
                                "True Seeing"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Anticipation",
                                "Find the Path",
                                "Manor's Mind Vision",
                                "Vision",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            "Vision"
                        }
                    }

                    8   -> {
                        if (useSSG) {
                            listOf(
                                "Diviner's Insight",
                                "Screen",
                                "Jonid's Jewel",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            "Screen"
                        }
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Detect All",
                                "Foresight",
                                "Glyph of Divination",
                                "Greater Divination Enhancement",
                                "Seek Teleporter",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            "Foresight"
                        }
                    }
                }
            }

            "Enc"   -> {

                when (inputLevel){

                    1   -> {
                        if (useSSG) {
                            listOf(
                                "Aura of Innocence",
                                "Befriend",
                                "Charm Person",
                                "Divining Rod",
                                "Hypnotism",
                                "Magic Stone",
                                "Protective Amulet",
                                "Remove Thirst",
                                "Run",
                                "Shift Blame",
                                "Sleep",
                                "Taunt"
                            ).random()
                        } else {
                            listOf(
                                "Aura of Innocence",
                                "Befriend",
                                "Charm Person",
                                "Hypnotism",
                                "Run",
                                "Shift Blame",
                                "Sleep",
                                "Taunt"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            listOf(
                                "Bind",
                                "Deeppockets",
                                "Forget",
                                "Fustis's Mnemonic Enhancer",
                                "Murgain's Muster Strength",
                                "Proadus’ Uncontrollable Fit of Laughter",
                                "Ray of Enfeeblement",
                                "Scare",
                                "Total Control",
                                "GM Choice"
                            ).random()
                        } else {
                            listOf(
                                "Ray of Enfeeblement",
                                "Scare",
                                "Total Control",
                                "Forget",
                                "Bind",
                                "Proadus’ Uncontrollable Fit of Laughter",
                                "Murgain's Muster Strength",
                                "Deeppockets"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            listOf(
                                "Bone Club",
                                "Delay Death",
                                "Empathic Link",
                                "Hold Person",
                                "No Fear",
                                "Perceived Malignment",
                                "Suggestion",
                                "Yargroove's Eidelon"
                            ).random()
                        } else {
                            listOf(
                                "Hold Person",
                                "No Fear",
                                "Perceived Malignment",
                                "Suggestion"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            listOf(
                                "Charm Monster",
                                "Confusion",
                                "Emotion",
                                "Enchanted Weapon",
                                "Fire Charm",
                                "Fumble",
                                "Mage Lock",
                                "Magic Mirror",
                                "Stirring Oration",
                                "Zargosa's Lodge of Protection"
                            ).random()
                        } else {
                            listOf(
                                //Omitted "Haarpang’s Magnificent Sphere of Resiliency" due to being Alt/Evo
                                "Zargosa's Lodge of Protection",
                                "Charm Monster",
                                "Confusion",
                                "Fire Charm",
                                "Fumble",
                                "Stirring Oration",
                                "Magic Mirror",
                                "Emotion",
                                "Mage Lock",
                                "Enchanted Weapon",
                                "GM Choice"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            listOf(
                                "Chaos",
                                "Dolor",
                                "Domination",
                                "Drayton’s Engaging Conversation",
                                "Fabricate",
                                "Feeblemind",
                                "Hold Monster",
                                "Magic Staff"
                            ).random()
                        } else {
                            listOf(
                                "Fabricate",
                                "Chaos",
                                "Dolor",
                                "Domination",
                                "Feeblemind",
                                "Hold Monster",
                                "Drayton’s Engaging Conversation",
                                "GM Choice"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            listOf(
                                "Charm of Undying Devotion",
                                "Enchant an Item",
                                "Eyebite",
                                "Geas",
                                "Guards and Wards",
                                "Mass Suggestion",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Eyebite",
                                "Charm of Undying Devotion",
                                "Mass Suggestion",
                                "Enchant an Item",
                                "Geas",
                                "Guards and Wards"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Anger Deity",
                                "Charm Plants",
                                "Major Domination",
                                "Mass Hypnosis",
                                "Steal Enchantment",
                                "Truename",
                                "Tybalt's Planar Pacifier",
                                "Zarba’s Sphere of Insanity"
                            ).random()
                        } else {
                            listOf(
                                "Anger Deity",
                                "Charm Plants",
                                "Zarba’s Sphere of Insanity",
                                "Truename",
                                "Shadow Walk",
                                "GM Choice"
                            ).random()
                        }
                    }

                    8   -> {

                        listOf(
                            "Sink",
                            "Antipathy-Sympathy",
                            "Mass Charm",
                            "Munari’s Irresistible Jig",
                            "Binding",
                            "Mimic Caster",
                            "Demand",
                            "GM Choice"
                        ).random()
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Hyptor's Disjunction",
                                "Mass Domination",
                                "Programmed Amnesia",
                                "Succor",
                                "GM Choice",
                                "GM Choice"
                            ).random()
                        } else {
                            listOf(
                                "Hyptor's Disjunction",
                                "Succor"
                            ).random()
                        }
                    }
                }
            }

            "Ill"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 15..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Audible Glamer",
                                    "Change Self",
                                    "Corpse Visage",
                                    "Faerie Phantoms",
                                    "Fool's Silver",
                                    "Gabal's Magic Aura",
                                    "Imaginary Friend",
                                    "Phantasmal Fireball",
                                    "Phantasmal Force",
                                    "Smell Immunity",
                                    "Spook",
                                    "Throw Voice",
                                    "Wrygal’s Delicious Deception",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Audible Glamer",
                                "Change Self",
                                "Faerie Phantoms",
                                "Gabal's Magic Aura",
                                "Phantasmal Fireball",
                                "Phantasmal Force",
                                "Phantasmal Force",
                                "Phantom Armor",
                                "Smell Immunity",
                                "Spook",
                                "Throw Voice",
                                "Wrygal’s Delicious Deception"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 15..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Blindness",
                                    "Blur",
                                    "Dancing Shadows",
                                    "Deafness",
                                    "Deepen Shadows",
                                    "Fascinate",
                                    "Fool's Gold",
                                    "Hypnotic Pattern",
                                    "Improved Phantasmal Force",
                                    "Invisibility",
                                    "Mirror Image",
                                    "Misdirection",
                                    "Whispering Wind",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Blindness",
                                "Blur",
                                "Deafness",
                                "Fascinate",
                                "Gandle's Feeble Trap",
                                "Hypnotic Pattern",
                                "Improved Phantasmal Force",
                                "Invisibility",
                                "Mirror Image",
                                "Misdirection",
                                "Whispering Wind",
                                "Fool's Gold"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            listOf(
                                "Illusionary Script",
                                "Invisibility, 10’ Radius",
                                "Paralyzation",
                                "Phantom Steed",
                                "Phantom Wind",
                                "Spectral Force",
                                "Wraithform",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Illusionary Script",
                                "Invisibility, 10’ Radius",
                                "Paralyzation",
                                "Spectral Force",
                                "Phantom Steed",
                                "Wraithform",
                                "Phantom Wind",
                                "GM Choice"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            listOf(
                                "Dispel Exhaustion",
                                "Fear",
                                "Hallucinatory Terrain",
                                "Illusionary Wall",
                                "Improved Invisibility",
                                "Minor Creation",
                                "Phantasmal Killer",
                                "Rainbow Pattern",
                                "Shadow Monsters",
                                "Vacancy"
                            ).random()
                        } else {
                            listOf(
                                "Shadow Monsters",
                                "Dispel Exhaustion",
                                "Fear",
                                "Illusionary Wall",
                                "Improved Invisibility",
                                "Minor Creation",
                                "Phantasmal Killer",
                                "Rainbow Pattern",
                                "Hallucinatory Terrain",
                                "Vacancy"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            listOf(
                                "Advanced Illusion",
                                "Demi-Shadow Monster",
                                "Dream",
                                "Major Creation",
                                "Seeming",
                                "Shadow Door",
                                "Shadow Magic",
                                "Tempus Fugit",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Major Creation",
                                "Advanced Illusion",
                                "Demi-Shadow Monster",
                                "Seeming",
                                "Shadow Door",
                                "Shadow Magic",
                                "Tempus Fugit",
                                "Dream"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            listOf(
                                "Demi-Shadow Magic",
                                "Mirage Arcana",
                                "Mislead",
                                "Perpetual Illusion",
                                "Phantasmagoria",
                                "Programmed Illusion",
                                "Project Image",
                                "Shades",
                                "Veil",
                                "GM Choice"
                            ).random()
                        } else {
                            listOf(
                                "Eyebite",
                                "Mirage Arcana",
                                "Project Image",
                                "Demi-Shadow Magic",
                                "Mislead",
                                "Perpetual Illusion",
                                "Phantasmagoria",
                                "Programmed Illusion",
                                "Shades",
                                "Veil"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Mass Hypnosis",
                                "Mass Invisibility",
                                "Merryweather's Dramatic Death",
                                "Sequester",
                                "Shadow Walk",
                                "Shadowcat",
                                "Simulacrum",
                                "GM Choice"
                            ).random()
                        } else {
                            listOf(
                                "Sequester",
                                "Shadow Walk",
                                "Mass Invisibility",
                                "Simulacrum"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG) {
                            listOf(
                                "Mind Maze",
                                "Screen",
                                "GM Choice"
                            ).random()
                        } else {
                            "Screen"
                        }
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Shadow Creep",
                                "Weird"
                            ).random()
                        } else {
                            "Weird"
                        }
                    }
                }
            }

            "Evo"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 15..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Alarm",
                                    "Bash Door",
                                    "Chromatic Orb",
                                    "Copy",
                                    "Fireball, Sidewinder Factor 1",
                                    "Haarpang's Floating Cart",
                                    "Icy Sphere",
                                    "Jack Punch",
                                    "Kachirut's Exploding Palm",
                                    "Magic Missile",
                                    "Magic Shield",
                                    "Resist Cold",
                                    "Wall of Fog",
                                    "Yudder's Whistle of Hell's Gate",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Alarm",
                                "Bash Door",
                                "Chromatic Orb",
                                "Fireball, Sidewinder Factor 1",
                                "Haarpang's Floating Cart",
                                "Magic Missile",
                                "Magic Shield",
                                "Wall of Fog",
                                "Yudder's Whistle of Hell's Gate",
                                "GM Choice"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 16..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Chain of Fire",
                                    "Cloud of Pummeling Fists",
                                    "Fireball, Sidewinder Factor 2",
                                    "Fireball, Skipping Betty",
                                    "Flaming Sphere",
                                    "Heat Seeking Fist of Thunder",
                                    "Ice Knife",
                                    "Icy Sphere", // per HackJournal errata
                                    "Kachirut's Kinetic Strike",
                                    "Magic Missile, Sidewinder",
                                    "Magic Missile of Skewering",
                                    "Shield Screen",
                                    "Stinking Cloud",
                                    "Web",
                                    "Whip",
                                    "Zed's Crystal Dagger",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Chain of Fire",
                                "Cloud of Pummeling Fists",
                                "Fireball, Sidewinder Factor 2",
                                "Fireball, Skipping Betty",
                                "Flaming Sphere",
                                "Heat Seeking Fist of Thunder",
                                "Magic Missile of Skewering",
                                "Stinking Cloud",
                                "Web",
                                "Whip"
                            ).random()
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 16..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Bash Face",
                                    "Fireball",
                                    "Fireball, Sidewinder Factor 3",
                                    "Fireball, Scatter-Blast",
                                    "Force Hammer",
                                    "Glyph of Ice",
                                    "Glyph of Sniping",
                                    "Lightning Bolt",
                                    "Material",
                                    "Morton's Minute Meteors",
                                    "Preemptive Strike",
                                    "Sure Grip Snare",
                                    "Wall of Water",
                                    "Zargosa’s Flaming Spheres of Torment",
                                    "Zed's Crystal Dirk",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Zargosa’s Flaming Spheres of Torment",
                                "Material",
                                "Bash Face",
                                "Fireball",
                                "Fireball, Sidewinder Factor 3",
                                "Fireball, Scatter-Blast",
                                "Lightning Bolt",
                                "Preemptive Strike",
                                "Sure Grip Snare",
                                "GM Choice"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            when(Random.nextInt(1,101)) {
                                in 1..5     -> "Delayed Magic Missile"
                                in 6..10    -> "Dig"
                                in 11..15   -> "Divination Enhancement"
                                in 16..20   -> "Exploding Glyph"
                                in 21..25   -> "Fire Shield"
                                in 26..30   -> "Fire Trap"
                                in 31..35   -> "Fireball, Land Scraper"
                                in 36..40   -> "Fireball, Sidewinder Factor 4"
                                in 41..45   -> "Fireball, Volley"
                                in 46..50   -> "Force Grenade"
                                in 51..55   -> "Haarpang’s Magnificent Sphere of Resiliency"
                                in 56..60   -> "Ice Storm"
                                in 61..65   -> "Mist of Corralling"
                                in 66..70   -> "Shout"
                                in 71..75   -> "Silver Globes"
                                in 76..80   -> "Wall of Acid"
                                in 81..85   -> "Wall of Fire"
                                in 86..90   -> "Wall of Ice"
                                in 91..95   -> "Wimpel's Dispelling Screen"
                                in 96..99   -> "GM Choice"
                                else        -> "Player Choice"
                            }
                        } else {
                            listOf(
                                "Fire Shield",
                                "Dig",
                                "Fireball, Land Scraper",
                                "Fireball, Sidewinder Factor 4",
                                "Fireball, Volley",
                                "Ice Storm",
                                "Mist of Corralling",
                                "Shout",
                                "Wall of Acid",
                                "Wall of Fire",
                                "Wall of Ice",
                                "Fire Trap"

                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 15..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Cloudkill",
                                    "Dream",
                                    "Fireball, Sidewinder Factor 5",
                                    "Fireball, Torrential",
                                    "Haarpang's Polar Screen",
                                    "Lyggl's Cone of Cold",
                                    "Preston's Moonbow",
                                    "Sending", // Corrected from "Seeming"
                                    "Shincock's Major Missile",
                                    "Stone Sphere",
                                    "Wall of Force",
                                    "Wall of Iron",
                                    "Wall of Stone",
                                    "Zarba’s Guardian Hand",
                                    "Player's Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Drayton’s Engaging Conversation",
                                "Cloudkill",
                                "Fireball, Sidewinder Factor 5",
                                "Fireball, Torrential",
                                "Lyggl's Cone of Cold",
                                "Sending",
                                "Stone Sphere",
                                "Wall of Force",
                                "Wall of Iron",
                                "Wall of Stone",
                                "Zarba’s Guardian Hand",
                                "Dream"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 16..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Body Heat Activation Spell",
                                    "Bradley's Besieging Bolt",
                                    "Chain Lightning",
                                    "Contingency",
                                    "Death Fog",
                                    "Fireball, Proximity Fused",
                                    "Fireball, Show-No-Mercy",
                                    "Gauntlet of Teeth",
                                    "Guards and Wards",
                                    "Haarpang’s Magnificent Sphere of Freezing",
                                    "Haarpang's Orb of Containment",
                                    "Kaarnac's Transformation",
                                    "Snap Drake",
                                    "Spiritwrack",
                                    "Zarba's Shoving Hand",
                                    "Player Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Haarpang’s Magnificent Sphere of Freezing",
                                "Kaarnac's Transformation",
                                "Death Fog",
                                "Guards and Wards",
                                "Spiritwrack",
                                "Body Heat Activation Spell",
                                "Chain Lightning",
                                "Contingency",
                                "Fireball, Proximity Fused",
                                "Fireball, Show-No-Mercy",
                                "Zarba's Shoving Hand",
                                "GM Choice"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Bone Javelin",
                                "Dragon Breath",
                                "Fireball, Delayed Blast",
                                "Flame Chase",
                                "Forcecage",
                                "Hyptor’s Shimmering Sword",
                                "Limited Wish",
                                "Merrywether's Frost Fist", //TODO search for "Merryweather" in other spell names
                                "Torment",
                                "Zarba's Grasping Hand",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Fireball, Delayed Blast",
                                "Forcecage",
                                "Hyptor’s Shimmering Sword",
                                "Zarba's Grasping Hand",
                                "Torment",
                                "Limited Wish"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG) {
                            if (Random.nextInt(1,21) in 11..19){
                                "GM Choice"
                            } else {
                                listOf(
                                    "Blizzard",
                                    "Demand",
                                    "Fireball, Death Brusher",
                                    "Fireball, Maximus",
                                    "Freeze",
                                    "Haarpang’s Magnificent Sphere of Telekinesis",
                                    "Hornet's Nest",
                                    "Incendiary Cloud",
                                    "Shooting Stars",
                                    "Zarba's Fist of Rage",
                                    "Player's Choice"
                                ).random()
                            }
                        } else {
                            listOf(
                                "Binding",
                                "Haarpang’s Magnificent Sphere of Telekinesis",
                                "Demand",
                                "Fireball, Death Brusher",
                                "Fireball, Maximus",
                                "Zarba's Fist of Rage",
                                "Incendiary Cloud",
                                "GM Choice"
                            ).random()
                        }
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Astral Spell",
                                "Elemental Aura",
                                "Energy Drain",
                                "Fireball, Lava Yield",
                                "Ice Juggernaut",
                                "Kachirut's White Lance",
                                "Meteor Swarm",
                                "Tempestcone",
                                "Zarba's Crushing Hand",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Astral Spell",
                                "Fireball, Lava Yield",
                                "Meteor Swarm",
                                "Zarba's Crushing Hand",
                                "Energy Drain",
                                "GM Choice"
                            ).random()
                        }
                    }
                }
            }

            "Nec"   -> {
                when (inputLevel){

                    1   -> {
                        if (useSSG) {
                            listOf(
                                "Animate Dead Animals",
                                "Chill Touch",
                                "Corpse Visage",
                                "Detect Undead",
                                "Disable Hand",
                                "Exterminate",
                                "Ralph's Placid Arrow",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Chill Touch",
                                "Detect Undead"
                            ).random()
                        }
                    }

                    2   -> {
                        if (useSSG) {
                            listOf(
                                "Choke",
                                "Death Recall",
                                "Disable Foot",
                                "Fihrsid's Horrid Armor",
                                "Ghoul Touch",
                                "Slow Healing",
                                "Spectral Hand",
                                "Spy of Derijnah",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            "Spectral Hand"
                        }
                    }

                    3   -> {
                        if (useSSG) {
                            listOf(
                                "Bone Club",
                                "Charm Undead",
                                "Delay Death",
                                "Feign Death",
                                "Hold Undead",
                                "Hovering Skull",
                                "Murgain's Migraine",
                                "Pain Touch",
                                "Rot Dawgs",
                                "Vampiric Touch",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Charm Undead",
                                "Feign Death",
                                "Hold Undead",
                                "Murgain's Migraine",
                                "Vampiric Touch",
                                "GM Choice"
                            ).random()
                        }
                    }

                    4   -> {
                        if (useSSG) {
                            listOf(
                                "Contagion",
                                "Enervation",
                                "Poison",
                                "Zombie Slave",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Contagion",
                                "Enervation",
                                "Zombie Slave"
                            ).random()
                        }
                    }

                    5   -> {
                        if (useSSG) {
                            listOf(
                                "Animate Dead",
                                "Force Shapechange",
                                "Magic Jar",
                                "Mummy Rot",
                                "Throbbing Bones",
                                "Touch of Death",
                                "Wall of Bones",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Summon Shadow",
                                "Animate Dead",
                                "Mummy Rot",
                                "Touch of Death"
                            ).random()
                        }
                    }

                    6   -> {
                        if (useSSG) {
                            listOf(
                                "Aliron's Dark Graft",
                                "Dead Man's Eyes",
                                "Death Spell",
                                "Reincarnation",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Death Spell",
                                "Reincarnation"
                            ).random()
                        }
                    }

                    7   -> {
                        if (useSSG) {
                            listOf(
                                "Control Undead",
                                "Finger of Death",
                                "Harm",
                                "Zombie Double",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Control Undead",
                                "Finger of Death"
                            ).random()
                        }
                    }

                    8   -> {
                        if (useSSG) {
                            listOf(
                                "Clone",
                                "Death Chain",
                                "Defoliate",
                                "Shadow Form",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            "Clone"
                        }
                    }

                    else-> {
                        if (useSSG) {
                            listOf(
                                "Blood Curse",
                                "Death Rune",
                                "Energy Drain",
                                "Fawlgar's Grasping Death",
                                "Immunity to Undeath",
                                "Master Undead",
                                "GM Choice",
                                "GM Choice",
                                "GM Choice",
                                "Player Choice"
                            ).random()
                        } else {
                            listOf(
                                "Death Rune",
                                "Energy Drain",
                                "Fawlgar's Grasping Death"
                            ).random()
                        }
                    }
                }
            }

            else    -> "GM Choice"
        }

        //TODO implement function for try/catch of querying db for template and setting querySuccessful to true if successful
        fun tryTempFetch() {
            tempHolder = SAMPLE_ARCANE_SPELL //TODO actually query DB using inputLevel and spellName as arguments.
            querySuccessful = true           //TODO only return true if valid spell template is found
        }

        //minor to-do: Add spell lists for specialists/unorthodox practitioners

        // Attempt to roll a valid spell
        do {

            // Get spell name from copied tables
            spellName = getSpellName()

            // Fetch first valid entry from database TODO Not implemented; just returns same spell for now.
            if ((spellName == "GM Choice")||(spellName == "Player Choice")){
                if (rerollChoices) {

                    // Re-roll until non-choice entry is selected.
                    while ((spellName == "GM Choice")||(spellName == "Player Choice")) { spellName = getSpellName() }

                    tryTempFetch()

                } else {

                    // Return a custom "Choice" SpellTemplate
                    tempHolder = SpellTemplate(0,spellName,0,
                        if (useSSG) {
                            "Spellslinger's Guide to Wurld Domination"
                        } else {"Gamemaster's Guide"},
                        if (useSSG) {
                            ssgSpellTablePgs.getOrDefault(school,7)
                        } else {
                            gmgSpellTablePgs.getOrDefault(school,77) },
                        0, inputLevel, school, "", "", "", "")

                    querySuccessful = true
                }
            } else {

                tryTempFetch()
            }

        } while (!(querySuccessful)||((rerollChoices)&&((spellName == "GM Choice")||(spellName == "Player Choice"))))

        return convertTemplateToSpell(tempHolder)
    }

    override fun getInitialSpellbookSpells(_specialistType: String, useSSG: Boolean): List<Spell> {
        val NESTED_OFFENSIVE_REROLL = 0
        val NESTED_DEFENSIVE_REROLL = 1
        val NESTED_MISC_REROLL = 2

        val specialistType = if (Spell.mageSpecialistTags.contains(_specialistType)) {
            _specialistType
        } else ""

        val spellNameList = arrayListOf("Read Magic","Write")
        var tempHolder: SpellTemplate = SAMPLE_ARCANE_SPELL
        val spellList = ArrayList<Spell>()
        var (offensiveRolls,defensiveRolls,miscRolls,schoolRolls) = when (specialistType) {
            "Battle_Mage"   -> listOf(3,0,0,0)
            ""              -> listOf(1,1,1,0) // Generalist Magic-User
            else            -> listOf(0,0,0,3) // Single Specialist
        }
        var (offensiveRerolls,defensiveRerolls,miscRerolls) = 0

        val gmgOffensiveSpells = listOf(
            "Befriend",
            "Burning Hands",
            "Charm Person",
            "Chill Touch",
            "Chromatic Orb",
            "Color Spray",
            "Enlarge",
            "Fireball, Barrage",
            "Fireball, Sidewinder Factor 1",
            "Firewater",
            "Grease",
            "Hypnotism",
            "Light",
            "Magic Missile",
            "Minor Sphere of Perturbation",
            "Phantasmal Fireball",
            "Shocking Grasp",
            "Sleep",
            "Spook",
            "Taunt"
        )
        val gmgDefensiveSpells = listOf(
            "Affect Normal Fires",
            "Alarm",
            "Armor",
            "Audible Glamer",
            "Aura of Innocence",
            "Change Self",
            "Dancing Lights",
            "Faerie Phantoms",
            "Feather Fall",
            "Flutter Soft",
            "Gaze Reflection",
            "Hold Portal",
            "Jump",
            "Magic Shield",
            "Phantom Armor",
            "Protection from Evil",
            "Shift Blame",
            "Smell Immunity",
            "Spider Climb",
            "Wall of Fog"
        )
        val gmgMiscSpells = listOf(
            "Bash Door",
            "Comprehend Languages",
            "Conjure Mount",
            "Detect Magic",
            "Detect Undead",
            "Erase",
            "Find Familiar",
            "Fog Vision",
            "Gabal's Magic Aura",
            "Melt",
            "Mend",
            "Merge Coin Pile",
            "Message",
            "Phantasmal Force",
            "Pool Gold",
            "Precipitation",
            "Run",
            "Throw Voice",
            "Unseen Servant",
            "Wizard Mark"
        )
        val ssgOffensiveSpells = listOf(
            "Befriend",
            "Burning Hands",
            "Charm Person",
            "Chill Touch",
            "Chromatic Orb",
            "Color Spray",
            "Enlarge",
            "Fireball, Barrage",
            "Fireball, Sidewinder Factor 1",
            "Firewater",
            "Grease",
            "Hypnotism",
            "Icy Blast",
            "Jack Punch",
            "Light",
            "Magic Missile",
            "Magic Stone",
            "Minor Sphere of Perturbation",
            "Phantasmal Fireball",
            "Power Word: Cartwheel",
            "Power Word: Moon",
            "Power Word: Summersault",
            "Push",
            "Shocking Grasp",
            "Sleep",
            "Spook",
            "Taunt"
        )
        val ssgDefensiveSpells = listOf(
            "Affect Normal Fires",
            "Alarm",
            "Armor",
            "Audible Glamer",
            "Aura of Innocence",
            "Change Self",
            "Corpse Visage",
            "Dancing Lights",
            "Disable Hand",
            "Faerie Phantoms",
            "Feather Fall",
            "Flutter Soft",
            "Gaze Reflection",
            "Hold Portal",
            "Jump",
            "Magic Shield",
            "Phantom Armor",
            "Protection from Evil",
            "Protection from Sunburn",
            "Protective Amulet",
            "Remove Fear",
            "Resist Cold",
            "Resist Fire",
            "Shift Blame",
            "Smell Immunity",
            "Spider Climb",
            "Wall of Fog"
        )
        val ssgMiscSpells = listOf(
            "Animate Dead Animals",
            "Bash Door",
            "Comprehend Languages",
            "Conjure Mount",
            "Copy",
            "Detect Disease",
            "Detect Illusion",
            "Detect Magic",
            "Detect Phase",
            "Detect Undead",
            "Divining Rod",
            "Erase",
            "Find Familiar",
            "Fog Vision",
            "Gabal's Magic Aura",
            "Melt",
            "Mend",
            "Merge Coin Pile",
            "Message",
            "Phantasmal Force",
            "Pool Gold",
            "Precipitation",
            "Remove Thirst",
            "Run",
            "Throw Voice",
            "Unseen Servant",
            "Wizard Mark"
        )

        fun rollNestedReroll(type: Int, count: Int) {
            when (type){

                NESTED_OFFENSIVE_REROLL -> {

                    repeat(count){
                        when (Random.nextInt(1,99)){
                            in 88..96   -> offensiveRerolls ++

                            97          -> {
                                offensiveRerolls ++
                                miscRerolls ++
                            }

                            98          -> {
                                offensiveRerolls ++
                                defensiveRerolls ++
                            }

                            else        -> offensiveRolls ++
                        }
                    }
                }

                NESTED_DEFENSIVE_REROLL -> {

                    repeat(count){
                        when (Random.nextInt(1,99)){
                            in 82..92   -> defensiveRerolls ++

                            in 93..94   -> {
                                defensiveRerolls ++
                                miscRerolls ++
                            }

                            in 95..96   -> {
                                defensiveRerolls ++
                                offensiveRerolls ++
                            }

                            97          -> spellNameList.plus("GM Choice (Defensive)")

                            98          -> spellNameList.plus("Player Choice (Defensive)")

                            else        -> defensiveRolls ++
                        }
                    }
                }

                NESTED_MISC_REROLL -> {

                    repeat(count){
                        when (Random.nextInt(1,99)){
                            in 88..96   -> miscRerolls ++

                            97          -> {
                                miscRerolls ++
                                defensiveRerolls ++
                            }

                            98          -> {
                                miscRerolls ++
                                offensiveRerolls ++
                            }

                            else        -> miscRolls ++
                        }
                    }
                }
            }
        }

        //TODO implement function for try/catch of querying db for template. Just returns sample spell for now.
        fun tryTempFetch(spellName: String) : SpellTemplate {
            return SAMPLE_ARCANE_SPELL //TODO actually query DB using spellName as argument.
        }

        fun getSpellByName(name: String) : Spell {

            tempHolder = if ((name == "GM Choice (Defensive)") || ( name == "Player Choice (Defensive)")){
                // Return a custom "Choice" SpellTemplate
                SpellTemplate(0,name,1,
                    "Spellslinger's Guide to Wurld Domination", 6,
                    0, 1, "Abj", "", "", "", "See errata for updated Table 1A")
            } else {

                tryTempFetch(name)
            }

            return convertTemplateToSpell(tempHolder)
        }

        // First run of checks for rerolls/bonuses (if using SSG)
        if (useSSG){
            repeat(offensiveRolls) {

                when (Random.nextInt(1,101)){
                    in 88..96   -> {
                        offensiveRolls --
                        offensiveRerolls ++
                    }

                    97          -> {
                        offensiveRolls --
                        offensiveRerolls ++
                        defensiveRerolls ++
                    }

                    98          -> {
                        offensiveRolls --
                        offensiveRerolls ++
                        miscRerolls ++
                    }

                    99          -> {
                        offensiveRolls --
                        rollNestedReroll(NESTED_OFFENSIVE_REROLL,2)
                    }

                    100         -> {
                        offensiveRolls --
                        rollNestedReroll(NESTED_OFFENSIVE_REROLL,3)
                    }
                }
            }

            repeat(defensiveRolls) {

                when (Random.nextInt(1,101)){
                    in 82..92   -> {
                        defensiveRolls --
                        defensiveRerolls ++
                    }

                    in 93..94   -> {
                        defensiveRolls --
                        defensiveRerolls ++
                        miscRerolls ++
                    }

                    in 95..96   -> {
                        defensiveRolls --
                        defensiveRerolls ++
                        offensiveRerolls ++
                    }

                    97          -> {
                        defensiveRolls --
                        rollNestedReroll(NESTED_DEFENSIVE_REROLL,2)
                    }

                    98         -> {
                        defensiveRolls --
                        rollNestedReroll(NESTED_DEFENSIVE_REROLL,3)
                    }

                    99          -> {
                        defensiveRolls --
                        spellNameList.plus("GM Choice (Defensive)")
                    }

                    100         -> {
                        defensiveRolls --
                        spellNameList.plus("Player Choice (Defensive)")
                    }
                }
            }

            repeat(miscRolls) {

                when (Random.nextInt(1,101)){
                    in 88..96   -> {
                        miscRolls --
                        miscRerolls ++
                    }

                    97          -> {
                        miscRolls --
                        miscRerolls ++
                        defensiveRerolls ++
                    }

                    98          -> {
                        miscRolls --
                        miscRerolls ++
                        offensiveRerolls ++
                    }

                    99          -> {
                        miscRolls --
                        rollNestedReroll(NESTED_MISC_REROLL,2)
                    }

                    100         -> {
                        miscRolls --
                        rollNestedReroll(NESTED_MISC_REROLL,3)
                    }
                }
            }
        }

        // Reroll as indicated. Resolve all rerolls before spell determination.
        while ((offensiveRerolls > 0)||(defensiveRerolls > 0)||(miscRerolls > 0)) {

            while (offensiveRerolls > 0) {

                when (Random.nextInt(1,101)){

                    in 1..87    -> {
                        offensiveRolls ++
                        offensiveRerolls --
                    }

                    97          -> defensiveRerolls ++

                    98          -> miscRerolls ++

                    99          -> rollNestedReroll(NESTED_OFFENSIVE_REROLL,2)

                    100         -> rollNestedReroll(NESTED_OFFENSIVE_REROLL,3)
                }
            }

            while (defensiveRerolls > 0) {

                when (Random.nextInt(1,101)){
                    in 1..81   -> {
                        defensiveRolls ++
                        defensiveRerolls --
                    }

                    in 93..94   -> miscRerolls ++

                    in 95..96   -> offensiveRerolls ++

                    97          -> rollNestedReroll(NESTED_DEFENSIVE_REROLL,2)

                    98          -> rollNestedReroll(NESTED_DEFENSIVE_REROLL,3)

                    99          -> {
                        defensiveRerolls --
                        spellNameList.plus("GM Choice (Defensive)")
                    }

                    100         -> {
                        defensiveRerolls --
                        spellNameList.plus("Player Choice (Defensive)")
                    }
                }
            }

            while (miscRerolls > 0) {

                when (Random.nextInt(1,101)){

                    in 1..87    -> {
                        miscRolls ++
                        miscRerolls --
                    }

                    97          -> defensiveRerolls ++

                    98          -> offensiveRerolls ++

                    99          -> rollNestedReroll(NESTED_MISC_REROLL,2)

                    100         -> rollNestedReroll(NESTED_MISC_REROLL,3)
                }
            }

        }

        // Once all re-rolls are converted to rolls, roll spells.
        if (useSSG){
            repeat(offensiveRolls) { spellNameList.plus(ssgOffensiveSpells.random()) }
            repeat(defensiveRolls) { spellNameList.plus(ssgDefensiveSpells.random()) }
            repeat(miscRolls) { spellNameList.plus(ssgMiscSpells.random()) }
            //TODO implement specialist spell lists
        } else {
            repeat(offensiveRolls) { spellNameList.plus(gmgOffensiveSpells.random()) }
            repeat(defensiveRolls) { spellNameList.plus(gmgDefensiveSpells.random()) }
            repeat(miscRolls) { spellNameList.plus(gmgMiscSpells.random()) }
            //TODO implement specialist spell lists
        }

        // Convert names into spells
        spellNameList.forEach { spellList.add(getSpellByName(it)) }

        return spellList
    }

    //TODO Add function for generating ring of spell storing from SpecialItemOrder

    /* TODO finish implementing after first shipped build
    override fun createSpellBook(parentHoard: Int, _effectiveLevel: Int, _extraSpells: Int,
                        _specialistType: String, extraProperties: Int,
                        extraSpellMethod: SpCoGenMethod,
                        useSSG: Boolean,
                        allowRestricted: Boolean) : SpellCollection {

        var pageType = object {
            var featureName         = "Parchment"
            var costPerFourPages    = 3.0   // Given in
            var timePerFourPages    = 2.0   // Given in hours
            var pagesPerHalfPound   = 48
        }
        var inkType = object {
            var featureName         = "Standard Ink"
            var costPerFourPages    = 25.0
            var timePerFourPages    = 1.0
        }
        var bindingType = object {
            var featureName         = "Standard binding"
            var time                = 2.0
            var weight              = 0.5
        }
        var coverType = object {
            var featureName         = "Leather cover"
            var cost                = 20.0
            var time                = 2.0
            var weight : Pair<Double,Double> = 1.5 to 0.75
        }
        val enhancements = object{
            val pageEnhancements = mutableSetOf<Pair<String,Double>>()
            val inkEnhancements = mutableSetOf<Pair<String,Double>>()
            val bindingEnhancements = mutableSetOf<Pair<String,Double>>()
            val coverEnhancements = mutableSetOf<Pair<String,Double>>()
            val securityEnhancements =  mutableSetOf<Pair<String,Double>>()
            val carryingEnhancements = mutableSetOf<Pair<String,Double>>()
        }

        var pageCount   = 64
        var materialCost= 0.0
        var prodTime    = 0.0
        var totalWeight = 0.0

        val effectiveLevel = _effectiveLevel.coerceIn(1..20)
        val specialistType = if (Spell.mageSpecialistTags.contains(_specialistType)) {
            _specialistType
        } else ""
        var iconID = "book_silverbound"
        val nameBuilder = StringBuilder()

        val spells = arrayListOf<Spell>()
        val extraProperties = arrayListOf<Pair<String,Double>>()

        fun convertEnhancementsToList() { // TODO implement
            }
        fun getInitialGPValue() : Double {

            var gpTotal: Double = materialCost

            if (extraProperties.isNotEmpty()) {
                extraProperties.forEach { (_, gpValue) -> gpTotal += gpValue }
            }

            if (spells.isNotEmpty()) {
                spells.forEach {

                    gpTotal += if (it.spellLevel == 0) {
                        75.0
                    } else {
                        (300.0 * it.spellLevel)
                    }
                }
            }

            return gpTotal
        }
        fun getInitialXpValue() : Int {

            var xpTotal = 0

            if (spells.isNotEmpty()) { spells.forEach {

                xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel) }
            }

            return xpTotal
        }

        fun addRandomEnhancement() {


            when (Random.nextInt(1,7)){
                else-> {

                }
            }

        }

        //TODO Implement specialist-specific spellbooks.

        return SpellCollection(0,parentHoard,iconID,nameBuilder.toString(),
        SpCoType.BOOK,extraProperties.toList(),getInitialGPValue(),getInitialXpValue(),
        spells.toList(),"")
    } */

    override fun createTreasureMap(parentHoard: Int, sourceDesc: String, allowFalseMaps: Boolean): MagicItem {

        val notesLists=     LinkedHashMap<String,ArrayList<String>>()

        val nameBuilder=    StringBuilder("Treasure Map")

        val mNotes          : List<List<String>>

        val isRealMap: Boolean
        val distanceRoll= Random.nextInt(1,21)

        // Roll type of map
        when (Random.nextInt(if (allowFalseMaps) 1 else 2,11)) {

            1       -> {
                isRealMap = false
                notesLists["Map details"]?.plusAssign("False map " +
                        "(No treasure or already looted")
                nameBuilder.append(" (False)")
            }

            in 2..7 -> {
                isRealMap = true
                notesLists["Map details"]?.plusAssign("Map to monetary treasure " +
                        "(0% chance of art objects or magic items")
                nameBuilder.append(" (Monetary)")
            }

            in 8..9 -> {
                isRealMap = true
                notesLists["Map details"]?.plusAssign("Map to magical treasure " +
                        "(0% chance of coin)")
                nameBuilder.append(" (Magical)")
            }

            else    -> {
                isRealMap = true
                notesLists["Map details"]?.plusAssign("Map to combined treasure")
                nameBuilder.append(" (Combined)")
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
        if (isRealMap)
            notesLists["Map details"]?.plusAssign("Treasure present: " +
                    listOf("I","G","H","F","A","B","C","D","E","Z","A and Z","A and H").random())

        // Note item map replaced
        if (sourceDesc.isNotBlank()){
            notesLists["Map details"]?.plusAssign("This map replaced $sourceDesc")
        }

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

        return MagicItem(
            0,
            -1,
            parentHoard,
            "scroll_map",
            "Map",
            nameBuilder.toString(),
            "GameMaster's Guide",
            182,
            0,
            0.0,
            mapOf(
                "fighter" to true,
                "thief" to true,
                "cleric" to true,
                "magic-user" to true,
                "druid" to true),
            !isRealMap,
            "",
            convertMapToNestedLists(notesLists))
    }

    override fun createIounStones(parentHoard: Int, qty: Int): List<MagicItem> {

        val iounList = arrayListOf<MagicItem>()
        val currentSet = mutableSetOf<Int>()

        val itemCount = qty.coerceIn(1,10)
        var currentStone : Int
        var deadStones = 0

        // Roll which stones are present
        repeat(itemCount) {

            currentStone = Random.nextInt(1,21)

            // Convert to 'dead' stone if rolled or already present
            if ((currentSet.contains(currentStone))||(currentStone > 14))
                deadStones ++
            else currentSet.add(currentStone)
        }

        // Retrieve stones from database
        currentSet.sorted().forEach { indexAddend ->

            // Add to running list
            iounList.add(createMagicItemTuple(parentHoard,
                (FIRST_IOUN_STONE_KEY + (indexAddend - 1)),
                listOf("A14")).magicItem)
        }

        // Add dead stones
        if (deadStones > 0){

            val deadStoneItem = createMagicItemTuple(parentHoard,
                LAST_IOUN_STONE_KEY, listOf("A14")).magicItem

            repeat(deadStones) { iounList.add(deadStoneItem) }
        }

        // Return list of ioun stones
        return iounList
    }

    override fun createGutStones(parentHoard: Int, qty: Int): List<Gem> {

        val gutStoneList = arrayListOf<Gem>()

        repeat(qty.coerceAtLeast(1)){
            gutStoneList.add(createGem(parentHoard, GUT_STONE_KEY))
        }

        return gutStoneList
    }

    override fun convertTemplateToSpell(template:SpellTemplate, appendedNotes: List<String>): Spell {

        fun getDisciplineFromInt() : SpCoDiscipline {

            return when (template.type) {
                0   -> SpCoDiscipline.ARCANE
                1   -> SpCoDiscipline.DIVINE
                2   -> SpCoDiscipline.NATURAL
                else-> SpCoDiscipline.ALL_MAGIC
            }
        }

        return Spell(
            template.refId,
            template.name,
            getDisciplineFromInt(),
            template.level,
            template.source,
            template.page,
            template.schools.split("/"),
            template.spellSpheres.split("/"),
            template.subclass,
            template.restrictions.split("/"), //TODO - Maybe convert to readable strings with Spell companion object functions
            listOf(listOf(template.note),appendedNotes).flatten()
        )
    }

}