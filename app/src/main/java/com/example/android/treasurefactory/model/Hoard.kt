package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

// TODO: Figure out where I need to define foreign keys for nested relationships

// TODO: Separately define Database object and app-level model for encapsulation https://stackoverflow.com/questions/64823212/use-android-room-without-breaking-encapsulation

@Entity (tableName = "hackmaster_hoard_table")
data class Hoard(@PrimaryKey(autoGenerate = true) val hoardID: Int = 0,
                 var name: String = "",
                 var creationDate: Date = Date(),
                 var creationDesc: String = "",
                 var iconID: String = "",
                 var gpTotal: Double = 0.0,
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
                 var isNew: Boolean = true) {}

@Entity (tableName = "hackmaster_hoard_leftover_table",
    foreignKeys = [ForeignKey(
        entity = Hoard::class,
        parentColumns = arrayOf ("hoardID"),
        childColumns = arrayOf("leftoverID"),
        onDelete = ForeignKey.CASCADE) ])
data class HoardLeftover(@PrimaryKey(autoGenerate = true) val leftoverID: Int = 0,
                         val hoardID: Int,
                         val gems: Int = 0,
                         val artObjects: Int = 0,
                         val potions: Int = 0,
                         val scrolls: Int = 0,
                         val armorOrWeapons: Int = 0,
                         val anyButWeapons: Int = 0,
                         val anyMagicItems: Int = 0)