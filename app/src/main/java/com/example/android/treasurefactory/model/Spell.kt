package com.example.android.treasurefactory.model

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.repository.HMRepository
import kotlin.random.Random

enum class SpellSchool(val resName: String){
    ABJURATION("spell_school_abjuration"),
    ALTERATION("spell_school_alteration"),
    CONJURATION("spell_school_conjuration"),
    DIVINATION("spell_school_divination"),
    ENCHANTMENT("spell_school_enchantment"),
    EVOCATION("spell_school_evocation"),
    ILLUSION("spell_school_illusion"),
    NECROMANCY("spell_school_necromancy");

    @Ignore
    fun getShortName(context: Context): String {

        return try {

            context.getString(
                context.resources.getIdentifier(
                    "${resName}_s", "string", context.packageName
                )
            )
        } catch (e: Resources.NotFoundException) {
            "???"
        }
    }


    @Ignore
    fun getLongName(context: Context): String {

        return try {

            context.getString(
                context.resources.getIdentifier(
                    "${resName}_l", "string", context.packageName
                )
            )
        } catch (e: Resources.NotFoundException) {
            "<School not found>"
        }
    }

    @Ignore
    @DrawableRes
    fun getDrawableResID(context: Context): Int {

        return try {

            context.resources.getIdentifier(resName,"drawable",context.packageName)
        } catch (e: Resources.NotFoundException) {
            R.drawable.rounded_rectangle_border_bg
        }
    }
}

enum class ClericalSphere(val resName: String){
    AIR("spell_sphere_air"),
    ANIMAL("spell_sphere_animal"),
    CHARM("spell_sphere_charm"),
    COMBAT("spell_sphere_combat"),
    CREATION("spell_sphere_creation"),
    DEVOTIONAL("spell_sphere_devotional"),
    DIVINATION("spell_sphere_divination"),
    EARTH("spell_sphere_earth"),
    FIRE("spell_sphere_fire"),
    HEALING("spell_sphere_healing"),
    HURTING("spell_sphere_hurting"),
    NECROMANTIC("spell_sphere_necromantic"),
    PLANT("spell_sphere_plant"),
    SUMMONING("spell_sphere_summoning"),
    SUN("spell_sphere_sun"),
    TRAVELER("spell_sphere_traveler"),
    WARDING("spell_sphere_warding"),
    WATER("spell_sphere_water"),
    WEATHER("spell_sphere_weather");

    @Ignore
    fun getNameString(context: Context): String {

        return try {

            context.getString(
                context.resources.getIdentifier(
                    resName, "string", context.packageName
                )
            )
        } catch (e: Resources.NotFoundException) {
            "String not found."
        }
    }

    @Ignore
    @DrawableRes
    fun getDrawableResID(context: Context): Int {

        return try {

            context.resources.getIdentifier(resName,"drawable",context.packageName)
        } catch (e: Resources.NotFoundException) {
            R.drawable.rounded_rectangle_border_bg
        }
    }
}

enum class ArcaneSpecialist(val resName: String){
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
    EXTERMINATOR("specialist_exterminator");

    @Ignore
    fun getFullName(context: Context): String {

        return try {

            context.getString(
                context.resources.getIdentifier(
                    resName, "string", context.packageName
                )
            )
        } catch (e: Resources.NotFoundException) {
            "String not found."
        }
    }
}

/** Category of sources for [magic items][MagicItem] and [spells][Spell].*/
enum class ReferenceType {
    CORE,               // Published within the PHB or GMG for HM4e
    SPLATBOOK,          // Published in any official, non-adventure supplement for HM4e
    HACKJOURNAL,        // Published in an official HackJournal article during HM4e support
    PUBLISHED_MODULE,   // Published in an official adventure module for HM4e
    BEYOND_HM4E,        // Intended for material published outside of official Kenzer & Co HM4e products
    RESEARCHED,         // Intended for new spells researched/items created in-game by player characters
    OTHER_HOMEBREW      // Intended as catch-all for any other spells
}

/**
 * Full description of a magic spell referenced by a [SpellEntry] within a [SpellCollection].
 *
 * @param reverse If true on an [arcane][SpCoDiscipline.ARCANE] spell, marks that spell as "Reversible". Otherwise, marks this spell as the "Reversed" form of another spell.
 */
@Entity(tableName = "hackmaster_spell_table")
data class Spell(
    @PrimaryKey @ColumnInfo(name = "spell_id") val spellID: Int = 0,
    val name: String = "<undefined spell>",
    val reverse: Boolean = false,
    val refType: ReferenceType,
    val type: SpCoDiscipline,
    val spellLevel: Int = 0,
    val sourceText: String = "<undefined source>",
    val sourcePage: Int = 0,
    val schools: List<SpellSchool>,
    val spheres: List<ClericalSphere>,
    val subclass: String,
    val restrictions: List<ArcaneSpecialist> = emptyList<ArcaneSpecialist>(),
    val note: String,
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

    @Ignore
    fun toSimpleSpellEntry(isUsed: Boolean = false) : SimpleSpellEntry {
        return SimpleSpellEntry(spellID, name, spellLevel, type, schools, subclass,
            "$sourceText, pg $sourcePage", isUsed)
    }

    companion object {


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

/**
 * Converts this [Spell] into a [SpellEntry] reference for storage in a [SpellCollection].
 *
 * @param rollPages If true, rolls for how many extra pages this copy of the spell would take up in
 * a spell book. Otherwise, returns 0 for [extraPages][SpellEntry.extraPages].
 * @param standardBook Whether or not the target SpellCollection is a standard spellbook. Affects
 * how many extra pages this copy of the spell takes up.
 */
@Ignore
fun Spell.toSpellEntry(rollPages: Boolean = false, standardBook: Boolean = true) : SpellEntry {

    val pageRoll = if (rollPages) {
            if (spellLevel == 0) {
                Random.nextInt(1,7) - (if (standardBook) 1 else 0)
            } else {
                Random.nextInt(1,4)
            }
        } else 0

    return SpellEntry(this.spellID, this.spellLevel, pageRoll)
}

/**
 * Fetches the [Spell] referenced by this [SpellEntry], or returns null if spellID is invalid.
 *
 * @param repository Required instance of [HMRepository] to look up
 */
@Ignore
suspend fun SpellEntry.toSpellOrNull(repository: HMRepository) : Spell? {

    return repository.getSpell(this.spellID)
}