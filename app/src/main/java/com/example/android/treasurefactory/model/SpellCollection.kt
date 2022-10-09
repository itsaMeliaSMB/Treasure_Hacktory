package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

data class SpellEntry(
    val spellID: Int,
    val spellLevel: Int = 1,
    val extraPages: Int = 0,
    val usedUp: Boolean = false
)

@Entity(tableName = "hackmaster_spell_collection_table")
data class SpellCollection(
    @PrimaryKey(autoGenerate = true) @NotNull val sCollectID: Int,
    val hoardID: Int,
    val creationTime: Long,
    var iconID: String,
    var name: String = "<Spell Collection>",
    var type: SpCoType,
    var properties: List<Pair<String,Double>> = emptyList(),
    var gpValue: Double = 0.0,
    var xpValue: Int = 0,
    var spells: List<SpellEntry> = emptyList(),
    var curse: String = "") {

    @Ignore
    fun calculateGPValue(): Double {

        if (type == SpCoType.RING) return 7000.0

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

    @Ignore
    fun calculateXpValue() : Int {

        if (type == SpCoType.RING) return 2500

        var xpTotal = 0

        if (spells.isNotEmpty()) { spells.forEach {

            xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel)

            }
        }

        return xpTotal
    }
}