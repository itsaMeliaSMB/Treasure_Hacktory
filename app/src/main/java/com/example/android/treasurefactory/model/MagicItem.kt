package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

enum class MagicItemType(tableLabel: String) {
    A2 ("Potions and Oils"),
    A3 ("Scrolls"),
    A4 ("Rings"),
    A5 ("Rods"),
    A6 ("Staves"),
    A7 ("Wands"),
    A8 ("Books, Librams, Manuals, Tomes"),
    A9 ("Jewels, Jewelry, Phylacteries"),
    A10("Cloaks and Robes"),
    A11("Boots, Bracers, Gloves"),
    A12("Belts, Girdles, Hats, Helms"),
    A13("Bags, Bottles, Pouches, Containers"),
    A14("Candles, Dusts, Ointments, Incense, and Stones"),
    A15("Household Items and Tools"),
    A16("Musical Instruments"),
    A17("The Weird Stuff"),
    A18("Standard Armor and Shields"),
    A20("Special Armors"),
    A21("Standard Weapons"),
    A23("Special Weapons"),
    A24("Artifacts and Relics"),
    Map("Treasure Map"),
    Mundane("Non-Magical Unique Items")
}

/**
 * Generated magic item following HackMaster 4e rules.
 *
 * @param templateID Primary key of the magic item template in the database reference table
 * @param classUsability Map of class type (fighter/thief/cleric/magic-user/druid) to its ability to use this item (true/false). Use lowercase for strings.
 * @param notes List of generated special notes for the object. First list should be a list of names for all the other lists.
 */
@Entity(tableName = "hackmaster_magic_item_table")
data class MagicItem(
    @PrimaryKey(autoGenerate = true) @NotNull val mItemID: Int,
    val templateID: Int,
    val hoardID: Int,
    val creationTime: Long,
    val iconID: String,
    val typeOfItem: MagicItemType,
    val name: String,
    val sourceText: String,
    val sourcePage: Int,
    val xpValue: Int,
    val gpValue: Double,
    val classUsability: Map<String,Boolean>,
    val isCursed: Boolean,
    val alignment: String,
    val notes: List<Pair<String,List<String>>> = emptyList())