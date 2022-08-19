package com.example.android.treasurefactory

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.core.util.size
import androidx.recyclerview.widget.RecyclerView

abstract class MultiselectRecyclerAdapter<VH: RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    //https://enoent.fr/posts/recyclerview-basics/ TODO remove when fully tested

    private val selectedItems = SparseBooleanArray()

    var selectedCount: Int = selectedItems.size
        private set

    fun isSelected(position: Int): Boolean = getSelectedPositions().contains(position)

    /**
     * Sets key of item at given position to true, or removes it if already true.
     */
    open fun toggleSelection(position: Int) {
        if (selectedItems.get(position,false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position,true)
        }

        selectedCount = selectedItems.size

        notifyItemChanged(position)
    }

    /**
     * Sets multiple indices to new value, regardless of old value.
     *
     * @param positions List of indices to change value of.
     * @param isNowSelected New boolean value to set entries to.
     */
    open fun setPositions(positions: List<Int>, isNowSelected: Boolean) {
        val newIndices = positions.distinct().filter { it > -1 }

        Log.d("setPositions() | Indices", newIndices.joinToString())
        if (newIndices.isNotEmpty()) {
            if (isNowSelected) {
                newIndices.forEach { index -> selectedItems.put(index, true) }
            } else {
                newIndices.forEach { index ->
                    if (selectedItems.get(index, false)) selectedItems.delete(index)
                }
            }

            selectedCount = selectedItems.size

            newIndices.forEach { notifyItemChanged(it) }
        }
    }

    open fun clearAllSelections() {
        val selectedPositions = getSelectedPositions()
        selectedItems.clear()
        selectedCount = selectedItems.size
        selectedPositions.forEach { notifyItemChanged(it) }
    }

    /**
     * Returns a list of all selected positions.
     */
    open fun getSelectedPositions(): List<Int> {
        val selectedIndices = ArrayList<Int>()

        selectedItems.forEach { itemPosition, isSelected ->
            if (isSelected) selectedIndices.add(itemPosition)
        }

        return selectedIndices.toList()
    }
}