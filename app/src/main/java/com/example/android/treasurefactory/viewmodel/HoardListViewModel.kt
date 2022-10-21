package com.example.android.treasurefactory.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.treasurefactory.BookSpellListKeeper
import com.example.android.treasurefactory.MultihoardProcessor
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.SpCoDiscipline
import com.example.android.treasurefactory.model.SpellSchool
import com.example.android.treasurefactory.repository.HMRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HoardListViewModel(private val repository: HMRepository) : ViewModel() {

    private var isRunningAsync = false

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val hoardListLiveData = repository.getHoards()

    val textToastHolderLiveData = MutableLiveData<Pair<String,Int>?>(null)

    fun deleteSelectedHoards(hoardsToDelete: List<Hoard>) {

        viewModelScope.launch {

            setRunningAsync(true)

            if (hoardsToDelete.isNotEmpty()) {

                repository.deleteHoardsAndChildren(hoardsToDelete)
                textToastHolderLiveData.postValue(
                    Pair("${hoardsToDelete.size} hoard" + if (hoardsToDelete.size != 1) "s deleted."
                    else " deleted.", Toast.LENGTH_SHORT))
            }

            delay(1000L)

            setRunningAsync(false)
        }
    }

    /**
     * Checks hoards to see if they are mergeable and compiles them into a new hoard if so.
     *
     * @return true if ActionMode should be exited.
     */
    fun mergeSelectedHoards(hoardsToMerge: List<Hoard>, newHoardName: String? = null,
                            keepOriginal: Boolean = false) : Boolean {

        var exitActionMode = false

        viewModelScope.launch {

            setRunningAsync(true)

            val hoardProcessor = MultihoardProcessor(repository)

            val isMergeable : Boolean
            val mergeReason : String

            hoardProcessor.checkHoardMergeability(hoardsToMerge).also { result ->
                isMergeable = result.first
                mergeReason = result.second
            }

            if (isMergeable) {

                hoardProcessor.mergeHoards(hoardsToMerge,newHoardName,keepOriginal)

                textToastHolderLiveData.postValue(
                    Pair("${hoardsToMerge.size} hoards merged. Original hoards have been " +
                            if (keepOriginal) "retained." else "discarded.", Toast.LENGTH_SHORT))

                exitActionMode = !keepOriginal

            } else {

                textToastHolderLiveData.postValue(
                    Pair("Merge not allowed.\n\tReason:\n$mergeReason", Toast.LENGTH_SHORT))
            }

            delay(1000L)

            setRunningAsync(false)
        }

        return exitActionMode
    }

    /**
     * Creates copies of all provided hoards and added them to the database.
     *
     * @return Count of hoards duplicated.
     */
    fun duplicateSelectedHoards(hoardsToCopy: List<Hoard>) {

        viewModelScope.launch {

            setRunningAsync(true)

            if (hoardsToCopy.isNotEmpty()) {

                val hoardProcessor = MultihoardProcessor(repository)

                hoardProcessor.copyHoards(hoardsToCopy).also {
                    textToastHolderLiveData.postValue(
                        Pair("$it hoard" + if (hoardsToCopy.size != 1) "s duplicated."
                        else " duplicated.", Toast.LENGTH_SHORT))
                }
            }

            delay(1000L)

            setRunningAsync(false)
        }
    }

    // region [ Helper functions ]

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }

    fun testSpellTriples() {

        viewModelScope.launch {

            val bookSpellListKeeper = BookSpellListKeeper()

            setRunningAsync(true)

            val coreNullReturnEntries = ArrayList<Triple<String, Int, Int>>()

            val splatNullReturnEntries = ArrayList<Triple<String, Int, Int>>()

            repeat(2 ) { rootCount ->

                enumValues<SpCoDiscipline>().forEach { spCoDiscipline ->

                    for (index in 0..9) {

                        enumValues<SpellSchool>().forEach { spellSchool ->

                            val orderTripleList = bookSpellListKeeper.getSpellChoiceTripleList(
                                spCoDiscipline,
                                index,
                                spellSchool,
                                (rootCount == 0)
                            )

                            orderTripleList.forEach { entry ->

                                entry.let{ (name, disciplineInt, level) ->
                                    val pulledSpell =
                                        repository.getSpellByName(name,disciplineInt,level)

                                    if (pulledSpell == null) {
                                        if (rootCount == 0) {
                                            splatNullReturnEntries.add(entry)
                                        } else {
                                            coreNullReturnEntries.add(entry)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (index in 0..3) {

                        val orderTripleList = bookSpellListKeeper.getSpellChoiceTripleList(
                            spCoDiscipline,
                            index,
                            null,
                            (rootCount == 0),
                            index
                        )

                        orderTripleList.forEach { entry ->

                            entry.let{ (name, disciplineInt, level) ->
                                val pulledSpell =
                                    repository.getSpellByName(name,disciplineInt,level)

                                if (pulledSpell == null) {
                                    if (rootCount == 0) {
                                        splatNullReturnEntries.add(entry)
                                    } else {
                                        coreNullReturnEntries.add(entry)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            println("< GMG entries which yielded null result >")
            if (coreNullReturnEntries.isNotEmpty()) {

                coreNullReturnEntries.forEach { (name, disciplineInt, level) ->

                    println("[${enumValues<SpCoDiscipline>()[disciplineInt].name} $level] " + name)
                }
                println("")
            } else {
                println("None, good job!\n")
            }

            println("< Splat entries which yielded null result >")
            if (splatNullReturnEntries.isNotEmpty()) {

                splatNullReturnEntries.forEach { (name, disciplineInt, level) ->

                    println("[${enumValues<SpCoDiscipline>()[disciplineInt].name} $level] " + name)
                }
                println("")
            } else {
                println("None, good job!\n")
            }

            textToastHolderLiveData.postValue("Test concluded. Check console for results." to Toast.LENGTH_LONG)

            setRunningAsync(false)
        }
    }

    // endregion
}

class HoardListViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoardListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoardListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}