package com.example.android.treasurefactory.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hackmaster_gem_reference")
data class GemTemplate(@PrimaryKey
                        @ColumnInfo(name="ref_id") val refId: Int,
                       val type: String,
                       val name: String,
                       val ordinal: Int,
                       val opacity: Int,
                       val description: String,
                       @ColumnInfo(name ="icon_id") val iconID : String
)

@Entity(tableName = "hackmaster_magic_item_reference")
data class MagicItemTemplate(@PrimaryKey
                               @ColumnInfo(name="ref_id") val refId: Int,
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
                             @ColumnInfo(name="intel_chance") val intel_chance: Int,
                             val alignment: String,
                             @ColumnInfo(name="i_power") val iPower: Int,
                             @ColumnInfo(name="ii_power") val iiPower: Int,
                             @ColumnInfo(name="iii_power") val iiiPower: Int,
                             @ColumnInfo(name="iv_power") val ivPower: Int,
                             @ColumnInfo(name="v_power") val vPower: Int,
                             @ColumnInfo(name="vi_power") val viPower: Int
)

@Entity(tableName = "hackmaster_spell_reference")
data class SpellTemplate(@PrimaryKey
                           @ColumnInfo(name="ref_id") val refId: Int,
                         val name: String,
                         val source: String,
                         val page: Int,
                         val type: Int,
                         val level: Int,
                         val schools: String,
                         @ColumnInfo(name="restricted_to") val restrictions: String,
                         @ColumnInfo(name="spell_spheres") val spellSpheres: String,
                         @ColumnInfo(name="arcane_subclass") val subclass: String,
                         val note: String
)
