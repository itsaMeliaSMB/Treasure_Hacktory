package com.example.android.treasurefactory

data class HMHoardOrder(var hoardName: String = "Untitled Hoard",
                        var creationDescription: String = "",
                        var copperPieces: Int = 0 , // Coinage is determined before order is generated
                        var silverPieces: Int = 0 ,
                        var electrumPieces: Int = 0 ,
                        var goldPieces: Int = 0 ,
                        var hardSilverPieces: Int = 0 ,
                        var platinumPieces: Int = 0 ,
                        var gems: Int = 0 ,
                        var artObjects: Int = 0 ,
                        var potions: Int = 0,
                        var scrolls: Int = 0,
                        var armorOrWeapons: Int = 0,
                        var anyButWeapons: Int = 0,
                        var anyMagicItems: Int = 0,
                        val genParams: HMOrderParams = HMOrderParams()
)

data class HMOrderParams(val _gemMinLvl: Int = 0, val _gemMaxLvl : Int = 16,
                        val _artMinLvl: Int = -19, val _artMaxLvl: Int = 31,
                        val allowArtMaps: Boolean = false,
                        val allowArcane: Boolean = true,
                        val allowDivine: Boolean= true,
                        val allowNonSpell: Boolean = true,
                        val allowScrollMaps: Boolean= false,
                        val _spellMinLvl: Int = 1, val _spellMaxLvl: Int = 9,
                        val allowIntWeapons: Boolean = true,
                        val allowArtifacts: Boolean = true) {

    val gemMinLvl = correctOOBInt(_gemMinLvl, 0, 16)
    val gemMaxLvl = correctOOBInt(_gemMaxLvl, gemMinLvl, 16)
    val artMinLvl = correctOOBInt(_artMinLvl, -19, 31)
    val artMaxLvl = correctOOBInt(_artMaxLvl, artMinLvl, 31)
    val spellMinLvl = correctOOBInt(_spellMinLvl, 0, 9)
    val spellMaxLvl = correctOOBInt(_spellMaxLvl, spellMinLvl, 9)

    private fun correctOOBInt (input: Int, min: Int, max: Int) =
        if (input < min) { min } else { if (input > max) { max } else { input } }

    // TODO: When go to spell generation, build in safeguards for generating divine spells despite ranges outside of 1..7
}
