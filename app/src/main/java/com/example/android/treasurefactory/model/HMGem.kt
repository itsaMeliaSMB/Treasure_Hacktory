package com.example.android.treasurefactory.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "hackmaster_gem_table",
    foreignKeys = [ForeignKey(
        entity = HMHoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("gemID"),
        onDelete = CASCADE ) ])
data class HMGem(@PrimaryKey(autoGenerate = true) val gemID: Int = 0,
                 val hoardID: Int = 0,
                 val iconID: String,
                 val variety: Variety,
                 val size: Size,
                 val quality: Quality,
                 val initialValue: Int,
                 val type: String,
                 val opacity: Opacity = Opacity.TRANSPARENT,
                 val description: String = "" //TODO consider ignoring for db
) {

    enum class Size(val valueMod: Int, val description: String, val gemCarat: Double,
                    val gemDiameterInches: Double){

        TINY        (-3, "tiny", 0.25, 0.125),
        VERY_SMALL  (-2, "very small", 0.5, 0.25),
        SMALL       (-1, "small", 1.0, 0.375),
        AVERAGE     (0, "average",2.0, 0.67),
        LARGE       (1, "large", 3.0, 1.0),
        VERY_LARGE  (2, "very large", 6.0, 1.33),
        HUGE        (3, "huge", 9.0, 1.67),
        MASSIVE     (4, "massive", 14.0, 2.375),
        GARGANTUAN  (5, "gargantuan", 20.0, 3.33)
    }
    enum class Variety(val tier: String, val baseLevel: Int, val possibilities: Int, val gemMultiplier: Double){

        ORNAMENTAL      ("ornamental", 5, 12, 5.0),
        SEMIPRECIOUS    ("semiprecious", 6, 12, 3.0),
        FANCY           ("fancy", 7, 12, 2.0),
        PRECIOUS        ("precious", 8, 6, 1.0),
        GEM             ("gem", 9, 10, 0.75),
        JEWEL           ("jewel", 10, 6, 0.5)
    }
    enum class Opacity {

        TRANSLUCENT(),
        TRANSPARENT(),
        OPAQUE();
    }
    enum class Quality(val valueMod: Int, val description: String){

        BADLY_FLAWED    (-3, "badly flawed"),
        FLAWED          (-2, "flawed"),
        MINOR_INCLUSIONS(-1, "minor inclusions"),
        AVERAGE         (0, "average"),
        GOOD            (1, "good"),
        EXCELLENT       (2, "excellent"),
        NEAR_PERFECT    (3, "near perfect"),
        PERFECT         (4, "perfect"),
        FLAWLESS        (5, "flawless")
    }

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

    val gpValue = valueLevelToGPValue[initialValue]

    fun getWeightInCarats() = size.gemCarat * variety.gemMultiplier
    fun getDiameterInInches() = size.gemDiameterInches * variety.gemMultiplier
}