package com.example.android.treasurefactory

import java.util.*

data class HMHoard(val id: UUID = UUID.randomUUID(),
                    var hoardName: String = "",
                    var creationDate: Date = Date(),
                    var favorited: Boolean = false) {

    //TODO: Add initializer
    var coinage = HMCoinPile()
    var gemList = mutableListOf<HMGem>()
    var artList = mutableListOf<HMArtObject>()
    var magicList = mutableListOf<HMMagicItem>()
    var spellsList = mutableListOf<HMSpellCollection>()

}