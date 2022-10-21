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

class ListableSpellCollection(
    override val id: Int, override val name: String,
    override val iconStr: String,
    override val iFrameFlavor: ItemFrameFlavor,
    override val gpValue: Double, override val endIconStr: String,
    override val endLabel: String, val xpValue: Int, val discipline: SpCoDiscipline
) : ListableItem()
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
interface Viewable {
    val itemID: Int
    val hoardID: Int
    val name: String
    val subtitle: String
    val creationTime: Long
    val iconStr: String
    val iFrameFlavor: ItemFrameFlavor
    val source: String
    val sourcePage: Int
    val gpValue: Double
    val xpValue: Int
    val itemType: UniqueItemType
    val details: List<Pair<String,List<DetailEntry>>>
}

sealed class ViewableItem : Viewable

// region ( Subclasses )

class ViewableGem(
    override val itemID: Int,
    override val hoardID: Int,
    override val name: String,
    override val subtitle: String,
    override val creationTime: Long,
    override val iconStr: String,
    override val iFrameFlavor: ItemFrameFlavor,
    override val source: String,
    override val sourcePage: Int,
    override val gpValue: Double,
    override val xpValue: Int,
    override val itemType: UniqueItemType,
    override val details: List<Pair<String,List<DetailEntry>>>,
    val gemType: Int,
    val gemQuality: Int,
    val gemOpacity: Int,
    val gemDesc: String,
) : ViewableItem()

class ViewableArtObject(
    override val itemID: Int,
    override val hoardID: Int,
    override val name: String,
    override val subtitle: String,
    override val creationTime: Long,
    override val iconStr: String,
    override val iFrameFlavor: ItemFrameFlavor,
    override val source: String,
    override val sourcePage: Int,
    override val gpValue: Double,
    override val xpValue: Int,
    override val itemType: UniqueItemType,
    override val details: List<Pair<String, List<DetailEntry>>>,
    val artType: Int,
    val artRenown: Int,
    val artSize: Int,
    val artCondition: Int,
    val artMaterials: Int,
    val artQuality: Int,
    val artAge: Int,
    val artSubject: Int,
    var artValueLevel: Int,
    val artIsForgery: Boolean
) : ViewableItem()

class ViewableMagicItem(
    override val itemID: Int,
    override val hoardID: Int,
    override val name: String,
    override val subtitle: String,
    override val creationTime: Long,
    override val iconStr: String,
    override val iFrameFlavor: ItemFrameFlavor,
    override val source: String,
    override val sourcePage: Int,
    override val gpValue: Double,
    override val xpValue: Int,
    override val itemType: UniqueItemType,
    override val details: List<Pair<String,List<DetailEntry>>>,
    val mgcTemplateID: Int,
    val mgcItemType: MagicItemType,
    val mgcClassUsability: Map<String,Boolean>,
    val mgcIsCursed: Boolean,
    val mgcAlignment: String,
    val mgcOriginalNotes: List<Pair<String,List<String>>>
) : ViewableItem()

class ViewableSpellCollection(
    override val itemID: Int,
    override val hoardID: Int,
    override val name: String,
    override val subtitle: String,
    override val creationTime: Long,
    override val iconStr: String,
    override val iFrameFlavor: ItemFrameFlavor,
    override val source: String,
    override val sourcePage: Int,
    override val gpValue: Double,
    override val xpValue: Int,
    override val itemType: UniqueItemType,
    override val details: List<Pair<String, List<DetailEntry>>>,
    val spCoType: SpCoType,
    val spCoDiscipline: SpCoDiscipline,
    val spCoProperties: List<Pair<String, Double>>,
    val spCoSpells: List<SpellEntry>,
    val spCoCurse: String
) : ViewableItem() {

    fun convertBack() : SpellCollection = SpellCollection(
        sCollectID = itemID,
        hoardID = hoardID,
        creationTime= creationTime,
        iconID = iconStr,
        name = name,
        type = spCoType,
        discipline = spCoDiscipline,
        properties = spCoProperties,
        gpValue = gpValue,
        xpValue = xpValue,
        spells = spCoSpells,
        curse = spCoCurse
    )
}

// endregion

// endregion

// region [ DetailEntry ]

sealed class DetailEntry

class PlainTextEntry(val message: String) : DetailEntry()
class LabelledQualityEntry(val caption: String, val value: String) : DetailEntry()
class SimpleSpellEntry(
    val spellID: Int,
    val name: String,
    val level: Int,
    val discipline: SpCoDiscipline,
    val schools: List<SpellSchool>,
    val subclass: String,
    val sourceString: String,
    val isUsed: Boolean,
    val spellsPos: Int
) : DetailEntry()

// endregion

enum class UniqueItemType{
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

