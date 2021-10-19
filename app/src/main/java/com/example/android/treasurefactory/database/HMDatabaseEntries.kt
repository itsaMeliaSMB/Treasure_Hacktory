package com.example.android.treasurefactory.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hackmaster_gem_reference")
data class HMGemTemplate(@PrimaryKey(autoGenerate = true)
                        @ColumnInfo(name="ref_id") val refId: Int,
                         val type: String,
                         val name: String,
                         val ordinal: Int,
                         val opacity: Int,
                         val description: String,
                         @ColumnInfo(name ="icon_id") val iconID : String //TODO add default ID for gem drawable
)
