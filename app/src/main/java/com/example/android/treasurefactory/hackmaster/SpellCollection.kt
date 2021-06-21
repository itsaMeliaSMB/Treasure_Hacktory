package com.example.android.treasurefactory.hackmaster

class SpellCollection(val name: String = "<Spell Scroll>", val isArcane: Boolean = true,
                      val type: String = "scroll",
                      var spells: MutableList<Spell> = mutableListOf<Spell>()){


}