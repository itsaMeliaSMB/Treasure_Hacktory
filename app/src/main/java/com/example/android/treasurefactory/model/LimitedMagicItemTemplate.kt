package com.example.android.treasurefactory.model

import androidx.room.ColumnInfo

data class LimitedMagicItemTemplate(@ColumnInfo(name="ref_id")
                                    val primaryKey: Int,
                                    @ColumnInfo(name="wt")
                                    val itemWeight: Int) {
}