package com.example.android.treasurefactory

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class HMLetterAdapter(val context: Context,
                      var expandableListView: ExpandableListView,
                      val groupList: List<String>,
                      val childList: Array<Array<String>>) : BaseExpandableListAdapter() {

    //TODO Declare Lists/Maps/HashMaps/whatever for treasure types specifically here
    /*val lairCountMap = mutableMapOf<String,Int>(
        "A" to 0,
        "B" to 0,
        "C" to 0,
        "D" to 0,
        "E" to 0,
        "F" to 0,
        "G" to 0,
        "H" to 0,
        "I" to 0)
    val smallCountMap = mutableMapOf<String,Int>(
        "J" to 0,
        "K" to 0,
        "L" to 0,
        "M" to 0,
        "N" to 0,
        "O" to 0,
        "P" to 0,
        "Q" to 0,
        "R" to 0,
        "S" to 0,
        "T" to 0,
        "U" to 0,
        "V" to 0,
        "W" to 0,
        "X" to 0,
        "Y" to 0,
        "Z" to 0)*/

    var quantityHolder: Array<IntArray> = arrayOf(IntArray(9){0},IntArray(17){0}) //https://stackoverflow.com/questions/37117717/groupview-with-edittext-in-expandablelistview?

    override fun getChild(exListPos: Int, listPos: Int): Any = childList[exListPos][listPos]

    override fun getChildId(exListPos: Int, listPos: Int): Long = listPos.toLong()

    override fun getChildView(exListPos: Int, listPos: Int, isLast: Boolean, convertView: View?, parent: ViewGroup?): View {

        var childViewHolder: ChildViewHolder
        var convertView = convertView

        if (convertView == null){ // Might need to remove this per https://stackoverflow.com/questions/40895070/button-inside-childview-of-expandablelistview-is-not-working#comment69087483_40895555

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.hackmaster_treasure_gen_letter_list_child,null)

            childViewHolder = ChildViewHolder(convertView)
            quantityHolder[exListPos][listPos] = 0
            convertView.tag = childViewHolder

        } else {

           childViewHolder = convertView.tag as ChildViewHolder
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

            return correctCount(Integer.parseInt(quantityEdit.text.toString()) + increment)
        }

        // Set text of label and counter
        "Type ${childList[exListPos][listPos]}".also { typeLabel.text = it }
        quantityEdit.setText(quantityHolder[exListPos][listPos].toString())

        // *** Add listeners for widgets ***

        infoDot.setOnClickListener {

            val toastString = HMLetterObject.oddsList[exListPos][listPos]

            Toast.makeText(context, toastString, Toast.LENGTH_LONG).show()
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

            override fun onTextChanged(sequence: CharSequence?, //https://stackoverflow.com/a/33089950
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                Log.d("HMLetterAdapter", "<1> BEFORE: Value at quantityHolder[$exListPos][$listPos]= ${quantityHolder[exListPos][listPos]}")
                Log.d("HMLetterAdapter","<2> EditText at ${childList[exListPos][listPos]}= " + sequence.toString())
                var intValue = Integer.parseInt(sequence.toString())
                /* val fixedValue = correctCount(intValue)

                // Correct value if outside acceptable range
                if (intValue != fixedValue) {

                    intValue = fixedValue
                    quantityEdit.setText(intValue.toString())
                    Log.d("HMLetterAdapter","Value programmatically changed at ${childList[exListPos][listPos]}")
                }*/

                // Update stored value in model
                quantityHolder[exListPos][listPos] = intValue
                Log.d("HMLetterAdapter", "<3> AFTER: Value at quantityHolder[$exListPos][$listPos]= ${quantityHolder[exListPos][listPos]}")
            }

            override fun afterTextChanged(sequence: Editable?) {
                //Log.d("HMLetterAdapter","Edit text at ${childList[exListPos][listPos]}")
            }
        }

        quantityEdit.addTextChangedListener(counterWatcher)

        //log

        // *** Return view ***
        return convertView
    }

    override fun getChildrenCount(exListPos: Int): Int = childList[exListPos].size

    override fun getGroup(exListPos: Int): String = groupList[exListPos]

    override fun getGroupCount(): Int = groupList.size

    override fun getGroupId(exListPos: Int): Long = exListPos.toLong()

    override fun getGroupView(exListPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView

        if (convertView == null){ // Might need to remove this per https://stackoverflow.com/questions/40895070/button-inside-childview-of-expandablelistview-is-not-working#comment69087483_40895555

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.hackmaster_treasure_gen_letter_list_group,null)
        }

        val header = convertView?.findViewById(R.id.hackmaster_treasure_gen_exlist_header) as TextView

        header.text = groupList[exListPos]

        return convertView

    }

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(listPos: Int, exListPos: Int): Boolean = true

    private class ChildViewHolder(childView: View){
        val infoDot     = childView?.findViewById(R.id.treasure_type_info) as ImageView
        val typeLabel   = childView.findViewById(R.id.treasure_type_label) as TextView
        val minusButton = childView.findViewById(R.id.treasure_type_decrement_button) as Button
        val quantityEdit= childView.findViewById(R.id.treasure_type_counter) as EditText
        val plusButton  = childView.findViewById(R.id.treasure_type_increment_button) as Button

    }
}