package com.example.android.treasurefactory.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "hackmaster_gem_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("gemID"),
        onDelete = CASCADE ) ])
data class Gem(@PrimaryKey(autoGenerate = true) val gemID: Int = 0,
               val hoardID: Int = 0,
               val iconID: String,
               val type: String,
               val size: String,
               val quality: String,
               val value: Int,
               val name: String,
               val opacity: Int,
               val description: String = "" //TODO consider ignoring for db
) {

    /**
     * Size to carat and diameter multipliers.
     */
    private val sizeToMetricsPair = mapOf(
        "Tiny" to Pair(0.25,0.125),
        "Very small" to Pair(0.5, 0.25),
        "Small" to Pair(1.0, 0.375),
        "Average" to Pair(2.0, 0.67),
        "Large" to Pair(3.0, 1.0),
        "Very large" to Pair(6.0, 1.33),
        "Huge" to Pair(9.0, 1.67),
        "Massive" to Pair(14.0, 2.375),
        "Gargantuan" to Pair(20.0, 3.33)
    )

    private val typeToMultiplier = mapOf(
        "Ornamental" to 5.0,
        "Semiprecious" to 3.0,
        "Fancy" to 2.0,
        "Precious" to 1.0,
        "Gem" to 0.75,
        "Jewel" to 0.5
    )

    private val valueLevelToGPValue = mapOf(

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
        12 to 250000.0,
        13 to 500000.0,
        14 to 1000000.0,
        15 to 2500000.0,
        16 to 10000000.0
    )

    val gpValue = valueLevelToGPValue[value]

    fun getWeightInCarats() = (sizeToMetricsPair[size]?.first ?: 2.0) * (typeToMultiplier[type] ?: 1.0)
    fun getDiameterInInches() = (sizeToMetricsPair[size]?.second ?: 0.67) * (typeToMultiplier[type] ?: 1.0)
}