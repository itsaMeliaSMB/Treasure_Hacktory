package com.example.android.treasurefactory.database

import androidx.room.TypeConverter
import com.example.android.treasurefactory.HMHoard

@Database(entities = [ HMHoard::class ], version=1) //TODO revisit version
@TypeConverters(HoardTypeConverters::class)
abstract class HoardDatabase : RoomDatabase {
}