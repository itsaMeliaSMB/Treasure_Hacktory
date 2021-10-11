package com.example.android.treasurefactory

import androidx.room.PrimaryKey

data class HMSpellCollection(@PrimaryKey(autoGenerate = true) val sCollectID: Int,
                             val name: String = "<Spell Scroll>", val isArcane: Boolean = true,
                             val type: String = "scroll", var properties: Map<String,Int> = emptyMap(),
                             var spells: MutableList<HMSpell> = mutableListOf<HMSpell>()) {

    fun getGpValue(): Int {

        var gpTotal: Int = 0

        if (properties.isNotEmpty()) { properties.forEach { (_, gpValue) -> gpTotal += gpValue} }

        if (spells.isNotEmpty()) { spells.forEach() {

                gpTotal += if (it.spellLevel == 0) {
                    75
                } else {
                    (it.spellLevel * 300)
                }
            }
        }

        return gpTotal
    }
}