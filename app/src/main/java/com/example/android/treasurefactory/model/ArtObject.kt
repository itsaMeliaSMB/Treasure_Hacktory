package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import kotlin.math.floor

data class ArtObject(
    @PrimaryKey(autoGenerate = true) @NotNull val artID: Int,
    val hoardID: Int,
    val iconID: String,
    val artType: String,
    val renown: String,
    val size: String,
    val condition: String,
    val materials: String,
    val quality: String,
    val age: Int,
    val subject: String,
    var valueLevel: Int) : Evaluable{

    @Ignore
    override fun getGpValue() : Double {

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

      return valueLevelToGPValue[valueLevel] ?: 0.0
    }

    @Ignore
    override fun getXpValue(): Int {

        val xpGpRatio = 0.2

        return floor(getGpValue() * xpGpRatio).toInt()
    }
}