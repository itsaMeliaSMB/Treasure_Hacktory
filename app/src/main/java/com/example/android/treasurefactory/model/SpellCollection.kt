package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "hackmaster_spell_collection_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("sCollectID"),
        onDelete = ForeignKey.CASCADE ) ])
data class SpellCollection(
    @PrimaryKey(autoGenerate = true) @NotNull val sCollectID: Int,
    val hoardID: Int,
    var iconID: String,
    var name: String = "<Spell Scroll>",
    var type: String = "scroll",
    var properties: Map<String,Double> = emptyMap(),
    var spells: List<Spell> = listOf(),
    var curse: String = "") : Evaluable{

    @Ignore
    override fun getGpValue(): Double {

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
    override fun getXpValue() : Int {

        var xpTotal = 0

        if (spells.isNotEmpty()) { spells.forEach {

            xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel)

            }
        }

        return xpTotal
    }
}