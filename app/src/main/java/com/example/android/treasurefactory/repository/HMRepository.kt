package com.example.android.treasurefactory.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.treasurefactory.database.HMGemTemplate
import com.example.android.treasurefactory.database.TreasureDatabase
import com.example.android.treasurefactory.model.HMGem
import com.example.android.treasurefactory.model.HMHoard

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

    private val hmHoardDao = database.hmHoardDao()
    private val hmGemDao = database.hmGemDao()
    //TODO add other Daos as they are made

    // region [ Getter functions ]

        // region [[ Hoard Functions ]]

    fun getHoards(): LiveData<List<HMHoard>> = hmHoardDao.getHoards()

        // endregion

        // region [[ Gem functions ]]

    fun getGemTableByType(type: String) : LiveData<List<HMGemTemplate>> = hmGemDao.getGemTableByType(type)

    fun getGemsByHoardID(id: Int) : LiveData<List<HMGem>> = hmGemDao.getGems(id)

    fun getGemByID(id: Int): LiveData<HMGem?> = hmGemDao.getGem(id)

        // endregion

    // endregion

    //region [ Setter functions ]

        // region [[ Gem functions ]]

        // endregion

    //endregion

    companion object {
        private var INSTANCE: HMRepository? = null

        fun initialize(context: Context) { if (INSTANCE == null) INSTANCE = HMRepository(context) }

        fun get(): HMRepository = INSTANCE ?: throw IllegalStateException("HMRepository must be initialized")
    }
}