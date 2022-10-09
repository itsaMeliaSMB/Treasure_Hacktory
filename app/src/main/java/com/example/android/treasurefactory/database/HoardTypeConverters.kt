package com.example.android.treasurefactory.database

import androidx.room.TypeConverter
import com.example.android.treasurefactory.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class HoardTypeConverters {

    //region ( List<String> )

    @TypeConverter
    fun fromStringList(list: List<String?>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toStringList(jsonList: String?) : List<String?>? {

        val typeToken = object : TypeToken<List<String>>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
    // endregion

    // region ( Date )

    @TypeConverter
    fun fromDate(date: Date?) : Long? = date?.time

    @TypeConverter
    fun toDate(msecSinceEpoch: Long?) : Date? = msecSinceEpoch?.let { Date(it) }
    // endregion

    // region ( SpellEntry )

    @TypeConverter
    fun fromSpellEntry(entry: SpellEntry?) : String? = Gson().toJson(entry,SpellEntry::class.java)

    @TypeConverter
    fun toSpellEntry(jsonEntry: String?) : SpellEntry? = Gson().fromJson(jsonEntry,SpellEntry::class.java)

    @TypeConverter
    fun fromSpellEntryList(list: List<SpellEntry?>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toSpellEntryList(jsonList: String?) : List<SpellEntry?>? {

        if (jsonList == null) return emptyList()

        val typeToken = object : TypeToken<List<SpellEntry?>?>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
    // endregion

    // region [ String -> <*> Lists/Maps ]

    @TypeConverter
    fun fromStringListPair(nList: List<Pair<String?,List<String?>?>?>) : String? = Gson().toJson(nList)

    @TypeConverter
    fun toStringListPair(jsonNList: String?) : List<Pair<String?,List<String?>?>?> {

        val typeToken = object : TypeToken<List<Pair<String?,List<String?>?>?>>() {}.type

        return Gson().fromJson(jsonNList,typeToken)
    }

    @TypeConverter
    fun fromStringBoolMap(map: Map<String,Boolean>?) : String? = Gson().toJson(map)

    @TypeConverter
    fun toStringBoolMap(jsonMap: String?) : Map<String,Boolean>? {

        val typeToken = object : TypeToken<Map<String,Boolean>>() {}.type

        return Gson().fromJson(jsonMap,typeToken)
    }

    @TypeConverter
    fun fromStringDoubleMap(map: Map<String,Double>?) : String? = Gson().toJson(map)

    @TypeConverter
    fun toStringDoubleMap(jsonMap: String?) : Map<String,Double>? {

        val typeToken = object : TypeToken<Map<String,Double>>() {}.type

        return Gson().fromJson(jsonMap,typeToken)
    }

    @TypeConverter
    fun fromListOfStringDoublePairs(list: List<Pair<String,Double>>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toListOfStringDoublePairs(jsonList: String?) : List<Pair<String,Double>?>? {

        val typeToken = object : TypeToken<List<Pair<String,Double>>>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
    // endregion

    // region [ Enums ]

    // region ( SpellSchool )

    @TypeConverter
    fun fromSpellSchoolList(list: List<SpellSchool>) : String {

        return list.joinToString("|") { it.ordinal.toString() }
    }

    @TypeConverter
    fun toSpellSchoolList(string: String) : List<SpellSchool> {

        return string.split("|").mapNotNull { ordinalString ->
            enumValues<SpellSchool>().getOrNull(ordinalString.toIntOrNull() ?: -1) }
    }

    @TypeConverter
    fun fromSpellSchool(school: SpellSchool): Int = school.ordinal

    @TypeConverter
    fun toSpellSchool(ordinal: Int) : SpellSchool = enumValues<SpellSchool>()[ordinal]
    // endregion

    // region ( ClericalSphere )

    @TypeConverter
    fun fromClericalSphereList(list: List<ClericalSphere>) : String {

        return list.joinToString("|") { it.ordinal.toString() }
    }

    @TypeConverter
    fun toClericalSphereList(string: String) : List<ClericalSphere> {

        return string.split("|").mapNotNull { ordinalString ->
            enumValues<ClericalSphere>().getOrNull(ordinalString.toIntOrNull() ?: -1) }
    }

    @TypeConverter
    fun fromClericalSphere(sphere: ClericalSphere): Int = sphere.ordinal

    @TypeConverter
    fun toClericalSphere(ordinal: Int) : ClericalSphere = enumValues<ClericalSphere>()[ordinal]
    // endregion

    // region ( ArcaneSpecialist )

    @TypeConverter
    fun fromArcaneSpecialistList(list: List<ArcaneSpecialist>) : String {

        return list.joinToString("|") { it.ordinal.toString() }
    }

    @TypeConverter
    fun toArcaneSpecialistList(string: String) : List<ArcaneSpecialist> {

        return string.split("|").mapNotNull { ordinalString ->
            enumValues<ArcaneSpecialist>().getOrNull(ordinalString.toIntOrNull() ?: -1) }
    }

    @TypeConverter
    fun fromArcaneSpecialist(specialty: ArcaneSpecialist): Int = specialty.ordinal

    @TypeConverter
    fun toArcaneSpecialist(ordinal: Int) : ArcaneSpecialist = enumValues<ArcaneSpecialist>()[ordinal]
    // endregion

    // region ( SpCoType )

    @TypeConverter
    fun fromSpCoType(spCoType: SpCoType) : Int = spCoType.ordinal

    @TypeConverter
    fun toSpCoType(ordinal: Int) : SpCoType = enumValues<SpCoType>()[ordinal]
    //endregion

    // region ( MagicItemType )

    @TypeConverter
    fun fromMagicItemType(itemType: MagicItemType) : Int = itemType.ordinal

    @TypeConverter
    fun toMagicItemType(ordinal: Int) : MagicItemType = enumValues<MagicItemType>()[ordinal]
    // endregion

    // region ( HoardBadge )

    @TypeConverter
    fun fromHoardBadge(hoardBadge: HoardBadge) : Int = hoardBadge.ordinal

    @TypeConverter
    fun toHoardBadge(ordinal: Int) : HoardBadge = enumValues<HoardBadge>()[ordinal]
    // endregion
    // endregion

    @TypeConverter
    fun fromBoolean(bool: Boolean) : String? = Gson().toJson(bool)

    @TypeConverter
    fun toBoolean(jsonBool: String?): Boolean = Gson().fromJson(jsonBool,Boolean::class.java)

    // region TODO <Remove after Spell refactor>

    @TypeConverter
    fun fromSpell(spell: Spell?) : String? = Gson().toJson(spell,Spell::class.java)

    @TypeConverter
    fun toSpell(jsonSpell: String?) : Spell? = Gson().fromJson(jsonSpell,Spell::class.java)

    @TypeConverter
    fun fromSpellList(list: List<Spell?>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toSpellList(jsonList: String?) : List<Spell?>? {

        if (jsonList == null) return emptyList()

        val typeToken = object : TypeToken<List<Spell?>?>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
    // endregion

    // region TODO < Remove after GemEvaluation refactor >

    @TypeConverter
    fun fromListOfLongStringPairs(list: List<Pair<Long,String>>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toListOfLongStringPairs(jsonList: String?) : List<Pair<Long,String>?>? {

        val typeToken = object : TypeToken<List<Pair<Long,String>>>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
    // endregion
}