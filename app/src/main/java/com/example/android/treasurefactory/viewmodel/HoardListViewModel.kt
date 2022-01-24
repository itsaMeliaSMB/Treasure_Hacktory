package com.example.android.treasurefactory.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android.treasurefactory.repository.HMRepository

class HoardListViewModel() : ViewModel() {

    private val treasureRepository = HMRepository.get()

    val hoardListLiveData = treasureRepository.getHoards()
}