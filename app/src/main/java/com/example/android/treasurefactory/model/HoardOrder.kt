package com.example.android.treasurefactory.model

const val DEFAULT_MAX_SPELLS_PER_SCROLL = 7

data class HoardOrder(var hoardName: String = "Untitled Hoard",
                      var creationDescription: String = "",
                      var copperPieces: Int = 0, // Coinage is determined before order is generated
                      var silverPieces: Int = 0,
                      var electrumPieces: Int = 0,
                      var goldPieces: Int = 0,
                      var hardSilverPieces: Int = 0,
                      var platinumPieces: Int = 0,
                      var gems: Int = 0,
                      var artObjects: Int = 0,
                      var potions: Int = 0,
                      var scrolls: Int = 0,
                      var armorOrWeapons: Int = 0,
                      var anyButWeapons: Int = 0,
                      var anyMagicItems: Int = 0,
                      var extraSpellCols: Int = 0,
                      var genParams: OrderParams = OrderParams()
)

/**
 * Additional parameters for what may be generated with this order.
 */
data class OrderParams(val gemParams: GemRestrictions = GemRestrictions(),
                       val artParams: ArtRestrictions = ArtRestrictions(),
                       val magicParams: MagicItemRestrictions = MagicItemRestrictions())

data class GemRestrictions(val _minLvl: Int = 0, val _maxLvl : Int = 17) {
    val levelRange = IntRange(
        _minLvl.coerceIn(0,17),
        if ( _minLvl.coerceIn(0,17) >= _maxLvl ) _minLvl.coerceIn(0,17) else _maxLvl.coerceIn(0,17)
    )
}

data class ArtRestrictions(val _minLvl: Int = -19, val _maxLvl: Int = 31,
                           val paperMapChance: Int = 0) {
    val levelRange = IntRange(
        _minLvl.coerceIn(-19,31),
        if (_minLvl.coerceIn(-19,31) >= _maxLvl) _minLvl.coerceIn(-19,31) else _maxLvl.coerceIn(-19,31)
    )
}

data class MagicItemRestrictions(val scrollMapChance: Int = 0,
                                 val allowCursedItems: Boolean = true,
                                 val allowIntWeapons: Boolean = true,
                                 val allowArtifacts: Boolean = true,
                                 val spellCoRestrictions: SpellCoRestrictions =
                                     SpellCoRestrictions(
                                         allowCurse = allowCursedItems
                                     ))

data class SpellCoRestrictions(
    val _minLvl: Int = 0,
    val _maxLvl: Int = 9,
    val allowedDisciplines: AllowedDisciplines = AllowedDisciplines(),
    val spellCountMax: Int = DEFAULT_MAX_SPELLS_PER_SCROLL,
    val spellSources: SpCoSources = SpCoSources(true, false, false),
    val allowRestricted: Boolean = false,
    val rerollChoice: Boolean = false,
    val allowCurse: Boolean = true,
    val allowedCurses: SpCoCurses = SpCoCurses.STRICT_GMG,
    val genMethod: SpCoGenMethod = SpCoGenMethod.TRUE_RANDOM,
) {
    val levelRange = IntRange(
        _minLvl.coerceIn(if (allowedDisciplines.arcane) 0..9 else 1..7),
        if (_minLvl.coerceIn(if (allowedDisciplines.arcane) 0..9 else 1..7) >= _maxLvl) {
            _minLvl.coerceIn(if (allowedDisciplines.arcane) 0..9 else 1..7)
        } else {
            _maxLvl.coerceIn(if (allowedDisciplines.arcane) 0..9 else 1..7)
        }
    )
}

data class AllowedDisciplines(val arcane: Boolean = true, val divine: Boolean = true, val natural: Boolean = false)