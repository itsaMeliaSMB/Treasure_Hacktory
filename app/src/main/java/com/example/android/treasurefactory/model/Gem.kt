package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.capitalized
import org.jetbrains.annotations.NotNull

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
    val type: Int,
    val size: Int,
    val quality: Int,
    val variation: Int,
    val name: String,
    val opacity: Int,
    val description: String = "",
    val currentGPValue: Double = 0.0, //TODO refactor db entity et al to include this value
    val valueHistory: List<Pair<Long,String>> = emptyList() //TODO refactor db entity et al to include this value
    ) {

    // TODO Refactor to include everything in new Gem schema

    @Ignore
    fun getTypeAsString() : String {

        val typeIntToString = mapOf(

            5 to "ornamental stone",
            6 to "semiprecious stone",
            7 to "fancy stone",
            8 to "precious stone",
            9 to "gemstone",
            10 to "jewel"
        )

        return typeIntToString.getOrDefault(type,"stone")
    }

    @Ignore
    fun getSizeAsString() : String {

        val sizeIntToString = mapOf(

            -3 to "tiny",
            -2 to "very small",
            -1 to "small",
            0 to "average-sized",
            1 to "large",
            2 to "very large",
            3 to "huge",
            4 to "massive",
            5 to "gargantuan"
        )

        return sizeIntToString.getOrDefault(size,"oddly-sized")
    }

    @Ignore
    fun getQualityAsString() : String {

        val qualityIntToString = mapOf(

            -3 to "badly flawed",
            -2 to "flawed",
            -1 to "minorly included",
            0 to "average",
            1 to "good",
            2 to "excellent",
            3 to "near-perfect",
            4 to "perfect",
            5 to "flawless"
        )

        return qualityIntToString.getOrDefault(quality,"inscrutable")
    }

    @Ignore
    fun getOpacityAsString() : String {

        return when (opacity) {
            0   -> "transparent"
            1   -> "translucent"
            2   -> "opaque"
            else-> "unknown"
        }
    }

    @Ignore
    fun getDefaultBaseValue() : Int = type + size + quality

    @Ignore
    fun getWeightInCarats(): Double {

        val sizeToWeight = mapOf(
            -3 to 0.25,
            -2 to .5,
            -1 to 1.0,
            0 to 2.0,
            1 to 3.0,
            2 to 6.0,
            3 to 9.0,
            4 to 14.0,
            5 to 20.0
        )

        val typeToMultiplier = mapOf(
            5 to 5.0,
            6 to 3.0,
            7 to 2.0,
            8 to 1.0,
            9 to 0.75,
            10 to 0.5
        )

        return sizeToWeight.getOrDefault(size,2.0) * typeToMultiplier.getOrDefault(type,1.0)
    }

    @Ignore
    fun getDiameterInInches(): Double {

        val sizeToCarats = mapOf(
            -3 to 0.125,
            -2 to 0.25,
            -1 to 0.375,
            0 to 0.67,
            1 to 1.0,
            2 to 1.33,
            3 to 1.67,
            4 to 2.375,
            5 to 3.33
        )

        val typeToMultiplier = mapOf(
            5 to 5.0,
            6 to 3.0,
            7 to 2.0,
            8 to 1.0,
            9 to 0.75,
            10 to 0.5
        )

        return sizeToCarats.getOrDefault(size,0.67) * typeToMultiplier.getOrDefault(type,1.0)
    }

    @Ignore
    fun getSubtitle(): String = getSizeAsString().capitalized() + ", " + getQualityAsString() +
            " " + getTypeAsString()

    @Ignore
    fun getFlavorTextAsList(): List<Pair<String,String>> {

        //TODO look up formatting for cast-double strings in kotlin for leading zeros and limited decimal places
        return listOf(
            Pair("Weight","${getWeightInCarats()} ct (~ ${getWeightInCarats() * 0.200} g)"),
            Pair("Diameter","${getDiameterInInches()} in (~ ${getDiameterInInches() * 2.54} cm)"),
            Pair("Opacity",getOpacityAsString().capitalized()),
            Pair("Appearance",description.capitalized())
        )
    }
}