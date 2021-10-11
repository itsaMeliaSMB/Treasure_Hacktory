package com.example.android.treasurefactory.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.android.treasurefactory.HMHoard

@Dao
interface HMHoardDao{

    @Query("SELECT * FROM hackmaster_hoard_table")
    fun getHoards(): LiveData<List<HMHoard>>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<HMHoard?>
}