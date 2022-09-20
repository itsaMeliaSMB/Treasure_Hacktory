package com.example.android.treasurefactory.ui

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModel
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModelFactory

class AddHoardEventDialog() : DialogFragment() {

    val safeArgs : AddHoardEventDialogArgs by navArgs()

    private val addHoardEventViewModel: AddHoardEventViewModel by viewModels {
        AddHoardEventViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    //TODO finish implementing
}