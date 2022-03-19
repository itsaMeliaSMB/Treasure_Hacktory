package com.example.android.treasurefactory.model



/** Return type of standard magic item generation procedure. */
data class NewMagicItemTuple(val magicItem: MagicItem,
                               val specialItemOrder: SpecialItemOrder? = null, val gemOrder: GemOrder? = null)

/**
 * Messenger object for ordering one or many magic item(s) with special generation procedures.
 *
 * @param spellParams Parameters for items containing spells
 * @param quantity How many to generate, determined during order creation. Ignored for non-ioun stone orders.
 */
data class SpecialItemOrder(val parentHoard:Int, val itemType:SpItType, val spellParams: SpellCollectionOrder?, val quantity: Int = 0)

/** Type of magic item to generate which has special generation procedures. */
enum class SpItType(){
    SPELL_SCROLL,RING_OF_SPELL_STORING,IOUN_STONES,TREASURE_MAP
}

// region [ Spell Collection classes ]
/**
 * Messenger object for passing a simple spell collection generation order.
 *
 * @param collectionType The form of spell collection to generate.
 * @param spellType Magical discipline to draw spells from.
 * @param spellCount How many spells to explicitly generate. If [genMethod] is set to [CHOSEN_ONE][SpCoGenMethod.CHOSEN_ONE], represents Chosen One's CHA Comeliness modifier.
 * @param spellLvRange Level of spells to generate, prioritizing minimum. If [genMethod] is set to [CHOSEN_ONE][SpCoGenMethod.CHOSEN_ONE], minimum is used as Chosen One's level.
 * @param allowedSources What source material spells may be pulled from. PHB is always allowed.
 * @param allowRestricted Whether or not specialist-restricted spells may be included. For spell books, may generate specialist's spell books.
 * @param genMethod How to pick which spells are generated.
 * @param allowedCurses What set of curses may be applied to this collection.
 */
data class SpellCollectionOrder(val collectionType: SpCoType,
                                val spellType: SpCoDiscipline,
                                val spellCount: Int,
                                val spellLvRange: IntRange,
                                val allowedSources: SpCoSources,
                                val allowRestricted: Boolean,
                                val genMethod: SpCoGenMethod = SpCoGenMethod.TRUE_RANDOM,
                                val isCursed: Boolean,
                                val allowedCurses: SpCoCurses = SpCoCurses.ANY_CURSE)

/** Type of spell collection to generate. */
enum class SpCoType{
    SCROLL,
    BOOK,
    ALLOTMENT,
    RING,
    OTHER
}

/** Sources to pull spells from in addition to the Player's Handbook. */
data class SpCoSources(val splatbooksOK: Boolean, val hackJournalsOK: Boolean, val modulesOK: Boolean)

/** Which curses may be applied to this spell collection. */
enum class SpCoCurses{

    /**
     * Do not add any curses, even if indicated otherwise.
     */
    NONE,

    /**
     * Only sources curses from sample scroll curse list on GMG pgs 225-226.
     */
    STRICT_GMG,

    /**
     * Sources GMG sample curses and all official examples of curses.
     */
    OFFICIAL_ONLY,

    /**
     * Sources all curses, including those made for this app.
     */
    ANY_CURSE
}

/** What magical discipline to pull spells from. */
enum class SpCoDiscipline{
    /**
     * Only pull magic-user/specialist spells.
     */
    ARCANE,

    /**
     * Only pull cleric/zealot spells.
     */
    DIVINE,

    /**
     * Only pull druid spells. Non-standard.
     */
    NATURAL,

    /**
     * Pull any type of spell for this collection.
     */
    ALL_MAGIC
}

/** How generator is to pick spells when creating this collection's spells. */
enum class SpCoGenMethod{

    /**
     * Unweighted, random selection from all eligible spells in database.
     */
    TRUE_RANDOM,

    /**
     * Follow rules on GMG pg 75 and SSG pg 6 for spell generation.
     * Starting spells are not counted towards spellCount.
     * Duplicates are disallowed.
     * Upper-level spells are rolled using [LEVEL_ACQUISITION] rules.
     */
    SPELL_BOOK, //TODO Unimplemented

    /**
     * Follows spell acquisition rules for magic-user level-up procedure on SSG pg 7.
     */
    LEVEL_ACQUISITION, //TODO Unimplemented

    /**
     * Follows Chosen One daily spell allotment from ZG pgs 6 and 132-133.
     */
    CHOSEN_ONE, //TODO Unimplemented

    /**
     * Combination of [TRUE_RANDOM], [SPELL_BOOK], and [LEVEL_ACQUISITION].
     */
    ANY_PHYSICAL //TODO Unimplemented
}
// endregion

/** Messenger object for generating a gem when one is called for during another item type generation. */
data class GemOrder(val gemTemplate : Int = GUT_STONE_KEY, val qtyToMake: Int)