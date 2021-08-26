package com.example.android.treasurefactory.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.treasurefactory.HMHoard

@Database(entities = [ HMHoard::class ], version=1, exportSchema = false) //TODO add exportSchema
@TypeConverters(HoardTypeConverters::class)
abstract class HoardDatabase : RoomDatabase() {
}