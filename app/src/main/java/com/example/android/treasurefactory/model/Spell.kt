package com.example.android.treasurefactory.model

data class Spell(val templateID: Int = 0, val name: String = "<undefined spell>", val type: String, val spellLevel: Int= 0,
                 val sourceText: String = "<undefined source>", val sourcePage: Int = 0,
                 val schools: List<String>, val spheres: List<String>, val subclass: String,
                 val restrictions : List<String>, val note: String)