package com.example.android.treasurefactory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ViewAnimator
import androidx.fragment.app.Fragment

class HMHoardGeneratorFragment : Fragment() {

    private lateinit var hoardTitleField: EditText
    private lateinit var letterRadioButton: RadioButton
    private lateinit var specificRadioButton: RadioButton
    private lateinit var viewAnimator: ViewAnimator
    private lateinit var generateButton: Button

    val letterGroupList: MutableList<String> = ArrayList()
    val letterChildList: MutableList<MutableList<String>> = ArrayList()

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
        generateButton = view.findViewById(R.id.hackmaster_gen_generate_button) as Button

        // Setting widget properties (TODO: relabel this comment)
        generateButton.apply {
            isEnabled = false
        }

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


    }
}