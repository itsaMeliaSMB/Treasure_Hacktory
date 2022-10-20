package com.example.android.treasurefactory.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.model.ReferenceType
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

data class LimitedItemTemplate(@ColumnInfo(name = "ref_id") val templateID: Int,
                               @ColumnInfo(name = "wt") val weight: Int,
                               @ColumnInfo(name = "is_cursed") val isCursed: Int)

@Entity(tableName = "hackmaster_magic_item_reference")
data class MagicItemTemplate(
    @PrimaryKey @ColumnInfo(name = "ref_id") val refId: Int,
    val wt: Int,
    val name: String,
    @ColumnInfo(name = "ref_type") val refType: ReferenceType,
    val source: String,
    val page: Int,
    @ColumnInfo(name = "xp_value") val xpValue: Int,
    @ColumnInfo(name = "gp_value") val gpValue: Int,
    @ColumnInfo(name = "multiply_xp_gp_by_charge") val multiType: Int,
    val notes: String,
    @ColumnInfo(name = "die_count") val dieCount: Int,
    @ColumnInfo(name = "die_sides") val dieSides: Int,
    @ColumnInfo(name = "die_mod") val dieMod: Int,
    @ColumnInfo(name = "table_type") val tableType: String,
    @ColumnInfo(name = "icon_ref") val iconRef: String,
    @ColumnInfo(name = "f_usable") val fUsable: Int,
    @ColumnInfo(name = "t_usable") val tUsable: Int,
    @ColumnInfo(name = "c_usable") val cUsable: Int,
    @ColumnInfo(name = "m_usable") val mUsable: Int,
    @ColumnInfo(name = "d_usable") val dUsable: Int,
    @ColumnInfo(name = "has_child") val hasChild: Int,
    @ColumnInfo(name = "parent_id") val parentID: Int,
    @ColumnInfo(name = "imitation_keyword") val imitationKeyword: String,
    @ColumnInfo(name = "is_cursed") val isCursed: Int,
    @ColumnInfo(name = "command_word") val commandWord: String,
    @ColumnInfo(name = "intel_chance") val intelChance: Int,
    val alignment: String,
    @ColumnInfo(name = "i_power") val iPower: Int,
    @ColumnInfo(name = "ii_power") val iiPower: Int,
    @ColumnInfo(name = "iii_power") val iiiPower: Int,
    @ColumnInfo(name = "iv_power") val ivPower: Int,
    @ColumnInfo(name = "v_power") val vPower: Int,
    @ColumnInfo(name = "vi_power") val viPower: Int
)

@Entity(tableName = "command_word_suggestions")
data class CommandWord(
    @PrimaryKey @NotNull val commandWord: String,
    val themeWord: String )

@Entity(tableName = "hackmaster_letter_codes")
data class LetterCode(
    @PrimaryKey @NotNull val letterID: String,
    val cpChance: Int = 0, val cpMin : Int = 0, val cpMax : Int = 0,
    val spChance: Int = 0, val spMin : Int = 0, val spMax : Int = 0,
    val epChance: Int = 0, val epMin : Int = 0, val epMax : Int = 0,
    val gpChance: Int = 0, val gpMin : Int = 0, val gpMax : Int = 0,
    val hspChance:Int = 0, val hspMin: Int = 0, val hspMax: Int = 0,
    val ppChance: Int = 0, val ppMin : Int = 0, val ppMax : Int = 0,
    val gemChance:Int = 0, val gemMin: Int = 0, val gemMax: Int = 0,
    val artChance:Int = 0, val artMin: Int = 0, val artMax: Int = 0,
    val potionChance: Int = 0, val potionMin : Int = 0, val potionMax : Int = 0,
    val scrollChance: Int = 0, val scrollMin : Int = 0, val scrollMax : Int = 0,
    val weaponChance: Int = 0, val weaponMin : Int = 0, val weaponMax : Int = 0,
    val noWeaponChance: Int = 0, val noWeaponMin : Int = 0, val noWeaponMax : Int = 0,
    val anyChance:Int = 0, val anyMin:Int = 0, val anyMax: Int = 0
)
// endregion