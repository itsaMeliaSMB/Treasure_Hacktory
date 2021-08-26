package com.example.android.treasurefactory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class HMHoardGeneratorFragment : Fragment() {

    private lateinit var hoardTitleField: EditText
    private lateinit var letterRadioButton: RadioButton
    private lateinit var specificRadioButton: RadioButton
    private lateinit var viewAnimator: ViewAnimator
    private lateinit var generateButton: Button
    private lateinit var byLetterExpandableList: ExpandableListView

    private val letterGroupList: List<String> = listOf( //TODO: populate using String resources instead of hardcoding
        "Lair Treasures",
        "Individual and Small Lair Treasures")
    private val letterChildList = HMLetterObject.letterArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.hackmaster_treasure_gen_fragment, container, false)

        // Wire widgets to fragment
        hoardTitleField = view.findViewById(R.id.hackmaster_gen_name_entry) as EditText
        letterRadioButton = view.findViewById(R.id.hackmaster_gen_method_letter) as RadioButton
        specificRadioButton = view.findViewById(R.id.hackmaster_gen_method_specific) as RadioButton
        viewAnimator = view.findViewById(R.id.hackmaster_gen_view_animator) as ViewAnimator

        byLetterExpandableList = view.findViewById(R.id.hackmaster_gen_letter_exlist) as ExpandableListView

        generateButton = view.findViewById(R.id.hackmaster_gen_generate_button) as Button

        //Set adapter for expandable list view
        byLetterExpandableList.setAdapter(HMLetterAdapter(requireContext(),byLetterExpandableList,letterGroupList,letterChildList))

        // Return inflated view
        return view
    }

    override fun onStart() {

        super.onStart()

        // Listener for hoard title EditText
        val hoardTitleWatcher = object : TextWatcher {

            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Left intentionally blank (pg 163)
            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                // hoard.HoardName = sequence.toString() TODO may not be necessary since not editing active hoard. Revisit.
            }

            override fun afterTextChanged(sequence: Editable?) {
                // Left intentionally blank (pg 163)
            }
        }

        hoardTitleField.addTextChangedListener(hoardTitleWatcher)

        // Setting widget properties (TODO: relabel this comment)
        letterRadioButton.apply{
            setOnCheckedChangeListener { _, isChecked ->
                Toast.makeText(context,"By-Letter method is ${if (isChecked) "en" else "dis"}abled.",Toast.LENGTH_SHORT).show()
            }
        }

        generateButton.apply {

            isEnabled = true //TODO only enable button when valid input is available.
            setOnClickListener {
                //Toast.makeText(context,"Generate button clicked.",Toast.LENGTH_SHORT).show()
                var debugString = "DEBUG STRING:\n"

                Toast.makeText(context,"Generate button clicked.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}