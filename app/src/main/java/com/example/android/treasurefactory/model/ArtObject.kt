package com.example.android.treasurefactory.model

import androidx.room.*
import com.example.android.treasurefactory.capitalized
import org.jetbrains.annotations.NotNull
import kotlin.random.Random

@Entity(tableName = "hackmaster_art_table")
data class ArtObject(
    @PrimaryKey(autoGenerate = true) @NotNull val artID: Int,
    val hoardID: Int, // NOTE: removed iconID; TODO refactor accordingly
    val creationTime: Long,
    var name: String, // NOT: added name field; TODO refactor accordingly
    val artType: Int,
    val renown: Int,
    val size: Int,
    val condition: Int,
    val materials: Int,
    val quality: Int,
    val age: Int,
    val subject: Int,
    var valueLevel: Int,
    var gpValue: Double = 0.0) {

    @Ignore
    fun generateNewName() { name = getRandomName(artType, subject) }

    @Ignore
    fun getArtTypeAsString() : String {

        val typeIntToString = mapOf(
            0 to "paper art",
            1 to "fabric art",
            2 to "furnishing",
            3 to "painting",
            4 to "scrimshaw & woodwork",
            5 to "ceramics",
            6 to "glasswork",
            7 to "stonework",
            8 to "metalwork",
            9 to "magical artwork"
        )

        return typeIntToString.getOrDefault(artType,"artwork")
    }

    @Ignore
    fun getRenownAsString() : String {

        val renownIntToString = mapOf(
            -3 to "unknown",
            -2 to "obscure",
            -1 to "city-renowned",
            0 to "regionally-renowned",
            1 to "nationally-renowned",
            2 to "continentally-renowned",
            3 to "world-renowned",
            4 to "movement-leading"
        )

        return renownIntToString.getOrDefault(renown,"artwork")
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
    fun getMaterialsAsString() : String {

        val materialsIntToString = mapOf(
            -3 to "awful",
            -2 to "poor",
            -1 to "below average",
            0 to "average",
            1 to "above average",
            2 to "good",
            3 to "excellent",
            4 to "finest",
            5 to "unique"
        )

        return materialsIntToString.getOrDefault(materials,"inscrutable")
    }

    @Ignore
    fun getQualityAsString() : String {

        val qualityIntToString = mapOf(
            -3 to "awfully executed",
            -2 to "poorly executed",
            -1 to "below average execution",
            0 to "average execution",
            1 to "above average execution",
            2 to "good execution",
            3 to "excellent execution",
            4 to "brilliant execution",
            5 to "masterpiece"
        )

        return qualityIntToString.getOrDefault(quality,"divisively executed")
    }

    @Ignore
    fun getAgeInYearsAsRank() : Int {

        return if (age >= 5) {
            when (age) {

                in 5..25        -> -2
                in 26..75       -> -1
                in 76..150      -> 0
                in 151..300     -> 1
                in 301..600     -> 2
                in 601..1500    -> 3
                in 1501..3000   -> 4
                else            -> 5
            }
        } else -2
    }

    @Ignore
    fun getAgeAsString(): String {
        val ageRank = getAgeInYearsAsRank()
        return "$age years old [${if (ageRank < 0) ageRank else "+$ageRank"} value ranks]"
    }

    @Ignore
    fun getConditionAsString() : String {

        val conditionIntToString = mapOf(
            -3 to "badly damaged",
            -2 to "damaged",
            -1 to "worn",
            0 to "average",
            1 to "good",
            2 to "excellent",
            3 to "near perfect",
            4 to "perfect",
            5 to "flawless"
        )

        return conditionIntToString.getOrDefault(condition,"inscrutable")
    }

    @Ignore
    fun getSubjectAsString() : String {

        val subjectIntToString = mapOf(
            -2 to "abstract",
            -1 to "monster",
            0 to "human or demi-human",
            1 to "natural",
            2 to "historical",
            3 to "religious",
            4 to "wealthy/noble",
            5 to "royalty"
        )

        return subjectIntToString.getOrDefault(subject,"ambiguous")
    }

    @Ignore
    fun getSubjectAsRank() : Int = when (subject) {
            in -2..-1   -> subject
            in 4..5     -> subject - 3
            else        -> 0
        }

    @Ignore
    private fun updateValueLevel() {

        valueLevel =
            ( renown + size + condition + quality + getSubjectAsRank() + getAgeInYearsAsRank() )
                .coerceIn(-19,31)
    }

    @Ignore
    fun setGpValueFromLevel() : Double {

        updateValueLevel()

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

        gpValue = (valueLevelToGPValue[valueLevel])!!

        return (valueLevelToGPValue[valueLevel])!!
    }

    companion object {

        @Ignore
        fun getRandomName(medium: Int, focus: Int):String {

            val nameBuilder = StringBuilder()

            // Start with form of artwork
            nameBuilder.append(when (medium) {

                0 -> listOf( // Paper artwork types
                    "calligraphy",
                    "charcoal sketch",
                    "illuminated manuscript",
                    "illustration",
                    "napkin sketch",
                    "origami",
                    "papercraft",
                    "paper collage",
                    "paper print",
                    "papyrus art",
                    "parchment",
                    "sketch"
                ).random()

                1 -> listOf( // Fabric artwork types
                    "arras",
                    "curtains",
                    "cushion",
                    "embroidery",
                    "kilim",
                    "pillowcase",
                    "quilt",
                    "tablecloth",
                    "tapestry",
                    "textile",
                    "vestments",
                    "vintage clothing"
                ).random()

                2 -> listOf( // Furnishing types
                    "armoire",
                    "bed",
                    "cabinet",
                    "candelabra",
                    "candle snuffer",
                    "chair",
                    "lectern",
                    "ottoman",
                    "partition",
                    "table",
                    "table",
                    "throne"
                ).random()

                3 -> listOf( // Painting
                    "acrylic painting",
                    "enamel painting",
                    "fresco",
                    "landscape",
                    "mixed-media painting",
                    "mural",
                    "oil painting",
                    "painting",
                    "portrait",
                    "watercolor painting"
                ).random()

                4 -> listOf( // Scrimshaw/woodwork types
                    "butt (large barrel)",
                    "carved ivory",
                    "carved walking staff",
                    "carving",
                    "comb",
                    "game board",
                    "intarsia",
                    "jewelry box",
                    "marquetry",
                    "scroll tube",
                    "soap carving",
                    "woodburning",
                    "wooden mantelpiece",
                    "wooden engraving",
                    "wooden statue"
                ).random()

                5 -> listOf( // Ceramic types
                    "ceramic idol",
                    "ceramic statue",
                    "clay pot",
                    "offectory container",
                    "ornate crucible",
                    "porcelain dishware",
                    "porcelain tea set",
                    "pottery",
                    "slipware",
                    "terra sigillata",
                    "terracotta",
                    "urn",
                    "vase"
                ).random()

                6 -> listOf( // Glasswork types
                    "blown-glass sculpture",
                    "chandelier",
                    "decanter",
                    "fused-glass art",
                    "glass bottle",
                    "glass bowl",
                    "goblet",
                    "sculpture-in-a-bottle",
                    "shattered glass pile",
                    "stained glass"
                ).random()

                7 -> listOf( // Stonework types
                    "birdbath",
                    "dias",
                    "marble bookend",
                    "marble statue",
                    "sarcophagus",
                    "stone carving",
                    "stone column",
                    "stone pedestal",
                    "stone relief",
                    "stone statue",
                    "stucco"
                ).random()

                8 -> listOf( // Metalwork types
                    "antique armor",
                    "bronze bust",
                    "bronze sculpture",
                    "chimes",
                    "engraved bell",
                    "gilded utensil",
                    "golden idol",
                    "hand mirror",
                    "iron statuette",
                    "medallion",
                    "metallic ornament",
                    "ornate doorknob",
                    "silver tea set",
                    "tin inkwell"
                ).random()

                9 -> listOf( // Magical artwork types
                    "animated statue",
                    "animation",
                    "arcane circle",
                    "audio recording",
                    "divinity-infused reliquary",
                    "drinking bird statue",
                    "enchanted manuscript",
                    "enchanted music box",
                    "ethereal sculpture",
                    "Glassteel artwork",
                    "glowing doohickey",
                    "holographic jewelry",
                    "magical curio",
                    "moving painting",
                    "mystical artwork",
                    "non-Euclidian sculpture",
                    "permanent glamour",
                    "talking bust",
                    "Two D'lusionary screen"
                ).random()

                else -> "artwork"
            })

            nameBuilder.append(" of ")

            when (focus) {

                -2 -> { // Abstract subjects

                    val abstractQuality = listOf(
                        "aesthetics",
                        "beauty",
                        "concept",
                        "cost",
                        "dark side",
                        "divinity",
                        "essence",
                        "explosion",
                        "feeling",
                        "flow",
                        "frivolity",
                        "glory",
                        "hope",
                        "incomprehensibility",
                        "insatiability",
                        "inspiration",
                        "intensity",
                        "inversion",
                        "isolation",
                        "joy",
                        "magic",
                        "mendacity",
                        "mundanity",
                        "nothingness",
                        "opposite",
                        "reality",
                        "resonance",
                        "scent",
                        "shadow",
                        "shape",
                        "sound",
                        "synesthesia",
                        "taste",
                        "tears",
                        "truth",
                        "totality",
                        "vortex",
                        "weight"
                    ).random()

                    val abstractTopic = listOf(
                        "air",
                        "ambition",
                        "art",
                        "capitalism",
                        "charity",
                        "colors",
                        "community",
                        "concepts",
                        "divinity",
                        "duty",
                        "earth",
                        "emotions",
                        "everything",
                        "excess",
                        "fame",
                        "fire",
                        "friendship",
                        "greed",
                        "hate",
                        "ignorance",
                        "kitsch",
                        "knowledge",
                        "loss",
                        "love",
                        "love, laugh, live",
                        "magic",
                        "militarism",
                        "nihilism",
                        "nature",
                        "pacifism",
                        "peace",
                        "petrichor",
                        "philosophy",
                        "serendipity",
                        "shapes",
                        "silence",
                        "socialism",
                        "the future",
                        "the past",
                        "the present",
                        "the unknown",
                        "tragedy",
                        "war",
                        "water",
                        "wealth",
                        "zjierb"
                    ).random()

                    nameBuilder.append("the $abstractQuality of $abstractTopic")
                }

                -1 -> { //Monster subjects

                    val monsterAdjective = listOf(
                        "angry",
                        "clever",
                        "comely",
                        "confused",
                        "crazed",
                        "depraved",
                        "devious",
                        "enlightened",
                        "funny",
                        "grotesque",
                        "grumpy",
                        "happy",
                        "hard-working",
                        "hungry",
                        "lively",
                        "lonely",
                        "purple",
                        "sad",
                        "sloven",
                        "sneaky",
                        "spooky",
                        "stinky",
                        "stupid",
                        "ugly",
                        "undead",
                        "unusual"
                    ).random()

                    val monsterRace = listOf(
                        "basilisk",
                        "centaur",
                        "dragon",
                        "dungeon cat",
                        "gargoyle",
                        "ghoul",
                        "giant",
                        "gnoll",
                        "hobgoblin",
                        "kobold",
                        "manticore",
                        "minotaur",
                        "ogre",
                        "orc",
                        "skeleton",
                        "slobgoblin",
                        "troll",
                        "vampire",
                        "zombie"
                    ).random()

                    val monsterActivity = listOf(
                        "arguing",
                        "at it again",
                        "counting treasure",
                        "eating lunch",
                        "feasting",
                        "frowning",
                        "gathering food",
                        "haggling",
                        "hard at work",
                        "having some fun",
                        "in combat",
                        "in contemplation",
                        "in retreat",
                        "just vibing",
                        "on death's doorstep",
                        "on the hunt",
                        "playing cards",
                        "pointing",
                        "smiling",
                        "socializing",
                        "surrendering",
                        "up to no good",
                        "victorious"
                    ).random()

                    val monsterCount = Random.nextInt(1,8)

                    if (monsterCount > 1) {

                        if (monsterCount > 5) {

                            nameBuilder.append("many")

                        } else {

                            nameBuilder.append(monsterCount.toString())
                        }

                        nameBuilder.append(" $monsterAdjective ${monsterRace}s")

                    } else {

                        nameBuilder.append("$monsterAdjective $monsterRace")
                    }

                    nameBuilder.append(" $monsterActivity")
                }

                0 -> { // Human subject matter

                    val humanAdjective = listOf(
                        "aesthetic",
                        "aloof",
                        "ancient",
                        "attractive",
                        "average",
                        "barbaric",
                        "benevolent",
                        "brilliant",
                        "careless",
                        "clean",
                        "cold-hearted",
                        "cowardly",
                        "curious",
                        "depraved",
                        "dirty",
                        "driven",
                        "dull",
                        "female",
                        "foolhardy",
                        "foppish",
                        "forceful",
                        "friendly",
                        "fussy",
                        "greedy",
                        "grumpy",
                        "hedonistic",
                        "hostile",
                        "hot-headed",
                        "iconoclastic",
                        "imposing",
                        "jealous",
                        "kindly",
                        "lazy",
                        "lusty",
                        "male",
                        "malevolent",
                        "mature",
                        "middle-aged",
                        "militaristic",
                        "modest",
                        "morose",
                        "neurotic",
                        "non-binary",
                        "old",
                        "pious",
                        "purple",
                        "rough",
                        "rude",
                        "scheming",
                        "secretive",
                        "servile",
                        "studious",
                        "suspicious",
                        "ugly",
                        "vengeful",
                        "violent",
                        "virtuous",
                        "young",
                        "youthful"
                    ).random()

                    val humanRace = listOf(
                        "beastkin",
                        "brownie",
                        "dracon",
                        "drow",
                        "drow",
                        "dwarven",
                        "dwarven",
                        "dwarven",
                        "dwarven",
                        "duergar",
                        "elvariel",
                        "elven",
                        "elven",
                        "elven",
                        "elven",
                        "elven",
                        "elven",
                        "fairy",
                        "giff",
                        "gnomish",
                        "gnome titan",
                        "grevan",
                        "Gronnanarian",
                        "human",
                        "human",
                        "human",
                        "human",
                        "human",
                        "human",
                        "human",
                        "half-elven",
                        "half-orcish",
                        "half-orge",
                        "halfling",
                        "lizard man",
                        "nixie",
                        "nymph",
                        "pixie",
                        "pixie-fairy",
                        "pixie-sprite"
                    ).random()

                    val humanProfession = listOf(
                        "acrobat",
                        "adventurer",
                        "animal handler",
                        "alchemist",
                        "archer",
                        "architect",
                        "armorer",
                        "artisan",
                        "athlete",
                        "baker",
                        "bandit",
                        "barber",
                        "begger",
                        "blacksmith",
                        "bowyer",
                        "brewer",
                        "butcher",
                        "candlestick-maker",
                        "carpenter",
                        "cartographer",
                        "chef",
                        "combatant",
                        "cook",
                        "courtesan",
                        "crafter",
                        "crossbowman",
                        "dancer",
                        "doctor",
                        "druid",
                        "engineer",
                        "entertainer",
                        "fighter",
                        "fishmonger",
                        "fortune teller",
                        "gamer",
                        "grifter",
                        "guide",
                        "hunter",
                        "innkeeper",
                        "jeweler",
                        "librarian",
                        "laborer",
                        "lay-about",
                        "leatherworker",
                        "maidservant",
                        "magic-user",
                        "marine",
                        "mason",
                        "merchant",
                        "messenger",
                        "miner",
                        "minstrel",
                        "patron",
                        "philosopher",
                        "plumber",
                        "ranger",
                        "rogue",
                        "saboteur",
                        "sailor",
                        "sage",
                        "scholar",
                        "scribe",
                        "sellsword",
                        "shipwright",
                        "slave",
                        "slinger",
                        "smith",
                        "spellslinger",
                        "sprinter",
                        "steward",
                        "student",
                        "tailor",
                        "tattoo artist",
                        "trendsetter",
                        "troublemaker",
                        "warrior",
                        "wastrel",
                        "weaver",
                        "worker"
                    ).random()

                    val humanActivity = listOf(
                        "arguing",
                        "at a ceremony",
                        "at a hot spring",
                        "at it again",
                        "at rest",
                        "attacking",
                        "being total bros",
                        "building/creating",
                        "celebrating",
                        "cleaning up",
                        "contemplating",
                        "conversing",
                        "creating art",
                        "defeated",
                        "defending",
                        "devastated",
                        "drinking",
                        "eating",
                        "eating a book",
                        "engrossed",
                        "fighting",
                        "haggling",
                        "hanging out",
                        "hard at work",
                        "helping out",
                        "holding fruit",
                        "in danger",
                        "in high spirits",
                        "in mortal combat",
                        "in pursuit",
                        "investigating",
                        "just vibing",
                        "laughing",
                        "looking really cool",
                        "on alert",
                        "performing live",
                        "pining",
                        "playing chess",
                        "playing",
                        "preparing for battle",
                        "questioning",
                        "reading a book",
                        "showing off",
                        "sitting still",
                        "slacking off",
                        "striking a pose",
                        "studying",
                        "trying something new",
                        "up to no good",
                        "victorious",
                        "waking up"
                    ).random()

                    val humanCount = Random.nextInt(1,13) - 6

                    if (humanCount > 1) {

                        if (humanCount > 5) {

                            nameBuilder.append("many")

                        } else {

                            nameBuilder.append(humanCount.toString())
                        }

                        nameBuilder.append(" $humanAdjective $humanRace ${humanProfession}s")

                    } else {

                        nameBuilder.append("$humanAdjective $humanRace $humanProfession")
                    }

                    nameBuilder.append(" $humanActivity")
                }

                1 -> { // Natural subject matter

                    when (Random.nextInt(1,4)) {

                        1   -> { // Creature focus

                            val creatureAdjective = listOf(
                                "arcane",
                                "blood",
                                "boneless",
                                "bored",
                                "carrion",
                                "celestial",
                                "civilized",
                                "corpulent",
                                "crested",
                                "cute",
                                "desert",
                                "dire",
                                "doom",
                                "draconic",
                                "elder",
                                "electric",
                                "feral",
                                "freshwater",
                                "giant",
                                "gnarl-toothed",
                                "great",
                                "hissing",
                                "horned",
                                "iron",
                                "jungle",
                                "jurassic",
                                "large",
                                "lowland",
                                "mountain",
                                "nefarious",
                                "phantom",
                                "poisonous",
                                "predatory",
                                "primodrial",
                                "purple",
                                "regular",
                                "rock",
                                "rot",
                                "sabre-toothed",
                                "saltwater",
                                "shadow",
                                "silver-backed",
                                "sinister",
                                "sleepy",
                                "small",
                                "subterranean",
                                "tiny",
                                "tundra",
                                "ugly",
                                "urban",
                                "were -",
                                "winged",
                                "woodland"
                            ).random()

                            val creatureCount = Random.nextInt(8).takeIf { it < 4 } ?: 1

                            val creatureType = listOf(
                                "ape" to "apes",
                                "bat" to "bats",
                                "bear" to "bears",
                                "beaver" to "beavers",
                                "beetle" to "beetles",
                                "bird" to "birds",
                                "boar" to "boars",
                                "butterfly" to "butterflies",
                                "cat" to "cats",
                                "chicken" to "chickens",
                                "crab" to "crabs",
                                "dawg" to "dawgs",
                                "deer" to "deer",
                                "dolphin" to "dolphins",
                                "eagle" to "eagles",
                                "elephants" to "elephants",
                                "elk" to "elks",
                                "falcon" to "falcons",
                                "ferret" to "ferrets",
                                "fish" to "fish",
                                "fox" to "foxes",
                                "frog" to "frogs",
                                "goat" to "goats",
                                "goose" to "geese",
                                "gorilla" to "gorillas",
                                "hawk" to "hawks",
                                "hedgehawg" to "hedgehawgs",
                                "hippo" to "hippos",
                                "horse" to "horses",
                                "lion" to "lions",
                                "mammoth" to "mammoths",
                                "mongoose" to "mongooses",
                                "monkey" to "monkeys",
                                "moose" to "moose",
                                "mouse" to "mice",
                                "owl" to "owls",
                                "ox" to "oxen",
                                "rabbit" to "rabbits",
                                "rat" to "rats",
                                "rhino" to "rhinos",
                                "shark" to "sharks",
                                "skunk" to "skunks",
                                "snake" to "snakes",
                                "songbird" to "songbirds",
                                "spider" to "spiders",
                                "squirrel" to "squirrels",
                                "stag" to "stags",
                                "stoat" to "stoats",
                                "tiger" to "tigers",
                                "turtle" to "turtles",
                                "unicorn" to "unicorns",
                                "whale" to "whales",
                                "wolf" to "wolves",
                                "worm" to "worms"
                            ).random()

                            val creatureAction = listOf(
                                "answering summon",
                                "answering the call",
                                "at it again",
                                "being cute",
                                "being hunted",
                                "being menacing",
                                "dawgpiling",
                                "drinking",
                                "eating",
                                "embarrassing an adventurer",
                                "feeding young",
                                "fighting",
                                "grazing",
                                "in pursuit",
                                "investigating",
                                "just vibin'",
                                "looking hella sweet",
                                "mating",
                                "on lookout",
                                "on the hunt",
                                "playing cards",
                                "resting",
                                "sleeping",
                                "staring blankly",
                                "up to no good",
                                "waking up",
                                "with a druid",
                                "with big ol' eyes"
                            ).random()

                            if (creatureCount > 1){
                                nameBuilder.append("$creatureCount $creatureAdjective ${creatureType.second} $creatureAction")
                            } else {
                                nameBuilder.append("$creatureAdjective ${creatureType.first} $creatureAction")
                            }
                        }

                        2   -> { // Object focus

                            val objectAdjective = listOf(
                                "abnormal",
                                "blossoming",
                                "calm",
                                "clean",
                                "cool",
                                "crooked",
                                "dead",
                                "delicious",
                                "dirty",
                                "earthy",
                                "extraplanar",
                                "fallen",
                                "flecked",
                                "fresh",
                                "gilded",
                                "gleaming",
                                "hardened",
                                "petrified",
                                "phosphorescent",
                                "plump",
                                "pressed",
                                "pretty",
                                "purple",
                                "repulsive",
                                "stale",
                                "supernatural",
                                "turbulent",
                                "undead",
                                "verdant",
                                "warm",
                                "whispering",
                                "withering",
                                "zaftig"
                            ).random()

                            val objectCount = (Random.nextInt(1,16) - 5).takeIf { it < 11 } ?: 1

                            val objectNoun = listOf(
                                "acorn" to "acorns",
                                "antler" to "antlers",
                                "apple" to "apples",
                                "berry" to "berries",
                                "birch tree" to "birch trees",
                                "bone" to "bones",
                                "boulder" to "boulders",
                                "bramble" to "brambles",
                                "carcass" to "carcasses",
                                "corn cob" to "corn cobs",
                                "daisy" to "daisies",
                                "feather" to "feathers",
                                "flower" to "flowers",
                                "footprint" to "animal tracks",
                                "forget-me-not" to "forget-me-nots",
                                "fruit tree" to "fruit trees",
                                "fruit" to "various fruits",
                                "geode" to "geodes",
                                "hive" to "hives",
                                "leaf" to "leaves",
                                "lily" to "lilies",
                                "melon" to "melons",
                                "mushroom" to "mushrooms",
                                "oak tree" to "oak trees",
                                "orchid" to "orchids",
                                "peony" to "peonies",
                                "pine cone" to "pine cones",
                                "pine tree" to "pine trees",
                                "rose" to "roses",
                                "shrubbery" to "shrubs",
                                "snow drift" to "snowdrifts",
                                "tree" to "trees",
                                "vine" to "vines",
                                "weeping willow" to "weeping willows"
                            ).random()

                            if (objectCount > 1){
                                nameBuilder.append("${if(objectCount>5) "many" else objectCount} $objectAdjective ${objectNoun.second}")
                            } else {
                                nameBuilder.append("$objectAdjective ${objectNoun.first}")
                            }
                        }

                        else-> { // Phenomenon focus

                            val phenomAdjective = listOf(
                                "abandoned",
                                "amber",
                                "breezy",
                                "calamitious",
                                "calm",
                                "clear",
                                "cold",
                                "colorful",
                                "colorless",
                                "comforting",
                                "cool",
                                "dancing",
                                "dank",
                                "dry",
                                "dull",
                                "exotic",
                                "foggy",
                                "foreboding",
                                "fruity",
                                "glistening",
                                "happy little",
                                "hazy",
                                "hot",
                                "layered",
                                "loud",
                                "magical",
                                "misty",
                                "ominous",
                                "purple",
                                "quiet",
                                "secret",
                                "sharp",
                                "solid",
                                "variegated",
                                "verdant",
                                "violent",
                                "warm",
                                "wet",
                                "wonderous"
                            ).random()

                            val phenomObject = listOf(
                                "Aubrey knot",
                                "aurora",
                                "canyon",
                                "cave",
                                "cavern",
                                "cliffside",
                                "clouds",
                                "desert",
                                "eclipse",
                                "forest",
                                "garden",
                                "geyser",
                                "glacier",
                                "grove",
                                "hot spring",
                                "inferno",
                                "jungle",
                                "lake",
                                "ley line",
                                "mesa",
                                "mountain",
                                "oasis",
                                "ocean",
                                "pond",
                                "preserve",
                                "ravine",
                                "river",
                                "sanctuary",
                                "steppe",
                                "stream",
                                "swamp",
                                "tornado",
                                "valley",
                                "whirlpool"
                            ).random()

                            val phenomTime = listOf(
                                "at midnight",
                                "at dusk",
                                "at sunrise",
                                "at dawn",
                                "in the morning",
                                "at noon",
                                "in the afternoon",
                                "at sunset",
                                "in the evening",
                                "in twilight",
                                "after dark",
                                "at night",
                                "in summer",
                                "in autumn",
                                "in winter",
                                "in spring"
                            ).random()

                            if (Random.nextBoolean()) { // Randomly use time of day
                                nameBuilder.append("$phenomAdjective $phenomObject $phenomTime")
                            } else {
                                nameBuilder.append("$phenomAdjective $phenomObject")
                            }
                        }

                    }
                }

                2 -> { // Historical subject matter

                    nameBuilder.append(listOf(
                        "a major battle",
                        "a new technology discovered",
                        "accomplishment of a great person",
                        "ancient elven war",
                        "apotheosis of folk hero",
                        "army marching to battle",
                        "ascension of a HackMaster",
                        "ascension of a protege",
                        "battle against a great wyrm",
                        "birth of a hero",
                        "brave last stand",
                        "canal opening",
                        "capital city founding",
                        "castle under construction",
                        "coronation of sovereign",
                        "discovery of natural wonder",
                        "discovery of new lands",
                        "establishment of colony",
                        "first contact",
                        "first meeting of two cultures",
                        "founding of academy",
                        "founding of city",
                        "founding of guild",
                        "founding of the order",
                        "general surrendering",
                        "great evil vanquished",
                        "historic wedding",
                        "important assassination",
                        "infamous massacre",
                        "knighting ceremony",
                        "legendary performance",
                        "liberation of city",
                        "life of a HackMaster",
                        "national epic",
                        "razing of city",
                        "set piece battle",
                        "signing of treaty",
                        "terrible cataclysm",
                        "the first magic spell",
                        "tournament finale",
                        "world wonder built"
                    ).random())
                }

                3 -> { // Religious subject matter

                    when (Random.nextInt(1,4)) {

                        1   -> { // Person

                            val personIsMale = Random.nextBoolean()
                            val personTitle = if (personIsMale) {
                                listOf(
                                    "Abbot",
                                    "Apostle",
                                    "Archbishop",
                                    "Archdruid",
                                    "Bishop",
                                    "Brother",
                                    "Cantor",
                                    "Cardinal",
                                    "Deacon",
                                    "Father",
                                    "Guru",
                                    "High Priest",
                                    "Knight Templar",
                                    "Patriarch",
                                    "Pontiff",
                                    "Prelate",
                                    "Priest",
                                    "Prior",
                                    "Prophet",
                                    "Prophet",
                                    "Rabbi",
                                    "Rector",
                                    "Reverend",
                                    "Saint",
                                    "Shaman",
                                    "Vicar",
                                    "Holy WarLord of the 1st Order"
                                ).random()
                            } else {
                                listOf(
                                    "Abbess",
                                    "Apostle",
                                    "Chosen One",
                                    "Deacon",
                                    "Druid",
                                    "Guru",
                                    "Hierophant",
                                    "High Priestess",
                                    "Holy Maiden",
                                    "Hospitaller",
                                    "Matriarch",
                                    "Mother",
                                    "Oracle",
                                    "Priestess",
                                    "Prioress",
                                    "Prophet",
                                    "Rabbi",
                                    "Reverend",
                                    "Saint",
                                    "Shaman",
                                    "Sister",
                                    "Virgin",
                                    "Holy WarLady of the 3rd Order"
                                ).random()
                            }

                            val personName = if (personIsMale){
                                listOf(
                                    "Cuthbert",
                                    "Boniface",
                                    "Thomas",
                                    "Benedict",
                                    "Edmund",
                                    "Hugh",
                                    "Gilbert",
                                    "John",
                                    "Patrick",
                                    "Oswald",
                                    "Theodore",
                                    "Vincent",
                                    "Louis",
                                    "Strength",
                                    "Odo",
                                    "Godot",
                                    "Gregory",
                                    "Maximillion",
                                    "Justin",
                                    "Henry",
                                    "Godwin",
                                    "Walter",
                                    "Rudolph",
                                    "Adam",
                                    "Polybius",
                                    "Stephen",
                                    "Oliver",
                                    "Paul",
                                    "John Paul",
                                    "Urban",
                                    "Gabriel",
                                    "Luke",
                                    "Abraham"
                                ).random()
                            } else {
                                listOf(
                                    "Joan",
                                    "Colette",
                                    "Rose",
                                    "Marigold",
                                    "Lilith",
                                    "Candice",
                                    "Hildegard",
                                    "Clementine",
                                    "Matilda",
                                    "Grace",
                                    "Catherine",
                                    "Kristina",
                                    "Eve",
                                    "Margaret",
                                    "Mary",
                                    "Theophany",
                                    "Agatha",
                                    "Kim",
                                    "Jadwiga",
                                    "Cecilia",
                                    "Olga",
                                    "Bridgette",
                                    "Veronica",
                                    "Ursula"
                                ).random()
                            }

                            val personLineage = listOf(
                                "",
                                "",
                                "",
                                "",
                                "",
                                " I",
                                " II",
                                " II",
                                " III",
                                " VI",
                                " V"
                            ).random()

                            val personNickname = listOf(
                                "Anointed",
                                "Avatar",
                                "Barbarian",
                                "Beautiful",
                                "Chosen One",
                                "Civilized",
                                "Club-footed",
                                "Courageous",
                                "Crusader",
                                "Diplomat",
                                "Dogmatic",
                                "Earnest",
                                "Enabler",
                                "Enlightened",
                                "Generous",
                                "HacKleric",
                                "Healer",
                                "Heretic",
                                "Historian",
                                "Hypocrite",
                                "Ill-fated",
                                "Ill-informed",
                                "Inquisitor",
                                "Inspired",
                                "Lowborn",
                                "Martyr",
                                "Mesmerizing",
                                "Messenger",
                                "Missionary",
                                "Motivator",
                                "Noble",
                                "Orator",
                                "Outlaw",
                                "Pacifist",
                                "Philosopher",
                                "Pilgrim",
                                "Pious",
                                "Plump",
                                "Politician",
                                "Proselytizer",
                                "Puritanical",
                                "Sagacious",
                                "Savage",
                                "Savior",
                                "Scholar",
                                "Shepherd",
                                "Templebuilder",
                                "Theologian",
                                "Translator",
                                "Undead Slayer",
                                "Wicked",
                                "Wise"
                            ).random()

                            nameBuilder.append("$personTitle $personName$personLineage the $personNickname")

                        }

                        2   -> { // Event/Object/Place

                            listOf(
                                "a valkyrie",
                                "Acheron",
                                "Amaterasu and the cave",
                                "Arcadia",
                                "Coyote killing a giant",
                                "Elysium",
                                "Gehennah",
                                "Hades (the plane)",
                                "Happy Hunting Ground",
                                "Hunahpu and Xbalanque in Xibalba",
                                "Kyrnn Age of Might",
                                "Limbo",
                                "Loki's wager",
                                "Moradin forging the first dwarves",
                                "Nine Hells",
                                "Nirvana",
                                "Olympus",
                                "Pan-Demonium",
                                "Pangrus batting a Rotgut",
                                "Prometheus bringing fire to humanity",
                                "Ragnarok",
                                "Seven Heavens",
                                "Tartarus",
                                "Tuonela, the land of the dead",
                                "Twin Paradises",
                                "Tír na nÓg",
                                "Vigrid",
                                "Gilgamesh and Enkidu",
                                "contest of Enki and Ninmah",
                                "journey through the underworld",
                                "the Abyss",
                                "the Battle at Mag Tuired",
                                "the Epoch of Myth",
                                "the Jade Emperor's Great Race",
                                "the Natra-Kor (Mother Stone)",
                                "the Second Sundering",
                                "the Seelie Court",
                                "the Seldarine",
                                "the War of the Shadow",
                                "the War of the Tablet of the Ages",
                                "the Zelaurian creation story",
                                "the exodus of the evangelists",
                                "the resurrection of Osiris",
                                "the tale of Orpheus",
                                "the treachery of Yi'Gor",
                                "the trial of Dela Menyor",
                                "12 Labors of Herakles"
                            ).random().also { nameBuilder.append(it) }

                        }

                        else-> { // Diety

                            listOf(
                                "Benyar, Gawd of Empire",
                                "Kazaar-Freem, Gawd of Peace and Tranquility",
                                "Luvia, Gawd of Justice",
                                "Sumar-Fareen, Gawdess of Birth and Love",
                                "Zeus the Diminished, Gawd of Lightning",
                                "The Feeble Gawd, Gawd of Mysteries",
                                "Marlog, Gawd of Sailing and Sailors",
                                "Shang-Ti, Gawd of Sky and Agriculture",
                                "Shona, Gawdess of Games and Ritual Combat",
                                "Skraad, Gawd of Blacksmiths and Fate",
                                "Thrain, Gawd of Wisdom and Mountaineering",
                                "Druga, Gawd of Devils and Oppresive Contracts",
                                "Fracor'Dieus, Gawd of Earth",
                                "Gruumsh, Gawd of Orcs",
                                "Loviatar, Gawdess of Pain and Torment",
                                "Set, Gawd of the Night",
                                "Denier, Gawd of Art and Literature",
                                "Enlil, Gawd of Air and War",
                                "Kishijoten, Gawdess of Luck",
                                "Lathander, Gawd of the Spring/Beginnings",
                                "Nudor, Gawd of Healing",
                                "Aknar, Gawd of Stealth and Wolves",
                                "Camaxtli, Gawd of Fate",
                                "Hokalas the RiftMaster, Gawd of Magic",
                                "Ikka Putaang, Gawd of Nature",
                                "Oghma, Gawd of Knowledge",
                                "Alu the Locust Lord, Gawd of Famine",
                                "Grawdyng, Gawd of Death",
                                "P'Rakeke, Emporer of Scorn, Gawd of Bigoty and Hate",
                                "Pyremius, Gawd of Fire, Poison, and Disease",
                                "Tobadzistini, Gawd of Warriors",
                                "Bast, Gawdess of Felines",
                                "Markovia, Gawd of Oceans",
                                "Navinger, Gawd of Love and Eunuchs",
                                "Nephthys, Gawdess of Tombs",
                                "Thor, Gawd of Thunder",
                                "Zelaur, Gawd of Honor",
                                "Draper, Gawd of Stealth and Cunning",
                                "Mangrus, Gawd of Disease",
                                "Odin, Gawd of War",
                                "Pangrus, gnomish Gawd of War",
                                "Par'Kyrus, Gawd of Wind",
                                "Arnuya, Gawd of Vengence",
                                "Gronyfr, Gawd of War and Grevans",
                                "Kuchooloo, Gawd of Wanton Destruction",
                                "Yiders, Gawd of Strength",
                                "Yi'Gor, Gawd of Treachery",
                                "Athena, Gawdess of Wisdom and Combat",
                                "Laerme, Gawdess of Fire, Art, and Love",
                                "Laduguer, Gawd of Gray Dwarves and Skilled Artisans",
                                "Hanili Celanil, Gawdess of Love, Romance, Beauty, & Fine Arts",
                                "Coyote, Gawd of Arts, Crafts, Fire, and Thieves",
                                "Pinini the Raconteur, Gawd of the Arts",
                                "Eilistraee, Demi-Gawdess of Moonlight, Beauty, et al.",
                                "Moradin, Gawd of Dwarves",
                                "Corellon Larethian, Gawd of Elves, Music, Poetry, and Magic",
                                "Garl Glittergold, Gawd of Dwarves",
                                "Dionysus, Gawd of Wine",
                                "Yondalla, Gawdess of Halflings and Fertility",
                                "Quetzalcoatl, Gawd of Arts and Air",
                                "The Confuser of Ways, Gawd of Lies, Deceit, and Mischief",
                                "Rotovi the Mule, Gawd of Mathematics, Science, et al."
                            ).random().also { nameBuilder.append(it) }
                        }
                    }
                }

                4 -> { // Wealthy/noble subject matter

                    val nobleIsMale = Random.nextBoolean()

                    val nobleTitle = if (nobleIsMale){
                        listOf(
                            "Ambassador",
                            "Arch-Mage",
                            "Archon",
                            "Baron",
                            "Baronet",
                            "Captain",
                            "Castellan",
                            "Chairman",
                            "Chamberlain",
                            "Chancellor",
                            "Chief Eunuch",
                            "Chief",
                            "Commander",
                            "Consul",
                            "Count",
                            "Court Wizard",
                            "Dean",
                            "Doge",
                            "Earl",
                            "General",
                            "Governor",
                            "Grandmaster",
                            "Guildmaster",
                            "Justice",
                            "Lord",
                            "Lord President",
                            "Marquis",
                            "Mayor",
                            "Minister",
                            "Professor",
                            "Sir",
                            "Spymaster",
                            "Treasure Hunter",
                            "Treasurer",
                            "Vice President"
                        ).random()
                    } else {
                        listOf(
                            "Ambassador",
                            "Arch-Mage",
                            "Baroness",
                            "Chairwoman",
                            "Chamberlain",
                            "Clan Mother",
                            "Commander",
                            "Countess",
                            "Court Tutor",
                            "Court Wizard",
                            "Dame",
                            "Doge",
                            "Emissary",
                            "General",
                            "Grandmaster",
                            "Guildmistress",
                            "Justice",
                            "Lady",
                            "Madame President",
                            "Marquise",
                            "Master Bard",
                            "Preceptress",
                            "Professor",
                            "Spymaster",
                            "Stewardess",
                            "Treasure Huntress",
                            "Vice President"
                        ).random()
                    }

                    val nobleName = if (nobleIsMale){
                        listOf(
                            "Albert",
                            "Alexander",
                            "Alfred",
                            "Arthur",
                            "Augustus",
                            "Boris",
                            "Charles",
                            "Cyrus",
                            "Edmund",
                            "Edward",
                            "Franklin",
                            "Frederick",
                            "George",
                            "Giovanni",
                            "Harald",
                            "Harry",
                            "Igor",
                            "James",
                            "John",
                            "João",
                            "Leopold",
                            "Louis",
                            "Marcus",
                            "Matthias",
                            "Nicholas",
                            "Patrick",
                            "Pedro",
                            "Peter",
                            "Robert",
                            "Simón",
                            "Theodore",
                            "Usidore",
                            "Vladimir",
                            "Wilfrid",
                            "William"
                        ).random()
                    } else {
                        listOf(
                            "Alice",
                            "Amelia",
                            "Anastasia",
                            "Anne",
                            "Beatrice",
                            "Blanche",
                            "Catherine",
                            "Cecilla",
                            "Charlotte",
                            "Diana",
                            "Eleanor",
                            "Elizabeth",
                            "Gloria",
                            "Helen",
                            "Isabella",
                            "Julia",
                            "Kristina",
                            "Lidelle",
                            "Madeleine",
                            "Mary",
                            "Natalia",
                            "Odelia",
                            "Olga",
                            "Persephone",
                            "Sophia",
                            "Tiffany",
                            "Victoria",
                            "Wilhelmina"
                        ).random()
                    }

                    val noblePlacement = listOf(
                        "",
                        "",
                        "",
                        "",
                        " Sr.",
                        " Jr.",
                        " II",
                        " III",
                        " IV",
                        " V",
                        " VI"
                    ).random()

                    val nobleNickname = listOf(
                        "Administrator",
                        "Affable",
                        "Ambitious",
                        "Architect",
                        "August",
                        "Avaricious",
                        "Awesome",
                        "Benevolent",
                        "Blue",
                        "Bossy",
                        "Brave",
                        "Brilliant",
                        "Charming",
                        "Chronicler",
                        "Conqueror",
                        "Crow",
                        "Curious",
                        "Cynical",
                        "Diligent",
                        "Diplomat",
                        "Elegant",
                        "Enlightened",
                        "Flexible",
                        "Gallant",
                        "Generous",
                        "Guardian",
                        "HackMaster",
                        "Hard Ruler",
                        "Historian",
                        "Honorable",
                        "Impaler",
                        "Industrious",
                        "Just",
                        "Kind",
                        "Lawgiver",
                        "Logistician",
                        "Magnanimous",
                        "Magnificent",
                        "Melancholic",
                        "Noble",
                        "Organizer",
                        "Overseer",
                        "Paranoid",
                        "Peacemaker",
                        "Pensive",
                        "Rowdy",
                        "Sage",
                        "Schemer",
                        "Scholar",
                        "Seducer",
                        "Shadow",
                        "Sly",
                        "Snorer",
                        "Spider",
                        "Strategist",
                        "Tactician",
                        "Theologian",
                        "Thrifty",
                        "Torturer",
                        "Truthseeker",
                        "Unready",
                        "Valiant",
                        "Vengeful",
                        "Well-endowed",
                        "Whisperer",
                        "Whole of Body",
                        "Wizard of the 12th Realm of Ephysiyies, etc."
                    ).random()

                    nameBuilder.append("$nobleTitle ${nobleName}$noblePlacement" +
                            " the $nobleNickname")
                }

                5 -> { // Royalty subject matter

                    val royalIsMale = Random.nextBoolean()

                    val royalTitle = if (royalIsMale){
                        listOf(
                            "Emperor",
                            "Kaiser",
                            "Tsar",
                            "High King",
                            "King",
                            "Archduke",
                            "Duke",
                            "Crown Prince",
                            "Prince",
                            "High Sorcerer"
                        ).random()
                    } else {
                        listOf(
                            "Empress",
                            "Kaiserin",
                            "Tsarina",
                            "High Queen",
                            "Queen",
                            "Archduchess",
                            "Duchess",
                            "Crown Princess",
                            "Princess",
                            "High Sorceress"
                        ).random()
                    }

                    val royalName = if (royalIsMale){
                        listOf(
                            "Albert",
                            "Alexander",
                            "Alfred",
                            "Arthur",
                            "Augustus",
                            "Boris",
                            "Charles",
                            "Cyrus",
                            "Edmund",
                            "Edward",
                            "Franklin",
                            "Frederick",
                            "George",
                            "Giovanni",
                            "Harald",
                            "Harry",
                            "Igor",
                            "James",
                            "John",
                            "João",
                            "Leopold",
                            "Louis",
                            "Marcus",
                            "Matthias",
                            "Nicholas",
                            "Patrick",
                            "Pedro",
                            "Peter",
                            "Robert",
                            "Simón",
                            "Theodore",
                            "Vladimir",
                            "Wilfrid",
                            "William"
                        ).random()
                    } else {
                        listOf(
                            "Alice",
                            "Amelia",
                            "Anastasia",
                            "Anne",
                            "Beatrice",
                            "Blanche",
                            "Catherine",
                            "Cecilla",
                            "Charlotte",
                            "Diana",
                            "Eleanor",
                            "Elizabeth",
                            "Gloria",
                            "Helen",
                            "Isabella",
                            "Julia",
                            "Kristina",
                            "Madeleine",
                            "Mary",
                            "Natalia",
                            "Odelia",
                            "Olga",
                            "Sophia",
                            "Tiffany",
                            "Victoria",
                            "Wilhelmina"
                        ).random()
                    }

                    val royalPlacement = listOf(
                        "",
                        "",
                        "",
                        "",
                        " Sr.",
                        " Jr.",
                        " II",
                        " III",
                        " IV",
                        " V",
                        " VI",
                        " VII"
                    ).random()

                    val royalNickname = listOf(
                        "Administrator",
                        "Affable",
                        "Ambitious",
                        "Architect",
                        "August",
                        "Avaricious",
                        "Benevolent",
                        "Bossy",
                        "Brave",
                        "Brilliant Strategist",
                        "Charismatic Negotiator",
                        "Charming",
                        "Chronicler",
                        "Conqueror",
                        "Crow",
                        "Curious",
                        "Cynical",
                        "Diligent",
                        "Diplomat",
                        "Elegant",
                        "Elusive Shadow",
                        "Enlightened",
                        "Famous Champion",
                        "Gallant",
                        "Generous",
                        "Great",
                        "Guardian",
                        "HacKleric",
                        "HackFighter",
                        "HackMage",
                        "HackMaster",
                        "Hard Ruler",
                        "Historian",
                        "Impaler",
                        "Industrious",
                        "Just",
                        "Kind",
                        "Lawgiver",
                        "Legendary",
                        "Magnanimous",
                        "Magnificent",
                        "Mastermind Philosopher",
                        "Midas Touched",
                        "Most Serene",
                        "Noble",
                        "Overseer",
                        "Paragon",
                        "Paranoid",
                        "Peacemaker",
                        "Pensive",
                        "Purple",
                        "Rowdy",
                        "Sagacious",
                        "Schemer",
                        "Scholar",
                        "Seducer",
                        "Shadow",
                        "Sly",
                        "Spider",
                        "Strategist",
                        "Tactician",
                        "Terrible",
                        "Theologian",
                        "Torturer",
                        "Truthseeker",
                        "Valiant",
                        "Vengeful",
                        "Whisperer",
                        "Whole of Body"
                    ).random()

                    nameBuilder.append("$royalTitle ${royalName}$royalPlacement" +
                            " the $royalNickname")
                }

                else -> nameBuilder.append("something")
            }
            return nameBuilder.toString().capitalized()
        }
    }
}