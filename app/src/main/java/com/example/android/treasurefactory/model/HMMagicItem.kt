package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


/**
 * Generated magic item following HackMaster 4e rules.
 *
 * @property templateID Primary key of the magic item template in the database reference table
 * @property classUsability Map of class type (fighter/thief/cleric/magic-user/druid) to its ability to use this item (true/false). Use lowercase for strings.
 * @property notes List of generated special notes for the object. First list should be a list of names for all the other lists.
 */
@Entity(foreignKeys = [ForeignKey(
    entity = HMHoard::class,
    parentColumns = arrayOf ("hoardID"),
    childColumns = arrayOf("mItemID"),
    onDelete = CASCADE ) ] )
data class HMMagicItem(@PrimaryKey(autoGenerate = true) val mItemID: Int,
                       val templateID: Int,
                       val hoardID: Int,
                       var iconID: String, //TODO Add necessary foreign keys
                       val typeOfItem: String, //TODO add function for converting table to descriptive label
                       val name: String,
                       val sourceText: String,
                       val sourcePage: Int, //TODO add var for nickname when architecture is clearer
                       val xpValue: Int,
                       val gpValue: Double,
                       val classUsability: Map<String,Boolean>,
                       val isCursed: Boolean,
                       val alignment: String,
                       val notes: List<List<String>> = emptyList(),
                       val userNotes: List<String> = emptyList()){ //TODO Refactor lists to be more flat

    enum class MagicItemType(val description: String) { //TODO delete when generator functionality is complete

        A1("DEFAULT TYPE"),
        A2("Potions and Oils"),
        A3("Scrolls"),
        A4("Rings"),
        A5("Rods"),
        A6("Staves"),
        A7("Wands"),
        A8("Misc: Books, etc."),
        A9("Misc: Jewels, etc."),
        A10("Misc: Cloaks, etc."),
        A11("Misc: Boots, etc."),
        A12("Misc: Girdles, etc."),
        A13("Misc: Bags, etc."),
        A14("Misc: Dusts, etc."),
        A15("Misc: Household"),
        A16("Misc: Musical"),
        A17("Misc: Weird"),
        A18("Armor and Shields"),
        A21("Weapons"),
        A24("Artifacts/Relics");
    }
}