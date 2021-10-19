package com.example.android.treasurefactory

import kotlin.random.Random

class HMTreasureFactory {
    
    companion object {

        //TODO Add discrimination logic for specified gem ranges
        fun createGem(constrainedRules: Boolean = false) {}

        fun createArtObject(parentHoardID: Int, constrainedRules: Boolean = false) : HMArtObject {

            var temporaryRank:  Int
            var ageInYears:     Int
            var ageModifier:    Int

            val artType:        HMArtObject.ArtType
            val renown:         HMArtObject.Renown
            val size:           HMArtObject.Size
            val condition:      HMArtObject.Condition
            val materials:      HMArtObject.Materials
            val quality:        HMArtObject.Quality
            val subject:        HMArtObject.Subject

            val newArt:         HMArtObject

            // --- Roll for type of art object ---

            artType = when (Random.nextInt(1,101)) {

                in 1..5     -> HMArtObject.ArtType.PAPER
                in 6..15    -> HMArtObject.ArtType.FABRIC
                in 16..30   -> HMArtObject.ArtType.FURNISHING
                in 31..45   -> HMArtObject.ArtType.PAINTING
                in 46..60   -> HMArtObject.ArtType.WOOD
                in 61..70   -> HMArtObject.ArtType.CERAMIC
                in 71..80   -> HMArtObject.ArtType.GLASS
                in 81..90   -> HMArtObject.ArtType.STONE
                in 91..99   -> HMArtObject.ArtType.METAL
                else        -> HMArtObject.ArtType.MAGICAL
            }

            //TODO: get resource ID as string for given item type

            // --- Roll for the renown of the artist ---

            renown = when (Random.nextInt(1,101)) {

                in 1..15    -> HMArtObject.Renown.UNKNOWN
                in 16..30   -> HMArtObject.Renown.OBSCURE
                in 31..45   -> HMArtObject.Renown.CITY_RENOWNED
                in 46..65   -> HMArtObject.Renown.REGIONALLY_RENOWNED
                in 66..85   -> HMArtObject.Renown.NATIONALLY_RENOWNED
                in 86..95   -> HMArtObject.Renown.CONTINENTALLY_RENOWNED
                in 96..99   -> HMArtObject.Renown.WORLDLY_RENOWNED
                else        -> HMArtObject.Renown.MOVEMENT_LEADER
            }

            // --- Roll for size of art object ---

            size = when(Random.nextInt(1,101)) {

                in 1..5     -> HMArtObject.Size.TINY
                in 6..25    -> HMArtObject.Size.VERY_SMALL
                in 26..45   -> HMArtObject.Size.SMALL
                in 46..65   -> HMArtObject.Size.AVERAGE
                in 66..85   -> HMArtObject.Size.LARGE
                in 86..90   -> HMArtObject.Size.VERY_LARGE
                in 91..96   -> HMArtObject.Size.HUGE
                in 97..99   -> HMArtObject.Size.MASSIVE
                else        -> HMArtObject.Size.GARGANTUAN
            }

            // --- Roll for quality of materials used ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + renown.valueMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            materials = when (temporaryRank) {

                0       -> HMArtObject.Materials.AWFUL
                1       -> HMArtObject.Materials.POOR
                2       -> HMArtObject.Materials.BELOW_AVERAGE
                3       -> HMArtObject.Materials.AVERAGE
                4       -> HMArtObject.Materials.ABOVE_AVERAGE
                5       -> HMArtObject.Materials.GOOD
                6       -> HMArtObject.Materials.EXCELLENT
                7       -> HMArtObject.Materials.FINEST
                else    -> HMArtObject.Materials.UNIQUE
            }

            // --- Roll for quality of work done ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + renown.valueMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            quality = when (temporaryRank) {

                0       -> HMArtObject.Quality.AWFUL
                1       -> HMArtObject.Quality.POOR
                2       -> HMArtObject.Quality.BELOW_AVERAGE
                3       -> HMArtObject.Quality.AVERAGE
                4       -> HMArtObject.Quality.ABOVE_AVERAGE
                5       -> HMArtObject.Quality.GOOD
                6       -> HMArtObject.Quality.EXCELLENT
                7       -> HMArtObject.Quality.BRILLIANT
                else    -> HMArtObject.Quality.MASTERPIECE
            }

            // --- Roll for age of artwork ---

            ageInYears = (rollPenetratingDice(DieType(5,20)).baseRoll) *       // 5d20 x 1d4, penetrate on all rolls
                    rollPenetratingDice(DieType(1,4)).getRollTotal()

            if (ageInYears < 0) { ageInYears = 0 }

            ageModifier = when (ageInYears) {

                in 0..25        -> -2
                in 26..75       -> -1
                in 76..150      -> 0
                in 151..300     -> 1
                in 301..600     -> 2
                in 601..1500    -> 3
                in 1500..3000   -> 4
                else            -> 5
            } + artType.ageMod

            if (ageModifier < -2) { ageModifier = -2 } else
                if (ageModifier > 5) { ageModifier = 5 }

            // --- Roll for condition of art object ---

            temporaryRank = when (Random.nextInt(1,101)){ //convert to value rank for modification

                in 1..5     -> 0
                in 6..25    -> 1
                in 26..45   -> 2
                in 46..65   -> 3
                in 66..85   -> 4
                in 86..90   -> 5
                in 91..96   -> 6
                in 97..99   -> 7
                else        -> 8
            } + artType.conditionMod

            if (temporaryRank < 0) { temporaryRank = 0 }

            condition = when (temporaryRank) {

                0       -> HMArtObject.Condition.BADLY_DAMAGED
                1       -> HMArtObject.Condition.DAMAGED
                2       -> HMArtObject.Condition.WORN
                3       -> HMArtObject.Condition.AVERAGE
                4       -> HMArtObject.Condition.GOOD
                5       -> HMArtObject.Condition.EXCELLENT
                6       -> HMArtObject.Condition.NEAR_PERFECT
                7       -> HMArtObject.Condition.PERFECT
                else    -> HMArtObject.Condition.FLAWLESS
            }

            // --- Roll for subject matter of art object ---

            subject = when (Random.nextInt(1,101)) {

                in 1..10    -> HMArtObject.Subject.ABSTRACT
                in 11..20   -> HMArtObject.Subject.MONSTER
                in 21..30   -> HMArtObject.Subject.HUMAN
                in 31..50   -> HMArtObject.Subject.NATURAL
                in 51..70   -> HMArtObject.Subject.HISTORICAL
                in 71..90   -> HMArtObject.Subject.RELIGIOUS
                in 91..99   -> HMArtObject.Subject.NOBLE
                else        -> HMArtObject.Subject.ROYALTY
            }

            // ---Generate and return new art object ---

            return HMArtObject(0, parentHoardID,"",artType,renown,size,condition,materials,quality,ageInYears,
                subject,(renown.valueMod + size.valueMod + materials.valueMod + quality.valueMod +
                        ageModifier + condition.valueMod + subject.valueMod))
        }

        fun createMagicItem(constrainedRules: Boolean = false) {}

        fun createSpellCollection(constrainedRules: Boolean = false) {}
    }
}
