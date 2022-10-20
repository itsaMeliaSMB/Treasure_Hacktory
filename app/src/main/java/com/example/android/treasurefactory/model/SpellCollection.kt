package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.capitalized
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
    val discipline: SpCoDiscipline = SpCoDiscipline.ALL_MAGIC,
    var properties: List<Pair<String, Double>> = emptyList(),
    var gpValue: Double = 0.0,
    var xpValue: Int = 0,
    var spells: List<SpellEntry> = emptyList(),
    var curse: String = ""
) {

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

        result.append(when (type){
            SpCoType.SCROLL     -> "scroll "
            SpCoType.BOOK       -> "spellbook "
            SpCoType.ALLOTMENT -> "Chosen One allotment "
            SpCoType.RING       -> "ring "
            SpCoType.OTHER      -> "collection "
        })

        result.append(" of ${spells.size}")

        when (discipline){
            SpCoDiscipline.ARCANE       -> result.append("arcane spells")
            SpCoDiscipline.DIVINE       -> result.append("divine spells")
            SpCoDiscipline.NATURAL      -> result.append("druidic spells (non-standard)")
            SpCoDiscipline.ALL_MAGIC    -> result.append("varied spells")
        }

        return result.toString().capitalized()
    }

    @Ignore
    suspend fun getFlavorTextAndSpellsAsDetailsLists(repository: HMRepository): List<Pair<String,List<DetailEntry>>> {

        val spellCollectionPropertiesList = ArrayList<List<LabelledQualityEntry>>()

        val resultList = ArrayList<Pair<String,List<DetailEntry>>>()

        if (properties.isNotEmpty()) {
            spellCollectionPropertiesList.add(
                properties.map { (spCoProperty, gpValue) ->
                    LabelledQualityEntry(spCoProperty,"${DecimalFormat("#,##0.0#")
                        .format(gpValue).removeSuffix(".0")} gp")}
            )
        }

        if (curse.isNotBlank()) {
            spellCollectionPropertiesList.add(
                    listOf(LabelledQualityEntry("Suggested curse", curse))
            )
        }

        if (spellCollectionPropertiesList.isNotEmpty()) {
            resultList.add("Spell collection properties" to spellCollectionPropertiesList.flatten())
        }

        if (spells.isNotEmpty()) {
            resultList.add(
                "Spell list" to spells.map { spEntry ->
                    repository.getSpell(spEntry.spellID)
                        ?.toSimpleSpellEntry(spEntry.usedUp) ?:
                    PlainTextEntry("This spell could not be loaded") }
            )
        }

        return resultList.toList()
    }
}