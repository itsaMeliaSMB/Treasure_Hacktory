package com.example.android.treasurefactory.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.treasurefactory.LootGeneratorAsync
import com.example.android.treasurefactory.database.LetterCode
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository
import com.example.android.treasurefactory.ui.GenEditTextTag
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.set
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

const val MINIMUM_LETTER_QTY = 0
const val MAXIMUM_LETTER_QTY = 20

const val MAXIMUM_UNIQUE_QTY = 150
const val MAXIMUM_SPELLS_PER_SCROLL = 20
const val MAXIMUM_SPELL_COLLECTION_QTY = 75

const val MAXIMUM_HOARD_VALUE = 9999999999.99
const val MAXIMUM_COINAGE_AMOUNT = 999999999.99

class HoardGeneratorViewModel(private val repository: HMRepository): ViewModel() {

    //region << Values, variables, and containers >>

    // region ( Method-agnostic variables )
    var hoardName = ""

    /**
     * Index of displayed child view group and method to use for order generation.
     * 0 = Letter-code, 1 = Specific quantity.
     */
    var generationMethodPos = 0
        private set

    private var isRunningAsync = false

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)
    // endregion

    // region ( Letter code value containers )
    private val lairList = getCleanLairList()
    private val smallList = getCleanSmallList()

    val lairListLiveData = MutableLiveData(lairList.toList())
    val smallListLiveData = MutableLiveData(smallList.toList())

    val letterCodeHolderLiveData = MutableLiveData<LetterCode?>(null)

    val generatedHoardLiveData = MutableLiveData<Hoard?>(null)
    // endregion

    // region ( Specific Quantity value containers )
    var coinMin = 0.0
        private set
    var coinMax = 0.0
        private set
    var cpChecked = false
    var spChecked = false
    var epChecked = false
    var gpChecked = false
    var hspChecked = false
    var ppChecked = false

    var generatorOptions = GeneratorOptions()

    var gemQty = 0
        private set

    var artQty = 0
        private set

    var potionQty = 0
        private set
    var scrollQty = 0
        private set
    var armWepQty = 0
        private set
    var anyButQty = 0
        private set
    var anyMgcQty = 0
        private set

    var spCoQty = 0
        private set
    var spellLevelRange = IntRange(0,9)
    var spellsPerRange = IntRange(1,7)
    // endregion

    // endregion

    // region [ Setter functions ]
    fun setGeneratorMethodPos(newViewGroupIndex: Int){
        if (newViewGroupIndex in 0..1) {
            generationMethodPos = newViewGroupIndex
        }
    }

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    // region ( Letter update functions )
    fun updateLairEntry(position: Int, newValue : Int) {
        if (position in lairList.indices) {
            lairList[position] = lairList[position].first to newValue.coerceIn(MINIMUM_LETTER_QTY,
                MAXIMUM_LETTER_QTY)
            lairListLiveData.postValue(lairList.toList())
        }
    }

    fun updateSmallEntry(position: Int, newValue : Int) {
        if (position in smallList.indices) {
            smallList[position] = smallList[position].first to newValue.coerceIn(MINIMUM_LETTER_QTY,
                MAXIMUM_LETTER_QTY)
            smallListLiveData.postValue(smallList.toList())
        }
    }

    fun resetLairCount() {

        lairList.apply {
            clear()
            addAll(getCleanLairList())
        }
        lairListLiveData.postValue(lairList.toList())
    }

    fun resetSmallCount() {

        smallList.apply{
            clear()
            addAll(getCleanSmallList())
        }
        smallListLiveData.postValue(smallList.toList())
    }

    // endregion

    // region ( Specific update functions )
    fun resetSpecificQtyValues() {
        coinMin = 0.0
        coinMax = 0.0
        cpChecked = false
        spChecked = false
        epChecked = false
        gpChecked = false
        hspChecked = false
        ppChecked = false

        gemQty = 0

        artQty = 0
        potionQty = 0
        scrollQty = 0
        armWepQty = 0
        anyButQty = 0
        anyMgcQty = 0

        spCoQty = 0
        spellLevelRange = IntRange(0, 9)
        spellsPerRange = IntRange(1, MAXIMUM_SPELLS_PER_SCROLL)
    }

    /**
     * Sets value of GeneratorViewModel field from data passed from an <<<LISTENER>>>
     * from GeneratorHoardFragment. Returns a string to be set as the calling view's error message
     * if it fails validation.
     */
    fun setValueFromEditText(sourceViewTag: GenEditTextTag, capturedString: String) : String? {

        var errorString : String? = null

        /**
         * Validate captured [String] as an [Int].
         *
         * @param floor Lowest allowed value. When set to a negative value, floor is ignored.
         * @param ceiling Highest allowed value. When set to a negative value, ceiling is ignored.
         */
        fun validateAsInt(floor: Int = -1, ceiling: Int = -1) : String? {

            val parsedValue = capturedString.trim().toIntOrNull()

            if (parsedValue != null) {

                when (sourceViewTag) {

                    GenEditTextTag.GEM_QTY  -> if (parsedValue != gemQty) {

                        gemQty = parsedValue
                    }

                    GenEditTextTag.ART_QTY  -> if (parsedValue != artQty) {

                        artQty = parsedValue
                    }

                    GenEditTextTag.POTION_QTY   -> if (parsedValue != potionQty) {

                        potionQty = parsedValue
                    }

                    GenEditTextTag.SCROLL_QTY   -> if (parsedValue != scrollQty) {

                        scrollQty = parsedValue
                    }

                    GenEditTextTag.WEAPON_ARMOR_QTY -> if (parsedValue != armWepQty) {

                        armWepQty = parsedValue
                    }

                    GenEditTextTag.ANY_BUT_WEAP_QTY -> if (parsedValue != anyButQty) {

                        anyButQty = parsedValue
                    }

                    GenEditTextTag.ANY_MAGIC_QTY -> if (parsedValue != anyMgcQty) {

                        anyMgcQty = parsedValue
                    }

                    GenEditTextTag.SPELL_CO_QTY -> if (parsedValue != spCoQty) {

                        spCoQty = parsedValue
                    }

                    else -> {}
                }

                errorString = when {

                    (floor >= 0) && (parsedValue < floor)    -> "Input too low."
                    (ceiling >= 0) && (parsedValue > ceiling)-> "Input too high."
                    else -> errorString
                }

            }

            return errorString
        }

        fun validateAsDouble() {

            val parsedValue = capturedString.trim().toDoubleOrNull()

            if (parsedValue != null) {

                when (sourceViewTag) {

                    GenEditTextTag.COIN_MINIMUM -> {

                        if (coinMin != parsedValue) {
                            coinMin = parsedValue
                        }

                        if (parsedValue !in 0.0..MAXIMUM_COINAGE_AMOUNT) {

                            errorString = "Amount out of bounds"
                        }
                    }

                    GenEditTextTag.COIN_MAXIMUM -> {

                        if (coinMax != parsedValue) {

                            coinMax = parsedValue
                        }

                        if (parsedValue !in 0.0..MAXIMUM_COINAGE_AMOUNT) {

                            errorString = "Amount out of bounds."
                        }

                        validateCoinageMaximum().also {
                            if (!(it.isNullOrBlank())) {
                                errorString = it
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        when (sourceViewTag){

            GenEditTextTag.HOARD_NAME       -> hoardName = capturedString
            GenEditTextTag.COIN_MINIMUM     -> validateAsDouble()
            GenEditTextTag.COIN_MAXIMUM     -> validateAsDouble()
            GenEditTextTag.GEM_QTY          -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.ART_QTY          -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.POTION_QTY       -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.SCROLL_QTY       -> validateAsInt(0, MAXIMUM_SPELL_COLLECTION_QTY)
            GenEditTextTag.WEAPON_ARMOR_QTY -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.ANY_BUT_WEAP_QTY -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.ANY_MAGIC_QTY    -> validateAsInt(0, MAXIMUM_UNIQUE_QTY)
            GenEditTextTag.SPELL_CO_QTY     -> validateAsInt(0, MAXIMUM_SPELL_COLLECTION_QTY)
        }

        return errorString
    }
    // endregion
    // endregion

    // region [ Validation functions ]
    fun validateCoinageMaximum() : String? {

        return when {
            (coinMax < coinMin) -> "Cannot be lower than minimum"
            (coinMax !in 0.0..MAXIMUM_COINAGE_AMOUNT) -> "Value out of bounds"
            else -> null
        }
    }

    fun validateLetterCodeValues() : Boolean {

        return (listOf(lairList, smallList).flatten().indexOfFirst { entry -> entry.second > 0 }) != -1
    }
    // endregion

    private suspend fun compileLetterHoardOrder(): HoardOrder{

        var generatedCp = 0
        var generatedSp = 0
        var generatedEp = 0
        var generatedGp = 0
        var generatedHsp = 0
        var generatedPp = 0
        var generatedGems = 0
        var generatedArt = 0
        var generatedPotions = 0
        var generatedScrolls = 0
        var generatedArmWeps = 0
        var generatedNonWeps = 0
        var generatedAnyMagic = 0
        var generatedMaps = 0

        val compString = StringBuilder()

        // Compile iterable key-value pairs of what codes to query

        val entriesToRoll = listOf(lairList,smallList).flatten().filter { it.second > 0 }

        // Iterate through each key

        entriesToRoll.forEach { (letterKey, orderQty) ->

            val letterCode = try {
                repository.getLetterCodeOnce(letterKey)
            } catch(e: Exception){
                null
            }

            if (letterCode != null) {

                repeat(orderQty) {
                    if (letterCode.cpChance != 0 && Random.nextInt(101) <= letterCode.cpChance) {
                        generatedCp += Random.nextInt(letterCode.cpMin, letterCode.cpMax + 1)
                    }
                    if (letterCode.spChance != 0 && Random.nextInt(101) <= letterCode.spChance) {
                        generatedSp += Random.nextInt(letterCode.spMin, letterCode.spMax + 1)
                    }
                    if (letterCode.epChance != 0 && Random.nextInt(101) <= letterCode.epChance) {
                        generatedEp += Random.nextInt(letterCode.epMin, letterCode.epMax + 1)
                    }
                    if (letterCode.gpChance != 0 && Random.nextInt(101) <= letterCode.gpChance) {
                        generatedGp += Random.nextInt(letterCode.gpMin, letterCode.gpMax + 1)
                    }
                    if (letterCode.hspChance != 0 && Random.nextInt(101) <= letterCode.hspChance) {
                        generatedHsp += Random.nextInt(letterCode.spMin, letterCode.spMax + 1)
                    }
                    if (letterCode.ppChance != 0 && Random.nextInt(101) <= letterCode.ppChance) {
                        generatedPp += Random.nextInt(letterCode.ppMin, letterCode.ppMax + 1)
                    }
                    if (letterCode.gemChance != 0 && Random.nextInt(101) <= letterCode.gemChance) {
                        generatedGems += Random.nextInt(letterCode.gemMin, letterCode.gemMax + 1)
                    }
                    if (letterCode.artChance != 0 && Random.nextInt(101) <= letterCode.artChance) {
                        generatedArt += Random.nextInt(letterCode.artMin, letterCode.artMax + 1)
                    }

                    if (letterCode.anyChance > 0){

                        when {
                            letterCode.potionChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.anyChance) {
                                    generatedPotions += letterCode.potionMin
                                    generatedAnyMagic += letterCode.anyMin
                                }
                            }
                            letterCode.scrollChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.anyChance) {
                                    generatedScrolls += letterCode.scrollMin
                                    generatedAnyMagic += letterCode.anyMin
                                }
                            }
                            else    -> {
                                if (Random.nextInt(101) <= letterCode.anyChance) {
                                    generatedAnyMagic += letterCode.anyMin
                                }
                            }
                        }

                    } else {

                        when {
                            letterCode.potionChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.potionChance) {
                                    generatedPotions += Random.nextInt(letterCode.potionMin,
                                        letterCode.potionMax + 1)
                                }
                            }
                            letterCode.scrollChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.scrollChance) {
                                    generatedScrolls += Random.nextInt(letterCode.scrollMin,
                                        letterCode.scrollMax + 1)
                                }
                            }
                            letterCode.weaponChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.weaponChance) {
                                    generatedArmWeps += letterCode.weaponMin
                                }
                            }
                            letterCode.noWeaponChance > 0 -> {
                                if (Random.nextInt(101) <= letterCode.noWeaponChance) {
                                    generatedNonWeps += letterCode.noWeaponMin
                                }
                            }
                        }
                    }

                    if (Random.nextInt(1,101) <= generatorOptions.mapBase.coerceIn(0..100)) {
                        generatedMaps ++ }

                }
                compString.append("$letterKey x$orderQty,")
            }
        }

        // Compile results of rolls and hoard identifiers as hoard order

        return HoardOrder(
            hoardName,
            creationDescription = "Generated by letter code method.\n" +
                    "Initial composition: " + compString.toString().removeSuffix(", ") +
                    if (generatorOptions.mapBase > 0)
                        "\n(${generatorOptions.mapBase}% chance per entry of treasure map)" else "",
            copperPieces = generatedCp,
            silverPieces = generatedSp,
            electrumPieces = generatedEp,
            goldPieces = generatedGp,
            hardSilverPieces = generatedHsp,
            platinumPieces = generatedPp,
            gems = generatedGems,
            artObjects = generatedArt,
            potions = generatedPotions,
            scrolls = generatedScrolls,
            armorOrWeapons = generatedArmWeps,
            anyButWeapons = generatedNonWeps,
            anyMagicItems = generatedAnyMagic,
            baseMaps = generatedMaps,
            allowFalseMaps = generatorOptions.falseMapsOK,
            genParams = OrderParams(
                GemRestrictions(
                    generatorOptions.gemMin, generatorOptions.gemMax),
                ArtRestrictions(
                    _minLvl = generatorOptions.artMin,
                    _maxLvl = generatorOptions.artMax,
                    paperMapChance = generatorOptions.mapPaper),
                MagicItemRestrictions(
                    spellScrollEnabled = generatorOptions.spellOk,
                    nonScrollEnabled = generatorOptions.utilityOk,
                    scrollMapChance = generatorOptions.mapScroll,
                    allowedTables = generatorOptions.allowedMagic,
                    allowCursedItems = generatorOptions.cursedOk,
                    allowIntWeapons = generatorOptions.intelOk,
                    spellCoRestrictions = SpellCoRestrictions(
                        spellLevelRange.first,
                        spellLevelRange.last,
                        when(generatorOptions.spellDisciplinePos){
                            0   -> AllowedDisciplines(true,false,false)
                            1   -> AllowedDisciplines(false,true,false)
                            2   -> AllowedDisciplines(false,false,true)
                            else-> AllowedDisciplines(true,true,false) },
                        spellsPerRange,
                        generatorOptions.allowedSources,
                        generatorOptions.restrictedOk,
                        generatorOptions.spellReroll,
                        generatorOptions.cursedOk,
                        generatorOptions.spellCurses,
                        generatorOptions.spellMethod
                    )
                )
            )
        )
    }

    // region [ Order compilation functions ]

    private fun compileSpecificQtyHoardOrder() : HoardOrder {

        val newOrder : HoardOrder

        val newHoardName = hoardName

        // Roll coinage values
        val coinDenomsAsSet = mutableSetOf<Pair<Double,String>>()

        if (cpChecked) coinDenomsAsSet.add(0.01 to "cp")
        if (spChecked) coinDenomsAsSet.add(0.1 to "sp")
        if (epChecked) coinDenomsAsSet.add(0.5 to "ep")
        if (gpChecked) coinDenomsAsSet.add(1.0 to "cp")
        if (hspChecked) coinDenomsAsSet.add(2.0 to "hsp")
        if (ppChecked) coinDenomsAsSet.add(5.0 to "pp")

        val coinPileMap = getRandomCoinDistribution(coinMin,coinMax,coinDenomsAsSet.toSet())

        // Compile order parameters
        val newParams = OrderParams(
            GemRestrictions(
                generatorOptions.gemMin, generatorOptions.gemMax),
            ArtRestrictions(
                _minLvl = generatorOptions.artMin,
                _maxLvl = generatorOptions.artMax,
                paperMapChance = generatorOptions.mapPaper),
            MagicItemRestrictions(
                spellScrollEnabled = generatorOptions.spellOk,
                nonScrollEnabled = generatorOptions.utilityOk,
                scrollMapChance = generatorOptions.mapScroll,
                allowedTables = generatorOptions.allowedMagic,
                allowCursedItems = generatorOptions.cursedOk,
                allowIntWeapons = generatorOptions.intelOk,
                spellCoRestrictions = SpellCoRestrictions(
                    spellLevelRange.first,
                    spellLevelRange.last,
                    when(generatorOptions.spellDisciplinePos){
                        0   -> AllowedDisciplines(true,false,false)
                        1   -> AllowedDisciplines(true,true,false)
                        2   -> AllowedDisciplines(false,false,true)
                        else-> AllowedDisciplines(true,true,false) },
                    spellsPerRange,
                    generatorOptions.allowedSources,
                    generatorOptions.restrictedOk,
                    generatorOptions.spellReroll,
                    generatorOptions.cursedOk,
                    generatorOptions.spellCurses,
                    generatorOptions.spellMethod
                )
            )
        )

        newOrder = HoardOrder(
            hoardName = newHoardName,
            creationDescription = "User-specified loot quantity." +
                    if (generatorOptions.mapBase > 0)
                        "\n(${generatorOptions.mapBase}% chance per entry of treasure map)" else "",
            copperPieces = coinPileMap.getOrDefault("cp",0),
            silverPieces = coinPileMap.getOrDefault("sp",0),
            electrumPieces = coinPileMap.getOrDefault("ep",0),
            goldPieces = coinPileMap.getOrDefault("gp",0),
            hardSilverPieces = coinPileMap.getOrDefault("hsp",0),
            platinumPieces = coinPileMap.getOrDefault("pp",0),
            gems = gemQty,
            artObjects = artQty,
            potions = potionQty,
            scrolls = scrollQty,
            armorOrWeapons = armWepQty,
            anyButWeapons = anyButQty,
            anyMagicItems = anyMgcQty,
            extraSpellCols = spCoQty,
            baseMaps = if ((Random.nextInt(1,101) <=
                        generatorOptions.mapBase.coerceIn(0..100))) 1 else 0,
            allowFalseMaps = generatorOptions.falseMapsOK,
            genParams = newParams
        )

        return newOrder
    }
    // endregion

    // region [ Hoard generation functions ]

    fun generateHoard(isLetterCodeMethod: Boolean, appVersion: Int) {

        //https://developer.android.com/kotlin/coroutines

        viewModelScope.launch {

            setRunningAsync(true)

            val hoardOrder = if (isLetterCodeMethod) {

                compileLetterHoardOrder()

            } else {

                compileSpecificQtyHoardOrder()
            }

            val lootGenerator = LootGeneratorAsync(repository)

            val newHoardId = lootGenerator.createHoardFromOrder(hoardOrder, appVersion)

            setRunningAsync(false)

            generatedHoardLiveData.postValue(repository.getHoardOnce(newHoardId))
        }
    }
    // endregion

    // region [ Helper functions ]
    fun fetchLetterCode(letterKey: String) {

        viewModelScope.launch{
            isRunningAsync = true

            letterCodeHolderLiveData.postValue(repository.getLetterCodeOnce(letterKey))

            isRunningAsync = false
        }
    }

    private fun getCleanLairList() : MutableList<Pair<String,Int>> {

        return mutableListOf(
            "A" to 0, "B" to 0, "C" to 0, "D" to 0, "E" to 0, "F" to 0, "G" to 0, "H" to 0, "I" to 0
        )
    }

    private fun getCleanSmallList() : MutableList<Pair<String,Int>> {

        return mutableListOf(
            "J" to 0, "K" to 0, "L" to 0, "M" to 0, "N" to 0, "O" to 0, "P" to 0, "Q" to 0,
            "R" to 0, "S" to 0, "T" to 0, "U" to 0, "V" to 0, "W" to 0, "X" to 0, "Y" to 0, "Z" to 0
        )
    }

    fun getRandomCoinDistribution(_minimum: Double, _maximum: Double,
                                          allowedDenoms: Set<Pair<Double,String>>) : Map<String,Int> {

        /** Returns a mutable list of gpVale/name pairs from [allowedDenoms], starting with lowest value */
        fun getSortedDenominations() : MutableList<Pair<Double,String>> {
            return allowedDenoms.filter { it.first in 0.01..5.00 }
                .sortedBy{it.first}
                .map { (gpValue, name) -> gpValue.roundToTwoDecimal() to name.lowercase(Locale.getDefault()) }
                .toMutableList()
        }

        /** Returns the minimum necessary remainder for all remaining coin types */
        fun MutableList<Pair<Double,String>>.getMinimumRemainder() : Double {

            var sum = 0.0

            this.forEach { (gpValue, _) -> sum += gpValue }

            return sum.coerceAtLeast(0.0).roundToTwoDecimal()
        }

        val newCoinageMap = mutableMapOf<String,Int>()
        val sortedDenomList = getSortedDenominations()
        val minimumTotal: Double
        val maximumTotal: Double
        val targetGpTotal: Double
        var runningGpTotal = 0.0

        // region [ Determine range of total value of coinage to generate ]
        if ((sortedDenomList.isNotEmpty())&&(_maximum > 0.0)){
            minimumTotal = _minimum.coerceIn(sortedDenomList.getMinimumRemainder(),10000000.00)
            maximumTotal = if (_maximum <= minimumTotal) _minimum else _maximum.coerceAtMost(10000000.00)
        } else {
            minimumTotal = 0.0
            maximumTotal = 0.0
        }

        targetGpTotal = if (minimumTotal == maximumTotal) {
            minimumTotal
        } else {
            Random.nextDouble(minimumTotal,maximumTotal + 0.01).roundToTwoDecimal()
        }
        // endregion

        // region [ Roll coinage  ]
        if (targetGpTotal != 0.0) {

            // Roll first wave of denomination amounts
            while ((sortedDenomList.isNotEmpty())&&(runningGpTotal < targetGpTotal)){

                val pulledGPValue = sortedDenomList.first().first
                val pulledCoinName= sortedDenomList.first().second

                // Remove the pulled entry from the collection.
                sortedDenomList.removeAt(0)

                // If there are denominations left to generate, make sure there is enough to generate at least one of each.
                val requiredLeftover = if (sortedDenomList.isNotEmpty()) sortedDenomList.getMinimumRemainder() else 0.0

                val remainingPool = targetGpTotal - requiredLeftover - runningGpTotal

                /** The ceiling of this denomiation that can be generated in this step.*/
                val potentialCoinsCeiling = floor(remainingPool / pulledGPValue).toInt() + 1

                // Pull a random number of coins from the permitted range
                val coinsGenerated = Random.nextInt(1,potentialCoinsCeiling)

                // Add results
                newCoinageMap.plusAssign(pulledCoinName to coinsGenerated)
                runningGpTotal += (coinsGenerated * pulledGPValue)
            }

            // Backfill remaining value deficit greedily
            if (runningGpTotal < targetGpTotal){
                getSortedDenominations().reversed().forEach { (gpValue, coinName) ->
                    val totalToBackfill = targetGpTotal - runningGpTotal
                    if (totalToBackfill / gpValue >= 1.0) {
                        val backfillCoins = floor(totalToBackfill/gpValue).toInt()

                        newCoinageMap[coinName] = backfillCoins + newCoinageMap.getOrPut(coinName) { 0 }
                        runningGpTotal += (backfillCoins * gpValue)
                    }
                }
            }
        }

        return newCoinageMap
    }

    fun Double.roundToTwoDecimal():Double = (this * 100.00).roundToInt() / 100.00

    // endregion
}

class HoardGeneratorViewModelFactory(private val hmRepository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardGeneratorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardGeneratorViewModel(hmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}