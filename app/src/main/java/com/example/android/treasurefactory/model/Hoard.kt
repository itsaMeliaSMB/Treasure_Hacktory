package com.example.android.treasurefactory.model

import androidx.room.Entity
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
                 var xpGpRatio: Double = 5.0, //
                 var gemCount: Int = 0,
                 var artCount: Int = 0,
                 var magicCount: Int = 0,
                 var spellsCount: Int = 0,
                 var isFavorite: Boolean = false,
                 var isNew: Boolean = true) {}