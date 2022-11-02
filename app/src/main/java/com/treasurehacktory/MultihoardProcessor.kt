package com.treasurehacktory

import com.treasurehacktory.model.*
import com.treasurehacktory.repository.HMRepository
import com.treasurehacktory.viewmodel.MAXIMUM_COINAGE_AMOUNT
import com.treasurehacktory.viewmodel.MAXIMUM_HOARD_VALUE
import com.treasurehacktory.viewmodel.MAXIMUM_SPELL_COLLECTION_QTY
import com.treasurehacktory.viewmodel.MAXIMUM_UNIQUE_QTY
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MultihoardProcessor(private val repository: HMRepository) {

    /**
     * Moves valid items passed in to an acceptor [Hoard] by changing the hoard ID on the items and
     * re-evaluating all affected hoards.
     *
     * @return Pair of whether or not move was successful and provided message.
     */
    suspend fun moveItems(acceptorHoardID: Int, itemsToMove: List<ViewableItem>) : Pair<Boolean,String>{

        val donorHoardIDs = mutableSetOf<Int>()

        val gemsToMove = ArrayList<Gem>()
        val artObjectsToMove = ArrayList<ArtObject>()
        val magicItemsToMove = ArrayList<MagicItem>()
        val spellCollectionsToMove = ArrayList<SpellCollection>()

        itemsToMove.forEach { itemToMove ->

            // Log unique hoard IDs
            donorHoardIDs.add(itemToMove.hoardID)

            // Convert and cache item to move
            when (itemToMove){
                is ViewableGem -> {
                    val gemToMove = itemToMove.toGem()
                    gemsToMove.add(gemToMove)
                }
                is ViewableArtObject -> {
                    val artToMove = itemToMove.toArtObject()
                    artObjectsToMove.add(artToMove)
                }
                is ViewableMagicItem -> {
                    val mItemToMove = itemToMove.toMagicItem()
                    magicItemsToMove.add(mItemToMove)
                }
                is ViewableSpellCollection -> {
                    val spCoToMove = itemToMove.toSpellCollection()
                    spellCollectionsToMove.add(spCoToMove)
                }
            }
        }

        // Check if new Hoard would exceed limits
        val hoardCapacityResult = ensureHoardCapacity(acceptorHoardID,
            HoardUniqueItemBundle(
                gemsToMove.toList(),
                artObjectsToMove.toList(),
                magicItemsToMove.toList(),
                spellCollectionsToMove.toList()),
            emptyMap())

        if (hoardCapacityResult.first) {

            // Proceed only if move is permitted.
            /**
             * Key is the original hoard ID of the item. Value is a list of a moved items'
             * [UniqueItemType]s, names, and ids.
             */
            val moveLedger = mutableMapOf<Int,List<Triple<UniqueItemType, String, Int>>>()
            var moveOccurred = false

            // Log prospective moves and update lists
            gemsToMove.forEachIndexed { index, movedItem ->

                // Log in move ledger
                val itemTriple = Triple(UniqueItemType.GEM, movedItem.name, movedItem.gemID)

                val priorList = moveLedger.getOrPut(movedItem.hoardID) { emptyList() }

                moveLedger[movedItem.hoardID] = listOf(priorList, listOf(itemTriple)).flatten()

                // Change the hoard ID on the affected item
                gemsToMove[index] = movedItem.copy(hoardID = acceptorHoardID)
            }

            artObjectsToMove.forEachIndexed { index, movedItem ->

                // Log in move ledger
                val itemTriple = Triple(UniqueItemType.ART_OBJECT, movedItem.name, movedItem.artID)

                val priorList = moveLedger.getOrPut(movedItem.hoardID) { emptyList() }

                moveLedger[movedItem.hoardID] = listOf(priorList, listOf(itemTriple)).flatten()

                // Change the hoard ID on the affected item
                artObjectsToMove[index] = movedItem.copy(hoardID = acceptorHoardID)
            }

            magicItemsToMove.forEachIndexed { index, movedItem ->

                // Log in move ledger
                val itemTriple = Triple(UniqueItemType.MAGIC_ITEM, movedItem.name, movedItem.mItemID)

                val priorList = moveLedger.getOrPut(movedItem.hoardID) { emptyList() }

                moveLedger[movedItem.hoardID] = listOf(priorList, listOf(itemTriple)).flatten()

                // Change the hoard ID on the affected item
                magicItemsToMove[index] = movedItem.copy(hoardID = acceptorHoardID)
            }

            spellCollectionsToMove.forEachIndexed { index, movedItem ->

                // Log in move ledger
                val itemTriple = Triple(UniqueItemType.SPELL_COLLECTION, movedItem.name, movedItem.sCollectID)

                val priorList = moveLedger.getOrPut(movedItem.hoardID) { emptyList() }

                moveLedger[movedItem.hoardID] = listOf(priorList, listOf(itemTriple)).flatten()

                // Change the hoard ID on the affected item
                spellCollectionsToMove[index] = movedItem.copy(hoardID = acceptorHoardID)
            }

            // Update listed items in db
            if (gemsToMove.isNotEmpty()) {
                repository.updateGems(gemsToMove.toList())
                moveOccurred = true
            }
            if (artObjectsToMove.isNotEmpty()) {
                repository.updateArtObjects(artObjectsToMove.toList())
                moveOccurred = true
            }
            if (magicItemsToMove.isNotEmpty()) {
                repository.updateMagicItems(magicItemsToMove.toList())
                moveOccurred = true
            }
            if (spellCollectionsToMove.isNotEmpty()) {
                repository.updateSpellCollections(spellCollectionsToMove.toList())
                moveOccurred = true
            }

            if (moveOccurred) {

                val moveEvents = ArrayList<HoardEvent>()
                val originalAcceptor = repository.getHoardOnce(acceptorHoardID)
                val newHoards = ArrayList<Hoard?>()

                // Acceptor event
                moveLedger.values.toList().flatten().let { itemTripleList ->

                    val gemList = ArrayList<String>()
                    val artList = ArrayList<String>()
                    val itemList = ArrayList<String>()
                    val spCoList = ArrayList<String>()

                    val maxSize = 25

                    val description = StringBuilder().apply{
                        append("The following items were moved this hoard [id:$acceptorHoardID] :\n\n")
                    }
                    val tags = StringBuilder().apply{
                        append("merge")
                    }

                    itemTripleList.sortedBy { it.first }.forEach {
                        when (it.first) {
                            UniqueItemType.GEM -> {
                                gemList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:$${it.third}]")
                            }
                            UniqueItemType.ART_OBJECT -> {
                                artList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:$${it.third}]")
                            }
                            UniqueItemType.MAGIC_ITEM -> {
                                itemList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:$${it.third}]")
                            }
                            UniqueItemType.SPELL_COLLECTION -> {
                                spCoList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:$${it.third}]")
                            }
                        }
                    }

                    if (gemList.size > 0) {
                        description.append("< Gemstone(s) & Jewel(s) >\n")
                        if (gemList.size > maxSize) {
                            gemList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${gemList.size - maxSize} more.\n")
                        } else {
                            gemList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|gemstone")
                    }

                    if (artList.size > 0) {
                        description.append("< Art Object(s) >\n")
                        if (artList.size > maxSize) {
                            artList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${artList.size - maxSize} more.\n")
                        } else {
                            artList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|art-object")
                    }

                    if (itemList.size > 0) {
                        description.append("< Magic item(s) >\n")
                        if (itemList.size > maxSize) {
                            itemList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${itemList.size - maxSize} more.\n")
                        } else {
                            artList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|magic-item")
                    }

                    if (spCoList.size > 0) {
                        description.append("< Spell Collection(s) >\n")
                        if (itemList.size > maxSize) {
                            itemList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${itemList.size - maxSize} more.\n")
                        } else {
                            artList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|spell-collection")
                    }

                    val acceptorEvent = HoardEvent(
                        timestamp = System.currentTimeMillis(),
                        hoardID = acceptorHoardID,
                        description = description.toString(),
                        tag = tags.toString()
                    )

                    moveEvents.add(acceptorEvent)
                }

                // Donor events
                moveLedger.forEach { (origParentID, itemTripleList) ->

                    val gemList = ArrayList<String>()
                    val artList = ArrayList<String>()
                    val itemList = ArrayList<String>()
                    val spCoList = ArrayList<String>()

                    val maxSize = 25

                    val description = StringBuilder().apply{
                        append("The following items were moved from this hoard to \"" +
                                originalAcceptor?.name + "\" [id:$acceptorHoardID] :\n\n")
                    }
                    val tags = StringBuilder().apply{
                        append("merge")
                    }

                    itemTripleList.sortedBy { it.first }.forEach {
                        when (it.first) {
                            UniqueItemType.GEM -> {
                                gemList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..." 
                                    else {it.second} } [id:${it.third}]")
                            }
                            UniqueItemType.ART_OBJECT -> {
                                artList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:${it.third}]")
                            }
                            UniqueItemType.MAGIC_ITEM -> {
                                itemList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:${it.third}]")
                            }
                            UniqueItemType.SPELL_COLLECTION -> {
                                spCoList.add("${
                                    if (it.second.length > 25) it.second.take(25) + "..."
                                    else {it.second} } [id:${it.third}]")
                            }
                        }
                    }

                    if (gemList.size > 0) {
                        description.append("< Gemstone(s) & Jewel(s) >\n")
                        if (gemList.size > maxSize) {
                            gemList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${gemList.size - maxSize} more.\n")
                        } else {
                            gemList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|gemstone")
                    }

                    if (artList.size > 0) {
                        description.append("< Art Object(s) >\n")
                        if (artList.size > maxSize) {
                            artList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${artList.size - maxSize} more.\n")
                        } else {
                            artList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|art-object")
                    }

                    if (itemList.size > 0) {
                        description.append("< Magic item(s) >\n")
                        if (itemList.size > maxSize) {
                            itemList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${itemList.size - maxSize} more.\n")
                        } else {
                            itemList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|magic-item")
                    }

                    if (spCoList.size > 0) {
                        description.append("< Spell Collection(s) >\n")
                        if (spCoList.size > maxSize) {
                            spCoList.take(maxSize).forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                            description.append("\t...And ${spCoList.size - maxSize} more.\n")
                        } else {
                            spCoList.forEachIndexed { index, entry ->
                                description.append("\t[#${index+1}] $entry\n")
                            }
                        }
                        tags.append("|spell-collection")
                    }

                    val donorEvent = HoardEvent(
                        timestamp = System.currentTimeMillis(),
                        hoardID = origParentID,
                        description = description.toString(),
                        tag = tags.toString()
                    )

                    moveEvents.add(donorEvent)
                }

                // Update values on affected hoards
                newHoards.add(LootMutator.auditHoard(acceptorHoardID, repository))

                donorHoardIDs.forEach { donorID ->
                    newHoards.add(LootMutator.auditHoard(donorID, repository))
                }

                // Update hoards and events in database.
                repository.updateHoards(newHoards.filterNotNull())
                repository.addHoardEvent(moveEvents.toList())

                return true to "${itemsToMove.size} item(s) successfully moved."
            }

            return false to "No items were actually selected."

        } else {

            return hoardCapacityResult
        }
    }

    suspend fun ensureHoardCapacity(targetHoardID: Int, newItems: HoardUniqueItemBundle,
                                    newCoinage: Map<CoinType,Int>) : Pair<Boolean,String> {

        val originalHoard = repository.getHoardOnce(targetHoardID)

        if (originalHoard != null) {

            val newGemTotalValue : Double
            val newArtTotalValue : Double
            val newItemTotalValue : Double
            val newSpCoTotalValue : Double

            val newCoinTotals = newCoinage.toMutableMap().apply{
                enumValues<CoinType>().forEach { enumCoinType ->
                    this.putIfAbsent(enumCoinType, 0) } }.toSortedMap().toList()

            newItems.run {

                newGemTotalValue = hoardGems.sumOf { it.currentGPValue}
                newArtTotalValue = hoardArt.sumOf { it.gpValue }
                newItemTotalValue = hoardItems.sumOf { it.gpValue }
                newSpCoTotalValue = hoardSpellCollections.sumOf { it.gpValue }
            }

            val newTotalValue = originalHoard.gpTotal +
                    newCoinTotals.sumOf { (coinType, coinQty) ->
                        (coinQty * coinType.gpValue * 100.00).roundToInt() / 100.00 } +
                    newGemTotalValue + newArtTotalValue + newItemTotalValue + newSpCoTotalValue

            return when {

                newTotalValue > MAXIMUM_HOARD_VALUE ->
                    false to "Total value of hoard would be " +
                            ((newTotalValue * 100.00).roundToInt() / 100.00).toString() +
                            " gp, which exceeds the maximum single-hoard value of $MAXIMUM_HOARD_VALUE gp."

                (newCoinTotals[0].second * newCoinTotals[0].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[0].second * newCoinTotals[0].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of copper pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newCoinTotals[1].second * newCoinTotals[1].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[1].second * newCoinTotals[1].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of silver pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newCoinTotals[2].second * newCoinTotals[2].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[2].second * newCoinTotals[2].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of electrum pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newCoinTotals[3].second * newCoinTotals[3].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[3].second * newCoinTotals[3].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of gold pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newCoinTotals[4].second * newCoinTotals[4].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[4].second * newCoinTotals[4].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of hard silver pieces. " +
                            "Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newCoinTotals[5].second * newCoinTotals[5].first.gpValue * 100.00).roundToInt() / 100.00 >
                        MAXIMUM_COINAGE_AMOUNT ->
                    false to "Hoard would have " +
                            ((newCoinTotals[5].second * newCoinTotals[5].first.gpValue * 100.00)
                                .roundToInt() / 100.00).toString() +
                            " gp worth of platinum pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

                (newItems.hoardGems.size + repository.getGemCountOnce(targetHoardID))
                        > MAXIMUM_UNIQUE_QTY ->
                    false to "Hoard would have ${(newItems.hoardGems.size + 
                            repository.getGemCountOnce(targetHoardID))} gems. Maximum allowed is " +
                            "$MAXIMUM_UNIQUE_QTY gems."

                (newItems.hoardArt.size + repository.getArtCountOnce(targetHoardID))
                        > MAXIMUM_UNIQUE_QTY ->
                    false to "Hoard would have ${(newItems.hoardArt.size + 
                            repository.getArtCountOnce(targetHoardID))} art objects. Maximum " +
                            "allowed is $MAXIMUM_UNIQUE_QTY art objects."

                (newItems.hoardItems.size + repository.getMagicItemCountOnce(targetHoardID))
                        > MAXIMUM_UNIQUE_QTY ->
                    false to "Hoard would have ${(newItems.hoardItems.size + 
                            repository.getMagicItemCountOnce(targetHoardID))} magic items. " +
                            "Maximum allowed is $MAXIMUM_UNIQUE_QTY magic items."

                (newItems.hoardSpellCollections.size + repository.getSpellCollectionCountOnce(targetHoardID))
                        > MAXIMUM_SPELL_COLLECTION_QTY ->
                    false to "Hoard would have ${(newItems.hoardSpellCollections.size + 
                            repository.getSpellCollectionCountOnce(targetHoardID))} spell " +
                            "collections. Maximum allowed is $MAXIMUM_SPELL_COLLECTION_QTY " +
                            "regardless of number of spells contained."

                else ->
                    true to "Move permitted."
            }

        } else {

            return false to "No hoard found at id $targetHoardID"
        }
    }

    /**
     * Creates a new hoard combining the coinage and unique items of the original hoard, adding it
     * to the database and discarding originals, unless directed otherwise.
     * @param providedName If not null, the name of the new super-hoard.
     * @param keepOriginal If true, original hoards are not consumed.
     */
    suspend fun mergeHoards(hoardsToMerge: List<Hoard>, providedName : String?, keepOriginal: Boolean) {

        coroutineScope {

            val mergedHoardID: Int
            val mergedHoardName = providedName ?: "Merged Hoards (${hoardsToMerge.size})"

            val mergedCoinPile = IntArray(6) { 0 }
            val mergedGemPile = ArrayList<Gem>()
            val mergedArtPile = ArrayList<ArtObject>()
            val mergedItemPile = ArrayList<MagicItem>()
            val mergedSpellPile= ArrayList<SpellCollection>()

            // Get coinage values first
            hoardsToMerge.forEach { subHoard ->

                mergedCoinPile[0].plus(subHoard.cp)
                mergedCoinPile[1].plus(subHoard.sp)
                mergedCoinPile[2].plus(subHoard.ep)
                mergedCoinPile[3].plus(subHoard.gp)
                mergedCoinPile[4].plus(subHoard.hsp)
                mergedCoinPile[5].plus(subHoard.pp)
            }

            // Add base hoard to database
            mergedHoardID = repository.getHoardIdByRowId(
                repository.addHoard(
                    Hoard(
                        name = mergedHoardName, cp =  mergedCoinPile[0], sp = mergedCoinPile[1],
                        ep =  mergedCoinPile[2], gp = mergedCoinPile[3],
                        hsp =  mergedCoinPile[4], pp = mergedCoinPile[5]
                    )
                ))

            // Compile all unique objects within all targeted hoards
            hoardsToMerge.forEach { subHoard ->

                val uniqueItems = getHoardUniqueItemBundle(subHoard.hoardID)
                    .scrub(mergedHoardID)

                mergedGemPile.addAll(uniqueItems.hoardGems)
                mergedArtPile.addAll(uniqueItems.hoardArt)
                mergedItemPile.addAll(uniqueItems.hoardItems)
                mergedSpellPile.addAll(uniqueItems.hoardSpellCollections)
            }

            // Add compiled items
            val addGemsJob = launch { mergedGemPile.forEach {
                repository.addGem(it) } }
            val addArtJob = launch { mergedArtPile.forEach {
                repository.addArtObject(it) } }
            val addItemsJob = launch { mergedItemPile.forEach {
                repository.addMagicItem(it) } }
            val addSpellsJob = launch { mergedSpellPile.forEach {
                repository.addSpellCollection(it) } }

            joinAll(addGemsJob, addArtJob, addItemsJob, addSpellsJob)

            // Update hoard icon and counts

            suspend fun updateItemDetails() {

                val gemCount = repository.getGemCountOnce(mergedHoardID)
                val artCount = repository.getArtCountOnce(mergedHoardID)
                val itemCount = repository.getMagicItemCountOnce(mergedHoardID)
                val spellCount = repository.getSpellCollectionCountOnce(mergedHoardID)

                val gpTotal = (((mergedCoinPile[0] * 0.01) + (mergedCoinPile[1] * 0.1) +
                        (mergedCoinPile[2] * 0.5) + (mergedCoinPile[3] * 1.0) +
                        (mergedCoinPile[4] * 2.0) + (mergedCoinPile[5] * 5.0))
                        * 100.00.roundToInt() / 100.00 ) +
                        (repository.getGemValueTotalOnce(mergedHoardID)) +
                        (repository.getArtValueTotalOnce(mergedHoardID)) +
                        (repository.getMagicItemValueTotalOnce(mergedHoardID)) +
                        (repository.getSpellCollectionValueTotalOnce(mergedHoardID))

                val updatedHoard = repository.getHoardOnce(mergedHoardID).takeIf { it != null }?.copy(
                    cp = mergedCoinPile[0],
                    sp = mergedCoinPile[1],
                    ep = mergedCoinPile[2],
                    gp = mergedCoinPile[3],
                    hsp = mergedCoinPile[4],
                    pp = mergedCoinPile[5],
                    gpTotal = gpTotal,
                    badge = HoardBadge.MERGED,
                    gemCount = gemCount,
                    artCount = artCount,
                    magicCount = itemCount,
                    spellsCount = spellCount,
                    successful = true)

                if (updatedHoard != null) {

                    val updatedIconString = LootMutator().selectHoardIconByValue(updatedHoard,
                        HoardUniqueItemBundle(mergedGemPile, mergedArtPile,
                            mergedItemPile, mergedSpellPile)
                    )
                    repository.updateHoard(updatedHoard.copy(iconID = updatedIconString))
                }
            }

            updateItemDetails()

            // Record events related to initial merging
            val newHoardEvents = ArrayList<HoardEvent>()

            newHoardEvents.add(
                // Record time and date of hoard merger
                HoardEvent(
                    hoardID = mergedHoardID,
                    timestamp = System.currentTimeMillis(),
                    description = "Hoard \"${mergedHoardName}\" created by merging " +
                            hoardsToMerge.size + " hoards. Originals were " +
                            if (keepOriginal) "retained." else "discarded.",
                    tag = "creation|merge" + if (keepOriginal) "|duplication" else "")
            )

            hoardsToMerge.forEachIndexed { index, subHoard ->
                // Record original compositions of hoards
                newHoardEvents.add(
                    HoardEvent(
                        hoardID = mergedHoardID,
                        timestamp = System.currentTimeMillis(),
                        description = "Original hoard #${index + 1}: ${subHoard.name}\n" +
                                "\tCreated on ${subHoard.creationDate}\n" +
                                "\tTotal monetary value: ${subHoard.gpTotal} gp\n" +
                                "\tCoinage: (${subHoard.cp} cp)||(${subHoard.sp} sp)||" +
                                "(${subHoard.ep} ep)||(${subHoard.gp} gp)||" +
                                "(${subHoard.hsp} hsp)||(${subHoard.pp} pp)\n" +
                                "\tUnique items: [${subHoard.gemCount} gem(s)]||" +
                                "[${subHoard.artCount} art objects(s)]" +
                                "[${subHoard.magicCount} magic item(s)]" +
                                "[${subHoard.spellsCount} spell collection(s)]\n" +
                                "\tApp version: ${subHoard.appVersion}",
                        tag = "creation|merge|verbose" +
                                (if (subHoard.gemCount > 0) "|gemstone" else "") +
                                (if (subHoard.artCount > 0) "|art-object" else "") +
                                (if (subHoard.magicCount > 0) "|magic-item" else "") +
                                (if (subHoard.spellsCount > 0) "|spell-collection" else ""))
                )
            }

            if (keepOriginal) {

                hoardsToMerge.forEach { subHoard ->
                    // Record use in merger, if retained.
                    newHoardEvents.add(
                        HoardEvent(
                            hoardID = subHoard.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Hoard was used in creation of $mergedHoardName.",
                            tag = "merge|duplication"
                        )
                    )
                }

            } else {
                // Otherwise, discard original hoards and all child unique items.
                repository.deleteHoardsAndChildren(hoardsToMerge)
            }

            // Add recorded events to database
            newHoardEvents.forEach { hoardEvent ->
                repository.addHoardEvent(hoardEvent)
            }
        }
    }

    /**
     * Checks to see if provided list of Hoards meets the criteria for merging.
     * @return Pair of actual eligibility (as Boolean) and reasoning (as String).
     */
    fun checkHoardMergeability(hoardsToCheck: List<Hoard>): Pair<Boolean,String> {

        // Pre-check if there are even enough hoards to merge
        if (hoardsToCheck.size < 2) {
                return false to "Must have multiple hoards in order to merge."
        }

        // Tabulate projected totals
        var combinedHoardValue  = 0.0
        val combinedCoinageTotals = IntArray(6) { 0 }
        var combinedGemCount    = 0
        var combinedArtCount    = 0
        var combinedItemCount   = 0
        var combinedSpellCount  = 0

        hoardsToCheck.forEach { checkedHoard ->

            combinedHoardValue += checkedHoard.gpTotal
            combinedCoinageTotals[0].plus(checkedHoard.cp)
            combinedCoinageTotals[1].plus(checkedHoard.sp)
            combinedCoinageTotals[2].plus(checkedHoard.ep)
            combinedCoinageTotals[3].plus(checkedHoard.gp)
            combinedCoinageTotals[4].plus(checkedHoard.hsp)
            combinedCoinageTotals[5].plus(checkedHoard.pp)
            combinedGemCount += checkedHoard.gemCount   // could use repo to confirm count
            combinedArtCount += checkedHoard.artCount
            combinedItemCount += checkedHoard.magicCount
            combinedSpellCount += checkedHoard.spellsCount
        }

        // Return result
        return when {

            combinedHoardValue > MAXIMUM_HOARD_VALUE ->
                false to "Total value of combined hoard (" +
                        ((combinedHoardValue * 100.00).roundToInt() / 100.00).toString() +
                        " gp) cannot exceed maximum single-hoard value of $MAXIMUM_HOARD_VALUE gp."

            (combinedCoinageTotals[0] * 0.01 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[0] * 0.01* 100.00).roundToInt()/100.00).toString() +
                        " gp worth of copper pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            (combinedCoinageTotals[1] * 0.1 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[1] * 0.1 * 100.00).roundToInt()/100.00).toString() +
                        " gp worth of silver pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            (combinedCoinageTotals[2] * 0.5 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[2] * 0.5 * 100.00).roundToInt()/100.00).toString() +
                        " gp worth of electrum pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            (combinedCoinageTotals[3] * 1.0 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[3] * 1.0 * 100.00).roundToInt()/100.00).toString() +
                        " gp worth of gold pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            (combinedCoinageTotals[4] * 2.0 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[4] * 2.0 * 100.00).roundToInt()/100.00).toString() +
                        " gp worth of hard silver pieces. " +
                        "Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            (combinedCoinageTotals[5] * 5.0 * 100.00).roundToInt() / 100.00 > MAXIMUM_COINAGE_AMOUNT ->
                false to "Combined hoard would have " +
                        ((combinedCoinageTotals[5] * 5.0 * 100.00).roundToInt()/100.00).toString() +
                        " gp worth of platinum pieces. Maximum allowed is $MAXIMUM_COINAGE_AMOUNT gp."

            combinedGemCount > MAXIMUM_UNIQUE_QTY ->
                false to "Combined hoard would have $combinedGemCount gems. " +
                        "Maximum allowed is $MAXIMUM_UNIQUE_QTY gems."

            combinedArtCount > MAXIMUM_UNIQUE_QTY ->
                false to "Combined hoard would have $combinedArtCount art objects. " +
                        "Maximum allowed is $MAXIMUM_UNIQUE_QTY gems."

            combinedItemCount > MAXIMUM_UNIQUE_QTY ->
                false to "Combined hoard would have $combinedArtCount magic items. " +
                        "Maximum allowed is $MAXIMUM_UNIQUE_QTY gems."

            combinedSpellCount > MAXIMUM_SPELL_COLLECTION_QTY ->
                false to "Combined hoard would have $combinedSpellCount spell collections. " +
                        "Maximum allowed is $MAXIMUM_SPELL_COLLECTION_QTY regardless of number " +
                        "of spells contained."

            else ->
                true to "Merge permitted."
        }
    }

    /**
     * Makes a copy of all hoards provided and adds them to the list.
     * @return count of hoards copied.
     */
    suspend fun copyHoards(hoardsToCopy: List<Hoard>): Int {

        var hoardsCopied = 0

        coroutineScope {

            hoardsToCopy.forEach { originalHoard ->

                val newHoardID : Int
                val newItemBundle : HoardUniqueItemBundle
                val cloneHoardName = if (originalHoard.name.endsWith(" [Copy]")) {
                    originalHoard.name
                } else { originalHoard.name + " [Copy]" }

                val newClonedHoard = originalHoard.copy(hoardID = 0, badge = HoardBadge.COPIED,
                    name = cloneHoardName, isNew = true)

                // Add hoard to database and get its ID
                newHoardID = repository.getHoardIdByRowId(repository.addHoard(newClonedHoard))

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

                    val gpTotal = (newClonedHoard.let {
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
                            tag = "creation|duplication")
                    ),
                        repository.getHoardEventsOnce(originalHoard.hoardID).map{ originalEvent ->
                            originalEvent.copy(
                                hoardID = newHoardID,
                                eventID = 0,
                                description = "[Copied] " + originalEvent.description,
                                tag = originalEvent.tag +
                                        if (!originalEvent.tag.contains("copied"))
                                            "|copied" else ""
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

                // Add to counter
                hoardsCopied ++
            }
        }

        return hoardsCopied
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