package com.example.android.treasurefactory.model

sealed class ListableItem() {}

class ListableGem() : ListableItem()
class ListableArtObject(val badgeStr: String) : ListableItem()
class ListableMagicItem(val xpValue: Int, val noteCount : Int) : ListableItem()
class ListableSpellCollection(val xpValue: Int, val spellCount : Int) : ListableItem()

sealed class ViewableItem() {}

class ViewableGem() : ViewableItem()
class ViewableArtObject() : ViewableItem()
class ViewableMagicItem() : ViewableItem()
class ViewableSpellCollection : ViewableItem()

// Extension mapping functions TODO