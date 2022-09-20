package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogBottomHoardInfoEditBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardEditViewModel
import com.example.android.treasurefactory.viewmodel.HoardEditViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HoardEditBottomDialog() : BottomSheetDialogFragment() {

    // TODO https://medium.com/androiddevelopers/navigation-component-dialog-destinations-bfeb8b022759

    private lateinit var activeHoard: Hoard

    val safeArgs : HoardEditBottomDialogArgs by navArgs()

    private val hoardEditViewModel: HoardEditViewModel by viewModels {
        HoardEditViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    private var _binding: DialogBottomHoardInfoEditBinding? = null
    private val binding get() = _binding!!

    //TODO make holders for drawable resources for most valuable items' icons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = safeArgs.activeHoardID

        hoardEditViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = DialogBottomHoardInfoEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observers
        hoardEditViewModel.apply {

            hoardLiveData.observe(viewLifecycleOwner) { hoard ->

                //TODO Also consider, instead of using a listener, getting this information one-time.

                hoard?.let {

                    activeHoard = hoard

                    //TODO Get hoard icon IDs here

                }
            }
        }
    }

    //TODO Left off here. Finish implementing this HoardEdit fragment as a BottomSheetDialog and
    // navigation to it (pending hoard badge implementation). Afterwards, Finish implementing
    // AddHoardEventDialog. Following that, do the Gem, Hoard, and Spell/SpellCollection refactor.
}