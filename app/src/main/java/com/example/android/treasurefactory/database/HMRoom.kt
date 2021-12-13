package com.example.android.treasurefactory.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.treasurefactory.model.*

//TODO revisit this and consider seperating entities from data class: https://jacquessmuts.github.io/post/modularization_room/

/**
 * Singleton database for entire app.
 */
@Database(
    entities = [
        Hoard::class,
        GemTemplate::class,
        MagicItemTemplate::class,
        SpellTemplate::class,
        Gem::class,
        ArtObject::class,
        MagicItem::class,
        SpellCollection::class],
    version = 1)
@TypeConverters(HoardTypeConverters::class)
abstract class TreasureDatabase : RoomDatabase() {

    abstract fun hoardDao(): HoardDao
    abstract fun gemDao(): GemDao
    abstract fun artDao(): ArtDao
    abstract fun magicItemDao(): MagicItemDao
    abstract fun spellCollectionDao(): SpellCollectionDao

    /* Note for self:
    We do NOT need the singleton here, since this will be instantiated as part of the HM Repository
    class, which will have the singleton with context as necessary. Err on the side of BNR. */
}

//region [ Data Access Objects ]

@Dao
interface HoardDao{

    @Query("SELECT * FROM hackmaster_hoard_table")
    fun getHoards(): LiveData<List<Hoard>>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<Hoard?>
}

@Dao
interface GemDao {

    @Query("SELECT * FROM hackmaster_gem_reference WHERE type=(:type) ORDER BY ordinal")
    fun getGemTableByType(type: String): LiveData<List<GemTemplate>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:parentID)")
    fun getGems(parentID: Int): LiveData<List<Gem>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    fun getGem(id: Int): LiveData<Gem?>

    // Add a gem to the hoard TODO

    // Update this gem in the hoard TODO

    // Remove this gem from hoard TODO
}

@Dao
interface ArtDao {

    @Query("SELECT * FROM hackmaster_art_table WHERE hoardID=(:parentID)")
    fun getArtObjects(parentID: Int): LiveData<List<ArtObject>>

    @Query("SELECT * FROM hackmaster_art_table WHERE artID=(:id)")
    fun getArtObject(id: Int): LiveData<ArtObject?>

    // Add art object to the hoard TODO

    // Update this art object in the hoard TODO

    // Remove this art object from hoard TODO
}

@Dao
interface MagicItemDao {

    /**
     * Pulls all item entries lacking a parent belonging to a given type as a LimitedMagicItemTemplate.
     *
     * @param type String to match in table_type column
     */
    @Query("SELECT ref_id, wt FROM hackmaster_magic_item_reference WHERE table_type=(:type) AND parent_id=0")
    fun getBaseLimItemTempsByType(type: String): LiveData<List<LimitedMagicItemTemplate>>

    /**
     * Pulls all item entries with given ref_id as a LimitedMagicItemTemplate.
     *
     * @param parentID Integer primary key id number of parent entry.
     */
    @Query("SELECT ref_id, wt FROM hackmaster_magic_item_reference WHERE table_type=(:parentID)")
    fun getChildLimItemTempsByParent(parentID: Int): LiveData<List<LimitedMagicItemTemplate>>

    /**
     * Pulls item entry matching given ref_id as MagicItemTemplate.
     *
     * @param itemID Integer primary key ID number of entry to pull.
     */
    @Query("SELECT * FROM hackmaster_magic_item_reference WHERE ref_id=(:itemID) LIMIT 1")
    fun getItemTemplateByID(itemID: Int): LiveData<MagicItemTemplate>

    // Get magic items from hoard TODO

    // Add magic item to hoard TODO

    // Update this magic item in hoard TODO

    // Remove this magic item from hoard TODO

}

@Dao
interface SpellCollectionDao{

    // Pull all spell collections from a hoard TODO
    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE hoardID=(:parentID)")
    fun getSpellCollections(parentID: Int): LiveData<List<SpellCollection>>

    // Pull specific spell collection from hoard with given ID TODO
    @Query("SELECT * FROM hackmaster_spell_collection_table WHERE sCollectID=(:id)")
    fun getSpellCollection(id: Int): LiveData<SpellCollection?>

    // Add spell collection to hoard TODO

    // Update a spell collection in hoard TODO

    // Pull specific spell template by ID
    @Query("SELECT * FROM hackmaster_spell_reference WHERE ref_id(:id)")
    fun getSpellTempByID(id: Int): LiveData<SpellTemplate?>

    // Pull all spell IDs of a level and magical discipline (excluding restricted spells)
    @Query("SELECT ref_id FROM hackmaster_spell_reference WHERE type=(:type) AND level=(:level) AND restricted_to=(NULL OR '')")
    fun getSpellsOfLevelType(type: Int, level: Int): LiveData<List<Int>>

    // Pull all spell IDs of a level and magical discipline
    @Query("SELECT ref_id FROM hackmaster_spell_reference WHERE type=(:type) AND level=(:level)")
    fun getAllSpellsOfLevelType(type: Int, level: Int): LiveData<List<Int>>
}
//endregion