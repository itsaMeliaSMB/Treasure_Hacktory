package com.example.android.treasurefactory.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.model.*
import org.jetbrains.annotations.NotNull

// region [ Read-only template entities ]

//TODO May need to remove column info for template classes

@Entity(tableName = "hackmaster_gem_reference")
data class GemTemplate(
    @PrimaryKey @ColumnInfo(name="ref_id") val refId: Int,
    val type: Int,
    val name: String,
    val ordinal: Int,
    val opacity: Int,
    val description: String,
    @ColumnInfo(name ="icon_id") val iconID : String)

// TODO Refactor to include everything in new Gem schema

@Entity(tableName = "hackmaster_magic_item_reference")
data class MagicItemTemplate(
    @PrimaryKey @ColumnInfo(name="ref_id") val refId: Int,
    val wt: Int,
    val name: String,
    val source: String,
    val page: Int,
    @ColumnInfo(name="xp_value") val xpValue: Int,
    @ColumnInfo(name="gp_value") val gpValue: Int,
    @ColumnInfo(name="multiply_xp_gp_by_charge") val multiType: Int,
    val notes: String,
    @ColumnInfo(name="die_count") val dieCount: Int,
    @ColumnInfo(name="die_sides") val dieSides: Int,
    @ColumnInfo(name="die_mod") val dieMod: Int,
    @ColumnInfo(name="table_type") val tableType: String,
    @ColumnInfo(name="icon_ref") val iconRef: String,
    @ColumnInfo(name="f_usable") val fUsable: Int,
    @ColumnInfo(name="t_usable") val tUsable: Int,
    @ColumnInfo(name="c_usable") val cUsable: Int,
    @ColumnInfo(name="m_usable") val mUsable: Int,
    @ColumnInfo(name="d_usable") val dUsable: Int,
    @ColumnInfo(name="has_child") val hasChild: Int,
    @ColumnInfo(name="parent_id") val parentID: Int,
    @ColumnInfo(name="imitation_keyword") val imitationKeyword: String,
    @ColumnInfo(name="is_cursed") val isCursed: Int,
    @ColumnInfo(name="command_word") val commandWord: String,
    @ColumnInfo(name="intel_chance") val intelChance: Int,
    val alignment: String,
    @ColumnInfo(name="i_power") val iPower: Int,
    @ColumnInfo(name="ii_power") val iiPower: Int,
    @ColumnInfo(name="iii_power") val iiiPower: Int,
    @ColumnInfo(name="iv_power") val ivPower: Int,
    @ColumnInfo(name="v_power") val vPower: Int,
    @ColumnInfo(name="vi_power") val viPower: Int)

@Entity(tableName = "hackmaster_spell_reference")
data class SpellTemplate(
    @PrimaryKey @ColumnInfo(name="ref_id") val refId: Int,
    val name: String,
    @ColumnInfo(name="ref_type") val refType: Int,
    val source: String,
    val page: Int,
    val type: Int,
    val level: Int,
    val schools: String,
    @ColumnInfo(name="restricted_to") val restrictions: String,
    @ColumnInfo(name="spell_spheres") val spellSpheres: String,
    @ColumnInfo(name="arcane_subclass") val subclass: String,
    val note: String)

// endregion

// region [ Database model classes ]
@Entity(tableName = "hackmaster_gem_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("gemID"),
        onDelete = ForeignKey.CASCADE ) ])
data class GemEntity(
    @PrimaryKey(autoGenerate = true) @NotNull val gemID: Int = 0,
    val hoardID: Int = 0,
    val iconID: String,
    val type: Int,
    val size: Int,
    val quality: Int,
    val variation: Int,
    val name: String,
    val opacity: Int,
    val description: String,
    val currentGPValue: Double,
    val valueHistory: List<Pair<Long,String>>)

@Entity(tableName = "hackmaster_art_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("artID"),
        onDelete = ForeignKey.CASCADE
    ) ] )
data class ArtObjectEntity(
    @PrimaryKey(autoGenerate = true) @NotNull val artID: Int,
    val hoardID: Int,
    @ColumnInfo(name="icon_id") val iconID: String,
    val artType: String,
    val renown: String,
    val size: String,
    val condition: String,
    val materials: String,
    val quality: String,
    val age: Int,
    val subject: String,
    val valueLevel: Int)

@Entity(tableName = "hackmaster_magic_item_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("mItemID"),
        onDelete = ForeignKey.CASCADE) ] )
data class MagicItemEntity(
    @PrimaryKey(autoGenerate = true) @NotNull val mItemID: Int,
    val templateID: Int,
    val hoardID: Int,
    @ColumnInfo(name="icon_id") val iconID: String,
    val typeOfItem: String,
    val name: String,
    val sourceText: String,
    val sourcePage: Int,
    val xpWorth: Int,
    val gpWorth: Double,
    val classUsability: Map<String,Boolean>,
    val isCursed: Boolean,
    val alignment: String,
    val notes: List<List<String>> = emptyList())

@Entity(tableName = "hackmaster_spell_collection_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("sCollectID"),
        onDelete = ForeignKey.CASCADE ) ])
data class SpellCollectionEntity(
    @PrimaryKey(autoGenerate = true) @NotNull val sCollectID: Int,
    val hoardID: Int,
    @ColumnInfo(name="icon_id") val iconID: String,
    val name: String = "<Spell Scroll>",
    val type: String = "scroll",
    val properties: Map<String,Double> = emptyMap(),
    val spells: List<Spell> = listOf(),
    val curse: String = "")

// endregion

// region [ Convenience Data Entities ]

/*@Entity(tableName = "icon_id_int_directory")
data class IconIDTuple(
    @PrimaryKey val stringID: String,
    val resID: Int,
    val appVersion: Long,
)*/

// endregion

// region [ Mapping functions ]

@JvmName("asDomainModelGemEntity")
fun List<GemEntity>.asDomainModel(): List<Gem> {

    return map {
        Gem(
            it.gemID,
            it.hoardID,
            it.iconID,
            it.type,
            it.size,
            it.quality,
            it.variation,
            it.name,
            it.opacity,
            it.description,
            it.currentGPValue,
            it.valueHistory)
    }
}

@JvmName("asDomainModelArtObjectEntity")
fun List<ArtObjectEntity>.asDomainModel(): List<ArtObject> { //TODO refactor mappings in light of new ArtObject schema

    return map {
        ArtObject(
            it.artID,
            it.hoardID,
            it.iconID,
            it.artType,
            it.renown,
            it.size,
            it.condition,
            it.materials,
            it.quality,
            it.age,
            it.subject,
            it.valueLevel)
    }
}

@JvmName("asDomainModelMagicItemEntity")
fun List<MagicItemEntity>.asDomainModel(): List<MagicItem> {

    return map {
        MagicItem(
            it.mItemID,
            it.templateID,
            it.hoardID,
            it.iconID,
            it.typeOfItem,
            it.name,
            it.sourceText,
            it.sourcePage,
            it.xpWorth,
            it.gpWorth,
            it.classUsability,
            it.isCursed,
            it.alignment,
            it.notes)
    }
}

@JvmName("asDomainModelSpellCollectionEntity")
fun List<SpellCollectionEntity>.asDomainModel(): List<SpellCollection> { // TODO refactor mappings in light of new SpellCollection schema

    return map {
        SpellCollection(
            it.sCollectID,
            it.hoardID,
            it.iconID,
            it.name,
            it.type,
            it.properties,
            it.spells,
            it.curse )
    }
}

// endregion