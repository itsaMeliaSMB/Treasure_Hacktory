package com.example.android.treasurefactory.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO: Figure out where I need to define foreign keys for nested relationships

// TODO: Separately define Database object and app-level model for encapsulation https://stackoverflow.com/questions/64823212/use-android-room-without-breaking-encapsulation

@Entity (tableName = "hackmaster_hoard_table")
data class Hoard(@PrimaryKey(autoGenerate = true) val hoardID: Int,
                 var name: String = "",
                 var creationDate: Date = Date(),
                 var creationDesc: String = "",
                 var iconID: String,
                 var gpTotal: Double,
                 var cp: Int,
                 var sp: Int,
                 var ep: Int,
                 var gp: Int,
                 var hsp: Int,
                 var pp: Int,
                 var gemCount: Int,
                 var artCount: Int,
                 var magicCount: Int,
                 var spellsCount: Int,
                 var isFavorite: Boolean,
                 var isNew: Boolean) {}