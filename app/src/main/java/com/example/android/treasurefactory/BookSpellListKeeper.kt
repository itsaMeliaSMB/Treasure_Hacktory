package com.example.android.treasurefactory

class BookSpellListKeeper() {

    fun getSpellChoiceTripleList(discipline: SpCoDiscipline, spellLevel: Int, school: SpellSchool?,
                                 useSplat: Boolean, starterCat: Int = -1)
    : List<Triple<String,Int,Int>> {

        return when (discipline) {
            SpCoDiscipline.ARCANE   -> {

                when (starterCat) {

                    0   -> {    // Starting offensive spell list

                        if (useSplat) { // SSG list with Errata

                            listOf(
                                Triple("Befriend",0,1),
                                Triple("Burning Hands",0,1),
                                Triple("Charm Person",0,1),
                                Triple("Chill Touch",0,1),
                                Triple("Chromatic Orb",0,1),
                                Triple("Color Spray",0,1),
                                Triple("Enlarge",0,1),
                                Triple("Fireball, Barrage",0,1),
                                Triple("Fireball, Sidewinder Factor 1",0,1),
                                Triple("Firewater",0,1),
                                Triple("Grease",0,1),
                                Triple("Hypnotism",0,1),
                                Triple("Icy Blast",0,1),
                                Triple("Jack Punch",0,1),
                                Triple("Light",0,1),
                                Triple("Magic Missile",0,1),
                                Triple("Magic Stone",0,1),
                                Triple("Minor Sphere of Perturbation",0,1),
                                Triple("Phantasmal Fireball",0,1),
                                Triple("Power Word: Cartwheel",0,1),
                                Triple("Power Word: Moon",0,1),
                                Triple("Power Word: Summersault",0,1),
                                Triple("Push",0,1),
                                Triple("Shocking Grasp",0,1),
                                Triple("Sleep",0,1),
                                Triple("Spook",0,1),
                                Triple("Taunt",0,1)
                            )
                        } else {        // GMG list

                            listOf(
                                Triple("Befriend",0,1),
                                Triple("Burning Hands",0,1),
                                Triple("Charm Person",0,1),
                                Triple("Chill Touch",0,1),
                                Triple("Chromatic Orb",0,1),
                                Triple("Color Spray",0,1),
                                Triple("Enlarge",0,1),
                                Triple("Fireball, Barrage",0,1),
                                Triple("Fireball, Sidewinder Factor 1",0,1),
                                Triple("Firewater",0,1),
                                Triple("Grease",0,1),
                                Triple("Hypnotism",0,1),
                                Triple("Light",0,1),
                                Triple("Magic Missile",0,1),
                                Triple("Minor Sphere of Perturbation",0,1),
                                Triple("Phantasmal Fireball",0,1),
                                Triple("Shocking Grasp",0,1),
                                Triple("Sleep",0,1),
                                Triple("Spook",0,1),
                                Triple("Taunt",0,1)
                            )
                        }
                    }

                    1   -> {    // Starting defensive spell list

                        if (useSplat) { // SSG list with Errata

                            listOf(
                                Triple("Affect Normal Fires",0,1),
                                Triple("Alarm",0,1),
                                Triple("Armor",0,1),
                                Triple("Audible Glamer",0,1),
                                Triple("Aura of Innocence",0,1),
                                Triple("Change Self",0,1),
                                Triple("Corpse Visage",0,1),
                                Triple("Dancing Lights",0,1),
                                Triple("Disable Hand",0,1),
                                Triple("Faerie Phantoms",0,1),
                                Triple("Feather Fall",0,1),
                                Triple("Flutter Soft",0,1),
                                Triple("Gaze Reflection",0,1),
                                Triple("Hold Portal",0,1),
                                Triple("Jump",0,1),
                                Triple("Magic Shield",0,1),
                                Triple("Phantom Armor",0,1),
                                Triple("Protection from Evil",0,1),
                                Triple("Protection from Sunburn",0,1),
                                Triple("Protective Amulet",0,1),
                                Triple("Remove Fear",0,1),
                                Triple("Resist Cold",0,1),
                                Triple("Resist Fire",0,1),
                                Triple("Shift Blame",0,1),
                                Triple("Smell Immunity",0,1),
                                Triple("Spider Climb",0,1),
                                Triple("Wall of Fog",0,1)
                            )
                        } else {        // GMG list

                            listOf(
                                Triple("Affect Normal Fires",0,1),
                                Triple("Alarm",0,1),
                                Triple("Armor",0,1),
                                Triple("Audible Glamer",0,1),
                                Triple("Aura of Innocence",0,1),
                                Triple("Change Self",0,1),
                                Triple("Dancing Lights",0,1),
                                Triple("Faerie Phantoms",0,1),
                                Triple("Feather Fall",0,1),
                                Triple("Flutter Soft",0,1),
                                Triple("Gaze Reflection",0,1),
                                Triple("Hold Portal",0,1),
                                Triple("Jump",0,1),
                                Triple("Magic Shield",0,1),
                                Triple("Phantom Armor",0,1),
                                Triple("Protection from Evil",0,1),
                                Triple("Shift Blame",0,1),
                                Triple("Smell Immunity",0,1),
                                Triple("Spider Climb",0,1),
                                Triple("Wall of Fog",0,1)
                            )
                        }

                    }

                    2   -> {    // Starting miscellaneous spell list

                        if (useSplat) { // SSG list with Errata

                            listOf(
                                Triple("Animate Dead Animals",0,1),
                                Triple("Bash Door",0,1),
                                Triple("Comprehend Languages",0,1),
                                Triple("Conjure Mount",0,1),
                                Triple("Copy",0,1),
                                Triple("Detect Disease",0,1),
                                Triple("Detect Illusion",0,1),
                                Triple("Detect Magic",0,1),
                                Triple("Detect Phase",0,1),
                                Triple("Detect Undead",0,1),
                                Triple("Divining Rod",0,1),
                                Triple("Erase",0,1),
                                Triple("Find Familiar",0,1),
                                Triple("Fog Vision",0,1),
                                Triple("Gabal's Magic Aura",0,1),
                                Triple("Melt",0,1),
                                Triple("Mend",0,1),
                                Triple("Merge Coin Pile",0,1),
                                Triple("Message",0,1),
                                Triple("Phantasmal Force",0,1),
                                Triple("Pool Gold",0,1),
                                Triple("Precipitation",0,1),
                                Triple("Remove Thirst",0,1),
                                Triple("Run",0,1),
                                Triple("Throw Voice",0,1),
                                Triple("Unseen Servant",0,1),
                                Triple("Wizard Mark",0,1)
                            )
                        } else {        // GMG list

                            listOf(
                                Triple("Bash Door",0,1),
                                Triple("Comprehend Languages",0,1),
                                Triple("Conjure Mount",0,1),
                                Triple("Detect Magic",0,1),
                                Triple("Detect Undead",0,1),
                                Triple("Erase",0,1),
                                Triple("Find Familiar",0,1),
                                Triple("Fog Vision",0,1),
                                Triple("Gabal's Magic Aura",0,1),
                                Triple("Melt",0,1),
                                Triple("Mend",0,1),
                                Triple("Merge Coin Pile",0,1),
                                Triple("Message",0,1),
                                Triple("Phantasmal Force",0,1),
                                Triple("Pool Gold",0,1),
                                Triple("Precipitation",0,1),
                                Triple("Run",0,1),
                                Triple("Throw Voice",0,1),
                                Triple("Unseen Servant",0,1),
                                Triple("Wizard Mark",0,1)
                            )
                        }
                    }

                    else-> {

                        if (school != null) {   // Look up list for provided school

                            when (school) {

                                SpellSchool.ABJURATION  -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Protection from Evil",0,1),
                                                    Triple("Protection from Sunburn",0,1),
                                                    Triple("Protective Amulet",0,1),
                                                    Triple("Remove Fear",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Alarm",0,1),
                                                    Triple("Protection from Evil",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Filter",0,2),
                                                    Triple("Magic Missile Reflection",0,2),
                                                    Triple("Preserve",0,2),
                                                    Triple("Protection from Cantrips",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Preserve",0,2),
                                                    Triple("Protection from Cantrips",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Dispel Magic",0,3),
                                                    Triple("Dispel Silence",0,3),
                                                    Triple("Glyph of Ice",0,3),
                                                    Triple("Glyph of Sniping",0,3),
                                                    Triple("Non-Detection",0,3),
                                                    Triple("Proof from Teleportation",0,3),
                                                    Triple("Protection from Normal Missiles",0,3),
                                                    Triple("Quarantine",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Dispel Magic",0,3),
                                                    Triple("Non-Detection",0,3),
                                                    Triple("Protection from Normal Missiles",0,3),
                                                    Triple("Ward Off Evil",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Circle of Protection",0,4),
                                                    Triple("Exploding Glyph",0,4),
                                                    Triple("Fire Trap",0,4),
                                                    Triple("Minor Globe of Invulnerability",0,4),
                                                    Triple("Remove Curse",0,4),
                                                    Triple("Wimpel's Dispelling Screen",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Minor Globe of Invulnerability",0,4),
                                                    Triple("Remove Curse",0,4),
                                                    Triple("Fire Trap",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Avoidance",0,5),
                                                    Triple("Dismissal",0,5),
                                                    Triple("Jorrel's Private Sanctum",0,5),
                                                    Triple("Spell Shield",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Containment",0,5),
                                                    Triple("Dismissal",0,5),
                                                    Triple("Avoidance",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Anti-Animal Shell",0,6),
                                                    Triple("Anti-Magic Shell",0,6),
                                                    Triple("Break Hex",0,6),
                                                    Triple("Globe of Invulnerability",0,6),
                                                    Triple("Invulnerability to Magical Weapons",0,6),
                                                    Triple("Repulsion",0,6),
                                                    Triple("Spiritwrack",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Anti-Magic Shell",0,6),
                                                    Triple("Break Hex",0,6),
                                                    Triple("Globe of Invulnerability",0,6),
                                                    Triple("Repulsion",0,6),
                                                    Triple("Spiritwrack",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Banishment",0,7),
                                                    Triple("Sequester",0,7),
                                                    Triple("Spell Turning",0,7),
                                                    Triple("Volley",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Banishment",0,7),
                                                    Triple("Sequester",0,7),
                                                    Triple("Spell Turning",0,7),
                                                    Triple("Volley",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Dispel Enchantment",0,8),
                                                    Triple("Gandle's Spell Immunity",0,8),
                                                    Triple("Mind Blank",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Gandle's Spell Immunity",0,8),
                                                    Triple("Mind Blank",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Elemental Aura",0,9),
                                                    Triple("Immunity to Undeath",0,9),
                                                    Triple("Imprisonment",0,9),
                                                    Triple("Jebidiah's Ultimate Circle",0,9),
                                                    Triple("Prismatic Sphere",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Prismatic Sphere",0,9),
                                                    Triple("Imprisonment",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.ALTERATION  -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Affect Normal Fires",0,1),
                                                    Triple("Burning Hands",0,1),
                                                    Triple("Chromatic Orb",0,1),
                                                    Triple("Color Spray",0,1),
                                                    Triple("Comprehend Languages",0,1),
                                                    Triple("Corpse Link",0,1),
                                                    Triple("Dancing Lights",0,1),
                                                    Triple("Enlarge",0,1),
                                                    Triple("Erase",0,1),
                                                    Triple("Evaporate",0,1),
                                                    Triple("Feather Fall",0,1),
                                                    Triple("Fireball, Barrage",0,1),
                                                    Triple("Firewater",0,1),
                                                    Triple("Fist of Stone",0,1),
                                                    Triple("Flutter Soft",0,1),
                                                    Triple("Gaze Reflection",0,1),
                                                    Triple("Hold Portal",0,1),
                                                    Triple("Jump",0,1),
                                                    Triple("Light",0,1),
                                                    Triple("Melt",0,1),
                                                    Triple("Mend",0,1),
                                                    Triple("Merge Coin Pile",0,1),
                                                    Triple("Message",0,1),
                                                    Triple("Metal Bug",0,1),
                                                    Triple("Minor Sphere of Perturbation",0,1),
                                                    Triple("Phantom Armor",0,1),
                                                    Triple("Pool Gold",0,1),
                                                    Triple("Precipitation",0,1),
                                                    Triple("Remove Thirst",0,1),
                                                    Triple("Resist Cold",0,1),
                                                    Triple("Resist Fire",0,1),
                                                    Triple("Shocking Grasp",0,1),
                                                    Triple("Spider Climb",0,1),
                                                    Triple("Wizard Mark",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Affect Normal Fires",0,1),
                                                    Triple("Burning Hands",0,1),
                                                    Triple("Chromatic Orb",0,1),
                                                    Triple( "Color Spray",0,1),
                                                    Triple("Comprehend Languages",0,1),
                                                    Triple("Dancing Lights",0,1),
                                                    Triple("Enlarge",0,1),
                                                    Triple("Erase",0,1),
                                                    Triple("Feather Fall",0,1),
                                                    Triple("Fireball, Barrage",0,1),
                                                    Triple("Firewater",0,1),
                                                    Triple("Flutter Soft",0,1),
                                                    Triple("Gaze Reflection",0,1),
                                                    Triple("Hold Portal",0,1),
                                                    Triple("Jump",0,1),
                                                    Triple("Light",0,1),
                                                    Triple("Melt",0,1),
                                                    Triple("Mend",0,1),
                                                    Triple("Merge Coin Pile",0,1),
                                                    Triple("Message",0,1),
                                                    Triple("Minor Sphere of Perturbation",0,1),
                                                    Triple("Phantom Armor",0,1),
                                                    Triple("Pool Gold",0,1),
                                                    Triple("Precipitation",0,1),
                                                    Triple("Shocking Grasp",0,1),
                                                    Triple("Spider Climb",0,1),
                                                    Triple("Wizard Mark",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Alter Self",0,2),
                                                    Triple("Chaotic Transformation",0,2),
                                                    Triple("Cheetah Speed",0,2),
                                                    Triple("Continual Light",0,2),
                                                    Triple("Darkness",0,2),
                                                    Triple("Deeppockets",0,2),
                                                    Triple("Elenwyd's Majestic Bosom",0,2),
                                                    Triple("Fire Telekinesis",0,2),
                                                    Triple("Fog Cloud",0,2),
                                                    Triple("Fool's Gold",0,2),
                                                    Triple("Fustis's Mnemonic Enhancer",0,2),
                                                    Triple("Galinor's Gender Reversal",0,2),
                                                    Triple("Irritation",0,2),
                                                    Triple("Knock",0,2),
                                                    Triple("Levitate",0,2),
                                                    Triple("Magic Missile Reflection",0,2),
                                                    Triple("Magic Mouth",0,2),
                                                    Triple("Pyrotechnics",0,2),
                                                    Triple("Rope Trick",0,2),
                                                    Triple("Shatter",0,2),
                                                    Triple("Strength",0,2),
                                                    Triple("Tattoo of Shame",0,2),
                                                    Triple("White Hot Metal",0,2),
                                                    Triple("Wizard Lock",0,2),
                                                    Triple("Whispering Wind",0,2),
                                                    Triple("White Hot Metal",0,2),
                                                    Triple("Wizard Lock",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Alter Self",0,2),
                                                    Triple("Cheetah Speed",0,2),
                                                    Triple("Whispering Wind",0,2),
                                                    Triple("Alter Self",0,2),
                                                    Triple("Cheetah Speed",0,2),
                                                    Triple("Continual Light",0,2),
                                                    Triple("Darkness, 15’ Radius",0,2),
                                                    Triple("Fire Telekinesis",0,2),
                                                    Triple("Fog Cloud",0,2),
                                                    Triple("Irritation",0,2),
                                                    Triple("Knock",0,2),
                                                    Triple("Levitate",0,2),
                                                    Triple("Magic Mouth",0,2),
                                                    Triple("Pyrotechnics",0,2),
                                                    Triple("Rope Trick",0,2),
                                                    Triple("Shatter",0,2),
                                                    Triple("Strength",0,2),
                                                    Triple("Tattoo of Shame",0,2),
                                                    Triple("Telepathic Mute",0,2),
                                                    Triple("White Hot Metal",0,2),
                                                    Triple("Wizard Lock",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("A Day in the Life",0,3),
                                                    Triple("Airbolt",0,3),
                                                    Triple("Arinathor's Dark Limbs",0,3),
                                                    Triple("Blink",0,3),
                                                    Triple("Cloudburst",0,3),
                                                    Triple("Continual Darkness",0,3),
                                                    Triple("Delude",0,3),
                                                    Triple("Dispel Silence",0,3),
                                                    Triple("Explosive Runes",0,3),
                                                    Triple("Fireflow",0,3),
                                                    Triple("Fly",0,3),
                                                    Triple("Fool's Speech",0,3),
                                                    Triple("Gandle's Humble Hut",0,3),
                                                    Triple("Grow",0,3),
                                                    Triple("Gust of Wind",0,3),
                                                    Triple("Haste",0,3),
                                                    Triple("Infravision",0,3),
                                                    Triple("Item",0,3),
                                                    Triple("Mericutyn's Grotesquely Distented Nose",0,3),
                                                    Triple("Morton's Minute Meteors",0,3),
                                                    Triple("Phantom Wind",0,3),
                                                    Triple("Polymorph to Insect",0,3),
                                                    Triple("Polymorph to Amphibian",0,3),
                                                    Triple("Polymorph to Primate",0,3),
                                                    Triple("Runes of Eyeball Implosion",0,3),
                                                    Triple("Secret Page",0,3),
                                                    Triple("Slow",0,3),
                                                    Triple("Snapping Teeth",0,3),
                                                    Triple("Tongues",0,3),
                                                    Triple("Transmute Wood to Steel",0,3),
                                                    Triple("Water Breathing",0,3),
                                                    Triple("Wind Wall",0,3),
                                                    Triple("Wraithform",0,3),
                                                    Triple("Zargosa's Flaming Spheres of Torment",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Phantom Wind",0,3),
                                                    Triple("Blink",0,3),
                                                    Triple("Cloudburst",0,3),
                                                    Triple("Continual Darkness",0,3),
                                                    Triple("Delude",0,3),
                                                    Triple("Explosive Runes",0,3),
                                                    Triple("Fly",0,3),
                                                    Triple("Gandle's Humble Hut",0,3),
                                                    Triple("Grow",0,3),
                                                    Triple("Gust of Wind",0,3),
                                                    Triple("Haste",0,3),
                                                    Triple("Infravision",0,3),
                                                    Triple("Item",0,3),
                                                    Triple("Polymorph to Amphibian",0,3),
                                                    Triple("Polymorph to Primate",0,3),
                                                    Triple("Runes of Eyeball Implosion",0,3),
                                                    Triple("Secret Page",0,3),
                                                    Triple("Slow",0,3),
                                                    Triple("Tongues",0,3),
                                                    Triple("Water Breathing",0,3),
                                                    Triple("Wind Wall",0,3),
                                                    Triple("Wraithform",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Close Portal",0,4),
                                                    Triple("Dimension Door",0,4),
                                                    Triple("Emergency Teleport at Random",0,4),
                                                    Triple("Extension I",0,4),
                                                    Triple("Fire Shield",0,4),
                                                    Triple("Flying Familiar",0,4),
                                                    Triple("Haarpang's Magnificent Sphere of Resiliency",0,4),
                                                    Triple("Haarpang's Memory Kick",0,4),
                                                    Triple("Hurl Animal",0,4),
                                                    Triple("Massmorph",0,4),
                                                    Triple("Perpetual Shocking Grasp",0,4),
                                                    Triple("Pixie Wings",0,4),
                                                    Triple("Plant Growth",0,4),
                                                    Triple("Polymorph Other",0,4),
                                                    Triple("Polymorph Self",0,4),
                                                    Triple("Rainbow Pattern",0,4),
                                                    Triple("Solid Fog",0,4),
                                                    Triple("Stone Passage",0,4),
                                                    Triple("Stoneskin",0,4),
                                                    Triple("Tusks of the Oliphant",0,4),
                                                    Triple("Ultravision",0,4),
                                                    Triple("Vacancy",0,4),
                                                    Triple("Wizard Eye",0,4),
                                                    Triple("Zargosa's Lodge of Protection",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Rainbow Pattern",0,4),
                                                    Triple("Vacancy",0,4),
                                                    Triple("Haarpang's Magnificent Sphere of Resiliency",0,4),
                                                    Triple("Zargosa's Lodge of Protection",0,4),
                                                    Triple("Close Portal",0,4),
                                                    Triple("Dimension Door",0,4),
                                                    Triple("Emergency Teleport at Random",0,4),
                                                    Triple("Extension I",0,4),
                                                    Triple("Haarpang's Memory Kick",0,4),
                                                    Triple("Hurl Animal",0,4),
                                                    Triple("Massmorph",0,4),
                                                    Triple("Perpetual Shocking Grasp",0,4),
                                                    Triple("Plant Growth",0,4),
                                                    Triple("Polymorph Other",0,4),
                                                    Triple("Polymorph Self",0,4),
                                                    Triple("Solid Fog",0,4),
                                                    Triple("Stone Passage",0,4),
                                                    Triple("Stoneskin",0,4),
                                                    Triple("Ultravision",0,4),
                                                    Triple("Wizard's Eye",0,4),
                                                    Triple("Fire Shield",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Airy Water",0,5),
                                                    Triple("Animal Growth",0,5),
                                                    Triple("Avoidance",0,5),
                                                    Triple("Breed Fusion",0,5),
                                                    Triple("Centaur’s Gift",0,5),
                                                    Triple("Distance Distortion",0,5),
                                                    Triple("Drayton's Hidden Stash",0,5),
                                                    Triple("Extension",0,5),
                                                    Triple("Fabricate",0,5),
                                                    Triple("Hiamohr's Unfortunate Incident",0,5),
                                                    Triple("Jorrel's Private Sanctum",0,5),
                                                    Triple("Manor's Mindsight",0,5),
                                                    Triple("Polymorph Plant to Mammal",0,5),
                                                    Triple("Stone Shape",0,5),
                                                    Triple("Telekinesis",0,5),
                                                    Triple("Teleport",0,5),
                                                    Triple("Transmute Stone to Mud",0,5),
                                                    Triple("Wall Passage",0,5),
                                                    Triple("Wings of PanDemonium",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Avoidance",0,5),
                                                    Triple("Drayton's Hidden Stash",0,5),
                                                    Triple("Airy Water",0,5),
                                                    Triple("Animal Growth",0,5),
                                                    Triple("Distance Distortion",0,5),
                                                    Triple("Extension II",0,5),
                                                    Triple("Stone Shape",0,5),
                                                    Triple("Telekinesis",0,5),
                                                    Triple("Teleport",0,5),
                                                    Triple("Transmute Rock to Mud",0,5),
                                                    Triple("Wall Passage",0,5),
                                                    Triple("Fabricate",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Control Weather",0,6),
                                                    Triple("Cytogenesis",0,6),
                                                    Triple("Death Fog",0,6),
                                                    Triple("Disintegrate",0,6),
                                                    Triple("Extension III",0,6),
                                                    Triple("Glassee",0,6),
                                                    Triple("Guards and Wards",0,6),
                                                    Triple("Haarpang's Magnificent Sphere of Freezing",0,6),
                                                    Triple("Hyptor's Total Recall",0,6),
                                                    Triple("Karnaac's Tranformation",0,6),
                                                    Triple("Lower Water",0,6),
                                                    Triple("Mirage Arcana",0,6),
                                                    Triple("Move Earth",0,6),
                                                    Triple("Part Water",0,6),
                                                    Triple("Project Image",0,6),
                                                    Triple("Stone to Flesh",0,6),
                                                    Triple("Tentacles",0,6),
                                                    Triple("Transmute Water into Dust",0,6),
                                                    Triple("Transmute Metal to Water",0,6),
                                                    Triple("Velimurio’s Merger",0,6),
                                                    Triple("Zarba's Sphere of Personal Inclement Weather",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Project Image",0,6),
                                                    Triple("Haarpang's Magnificent Sphere of Freezing",0,6),
                                                    Triple("Control Weather",0,6),
                                                    Triple("Disintegrate",0,6),
                                                    Triple("Extension III",0,6),
                                                    Triple("Glassee",0,6),
                                                    Triple("Hyptor's Total Recall",0,6),
                                                    Triple("Lower Water",0,6),
                                                    Triple("Move Earth",0,6),
                                                    Triple("Part Water",0,6),
                                                    Triple("Stone to Flesh",0,6),
                                                    Triple("Transmute Water to Dust",0,6),
                                                    Triple("Zarba's Sphere of Personal Inclement Weather",0,6),
                                                    Triple("Karnaac's Transformation",0,6),
                                                    Triple("Death Fog",0,6),
                                                    Triple("Guards and Wards",0,6),
                                                    Triple("Mirage Arcana",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Bone Javelin",0,7),
                                                    Triple("Command Element",0,7),
                                                    Triple("Create Shade",0,7),
                                                    Triple("Duo-Dimension",0,7),
                                                    Triple("Life Creation",0,7),
                                                    Triple("Phase Door",0,7),
                                                    Triple("Reverse Gravity",0,7),
                                                    Triple("Statue",0,7),
                                                    Triple("Teleport without Error",0,7),
                                                    Triple("Torment",0,7),
                                                    Triple("Transmute Rock to Lava",0,7),
                                                    Triple("Truename",0,7),
                                                    Triple("Tybalt's Planar Pacifier",0,7),
                                                    Triple("Vanish",0,7),
                                                    Triple("Zargosa's Opulent Manor House",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Reverse Gravity",0,7),
                                                    Triple("Duo-Dimension",0,7),
                                                    Triple("Phase Door",0,7),
                                                    Triple("Statue",0,7),
                                                    Triple("Teleport without Error",0,7),
                                                    Triple("Transmute Rock to Lava",0,7),
                                                    Triple("Vanish",0,7),
                                                    Triple("Zargosa's Opulent Manor House",0,7),
                                                    Triple("Truename",0,7),
                                                    Triple("Torment",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Glassteel",0,8),
                                                    Triple("Haarpang's Magnificent Sphere of Telekinesis",0,8),
                                                    Triple("Incendiary Cloud",0,8),
                                                    Triple("Permanency",0,8),
                                                    Triple("Polymorph Any Object",0,8),
                                                    Triple("Sink",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Incendiary Cloud",0,8),
                                                    Triple("Glassteel",0,8),
                                                    Triple("Permanency",0,8),
                                                    Triple("Polymorph Any Object",0,8),
                                                    Triple("Sink",0,8),
                                                    Triple("Haarpang's Magnificent Sphere of Telekinesis",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Crystalbrittle",0,9),
                                                    Triple("Hyptor's Disjunction",0,9),
                                                    Triple("Ring of Swords",0,9),
                                                    Triple("Shape Change",0,9),
                                                    Triple("Succor",0,9),
                                                    Triple("Teleport Intercampaignia",0,9),
                                                    Triple("Teleport Intragenre",0,9),
                                                    Triple("Tempestcone",0,9),
                                                    Triple("Temporal Stasis",0,9),
                                                    Triple("Time Stop",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Hyptor's Disjunction",0,9),
                                                    Triple("Succor",0,9),
                                                    Triple("Crystalbrittle",0,9),
                                                    Triple("Shape Change",0,9),
                                                    Triple("Teleport Intercampaignia",0,9),
                                                    Triple("Teleport Intragenre",0,9),
                                                    Triple("Temporal Stasis",0,9),
                                                    Triple("Time Stop",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.CONJURATION -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Armor",0,1),
                                                    Triple("Conjure Mount",0,1),
                                                    Triple("Find Familiar",0,1),
                                                    Triple("Grease",0,1),
                                                    Triple("Power Word: Cartwheel",0,1),
                                                    Triple("Power Word: Moon",0,1),
                                                    Triple("Power Word: Summersault",0,1),
                                                    Triple("Push",0,1),
                                                    Triple("Unseen Servant",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Armor",0,1),
                                                    Triple("Conjure Mount",0,1),
                                                    Triple("Find Familiar",0,1),
                                                    Triple("Grease",0,1),
                                                    Triple("Push",0,1),
                                                    Triple("Unseen Servant",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Aname's Extra-Dimensional Mallet",0,2),
                                                    Triple("Choke",0,2),
                                                    Triple("Glitterdust",0,2),
                                                    Triple("Munz's Bolt of Acid",0,2),
                                                    Triple("Power Word: Belch",0,2),
                                                    Triple("Power Word: Detect",0,2),
                                                    Triple("Power Word: Light",0,2),
                                                    Triple("Summon Swarm",0,2),
                                                    Triple("Zed's Crystal Dagger",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Summon Swarm",0,2),
                                                    Triple("Glitterdust",0,2),
                                                    Triple("Munz's Bolt of Acid",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Flame Arrow",0,3),
                                                    Triple("Material",0,3),
                                                    Triple("Monster Summoning I",0,3),
                                                    Triple("Phantom Steed",0,3),
                                                    Triple("Power Word: Attack",0,3),
                                                    Triple("Power Word: Burn",0,3),
                                                    Triple("Power Word: Chill",0,3),
                                                    Triple("Sepia Snake Sigil",0,3),
                                                    Triple("Zed's Crystal Dirk",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Phantom Steed",0,3),
                                                    Triple("Flame Arrow",0,3),
                                                    Triple("Sepia Snake Sigil",0,3),
                                                    Triple("Monster Summoning I",0,3),
                                                    Triple("Material",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Monster Summoning II",0,4),
                                                    Triple("Zargoza's Tentacled Fury",0,4),
                                                    Triple("Duplicate",0,4),
                                                    Triple("Power Word: Anosmitize",0,4),
                                                    Triple("Power Word: Freeze",0,4),
                                                    Triple("Power Word: Slow",0,4),
                                                    Triple("Segwick's Tool Box",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Monster Summoning II",0,4),
                                                    Triple("Zargosa’s Tentacled Fury",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Conjure Elemental",0,5),
                                                    Triple("Drayton's Hidden Stash",0,5),
                                                    Triple("Hyptor's Faithful Bitch-Hound",0,5),
                                                    Triple("Monster Summoning III",0,5),
                                                    Triple("Power Word: Charm",0,5),
                                                    Triple("Power Word: Fear",0,5),
                                                    Triple("Power Word: Sleep",0,5),
                                                    Triple("Summon Shadow",0,5),
                                                    Triple("Wall of Bones",0,5),
                                                    Triple("Water Bomb",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Drayton's Hidden Stash",0,5),
                                                    Triple("Summon Shadow",0,5),
                                                    Triple("Conjure Elemental",0,5),
                                                    Triple("Hyptor’s Faithful Bitch-Hound",0,5),
                                                    Triple("Monster Summoning III",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Conjure Animals",0,6),
                                                    Triple("Ensnarement",0,6),
                                                    Triple("Fandango's Fiery Constrictor",0,6),
                                                    Triple("Invisible Stalker",0,6),
                                                    Triple("Monster Summoning IV",0,6),
                                                    Triple("Power Word: Forget",0,6),
                                                    Triple("Power Word: Silence",0,6),
                                                    Triple("Tentacles",0,6),
                                                    Triple("Wall of Thorns",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Ensnarement",0,6),
                                                    Triple("Invisible Stalker",0,6),
                                                    Triple("Conjure Animals",0,6),
                                                    Triple("Monster Summoning IV",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Cacodemon",0,7),
                                                    Triple("Limited Wish",0,7),
                                                    Triple("Monster Summoning V",0,7),
                                                    Triple("Power Word: Deafness",0,7),
                                                    Triple("Power Word: Dispel",0,7),
                                                    Triple("Power Word: Heal",0,7),
                                                    Triple("Power Word: Stun",0,7),
                                                    Triple("Zargosa's Instant Summons",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Limited Wish",0,7),
                                                    Triple("Cacodemon",0,7),
                                                    Triple("Monster Summoning V",0,7),
                                                    Triple("Power Word: Stun",0,7),
                                                    Triple("Prismatic Wall",0,7),
                                                    Triple("Zargosa’s Instant Summons",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Grasping Death",0,8),
                                                    Triple("Jonid's Jewel",0,8),
                                                    Triple("Maze",0,8),
                                                    Triple("Monster Summoning VI",0,8),
                                                    Triple("Power Word: Banish",0,8),
                                                    Triple("Power Word: Blind",0,8),
                                                    Triple("Power Word: Terrify",0,8),
                                                    Triple("Symbol",0,8),
                                                    Triple("Trap the Soul",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Grasping Death",0,8),
                                                    Triple("Maze",0,8),
                                                    Triple("Monster Summoning VI",0,8),
                                                    Triple("Power Word: Blind",0,8),
                                                    Triple("Symbol",0,8),
                                                    Triple("Trap the Soul",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Alter Reality",0,9),
                                                    Triple("Demon Flame",0,9),
                                                    Triple("Gate",0,9),
                                                    Triple("Monster Summoning VII",0,9),
                                                    Triple("Power Word: Annihilate",0,9),
                                                    Triple("Power Word: Dance",0,9),
                                                    Triple("Power Word: Kill",0,9),
                                                    Triple("Prismatic Sphere",0,9),
                                                    Triple("Wish",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Prismatic Sphere",0,9),
                                                    Triple("Demon Flame",0,9),
                                                    Triple("Gate",0,9),
                                                    Triple("Power Word: Kill",0,9),
                                                    Triple("Wish",0,9),
                                                    Triple("Monster Summoning VII",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.DIVINATION  -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Detect Disease",0,1),
                                                    Triple("Detect Illusion",0,1),
                                                    Triple("Detect Magic",0,1),
                                                    Triple("Detect Phase",0,1),
                                                    Triple("Detect Undead",0,1),
                                                    Triple("Divining Rod",0,1),
                                                    Triple("Fog Vision",0,1),
                                                    Triple("Identify",0,1),
                                                    Triple("Read Magic",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Detect Magic",0,1),
                                                    Triple("Detect Undead",0,1),
                                                    Triple("Fog Vision",0,1),
                                                    Triple("Identify",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Death Recall",0,2),
                                                    Triple("Detect Charm",0,2),
                                                    Triple("Detect Evil",0,2),
                                                    Triple("Detect Invisibility",0,2),
                                                    Triple("Detect Life",0,2),
                                                    Triple("ESP",0,2),
                                                    Triple("Find Traps",0,2),
                                                    Triple("Know Alignment",0,2),
                                                    Triple("Locate Object",0,2),
                                                    Triple("Premonition",0,2),
                                                    Triple("Reveal Secret Portal",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Detect Evil",0,2),
                                                    Triple("Detect Invisibility",0,2),
                                                    Triple("ESP",0,2),
                                                    Triple("Know Alignment",0,2),
                                                    Triple("Locate Object",0,2),
                                                    Triple("Premonition",0,2),
                                                    Triple("Reveal Secret Portal",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            // Same in both.
                                            listOf(
                                                Triple("Clairaudience",0,3),
                                                Triple("Clairvoyance",0,3)
                                            )
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Detect Lie",0,4),
                                                    Triple("Detect Scrying",0,4),
                                                    Triple("Divination Enhancement",0,4),
                                                    Triple("Find Treasure",0,4),
                                                    Triple("Magic Mirror",0,4),
                                                    Triple("Omen",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Detect Scrying",0,4),
                                                    Triple("Magic Mirror",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Contact Other Plane",0,5),
                                                    Triple("False Vision",0,5),
                                                    Triple("Segwick's Seeking",0,5),
                                                    Triple("Wizard's Oracle",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Contact Other Plane",0,5),
                                                    Triple("False Vision",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Detect Ulterior Motive",0,6),
                                                    Triple("Legend Lore",0,6),
                                                    Triple("Revelation",0,6),
                                                    Triple("True Seeing",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Legend Lore",0,6),
                                                    Triple("True Seeing",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Anticipation",0,7),
                                                    Triple("Find the Path",0,7),
                                                    Triple("Manor's Mind Vision",0,7),
                                                    Triple("Vision",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Vision",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Diviner's Insight",0,8),
                                                    Triple("Screen",0,8),
                                                    Triple("Jonid's Jewel",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Screen",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Detect All",0,9),
                                                    Triple("Foresight",0,9),
                                                    Triple("Glyph of Divination",0,9),
                                                    Triple("Greater Divination Enhancement",0,9),
                                                    Triple("Seek Teleporter",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Foresight",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.ENCHANTMENT -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Aura of Innocence",0,1),
                                                    Triple("Befriend",0,1),
                                                    Triple("Charm Person",0,1),
                                                    Triple("Divining Rod",0,1),
                                                    Triple("Hypnotism",0,1),
                                                    Triple("Magic Stone",0,1),
                                                    Triple("Protective Amulet",0,1),
                                                    Triple("Remove Thirst",0,1),
                                                    Triple("Run",0,1),
                                                    Triple("Shift Blame",0,1),
                                                    Triple("Sleep",0,1),
                                                    Triple("Taunt",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Aura of Innocence",0,1),
                                                    Triple("Befriend",0,1),
                                                    Triple("Charm Person",0,1),
                                                    Triple("Hypnotism",0,1),
                                                    Triple("Run",0,1),
                                                    Triple("Shift Blame",0,1),
                                                    Triple("Sleep",0,1),
                                                    Triple("Taunt",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Bind",0,2),
                                                    Triple("Deeppockets",0,2),
                                                    Triple("Forget",0,2),
                                                    Triple("Fustis's Mnemonic Enhancer",0,2),
                                                    Triple("Murgain's Muster Strength",0,2),
                                                    Triple("Proadus’ Uncontrollable Fit of Laughter",0,2),
                                                    Triple("Ray of Enfeeblement",0,2),
                                                    Triple("Scare",0,2),
                                                    Triple("Total Control",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Ray of Enfeeblement",0,2),
                                                    Triple("Scare",0,2),
                                                    Triple("Total Control",0,2),
                                                    Triple("Forget",0,2),
                                                    Triple("Bind",0,2),
                                                    Triple("Proadus’ Uncontrollable Fit of Laughter",0,2),
                                                    Triple("Murgain's Muster Strength",0,2),
                                                    Triple("Deeppockets",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Bone Club",0,3),
                                                    Triple("Delay Death",0,3),
                                                    Triple("Empathic Link",0,3),
                                                    Triple("Hold Person",0,3),
                                                    Triple("No Fear",0,3),
                                                    Triple("Perceived Malignment",0,3),
                                                    Triple("Suggestion",0,3),
                                                    Triple("Yargroove's Eidelon",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Hold Person",0,3),
                                                    Triple("No Fear",0,3),
                                                    Triple("Perceived Malignment",0,3),
                                                    Triple("Suggestion",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Charm Monster",0,4),
                                                    Triple("Confusion",0,4),
                                                    Triple("Emotion",0,4),
                                                    Triple("Enchanted Weapon",0,4),
                                                    Triple("Fire Charm",0,4),
                                                    Triple("Fumble",0,4),
                                                    Triple("Mage Lock",0,4),
                                                    Triple("Magic Mirror",0,4),
                                                    Triple("Stirring Oration",0,4),
                                                    Triple("Zargosa's Lodge of Protection",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Zargosa's Lodge of Protection",0,4),
                                                    Triple("Charm Monster",0,4),
                                                    Triple("Confusion",0,4),
                                                    Triple("Fire Charm",0,4),
                                                    Triple("Fumble",0,4),
                                                    Triple("Stirring Oration",0,4),
                                                    Triple("Magic Mirror",0,4),
                                                    Triple("Emotion",0,4),
                                                    Triple("Mage Lock",0,4),
                                                    Triple("Enchanted Weapon",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Chaos",0,5),
                                                    Triple("Dolor",0,5),
                                                    Triple("Domination",0,5),
                                                    Triple("Drayton’s Engaging Conversation",0,5),
                                                    Triple("Fabricate",0,5),
                                                    Triple("Feeblemind",0,5),
                                                    Triple("Hold Monster",0,5),
                                                    Triple("Magic Staff",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Fabricate",0,5),
                                                    Triple("Chaos",0,5),
                                                    Triple("Dolor",0,5),
                                                    Triple("Domination",0,5),
                                                    Triple("Feeblemind",0,5),
                                                    Triple("Hold Monster",0,5),
                                                    Triple("Drayton’s Engaging Conversation",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Charm of Undying Devotion",0,6),
                                                    Triple("Enchant an Item",0,6),
                                                    Triple("Eyebite",0,6),
                                                    Triple("Geas",0,6),
                                                    Triple("Guards and Wards",0,6),
                                                    Triple("Mass Suggestion",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Eyebite",0,6),
                                                    Triple("Charm of Undying Devotion",0,6),
                                                    Triple("Mass Suggestion",0,6),
                                                    Triple("Enchant an Item",0,6),
                                                    Triple("Geas",0,6),
                                                    Triple("Guards and Wards",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Anger Deity",0,7),
                                                    Triple("Charm Plants",0,7),
                                                    Triple("Major Domination",0,7),
                                                    Triple("Mass Hypnosis",0,7),
                                                    Triple("Steal Enchantment",0,7),
                                                    Triple("Truename",0,7),
                                                    Triple("Tybalt's Planar Pacifier",0,7),
                                                    Triple("Zarba’s Sphere of Insanity",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Anger Deity",0,7),
                                                    Triple("Charm Plants",0,7),
                                                    Triple("Zarba’s Sphere of Insanity",0,7),
                                                    Triple("Truename",0,7),
                                                    Triple("Shadow Walk",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            //No, this isn't a mistake. They are both the same.
                                            listOf(
                                                Triple("Sink",0,8),
                                                Triple("Antipathy-Sympathy",0,8),
                                                Triple("Mass Charm",0,8),
                                                Triple("Munari’s Irresistible Jig",0,8),
                                                Triple("Binding",0,8),
                                                Triple("Mimic Caster",0,8),
                                                Triple("Demand",0,8)
                                            )
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Hyptor's Disjunction",0,9),
                                                    Triple("Mass Domination",0,9),
                                                    Triple("Programmed Amnesia",0,9),
                                                    Triple("Succor",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Hyptor's Disjunction",0,9),
                                                    Triple("Succor",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.EVOCATION   -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Alarm",0,1),
                                                    Triple("Bash Door",0,1),
                                                    Triple("Chromatic Orb",0,1),
                                                    Triple("Copy",0,1),
                                                    Triple("Fireball, Sidewinder Factor 1",0,1),
                                                    Triple("Haarpang's Floating Cart",0,1),
                                                    Triple("Icy Sphere",0,1),
                                                    Triple("Jack Punch",0,1),
                                                    Triple("Kachirut's Exploding Palm",0,1),
                                                    Triple("Magic Missile",0,1),
                                                    Triple("Magic Shield",0,1),
                                                    Triple("Resist Cold",0,1),
                                                    Triple("Wall of Fog",0,1),
                                                    Triple("Yudder's Whistle of Hell's Gate",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Alarm",0,1),
                                                    Triple("Bash Door",0,1),
                                                    Triple("Chromatic Orb",0,1),
                                                    Triple("Fireball, Sidewinder Factor 1",0,1),
                                                    Triple("Haarpang's Floating Cart",0,1),
                                                    Triple("Magic Missile",0,1),
                                                    Triple("Magic Shield",0,1),
                                                    Triple("Wall of Fog",0,1),
                                                    Triple("Yudder's Whistle of Hell's Gate",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Chain of Fire",0,2),
                                                    Triple("Cloud of Pummeling Fists",0,2),
                                                    Triple("Fireball, Sidewinder Factor 2",0,2),
                                                    Triple("Fireball, Skipping Betty",0,2),
                                                    Triple("Flaming Sphere",0,2),
                                                    Triple("Heat Seeking Fist of Thunder",0,2),
                                                    Triple("Ice Knife",0,2),
                                                    Triple("Icy Sphere",0,2),
                                                    Triple("Kachirut's Kinetic Strike",0,2),
                                                    Triple("Magic Missile, Sidewinder",0,2),
                                                    Triple("Magic Missile of Skewering",0,2),
                                                    Triple("Shield Screen",0,2),
                                                    Triple("Stinking Cloud",0,2),
                                                    Triple("Web",0,2),
                                                    Triple("Whip",0,2),
                                                    Triple("Zed's Crystal Dagger",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Chain of Fire",0,2),
                                                    Triple("Cloud of Pummeling Fists",0,2),
                                                    Triple("Fireball, Sidewinder Factor 2",0,2),
                                                    Triple("Fireball, Skipping Betty",0,2),
                                                    Triple("Flaming Sphere",0,2),
                                                    Triple("Heat Seeking Fist of Thunder",0,2),
                                                    Triple("Magic Missile of Skewering",0,2),
                                                    Triple("Stinking Cloud",0,2),
                                                    Triple("Web",0,2),
                                                    Triple("Whip",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Zargosa’s Flaming Spheres of Torment",0,3),
                                                    Triple("Material",0,3),
                                                    Triple("Bash Face",0,3),
                                                    Triple("Fireball",0,3),
                                                    Triple("Fireball, Sidewinder Factor 3",0,3),
                                                    Triple("Fireball, Scatter-Blast",0,3),
                                                    Triple("Lightning Bolt",0,3),
                                                    Triple("Preemptive Strike",0,3),
                                                    Triple("Sure Grip Snare",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Bash Face",0,3),
                                                    Triple("Fireball",0,3),
                                                    Triple("Fireball, Sidewinder Factor 3",0,3),
                                                    Triple("Fireball, Scatter-Blast",0,3),
                                                    Triple("Force Hammer",0,3),
                                                    Triple("Glyph of Ice",0,3),
                                                    Triple("Glyph of Sniping",0,3),
                                                    Triple("Lightning Bolt",0,3),
                                                    Triple("Material",0,3),
                                                    Triple("Morton's Minute Meteors",0,3),
                                                    Triple("Preemptive Strike",0,3),
                                                    Triple("Sure Grip Snare",0,3),
                                                    Triple("Wall of Water",0,3),
                                                    Triple("Zargosa’s Flaming Spheres of Torment",0,3),
                                                    Triple("Zed's Crystal Dirk",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Delayed Magic Missile",0,4),
                                                    Triple("Dig",0,4),
                                                    Triple("Divination Enhancement",0,4),
                                                    Triple("Exploding Glyph",0,4),
                                                    Triple("Fire Shield",0,4),
                                                    Triple("Fire Trap",0,4),
                                                    Triple("Fireball, Land Scraper" ,0,4),
                                                    Triple("Fireball, Sidewinder Factor 4",0,4),
                                                    Triple("Fireball, Volley",0,4),
                                                    Triple("Force Grenade",0,4),
                                                    Triple("Haarpang’s Magnificent Sphere of Resiliency",0,4),
                                                    Triple("Ice Storm",0,4),
                                                    Triple("Mist of Corralling",0,4),
                                                    Triple("Shout",0,4),
                                                    Triple("Silver Globes",0,4),
                                                    Triple("Wall of Acid",0,4),
                                                    Triple("Wall of Fire",0,4),
                                                    Triple("Wall of Ice",0,4),
                                                    Triple("Wimpel's Dispelling Screen",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Fire Shield",0,4),
                                                    Triple("Dig",0,4),
                                                    Triple("Fireball, Land Scraper",0,4),
                                                    Triple("Fireball, Sidewinder Factor 4",0,4),
                                                    Triple("Fireball, Volley",0,4),
                                                    Triple("Ice Storm",0,4),
                                                    Triple("Mist of Corralling",0,4),
                                                    Triple("Shout",0,4),
                                                    Triple("Wall of Acid",0,4),
                                                    Triple("Wall of Fire",0,4),
                                                    Triple("Wall of Ice",0,4),
                                                    Triple("Fire Trap",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Cloudkill",0,5),
                                                    Triple("Dream",0,5),
                                                    Triple("Fireball, Sidewinder Factor 5",0,5),
                                                    Triple("Fireball, Torrential",0,5),
                                                    Triple("Haarpang's Polar Screen",0,5),
                                                    Triple("Lyggl's Cone of Cold",0,5),
                                                    Triple("Preston's Moonbow",0,5),
                                                    Triple("Sending",0,5),
                                                    Triple("Shincock's Major Missile",0,5),
                                                    Triple("Stone Sphere",0,5),
                                                    Triple("Wall of Force",0,5),
                                                    Triple("Wall of Iron",0,5),
                                                    Triple("Wall of Stone",0,5),
                                                    Triple("Zarba’s Guardian Hand",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Drayton’s Engaging Conversation",0,5),
                                                    Triple("Cloudkill",0,5),
                                                    Triple("Fireball, Sidewinder Factor 5",0,5),
                                                    Triple("Fireball, Torrential",0,5),
                                                    Triple("Lyggl's Cone of Cold",0,5),
                                                    Triple("Sending",0,5),
                                                    Triple("Stone Sphere",0,5),
                                                    Triple("Wall of Force",0,5),
                                                    Triple("Wall of Iron",0,5),
                                                    Triple("Wall of Stone",0,5),
                                                    Triple("Zarba’s Guardian Hand",0,5),
                                                    Triple("Dream",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Body Heat Activation Spell",0,6),
                                                    Triple("Bradley's Besieging Bolt",0,6),
                                                    Triple("Chain Lightning",0,6),
                                                    Triple("Contingency",0,6),
                                                    Triple("Death Fog",0,6),
                                                    Triple("Fireball, Proximity Fused",0,6),
                                                    Triple("Fireball, Show-No-Mercy",0,6),
                                                    Triple("Gauntlet of Teeth",0,6),
                                                    Triple("Guards and Wards",0,6),
                                                    Triple("Haarpang’s Magnificent Sphere of Freezing",0,6),
                                                    Triple("Haarpang's Orb of Containment",0,6),
                                                    Triple("Kaarnac's Transformation",0,6),
                                                    Triple("Snap Drake",0,6),
                                                    Triple("Spiritwrack",0,6),
                                                    Triple("Zarba's Shoving Hand",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Haarpang’s Magnificent Sphere of Freezing",0,6),
                                                    Triple("Kaarnac's Transformation",0,6),
                                                    Triple("Death Fog",0,6),
                                                    Triple("Guards and Wards",0,6),
                                                    Triple("Spiritwrack",0,6),
                                                    Triple("Body Heat Activation Spell",0,6),
                                                    Triple("Chain Lightning",0,6),
                                                    Triple("Contingency",0,6),
                                                    Triple("Fireball, Proximity Fused",0,6),
                                                    Triple("Fireball, Show-No-Mercy",0,6),
                                                    Triple("Zarba's Shoving Hand",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Bone Javelin",0,7),
                                                    Triple("Dragon Breath",0,7),
                                                    Triple("Fireball, Delayed Blast",0,7),
                                                    Triple("Flame Chase",0,7),
                                                    Triple("Forcecage",0,7),
                                                    Triple("Hyptor’s Shimmering Sword",0,7),
                                                    Triple("Limited Wish",0,7),
                                                    Triple("Merrywether's Frost Fist",0,7),
                                                    Triple("Torment",0,7),
                                                    Triple("Zarba's Grasping Hand",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Fireball, Delayed Blast",0,7),
                                                    Triple("Forcecage",0,7),
                                                    Triple("Hyptor’s Shimmering Sword",0,7),
                                                    Triple("Zarba's Grasping Hand",0,7),
                                                    Triple("Torment",0,7),
                                                    Triple("Limited Wish",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Binding",0,8),
                                                    Triple("Haarpang’s Magnificent Sphere of Telekinesis",0,8),
                                                    Triple("Demand",0,8),
                                                    Triple("Fireball, Death Brusher",0,8),
                                                    Triple("Fireball, Maximus",0,8),
                                                    Triple("Zarba's Fist of Rage",0,8),
                                                    Triple("Incendiary Cloud",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Blizzard",0,8),
                                                    Triple("Demand",0,8),
                                                    Triple("Fireball, Death Brusher",0,8),
                                                    Triple("Fireball, Maximus",0,8),
                                                    Triple("Freeze",0,8),
                                                    Triple("Haarpang’s Magnificent Sphere of Telekinesis",0,8),
                                                    Triple("Hornet's Nest",0,8),
                                                    Triple("Incendiary Cloud",0,8),
                                                    Triple("Shooting Stars",0,8),
                                                    Triple("Zarba's Fist of Rage",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Astral Spell",0,9),
                                                    Triple("Elemental Aura",0,9),
                                                    Triple("Energy Drain",0,9),
                                                    Triple("Fireball, Lava Yield",0,9),
                                                    Triple("Ice Juggernaut",0,9),
                                                    Triple("Kachirut's White Lance",0,9),
                                                    Triple("Meteor Swarm",0,9),
                                                    Triple("Tempestcone",0,9),
                                                    Triple("Zarba's Crushing Hand",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Astral Spell",0,9),
                                                    Triple("Fireball, Lava Yield",0,9),
                                                    Triple("Meteor Swarm",0,9),
                                                    Triple("Zarba's Crushing Hand",0,9),
                                                    Triple("Energy Drain",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.ILLUSION    -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Audible Glamer",0,1),
                                                    Triple("Change Self",0,1),
                                                    Triple("Corpse Visage",0,1),
                                                    Triple("Faerie Phantoms",0,1),
                                                    Triple("Fool's Silver",0,1),
                                                    Triple("Gabal's Magic Aura",0,1),
                                                    Triple("Imaginary Friend",0,1),
                                                    Triple("Phantasmal Fireball",0,1),
                                                    Triple("Phantasmal Force",0,1),
                                                    Triple("Smell Immunity",0,1),
                                                    Triple("Spook",0,1),
                                                    Triple("Throw Voice",0,1),
                                                    Triple("Wrygal’s Delicious Deception",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Audible Glamer",0,1),
                                                    Triple("Change Self",0,1),
                                                    Triple("Faerie Phantoms",0,1),
                                                    Triple("Gabal's Magic Aura",0,1),
                                                    Triple("Phantasmal Fireball",0,1),
                                                    Triple("Phantasmal Force",0,1),
                                                    Triple("Phantasmal Force",0,1),
                                                    Triple("Phantom Armor",0,1),
                                                    Triple("Smell Immunity",0,1),
                                                    Triple("Spook",0,1),
                                                    Triple("Throw Voice",0,1),
                                                    Triple("Wrygal’s Delicious Deception",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Blindness",0,2),
                                                    Triple("Blur",0,2),
                                                    Triple("Dancing Shadows",0,2),
                                                    Triple("Deafness",0,2),
                                                    Triple("Deepen Shadows",0,2),
                                                    Triple("Fascinate",0,2),
                                                    Triple("Fool's Gold",0,2),
                                                    Triple("Hypnotic Pattern",0,2),
                                                    Triple("Improved Phantasmal Force",0,2),
                                                    Triple("Invisibility",0,2),
                                                    Triple("Mirror Image",0,2),
                                                    Triple("Misdirection",0,2),
                                                    Triple("Whispering Wind",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Blindness",0,2),
                                                    Triple("Blur",0,2),
                                                    Triple("Deafness",0,2),
                                                    Triple("Fascinate",0,2),
                                                    Triple("Gandle's Feeble Trap",0,2),
                                                    Triple("Hypnotic Pattern",0,2),
                                                    Triple("Improved Phantasmal Force",0,2),
                                                    Triple("Invisibility",0,2),
                                                    Triple("Mirror Image",0,2),
                                                    Triple("Misdirection",0,2),
                                                    Triple("Whispering Wind",0,2),
                                                    Triple("Fool's Gold",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Illusionary Script",0,3),
                                                    Triple("Invisibility, 10’ Radius",0,3),
                                                    Triple("Paralyzation",0,3),
                                                    Triple("Spectral Force",0,3),
                                                    Triple("Phantom Steed",0,3),
                                                    Triple("Wraithform",0,3),
                                                    Triple("Phantom Wind",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(

                                                    Triple("Illusionary Script",0,3),
                                                    Triple("Invisibility, 10’ Radius",0,3),
                                                    Triple("Paralyzation",0,3),
                                                    Triple("Phantom Steed",0,3),
                                                    Triple("Phantom Wind",0,3),
                                                    Triple("Spectral Force",0,3),
                                                    Triple("Wraithform",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Dispel Exhaustion",0,4),
                                                    Triple("Fear",0,4),
                                                    Triple("Hallucinatory Terrain",0,4),
                                                    Triple("Illusionary Wall",0,4),
                                                    Triple("Improved Invisibility",0,4),
                                                    Triple("Minor Creation",0,4),
                                                    Triple("Phantasmal Killer",0,4),
                                                    Triple("Rainbow Pattern",0,4),
                                                    Triple("Shadow Monsters",0,4),
                                                    Triple("Vacancy",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Shadow Monsters",0,4),
                                                    Triple("Dispel Exhaustion",0,4),
                                                    Triple("Fear",0,4),
                                                    Triple("Illusionary Wall",0,4),
                                                    Triple("Improved Invisibility",0,4),
                                                    Triple("Minor Creation",0,4),
                                                    Triple("Phantasmal Killer",0,4),
                                                    Triple("Rainbow Pattern",0,4),
                                                    Triple("Hallucinatory Terrain",0,4),
                                                    Triple("Vacancy",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Advanced Illusion",0,5),
                                                    Triple("Demi-Shadow Monster",0,5),
                                                    Triple("Dream",0,5),
                                                    Triple("Major Creation",0,5),
                                                    Triple("Seeming",0,5),
                                                    Triple("Shadow Door",0,5),
                                                    Triple("Shadow Magic",0,5),
                                                    Triple("Tempus Fugit",0,5)

                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Major Creation",0,5),
                                                    Triple("Advanced Illusion",0,5),
                                                    Triple("Demi-Shadow Monster",0,5),
                                                    Triple("Seeming",0,5),
                                                    Triple("Shadow Door",0,5),
                                                    Triple("Shadow Magic",0,5),
                                                    Triple("Tempus Fugit",0,5),
                                                    Triple("Dream",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Demi-Shadow Magic",0,6),
                                                    Triple("Mirage Arcana",0,6),
                                                    Triple("Mislead",0,6),
                                                    Triple("Perpetual Illusion",0,6),
                                                    Triple("Phantasmagoria",0,6),
                                                    Triple("Programmed Illusion",0,6),
                                                    Triple("Project Image",0,6),
                                                    Triple("Shades",0,6),
                                                    Triple("Veil",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Eyebite",0,6),
                                                    Triple("Mirage Arcana",0,6),
                                                    Triple("Project Image",0,6),
                                                    Triple("Demi-Shadow Magic",0,6),
                                                    Triple("Mislead",0,6),
                                                    Triple("Perpetual Illusion",0,6),
                                                    Triple("Phantasmagoria",0,6),
                                                    Triple("Programmed Illusion",0,6),
                                                    Triple("Shades",0,6),
                                                    Triple("Veil",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Mass Hypnosis",0,7),
                                                    Triple("Mass Invisibility",0,7),
                                                    Triple("Merryweather's Dramatic Death",0,7),
                                                    Triple("Sequester",0,7),
                                                    Triple("Shadow Walk",0,7),
                                                    Triple("Shadowcat",0,7),
                                                    Triple("Simulacrum",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(

                                                    Triple("Sequester",0,7),
                                                    Triple("Shadow Walk",0,7),
                                                    Triple("Mass Invisibility",0,7),
                                                    Triple("Simulacrum",0,7)
                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Mind Maze",0,8),
                                                    Triple("Screen",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Screen",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Shadow Creep",0,9),
                                                    Triple("Weird",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Weird",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }

                                SpellSchool.NECROMANCY  -> {

                                    when (spellLevel) {
                                        1   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Animate Dead Animals",0,1),
                                                    Triple("Chill Touch",0,1),
                                                    Triple("Corpse Visage",0,1),
                                                    Triple("Detect Undead",0,1),
                                                    Triple("Disable Hand",0,1),
                                                    Triple("Exterminate",0,1),
                                                    Triple("Ralph's Placid Arrow",0,1)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Chill Touch",0,1),
                                                    Triple("Detect Undead",0,1)
                                                )
                                            }
                                        }

                                        2   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Choke",0,2),
                                                    Triple("Death Recall",0,2),
                                                    Triple("Disable Foot",0,2),
                                                    Triple("Fihrsid's Horrid Armor",0,2),
                                                    Triple("Ghoul Touch",0,2),
                                                    Triple("Slow Healing",0,2),
                                                    Triple("Spectral Hand",0,2),
                                                    Triple("Spy of Derijnah",0,2)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Spectral Hand",0,2)
                                                )
                                            }
                                        }

                                        3   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Bone Club",0,3),
                                                    Triple("Charm Undead",0,3),
                                                    Triple("Delay Death",0,3),
                                                    Triple("Feign Death",0,3),
                                                    Triple("Hold Undead",0,3),
                                                    Triple("Hovering Skull",0,3),
                                                    Triple("Murgain's Migraine",0,3),
                                                    Triple("Pain Touch",0,3),
                                                    Triple("Rot Dawgs",0,3),
                                                    Triple("Vampiric Touch",0,3)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Charm Undead",0,3),
                                                    Triple("Feign Death",0,3),
                                                    Triple("Hold Undead",0,3),
                                                    Triple("Murgain's Migraine",0,3),
                                                    Triple("Vampiric Touch",0,3)
                                                )
                                            }
                                        }

                                        4   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Contagion",0,4),
                                                    Triple("Enervation",0,4),
                                                    Triple("Poison",0,4),
                                                    Triple("Zombie Slave",0,4)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Contagion",0,4),
                                                    Triple("Enervation",0,4),
                                                    Triple("Zombie Slave",0,4)
                                                )
                                            }
                                        }

                                        5   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Animate Dead",0,5),
                                                    Triple("Force Shapechange",0,5),
                                                    Triple("Magic Jar",0,5),
                                                    Triple("Mummy Rot",0,5),
                                                    Triple("Throbbing Bones",0,5),
                                                    Triple("Touch of Death",0,5),
                                                    Triple("Wall of Bones",0,5)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Summon Shadow",0,5),
                                                    Triple("Animate Dead",0,5),
                                                    Triple("Mummy Rot",0,5),
                                                    Triple("Touch of Death",0,5)
                                                )
                                            }
                                        }

                                        6   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(

                                                    Triple("Aliron's Dark Graft",0,6),
                                                    Triple("Dead Man's Eyes",0,6),
                                                    Triple("Death Spell",0,6),
                                                    Triple("Reincarnation",0,6)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Death Spell",0,6),
                                                    Triple("Reincarnation",0,6)
                                                )
                                            }
                                        }

                                        7   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Control Undead",0,7),
                                                    Triple("Finger of Death",0,7),
                                                    Triple("Harm",0,7),
                                                    Triple("Zombie Double",0,7)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Control Undead",0,7),
                                                    Triple("Finger of Death",0,7)

                                                )
                                            }
                                        }

                                        8   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Clone",0,8),
                                                    Triple("Death Chain",0,8),
                                                    Triple("Defoliate",0,8),
                                                    Triple("Shadow Form",0,8)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Clone",0,8)
                                                )
                                            }
                                        }

                                        9   -> {
                                            if (useSplat) { // SSG list with Errata

                                                listOf(
                                                    Triple("Blood Curse",0,9),
                                                    Triple("Death Rune",0,9),
                                                    Triple("Energy Drain",0,9),
                                                    Triple("Fawlgar's Grasping Death",0,9),
                                                    Triple("Immunity to Undeath",0,9),
                                                    Triple("Master Undead",0,9)
                                                )
                                            } else {        // GMG list

                                                listOf(
                                                    Triple("Death Rune",0,9),
                                                    Triple("Energy Drain",0,9),
                                                    Triple("Fawlgar's Grasping Death",0,9)
                                                )
                                            }
                                        }

                                        else-> emptyList()
                                    }
                                }
                            }

                        } else {
                            emptyList()
                        }
                    }
                }
            }

            SpCoDiscipline.DIVINE   -> {

                when (spellLevel) {

                    1   -> {
                        listOf(
                            Triple("Animal Friendship",1,1),
                            Triple("Awaken",1,1),
                            Triple("Befriend",1,1),
                            Triple("Bless",1,1),
                            Triple("Call Upon Faith",1,1),
                            Triple("Cause Light Wounds",1,1),
                            Triple("Ceremony",1,1),
                            Triple("Combine",1,1),
                            Triple("Command",1,1),
                            Triple("Create Water",1,1),
                            Triple("Cure Light Wounds",1,1),
                            Triple("Curse",1,1),
                            Triple("Darkness",1,1),
                            Triple("Destroy Water",1,1),
                            Triple("Detect Balance",1,1),
                            Triple("Detect Evil",1,1),
                            Triple("Detect Good",1,1),
                            Triple("Detect Magic",1,1),
                            Triple("Detect Poison",1,1),
                            Triple("Detect Snares & Pits",1,1),
                            Triple("Diagnose Injury",1,1),
                            Triple("Endure Cold",1,1),
                            Triple("Endure Heat",1,1),
                            Triple("Entangle",1,1),
                            Triple("Faerie Fire",1,1),
                            Triple("Fog Vision",1,1),
                            Triple("Indulgence",1,1),
                            Triple("Invisibility to Animals",1,1),
                            Triple("Invisibility to Undead",1,1),
                            Triple("Know Direction",1,1),
                            Triple("Light",1,1),
                            Triple("Locate Animals or Plants",1,1),
                            Triple("Log of Everburning",1,1),
                            Triple("Magical Stone",1,1),
                            Triple("Pass Without Trace",1,1),
                            Triple("Precipitation",1,1),
                            Triple("Predict Weather",1,1),
                            Triple("Protection From Evil",1,1),
                            Triple("Protection From Good",1,1),
                            Triple("Purify Food & Drink",1,1),
                            Triple("Putrify Food & Drink",1,1),
                            Triple("Remove Fear",1,1),
                            Triple("Alleviate Headache",1,1),
                            Triple("Repair Strain or Injury",1,1),
                            Triple("Sanctuary",1,1),
                            Triple("Shillelagh",1,1),
                            Triple("Strength of Stone",1,1),
                            Triple("Summon Divine Lackey",1,1),
                            Triple("Walking Corpse",1,1),
                            Triple("Wind Column",1,1)
                        )
                    }

                    2   -> {

                        listOf(
                            Triple("Adjustable Light",1,2),
                            Triple("Aid",1,2),
                            Triple("Animate Corpse",1,2),
                            Triple("Augury",1,2),
                            Triple("Aura of Comfort",1,2),
                            Triple("Badberry",1,2),
                            Triple("Barkskin",1,2),
                            Triple("Cause Moderate Wounds",1,2),
                            Triple("Charm Person",1,2),
                            Triple("Chant",1,2),
                            Triple("Chill Metal",1,2),
                            Triple("Create Holy Symbol",1,2),
                            Triple("Create Water",1,2),
                            Triple("Cure Color Blindness",1,2),
                            Triple("Cause Moderate Wounds",1,2),
                            Triple("Detect Charm",1,2),
                            Triple("Diminished Rite",1,2),
                            Triple("Draw Upon Holy Might",1,2),
                            Triple("Dust Devil",1,2),
                            Triple("Enthrall",1,2),
                            Triple("Extinguish",1,2),
                            Triple("Find Traps",1,2),
                            Triple("Fire Trap",1,2),
                            Triple("Flame Blade",1,2),
                            Triple("Goodberry",1,2),
                            Triple("Heal Light Wounds",1,2),
                            Triple("Heat Metal",1,2),
                            Triple("Hold Person",1,2),
                            Triple("Ignite",1,2),
                            Triple("Indulgence",1,2),
                            Triple("Know Alignment",1,2),
                            Triple("Lighten Load",1,2),
                            Triple("Mend Limb",1,2),
                            Triple("Mend Tendon",1,2),
                            Triple("Messenger",1,2),
                            Triple("Obscurement",1,2),
                            Triple("Premonition",1,2),
                            Triple("Produce Flame",1,2),
                            Triple("Reflecting Pool",1,2),
                            Triple("Resist Cold",1,2),
                            Triple("Resist Electricity",1,2),
                            Triple("Resist Fire",1,2),
                            Triple("Resist Gas",1,2),
                            Triple("Restore Movement",1,2),
                            Triple("Rigor Mortis",1,2),
                            Triple("Silence, 15' Radius",1,2),
                            Triple("Slow Poison",1,2),
                            Triple("Snake Charm",1,2),
                            Triple("Soften Stone",1,2),
                            Triple("Speak With Animals",1,2),
                            Triple("Spider Charm",1,2),
                            Triple("Spiritual Hammer",1,2),
                            Triple("Staunch Bleeding",1,2),
                            Triple("Straighten Wood",1,2),
                            Triple("Trip",1,2),
                            Triple("Undetectable Alignment",1,2),
                            Triple("Undetectable Charm",1,2),
                            Triple("Warp Wood",1,2),
                            Triple("Withdraw",1,2),
                            Triple("Wyvern Watch",1,2)
                        )
                    }

                    3   -> {

                        listOf(
                            Triple("Air Breathing",1,3),
                            Triple("Animate Dead",1,3),
                            Triple("Bestow Curse",1,3),
                            Triple("Call Lightning",1,3),
                            Triple("Cause Blindness/Deafness",1,3),
                            Triple("Cause Disease",1,3),
                            Triple("Cause Nasty Wounds",1,3),
                            Triple("Cloudburst",1,3),
                            Triple("Continual Darkness",1,3),
                            Triple("Continual Light",1,3),
                            Triple("Create Campfire",1,3),
                            Triple("Create Food & Water",1,3),
                            Triple("Cure Blindness/Deafness",1,3),
                            Triple("Cure Disease",1,3),
                            Triple("Cause Nasty Wounds",1,3),
                            Triple("Dispel Magic",1,3),
                            Triple("Emotion Control",1,3),
                            Triple("Enstrangle",1,3),
                            Triple("Feign Death",1,3),
                            Triple("Flame Walk",1,3),
                            Triple("Glyph of Warding",1,3),
                            Triple("Heal Moderate Wounds",1,3),
                            Triple("Helping Hand",1,3),
                            Triple("Hold Animals",1,3),
                            Triple("Hold Persons",1,3),
                            Triple("Indulgence",1,3),
                            Triple("Lesser Reanimation",1,3),
                            Triple("Locate Object",1,3),
                            Triple("Magic Vestment",1,3),
                            Triple("Meld Into Stone",1,3),
                            Triple("Mend Broken Bone",1,3),
                            Triple("Negative Plane Protection",1,3),
                            Triple("Obscure Object",1,3),
                            Triple("Prayer",1,3),
                            Triple("Protection From Fire",1,3),
                            Triple("Pyrotechnics",1,3),
                            Triple("Remove Curse",1,3),
                            Triple("Remove Paralysis",1,3),
                            Triple("Resist Acid and Caustic",1,3),
                            Triple("Shock Therapy",1,3),
                            Triple("Snare",1,3),
                            Triple("Speak to the Dead",1,3),
                            Triple("Spike Growth",1,3),
                            Triple("Spike Stones",1,3),
                            Triple("Starshine",1,3),
                            Triple("Stone Shape",1,3),
                            Triple("Summon Insects",1,3),
                            Triple("Tree",1,3),
                            Triple("Turn",1,3),
                            Triple("Vitality",1,3),
                            Triple("Ward Off Evil",1,3),
                            Triple("Ward Off Good",1,3),
                            Triple("Water Breathing",1,3),
                            Triple("Water Walk",1,3),
                            Triple("Weather Prediction",1,3),
                            Triple("White Hot Metal",1,3),
                            Triple("Wood Shape",1,3)
                        )
                    }

                    4   -> {

                        listOf(
                            Triple("Abjure",1,4),
                            Triple("Animal Summoning I",1,4),
                            Triple("Babble",1,4),
                            Triple("Blessed Warmth",1,4),
                            Triple("Call Woodland Beings",1,4),
                            Triple("Cause Lycanthropy",1,4),
                            Triple("Cause Serious Wounds",1,4),
                            Triple("Control Temperature, 10' Radius",1,4),
                            Triple("Cure Serious Wounds",1,4),
                            Triple("Detect Lie",1,4),
                            Triple("Divination",1,4),
                            Triple("Feign Life",1,4),
                            Triple("Free Action",1,4),
                            Triple("Giant Insect",1,4),
                            Triple("Gourmet Dinner",1,4),
                            Triple("Greater Restore Movement",1,4),
                            Triple("Grow",1,4),
                            Triple("Hallucinatory Forest",1,4),
                            Triple("Heal Nasty Wounds",1,4),
                            Triple("Hold Plant",1,4),
                            Triple("Imbue With Spell Ability",1,4),
                            Triple("Indulgence",1,4),
                            Triple("Join With Astral Traveler",1,4),
                            Triple("Lower Water",1,4),
                            Triple("Minor Raise Dead",1,4),
                            Triple("Neutralize Poison",1,4),
                            Triple("No Fear",1,4),
                            Triple("Plant Door",1,4),
                            Triple("Poison",1,4),
                            Triple("Produce Fire",1,4),
                            Triple("Protection from Elementals",1,4),
                            Triple("Protection from Lightning",1,4),
                            Triple("Protection from Lycanthropes",1,4),
                            Triple("Protection from Plants and Fungus",1,4),
                            Triple("Protection from Possession",1,4),
                            Triple("Protection from Undead",1,4),
                            Triple("Protection from Water",1,4),
                            Triple("Quench",1,4),
                            Triple("Raise Water",1,4),
                            Triple("Reflecting Pool",1,4),
                            Triple("Repel Insects",1,4),
                            Triple("Rigor Mortis, 10' Radius",1,4),
                            Triple("Shrink Insect",1,4),
                            Triple("Snakes to Sticks",1,4),
                            Triple("Speak with Plants",1,4),
                            Triple("Spell Immunity",1,4),
                            Triple("Spiders to Stones",1,4),
                            Triple("Spiritual Brigade",1,4),
                            Triple("Sticks to Snakes",1,4),
                            Triple("Stone Passage",1,4),
                            Triple("Stones to Spiders",1,4),
                            Triple("Tongues",1,4),
                            Triple("Touch of Death",1,4),
                            Triple("Undetectable Lie",1,4),
                            Triple("Warp Stone or Metal",1,4),
                            Triple("Weather Stasis",1,4),
                            Triple("Zone of Sweet Air",1,4)
                        )
                    }

                    5   -> {

                        listOf(
                            Triple("Air Walk",1,5),
                            Triple("Animal Growth",1,5),
                            Triple("Animal Reduction",1,5),
                            Triple("Animal Summoning II",1,5),
                            Triple("Anti-Plant Shell",1,5),
                            Triple("Atonement",1,5),
                            Triple("Blessed Abundance",1,5),
                            Triple("Cause Critical Wounds",1,5),
                            Triple("Commune",1,5),
                            Triple("Commune With Nature",1,5),
                            Triple("Control Winds",1,5),
                            Triple("Cure Critical Wounds",1,5),
                            Triple("Detect Ulterior Motives",1,5),
                            Triple("Dispel Evil",1,5),
                            Triple("Dispel Good",1,5),
                            Triple("Exorcism",1,5),
                            Triple("False Seeing",1,5),
                            Triple("Feeblemind",1,5),
                            Triple("Flame Strike",1,5),
                            Triple("Heal Serious Wounds",1,5),
                            Triple("Indulgence",1,5),
                            Triple("Insect Plague",1,5),
                            Triple("Magic Font",1,5),
                            Triple("Moonbeam",1,5),
                            Triple("Pass Plant",1,5),
                            Triple("Plane Shift",1,5),
                            Triple("Protection from Nefarians",1,5),
                            Triple("Quest",1,5),
                            Triple("Rainbow",1,5),
                            Triple("Raise Dead",1,5),
                            Triple("Reattach Limb",1,5),
                            Triple("Sink Into Earth",1,5),
                            Triple("Slay Living",1,5),
                            Triple("Spike Stones",1,5),
                            Triple("Transmute Mud to Rock",1,5),
                            Triple("Transmute Rock to Mud",1,5),
                            Triple("True Seeing",1,5),
                            Triple("Wall of Fire",1,5)
                        )
                    }

                    6   -> {

                        listOf(
                            Triple("Aerial Servant",1,6),
                            Triple("Animal Summoning III",1,6),
                            Triple("Animate Object",1,6),
                            Triple("Anti-Animal Shell",1,6),
                            Triple("Attach Limb",1,6),
                            Triple("Blade Barrier",1,6),
                            Triple("Conjure Animals",1,6),
                            Triple("Conjure Fire Elemental",1,6),
                            Triple("Control Winds",1,6),
                            Triple("Cure-All",1,6),
                            Triple("Easy March",1,6),
                            Triple("False Dawn",1,6),
                            Triple("Find the Path",1,6),
                            Triple("Forbiddance",1,6),
                            Triple("Greater Enstrangle",1,6),
                            Triple("Harm",1,6),
                            Triple("Hold Crowd",1,6),
                            Triple("Indulgence",1,6),
                            Triple("Live Oak",1,6),
                            Triple("Lose the Path",1,6),
                            Triple("Part Water",1,6),
                            Triple("Rain of Fire",1,6),
                            Triple("Speak With Monsters",1,6),
                            Triple("Stone Tell",1,6),
                            Triple("Transmute Water to Dust",1,6),
                            Triple("Transmute Dust to Water",1,6),
                            Triple("Transport via Plants",1,6),
                            Triple("Turn Wood",1,6),
                            Triple("Wall of Thorns",1,6),
                            Triple("Weather Summoning",1,6),
                            Triple("Whirlwind",1,6),
                            Triple("Word of Recall",1,6)
                        )
                    }

                    7   -> {

                        listOf(
                            Triple("Animate Rock",1,7),
                            Triple("Astral Spell",1,7),
                            Triple("Cause Inclement Weather",1,7),
                            Triple("Changestaff",1,7),
                            Triple("Chariot of Sustarre",1,7),
                            Triple("Confusion",1,7),
                            Triple("Conjure Earth Elemental",1,7),
                            Triple("Construct Temple",1,7),
                            Triple("Control Weather",1,7),
                            Triple("Creeping Doom",1,7),
                            Triple("Destruction",1,7),
                            Triple("Dismiss Earth Elemental",1,7),
                            Triple("Divine Inspiration",1,7),
                            Triple("Earthquake",1,7),
                            Triple("Exaction",1,7),
                            Triple("Finger of Death",1,7),
                            Triple("Fire Quench",1,7),
                            Triple("Fire Storm",1,7),
                            Triple("Gate",1,7),
                            Triple("Holy Word",1,7),
                            Triple("Indulgence",1,7),
                            Triple("Regenerate",1,7),
                            Triple("Reincarnation",1,7),
                            Triple("Repel Living Creatures & Plants",1,7),
                            Triple("Restoration",1,7),
                            Triple("Restorative Cure-All",1,7),
                            Triple("Resurrection",1,7),
                            Triple("Succor",1,7),
                            Triple("Sunray",1,7),
                            Triple("Symbol",1,7),
                            Triple("Transmute Lava to Rock",1,7),
                            Triple("Transmute Metal to Wood",1,7),
                            Triple("Transmute Rock to Lava",1,7),
                            Triple("Transmute Wood to Metal",1,7),
                            Triple("Unholy Word",1,7),
                            Triple("Wind Walk",1,7),
                            Triple("Wither",1,7)
                        )
                    }

                    else-> emptyList()
                }

            }

            SpCoDiscipline.NATURAL  -> {

                when (spellLevel) {
                    1   -> {

                        listOf(
                            Triple("Animal Friendship",2,1),
                            Triple("Awaken",2,1),
                            Triple("Befriend",2,1),
                            Triple("Bless",2,1),
                            Triple("Call Upon Faith",2,1),
                            Triple("Cause Light Wounds",2,1),
                            Triple("Ceremony",2,1),
                            Triple("Combine",2,1),
                            Triple("Command",2,1),
                            Triple("Create Water",2,1),
                            Triple("Cure Light Wounds",2,1),
                            Triple("Curse",2,1),
                            Triple("Darkness",2,1),
                            Triple("Destroy Water",2,1),
                            Triple("Detect Balance",2,1),
                            Triple("Detect Evil",2,1),
                            Triple("Detect Good",2,1),
                            Triple("Detect Magic",2,1),
                            Triple("Detect Poison",2,1),
                            Triple("Detect Snares & Pits",2,1),
                            Triple("Diagnose Injury",2,1),
                            Triple("Endure Cold",2,1),
                            Triple("Endure Heat",2,1),
                            Triple("Entangle",2,1),
                            Triple("Faerie Fire",2,1),
                            Triple("Fog Vision",2,1),
                            Triple("Indulgence",2,1),
                            Triple("Invisibility to Animals",2,1),
                            Triple("Invisibility to Undead",2,1),
                            Triple("Know Direction",2,1),
                            Triple("Light",2,1),
                            Triple("Locate Animals or Plants",2,1),
                            Triple("Log of Everburning",2,1),
                            Triple("Magical Stone",2,1),
                            Triple("Pass Without Trace",2,1),
                            Triple("Precipitation",2,1),
                            Triple("Predict Weather",2,1),
                            Triple("Protection From Evil",2,1),
                            Triple("Protection From Good",2,1),
                            Triple("Purify Food & Drink",2,1),
                            Triple("Putrify Food & Drink",2,1),
                            Triple("Remove Fear",2,1),
                            Triple("Alleviate Headache",2,1),
                            Triple("Repair Strain or Injury",2,1),
                            Triple("Sanctuary",2,1),
                            Triple("Shillelagh",2,1),
                            Triple("Strength of Stone",2,1),
                            Triple("Summon Divine Lackey",2,1),
                            Triple("Walking Corpse",2,1),
                            Triple("Wind Column",2,1)
                        )
                    }

                    2   -> {

                        listOf(
                            Triple("Adjustable Light",2,2),
                            Triple("Aid",2,2),
                            Triple("Animate Corpse",2,2),
                            Triple("Augury",2,2),
                            Triple("Aura of Comfort",2,2),
                            Triple("Badberry",2,2),
                            Triple("Barkskin",2,2),
                            Triple("Cause Moderate Wounds",2,2),
                            Triple("Charm Person",2,2),
                            Triple("Chant",2,2),
                            Triple("Chill Metal",2,2),
                            Triple("Create Holy Symbol",2,2),
                            Triple("Create Water",2,2),
                            Triple("Cure Color Blindness",2,2),
                            Triple("Cause Moderate Wounds",2,2),
                            Triple("Detect Charm",2,2),
                            Triple("Diminished Rite",2,2),
                            Triple("Draw Upon Holy Might",2,2),
                            Triple("Dust Devil",2,2),
                            Triple("Enthrall",2,2),
                            Triple("Extinguish",2,2),
                            Triple("Find Traps",2,2),
                            Triple("Fire Trap",2,2),
                            Triple("Flame Blade",2,2),
                            Triple("Goodberry",2,2),
                            Triple("Heal Light Wounds",2,2),
                            Triple("Heat Metal",2,2),
                            Triple("Hold Person",2,2),
                            Triple("Ignite",2,2),
                            Triple("Indulgence",2,2),
                            Triple("Know Alignment",2,2),
                            Triple("Lighten Load",2,2),
                            Triple("Mend Limb",2,2),
                            Triple("Mend Tendon",2,2),
                            Triple("Messenger",2,2),
                            Triple("Obscurement",2,2),
                            Triple("Premonition",2,2),
                            Triple("Produce Flame",2,2),
                            Triple("Reflecting Pool",2,2),
                            Triple("Resist Cold",2,2),
                            Triple("Resist Electricity",2,2),
                            Triple("Resist Fire",2,2),
                            Triple("Resist Gas",2,2),
                            Triple("Restore Movement",2,2),
                            Triple("Rigor Mortis",2,2),
                            Triple("Silence, 15' Radius",2,2),
                            Triple("Slow Poison",2,2),
                            Triple("Snake Charm",2,2),
                            Triple("Soften Stone",2,2),
                            Triple("Speak With Animals",2,2),
                            Triple("Spider Charm",2,2),
                            Triple("Spiritual Hammer",2,2),
                            Triple("Staunch Bleeding",2,2),
                            Triple("Straighten Wood",2,2),
                            Triple("Trip",2,2),
                            Triple("Undetectable Alignment",2,2),
                            Triple("Undetectable Charm",2,2),
                            Triple("Warp Wood",2,2),
                            Triple("Withdraw",2,2),
                            Triple("Wyvern Watch",2,2)
                        )
                    }

                    3   -> {

                        listOf(
                            Triple("Air Breathing",2,3),
                            Triple("Animate Dead",2,3),
                            Triple("Bestow Curse",2,3),
                            Triple("Call Lightning",2,3),
                            Triple("Cause Blindness/Deafness",2,3),
                            Triple("Cause Disease",2,3),
                            Triple("Cause Nasty Wounds",2,3),
                            Triple("Cloudburst",2,3),
                            Triple("Continual Darkness",2,3),
                            Triple("Continual Light",2,3),
                            Triple("Create Campfire",2,3),
                            Triple("Create Food & Water",2,3),
                            Triple("Cure Blindness/Deafness",2,3),
                            Triple("Cure Disease",2,3),
                            Triple("Cause Nasty Wounds",2,3),
                            Triple("Dispel Magic",2,3),
                            Triple("Emotion Control",2,3),
                            Triple("Enstrangle",2,3),
                            Triple("Feign Death",2,3),
                            Triple("Flame Walk",2,3),
                            Triple("Glyph of Warding",2,3),
                            Triple("Heal Moderate Wounds",2,3),
                            Triple("Helping Hand",2,3),
                            Triple("Hold Animals",2,3),
                            Triple("Hold Persons",2,3),
                            Triple("Indulgence",2,3),
                            Triple("Lesser Reanimation",2,3),
                            Triple("Locate Object",2,3),
                            Triple("Magic Vestment",2,3),
                            Triple("Meld Into Stone",2,3),
                            Triple("Mend Broken Bone",2,3),
                            Triple("Negative Plane Protection",2,3),
                            Triple("Obscure Object",2,3),
                            Triple("Prayer",2,3),
                            Triple("Protection From Fire",2,3),
                            Triple("Pyrotechnics",2,3),
                            Triple("Remove Curse",2,3),
                            Triple("Remove Paralysis",2,3),
                            Triple("Resist Acid and Caustic",2,3),
                            Triple("Shock Therapy",2,3),
                            Triple("Snare",2,3),
                            Triple("Speak to the Dead",2,3),
                            Triple("Spike Growth",2,3),
                            Triple("Spike Stones",2,3),
                            Triple("Starshine",2,3),
                            Triple("Stone Shape",2,3),
                            Triple("Summon Insects",2,3),
                            Triple("Tree",2,3),
                            Triple("Turn",2,3),
                            Triple("Vitality",2,3),
                            Triple("Ward Off Evil",2,3),
                            Triple("Ward Off Good",2,3),
                            Triple("Water Breathing",2,3),
                            Triple("Water Walk",2,3),
                            Triple("Weather Prediction",2,3),
                            Triple("White Hot Metal",2,3),
                            Triple("Wood Shape",2,3)
                        )
                    }

                    4   -> {

                        listOf(
                            Triple("Abjure",2,4),
                            Triple("Animal Summoning I",2,4),
                            Triple("Babble",2,4),
                            Triple("Blessed Warmth",2,4),
                            Triple("Call Woodland Beings",2,4),
                            Triple("Cause Lycanthropy",2,4),
                            Triple("Cause Serious Wounds",2,4),
                            Triple("Control Temperature, 10' Radius",2,4),
                            Triple("Cure Serious Wounds",2,4),
                            Triple("Detect Lie",2,4),
                            Triple("Divination",2,4),
                            Triple("Feign Life",2,4),
                            Triple("Free Action",2,4),
                            Triple("Giant Insect",2,4),
                            Triple("Gourmet Dinner",2,4),
                            Triple("Greater Restore Movement",2,4),
                            Triple("Grow",2,4),
                            Triple("Hallucinatory Forest",2,4),
                            Triple("Heal Nasty Wounds",2,4),
                            Triple("Hold Plant",2,4),
                            Triple("Imbue With Spell Ability",2,4),
                            Triple("Indulgence",2,4),
                            Triple("Join With Astral Traveler",2,4),
                            Triple("Lower Water",2,4),
                            Triple("Minor Raise Dead",2,4),
                            Triple("Neutralize Poison",2,4),
                            Triple("No Fear",2,4),
                            Triple("Plant Door",2,4),
                            Triple("Poison",2,4),
                            Triple("Produce Fire",2,4),
                            Triple("Protection from Elementals",2,4),
                            Triple("Protection from Lightning",2,4),
                            Triple("Protection from Lycanthropes",2,4),
                            Triple("Protection from Plants and Fungus",2,4),
                            Triple("Protection from Possession",2,4),
                            Triple("Protection from Undead",2,4),
                            Triple("Protection from Water",2,4),
                            Triple("Quench",2,4),
                            Triple("Raise Water",2,4),
                            Triple("Reflecting Pool",2,4),
                            Triple("Repel Insects",2,4),
                            Triple("Rigor Mortis, 10' Radius",2,4),
                            Triple("Shrink Insect",2,4),
                            Triple("Snakes to Sticks",2,4),
                            Triple("Speak with Plants",2,4),
                            Triple("Spell Immunity",2,4),
                            Triple("Spiders to Stones",2,4),
                            Triple("Spiritual Brigade",2,4),
                            Triple("Sticks to Snakes",2,4),
                            Triple("Stone Passage",2,4),
                            Triple("Stones to Spiders",2,4),
                            Triple("Tongues",2,4),
                            Triple("Touch of Death",2,4),
                            Triple("Undetectable Lie",2,4),
                            Triple("Warp Stone or Metal",2,4),
                            Triple("Weather Stasis",2,4),
                            Triple("Zone of Sweet Air",2,4)
                        )
                    }

                    5   -> {

                        listOf(
                            Triple("Air Walk",2,5),
                            Triple("Animal Growth",2,5),
                            Triple("Animal Reduction",2,5),
                            Triple("Animal Summoning II",2,5),
                            Triple("Anti-Plant Shell",2,5),
                            Triple("Atonement",2,5),
                            Triple("Blessed Abundance",2,5),
                            Triple("Cause Critical Wounds",2,5),
                            Triple("Commune",2,5),
                            Triple("Commune With Nature",2,5),
                            Triple("Control Winds",2,5),
                            Triple("Cure Critical Wounds",2,5),
                            Triple("Detect Ulterior Motives",2,5),
                            Triple("Dispel Evil",2,5),
                            Triple("Dispel Good",2,5),
                            Triple("Exorcism",2,5),
                            Triple("False Seeing",2,5),
                            Triple("Feeblemind",2,5),
                            Triple("Flame Strike",2,5),
                            Triple("Heal Serious Wounds",2,5),
                            Triple("Indulgence",2,5),
                            Triple("Insect Plague",2,5),
                            Triple("Magic Font",2,5),
                            Triple("Moonbeam",2,5),
                            Triple("Pass Plant",2,5),
                            Triple("Plane Shift",2,5),
                            Triple("Protection from Nefarians",2,5),
                            Triple("Quest",2,5),
                            Triple("Rainbow",2,5),
                            Triple("Raise Dead",2,5),
                            Triple("Reattach Limb",2,5),
                            Triple("Sink Into Earth",2,5),
                            Triple("Slay Living",2,5),
                            Triple("Spike Stones",2,5),
                            Triple("Transmute Mud to Rock",2,5),
                            Triple("Transmute Rock to Mud",2,5),
                            Triple("True Seeing",2,5),
                            Triple("Wall of Fire",2,5)
                        )
                    }

                    6   -> {

                        listOf(
                            Triple("Aerial Servant",2,6),
                            Triple("Animal Summoning III",2,6),
                            Triple("Animate Object",2,6),
                            Triple("Anti-Animal Shell",2,6),
                            Triple("Attach Limb",2,6),
                            Triple("Blade Barrier",2,6),
                            Triple("Conjure Animals",2,6),
                            Triple("Conjure Fire Elemental",2,6),
                            Triple("Control Winds",2,6),
                            Triple("Cure-All",2,6),
                            Triple("Easy March",2,6),
                            Triple("False Dawn",2,6),
                            Triple("Find the Path",2,6),
                            Triple("Forbiddance",2,6),
                            Triple("Greater Enstrangle",2,6),
                            Triple("Harm",2,6),
                            Triple("Hold Crowd",2,6),
                            Triple("Indulgence",2,6),
                            Triple("Live Oak",2,6),
                            Triple("Lose the Path",2,6),
                            Triple("Part Water",2,6),
                            Triple("Rain of Fire",2,6),
                            Triple("Speak With Monsters",2,6),
                            Triple("Stone Tell",2,6),
                            Triple("Transmute Water to Dust",2,6),
                            Triple("Transmute Dust to Water",2,6),
                            Triple("Transport via Plants",2,6),
                            Triple("Turn Wood",2,6),
                            Triple("Wall of Thorns",2,6),
                            Triple("Weather Summoning",2,6),
                            Triple("Whirlwind",2,6),
                            Triple("Word of Recall",2,6)
                        )
                    }

                    7   -> {

                        listOf(
                            Triple("Animate Rock",2,7),
                            Triple("Astral Spell",2,7),
                            Triple("Cause Inclement Weather",2,7),
                            Triple("Changestaff",2,7),
                            Triple("Chariot of Sustarre",2,7),
                            Triple("Confusion",2,7),
                            Triple("Conjure Earth Elemental",2,7),
                            Triple("Construct Temple",2,7),
                            Triple("Control Weather",2,7),
                            Triple("Creeping Doom",2,7),
                            Triple("Destruction",2,7),
                            Triple("Dismiss Earth Elemental",2,7),
                            Triple("Divine Inspiration",2,7),
                            Triple("Earthquake",2,7),
                            Triple("Exaction",2,7),
                            Triple("Finger of Death",2,7),
                            Triple("Fire Quench",2,7),
                            Triple("Fire Storm",2,7),
                            Triple("Gate",2,7),
                            Triple("Holy Word",2,7),
                            Triple("Indulgence",2,7),
                            Triple("Regenerate",2,7),
                            Triple("Reincarnation",2,7),
                            Triple("Repel Living Creatures & Plants",2,7),
                            Triple("Restoration",2,7),
                            Triple("Restorative Cure-All",2,7),
                            Triple("Resurrection",2,7),
                            Triple("Succor",2,7),
                            Triple("Sunray",2,7),
                            Triple("Symbol",2,7),
                            Triple("Transmute Lava to Rock",2,7),
                            Triple("Transmute Metal to Wood",2,7),
                            Triple("Transmute Rock to Lava",2,7),
                            Triple("Transmute Wood to Metal",2,7),
                            Triple("Unholy Word",2,7),
                            Triple("Wind Walk",2,7),
                            Triple("Wither",2,7)
                        )
                    }

                    else-> emptyList()
                }
            }

            else                    -> emptyList()
        }

    }
}