package com.example.android.treasurefactory

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class HMLetterAdapter(private val context: Context,
                      var expandableListView: ExpandableListView,
                      val groupList: MutableList<String>,
                      val childList: MutableList<MutableList<String>>) : BaseExpandableListAdapter() {

    //TODO Declare Lists/Maps/HashMaps/whatever for treasure types specifically here
    val lettersCountArray = Array<Int>(26) { 0 }
    val infoStrings = Array<String>(26)
        { i -> HMLetterType.getOddsString(HMLetterType.values()[i]) }
    val letterArray = Array<String>(26) { i -> HMLetterType.values()[i].name }

    override fun getChild(exListPos: Int, listPos: Int): Any {
        return childList[exListPos][listPos]
    }

    override fun getChildId(groupPos: Int, childPos: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getChildView(exListPos: Int, listPos: Int, isLast: Boolean, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView
        val flatListPos = listPos + if (exListPos > 0) { 9 } else { 0 }

        if (convertView == null){ // might need to remove this per https://stackoverflow.com/questions/40895070/button-inside-childview-of-expandablelistview-is-not-working#comment69087483_40895555

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.hackmaster_treasure_gen_letter_list_item,null)
        }
        // *** Wire widgets to child view ***
        val infoDot     = convertView?.findViewById(R.id.treasure_type_info) as ImageView
        val typeLabel   = convertView.findViewById(R.id.treasure_type_label) as TextView
        val minusButton = convertView.findViewById(R.id.treasure_type_decrement_button) as Button
        val quantityEdit= convertView.findViewById(R.id.treasure_type_counter) as EditText
        val plusButton  = convertView.findViewById(R.id.treasure_type_increment_button) as Button

        // *** Helper functions ***
        fun correctCount(initCount : Int) : Int {

            val MIN_TYPE_VALUE = 0
            val MAX_TYPE_VALUE = 1000

            var newCount = initCount

            if (newCount < MIN_TYPE_VALUE) newCount = MIN_TYPE_VALUE
            if (newCount > MAX_TYPE_VALUE) newCount = MAX_TYPE_VALUE

            return newCount
        }

        fun incrementCount(increment: Int) : Int {

            val newValue = Integer.parseInt(quantityEdit.text.toString()) + increment

            return correctCount(newValue)
        }

        // Set text of label and counter

        "Type ${letterArray[flatListPos]}".also { typeLabel.text = it }

        quantityEdit.setText(lettersCountArray[flatListPos].toString())

        // *** Add listeners and getters/setters for widgets ***

        infoDot.setOnClickListener {

            Toast.makeText(context, infoStrings[flatListPos], Toast.LENGTH_LONG).show()
        }

        minusButton.setOnClickListener {

            // Decrement the count in quantityEdit counter
            quantityEdit.setText(incrementCount(-1).toString())

        }
        plusButton.setOnClickListener {

            // Increment the count in quantityEdit counter
            quantityEdit.setText(incrementCount(1).toString())
        }

        val counterWatcher = object : TextWatcher {

            override fun beforeTextChanged(sequence: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
                // Left intentionally blank
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                // Left intentionally blank
            }

            override fun afterTextChanged(sequence: Editable?) {

                var intValue = Integer.parseInt(sequence.toString())
                val fixedValue = correctCount(intValue)

                // Correct value if outside acceptable range
                if (intValue != fixedValue) {

                    intValue = fixedValue
                    quantityEdit.setText(intValue.toString())
                }

                // Update stored value in model
                lettersCountArray[flatListPos] = intValue
            }
        }

        quantityEdit.addTextChangedListener(counterWatcher)

        // *** Return view ***
        return convertView
    }

    override fun getChildrenCount(p0: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getGroup(exListPos: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getGroupCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getGroupId(p0: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getGroupView(exListPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(listPos: Int, exListPos: Int): Boolean = true

}