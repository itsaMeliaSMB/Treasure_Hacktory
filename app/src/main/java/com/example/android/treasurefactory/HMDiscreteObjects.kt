package com.example.android.treasurefactory

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

@Entity(foreignKeys = [ForeignKey(
    entity = HMHoard::class,
    parentColumns = arrayOf ("hoardID"),
    childColumns = arrayOf("artID"),
    onDelete = CASCADE ) ] )
data class HMArtObject(@PrimaryKey val artID: Int,
                       val hoardID: Int,
                       val iconID: String,
                       val artType: ArtType,
                       val renown: Renown,
                       val size: Size,
                       val condition: Condition,
                       val materials: Materials,
                       val quality: Quality,
                       val age: Int,
                       val subject: Subject,
                       var valueLevel: Int) { //TODO refactor value level to allow for value variation

    enum class ArtType(val conditionMod: Int, val ageMod: Int, val description: String){

        // Adjustments are to condition and age tables

        PAPER       (-2, -2, "paper art"),
        FABRIC      (-2, -2, "fabric art"),
        FURNISHING  (-1, -1, "furnishing"),
        PAINTING    (-1, -1, "painting"),
        WOOD        (-1, -1, "scrimshaw and woodwork"),
        CERAMIC     (0, 0, "ceramics"),
        GLASS       (0, 0, "glasswork"),
        STONE       (1, 0, "stonework"),
        METAL       (2, 0, "metalwork"),
        MAGICAL     (3, 0, "magical")
    }
    enum class Renown(val valueMod: Int, val description: String) {

        UNKNOWN                 (-3,"unknown"),
        OBSCURE                 (-2,"obscure"),
        CITY_RENOWNED           (-1,"city renowned"),
        REGIONALLY_RENOWNED     (0,"regionally renowned"),
        NATIONALLY_RENOWNED     (1,"nationally renowned"),
        CONTINENTALLY_RENOWNED  (2,"continentally renowned"),
        WORLDLY_RENOWNED        (3,"worldly renowned"),
        MOVEMENT_LEADER         (4,"movement leader")
    }
    enum class Size(val valueMod: Int, val description: String){

        TINY        (-3, "tiny"),
        VERY_SMALL  (-2, "very small"),
        SMALL       (-1, "small"),
        AVERAGE     (0, "average"),
        LARGE       (1, "large"),
        VERY_LARGE  (2, "very large"),
        HUGE        (3, "huge"),
        MASSIVE     (4, "massive"),
        GARGANTUAN  (5, "gargantuan")
    }
    enum class Condition(val valueMod: Int, val description: String){

        BADLY_DAMAGED   (-3, "badly damaged"),
        DAMAGED         (-2, "damage"),
        WORN            (-1, "worn"),
        AVERAGE         (0, "average"),
        GOOD            (1, "good"),
        EXCELLENT       (2, "excellent"),
        NEAR_PERFECT    (3, "near perfect"),
        PERFECT         (4, "perfect"),
        FLAWLESS        (5, "flawless")
    }
    enum class Materials(val valueMod: Int, val description: String){

        AWFUL           (-3, "awful"),
        POOR            (-2, "poor"),
        BELOW_AVERAGE   (-1, "below average"),
        AVERAGE         (0, "average"),
        ABOVE_AVERAGE   (1, "above average"),
        GOOD            (2, "good"),
        EXCELLENT       (3, "excellent"),
        FINEST          (4, "finest"),
        UNIQUE          (5, "unique")
    }
    enum class Quality (val valueMod: Int, val description: String){

        AWFUL           (-3, "awfully executed"),
        POOR            (-2, "poorly executed"),
        BELOW_AVERAGE   (-1, "below average execution"),
        AVERAGE         (0, "average execution"),
        ABOVE_AVERAGE   (1, "above average execution"),
        GOOD            (2, "good execution"),
        EXCELLENT       (3, "excellent execution"),
        BRILLIANT       (4, "brilliant execution"),
        MASTERPIECE     (5, "masterpiece")
    }
    enum class Subject(val valueMod: Int, val description: String){

        ABSTRACT    (-2, "abstract"),
        MONSTER     (-1, "monster"),
        HUMAN       (0, "human or demi-human"),
        NATURAL     (0, "natural"),
        HISTORICAL  (0, "historical"),
        RELIGIOUS   (0, "religious"),
        NOBLE       (1, "wealthy/noble"),
        ROYALTY     (2, "royalty")
    }

    private val valueLevelToGPValue = mapOf(

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

    fun getGpValue() = valueLevelToGPValue[valueLevel] ?: 0.0
}

@Entity(foreignKeys = [ForeignKey(
    entity = HMHoard::class,
    parentColumns = arrayOf ("hoardID"),
    childColumns = arrayOf("mItemID"),
    onDelete = CASCADE ) ] )
data class HMMagicItem(@PrimaryKey(autoGenerate = true) val mItemID: Int,
                       val hoardID: Int,
                       var iconID: String, //TODO Add necessary foreign keys
                       val typeOfItem: MagicItemType,
                       val name: String,
                       val sourceText: String, val sourcePage: Int, //TODO add var for nickname when architecture is clearer
                       val xpValue: Int, val gpValue: Double,
                       val notes: List<List<String>> = emptyList(),
                       val userNotes: List<List<String>> = emptyList(), var iconVariant : Int = 0 ){ //TODO Refactor lists to be more flat

    enum class MagicItemType(val description: String) {

        A1("DEFAULT TYPE"),
        A2("Potions and Oils"),
        A3("Scrolls"),
        A4("Rings"),
        A5("Rods"),
        A6("Staves"),
        A7("Wands"),
        A8("Misc: Books, etc."),
        A9("Misc: Jewels, etc."),
        A10("Misc: Cloaks, etc."),
        A11("Misc: Boots, etc."),
        A12("Misc: Girdles, etc."),
        A13("Misc: Bags, etc."),
        A14("Misc: Dusts, etc."),
        A15("Misc: Household"),
        A16("Misc: Musical"),
        A17("Misc: Weird"),
        A18("Armor and Shields"),
        A21("Weapons"),
        A24("Artifacts/Relics");
    }
}