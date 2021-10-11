package com.example.android.treasurefactory

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO: Figure out where I need to define foreign keys for nested relationships

// TODO: Separately define Database object and app-level model for encapsulation https://stackoverflow.com/questions/64823212/use-android-room-without-breaking-encapsulation

@Entity (tableName = "hackmaster_hoard_table")
data class HMHoard(@PrimaryKey(autoGenerate = true) val hoardID: Int,
                   private var name: String = "",
                   private var creationDate: Date = Date(),
                   private var iconID: Int,
                   private var gpValue: Double,
                   private var gemCount: Int,
                   private var artCount: Int,
                   private var magicCount: Int,
                   private var isFavorite: Boolean,
                   private var isNew: Boolean) {

    // region [ Getters and Setters ]

    fun getName() = name

    fun setName(newName: String) { name = if (newName.isNotBlank()) newName else name }

    fun getCreationDate() = creationDate // No setter for creationDate as it should never change

    fun getIconID() = iconID

    fun setIconID(newIconID: Int) { iconID = newIconID }

    fun getGpValue() = gpValue

    fun setGpValue(newGpValue: Double) { gpValue = if (newGpValue < 0) gpValue else newGpValue }

    fun getGemCount() = gemCount

    fun setGemCount(newCount: Int) { gemCount = newCount }

    fun getArtCount(newCount: Int) = artCount

    fun setArtCount(newCount: Int) { artCount = newCount }

    fun getMagicCount() = magicCount

    fun setMagicCount(newCount: Int) { magicCount = newCount }

    fun getFavorite() = isFavorite

    fun setFavorite(favorited: Boolean) { isFavorite = favorited }

    fun getNewStatus() = isNew

    fun setNewStatus(markedNew: Boolean) { isNew = markedNew }

    // endregion

    /*companion object {

        private val file = File("data/treasureGenerator/Stones_${gemVariety.tier}.csv").readLines()
    }*/
}

@Entity(tableName="hackmaster_coin_pile_table")
data class HMCoinPile(@PrimaryKey(autoGenerate = true) val coinID: Int, val hoardID : Int,
                      var cp: Int = 0, var sp: Int = 0, var ep: Int = 0,
                      var gp: Int = 0, var hsp: Int= 0, var pp: Int = 0)

@Entity(tableName="hackmaster_gem_pile_table")
data class HMGemPile(@PrimaryKey(autoGenerate = true) val gemPileID: Int, val hoardID: Int)

@Entity(tableName="hackmaster_art_pile_table")
data class HMArtPile(@PrimaryKey(autoGenerate = true) val artPileID: Int, val hoardID: Int)

@Entity(tableName="hackmaster_magic_pile_table")
data class HMMagicPile(@PrimaryKey(autoGenerate = true) val magicPileID: Int, val hoardID: Int)