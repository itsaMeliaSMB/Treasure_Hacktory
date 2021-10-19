package com.example.android.treasurefactory.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.android.treasurefactory.HMGem
import com.example.android.treasurefactory.database.HMGemTemplate
import com.example.android.treasurefactory.database.TreasureDatabase

private const val DATABASE_NAME = "treasure-database"

/**
* Repository for all HM classes in this app.
*/
class HMRepository private constructor(context: Context) {

    private val database : TreasureDatabase = Room.databaseBuilder(
        context.applicationContext,
        TreasureDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val hmGemDao = database.hmGemDao()

    // region [ Getter functions ]

    fun getGemTableByType(type: String) : LiveData<List<HMGemTemplate>> = hmGemDao.getGemTableByType(type)

    fun getGemsByHoardID(id: Int) : MutableLiveData<List<HMGem>> = hmGemDao.getGems(id)

    // endregion

    //region [ Setter functions ]

    //

    //endregion

    companion object {
        private var INSTANCE: HMRepository? = null

        fun initialize(context: Context) { if (INSTANCE == null) INSTANCE = HMRepository(context) }

        fun get(): HMRepository = INSTANCE ?: throw IllegalStateException("HMRepository must be initialized")
    }
}