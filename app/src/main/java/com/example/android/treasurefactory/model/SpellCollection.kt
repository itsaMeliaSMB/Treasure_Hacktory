package com.example.android.treasurefactory.model

import org.jetbrains.annotations.NotNull

data class SpellCollection(
    @NotNull val sCollectID: Int,
    val hoardID: Int,
    var iconID: String,
    var name: String = "<Spell Scroll>",
    var type: SpCoType,
    var properties: List<Pair<String,Double>> = emptyList(), //TODO refactor existing scroll generation to return list
    var gpValue: Double = 0.0, //TODO add as field to db entities
    var xpValue: Int = 0, //TODO add as field to db entities
    var spells: List<Spell> = emptyList(),
    var curse: String = "") {

    fun calculateGPValue(): Double {

        var gpTotal: Double = 0.0

        if (properties.isNotEmpty()) { properties.forEach { (_, gpValue) -> gpTotal += gpValue} }

        if (spells.isNotEmpty()) { spells.forEach {

                gpTotal += if (it.spellLevel == 0) {
                    75.0
                } else {
                    (300.0 * it.spellLevel)
                }
            }
        }

        return gpTotal
    }

    fun calculateXpValue() : Int {

        var xpTotal = 0

        if (spells.isNotEmpty()) { spells.forEach {

            xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel)

            }
        }

        return xpTotal
    }
}