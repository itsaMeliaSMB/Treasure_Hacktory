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

data class SpCoAugmentation(
    val label: String,
    val textValue: String,
    val gpModifier: Double = 0.0,
    val byPage: Boolean = false
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
    var augmentations: List<SpCoAugmentation> = emptyList(),
    var gpValue: Double = 0.0,
    var xpValue: Int = 0,
    var spells: List<SpellEntry> = emptyList(),
    var curse: String = "",
    val pageCount: Int = 0,
    val originalName: String
) {

    @Ignore
    fun getSubtitle(): String {
        val result = StringBuilder()

        if (curse.isNotBlank()) result.append("cursed ")

        result.append(when (type){
            SpCoType.SCROLL     -> "scroll "
            SpCoType.BOOK       -> "spellbook "
            SpCoType.ALLOTMENT  -> "Chosen One allotment "
            SpCoType.RING       -> "ring "
            SpCoType.OTHER      -> "collection "
        })

        result.append(" of ${spells.size} ")

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

        if (pageCount > 0){
            spellCollectionPropertiesList.add(
                listOf(LabelledQualityEntry("Total page count", pageCount.toString()))
            )
        }

        if (augmentations.isNotEmpty()) {
            spellCollectionPropertiesList.add(
                augmentations.map { augment ->
                    LabelledQualityEntry(
                        augment.label,augment.textValue + " (" +
                            "${DecimalFormat("#,##0.0#")
                                .format(augment.gpModifier).removeSuffix(".0")} gp" +
                            if (augment.byPage)" / page)" else ")")}
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
                "Spell list" to spells.mapIndexed { index, spEntry ->
                    repository.getSpell(spEntry.spellID)
                        ?.toSimpleSpellEntry(spEntry.usedUp,index) ?:
                    PlainTextEntry("This spell could not be loaded") }
            )
        }

        return resultList.toList()
    }

    @Ignore
    suspend fun toViewableSpellCollection(repository: HMRepository) : ViewableSpellCollection {

        return ViewableSpellCollection(
            sCollectID,
            hoardID,
            name,
            getSubtitle(),
            creationTime,
            iconID,
            when {
                curse.isNotEmpty() -> ItemFrameFlavor.CURSED
                type == SpCoType.ALLOTMENT -> ItemFrameFlavor.GOLDEN
                else -> ItemFrameFlavor.NORMAL
            },
            when (type){
                SpCoType.SCROLL -> "GameMaster's Guide"
                SpCoType.BOOK -> "Spellslinger's Guide to Wurld Domination"
                SpCoType.ALLOTMENT -> "Zealot’s Guide to Wurld Conversion"
                SpCoType.RING -> "GameMaster's Guide"
                SpCoType.OTHER -> "Unspecified"
            },
            when (type){
                SpCoType.SCROLL -> 225
                SpCoType.BOOK -> 82
                SpCoType.ALLOTMENT -> 6
                SpCoType.RING -> 231
                SpCoType.OTHER -> 0
            },
            gpValue,
            xpValue,
            UniqueItemType.SPELL_COLLECTION,
            getFlavorTextAndSpellsAsDetailsLists(repository),
            originalName,
            type,
            discipline,
            augmentations,
            spells,
            curse,
            pageCount,
        )
    }

    companion object {

        @Ignore
        fun calculateGPValue(type: SpCoType, augmentations: List<SpCoAugmentation>, pageCount: Int,
                             spells: List<SpellEntry>): Double {

            if (type == SpCoType.RING) return 7000.0

            var gpTotal = 0.0

            if (augmentations.isNotEmpty()) {
                augmentations.forEach { augment ->
                    gpTotal += if (augment.byPage) pageCount * augment.gpModifier else augment.gpModifier
                }
            }

            if (spells.isNotEmpty()) { spells.forEach {

                if (!(it.usedUp)){

                    gpTotal += if (it.spellLevel == 0) {
                        75.0
                    } else {
                        (300.0 * it.spellLevel)
                    }
                } }
            }

            return gpTotal
        }

        @Ignore
        fun calculateXPValue(type: SpCoType, spells: List<SpellEntry>) : Int {

            if (type == SpCoType.RING) return 2500

            var xpTotal = 0

            if (spells.isNotEmpty()) { spells.forEach {

                if (!(it.usedUp)) { xpTotal += if (it.spellLevel == 0) 25 else (100 * it.spellLevel) }

            }
            }

            return xpTotal
        }
    }
}