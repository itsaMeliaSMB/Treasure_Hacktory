package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.repository.HMRepository
import org.jetbrains.annotations.NotNull
import java.text.DecimalFormat

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

    @Ignore
    fun getSubtitle(): String {
        val result = StringBuilder()

        if (curse.isNotBlank()) result.append("cursed ")

        when (iconID){
            "scroll_base"   -> result.append("assorted ")
            "scroll_red"    -> result.append("arcane ")
            "scroll_blue"   -> result.append("divine ")
            "scroll_green"  -> result.append("druidic (non-standard) ")
            "scroll_cursed" -> if (result.isEmpty()) result.append("cursed ")
            //TODO when spellbooks are implemented, include string checks for spellbook resources
            "icon_chosen_one" -> result.append("bestowed ")
        }

        result.append(when (type){
            SpCoType.SCROLL -> "scroll "
            SpCoType.BOOK -> "spellbook "
            SpCoType.ALLOTMENT -> "Chosen One allotment "
            SpCoType.RING -> "ring "
            SpCoType.OTHER -> "collection "
        })

        result.append(" of ${spells.size} spells")

        return result.toString()
    }

    @Ignore
    suspend fun getFlavorTextAndSpellsAsDetailsLists(repository: HMRepository): List<Pair<String,List<DetailEntry>>> {

        return listOf(
            "Spell collection properties" to properties.map { (spCoProperty, gpValue) ->
                LabelledQualityEntry(spCoProperty,"${DecimalFormat("#,##0.0#")
                    .format(gpValue).removeSuffix(".0")} gp")},
            "Spell list" to spells.map { spEntry ->
                repository.getSpell(spEntry.spellID)
                    ?.toSimpleSpellEntry(spEntry.usedUp) ?:
                    PlainTextEntry("This spell could not be loaded") })
    }
}