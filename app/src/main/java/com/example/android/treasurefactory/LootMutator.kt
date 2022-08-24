package com.example.android.treasurefactory

import com.example.android.treasurefactory.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random

class LootMutator {

    // TODO fun restoreOriginalValue(inputGem: Gem): Gem {}



    fun rollGemVariation(inputGem: Gem) : Gem {

        val timestamp = Calendar.getInstance().timeInMillis
        val gemModifierSum = inputGem.size + inputGem.quality
        val resultsBuilder = StringBuilder()
        val valueHistoryList = inputGem.valueHistory.toMutableList()
        val hasChanged : Boolean

        var currentVariation = inputGem.variation
        val newGPValue: Double
        var continueRolling = false
        var roll: Int
        var minAllowed = 1
        var maxAllowed = 12
        var multiplier = 1.0

        fun changeBaseValue(increment: Int) {

            currentVariation += increment

            // Require additional re-roll with new allowed range
            continueRolling = true

            if (increment > 0) maxAllowed = 8 else if (increment < 0) minAllowed = 2

            // Enforce variation limit
            if ( currentVariation < -5 ) { currentVariation = -5 }
            else if ( currentVariation > 7 ) { currentVariation = 7 }
        }

        // Roll for variation
        do {
            // Ensure sane values for allowed ranges
            if (minAllowed < 1) minAllowed = 1
            if (minAllowed > 12) minAllowed = 12
            if (maxAllowed > 12) maxAllowed = 12
            if (maxAllowed < minAllowed) maxAllowed = minAllowed

            // Roll d12
            roll = Random.nextInt(1,13)

            // Check for variation, re-roll if ignored result is rolled.
            if (roll in IntRange(minAllowed,maxAllowed)) {

                when (roll) {

                    1   -> { changeBaseValue(1) }

                    2   -> {

                        multiplier = 0.1 * ( Random.nextInt(1,11).toDouble() + 15.0 )
                        continueRolling = false
                    }

                    3   -> {

                        multiplier = 1.0 + ( 0.1 * Random.nextInt(1,7).toDouble() )
                        continueRolling = false
                    }

                    10  -> {

                        multiplier = 1.0 + ( 0.1 * Random.nextInt(1,7).toDouble() )
                        continueRolling = false
                    }

                    11  -> {

                        multiplier = -0.1 * ( Random.nextInt(1,11).toDouble() + 13.0 )
                        continueRolling = false
                    }

                    12  -> { changeBaseValue(-2) }

                    else -> {

                        continueRolling = false
                    }
                }

            } else continueRolling = true

        } while (continueRolling)

        hasChanged = (inputGem.variation != currentVariation)||(multiplier != 1.0)

        // Evaluate new GP value
        newGPValue = if (hasChanged) {
            convertGemValueToGP(
                inputGem.type + gemModifierSum + currentVariation
            ) * multiplier
        } else { inputGem.currentGPValue }

        // Add variation note to history list
        if (hasChanged) {

            if (inputGem.variation != currentVariation) {
                resultsBuilder.append("Base value level changed from " +
                        "${inputGem.type + gemModifierSum + inputGem.variation} to " +
                        "${inputGem.type + gemModifierSum + currentVariation}. ")
            }

            resultsBuilder.append("Market value of this ${inputGem.name} is " +
                    "now $newGPValue gp (${(multiplier*100.0).toInt()}% base value).")

        } else {

            resultsBuilder.append("${inputGem.name.capitalized()}'s market value of " +
                    "$newGPValue gp was not changed.")
        }

        valueHistoryList.add(Pair(timestamp,resultsBuilder.toString()))

        // Return result as new Gem()
        return inputGem.copy(
            variation = currentVariation,
            currentGPValue = newGPValue,
            valueHistory = valueHistoryList.toList())
    }

    companion object {

        fun convertGemValueToGP(input: Int): Double {

            val valueLevelToGPValue = mapOf(

                0 to 0.1,
                1 to 0.5,
                2 to 1.0,
                3 to 1.0,
                4 to 5.0,
                5 to 10.0,          // start of initial base values
                6 to 50.0,
                7 to 100.0,
                8 to 500.0,
                9 to 1000.0,
                10 to 5000.0,       // end of initial base values
                11 to 10000.0,
                12 to 25000.0,
                13 to 50000.0,
                14 to 100000.0,
                15 to 250000.0,
                16 to 500000.0,
                17 to 1000000.0
            )

            return when {

                (input < 0)     -> valueLevelToGPValue.getOrDefault(0,0.1)

                (input > 17)    -> valueLevelToGPValue.getOrDefault(17,1000000.0)

                else            -> valueLevelToGPValue.getOrDefault(input, 10.0)
            }
        }

        fun convertArtValueToGP(input:Int) : Double {

            val valueLevelToGPValue = mapOf(

                -19 to 1.0,
                -18 to 10.0,
                -17 to 20.0,
                -16 to 30.0,
                -15 to 40.0,
                -14 to 50.0,
                -13 to 60.0,
                -12 to 70.0,
                -11 to 85.0,
                -10 to 100.0,
                -9 to 125.0,
                -8 to 150.0,
                -7 to 200.0,
                -6 to 250.0,
                -5 to 325.0,
                -4 to 400.0,
                -3 to 500.0,
                -2 to 650.0,
                -1 to 800.0,
                0 to 1000.0,
                1 to 1250.0,
                2 to 1500.0,
                3 to 2000.0,
                4 to 2500.0,
                5 to 3000.0,
                6 to 4000.0,
                7 to 5000.0,
                8 to 6000.0,
                9 to 7500.0,
                10 to 10000.0,
                11 to 12500.0,
                12 to 15000.0,
                13 to 20000.0,
                14 to 25000.0,
                15 to 30000.0,
                16 to 40000.0,
                17 to 50000.0,
                18 to 60000.0,
                19 to 70000.0,
                20 to 85000.0,
                21 to 100000.0,
                22 to 125000.0,
                23 to 150000.0,
                24 to 200000.0,
                25 to 250000.0,
                26 to 300000.0,
                27 to 400000.0,
                28 to 500000.0,
                29 to 650000.0,
                30 to 800000.0,
                31 to 1000000.0
            )

            return when {

                (input < -19)     -> valueLevelToGPValue.getOrDefault(-19,1.0)

                (input > 31)    -> valueLevelToGPValue.getOrDefault(31,1000000.0)

                else            -> valueLevelToGPValue.getOrDefault(input, 1000.0)
            }
        }
    }

    fun selectHoardIconByValue(hoard: Hoard, hoardItems: HoardUniqueItemBundle): String {

        var hoardIconString = "container_chest"

        // Get most valuable icon for each item category

        fun getItemIconMap() : Map<Any?,Pair<Double,Int>> = runBlocking {

            val deferredGemInfo = async { getMostValuableGemInfo(hoardItems.hoardGems) }
            val deferredArtInfo = async { getMostValuableArtObjectInfo(hoardItems.hoardArt) }
            val deferredItemInfo = async { getMostValuableMagicItemInfo(hoardItems.hoardItems) }
            val deferredSpellInfo = async {
                getMostValuableSpellCollectionInfo(hoardItems.hoardSpellCollections) }

            return@runBlocking mapOf(
                deferredGemInfo.await(),
                deferredArtInfo.await(),
                deferredItemInfo.await(),
                deferredSpellInfo.await()
            )
        }

        // TODO left off here. Determine the actual necessary info and criteria for selecting an
        //  icon and modify getItemIconMap() and return type accordingly. Decide what cutoffs are
        //  necessary for coinage and assorted hoard icons and implement. After that, add this
        //  functionality to hoard Generator and merge functions.

        // If no category is valuable enough,

        return hoardIconString
    }

    fun getCoinPileIcon(hoard: Hoard) {
        //TODO implement
    }

    /**
     * Takes a list of Gems and returns relevant information for setting a hoard icon from
     * it.
     *
     * @return filename string of most valuable gem, paired with the total gp value of the entire
     * pile paired with item count.
     */
    fun getMostValuableGemInfo(gemPile: List<Gem>): Pair<Gem?,Pair<Double,Int>> {

        var bestGem : Gem? = null
        val totalGemCount = gemPile.size
        val totalGemValue = gemPile.sumOf { it.currentGPValue }

        // Determine most valuable gem
        if (gemPile.isNotEmpty()) {
            bestGem = gemPile.sortedWith( compareByDescending<Gem> { it.currentGPValue }
                .thenByDescending { it.type }
                .thenBy {it.gemID})
                .first()
        }

        return bestGem to (totalGemValue to totalGemCount)
    }

    /**
     * Takes a list of Art Objects and returns relevant information for setting a hoard icon from
     * it.
     *
     * @return most valuable art object in list (null if empty), paired with the total gp value of
     * the entire pile paired with total item count.
     */
    fun getMostValuableArtObjectInfo(artPile: List<ArtObject>): Pair<ArtObject?,Pair<Double,Int>> {

        var bestArt : ArtObject? = null
        val totalArtCount = artPile.size
        val totalArtValue = artPile.sumOf { it.gpValue }

        // Determine most valuable art object
        if (artPile.isNotEmpty()) {
            bestArt = artPile.sortedWith( compareByDescending<ArtObject> { it.valueLevel }
                .thenByDescending { it.artType }
                .thenBy { it.artID })
                .first()
        }

        return bestArt to (totalArtValue to totalArtCount)
    }

    /**
     * Takes a list of Magic Items and returns relevant information for setting a hoard icon from
     * it.
     *
     * @return most valuable magic item in list (null if empty), paired with the total gp value of
     * the entire pile paired with total item count.
     */
    fun getMostValuableMagicItemInfo(itemPile: List<MagicItem>): Pair<MagicItem?,Pair<Double,Int>> {

        var bestItem : MagicItem? = null
        val totalItemCount = itemPile.size
        val totalItemValue = itemPile.sumOf { it.gpValue }

        // Determine most valuable art object
        if (itemPile.isNotEmpty()) {
            bestItem = itemPile.sortedWith( compareByDescending<MagicItem> { it.gpValue }
                .thenByDescending { it.xpValue }
                .thenByDescending { it.typeOfItem.ordinal.takeUnless { it > 20 } ?: -1}
                .thenBy { it.mItemID })
                .first()
        }

        return bestItem to (totalItemValue to totalItemCount)
    }

    /**
     * Takes a list of Spell Collections and returns relevant information for setting a hoard icon
     * from it.
     *
     * @return Most valuable spell collection in list (null if empty), paired with the total gp
     * value of the entire pile paired with total item count.
     */
    fun getMostValuableSpellCollectionInfo(spellPile: List<SpellCollection>):
            Pair<SpellCollection?,Pair<Double,Int>> {

        var bestSpellCollection : SpellCollection? = null
        val totalSpellCount = spellPile.size
        val totalSpellValue = spellPile.sumOf { it.gpValue }

        // Determine most valuable art object
        if (spellPile.isNotEmpty()) {
            bestSpellCollection = spellPile
                .sortedWith( compareByDescending<SpellCollection> { it.gpValue }
                .thenByDescending { it.spells.size }
                .thenBy { it.sCollectID })
                .first()
        }

        return bestSpellCollection to (totalSpellValue to totalSpellCount)
    }
}

//TODO move to Util class
fun String.capitalized(): String {
    return this.replaceFirstChar {

        if (it.isLowerCase()){

            it.titlecase(Locale.getDefault())

        } else it.toString()
    }
}
