package com.treasurehacktory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.treasurehacktory.R
import com.treasurehacktory.TreasureHacktoryApplication
import com.treasurehacktory.databinding.DialogAddEventBinding
import com.treasurehacktory.model.HoardEvent
import com.treasurehacktory.viewmodel.AddHoardEventViewModel
import com.treasurehacktory.viewmodel.AddHoardEventViewModelFactory

@Keep
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

                        fun String.reduceNewLines() : String {

                            val maxNewLines = 20
                            val newLineReplacement = "—"

                            val splitString = this.split("\n")

                            if (splitString.size > maxNewLines - 1) {

                                val recombinedString = StringBuilder()

                                splitString.forEachIndexed { index, substring ->
                                    // If not the last or only entry
                                    if (splitString.size > 1 && index < splitString.lastIndex) {
                                        // If within the maximum quota of new lines,
                                        if (index < maxNewLines - 1) {
                                            recombinedString.append(substring + "\n")
                                        } else {
                                            recombinedString.append(substring + newLineReplacement)
                                        }
                                    } else {
                                        // Otherwise, add substring by itself to the end.
                                        recombinedString.append(substring)
                                    }
                                }

                                return recombinedString.toString()

                            } else { return this }
                        }

                        val descString = binding.addEventDescEdit.text.toString()

                        if (descString.isNotBlank()) {

                            // Collate tags to include
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
                                if (this.userTagMap.isChecked) {
                                    tagBuilder.append("|map")
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
                                if (this.userTagReroll.isChecked) {
                                    tagBuilder.append("|reroll")
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
                                description = descString.reduceNewLines(),
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