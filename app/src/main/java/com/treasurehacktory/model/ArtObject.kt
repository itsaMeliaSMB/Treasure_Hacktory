package com.treasurehacktory.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.treasurehacktory.capitalized
import org.jetbrains.annotations.NotNull
import kotlin.math.roundToInt
import kotlin.random.Random

@Entity(tableName = "hackmaster_art_table")
data class ArtObject(
    @PrimaryKey(autoGenerate = true) @NotNull val artID: Int,
    val hoardID: Int,
    val creationTime: Long,
    var name: String,
    val artType: Int,
    val renown: Int,
    val size: Int,
    val condition: Int,
    val materials: Int,
    val quality: Int,
    val age: Int,
    val subject: Int,
    var valueLevel: Int,
    var gpValue: Double = 0.0,
    val isForgery: Boolean = false,
    val originalName: String
) {

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
    fun getArtTypeAsIconString() : String {

        val typeIntToString = mapOf(
            0 to "artwork_paper",
            1 to "artwork_fabric",
            2 to "artwork_furnishing",
            3 to "artwork_painting",
            4 to "artwork_wood",
            5 to "artwork_ceramic",
            6 to "artwork_glass",
            7 to "artwork_stone",
            8 to "artwork_metal",
            9 to "artwork_magical"
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
        return "~$age years old"
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
    fun getFlavorTextAsDetailsList(): Pair<String,List<LabelledQualityEntry>> {
        return "Artwork details" to listOf(
            LabelledQualityEntry("Authenticity",
                if (isForgery) { "Forgery" } else { "Genuine" }),
            LabelledQualityEntry("Type of artwork",
                getArtTypeAsString().capitalized()),
            LabelledQualityEntry("Artist's renown",
                getRenownAsString().capitalized()),
            LabelledQualityEntry("Size",
                getSizeAsString().capitalized()),
            LabelledQualityEntry("Quality of materials used",
                getMaterialsAsString().capitalized()),
            LabelledQualityEntry("Quality of work",
                getQualityAsString().capitalized()),
            LabelledQualityEntry("Age when found", getAgeAsString()),
            LabelledQualityEntry("Condition when found",
                getConditionAsString().capitalized()),
            LabelledQualityEntry("Subject matter depicted",
                getSubjectAsString().capitalized()),
            LabelledQualityEntry("Combined value rank","$valueLevel  = " +
                    "($renown) + ($size) + ($materials) + ($quality) + " +
                    "(${getAgeInYearsAsRank()}) + ($condition) + (${getSubjectAsRank()})")
        )
    }

    @Ignore
    fun toViewableArtObject(effortRating: Double) : ViewableArtObject {

        return ViewableArtObject(
            artID,
            hoardID,
            name,
            "${getSizeAsString().capitalized()}, " +
                    "${getSubjectAsString()} ${getArtTypeAsString()}",
            creationTime,
            getArtTypeAsIconString(),
            when {
                isForgery       -> ItemFrameFlavor.CURSED
                valueLevel > 27 -> ItemFrameFlavor.GOLDEN
                else                -> ItemFrameFlavor.NORMAL },
            "HackJournal #6",
            2,
            gpValue,
            (gpValue / effortRating).roundToInt().coerceAtLeast(0),
            UniqueItemType.ART_OBJECT,
            listOf(getFlavorTextAsDetailsList()),
            originalName,
            artType,
            renown,
            size,
            condition,
            materials,
            quality,
            age,
            subject,
            valueLevel,
            isForgery
        )
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
                    "folding hand fan",
                    "illuminated manuscript",
                    "illustration",
                    "journal",
                    "napkin sketch",
                    "origami",
                    "papercraft",
                    "paper collage",
                    "paper print",
                    "papyrus art",
                    "parchment",
                    "political cartoon",
                    "sketch",
                    "sketchbook"
                ).random()

                1 -> listOf( // Fabric artwork types
                    "arras",
                    "bathrobe",
                    "brocade",
                    "cape",
                    "curtains",
                    "cushion",
                    "designer clothing",
                    "embroidery",
                    "handkerchief",
                    "kilim",
                    "leather embossing",
                    "pillowcase",
                    "plush doll",
                    "quilt",
                    "stage costume",
                    "tablecloth",
                    "tapestry",
                    "textile",
                    "vestments"
                ).random()

                2 -> listOf( // Furnishing types
                    "armoire",
                    "basket",
                    "bed",
                    "cabinet",
                    "candelabra",
                    "candle snuffer",
                    "chair",
                    "desk",
                    "frame",
                    "lectern",
                    "ottoman",
                    "partition",
                    "side table",
                    "table",
                    "table",
                    "throne",
                    "trunk"
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
                    "canoe",
                    "carved ivory",
                    "carved walking staff",
                    "carving",
                    "comb",
                    "game board",
                    "intarsia",
                    "jewelry box",
                    "marquetry",
                    "mask",
                    "scroll tube",
                    "skateboard",
                    "soap carving",
                    "woodburning",
                    "wooden bucket",
                    "wooden mantelpiece",
                    "wooden engraving",
                    "wooden puzzle",
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
                    "designer sunglasses",
                    "fused-glass art",
                    "glass bottle",
                    "glass bowl",
                    "goblet",
                    "hourglass",
                    "ornate bottle",
                    "reading glasses",
                    "sculpture-in-a-bottle",
                    "shattered glass pile",
                    "snow globe",
                    "stained glass"
                ).random()

                7 -> listOf( // Stonework types
                    "birdbath",
                    "carved tablet",
                    "dias",
                    "marble bookend",
                    "marble chess set",
                    "marble statue",
                    "painted rock",
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
                    "bi-metal yo-yo",
                    "brass relief",
                    "bronze bust",
                    "bronze sculpture",
                    "chimes",
                    "commemorative coin",
                    "engraved bell",
                    "Fabergé egg",
                    "gilded utensil",
                    "golden idol",
                    "hand mirror",
                    "iron statuette",
                    "medallion",
                    "metallic ornament",
                    "ornate doorknob",
                    "reliquary",
                    "silver tea set",
                    "tin inkwell",
                    "wall mirror"
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
                    "size-adjusting outfit",
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
                        "consequences",
                        "constipation",
                        "cost",
                        "dark side",
                        "divinity",
                        "draw",
                        "essence",
                        "explosion",
                        "feeling",
                        "flow",
                        "flavor",
                        "frivolity",
                        "game",
                        "glory",
                        "heat",
                        "hope",
                        "incomprehensibility",
                        "insatiability",
                        "inspiration",
                        "instrumentality",
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
                        "reason",
                        "resilience",
                        "resonance",
                        "scent",
                        "shadow",
                        "shape",
                        "sound",
                        "synesthesia",
                        "taste",
                        "tears",
                        "terminus",
                        "truth",
                        "totality",
                        "vortex",
                        "weight"
                    ).random()

                    val abstractTopic = listOf(
                        "adventure",
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
                        "femininity",
                        "fire",
                        "friendship",
                        "greed",
                        "hate",
                        "humanity",
                        "ignorance",
                        "industry",
                        "innovation",
                        "kitsch",
                        "knowledge",
                        "loss",
                        "love",
                        "love, laugh, live",
                        "magic",
                        "masculinity",
                        "militarism",
                        "nihilism",
                        "nature",
                        "pacifism",
                        "patriotism",
                        "peace",
                        "petrichor",
                        "philosophy",
                        "prisencolinensinainciusol",
                        "prophecy",
                        "purple",
                        "serendipity",
                        "shapes",
                        "silence",
                        "socialism",
                        "technology",
                        "the future",
                        "the past",
                        "the present",
                        "the unknown",
                        "tradition",
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
                        "average",
                        "chipper",
                        "clever",
                        "comely",
                        "confused",
                        "crazed",
                        "depraved",
                        "devious",
                        "enlightened",
                        "funny",
                        "greasy",
                        "grotesque",
                        "grumpy",
                        "happy",
                        "hard-working",
                        "hungry",
                        "intense",
                        "lively",
                        "lonely",
                        "magical",
                        "oily",
                        "purple",
                        "sad",
                        "sloven",
                        "smelly",
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
                        "adolescent",
                        "aesthetic",
                        "aloof",
                        "ancient",
                        "attractive",
                        "average",
                        "barbaric",
                        "beautiful",
                        "benevolent",
                        "bespectacled",
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
                        "feminine",
                        "foolhardy",
                        "foppish",
                        "forceful",
                        "friendly",
                        "fussy",
                        "girly",
                        "greedy",
                        "grumpy",
                        "hedonistic",
                        "hostile",
                        "hot-headed",
                        "iconoclastic",
                        "imposing",
                        "inauspicious",
                        "jealous",
                        "kindly",
                        "lazy",
                        "lusty",
                        "malevolent",
                        "manly",
                        "masculine",
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
                        "stupid",
                        "suspicious",
                        "ugly",
                        "vengeful",
                        "violent",
                        "virtuous",
                        "well-bred",
                        "young",
                        "youthful"
                    ).random()

                    val humanRace = listOf(
                        "beastkin",
                        "brownie",
                        "dracon",
                        "drow",
                        "drow",
                        "dwarven", "dwarven", "dwarven", "dwarven", "duergar",
                        "elvariel",
                        "gray elven",
                        "elven", "elven", "elven", "elven", "elven",
                        "fairy",
                        "giff",
                        "gnomish",
                        "gnome titan",
                        "grevan",
                        "Gronnanarian",
                        "human", "human", "human", "human", "human", "human", "human",
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
                        "dancing girl",
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
                        "manservant",
                        "marine",
                        "mason",
                        "merchant",
                        "messenger",
                        "miner",
                        "minstrel",
                        "patron",
                        "philosopher",
                        "plumber",
                        "politician",
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
                        "sommelier",
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
                        "answering the call",
                        "arguing",
                        "at a ceremony",
                        "at a hot spring",
                        "at it again",
                        "at rest",
                        "attacking",
                        "beckoning",
                        "being foolish",
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
                        "doing sick tricks",
                        "drinking",
                        "eating",
                        "eating a book",
                        "engrossed",
                        "enjoying fine Kenzer & Company products",
                        "fighting",
                        "haggling",
                        "getting drunk",
                        "hanging out",
                        "hard at work",
                        "helping out",
                        "holding fruit",
                        "in danger",
                        "in high spirits",
                        "in mortal combat",
                        "in great pain",
                        "in pursuit",
                        "investigating",
                        "jumping",
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
                        "solving a puzzle",
                        "striking a pose",
                        "studying",
                        "trying something new",
                        "up to no good",
                        "victorious",
                        "waking up",
                        "waltzing"
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
                                "were-",
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
                        "establishment of a colony",
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
                        "the great flood",
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
                                    "Abraham",
                                    "Adam",
                                    "Benedict",
                                    "Boniface",
                                    "Cuthbert",
                                    "Deony",
                                    "Edmund",
                                    "Gabriel",
                                    "Gilbert",
                                    "Godot",
                                    "Godwin",
                                    "Gregory",
                                    "Henry",
                                    "Hugh",
                                    "John Paul",
                                    "John",
                                    "Justin",
                                    "Louis",
                                    "Luigi",
                                    "Luke",
                                    "Mario",
                                    "Maximillion",
                                    "Odo",
                                    "Oliver",
                                    "Oswald",
                                    "Patrick",
                                    "Paul",
                                    "Polybius",
                                    "Rudolph",
                                    "Stephen",
                                    "Strength",
                                    "Theodore",
                                    "Thomas",
                                    "Urban",
                                    "Vincent",
                                    "Walter"
                                ).random()
                            } else {
                                listOf(
                                    "Agatha",
                                    "Amy",
                                    "Annie",
                                    "Bridget",
                                    "Bridgette",
                                    "Candice",
                                    "Catherine",
                                    "Cecilia",
                                    "Clementine",
                                    "Colette",
                                    "Eve",
                                    "Grace",
                                    "Hildegard",
                                    "Jadwiga",
                                    "Joan",
                                    "Kim",
                                    "Kristina",
                                    "Lilith",
                                    "Margaret",
                                    "Marigold",
                                    "Mary",
                                    "Matilda",
                                    "Olga",
                                    "Penny",
                                    "Rose",
                                    "Theophany",
                                    "Ursula",
                                    "Veronica"
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
                                "Four-eyed",
                                "Generous",
                                "Green-haired",
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
                                "Super",
                                "Templebuilder",
                                "Theologian",
                                "Translator",
                                "Undead Slayer",
                                "Unrestrained",
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

                        else-> { // Deity

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
                                "Eilistraee, Demi-Gawdess of Moonlight, Beauty, etc.",
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

                    if (Random.nextInt(1,11) == 1) {

                        val specificPerson = if (Random.nextInt(1,5) == 1) {
                                // Favorite personal characters of developer and her friends
                                listOf(
                                    "BattleMage Frank Battleforge, the Dwarven, Unbathed " +
                                            "Aficionado of Elven Culture",
                                    "Bladesinger Alaran Lodestar, the Slayer of the Dracolich",
                                    "Blood Lord Aspor Hundolfr, the Ambitious Revenant",
                                    "Cavalier Commander Gunner Calahander, the Undying",
                                    "Centaurian Dancer Yosoti, the Unforgettable",
                                    "Chairman of the Horde Leitric, the Book-burner",
                                    "Contender Rebel the Goblin, Fire Giant of Opera City",
                                    "Divinist Ambrose, the Prophet of Possibility",
                                    "Dr. Anselm Giger, the Mad Alchemist",
                                    "Dr. Joan Snow, Esq., the Arcane Anatomist and Psychic Surgeon",
                                    "Eldritch Barbarian Clodagh Grenawich, the Hagblood Halberd",
                                    "Espion Aminah Abadaan, the Honest Hand",
                                    "Gawdfather Kane Freeman, the Shadow Governor",
                                    "Initiate Piperine Scoville, the Flame of Knowledge",
                                    "Necromancer Ivanka, the Fallen Angel of Elturel",
                                    "SkateboarLock Pavimentum Shredz, the Bodaciously Radical",
                                    "Sorceress Unioos Bugulnoz, Madame of the Glitzy Pixie",
                                    "Spoiled Princess Minnie, the Fisher Queen",
                                    "Sr. Researcher Theodore Webster, " +
                                            "the Unintentionally-Calamitous",
                                    "Warlock Lugh Quicksmile, Son of Fey but Bastard to All",
                                    "Wordsmith Kopy-Copy, the Country Couturier",
                                    "Yakuza Lord Ragna Vel, the Inauspicious Assassin",
                                    "Zealous Inquisitor Agathokles Diodotus, the Avatar of Zelaur"
                                ).random()
                            } else {
                                // Canonical HackMaster 4e characters
                                listOf(
                                    "Animator Aliron Praetox, the Dark Beast",
                                    "Arch Angelic Knight Michael De Shalaray, " +
                                            "master of the Society for the Elimination of " +
                                            "Lycanthropy and Undead Monstrosities",
                                    "Arch Mage of the Circle Kramlak Lashym, " +
                                            "head of the Circle of Sequestered Magic",
                                    "Arch-Mage Yargrove Hendrachmin, the Golem Master",
                                    "Arch-Mage Zarba, the Dweomer-Shaper",
                                    "Consul Lord Rurrisen, head of the Supreme Council " +
                                            "of the Pan-Elf Union",
                                    "Dark Lord of the Pit Sosah Regeloj, " +
                                            "head of the Shadow Heart Battalion",
                                    "Grand Theocrat Hanzdor Warforge, " +
                                            "superior of the Holy Dwarven Theocracy",
                                    "Griftmaster Jonid Coincrawler, the Scourge of the Bag World",
                                    "HackFighter/HackSassin Sturm Pyre, the Growler",
                                    "Headmistress Drusilla Wystan, " +
                                            "dean of the University of Tilan",
                                    "Holy Knight Sir Lyran Daws, " +
                                            "master of the Holy Order of Luvia",
                                    "Iron General Frizdan Grazlyte, " +
                                            "head of the Iron Axes of Praxter",
                                    "Lady Martaney Amaran, heir to Lady Amaran's Society for the " +
                                            "Advancement of Swordplay",
                                    "Lonnya Grasswillow, " +
                                            "Artistic Director of the Five City Minstrel Society",
                                    "Lord Gilead, Ruler of Fangaerie",
                                    "Shining Knight Sir Ja'en Garnet, the Silver Defender",
                                    "Supreme Arch Transmuter Elenwyd Sesuliad, " +
                                            "the High Transmuter of Whisperydown",
                                    "Supreme Grand Merchant Wencelan Druffin, " +
                                            "leader of the Gnomish Syndicate"
                                ).random()
                            }

                        nameBuilder.append(specificPerson)

                    } else {

                        val nobleIsMale = Random.nextBoolean()

                        val nobleTitle = if (nobleIsMale) {
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
                                "Doctor",
                                "Doge",
                                "Earl",
                                "General",
                                "Governor",
                                "Grandmaster",
                                "Guildmaster",
                                "Justice",
                                "Lord", "Lord", "Lord",
                                "Lord President",
                                "Marquis",
                                "Mayor",
                                "Minister",
                                "Professor",
                                "Sir", "Sir", "Sir", "Sir", "Sir",
                                "Sorcerer",
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
                                "Dame", "Dame", "Dame", "Dame", "Dame",
                                "Doge",
                                "Emissary",
                                "General",
                                "Grandmaster",
                                "Guildmistress",
                                "Justice",
                                "Lady", "Lady", "Lady",
                                "Madame President",
                                "Marquise",
                                "Master Bard",
                                "Preceptress",
                                "Professor",
                                "Sorceress",
                                "Spymaster",
                                "Stewardess",
                                "Treasure Huntress",
                                "Vice President"
                            ).random()
                        }

                        val nobleName = if (nobleIsMale) {
                            listOf(
                                "Albert",
                                "Alexander",
                                "Alfred",
                                "Arthur",
                                "Augustus",
                                "Boris",
                                "Charles",
                                "Cyrus",
                                "David",
                                "Edmund",
                                "Edward",
                                "Eric",
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
                                "Luigi",
                                "Marcus",
                                "Mario",
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
                                "Robyn",
                                "Rosalia",
                                "Sophia",
                                "Tiffany",
                                "Victoria",
                                "Wilhelmina",
                                "Yvette"
                            ).random()
                        }

                        val noblePlacement = listOf(
                            "", "", "", "", "", "",
                            " Sr.",
                            " Jr."," Jr.",
                            " II",
                            " II",
                            " III",
                            " IV",
                            " V",
                            " VI",
                            "X"
                        ).random()

                        val nobleNickname = listOf(
                            "Administrator",
                            "Affable",
                            "Ambitious",
                            "Architect",
                            "Artist",
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
                            "Painter",
                            "Paranoid",
                            "Peacemaker",
                            "Pensive",
                            "Rowdy",
                            "Salty",
                            "Sage",
                            "Schemer",
                            "Scholar",
                            "Sculptor",
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
                            "Wizard of the 12th Realm of Ephysiyies, etc.",
                            "Wunderkind"
                        ).random()

                        nameBuilder.append(
                            "$nobleTitle ${nobleName}$noblePlacement" +
                                    " the $nobleNickname"
                        )
                    }
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
                            "Rozemyne",
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
                        "Bookworm",
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
                        "Uniter",
                        "Unready",
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