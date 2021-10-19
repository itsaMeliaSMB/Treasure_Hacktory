package com.example.android.treasurefactory

import android.app.Application
import com.example.android.treasurefactory.repository.HMRepository

//TODO:Rename entire project to "Treasure Hacktory"

class TreasureHacktoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        HMRepository.initialize(this)
    }
}