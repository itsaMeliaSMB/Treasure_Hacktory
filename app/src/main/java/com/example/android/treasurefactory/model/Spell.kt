package com.example.android.treasurefactory.model

import androidx.room.Ignore
import com.example.android.treasurefactory.capitalized

enum class SpellSchool(val longResName: String, val shortResName: String){
    ABJURATION("school_abj_l","school_abj_s"),
    ALTERATION("school_alt_l","school_alt_s"),
    CONJURATION("school_con_l","school_con_s"),
    DIVINATION("school_div_l","school_div_s"),
    ENCHANTMENT("school_enc_l","school_enc_s"),
    EVOCATION("school_evo_l","school_evo_s"),
    ILLUSION("school_ill_l","school_ill_s"),
    NECROMANCY("school_nec_l","school_nec_s")
}

enum class ClericalSphere(val resName: String){
    AIR("sphere_air"),
    ANIMAL("sphere_animal"),
    CHARM("sphere_charm"),
    COMBAT("sphere_combat"),
    CREATION("sphere_creation"),
    DEVOTIONAL("sphere_devotional"),
    DIVINATION("sphere_divination"),
    EARTH("sphere_earth"),
    FIRE("sphere_fire"),
    HEALING("sphere_healing"),
    HURTING("sphere_hurting"),
    NECROMANTIC("sphere_necromantic"),
    PLANT("sphere_plant"),
    SUMMONING("sphere_summoning"),
    SUN("sphere_sun"),
    TRAVELLER("sphere_traveller"),
    WARDING("sphere_warding"),
    WATER("sphere_water"),
    WEATHER("sphere_weather")
}

enum class ArcaneSpecialist(val resString: String){
    ABJURER("specialist_abjurer"),
    ABJURER_DS("specialist_ds_abjurer"),
    BATTLE_MAGE("specialist_battle_mage"),
    BLOOD_MAGE("specialist_blood_mage"),
    CONJURER("specialist_conjurer"),
    CONJURER_DS("specialist_ds_conjurer"),
    DIVINER("specialist_diviner"),
    DIVIDER_DS("specialist_ds_diviner"),
    ELEMENTALIST_FIRE("specialist_fire_elementalist"),
    ELEMENTALIST_WATER("specialist_water_elementalist"),
    ELEMENTALIST_AIR("specialist_air_elementalist"),
    ELEMENTALIST_EARTH("specialist_earth_elementalist"),
    ENCHANTER("specialist_enchanter"),
    ENCHANTER_DS("specialist_ds_enchanter"),
    ILLUSIONIST("specialist_illusionist"),
    ILLUSIONIST_DS("specialist_ds_illusionist"),
    INVOKER("specialist_invoker"),
    INVOKER_DS("specialist_ds_invoker"),
    NECROMANCER("specialist_necromancer"),
    NECROMANCER_DS("specialist_ds_necromancer"),
    PAINTED_MAGE("specialist_painter_mage"),
    TRANSMUTER("specialist_transmuter"),
    TRANSMUTER_DS("specialist_ds_transmuter"),
    WILD_MAGE("specialist_wild_mage"),
    ANTI_MAGE("specialist_anti_mage"),
    GUARDIAN("specialist_guardian"),
    CONSTRUCTOR("specialist_constructor"),
    METAMORPHER("specialist_metamorpher"),
    TRANSPORTER("specialist_transporter"),
    CONJURER_SP("specialist_sp_conjurer"),
    POWER_SPEAKER("specialist_power_speaker"),
    SUMMONER("specialist_summoner"),
    DETECTIVE("specialist_detective"),
    SEER("specialist_seer"),
    ITEMIST("specialist_itemist"),
    PUPPETEER("specialist_puppeteer"),
    HYPNOTIST("specialist_hypnotist"),
    SHADOW_WEAVER("specialist_shadow_weaver"),
    DEMOLITIONIST("specialist_demolitionist"),
    ICER("specialist_icer"),
    PYROTECHNICIAN("specialist_pyrotechnician"),
    SNIPER("specialist_sniper"),
    ANIMATOR("specialist_animator"),
    EXTERMINATOR("specialist_exterminator")
}



data class Spell(val templateID: Int = 0, val name: String = "<undefined spell>", val type: SpCoDiscipline,
                 val spellLevel: Int= 0,
                 val sourceText: String = "<undefined source>", val sourcePage: Int = 0,
                 val schools: List<SpellSchool>, val spheres: List<ClericalSphere>, val subclass: String,
                 val restrictions : List<ArcaneSpecialist>, val notes: List<String>,
                 var extraPages: Int = 0 //TODO remove from signiture
) {

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