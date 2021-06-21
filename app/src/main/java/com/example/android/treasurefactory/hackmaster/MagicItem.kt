package com.example.android.treasurefactory.hackmaster

data class MagicItem(
    val typeOfItem: MagicItemType,val name: String,val sourceText: String,val sourcePage: Int,
    val xpValue: Int,val gpValue: Double,val notes: List<List<String>> = emptyList(),
    val extraNotes: List<List<String>> = emptyList()) {

    enum class MagicItemType(val description: String) {

        A1("DEFAULT TYPE"),
        A2("Potions and Oils"),
        A3("Scrolls"),
        A4("Rings"),
        A5("Rods"),
        A6("Staves"),
        A7("Wands"),
        A8("Misc: Books, etc."),
        A9("Misc: Jewels, etc."),
        A10("Misc: Cloaks, etc."),
        A11("Misc: Boots, etc."),
        A12("Misc: Girdles, etc."),
        A13("Misc: Bags, etc."),
        A14("Misc: Dusts, etc."),
        A15("Misc: Household"),
        A16("Misc: Musical"),
        A17("Misc: Weird"),
        A18("Armor and Shields"),
        A21("Weapons"),
        A24("Artifacts/Relics");
    }
}