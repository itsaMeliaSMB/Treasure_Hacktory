package com.example.android.treasurefactory

import androidx.room.Entity
import java.util.*

// TODO: privatize some of the properties in the constructor
@Entity
data class HMHoard(@PrimaryKey val id: UUID = UUID.randomUUID(),
                    var hoardName: String = "",
                    var creationDate: Date = Date(),
                    var favorited: Boolean = false) {

    //TODO: Add initializer
    var coinage = HMCoinPile()
    var gemList = mutableListOf<HMGem>()
    var artList = mutableListOf<HMArtObject>()
    var magicList = mutableListOf<HMMagicItem>()
    var spellsList = mutableListOf<HMSpellCollection>()

    //TODO Add function to return total GP value of hoard

    //TODO Add function that returns totals of gems, artwork, and magic items

    companion object {

        private val file = File("data/treasureGenerator/Stones_${gemVariety.tier}.csv").readLines()
    }
}