package com.example.android.treasurefactory.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.android.treasurefactory.HMGem
import com.example.android.treasurefactory.HMHoard

/**
 * Singleton database for entire app.
 * TODO: add other entities and Daos
 */
@Database(entities = [HMGemTemplate::class], version = 1)
abstract class TreasureDatabase : RoomDatabase() {

    abstract fun hmGemDao(): HMGemDao

    /* Note for self:
    We do NOT need the singleton here, since this will be instantiated as part of the HM Repository
    class, which will have the singleton with context as necessary. Err on the side of BNR. */

}

//region [ Data Access Objects ]

@Dao
interface HMHoardDao{

    @Query("SELECT * FROM hackmaster_hoard_table")
    fun getHoards(): LiveData<List<HMHoard>>

    @Query("SELECT * FROM hackmaster_hoard_table WHERE hoardID=(:id)")
    fun getHoard(id: Int): LiveData<HMHoard?>
}

@Dao
interface HMGemDao {

    @Query("SELECT * FROM hackmaster_gem_reference WHERE type=(:type) ORDER BY ordinal")
    fun getGemTableByType(type: String): LiveData<List<HMGemTemplate>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE hoardID=(:parentID)")
    fun getGems(parentID: Int): MutableLiveData<List<HMGem>>

    @Query("SELECT * FROM hackmaster_gem_table WHERE gemID=(:id)")
    fun getGem(id: Int): MutableLiveData<HMGem?>
}

//endregion