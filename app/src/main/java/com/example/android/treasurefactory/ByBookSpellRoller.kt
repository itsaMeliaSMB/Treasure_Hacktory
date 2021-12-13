package com.example.android.treasurefactory

class ArcaneSpellRoller() {

    enum class SpellCategory(){
        OFFENSIVE,
        DEFENSIVE,
        MISCELLANEOUS
    }

    fun getInitialArcaneSpells(offensive: Int = 1,
                               defensive: Int = 1,
                               misc: Int = 1) {

        val spellIDList: ArrayList<Int>
        val gmChoiceMap: MutableMap<String,Int>
    }

    fun getInitialArcaneSpell(category: SpellCategory, preRoll: Int) {}

    fun getLevelUpArcaneSpell(level: Int, discipline: String?){}
}

class DivineSpellRoller() {

    fun getChosenOneSpells() {}
}