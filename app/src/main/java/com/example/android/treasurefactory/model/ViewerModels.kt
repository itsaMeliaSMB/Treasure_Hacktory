package com.example.android.treasurefactory.model

import com.example.android.treasurefactory.repository.HMRepository

// region [ Listable ]

interface Listable{
    val id: Int
    val name: String
    val iconStr: String
    val iFrameFlavor: ItemFrameFlavor
    val gpValue: Double
    val endIconStr: String
    val endLabel: String
}

sealed class ListableItem() : Listable

// region ( Subclasses )
class ListableGem(override val id: Int, override val name: String,
                  override val iconStr: String, override val iFrameFlavor: ItemFrameFlavor,
                  override val gpValue: Double, override val endIconStr: String,
                  override val endLabel: String) : ListableItem()

class ListableArtObject(override val id: Int, override val name: String,
                        override val iconStr: String, override val iFrameFlavor: ItemFrameFlavor,
                        override val gpValue: Double, override val endIconStr: String,
                        override val endLabel: String, val badgeStr: String) : ListableItem()

class ListableMagicItem(override val id: Int, override val name: String,
                        override val iconStr: String, override val iFrameFlavor: ItemFrameFlavor,
                        override val gpValue: Double, override val endIconStr: String,
                        override val endLabel: String, val xpValue: Int) : ListableItem()

class ListableSpellCollection(override val id: Int, override val name: String,
                              override val iconStr: String,
                              override val iFrameFlavor: ItemFrameFlavor,
                              override val gpValue: Double, override val endIconStr: String,
                              override val endLabel: String, val xpValue: Int) : ListableItem()
// endregion

// region ( Extension functions )

suspend fun ListableGem.toGem(repository: HMRepository): Gem? {
    return repository.getGemOnce(this.id)
}

suspend fun ListableArtObject.toArtObject(repository: HMRepository): ArtObject? {
    return repository.getArtObjectOnce(this.id)
}

suspend fun ListableMagicItem.toMagicItem(repository: HMRepository): MagicItem? {
    return repository.getMagicItemOnce(this.id)
}

suspend fun ListableSpellCollection.toSpellCollection(repository: HMRepository): SpellCollection? {
    return repository.getSpellCollectionOnce(this.id)
}

//endregion

// endregion

// region [ Viewable ]

sealed class Viewable() {}

// region ( Subclasses )

class ViewableGem() : Viewable()
class ViewableArtObject() : Viewable()
class ViewableMagicItem() : Viewable()
class ViewableSpellCollection : Viewable()

// endregion

// region ( Extension functions )

//TODO

// endregion

// endregion

enum class UniqueItemType(){
    GEM,
    ART_OBJECT,
    MAGIC_ITEM,
    SPELL_COLLECTION
}

enum class ItemFrameFlavor {
    NORMAL,
    CURSED,
    GOLDEN
}