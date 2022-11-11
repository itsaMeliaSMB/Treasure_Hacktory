package com.treasurehacktory.viewmodel

import android.widget.Toast
import androidx.lifecycle.*
import com.treasurehacktory.LootMutator
import com.treasurehacktory.MultihoardProcessor
import com.treasurehacktory.model.*
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.floor

class UniqueListViewModel(private val repository: HMRepository) : ViewModel() {

    // region [ Properties ]

    private var isRunningAsync = false

    private val combinedHoardLiveData = MutableLiveData<Pair<Int, UniqueItemType>>()

    val exposedHoardLiveData: LiveData<Hoard?> = Transformations.switchMap(combinedHoardLiveData) { (hoardID, _) ->
        repository.getHoard(hoardID)
    }

    var uniqueItemsLiveData = MutableLiveData<List<ListableItem>>()

    var hoardsLiveData = MutableLiveData<List<Hoard>>(null)

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val textToastHolderLiveData = MutableLiveData<Pair<String,Int>?>(null)

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    // endregion

    fun loadHoardInfo(hoardID: Int, itemType: UniqueItemType){
        combinedHoardLiveData.value = hoardID to itemType
    }

    fun updateUniqueItems(hoardID: Int, itemType: UniqueItemType, sortByValue: Boolean) {

        viewModelScope.launch{

            setRunningAsync(true)

            val listables = when (itemType){
                UniqueItemType.GEM              -> {
                    repository.getGemsOnce(hoardID).map{ gem ->
                        ListableGem(gem.gemID,
                            gem.name,
                            gem.iconID,
                            when {
                                gem.currentGPValue < 0.0        -> ItemFrameFlavor.CURSED
                                gem.currentGPValue >= 1000000.0 -> ItemFrameFlavor.GOLDEN
                                else                            -> ItemFrameFlavor.NORMAL },
                            gem.currentGPValue,
                            gem.getEndIconStr(),
                            gem.getEndLabel(),
                            (gem.name != gem.originalName)
                        )
                    }
                }
                UniqueItemType.ART_OBJECT       -> {
                    repository.getArtObjectsOnce(hoardID).map{ art ->
                        ListableArtObject(art.artID,
                            art.name,
                            art.getIconStr(),
                            when {
                                art.isForgery -> ItemFrameFlavor.CURSED
                                art.valueLevel > 27 -> ItemFrameFlavor.GOLDEN
                                else -> ItemFrameFlavor.NORMAL },
                            art.gpValue,
                            art.getEndIconStr(),
                            art.getEndLabel(),
                            (art.name != art.originalName),
                            art.getBadgeIconStr()
                        )
                    }
                }
                UniqueItemType.MAGIC_ITEM       -> {
                    repository.getMagicItemsOnce(hoardID).map { item ->
                        ListableMagicItem(item.mItemID,
                            item.name,
                            item.iconID,
                            when {
                                item.typeOfItem == MagicItemType.A24 ||
                                        item.name.endsWith(" Choice")-> ItemFrameFlavor.GOLDEN
                                item.isCursed -> ItemFrameFlavor.CURSED
                                else -> ItemFrameFlavor.NORMAL },
                            item.gpValue,
                            item.getEndIconStr(),
                            item.getEndLabel(),
                            (item.name != item.originalName),
                            item.xpValue
                        )
                    }
                }
                UniqueItemType.SPELL_COLLECTION -> {
                    repository.getSpellCollectionsOnce(hoardID).map { spCo ->
                        ListableSpellCollection(
                            spCo.sCollectID,
                            spCo.name,
                            spCo.iconID,
                            if (spCo.curse.isNotBlank()) {
                                ItemFrameFlavor.CURSED
                            } else ItemFrameFlavor.NORMAL,
                            spCo.gpValue,
                            spCo.getEndIconStr(),
                            spCo.getEndLabel(),
                            (spCo.name != spCo.originalName),
                            spCo.xpValue,
                            spCo.discipline
                        )
                    }
                }
            }

            uniqueItemsLiveData.postValue(
                if (sortByValue) {
                    listables.sortedWith (compareByDescending<ListableItem> { it.gpValue }
                        .thenBy { it.id })
                } else { listables }
            )

            setRunningAsync(false)
        }

    }

    fun moveSelectedItems(itemsToMove: List<Triple<Int, UniqueItemType,Int>>, targetHoardID: Int) {

        viewModelScope.launch{

            setRunningAsync(true)

            val expandedItems = itemsToMove.mapNotNull { (itemID, itemType, hoardID) ->

                when (itemType){

                    UniqueItemType.GEM -> {

                        val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                            .takeUnless { it == 0.0 } ?: 5.0

                        repository.getGemOnce(itemID)?.toViewableGem(effortRating)
                    }

                    UniqueItemType.ART_OBJECT -> {

                        val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                            .takeUnless { it == 0.0 } ?: 5.0

                        repository.getArtObjectOnce(itemID)?.toViewableArtObject(effortRating)
                    }

                    UniqueItemType.MAGIC_ITEM -> {

                        repository.getMagicItemOnce(itemID)?.toViewableMagicItem()
                    }

                    UniqueItemType.SPELL_COLLECTION -> {

                        repository.getSpellCollectionOnce(itemID)?.toViewableSpellCollection(repository)
                    }
                }
            }

            val processor = MultihoardProcessor(repository)

            val result = processor.moveItems(targetHoardID,expandedItems)

            setRunningAsync(false)

            textToastHolderLiveData.postValue(result.second to Toast.LENGTH_LONG)
        }
    }

    fun deleteSelectedItems(itemsToDelete: List<ListableItem>, targetHoardID: Int) {

        viewModelScope.launch {

            setRunningAsync(true)

            if (itemsToDelete.isNotEmpty()) {

                val gemList = ArrayList<Gem>()
                val artList = ArrayList<ArtObject>()
                val itemList = ArrayList<MagicItem>()
                val spCoList = ArrayList<SpellCollection>()

                itemsToDelete.forEach {

                    when (it){

                        is ListableGem -> {

                            repository.getGemOnce(it.id).let{ item ->
                                if (item != null){
                                    gemList.add(item)
                                }
                            }
                        }

                        is ListableArtObject -> {

                            repository.getArtObjectOnce(it.id).let{ item ->
                                if (item != null){
                                    artList.add(item)
                                }
                            }
                        }

                        is ListableMagicItem -> {

                            repository.getMagicItemOnce(it.id).let{ item ->
                                if (item != null){
                                    itemList.add(item)
                                }
                            }
                        }

                        is ListableSpellCollection -> {

                            repository.getSpellCollectionOnce(it.id).let{ item ->
                                if (item != null){
                                    spCoList.add(item)
                                }
                            }
                        }
                    }
                }

                val maxSize = 25
                val maxLength = 20

                val description = StringBuilder().apply{
                    append("The following item(s) were deleted from this hoard " +
                            "[id:$targetHoardID]:\n\n")
                }
                val tags = StringBuilder().apply{
                    append("deletion")
                }

                if (gemList.isNotEmpty()) {

                    description.append("< Gemstone(s) & Jewel(s) >\n")
                    tags.append("|gemstone")

                    repository.deleteGems(gemList.toList())

                    gemList.take(maxSize).forEachIndexed { index, item ->

                        val clippedName = item.name.take(maxLength) +
                                if (item.name.length > maxLength) "..." else ""
                        description.append("\t[#${index+1}] $clippedName [id:${item.gemID}]\n")
                    }

                    if (gemList.size > maxSize) {
                        description.append("\t...And ${gemList.size - maxSize} more.\n")
                    }
                }

                if (artList.isNotEmpty()) {

                    description.append("< Art Object(s) >\n")
                    tags.append("|art-object")

                    repository.deleteArtObjects(artList.toList())

                    artList.take(maxSize).forEachIndexed { index, item ->

                        val clippedName = item.name.take(maxLength) +
                                if (item.name.length > maxLength) "..." else ""
                        description.append("\t[#${index+1}] $clippedName [id:${item.artID}]\n")
                    }

                    if (artList.size > maxSize) {
                        description.append("\t...And ${artList.size - maxSize} more.\n")
                    }
                }

                if (itemList.isNotEmpty()) {

                    description.append("< Magic Item(s) >\n")
                    tags.append("|magic-item")

                    repository.deleteMagicItems(itemList.toList())

                    itemList.take(maxSize).forEachIndexed { index, item ->

                        val clippedName = item.name.take(maxLength) +
                                if (item.name.length > maxLength) "..." else ""
                        description.append("\t[#${index+1}] $clippedName [id:${item.mItemID}]\n")
                    }

                    if (itemList.size > maxSize) {
                        description.append("\t...And ${itemList.size - maxSize} more.\n")
                    }
                }

                if (spCoList.isNotEmpty()) {

                    description.append("< Spell Collection(s) >\n")
                    tags.append("|spell-collection")

                    repository.deleteSpellCollections(spCoList.toList())

                    spCoList.take(maxSize).forEachIndexed { index, item ->

                        val clippedName = item.name.take(maxLength) +
                                if (item.name.length > maxLength) "..." else ""
                        description.append("\t[#${index+1}] $clippedName [id:${item.sCollectID}]\n")
                    }

                    if (spCoList.size > maxSize) {
                        description.append("\t...And ${spCoList.size - maxSize} more.\n")
                    }
                }

                val deletionEvent = HoardEvent(
                    timestamp = System.currentTimeMillis(),
                    hoardID = targetHoardID,
                    description = description.toString(),
                    tag = tags.toString()
                )

                val updatedHoard = LootMutator.auditHoard(targetHoardID,repository)

                if (updatedHoard != null) {
                    repository.updateHoards(listOf(updatedHoard))
                }

                repository.addHoardEvent(deletionEvent)
            }

            setRunningAsync(false)
        }
    }

    fun sellSelectedItems(itemsToSell: List<ListableItem>, parentHoard: Hoard) {

        fun CoinType.getMaxCapacity(): Int = (MAXIMUM_COINAGE_AMOUNT / this.gpValue).toInt()

        viewModelScope.launch{

            setRunningAsync(true)

            val gemsToSell  = ArrayList<Gem>()
            val artToSell   = ArrayList<ArtObject>()
            val mItemsToSell= ArrayList<MagicItem>()
            val spCosToSell = ArrayList<SpellCollection>()

            val maxSize = 25
            val maxLength = 30

            val removalDesc = StringBuilder().apply{
                append("The following item(s) from this hoard " +
                        "[id:${parentHoard.hoardID}] were liquidated:\n\n") }
            val removalTags = StringBuilder().apply{
                append("sale|deletion")
            }

            itemsToSell.forEach{ item ->
                when (item) {
                    is ListableGem -> {
                        item.toGem(repository).let{ expandedItem ->
                            if (expandedItem != null) {
                                gemsToSell.add(expandedItem)
                            }
                        }
                    }
                    is ListableArtObject -> {
                        item.toArtObject(repository).let{ expandedItem ->
                            if (expandedItem != null) {
                                artToSell.add(expandedItem)
                            }
                        }
                    }
                    is ListableMagicItem -> {
                        item.toMagicItem(repository).let{ expandedItem ->
                            if (expandedItem != null) {
                                mItemsToSell.add(expandedItem)
                            }
                        }
                    }
                    is ListableSpellCollection -> {
                        item.toSpellCollection(repository).let{ expandedItem ->
                            if (expandedItem != null) {
                                spCosToSell.add(expandedItem)
                            }
                        }
                    }
                }
            }

            var totalSaleRemaining = gemsToSell.sumOf { it.currentGPValue } +
                    artToSell.sumOf { it.gpValue } + mItemsToSell.sumOf { it.gpValue } +
                    spCosToSell.sumOf { it.gpValue }
            val addedCoins = mutableMapOf<CoinType,Int>()
            val remainingCapacity = mutableMapOf(
                CoinType.CP to ((CoinType.CP).getMaxCapacity() - parentHoard.cp),
                CoinType.SP to ((CoinType.SP).getMaxCapacity() - parentHoard.sp),
                CoinType.EP to ((CoinType.EP).getMaxCapacity() - parentHoard.ep),
                CoinType.GP to ((CoinType.GP).getMaxCapacity() - parentHoard.gp),
                CoinType.HSP to ((CoinType.HSP).getMaxCapacity() - parentHoard.hsp),
                CoinType.PP to ((CoinType.PP).getMaxCapacity() - parentHoard.pp)
            )

            // Greedily make sale in as few coins as hoard as room for
            enumValues<CoinType>().reversed().forEach { coinType ->

                if (totalSaleRemaining > 0.00) {

                    val mostCoinsPossible = coinType.getMaxCapacity()
                    val remainingTypeCapacity = mostCoinsPossible.coerceAtMost(remainingCapacity[coinType]!!)

                    val coinsPutTowardsTotal = floor(totalSaleRemaining / coinType.gpValue).toInt()
                        .coerceAtMost(remainingTypeCapacity)

                    addedCoins[coinType] = coinsPutTowardsTotal

                    totalSaleRemaining -= coinsPutTowardsTotal * coinType.gpValue
                } else {
                    addedCoins[coinType] = 0
                }
            }

            // Delete sold items
            if (gemsToSell.isNotEmpty()) {

                removalDesc.append("< Gemstone(s) & Jewel(s) >\n")
                removalTags.append("|gemstone")

                repository.deleteGems(gemsToSell.toList())

                gemsToSell.take(maxSize).forEachIndexed { index, item ->

                    val clippedName = item.name.take(maxLength) +
                            if (item.name.length > maxLength) "..." else ""
                    removalDesc.append("\t[#${index+1}] $clippedName [id:${item.gemID}]\n")
                    removalDesc.append("\t(Worth " +
                            DecimalFormat("#,##0.0#")
                                .format(item.currentGPValue)
                                .removeSuffix(".0") + " gp when sold)\n")
                }

                if (gemsToSell.size > maxSize) {
                    removalDesc.append("\t...And ${gemsToSell.size - maxSize} more.\n")
                }
            }
            if (artToSell.isNotEmpty()) {
                removalDesc.append("< Art Object(s) >\n")
                removalTags.append("|art-object")

                repository.deleteArtObjects(artToSell.toList())

                artToSell.take(maxSize).forEachIndexed { index, item ->

                    val clippedName = item.name.take(maxLength) +
                            if (item.name.length > maxLength) "..." else ""
                    removalDesc.append("\t[#${index+1}] $clippedName [id:${item.artID}]\n")
                    removalDesc.append("\t(Worth " +
                            DecimalFormat("#,##0.0#")
                                .format(item.gpValue)
                                .removeSuffix(".0") + " gp when sold)\n")
                }

                if (artToSell.size > maxSize) {
                    removalDesc.append("\t...And ${artToSell.size - maxSize} more.\n")
                }
            }
            if (mItemsToSell.isNotEmpty()) {
                removalDesc.append("< Magic Item(s) >\n")
                removalTags.append("|magic-item")

                repository.deleteMagicItems(mItemsToSell.toList())

                mItemsToSell.take(maxSize).forEachIndexed { index, item ->

                    val clippedName = item.name.take(maxLength) +
                            if (item.name.length > maxLength) "..." else ""
                    removalDesc.append("\t[#${index+1}] $clippedName [id:${item.mItemID}]\n")
                    removalDesc.append("\t(Worth " +
                            DecimalFormat("#,##0.0#")
                                .format(item.gpValue)
                                .removeSuffix(".0") + " gp when sold)\n")
                }

                if (mItemsToSell.size > maxSize) {
                    removalDesc.append("\t...And ${mItemsToSell.size - maxSize} more.\n")
                }
            }
            if (spCosToSell.isNotEmpty()) {
                removalDesc.append("< Spell Collection(s) >\n")
                removalTags.append("|spell-collection")

                repository.deleteSpellCollections(spCosToSell.toList())

                spCosToSell.take(maxSize).forEachIndexed { index, item ->

                    val clippedName = item.name.take(maxLength) +
                            if (item.name.length > maxLength) "..." else ""
                    removalDesc.append("\t[#${index+1}] $clippedName [id:${item.sCollectID}]\n")
                    removalDesc.append("\t(Worth " +
                            DecimalFormat("#,##0.0#")
                                .format(item.gpValue)
                                .removeSuffix(".0") + " gp when sold)\n")
                }

                if (spCosToSell.size > maxSize) {
                    removalDesc.append("\t...And ${spCosToSell.size - maxSize} more.\n")
                }
            }

            // Update coinage on hoard before audit
            repository.updateHoard(
                parentHoard.copy(
                    cp = parentHoard.cp + addedCoins[CoinType.CP]!!,
                    sp = parentHoard.sp + addedCoins[CoinType.SP]!!,
                    ep = parentHoard.ep + addedCoins[CoinType.EP]!!,
                    gp = parentHoard.gp + addedCoins[CoinType.GP]!!,
                    hsp= parentHoard.hsp + addedCoins[CoinType.HSP]!!,
                    pp = parentHoard.pp + addedCoins[CoinType.PP]!!)
            )

            // Audit and update hoard with new item totals
            LootMutator.auditHoard(parentHoard.hoardID,repository).also{
                if (it != null) {
                    repository.updateHoard(it)
                }
            }

            // Log sale as two events
            val removalEvent = HoardEvent(
                hoardID = parentHoard.hoardID,
                timestamp = System.currentTimeMillis(),
                description = removalDesc.toString(),
                tag = removalTags.toString()
            )

            val coinageDesc = StringBuilder()

            coinageDesc.append("Coins added for sale of ${itemsToSell.size} item(s):\n")

            addedCoins.toSortedMap().forEach { (coinType, coinQty) ->
                if (coinQty > 0) {
                    coinageDesc.append("\t+" +  (NumberFormat.getNumberInstance().format(coinQty)) +
                            " ${coinType.name.lowercase()} (" +
                            DecimalFormat("#,##0.0#")
                                .format(coinQty * coinType.gpValue)
                                .removeSuffix(".0") + " gp)\n")
                     }
            }

            if (totalSaleRemaining > 0.001) {
                coinageDesc.append("\t(" + DecimalFormat("#,##0.0#")
                    .format(totalSaleRemaining).removeSuffix(".0") +
                        " gp left on the table)\n")
            }

            coinageDesc.append("\nTotal value of " +
                    DecimalFormat("#,##0.0#")
                        .format((gemsToSell.sumOf { it.currentGPValue } +
                                artToSell.sumOf { it.gpValue } + mItemsToSell.sumOf { it.gpValue } +
                                spCosToSell.sumOf { it.gpValue })).removeSuffix(".0") +
                    " gp")

            val coinageEvent = HoardEvent(
                hoardID = parentHoard.hoardID,
                timestamp = System.currentTimeMillis(),
                description = coinageDesc.toString(),
                tag = "sale|coinage"
            )

            repository.addHoardEvent(listOf(removalEvent,coinageEvent))

            setRunningAsync(false)
        }
    }

    fun fetchHoardsForDialog(excludedHoardID: Int) {

        viewModelScope.launch{
            hoardsLiveData.postValue(
                repository.getHoardsOnce().filter{ it.hoardID != excludedHoardID }) }
    }

    // region [ Extension functions ]

    private fun Gem.getEndIconStr() : String {
        return when (this.type) {
            5   -> "clipart_gem1_vector_icon"
            6   -> "clipart_gem2_vector_icon"
            7   -> "clipart_gem3_vector_icon"
            8   -> "clipart_gem4_vector_icon"
            9   -> "clipart_gem5_vector_icon"
            10  -> "clipart_gem_vector_icon"
            else-> "badge_hoard_broken"
        }
    }

    private fun Gem.getEndLabel() : String {
        return when (this.type) {
            5   -> "Ornamental"
            6   -> "Semiprecious"
            7   -> "Fancy"
            8   -> "Precious"
            9   -> "Gemstone"
            10  -> "Jewel"
            else-> "???"
        }
    }

    private fun ArtObject.getBadgeIconStr() : String {
        return when (this.subject) {
            -2  -> "badge_art_abstract"
            -1  -> "badge_art_monster"
            0   -> "badge_art_human"
            1   -> "badge_art_nature"
            2   -> "badge_art_historic"
            3   -> "badge_art_religion"
            4   -> "badge_art_nobility"
            5   -> "badge_art_royalty"
            else-> "badge_hoard_broken"
        }
    }

    private fun ArtObject.getIconStr() : String {
        return when (this.artType) {
            0   -> "artwork_paper"
            1   -> "artwork_fabric"
            2   -> "artwork_furnishing"
            3   -> "artwork_painting"
            4   -> "artwork_wood"
            5   -> "artwork_ceramic"
            6   -> "artwork_glass"
            7   -> "artwork_stone"
            8   -> "artwork_metal"
            9   -> "artwork_magical"
            else-> "badge_hoard_broken"
        }
    }

    private fun ArtObject.getEndIconStr() : String {
        return when (this.artType) {
            0   -> "clipart_origami_vector_icon"
            1   -> "clipart_fabric_vector_icon"
            2   -> "clipart_bed_vector_icon"
            3   -> "clipart_painting_specific_vector_icon"
            4   -> "clipart_chisel_vector_icon"
            5   -> "clipart_amphora_vector_icon"
            6   -> "clipart_glass_ball_vector_icon"
            7   -> "clipart_stone_tools_vector_icon"
            8   -> "clipart_anvil_vector_icon"
            9   -> "clipart_fairy_vector_icon"
            else-> "badge_hoard_broken"
        }
    }

    private fun ArtObject.getEndLabel() : String {
        return when (this.artType) {
            0   -> "Paper Art"
            1   -> "Fabric Art"
            2   -> "Furnishing"
            3   -> "Painting"
            4   -> "Woodwork"
            5   -> "Ceramic"
            6   -> "Glass Art"
            7   -> "Stonework"
            8   -> "Metalwork"
            9   -> "Magical"
            else-> "badge_hoard_broken"
        }
    }

    private fun MagicItem.getEndIconStr() : String {
        return when (this.typeOfItem){
            MagicItemType.A2 -> "clipart_potion_vector_icon"
            MagicItemType.A3 -> "clipart_scroll_vector_icon"
            MagicItemType.A4 -> "clipart_ring_vector_icon"
            MagicItemType.A5 -> "clipart_rod_vector_icon"
            MagicItemType.A6 -> "clipart_staff_vector_icon"
            MagicItemType.A7 -> "clipart_wand_vector_icon"
            MagicItemType.A8 -> "clipart_book_vector_icon"
            MagicItemType.A9 -> "clipart_jewelry_vector_icon"
            MagicItemType.A10 -> "clipart_robe_vector_icon"
            MagicItemType.A11 -> "clipart_boot_vector_icon"
            MagicItemType.A12 -> "clipart_belt_hat_vector_icon"
            MagicItemType.A13 -> "clipart_bag_vector_icon"
            MagicItemType.A14 -> "clipart_dust_vector_icon"
            MagicItemType.A15 -> "clipart_toolbox_vector_icon"
            MagicItemType.A16 -> "clipart_lyre_vector_icon"
            MagicItemType.A17 -> "clipart_crystal_ball_vector_icon"
            MagicItemType.A18 -> "clipart_armor_vector_icon"
            MagicItemType.A20 -> "clipart_cape_armor_vector_icon"
            MagicItemType.A21 -> "clipart_axe_sword_vector_icon"
            MagicItemType.A23 -> "clipart_winged_sword_vector_icon"
            MagicItemType.A24 -> "clipart_artifact_crown_vector_icon"
            MagicItemType.Map -> "clipart_map_vector_icon"
            MagicItemType.Mundane -> "clipart_crate_vector_icon"
        }
    }

    private fun MagicItem.getEndLabel() : String {
        return when (this.typeOfItem){
            MagicItemType.A2 -> "Potion A2"
            MagicItemType.A3 -> "Scroll A3"
            MagicItemType.A4 -> "Ring A4"
            MagicItemType.A5 -> "Rod A5"
            MagicItemType.A6 -> "Staff A6"
            MagicItemType.A7 -> "Wand A7"
            MagicItemType.A8 -> "Book A8"
            MagicItemType.A9 -> "Jewelry A9"
            MagicItemType.A10 -> "Robe, etc. A10"
            MagicItemType.A11 -> "Boots,etc. A11"
            MagicItemType.A12 -> "Hat, etc. A12"
            MagicItemType.A13 -> "Container A13"
            MagicItemType.A14 -> "Dust, etc. A14"
            MagicItemType.A15 -> "Tools A15"
            MagicItemType.A16 -> "Musical A16"
            MagicItemType.A17 -> "Odd Stuff A17"
            MagicItemType.A18 -> "Armor A18"
            MagicItemType.A20 -> "Sp.Armor A20"
            MagicItemType.A21 -> "Weapon A21"
            MagicItemType.A23 -> "Sp.Weap. A23"
            MagicItemType.A24 -> "Artifact A24"
            MagicItemType.Map -> "Treasure Map"
            MagicItemType.Mundane -> "Mundane"
        }
    }

    private fun SpellCollection.getEndIconStr() : String {
        return when (this.type) {
            SpCoType.SCROLL -> "clipart_scroll_vector_icon"
            SpCoType.BOOK -> "clipart_spellbook_vector_icon"
            SpCoType.ALLOTMENT -> "clipart_empowered_vector_icon"
            SpCoType.RING -> "clipart_ring_vector_icon"
            SpCoType.OTHER -> "badge_hoard_magic"
        }
    }

    private fun SpellCollection.getEndLabel() : String {
        val spellCount = this.spells.size
        return "$spellCount Spell" + (if (spellCount != 1) "s" else "")
    }

    // endregion
}

class UniqueListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniqueListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniqueListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}