package com.example.android.treasurefactory.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.treasurefactory.LootGeneratorAsync
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository
import com.example.android.treasurefactory.ui.GenDropdownTag
import com.example.android.treasurefactory.ui.GenEditTextTag
import kotlinx.coroutines.launch
import java.util.*
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

const val MAXIMUM_UNIQUE_QTY = 150
const val MAXIMUM_SPELLS_PER_SCROLL = 7
const val MAXIMUM_SPELL_COLLECTION_QTY = 75

const val MAXIMUM_HOARD_VALUE = 9999999999.99
const val MAXIMUM_COINAGE_AMOUNT = 999999999.99

const val ART_MAP_CHANCE = 5
const val SCROLL_MAP_CHANCE = 10

class HoardGeneratorViewModel(private val hmRepository: HMRepository): ViewModel() {

    //region << Values, variables, and containers >>

    // region ( Method-agnostic variables )
    private var hoardName = ""

    /**
     * Index of displayed child view group and method to use for order generation.
     * 0 = Letter-code, 1 = Specific quantity.
     */
    private var generationMethodPos = 0

    private var isRunningAsync = false

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)
    // endregion

    // region ( Letter Code value containers )
    private val oddsTable = sortedMapOf(
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

    private val lairList = getLetterArrayList(true)
    private val smallList = getLetterArrayList(false)

    val lairListLiveData = MutableLiveData(lairList.toList())
    val smallListLiveData = MutableLiveData(smallList.toList())
    // endregion

    // region ( Specific Quantity value containers )
    private var coinMin = 0.0
    private var coinMax = 0.0
    var cpChecked = false
    var spChecked = false
    var epChecked = false
    var gpChecked = false
    var hspChecked = false
    var ppChecked = false

    private var gemQty = 0
    private var gemMinPos = 0
    private var gemMaxPos = 0

    private var artQty = 0
    private var artMinPos = 0
    private var artMaxPos = 0
    var artMapChecked = false

    private var potionQty = 0
    private var scrollQty = 0
    private var armWepQty = 0
    private var anyButQty = 0
    private var anyMgcQty = 0
    var spellScrollChecked = false
    var nonSpScrollChecked = false
    var scrollMapChecked = false
    var intWepChecked = false
    var cursedChecked = true
    var relicsChecked = true

    private var spCoQty = 0
    private var maxSpellsPerSpCo = 1
    private var splDisciplinePos = 0
    private var genMethodPos = 0
    private var enabledGenMetArray = BooleanArray(1)
    private var spLvlMinPos = 1
    private var spLvlMaxPos = 9
    var splatChecked = false
    var hackJChecked = false
    var otherChecked = false
    var restrictChecked = false
    var rerollChecked = false
    private var spCoCursesPos = 0

    // region ( Public getters )

    fun getHoardName() = hoardName
    fun getGenerationMethodPos() = generationMethodPos
    fun getCoinMin() = coinMin
    fun getCoinMax() = coinMax

    fun getGemQty() = gemQty
    fun getGemMinPos() = gemMinPos
    fun getGemMaxPos() = gemMaxPos

    fun getArtQty() = artQty
    fun getArtMinPos() = artMinPos
    fun getArtMaxPos() = artMaxPos

    fun getPotionQty() = potionQty
    fun getScrollQty() = scrollQty
    fun getArmWepQty() = armWepQty
    fun getAnyButQty() = anyButQty
    fun getAnyMgcQty() = anyMgcQty

    fun getSpCoQty() = spCoQty
    fun getMaxSpellsPerSpCo() = maxSpellsPerSpCo
    fun getSplDisciplinePos() = splDisciplinePos
    fun getGenMethodPos() = genMethodPos
    fun getEnabledGenMetArray() = enabledGenMetArray
    fun getSpLvlMinPos() = spLvlMinPos
    fun getSpLvlMaxPos() = spLvlMaxPos
    fun getSpCoCursesPos() = spCoCursesPos
    // endregion
    // endregion

    /*
    LiveData links TODO remove before shipped build
    https://www.rockandnull.com/jetpack-viewmodel-initialization/
    https://medium.com/@fluxtah/two-ways-to-keep-livedata-t-immutable-and-mutable-only-from-a-single-source-a4a1dcdc0ef
    https://stackoverflow.com/questions/50629402/how-to-properly-update-androids-recyclerview-using-livedata
    https://stackoverflow.com/questions/66595863/update-a-row-in-recyclerview-with-listadapter
    */
    // endregion

    // region [ Setter functions ]
    fun setHoardName(newHoardName: String){ hoardName = newHoardName }

    fun setGeneratorMethodPos(newViewGroupIndex: Int){
        if (newViewGroupIndex in 0..1) {
            generationMethodPos = newViewGroupIndex
            Log.d("generatorViewModel","Value updated for generationMethodPos. [generationMethodPos = $generationMethodPos]")
        } else {
            Log.d("generatorViewModel","Out of bounds new value for generationMethodPos. [generationMethodPos = $generationMethodPos]")
        }
    }

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    // region ( Letter update functions )

    /**
     * Updates the list of [LetterEntries][LetterEntry] and posts new value to LiveData.
     *
     * @param position adapterPosition of entry to update.
     * @param addend Integer value to add to entry.
     * @param targetLairList If true, the [lairList] will be modified. Otherwise, [smallList] will be modified.
     */
    private fun addToLetterListEntryQty(position: Int, addend: Int, targetLairList: Boolean) {

        val newValue: Int

        if (targetLairList){
            if (!(lairList.isEmpty())&&(position in 0 until lairList.size)) {

                val oldValue = lairList[position].quantity
                newValue = (oldValue + addend).coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)

                lairList[position].quantity = newValue
                lairListLiveData.postValue(lairList.toList())

                Log.d("updateLetterListEntry","Updated Qty of lairList[$position] from " +
                    "$oldValue to $newValue (instructed to add $addend)")
            }
        } else {
            if (!(smallList.isEmpty())&&(position in 0 until smallList.size)) {

                val oldValue = smallList[position].quantity
                newValue = (oldValue + addend).coerceIn(MINIMUM_LETTER_QTY, MAXIMUM_LETTER_QTY)

                smallList[position].quantity = newValue
                smallListLiveData.postValue(smallList.toList())

                Log.d("updateLetterListEntry","Updated Qty of smallList[$position] from " +
                        "$oldValue to $newValue (instructed to add $addend)")
            }
        }
    }

    fun incrementLetterQty(position: Int, isPositive: Boolean, targetLairList: Boolean) {

        addToLetterListEntryQty(position, if (isPositive) 1 else -1, targetLairList)
    }

    fun resetLairCount() {
        lairList.forEach { entry -> entry.quantity = 0 }
        lairListLiveData.postValue(lairList.toList())
    }

    fun resetSmallCount() {
        smallList.forEach { entry -> entry.quantity = 0 }
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
        gemMinPos = 0
        gemMaxPos = 0

        artQty = 0
        artMinPos = 0
        artMaxPos = 0
        artMapChecked = false
        potionQty = 0
        scrollQty = 0
        armWepQty = 0
        anyButQty = 0
        anyMgcQty = 0
        spellScrollChecked = false
        nonSpScrollChecked = false
        scrollMapChecked = false
        intWepChecked = false
        cursedChecked = true
        relicsChecked = true

        spCoQty = 0
        maxSpellsPerSpCo = 1
        splDisciplinePos = 0
        genMethodPos = 0
        spLvlMinPos = 1
        spLvlMaxPos = 9
        splatChecked = false
        hackJChecked = false
        otherChecked = false
        restrictChecked = false
        rerollChecked = false
        spCoCursesPos = 0
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

                    GenEditTextTag.MAX_SPELL_PER -> if (parsedValue != maxSpellsPerSpCo) {

                        maxSpellsPerSpCo = parsedValue
                    }

                    else -> Log.e("setValueFromEditText | validateAsInt",
                        "Invalid GenEditTextTag entered. No value was changed.")
                }

                errorString = when {

                    (floor >= 0) && (parsedValue < floor)    -> "Input too low."
                    (ceiling >= 0) && (parsedValue > ceiling)-> "Input too high."
                    else -> errorString
                }

            } else {

                Log.e("setValueFromEditText | validateAsInt",
                    "No parsable integer in string. No value changed.")
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

                    else -> Log.e("setValueFromEditText | validateAsDouble",
                        "Invalid GenEditTextTag entered. No value was changed.")
                }
            } else {

                Log.e("setValueFromEditText | validateAsDouble",
                    "No parsable double in string. No value changed.")
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
            GenEditTextTag.MAX_SPELL_PER    -> validateAsInt(1, MAXIMUM_SPELLS_PER_SCROLL)
        }

        return errorString
    }

    /**
     * Sets value of GeneratorViewModel field from data passed from an OnItemSelectedListener
     * from GeneratorHoardFragment. Returns a string to be set as the calling view's error message
     * if it fails validation.
     */
    fun setValueFromDropdown(sourceViewTag: GenDropdownTag, position: Int, childEnabled: Boolean) : String? {

        var errorString : String? = null

        fun validateEnabled(){
            if (!childEnabled) errorString = "Invalid method for chosen discipline."
        }

        when (sourceViewTag) {

            GenDropdownTag.GEM_MINIMUM -> {
                Log.d("setValueFromDropdown($sourceViewTag,$position,$childEnabled)",
                    "Before: gemMinPos = ${gemMinPos}, gemMaxPos = $gemMaxPos")

                gemMinPos = position

                Log.d("setValueFromDropdown($sourceViewTag,$position,$childEnabled)",
                    "After: gemMinPos = ${gemMinPos}, gemMaxPos = $gemMaxPos")
            }

            GenDropdownTag.GEM_MAXIMUM -> {

                errorString = validateDropdownMaximum(sourceViewTag,position).takeIf { it != null }
                gemMaxPos = position
            }

            GenDropdownTag.ART_MINIMUM -> artMinPos = position

            GenDropdownTag.ART_MAXIMUM -> {
                errorString = validateDropdownMaximum(sourceViewTag,position).takeIf { it != null }
                artMaxPos = position
            }

            GenDropdownTag.SPELL_DISCIPLINE -> {
                splDisciplinePos = position
            }

            GenDropdownTag.SPELL_TYPE -> {
                validateEnabled()
                genMethodPos
            }

            GenDropdownTag.SPELL_MINIMUM -> spLvlMinPos = position

            GenDropdownTag.SPELL_MAXIMUM -> {
                errorString = validateDropdownMaximum(sourceViewTag,position).takeIf { it != null }
                spLvlMaxPos = position
            }

            GenDropdownTag.SPELL_CURSES -> {
                spCoCursesPos = position
            }
        }

        Log.d("setValueFromDropdown($sourceViewTag,$position,$childEnabled)",
            "Final errorString = " +
            if (errorString != null) "\"$errorString\"" else "null")

        return errorString
    }

    fun setEnabledItemsByDiscipline(splDisPos: Int, splTypeArraySize: Int) : BooleanArray {

        val newArray = BooleanArray( splTypeArraySize.takeUnless { it < 1 } ?: 1 ) { false }

        val templateArray = when (splDisPos) {
            0   -> booleanArrayOf(true,true)
            //1   -> booleanArrayOf(true,true,false,true) TODO spellbooks implemented
            //2   -> booleanArrayOf(true,true,true) TODO Chosen One is implemented
            else-> booleanArrayOf(true,true)
        }

        val templateArrayLastIndex = templateArray.lastIndex

        // Apply template over properly-size array
        newArray.forEachIndexed { index, _ ->

            newArray[index] = if (index <= templateArrayLastIndex) {
                templateArray[index]
            } else {
                false
            }
        }

        enabledGenMetArray = newArray

        return newArray
    }
    // endregion
    // endregion

    // region [ Validation functions ]
    fun validateDropdownMaximum(sourceViewTag: GenDropdownTag, position: Int): String? {

        var errorString : String? = null

        Log.d("validateDropdownMaximum($sourceViewTag, $position)",
            "Dropdown maximum validation process started.")

        val minPosition = when (sourceViewTag) {
            GenDropdownTag.GEM_MAXIMUM  -> gemMinPos
            GenDropdownTag.ART_MAXIMUM  -> artMinPos
            GenDropdownTag.SPELL_MAXIMUM-> spLvlMinPos
            else    -> {
                Log.e("setValueFromDropdown()","Invalid sourceViewTag $sourceViewTag " +
                        "passed in validateMaximum()")
                0
            }
        }

        Log.d("validateDropdownMaximum($sourceViewTag, $position)",
            "minPosition = $minPosition, position = $position")

        if ( !( ( (position == 0) && (sourceViewTag != GenDropdownTag.SPELL_MAXIMUM) )
                    || (position >= minPosition) ) ) {
            errorString = "Maximum cannot be lower than Minimum."

            Log.e("validateDropdownMaximum($sourceViewTag, $position)",
                "Invalid maximum detected.")
        }

        Log.d("validateDropdownMaximum($sourceViewTag, $position)",
            "Dropdown maximum validation process completed." +
                    if (errorString != null) " (\"$errorString\")" else "(no errorString)")

        return errorString
    }

    fun validateCoinageMaximum() : String? {

        return when {
            (coinMax < coinMin) -> "Cannot be lower than minimum"
            (coinMax !in 0.0..MAXIMUM_COINAGE_AMOUNT) -> "Value out of bounds"
            else -> null
        }
    }

    fun validateLetterCodeValues() : Boolean {

        return (listOf(lairList, smallList).flatten().indexOfFirst { entry -> entry.quantity > 0 }) != -1
    }
    // endregion

    // region [ Order compilation functions ]
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
        lairList.forEach{ letterMap[it.letterCode] = it.quantity }
        smallList.forEach{ letterMap[it.letterCode] = it.quantity}

        val initialDescription = "Initial composition: "
        val lettersStringBuilder = StringBuilder(initialDescription)

        val newOrder = HoardOrder()

        newOrder.hoardName = this.hoardName

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
                if (!(lettersStringBuilder.equals(initialDescription))) {
                    // Add a comma if not the first entry TODO fix this
                    lettersStringBuilder.append(", ")
                }
                // Add letter times quantity
                lettersStringBuilder.append("${key}x$value")
            }
        }

        // Update description log
        newOrder.creationDescription = lettersStringBuilder.toString()

        // TODO DEBUG ONLY: Log results
        reportHoardOrderToDebug(newOrder)

        // Return result
        return newOrder
    }

    fun compileSpecificQtyHoardOrder() : HoardOrder {

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
                if (gemMinPos == 0) 0 else gemMinPos,
                if (gemMaxPos == 0) 16 else gemMaxPos),
            ArtRestrictions(
                if (artMinPos == 0) -19 else artMinPos - 20,
                if (artMaxPos == 0) 31 else artMaxPos - 20,
                if (artMapChecked) ART_MAP_CHANCE else 0),
            MagicItemRestrictions(
                spellScrollEnabled = spellScrollChecked,
                nonScrollEnabled = nonSpScrollChecked,
                scrollMapChance = if (scrollMapChecked) SCROLL_MAP_CHANCE else 0,
                allowCursedItems = cursedChecked,
                allowIntWeapons = intWepChecked,
                allowArtifacts = relicsChecked,
                spellCoRestrictions = SpellCoRestrictions(
                    spLvlMinPos,
                    spLvlMaxPos,
                    when(splDisciplinePos){
                        0   -> AllowedDisciplines(true,true,false)
                        1   -> AllowedDisciplines(true,false,false)
                        2   -> AllowedDisciplines(false,true,false)
                        else-> AllowedDisciplines(true,true,true) },
                    maxSpellsPerSpCo,
                    SpCoSources(splatChecked,hackJChecked,otherChecked),
                    restrictChecked,
                    rerollChecked,
                    cursedChecked,
                    when(spCoCursesPos){
                        0   -> SpCoCurses.STRICT_GMG
                        1   -> SpCoCurses.OFFICIAL_ONLY
                        2   -> SpCoCurses.ANY_CURSE
                        else-> SpCoCurses.NONE },
                    when (genMethodPos) {
                        0   -> SpCoGenMethod.TRUE_RANDOM
                        1   -> SpCoGenMethod.BY_THE_BOOK
                        2   -> SpCoGenMethod.CHOSEN_ONE
                        3   -> SpCoGenMethod.SPELL_BOOK
                        else-> SpCoGenMethod.ANY_PHYSICAL }
                )
            )
        )

        newOrder = HoardOrder(
            hoardName = newHoardName,
            creationDescription = "User-specified loot quantity",
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
            genParams = newParams
        )

        // TODO DEBUG ONLY: Log results
        reportHoardOrderToDebug(newOrder)

        return newOrder
    }
    // endregion

    // region [ Hoard generation functions ]

    fun generateHoard(order: HoardOrder, appVersion: Int) {

        //https://developer.android.com/kotlin/coroutines

        viewModelScope.launch {

            setRunningAsync(true)

            val lootGenerator = LootGeneratorAsync(hmRepository)

            val newHoardId = lootGenerator.createHoardFromOrder(order, appVersion)

            Log.d("generateHoard()","newHoardId = $newHoardId")

            setRunningAsync(false)

            //TODO DialogFragment displaying hoard and offering navigation.
        }
    }
    // endregion

    // region [ Helper functions ]
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

    private fun reportHoardOrderToDebug(order: HoardOrder) {
        Log.d("convertLetterToHoardOrder","Received name: ${order.hoardName}")
        Log.d("convertLetterToHoardOrder",order.creationDescription)
        Log.d("convertLetterToHoardOrder","\n- - - QUANTITIES - - -")
        Log.d("convertLetterToHoardOrder","> COINAGE:")
        Log.d("convertLetterToHoardOrder","${order.copperPieces} cp, " +
                "${order.silverPieces} sp, " + "${order.electrumPieces} ep, " +
                "${order.goldPieces} gp, " + "${order.hardSilverPieces} hsp, " +
                "and ${order.platinumPieces} pp")
        Log.d("convertLetterToHoardOrder","> ART OBJECTS:")
        Log.d("convertLetterToHoardOrder","${order.gems} gems and " +
                "${order.artObjects} pieces of artwork")
        Log.d("convertLetterToHoardOrder","> MAGIC ITEMS:")
        Log.d("convertLetterToHoardOrder","${order.potions} potions, " +
                "${order.scrolls} scrolls, ${order.armorOrWeapons} armor/weapons, " +
                "${order.anyButWeapons} magic items (non-weapon), and " +
                "${order.anyMagicItems} magic items of any type")
        Log.d("convertLetterToHoardOrder", "> SPELL COLLECTIONS:")
        Log.d("convertLetterToHoardOrder","${order.extraSpellCols} explicitly- generated" +
                "spell collections")
        Log.d("convertLetterToHoardOrder.genParameters","\n- - - PARAMETERS - - -")
        Log.d("convertLetterToHoardOrder.genParameters","> GEM PARAMS:")
        Log.d("convertLetterToHoardOrder.genParameters","Value bias range: " +
                "${order.genParams.gemParams._minLvl}-${order.genParams.gemParams._maxLvl} (" +
                order.genParams.gemParams.levelRange.toString() + ")")
        Log.d("convertLetterToHoardOrder.genParameters","> ART PARAMS:")
        Log.d("convertLetterToHoardOrder.genParameters","Value bias range: " +
                "${order.genParams.artParams._minLvl}-${order.genParams.artParams._maxLvl} (" +
                order.genParams.artParams.levelRange.toString() + "), Paper map chance: " +
                order.genParams.artParams.paperMapChance.toString() + "%")
        Log.d("convertLetterToHoardOrder.genParameters","> MAGIC ITEM PARAMS:")
        Log.d("convertLetterToHoardOrder.genParameters",
            "Allow spell scrolls [${order.genParams.magicParams.spellScrollEnabled}], " +
                    "Allow non-spell scrolls [${order.genParams.magicParams.nonScrollEnabled}], " +
                    "Scroll map chance: ${order.genParams.magicParams.scrollMapChance}%, " +
                    "Allow cursed [${order.genParams.magicParams.allowCursedItems}], " +
                    "Allow int. weapons [${order.genParams.magicParams.allowIntWeapons}], " +
                    "Allow artifacts [${order.genParams.magicParams.allowCursedItems}]"
        )
        Log.d("convertLetterToHoardOrder.genParameters","> SPELL CO PARAMS:")
        Log.d("convertLetterToHoardOrder.genParameters","Level range: ${
            order.genParams.magicParams.spellCoRestrictions._minLvl}-${
            order.genParams.magicParams.spellCoRestrictions._maxLvl
        } (" + order.genParams.magicParams.spellCoRestrictions.levelRange.toString() +
                "), Allowed disciplines: ${
                    if (order.genParams.magicParams.spellCoRestrictions.allowedDisciplines.arcane)
                        "<arcane>" else "<>"} ${
                    if (order.genParams.magicParams.spellCoRestrictions.allowedDisciplines.divine)
                        "<divine>" else "<>"} ${
                    if (order.genParams.magicParams.spellCoRestrictions.allowedDisciplines.natural)
                        "<natural>" else "<>"}, Max spell count: " +
                order.genParams.magicParams.spellCoRestrictions.spellCountMax.toString() +
                ", sources: " + order.genParams.magicParams.spellCoRestrictions.spellSources.toString() +
                ", Allow restricted [${order.genParams.magicParams.spellCoRestrictions.allowRestricted}]" +
                ", Re-roll choices [${order.genParams.magicParams.spellCoRestrictions.rerollChoice}]" +
                ", Allow cursed [${order.genParams.magicParams.spellCoRestrictions.allowCurse}]" +
                ", Allowed curses <${order.genParams.magicParams.spellCoRestrictions.allowedCurses}>" +
                ", Generation method: ${order.genParams.magicParams.spellCoRestrictions.genMethod}\n")
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