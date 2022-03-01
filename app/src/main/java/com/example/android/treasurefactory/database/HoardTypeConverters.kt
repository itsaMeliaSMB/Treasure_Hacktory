package com.example.android.treasurefactory.database

import androidx.room.TypeConverter
import com.example.android.treasurefactory.model.Spell
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class HoardTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String?>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toStringList(jsonList: String?) : List<String?>? {

        val typeToken = object : TypeToken<List<String>>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }

    @TypeConverter
    fun fromDate(date: Date?) : Long? = date?.time

    @TypeConverter
    fun toDate(msecSinceEpoch: Long?) : Date? = msecSinceEpoch?.let { Date(it) }

    @TypeConverter
    fun fromNestedStringList(nList: List<List<String?>?>?) : String? = Gson().toJson(nList)

    @TypeConverter
    fun toNestedStringList(jsonNList: String?) : List<List<String?>?>? {

        val typeToken = object : TypeToken<List<List<String>>>() {}.type

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

    @TypeConverter
    fun fromBoolean(bool: Boolean) : String? = Gson().toJson(bool)

    @TypeConverter
    fun toBoolean(jsonBool: String?): Boolean = Gson().fromJson(jsonBool,Boolean::class.java)

    @TypeConverter
    fun fromListOfLongStringPairs(list: List<Pair<Long,String>>?) : String? = Gson().toJson(list)

    @TypeConverter
    fun toListOfLongStringPairs(jsonList: String?) : List<Pair<Long,String>?>? {

        val typeToken = object : TypeToken<List<Pair<Long,String>>>() {}.type

        return Gson().fromJson(jsonList,typeToken)
    }
}