package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogAddEventBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModel
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModelFactory

class AddHoardEventDialog() : DialogFragment() {

    private lateinit var activeHoard: Hoard

    val safeArgs : AddHoardEventDialogArgs by navArgs()

    private var _binding: DialogAddEventBinding? = null
    private val binding get() = _binding!!

    private val addHoardEventViewModel: AddHoardEventViewModel by viewModels {
        AddHoardEventViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    // region [ Overridden functions ]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = safeArgs.activeHoardID

        addHoardEventViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogAddEventBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observers
        addHoardEventViewModel.apply {
            hoardLiveData.observe(viewLifecycleOwner) { hoard ->

                hoard?.let {

                    activeHoard = hoard
                }
            }
        }
    }

    // endregion

    // region [ Helper functions ]



    // endregion

    //TODO finish implementing
}