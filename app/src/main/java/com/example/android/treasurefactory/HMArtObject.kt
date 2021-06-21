package com.example.android.treasurefactory

data class HMArtObject(val artType: ArtType, val renown: Renown, val size: Size,
                     val condition: Condition, val materials: Materials, val quality: Quality,
                     val age: Age, val subject: Subject, val valueLevel: Int) {


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
    data class Age(val valueMod: Int, val ageInYears: Int)
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

    val gpValue = valueLevelToGPValue[valueLevel]

}