package com.example.android.treasurefactory.model

import androidx.room.Ignore
import com.example.android.treasurefactory.capitalized

data class Spell(val templateID: Int = 0, val name: String = "<undefined spell>", val type: SpCoDiscipline,
                 val spellLevel: Int= 0,
                 val sourceText: String = "<undefined source>", val sourcePage: Int = 0,
                 val schools: List<String>, val spheres: List<String>, val subclass: String,
                 val restrictions : List<String>, val notes: List<String>, var extraPages: Int = 0) {

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

    companion object {

        @Ignore
        val validSchools = listOf("Div","Ill","Abj","Enc","Con","Nec","Alt","Evo")

        @Ignore
        fun checkIfValidSchool(input: String): Boolean = validSchools.contains(input.lowercase().capitalized())

        @Ignore
        fun schoolAbbrevToFull(_input: String): String {

            val input = _input.trim().lowercase().capitalized()

            val abbreviationMap = mapOf(
                "Div" to "Divination",
                "Ill" to "Illusion/Phantasm",
                "Abj" to "Abjuration",
                "Enc" to "Enchantment/Charm",
                "Con" to "Conjuration/Summoning",
                "Nec" to "Necromancy",
                "Alt" to "Alteration",
                "Evo" to "Invocation/Evocation"
            )

            return abbreviationMap.getOrDefault(input,input)
        }

        @Ignore
        val mageSpecialistTags = listOf("Abjurer","DS_Abjurer","Battle_Mage","Blood_Mage",
            "Conjurer","DS_Conjurer","Diviner","DS_Diviner",
            "Fire_Elementalist","Water_Elementalist","Air_Elementalist",
            "Earth_Elementalist","Enchanter","DS_Enchanter",
            "Illusionist","DS_Illusionist","Invoker","DS_Invoker",
            "Necromancer","DS_Necromancy","Painted_Mage","Transmuter",
            "DS_Transmuter","Wild_Mage","Anti-Mage","Guardian",
            "Constructor","Metamorpher","Transporter","SP_Conjurer",
            "Power_Speaker","Summoner","Detective","Seer","Itemist",
            "Puppeteer","Hypnotist","Shadow_Weaver","Demolitionist",
            "Icer", "Pyrotechnician","Sniper","Animator","Exterminator"
        )

        @Ignore
        fun specialistTagToString(_input: String) : String {

            val input = _input.trim().lowercase()

            val tagMap = mapOf(
                "abjurer" to "Abjurer",
                "ds_abjurer" to "Double specialist; Abjurer",
                "battle_mage" to "Battle Mage",
                "blood_mage" to "Blood Mage",
                "conjurer" to "Conjurer",
                "ds_conjurer" to "Double specialist; Conjurer",
                "diviner" to "Diviner",
                "ds_diviner" to "Double specialist; Diviner",
                "fire_elementalist" to "Fire Elementalist",
                "water_elementalist" to "Water Elementalist",
                "air_elementalist" to "Air Elementalist",
                "earth_elementalist" to "Earth Elementalist",
                "enchanter" to "Enchanter",
                "ds_enchanter" to "Double specialist; Enchanter",
                "illusionist" to "Illusionist",
                "ds_illusionist" to "Double specialist; Illusionist",
                "invoker" to "Invoker",
                "ds_invoker" to "Double specialist; Invoker",
                "necromancer" to "Necromancer",
                "ds_necromancy" to "Double specialist; Necromancer",
                "painted_mage" to "Painted Mage",
                "transmuter" to "Transmuter",
                "ds_transmuter" to "Double specialist; Transmuter",
                "wild_mage" to "Wild Mage",
                "anti-mage" to "Anti-Mage [Abjuration]",
                "guardian" to "Guardian [Abjuration]",
                "constructor" to "Constructor [Alteration]",
                "metamorpher" to "Metamorpher [Alteration]",
                "transporter" to "Transporter [Alteration]",
                "sp_conjurer" to "Conjurer [Conjuration/Summoning]",
                "power_speaker" to "Power Speaker [Conjuration/Summoning]",
                "summoner" to "Summoner [Conjuration/Summoning]",
                "detective" to "Detective [Divination]",
                "seer" to "Seer [Divination]",
                "itemist" to "Itemist [Enchantment/Charm]",
                "puppeteer" to "Puppeteer [Enchantment/Charm]",
                "hypnotist" to "Hypnotist [Illusion]",
                "shadow_weaver" to "Shadow Weaver [Illusion]",
                "demolitionist" to "Demolitionist [Invocation/Evocation]",
                "icer" to "Icer [Invocation/Evocation]",
                "pyrotechnician" to "Pyrotechnician [Invocation/Evocation]",
                "sniper" to "Sniper [Invocation/Evocation]",
                "animator" to "Animator [Necromancy]",
                "exterminator" to "Exterminator [Necromancy]"
            )

            return tagMap.getOrDefault(input,"[Undocumented specialist]")
        }
    }
}