package com.example.android.treasurefactory

import com.example.android.treasurefactory.database.GemTemplate
import com.example.android.treasurefactory.database.MagicItemTemplate
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
    val SAMPLE_ARCANE_SPELL: SpellTemplate
    val SAMPLE_DIVINE_SPELL: SpellTemplate
    val SAMPLE_GEM_TEMPLATE: GemTemplate
    val SAMPLE_MAGIC_ITEM_TEMPLATE: MagicItemTemplate

    /**
     * Returns a gem based on the method laid out on page 178 of the GameMaster's Guide.
     *
     * @param givenTemplate Primary key to query for a specific gem. Negative values are ignored.
     */
    fun createGem(parentHoardID: Int, givenTemplate: Int = -1): Gem

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
    fun createMagicItemTuple(
        parentHoardID: Int, givenTemplate: Int = -1,
        providedTypes: List<String> = ANY_MAGIC_ITEM_LIST,
        itemRestrictions: MagicItemRestrictions = MagicItemRestrictions()
    ): NewMagicItemTuple

    /** Converts a [SpellCollectionOrder] into a [SpellCollection], always as a scroll.*/
    fun convertOrderToSpellScroll(parentHoard: Int, order: SpellCollectionOrder): SpellCollection

    /** Returns a magic-user spell as if acquired by leveling up, outlined in the GMG/SSG */
    fun getSpellByLevelUp(
        _inputLevel: Int,
        enforcedSchool: String = "",
        rerollChoices: Boolean = false,
        useSSG: Boolean = true
    ): Spell

    fun getInitialSpellbookSpells(
        _specialistType: String = "",
        useSSG: Boolean = true
    ): List<Spell>

    /* TODO finish implementing after first shipped build
    /** Returns a [SpellCollection] of a spellbook following the procedure mentioned on SSG pgs 82-87 */
    fun createSpellBook(parentHoard: Int, _effectiveLevel: Int, _extraSpells: Int,
                        _specialistType: String, extraProperties: Int = 0,
                        extraSpellMethod: SpCoGenMethod = SpCoGenMethod.TRUE_RANDOM,
                        useSSG: Boolean = true,
                        allowRestricted: Boolean): SpellCollection
     */

    /** Generates a treasure map, following the rules outlined on GMG pgs 181 and 182 */
    fun createTreasureMap(
        parentHoard: Int,
        sourceDesc: String = "",
        allowFalseMaps: Boolean = true
    ): MagicItem

    /** Generates ioun stones when indicated by standard magic item generation methods */
    fun createIounStones(parentHoard: Int, qty: Int): List<MagicItem>

    /** Generate Gut Stones if indicated during magic item generation */
    fun createGutStones(parentHoard: Int, qty: Int): List<Gem>

    /** Converts a [SpellTemplate] into a [Spell], converting [type][SpellTemplate.type] to a [SpCoDiscipline]*/
    fun convertTemplateToSpell(
        template: SpellTemplate,
        appendedNotes: List<String> = emptyList()
    ): Spell
}