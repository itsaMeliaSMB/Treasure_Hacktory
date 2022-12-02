package com.treasurehacktory.model

import com.treasurehacktory.viewmodel.MAXIMUM_SPELLS_PER_SCROLL

data class HoardOrder(
    var hoardName: String = "Untitled Hoard",
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
    var baseMaps: Int = 0,
    val allowFalseMaps: Boolean = true,
    var genParams: OrderParams = OrderParams()
)

/**
 * Additional parameters for what may be generated with this order.
 */
data class OrderParams(val gemParams: GemRestrictions = GemRestrictions(),
                       val artParams: ArtRestrictions = ArtRestrictions(),
                       val magicParams: MagicItemRestrictions = MagicItemRestrictions()
)

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

data class MagicItemRestrictions(
    val spellScrollEnabled: Boolean = true,
    val nonScrollEnabled: Boolean = true,
    val scrollMapChance: Int = 0,
    val allowedTables: Set<MagicItemType> = enumValues<MagicItemType>().toSet(),
    val allowCursedItems: Boolean = true,
    val allowIntWeapons: Boolean = true,
    val itemSources: SpCoSources = SpCoSources(false, false, false),
    val spellCoRestrictions: SpellCoRestrictions =
        SpellCoRestrictions(
            allowCurse = allowCursedItems
        )
)

data class SpellCoRestrictions(
    val _minLvl: Int = 0,
    val _maxLvl: Int = 9,
    val allowedDisciplines: AllowedDisciplines = AllowedDisciplines(),
    val spellCountRange: IntRange = IntRange(1, MAXIMUM_SPELLS_PER_SCROLL),
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

/**
 * Data class for holding all user preferences captured from HoardGeneratorFragment's option dialog.
 */
data class GeneratorOptions(
    val gemMin: Int = 0, val gemMax: Int = 17, val artMin: Int = -19, val artMax: Int = 31,
    val mapBase: Int = 0, val mapPaper: Int = 0, val mapScroll: Int = 0,
    val falseMapsOK: Boolean = true,
    val allowedMagic: Set<MagicItemType> = setOf(
        MagicItemType.A2,
        MagicItemType.A3,
        MagicItemType.A4,
        MagicItemType.A5,
        MagicItemType.A6,
        MagicItemType.A7,
        MagicItemType.A8,
        MagicItemType.A9,
        MagicItemType.A10,
        MagicItemType.A11,
        MagicItemType.A12,
        MagicItemType.A13,
        MagicItemType.A14,
        MagicItemType.A15,
        MagicItemType.A16,
        MagicItemType.A17,
        MagicItemType.A18,
        MagicItemType.A20,
        MagicItemType.A21,
        MagicItemType.A23,
        MagicItemType.A24
    ),
    val spellOk: Boolean = true, val utilityOk: Boolean = true, val cursedOk: Boolean = true,
    val intelOk: Boolean = true, val spellDisciplinePos: Int = 3,
    val spellMethod: SpCoGenMethod = SpCoGenMethod.TRUE_RANDOM,
    val spellCurses: SpCoCurses = SpCoCurses.STRICT_GMG, val spellReroll: Boolean = true,
    val restrictedOk: Boolean = false,
    val allowedItemSources: SpCoSources = SpCoSources(
        splatbooksOK = false, hackJournalsOK = false, modulesOK = false
    ),
    val allowedSpellSources: SpCoSources = SpCoSources(
        splatbooksOK = true, hackJournalsOK = false, modulesOK = false
    )
)