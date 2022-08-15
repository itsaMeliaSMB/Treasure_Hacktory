package com.example.android.treasurefactory

import android.util.Log
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MultihoardProcessor(private val repository: HMRepository) {
    
    fun mergeHoards(hoardsToMerge: List<Hoard>, keepOriginal: Boolean) {
        
        // TODO Check if hoards targeted can be merged

        // TODO If permitted, create new combined hoard

        // TODO Add updated hoard to database

        // TODO return a result
    }
    
    fun checkHoardMergeability(hoardsToCheck: List<Hoard>): Pair<Boolean,String> {

        if (hoardsToCheck.size < 2) {
            return false to "Must have multiple hoards in order to merge."
        }

        // Check if hoard

        return true to "Merge permitted."
    }

    fun splitHoard(originalHoard: Hoard, splitHoard: Hoard, splitGems: List<Gem>,
                   splitArt: List<ArtObject>, splitItems: List<MagicItem>,
                   splitSpellCollections: List<SpellCollection>, keepOriginal: Boolean) {

        // TODO Ensure split is permitted first
            // Split cannot leave an empty hoard
            // Split cannot take out more coinage than is present in original

        // TODO Create and add new split hoard

        // TODO Remove items and coinage from original if instructed
            // Record split in hoard events
    }

    suspend fun copyHoards(hoardsToCopy: List<Hoard>) {

        coroutineScope {

            hoardsToCopy.forEach { originalHoard ->

                val newHoardID : Int
                val newItemBundle : HoardUniqueItemBundle
                val cloneHoardName = if (originalHoard.name.endsWith(" [Copy]")) {
                    originalHoard.name
                } else { originalHoard.name + " [Copy]" }

                var newCloneHoard = originalHoard.copy(hoardID = 0, name = cloneHoardName, isNew = true)

                // Add hoard to database and get its ID
                newHoardID = repository.getHoardIdByRowId(repository.addHoard(newCloneHoard))

                // Copy original hoard's items with scrubbed primary keys
                newItemBundle = getHoardUniqueItemBundle(originalHoard.hoardID).scrub(newHoardID)

                // Add copied items to hoard
                val addGemsJob = launch { newItemBundle.hoardGems.forEach {
                    repository.addGem(it) } }
                val addArtJob = launch { newItemBundle.hoardArt.forEach {
                    repository.addArtObject(it) } }
                val addItemsJob = launch { newItemBundle.hoardItems.forEach {
                    repository.addMagicItem(it) } }
                val addSpellsJob = launch { newItemBundle.hoardSpellCollections.forEach {
                    repository.addSpellCollection(it) } }

                joinAll(addGemsJob, addArtJob, addItemsJob, addSpellsJob)

                // Update all value counts on cloned hoard.

                suspend fun updateItemCounts() {

                    val gemCount = repository.getGemCountOnce(newHoardID)
                    val artCount = repository.getArtCountOnce(newHoardID)
                    val itemCount = repository.getMagicItemCountOnce(newHoardID)
                    val spellCount = repository.getSpellCollectionCountOnce(newHoardID)

                    val gpTotal = (newCloneHoard.let {
                        ((it.cp * 0.01) + (it.sp * 0.1) + (it.ep * 0.5) + (it.gp * 1.0) +
                                (it.hsp * 2.0) + (it.pp * 5.0)) * 100.00.roundToInt() / 100.00 }) +
                            (repository.getGemValueTotalOnce(newHoardID)) +
                            (repository.getArtValueTotalOnce(newHoardID)) +
                            (repository.getMagicItemValueTotalOnce(newHoardID)) +
                            (repository.getSpellCollectionValueTotalOnce(newHoardID))

                    val updatedHoard = repository.getHoardOnce(newHoardID).takeIf { it != null }?.copy(
                        gpTotal = gpTotal,
                        gemCount = gemCount,
                        artCount = artCount,
                        magicCount = itemCount,
                        spellsCount = spellCount,
                        successful = true)

                    if (updatedHoard != null) {
                        repository.updateHoard(updatedHoard)
                    } else {
                        Log.e("updateItemCounts()","updatedHoard is null.")
                    }
                }

                updateItemCounts()

                // Report copy event on original hoard and copy over events to new hoard
                val newHoardEvents =
                    listOf(listOf(
                        HoardEvent(
                            hoardID = newHoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Hoard \"$cloneHoardName\" generated as copy of " +
                                    originalHoard.name + ".",
                            tag = "creation|duplication")),
                        repository.getHoardEventsOnce(originalHoard.hoardID).map{ originalEvent ->
                            originalEvent.copy(
                                hoardID = newHoardID,
                                eventID = 0,
                                description = "[Copied] " + originalEvent.description,
                                tag = originalEvent.tag +
                                        if (!originalEvent.tag.contains("duplication"))
                                            "|duplication" else ""
                            )
                        }).flatten()

                repository.addHoardEvent(
                    HoardEvent(
                        hoardID = originalHoard.hoardID,
                        timestamp = System.currentTimeMillis(),
                        description = "Full copy of this hoard called \"" + cloneHoardName +
                                "\" generated.",
                        tag = "duplication")
                )

                newHoardEvents.forEach { repository.addHoardEvent(it) }
            }
        }
    }

    /**
     * Pulls all unique items associated with a given hoard IDm or an empty list if no Hoard
     * corresponds to the provided key.
     */
    suspend fun getHoardUniqueItemBundle(hoardID: Int): HoardUniqueItemBundle {

        val hoardGemList :  List<Gem>
        val hoardArtList :  List<ArtObject>
        val hoardItemList:  List<MagicItem>
        val hoardSpellList: List<SpellCollection>

        coroutineScope {

            if (repository.getHoardOnce(hoardID) != null) {

                // Pull all unique items for hoard, if there are any.
                hoardGemList = repository.getGemsOnce(hoardID)
                hoardArtList = repository.getArtObjectsOnce(hoardID)
                hoardItemList = repository.getMagicItemsOnce(hoardID)
                hoardSpellList = repository.getSpellCollectionsOnce(hoardID)

            } else {

                // Return empty lists of items
                hoardGemList = emptyList()
                hoardArtList = emptyList()
                hoardItemList = emptyList()
                hoardSpellList = emptyList()
            }
        }

        return HoardUniqueItemBundle(hoardGemList,hoardArtList,hoardItemList,hoardSpellList)
    }

    /**
     * Returns a copy of this HoardUniqueItemBundle with all unique item IDs replaced with zero and
     * the new parent hoard applied, if provided ID is valid.
     */
    suspend fun HoardUniqueItemBundle.scrub(newParentID: Int): HoardUniqueItemBundle {

        return if (newParentID < 1 || repository.getHoardOnce(newParentID) == null) {
            HoardUniqueItemBundle(
                hoardGems = this.hoardGems.map { originalGem ->
                    originalGem.copy(gemID = 0) },
                hoardArt = this.hoardArt.map { originalArt ->
                    originalArt.copy(artID = 0) },
                hoardItems = this.hoardItems.map { originalItem ->
                    originalItem.copy(mItemID = 0) },
                hoardSpellCollections = this.hoardSpellCollections.map { originalCollection ->
                    originalCollection.copy(sCollectID = 0) }
            )
        } else {
            HoardUniqueItemBundle(
                hoardGems = this.hoardGems.map { originalGem ->
                    originalGem.copy(gemID = 0, hoardID = newParentID) },
                hoardArt = this.hoardArt.map { originalArt ->
                    originalArt.copy(artID = 0, hoardID = newParentID) },
                hoardItems = this.hoardItems.map { originalItem ->
                    originalItem.copy(mItemID = 0, hoardID = newParentID) },
                hoardSpellCollections = this.hoardSpellCollections.map { originalCollection ->
                    originalCollection.copy(sCollectID = 0, hoardID = newParentID) }
            )
        }
    }
}