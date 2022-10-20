package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.*
import com.example.android.treasurefactory.model.*
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UniqueListViewModel(private val repository: HMRepository) : ViewModel() {

    // region [ Properties ]

    private var isRunningAsync = false

    private val combinedHoardLiveData = MutableLiveData<Pair<Int,UniqueItemType>>()

    val exposedHoardLiveData: LiveData<Hoard?> = Transformations.switchMap(combinedHoardLiveData) { (hoardID, _) ->
        repository.getHoard(hoardID)
    }

    var uniqueItemsLiveData = MutableLiveData<List<ListableItem>>()

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

    fun saveUniqueItems(itemsToUpdate: List<ListableItem>, parentHoard: Hoard) {
        //TODO determine what unique items are being held
        //TODO use that information to convert list to room entity list
        //TODO call repository function for updating list of items.
    }

    fun updateUniqueItems(hoardID: Int, itemType: UniqueItemType) {

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
                            gem.getEndLabel()
                        )
                    }
                }
                UniqueItemType.ART_OBJECT       -> {
                    repository.getArtObjectsOnce(hoardID).map{ art ->
                        ListableArtObject(art.artID,
                            art.name,
                            art.getIconStr(),
                            when {
                                art.valueLevel > 27 -> ItemFrameFlavor.GOLDEN
                                // if forgeries are added, indicate with CURSED
                                else -> ItemFrameFlavor.NORMAL },
                            art.gpValue,
                            art.getEndIconStr(),
                            art.getEndLabel(),
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
                                item.typeOfItem == MagicItemType.A24 -> ItemFrameFlavor.GOLDEN
                                item.isCursed -> ItemFrameFlavor.CURSED
                                else -> ItemFrameFlavor.NORMAL },
                            item.gpValue,
                            item.getEndIconStr(),
                            item.getEndLabel(),
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
                            if (spCo.curse.isNotEmpty()) {
                                ItemFrameFlavor.CURSED
                            } else ItemFrameFlavor.NORMAL,
                            spCo.gpValue,
                            spCo.getEndIconStr(),
                            spCo.getEndLabel(),
                            spCo.xpValue,
                            spCo.discipline
                        )
                    }
                }
            }

            uniqueItemsLiveData.postValue(listables)

            delay(500L)

            setRunningAsync(false)
        }

    }

    fun deleteSelectedItems(itemsToDelete: List<ListableItem>) {
        //TODO Not yet implemented
    }

    fun sellSelectedItems(itemsToSell: List<ListableItem>, parentHoard: Hoard) {
        //TODO Not yet implemented
    }

    fun duplicateSelectedItems(itemsToCopy: List<ListableItem>) {
        //TODO Not yet implemented
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
        return "$spellCount Spell" + (if (spellCount != 0) "s" else "")
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