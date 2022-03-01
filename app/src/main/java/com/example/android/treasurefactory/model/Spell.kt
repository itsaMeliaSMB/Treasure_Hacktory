package com.example.android.treasurefactory.model

import androidx.room.Ignore

data class Spell(val templateID: Int = 0, val name: String = "<undefined spell>", val type: String,
                 val spellLevel: Int= 0,
                 val sourceText: String = "<undefined source>", val sourcePage: Int = 0,
                 val schools: List<String>, val spheres: List<String>, val subclass: String,
                 val restrictions : List<String>, val note: String) {

    @Ignore
    fun getRestrictionsString():String {

        val runningList = StringBuilder()

        if (restrictions.isNotEmpty()) {

            restrictions.forEach {
                runningList.append("• $it\n")
            }
        }

        return runningList.toString().trimEnd()
    }
}