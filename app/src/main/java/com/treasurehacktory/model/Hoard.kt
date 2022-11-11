package com.treasurehacktory.model

import androidx.room.*
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.math.roundToInt

/**
 * Parent class for distinct treasure hoard with it's own items.
 *
 * @param gpTotal Total value of treasure hoard, including current market value of items.
 * @param effortRating GP/XP value ratio of items without specific XP values based on difficulty of acquisition (average difficulty is considered 5.0 gp : 1 xp)
 */
@Entity (tableName = "hackmaster_hoard_table", indices = [Index(value = ["hoardID"])])
data class Hoard(
    @PrimaryKey(autoGenerate = true) @NotNull val hoardID: Int = 0,
    var name: String = "",
    var creationDate: Date = Date(),
    var badge: HoardBadge = HoardBadge.NONE,
    @ColumnInfo(name = "icon_id") var iconID: String = "",
    var gpTotal: Double = 0.0,
    var effortRating: Double = 5.0,
    var cp: Int = 0,
    var sp: Int = 0,
    var ep: Int = 0,
    var gp: Int = 0,
    var hsp: Int = 0,
    var pp: Int = 0,
    var gemCount: Int = 0,
    var artCount: Int = 0,
    var magicCount: Int = 0,
    var spellsCount: Int = 0,
    var isFavorite: Boolean = false,
    var isNew: Boolean = true,
    var successful: Boolean = false,
    var appVersion: Int = 0           // Version code of app hoard was generated on
    ) {

    @Ignore
    fun getTotalCoinageValue(): Double {

        return (
                ((cp * 0.01) + (sp * 0.1) + (ep * 0.5) + (gp * 1.0) + (hsp * 2.0) + (pp * 5.0))
                        * 100.00).roundToInt() / 100.00
    }
}

enum class HoardBadge(val resString: String?) {
    NONE(null),
    // Functionary
    ARCHIVED("badge_hoard_archived"),
    EDITED("badge_hoard_edited"),
    COPIED("badge_hoard_copied"),
    MERGED("badge_hoard_merged"),
    NOTICE("badge_hoard_notice"),
    // Treasure
    COINAGE("badge_hoard_coinage"),
    GEMSTONE("badge_hoard_gem"),
    ARTWORK("badge_hoard_artwork"),
    MAGIC("badge_hoard_magic"),
    // Type
    LARGE_LAIR("badge_hoard_large_lair"),
    SMALL_LAIR("badge_hoard_small_lair"),
    CHEST("badge_hoard_chest"),
    SACK("badge_hoard_sack"),
    MIMIC("badge_hoard_mimic"),
    MAP("badge_hoard_map"),
    STORAGE("badge_hoard_storage"),
    SECRET("badge_hoard_secret"),
    // Class
    FIGHTER("badge_hoard_fighter"),
    MAGIC_USER("badge_hoard_mage"),
    THIEF("badge_hoard_thief"),
    CLERIC("badge_hoard_cleric"),
    // Owner
    CONSTRUCT("badge_hoard_construct"),
    DRAGON("badge_hoard_dragon"),
    DWARF("badge_hoard_dwarf"),
    ELF("badge_hoard_elf"),
    EXTRAPLANAR("badge_hoard_extraplanar"),
    ORC("badge_hoard_orc"),
    UNDEAD("badge_hoard_undead")
}

enum class CoinType(val longName: String, val gpValue: Double) {
    CP("Copper pieces",0.01),
    SP("Silver pieces", 0.1),
    EP("Electrum pieces",0.5),
    GP("Gold pieces",1.0),
    HSP("Hard silver pieces",2.0),
    PP("Platinum pieces", 5.0)
}

data class HoardUniqueItemBundle(val hoardGems: List<Gem>,
                                 val hoardArt: List<ArtObject>, val hoardItems: List<MagicItem>,
                                 val hoardSpellCollections: List<SpellCollection>)

/**
 * Record of an event that occurred in a [Hoard]'s history.
 * @param timestamp Milliseconds since Unix epoch that the event occurred on.
 * @param description User-readable description of event.
 * @param tag Identifier for source/type of event.
 */
@Entity(tableName = "hoard_events_log")
data class HoardEvent(
    @PrimaryKey(autoGenerate = true) @NotNull val eventID: Int = 0,
    val hoardID: Int = 0,
    val timestamp: Long,
    val description: String,
    val tag: String)