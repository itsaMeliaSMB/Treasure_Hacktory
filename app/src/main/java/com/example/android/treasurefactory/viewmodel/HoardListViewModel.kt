package com.example.android.treasurefactory.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.treasurefactory.MultihoardProcessor
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.SpCoDiscipline
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

    fun testSpellTriples(context: Context) {

        //TODO delete this after tests are finished; I really don't like having a context reference in viewmodel!

        viewModelScope.launch{

            setRunningAsync(true)

            println("Beginning spell validation test...\n")

            val nullReturnEntries = ArrayList<Triple<String,SpCoDiscipline,Int>>()

            val validReturnEntries = ArrayList<Triple<String,SpCoDiscipline,Int>>()

            suspend fun testArcane() {

                val inputStream = context.resources.openRawResource(
                    context.resources.getIdentifier("unique_book_arcane_doubles","raw",context.packageName))

                inputStream
                    .bufferedReader()
                    .lineSequence()
                    .forEach { spellLine ->

                        val spellName : String
                        val spellLevel : Int

                        spellLine.split(";").let{
                            spellName = it.first()
                            spellLevel = it.last().toIntOrNull() ?: -1
                        }

                        val spellPull = repository.getSpellByName(spellName,0,spellLevel)

                        if (spellPull == null) {
                            nullReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.ARCANE,spellLevel))
                        } else {
                            validReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.ARCANE,spellLevel))
                        }
                    }
            }

            suspend fun testDivine() {

                val inputStream = context.resources.openRawResource(
                    context.resources.getIdentifier("unique_book_divine_doubles","raw",context.packageName))

                inputStream
                    .bufferedReader()
                    .lineSequence()
                    .forEach { spellLine ->

                        val spellName : String
                        val spellLevel : Int

                        spellLine.split(";").let{
                            spellName = it.first()
                            spellLevel = it.last().toIntOrNull() ?: -1
                        }

                        val clericSpellPull = repository.getSpellByName(spellName,1,spellLevel)
                        val druidSpellPull = repository.getSpellByName(spellName,2,spellLevel)

                        if (clericSpellPull == null) {
                            nullReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.DIVINE,spellLevel))
                        } else {
                            validReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.DIVINE,spellLevel))
                        }

                        if (druidSpellPull == null) {
                            nullReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.NATURAL,spellLevel))
                        } else {
                            validReturnEntries.add(
                                Triple(spellName,SpCoDiscipline.NATURAL,spellLevel))
                        }
                    }
            }

            testArcane()
            testDivine()

            println("-------------------------------------------")
            println("<<< Entries which yielded valid results >>>")
            println("-------------------------------------------")
            validReturnEntries.sortedBy { it.second }.let{ sortedList ->

                if (sortedList.isNotEmpty()) {

                    sortedList.forEach { (name, discipline, level) ->

                        println("[${discipline.name} $level] \"$name\"")
                    }
                } else {
                    println("Er... none. Uh-oh.")
                }
            }

            println("")

            println("------------------------------------------")
            println("<<< Entries which yielded null results >>>")
            println("------------------------------------------")
            nullReturnEntries.sortedBy { it.second }.let{ sortedList ->

                if (sortedList.isNotEmpty()) {

                    sortedList.forEach { (name, discipline, level) ->

                        println("[${discipline.name} $level] \"$name\"")
                    }
                } else {
                    println("None, good job!")
                }
            }

            println("\nTest concluded.\n")

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