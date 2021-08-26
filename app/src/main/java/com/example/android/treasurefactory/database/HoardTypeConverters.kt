package com.example.android.treasurefactory.database

import androidx.room.TypeConverter
import com.example.android.treasurefactory.HMCoinPile
import com.google.gson.Gson
import java.util.*

class HoardTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let{
            Date(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: String?): UUID? = UUID.fromString(uuid)

    @TypeConverter
    fun toUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun fromCoinage(coinage: HMCoinPile): String? = Gson().toJson(coinage)

    @TypeConverter
    fun toCoinage(coinage: String): HMCoinPile = Gson().fromJson(coinage,HMCoinPile::class.java)
}