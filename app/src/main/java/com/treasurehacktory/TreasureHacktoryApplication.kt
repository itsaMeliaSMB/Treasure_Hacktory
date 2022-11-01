package com.treasurehacktory

import android.app.Application
import com.treasurehacktory.database.TreasureDatabase
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TreasureHacktoryApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { TreasureDatabase.getDatabase(this,applicationScope) }
    val repository by lazy {
        HMRepository(database.hoardDao(),
            database.gemDao(),
            database.artDao(),
            database.magicItemDao(),
            database.spellCollectionDao())
    }
}