package com.example.android.treasurefactory

import com.example.android.treasurefactory.database.SpellTemplate
import com.example.android.treasurefactory.model.*

const val ORDER_LABEL_STRING = "order_details"

const val MAX_SPELLS_PER_SCROLL = 50
const val MAX_SPELLS_PER_BOOK = 120

/**
 * Primary key of first single Ioun stone in database.
 *
 * @since 3/13/2022
 * */
const val FIRST_IOUN_STONE_KEY = 855
/**
 * Primary key of last (dead) single Ioun stone in database.
 *
 * @since 3/13/2022
 * */
const val LAST_IOUN_STONE_KEY = 868
/** Primary key for Gut Stone entry in gem database */
const val GUT_STONE_KEY = 58

interface BaseLootGenerator {
    val ANY_MAGIC_ITEM_LIST: List<String>

    val DUMMY_SPELL : SpellTemplate

    suspend fun createHoardFromOrder(hoardOrder: HoardOrder, appVersion: Int): Int

    /**
     * Returns a gem based on the method laid out on page 178 of the GameMaster's Guide.
     *
     * @param givenTemplate Primary key to query for a specific gem. Negative values are ignored.
     */
    suspend fun createGem(parentHoardID: Int, givenTemplate: Int = -1): Gem

    /**
     * Returns an art object based on the method laid out in HackJournal #6
     */
    fun createArtObject(
        parentHoardID: Int,
        itemRestrictions: ArtRestrictions
    ): Pair<ArtObject, MagicItem?>

    /**
     * Returns a magic item. Returns a "Nothing" item if an error is encountered.
     *
     * @param givenTemplate Primary key to query for a specific item. Negative values are ignored.
     * @param providedTypes Tables that are allowed to be queried to pick an item.
     * @param itemRestrictions inherited parameters from hoard order limited what can be generated.
     */
    suspend fun createMagicItemTuple(
        parentHoardID: Int, givenTemplate: Int = -1,
        providedTypes: List<String> = ANY_MAGIC_ITEM_LIST,
        itemRestrictions: MagicItemRestrictions = MagicItemRestrictions()
    ): NewMagicItemTuple

    /** Generates a treasure map, following the rules outlined on GMG pgs 181 and 182 */
    fun createTreasureMap(
        parentHoard: Int,
        sourceDesc: String = "",
        allowFalseMaps: Boolean = true
    ): MagicItem

    /** Generates ioun stones when indicated by standard magic item generation methods */
    suspend fun createIounStones(parentHoard: Int, qty: Int): List<MagicItem>

    /** Generate Gems */
    suspend fun createGemsFromGemOrder(parentHoard: Int, gemOrder: GemOrder): List<Gem>

    suspend fun createRingOfSpellStoring(parentHoard: Int, order: SpellCollectionOrder) : SpellCollection

    /** Converts a [SpellCollectionOrder] into a [SpellCollection], always as a scroll.*/
    suspend fun convertOrderToSpellScroll(parentHoard: Int, order: SpellCollectionOrder): SpellCollection

    suspend fun createSpellCollection(parentHoard: Int, spCoParams: SpellCoRestrictions) : SpellCollection

    fun createNewScrollOrder(scrollParams: SpellCoRestrictions, isByBook: Boolean) : SpellCollectionOrder


    /** Gets a random spell from allowed sources of the level, discipline, and restriction provided. */
    suspend fun getRandomSpell(_inputLevel: Int, _discipline: SpCoDiscipline,
                       sources: SpCoSources, rerollChoices: Boolean = false, allowRestricted: Boolean): Spell

    /** Returns a magic-user spell as if acquired by leveling up, outlined in the GMG/SSG */
    suspend fun getSpellByLevelUp(
        _inputLevel: Int,
        enforcedSchool: SpellSchool? = null,
        rerollChoices: Boolean = false,
        useSSG: Boolean = true
    ): Spell

    /* TODO implement when spellbooks are implemented

    fun getInitialSpellbookSpells(
        _specialistType: String = "",
        useSSG: Boolean = true
    ): List<Spell>*/

    /**
     * Returns a cleric or druid spell using Appendix E on pgs 132-133 of ZG
     *
     * @param _inputLevel Spell level of the spell to be generated.
     * @param allowDruid If false, any druid-only spells will be re-rolled.
     * @param useZG If false, spells not in the PHB will be re-rolled.
     * @param _maxCastable Highest castable spell level of theoretical caster. If 0, any
     * restrictions (i.e. spell level of Indulgence) regarding spell level are ignored.
     */
    suspend fun getSpellByChosenOneTable(
        _inputLevel: Int,
        allowDruid: Boolean,
        useZG: Boolean,
        _maxCastable: Int = 0) : Spell

    /* TODO finish implementing after first shipped build
    /** Returns a [SpellCollection] of a spellbook following the procedure mentioned on SSG pgs 82-87 */
    fun createSpellBook(parentHoard: Int, _effectiveLevel: Int, _extraSpells: Int,
                        _specialistType: String, extraProperties: Int = 0,
                        extraSpellMethod: SpCoGenMethod = SpCoGenMethod.TRUE_RANDOM,
                        useSSG: Boolean = true,
                        allowRestricted: Boolean): SpellCollection
     */
}