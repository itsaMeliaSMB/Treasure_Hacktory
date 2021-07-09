package com.example.android.treasurefactory.database

import androidx.room.TypeConverter
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
}