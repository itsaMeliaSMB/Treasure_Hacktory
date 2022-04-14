package com.example.android.treasurefactory.model

import androidx.room.*
import java.util.*

// https://stackoverflow.com/questions/64823212/use-android-room-without-breaking-encapsulation

/**
 * Parent class for distinct treasure hoard with it's own items.
 *
 * @param effortRating GP/XP value ratio of items without specific XP values based on difficulty of acquisition (average difficulty is considered 5.0 gp : 1 xp)
 */
@Entity (tableName = "hackmaster_hoard_table")
data class Hoard(@PrimaryKey(autoGenerate = true) val hoardID: Int = 0,
                 var name: String = "",
                 var creationDate: Date = Date(),
                 var creationDesc: String = "",
                 @ColumnInfo(name="icon_id") var iconID: String = "",
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
                 var appVersion: Int = 0,           // Version code of app hoard was generated on
                 @Embedded(prefix = "leftover_") val leftover: HoardLeftover = HoardLeftover())

data class HoardLeftover(val gems: Int = 0,
                         val artObjects: Int = 0,
                         val potions: Int = 0,
                         val scrolls: Int = 0,
                         val armorOrWeapons: Int = 0,
                         val anyButWeapons: Int = 0,
                         val anyMagicItems: Int = 0) {
    @Ignore
    fun isNotEmpty(): Boolean = !((gems == 0)||(artObjects==0)||((potions==0)||(scrolls==0)||(armorOrWeapons == 0)||(anyButWeapons==0)||(anyMagicItems==0)))
}