package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogAddEventBinding
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModel
import com.example.android.treasurefactory.viewmodel.AddHoardEventViewModelFactory

class AddHoardEventDialog() : DialogFragment() {

    private var targetHoardID = 0

    private val safeArgs : AddHoardEventDialogArgs by navArgs()

    private var _binding: DialogAddEventBinding? = null
    private val binding get() = _binding!!

    private val addHoardEventViewModel: AddHoardEventViewModel by viewModels {
        AddHoardEventViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    // region [ Overridden functions ]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        targetHoardID  = safeArgs.activeHoardID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogAddEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addHoardEventToolbar.apply{
            inflateMenu(R.menu.add_hoard_event_menu)
            setNavigationIcon(R.drawable.clipart_close_vector_icon)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->

                when (item.itemId){
                    R.id.action_add_event   -> {

                        val descString = binding.addEventDescEdit.text.toString()

                        if (descString.isNotBlank()) {

                            // Collate tags
                            val tagBuilder = StringBuilder()
                            binding.run {
                                if (this.userTagArtObject.isChecked) {
                                    tagBuilder.append("|art-object")
                                }
                                if (this.userTagCoinage.isChecked) {
                                    tagBuilder.append("|coinage")
                                }
                                if (this.userTagCreation.isChecked) {
                                    tagBuilder.append("|creation")
                                }
                                if (this.userTagDeletion.isChecked) {
                                    tagBuilder.append("|deletion")
                                }
                                if (this.userTagDuplication.isChecked) {
                                    tagBuilder.append("|duplication")
                                }
                                if (this.userTagGemstone.isChecked) {
                                    tagBuilder.append("|gemstone")
                                }
                                if (this.userTagHomebrew.isChecked) {
                                    tagBuilder.append("|homebrew")
                                }
                                if (this.userTagMagicItem.isChecked) {
                                    tagBuilder.append("|magic-item")
                                }
                                if (this.userTagMerge.isChecked) {
                                    tagBuilder.append("|merge")
                                }
                                if (this.userTagModification.isChecked) {
                                    tagBuilder.append("|modification")
                                }
                                if (this.userTagNote.isChecked) {
                                    tagBuilder.append("|note")
                                }
                                if (this.userTagSale.isChecked) {
                                    tagBuilder.append("|sale")
                                }
                                if (this.userTagSpellCollection.isChecked) {
                                    tagBuilder.append("|spell-collection")
                                }
                                if (this.userTagVerbose.isChecked) {
                                    tagBuilder.append("|verbose")
                                }
                            }

                            val userEvent = HoardEvent(
                                hoardID = targetHoardID,
                                timestamp = System.currentTimeMillis(),
                                description = descString,
                                tag = "user$tagBuilder"
                            )

                            addHoardEventViewModel.saveEvent(userEvent)

                            findNavController().popBackStack()

                        } else {

                            Toast.makeText(context,getString(R.string.user_event_empty_error),Toast.LENGTH_SHORT)
                        }

                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

}