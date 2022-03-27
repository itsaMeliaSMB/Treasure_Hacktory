package com.example.android.treasurefactory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.android.treasurefactory.model.LetterEntry
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.set
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

const val FIRST_SMALL_TREASURE_TYPE = "J"

const val MINIMUM_LETTER_QTY = 0
const val MAXIMUM_LETTER_QTY = 20

class HoardGeneratorViewModel(): ViewModel() {

    val oddsTable = sortedMapOf(
        // Lair treasure types
        "A" to arrayOf(
            intArrayOf(25, 1000, 3000),
            intArrayOf(30, 200, 2000),
            intArrayOf(35, 500, 3000),
            intArrayOf(40, 1000, 6000),
            intArrayOf(35, 500, 3000),
            intArrayOf(35, 300, 1800),
            intArrayOf(60, 10, 40),
            intArrayOf(50, 2, 12),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(30, 3, 3)
        ),
        "B" to arrayOf(
            intArrayOf(50, 1000, 6000),
            intArrayOf(25, 1000, 3000),
            intArrayOf(25, 300, 1800),
            intArrayOf(25, 200, 2000),
            intArrayOf(25, 150, 1500),
            intArrayOf(25, 100, 1000),
            intArrayOf(30, 1, 8),
            intArrayOf(20, 1, 4),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(10, 1, 1),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "C" to arrayOf(
            intArrayOf(20, 1000, 10000),
            intArrayOf(30, 1000, 6000),
            intArrayOf(40, 1000, 3000),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(10, 100, 600),
            intArrayOf(25, 1, 6),
            intArrayOf(20, 1, 3),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(10, 2, 2)
        ),
        "D" to arrayOf(
            intArrayOf(10, 1000, 6000),
            intArrayOf(15, 1000, 10000),
            intArrayOf(25, 1000, 12000),
            intArrayOf(50, 1000, 3000),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 100, 600),
            intArrayOf(30, 1, 10),
            intArrayOf(25, 1, 6),
            intArrayOf(15, 1, 1),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 2, 2)
        ),
        "E" to arrayOf(
            intArrayOf(5, 1000, 6000),
            intArrayOf(25, 1000, 10000),
            intArrayOf(45, 1000, 12000),
            intArrayOf(25, 1000, 4000),
            intArrayOf(15, 100, 1200),
            intArrayOf(25, 300, 1800),
            intArrayOf(15, 1, 12),
            intArrayOf(10, 1, 6),
            intArrayOf(0, 0, 0),
            intArrayOf(25, 1, 1),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(25, 3, 3)
        ),
        "F" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(10, 3000, 18000),
            intArrayOf(25, 2000, 12000),
            intArrayOf(40, 1000, 6000),
            intArrayOf(30, 500, 5000),
            intArrayOf(15, 1000, 4000),
            intArrayOf(20, 2, 20),
            intArrayOf(10, 1, 8),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(30, 5, 5),
            intArrayOf(0, 0, 0)
        ),
        "G" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 3000, 24000),
            intArrayOf(50, 2000, 20000),
            intArrayOf(50, 1500, 15000),
            intArrayOf(50, 1000, 10000),
            intArrayOf(30, 3, 18),
            intArrayOf(25, 1, 6),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(35, 5, 5)
        ),
        "H" to arrayOf(
            intArrayOf(25, 3000, 18000),
            intArrayOf(35, 2000, 20000),
            intArrayOf(45, 2000, 20000),
            intArrayOf(55, 2000, 20000),
            intArrayOf(45, 2000, 20000),
            intArrayOf(35, 1000, 8000),
            intArrayOf(50, 3, 30),
            intArrayOf(50, 2, 20),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 6, 6)
        ),
        "I" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 100, 400),
            intArrayOf(30, 100, 600),
            intArrayOf(55, 2, 12),
            intArrayOf(50, 2, 8),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(15, 1, 1)
        ),
        // Individual and small lair treasure types
        "J" to arrayOf(
            intArrayOf(100, 3, 24),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "K" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(100, 3, 18),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "L" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 3, 18),
            intArrayOf(100, 2, 12),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "M" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 3, 12),
            intArrayOf(100, 2, 8),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "N" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 1, 6),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "O" to arrayOf(
            intArrayOf(100, 10, 40),
            intArrayOf(100, 10, 30),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "P" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(100, 10, 60),
            intArrayOf(100, 3, 30),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 1, 20),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "Q" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 1, 4),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "R" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 2, 20),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 10, 60),
            intArrayOf(100, 2, 8),
            intArrayOf(100, 1, 3),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "S" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 1, 8),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "T" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 1, 4),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "U" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(90, 2, 16),
            intArrayOf(80, 1, 6),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(70, 1, 1)
        ),
        "V" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 2, 2)
        ),
        "W" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 4, 24),
            intArrayOf(100, 5, 30),
            intArrayOf(100, 2, 16),
            intArrayOf(100, 1, 8),
            intArrayOf(60, 2, 16),
            intArrayOf(50, 1, 8),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(60, 2, 2)
        ),
        "X" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 2, 2),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "Y" to arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(100, 200, 1200),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        "Z" to arrayOf(
            intArrayOf(100, 100, 300),
            intArrayOf(100, 100, 400),
            intArrayOf(100, 100, 500),
            intArrayOf(100, 100, 600),
            intArrayOf(100, 100, 500),
            intArrayOf(100, 100, 500),
            intArrayOf(55, 1, 6),
            intArrayOf(50, 2, 12),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(50, 3, 3)
        )
    )

    private val treasureLabels = listOf<String>(
        "copper piece(s)",
        "silver piece(s)",
        "electrum piece(s)",
        "gold piece(s)",
        "hard silver piece(s)",
        "platinum piece(s)",
        "gem(s)",
        "art object(s)",
        "potion(s)/oil(s)",
        "scroll(s)",
        "magic weapon(s)/armor",
        "non-weapon magic item(s)",
        "magic item(s)")

    /**
     * Index of displayed child view group and method to use for order generation.
     * 0 = Letter-code, 1 = Specific quantity.
     */
    private var generationMethodPos = 0

    val lairList = getLetterArrayList(true)
    val smallList = getLetterArrayList(false)

    /*
    Pending refactor for live data before safety commit.
    https://www.rockandnull.com/jetpack-viewmodel-initialization/
    https://medium.com/@fluxtah/two-ways-to-keep-livedata-t-immutable-and-mutable-only-from-a-single-source-a4a1dcdc0ef

    val _lairListLiveData = MutableLiveData<ArrayList<LetterEntry>>()
    val lairListLiveData: LiveData<ArrayList<LetterEntry>> = liveData {
        emit(getLetterArrayList(true))
    }
    val _smallListLiveData = MutableLiveData<ArrayList<LetterEntry>>()
    val smallListLiveData: LiveData<ArrayList<LetterEntry>>
        get() = _smallListLiveData


    init {
        viewModelScope.launch {
            _lairListLiveData.value = getLetterArrayList(true)
            _smallListLiveData.value = getLetterArrayList(false)
        }
    }
    */

    val coinageValues = object {
        var minimum = 0.0
        var maximum = 0.0
        var cpChecked = false
        var spChecked = false
        var epChecked = false
        var gpChecked = false
        var hspChecked = false
        var ppChecked = false
        fun reset(){
            minimum = 0.0
            maximum = 0.0
            cpChecked = false
            spChecked = false
            epChecked = false
            gpChecked = false
            hspChecked = false
            ppChecked = false
        }
    }
    val gemValues = object {
        var quantity = 0
        var minimumPos = 0
        var maximumPos = 0
        fun reset(){
            quantity = 0
            minimumPos = 0
            maximumPos = 0
        }
    }
    val artValues = object {
        var quantity = 0
        var minimumPos = 0
        var maximumPos = 0
        var mapAllowed = false
        fun reset() {
            quantity = 0
            minimumPos = 0
            maximumPos = 0
            mapAllowed = false
        }
    }
    val magicItemValues = object {
        var potionQty = 0
        var scrollQty = 0
        var armWepQty = 0
        var anyButQty = 0
        var anyQty = 0
        var spellChecked = false
        var nonSpChecked = false
        var treMapChecked = false
        var intWepChecked = false
        var cursedChecked = true
        var relicsChecked = true
        fun reset(){
            potionQty = 0
            scrollQty = 0
            armWepQty = 0
            anyButQty = 0
            anyQty = 0
            spellChecked = false
            nonSpChecked = false
            treMapChecked = false
            intWepChecked = false
            cursedChecked = true
            relicsChecked = true
        }
    }
    val spellCoValues = object {
        var quantity = 0
        var maxSpells = 1
        var disciplinePos = 0
        var genMethodPos = 0
        var minimumPos = 0
        var maximumPos = 9
        var splatChecked = false
        var hackJChecked = false
        var otherChecked = false
        var restrictChecked = false
        fun reset() {
            quantity = 0
            maxSpells = 1
            disciplinePos = 0
            genMethodPos = 0
            minimumPos = 0
            maximumPos = 9
            splatChecked = false
            hackJChecked = false
            otherChecked = false
            restrictChecked = false
        }
    }

    /**
     *  Returns a multi-line String for a given [oddsTable] value array.
     *
     *  @param rawLetterOdds Probability table of a given treasure's likelihood to appear. Decomposes into percentChance, minYield, and maxYield.
     */
    private fun returnOddsString(rawLetterOdds: Array<IntArray>) : String {

        val oddsList = mutableListOf<String>()

        rawLetterOdds.forEachIndexed { index, (percentChance,minYield,maxYield) ->

            val oddsLineBuilder = StringBuilder()

            if (percentChance != 0) {

                oddsLineBuilder.append("${percentChance}% odds of: ")
                    .append(if (minYield >= maxYield) {
                        minYield.toString()
                    } else {
                        "$minYield-$maxYield" })
                    .append(" ${treasureLabels[index]}")
            }

            if (oddsLineBuilder.isNotEmpty()) oddsList.add(oddsLineBuilder.toString())
        }

        var result: String = oddsList[0]

        if (oddsList.size > 1) {

            for( index in 1 until oddsList.size) {
                result += "\n${oddsList[index]}"
            }
        }

        return result
    }

    /** Returns an ArrayList of letter-coded treasure types. */
    private fun getLetterArrayList(isHeadMap: Boolean): ArrayList<LetterEntry> {

        val list = ArrayList<LetterEntry>()

        if (isHeadMap) {
            oddsTable.headMap(FIRST_SMALL_TREASURE_TYPE).forEach { (letterCode, rawLetterOdds) ->
                list.add(LetterEntry(letterCode, returnOddsString(rawLetterOdds), 0))
            }
        } else {
            oddsTable.tailMap(FIRST_SMALL_TREASURE_TYPE).forEach { (letterCode, rawLetterOdds) ->
                list.add(LetterEntry(letterCode, returnOddsString(rawLetterOdds), 0))
            }
        }

        return list
    }

    fun getGeneratorMethodPos() : Int = generationMethodPos

    fun setGeneratorMethodPos(newViewGroupIndex: Int){
        if (newViewGroupIndex in 0..1) {
            generationMethodPos = newViewGroupIndex
            Log.d("generatorViewModel","Value updated for generationMethodPos. [generationMethodPos = $generationMethodPos]")
        } else {
            Log.d("generatorViewModel","Out of bounds new value for generationMethodPos. [generationMethodPos = $generationMethodPos]")
        }
    }

    fun incrementLetterQty(position: Int, targetLairList: Boolean) {

        if (targetLairList) {
            try{
                lairList[position].quantity = (lairList[position].quantity + 1)
                    .coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)
            } catch (e: ArrayIndexOutOfBoundsException) {}
        } else {
            try{
                smallList[position].quantity = (smallList[position].quantity + 1)
                    .coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)
            } catch (e: ArrayIndexOutOfBoundsException) {}
        }
    }

    fun decrementLetterQty(position: Int, targetLairList: Boolean) {

        if (targetLairList) {
            try{
                lairList[position].quantity = (lairList[position].quantity - 1)
                    .coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)
            } catch (e: ArrayIndexOutOfBoundsException) {}
        } else {
            try{
                smallList[position].quantity = (smallList[position].quantity - 1)
                    .coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)
            } catch (e: ArrayIndexOutOfBoundsException) {}
        }
    }

    fun clearLairCount() { lairList.forEach { entry -> entry.quantity = 0 } }

    fun clearSmallCount() { smallList.forEach { entry -> entry.quantity = 0 } }

    /* Temporarily dummied-out order compilation functions.
    fun compileLetterCodeHoardOrder() : HoardOrder {

        // TODO refactor to new schema
        fun rollEntry(oddsArray: IntArray): Int {

            if (oddsArray[0] != 0) {

                // If number rolled is below target odds number,
                if (Random.nextInt(101) <= oddsArray[0]){

                    // Return random amount within range
                    return Random.nextInt(oddsArray[1],oddsArray[2] + 1)

                    // Otherwise, add nothing for this entry.
                } else return 0

            } else return 0
        }

        // Put values for every letter key
        val letterMap = mutableMapOf<String,Int>()

        // Put values for every letter key
        lairAdapter!!.getAdapterLetterEntries().forEach{ letterMap[it.letter] = it.quantity }
        smallAdapter!!.getAdapterLetterEntries().forEach{ letterMap[it.letter] = it.quantity}

        val initialDescription = "Initial composition: "
        val lettersStringBuffer = StringBuffer(initialDescription)

        val newOrder = HoardOrder()

        newOrder.hoardName = hoardTitleField.text.toString()

        // Roll for each non-empty entry TODO: move to non-UI thread
        letterMap.forEach { (key, value) ->
            if ((oddsTable.containsKey(key))&&(value > 0)) {

                // Roll for each type of treasure
                repeat (value) {
                    newOrder.copperPieces       += rollEntry(oddsTable[key]?.get(0)!!)
                    newOrder.silverPieces       += rollEntry(oddsTable[key]?.get(1)!!)
                    newOrder.electrumPieces     += rollEntry(oddsTable[key]?.get(2)!!)
                    newOrder.goldPieces         += rollEntry(oddsTable[key]?.get(3)!!)
                    newOrder.hardSilverPieces   += rollEntry(oddsTable[key]?.get(4)!!)
                    newOrder.platinumPieces     += rollEntry(oddsTable[key]?.get(5)!!)
                    newOrder.gems               += rollEntry(oddsTable[key]?.get(6)!!)
                    newOrder.artObjects         += rollEntry(oddsTable[key]?.get(7)!!)
                    newOrder.potions            += rollEntry(oddsTable[key]?.get(8)!!)
                    newOrder.scrolls            += rollEntry(oddsTable[key]?.get(9)!!)
                    newOrder.armorOrWeapons     += rollEntry(oddsTable[key]?.get(10)!!)
                    newOrder.anyButWeapons      += rollEntry(oddsTable[key]?.get(11)!!)
                    newOrder.anyMagicItems      += rollEntry(oddsTable[key]?.get(12)!!)
                }

                // Log letter type in the StringBuffer
                if (!(lettersStringBuffer.equals(initialDescription))) {
                    // Add a comma if not the first entry TODO fix this
                    lettersStringBuffer.append(", ")
                }
                // Add letter times quantity
                lettersStringBuffer.append("${key}x$value")
            }
        }

        // Update description log
        newOrder.creationDescription = lettersStringBuffer.toString()

        // Return result
        return newOrder
    }

    fun compileSpecificQtyHoardOrder(_coinMin: Double, _coinMax: Double,
                                     _coinSet: Set<Pair<Double,String>>,
                                     _gemQty: Int, _artQty: Int,
                                     _potQty: Int, _scrQty: Int, _aOWQty: Int,
                                     _aBWQty: Int, _anyQty: Int,
                                     _exSCQty: Int, _params: OrderParams) : HoardOrder {

        val coinPileMap = getRandomCoinDistribution(_coinMin,_coinMax,_coinSet)

        return HoardOrder(
            creationDescription = "Custom loot quantity",
            copperPieces = coinPileMap.getOrDefault("cp",0),
            silverPieces = coinPileMap.getOrDefault("sp",0),
            electrumPieces = coinPileMap.getOrDefault("ep",0),
            goldPieces = coinPileMap.getOrDefault("gp",0),
            hardSilverPieces = coinPileMap.getOrDefault("hsp",0),
            platinumPieces = coinPileMap.getOrDefault("pp",0),
        )
    }
     */

    fun getRandomCoinDistribution(_minimum: Double, _maximum: Double,
                                          allowedDenoms: Set<Pair<Double,String>>) : Map<String,Int> {

        fun Double.roundToTwoDecimal():Double = (this * 100.00).roundToInt() / 100.00

        /** Returns a mutable list of gpVale/name pairs from [allowedDenoms], starting with lowest value */
        fun getSortedDenominations() : MutableList<Pair<Double,String>> {
            return allowedDenoms.filter { it.first in 0.01..5.00 }
                .sortedBy{it.first}
                .map { (gpValue, name) -> gpValue.roundToTwoDecimal() to name.toLowerCase() }
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
}