package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import kotlin.math.roundToInt

@Entity(tableName = "hackmaster_gem_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("gemID"),
        onDelete = ForeignKey.CASCADE ) ])
data class Gem(
    @PrimaryKey(autoGenerate = true) @NotNull val gemID: Int = 0,
    val hoardID: Int = 0,
    val iconID: String,
    val type: String,
    val size: String,
    val quality: String,
    val value: Int,
    val name: String,
    val opacity: Int,
    val description: String = "") : Evaluable {

    @Ignore
    override fun getGpValue(): Double {

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
            12 to 250000.0,
            13 to 500000.0,
            14 to 1000000.0,
            15 to 2500000.0,
            16 to 10000000.0
        )

        return valueLevelToGPValue[value] ?: 0.0
    }

    @Ignore
    override fun getXpValue(): Int {

        val xpGpRatio = 0.2

        return (getGpValue() * xpGpRatio).roundToInt()
    }

    @Ignore
    fun getWeightInCarats(): Double {

        val sizeToWeight = mapOf(
            "Tiny" to 0.25,
            "Very small" to .5,
            "Small" to 1.0,
            "Average" to 2.0,
            "Large" to 3.0,
            "Very large" to 6.0,
            "Huge" to 9.0,
            "Massive" to 14.0,
            "Gargantuan" to 20.0
        )

        val typeToMultiplier = mapOf(
            "Ornamental" to 5.0,
            "Semiprecious" to 3.0,
            "Fancy" to 2.0,
            "Precious" to 1.0,
            "Gem" to 0.75,
            "Jewel" to 0.5
        )

        return (sizeToWeight[size] ?: 2.0) * (typeToMultiplier[type] ?: 1.0)
    }

    @Ignore
    fun getDiameterInInches(): Double {

        val sizeToCarats = mapOf(
            "Tiny" to 0.125,
            "Very small" to 0.25,
            "Small" to 0.375,
            "Average" to 0.67,
            "Large" to 1.0,
            "Very large" to 1.33,
            "Huge" to 1.67,
            "Massive" to 2.375,
            "Gargantuan" to 3.33
        )

        val typeToMultiplier = mapOf(
            "Ornamental" to 5.0,
            "Semiprecious" to 3.0,
            "Fancy" to 2.0,
            "Precious" to 1.0,
            "Gem" to 0.75,
            "Jewel" to 0.5
        )

        return (sizeToCarats[size] ?: 0.67) * (typeToMultiplier[type] ?: 1.0)
    }
}