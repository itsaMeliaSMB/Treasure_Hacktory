package com.treasurehacktory.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.treasurehacktory.LootGeneratorAsync
import com.treasurehacktory.LootMutator
import com.treasurehacktory.database.MagicItemTemplate
import com.treasurehacktory.model.*
import com.treasurehacktory.repository.HMRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class UniqueDetailsViewModel(private val repository: HMRepository) : ViewModel() {

    // region [ Properties ]
    private var isRunningAsync = false

    private val itemArgsLiveData = MutableLiveData<Triple<Int, UniqueItemType,Int>>()

    val exposedHoardLiveData: LiveData<Hoard?> = Transformations.switchMap(itemArgsLiveData) { (_, _, hoardID) ->
        repository.getHoard(hoardID)
    }

    var viewedItemLiveData = MutableLiveData<ViewableItem?>()

    /**
     * Pair of the [spell][Spell] and [SimpleSpellEntry] that was clicked to request this.
     * */
    var dialogSpellInfoLiveData = MutableLiveData<Pair<Spell?, SimpleSpellEntry>?>(null)

    /**
     * Pair of the lists of spells for deciding a choice slot and the [SimpleSpellEntry]
     * of the choice slot to be replaced.
     */
    var dialogSpellsInfoLiveData = MutableLiveData<Pair<List<Spell>, SimpleSpellEntry>?>(null)

    /**
     * List of [magic item templates][MagicItemTemplate] for deciding a choice slot.
     */
    var dialogItemTemplatesInfoLiveData = MutableLiveData<List<MagicItemTemplate>?>(null)

    val isRunningAsyncLiveData = MutableLiveData(isRunningAsync)

    val textToastHolderLiveData = MutableLiveData<Pair<String,Int>?>(null)

    private fun setRunningAsync(newValue: Boolean) {

        isRunningAsync = newValue
        isRunningAsyncLiveData.postValue(isRunningAsync)
    }
    // endregion

    fun loadItemArgs(itemID: Int, type: UniqueItemType, hoardID: Int) {
        itemArgsLiveData.value = Triple(itemID,type,hoardID)
    }

    fun saveViewedItem(itemToUpdate: ViewableItem, parentHoard: Hoard, event: HoardEvent?) {

        viewModelScope.launch {

            when (itemToUpdate) {

                is ViewableGem -> {
                    repository.updateGems(listOf(itemToUpdate.toGem()))
                }
                is ViewableArtObject -> {
                    repository.updateArtObjects(listOf(itemToUpdate.toArtObject()))
                }
                is ViewableMagicItem -> {
                    repository.updateMagicItems(listOf(itemToUpdate.toMagicItem()))
                }
                is ViewableSpellCollection -> {
                    repository.updateSpellCollections(listOf(itemToUpdate.toSpellCollection()))
                }
            }

            // Update parent hoard after updating item

            val updatedHoard : Hoard? = LootMutator.auditHoard(parentHoard.hoardID,repository)

            if (updatedHoard != null) { repository.updateHoard(updatedHoard) }

            if (event != null) { repository.addHoardEvent(event) }

            // Repost load-in values to trigger reload
            itemArgsLiveData.run {
                val oldValue = this.value
                postValue(oldValue)
            }
        }

    }

    fun updateViewedItem(itemID: Int, itemType: UniqueItemType, hoardID: Int){

        viewModelScope.launch{

            setRunningAsync(true)

            val viewableItem = when (itemType){

                UniqueItemType.GEM -> {

                    val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                        .takeUnless { it == 0.0 } ?: 5.0

                    repository.getGemOnce(itemID)?.toViewableGem(effortRating)
                }

                UniqueItemType.ART_OBJECT -> {

                    val effortRating = repository.getHoardEffortRatingOnce(hoardID)
                        .takeUnless { it == 0.0 } ?: 5.0

                    repository.getArtObjectOnce(itemID)?.toViewableArtObject(effortRating)
                }

                UniqueItemType.MAGIC_ITEM -> {

                    repository.getMagicItemOnce(itemID)?.toViewableMagicItem()
                }

                UniqueItemType.SPELL_COLLECTION -> {

                    repository.getSpellCollectionOnce(itemID)?.toViewableSpellCollection(repository)
                }
            }

            viewedItemLiveData.postValue(viewableItem)

            setRunningAsync(false)
        }
    }

    fun fetchSpellForDialog(entry: SimpleSpellEntry) {

        viewModelScope.launch{

            setRunningAsync(true)

            val fetchedSpell = repository.getSpell(entry.spellID)

            dialogSpellInfoLiveData.postValue(fetchedSpell to entry)

            setRunningAsync(false)
        }
    }

    fun fetchSpellsForDialog(entry: SimpleSpellEntry, isChoice: Boolean) {

        viewModelScope.launch {

            setRunningAsync(true)

            if (isChoice) {

                if (entry.schools.isEmpty()) {

                    val result = when {

                        entry.name.contains("SSG") -> {

                            when (entry.name.substringAfter("(").removeSuffix(")")) {
                                "Offensive" -> {
                                    repository.getInitialChoiceSpells("O")
                                }
                                "Defensive" -> {
                                    repository.getInitialChoiceSpells("D")
                                }
                                "Miscellaneous" -> {
                                    repository.getInitialChoiceSpells("M")
                                }
                                else -> {
                                    emptyList()
                                }
                            }
                        }
                        entry.name.contains("GMG") -> {

                            when (entry.name.substringAfter("(").removeSuffix(")")) {
                                "Offensive" -> {
                                    repository.getInitialChoiceSpells("o")
                                }
                                "Defensive" -> {
                                    repository.getInitialChoiceSpells("d")
                                }
                                "Miscellaneous" -> {
                                    repository.getInitialChoiceSpells("m")
                                }
                                else -> {
                                    emptyList()
                                }
                            }
                        }
                        else -> {
                            emptyList()
                        }
                    }

                    dialogSpellsInfoLiveData.postValue(result to entry)

                } else {

                    val result = repository.getLevelChoiceSpells(
                        entry.level, entry.schools.first(),
                        (entry.name.contains("SSG"))
                    )

                    dialogSpellsInfoLiveData.postValue(result to entry)
                }
            } else {

                val result = repository.getSpellsByDiscipline(
                    entry.discipline.ordinal.coerceIn(0,2),entry.level
                )

                dialogSpellsInfoLiveData.postValue(result to entry)
            }

            setRunningAsync(false)
        }
    }

    fun fetchItemTemplatesForDialog(tableType: MagicItemType) {

        fun getSpecialTableTemplates() : List<MagicItemTemplate> {
            return when (tableType) {
                MagicItemType.A18 -> listOf(
                    MagicItemTemplate( // Special Armor
                        -1,1,"[Converted] GM's Choice", ReferenceType.CORE,
                        "GameMaster's Guide",218,0,0,0,"",
                        0,0,0,"A20","container_chest",
                        1,1,1,1,1,0,0,
                        "",0,"",0,"",
                        0,0,0,0,0,0
                    )
                )
                MagicItemType.A20 -> listOf(
                    MagicItemTemplate( // Standard Armor
                        -1,1,"[Converted] GM's Choice", ReferenceType.CORE,
                        "GameMaster's Guide",218,0,0,0,"",
                        0,0,0,"A18","container_chest",
                        1,1,1,1,1,0,0,
                        "",0,"",0,"",
                        0,0,0,0,0,0
                    )
                )
                MagicItemType.A21 -> listOf(
                    MagicItemTemplate( // Special Weapon
                        -1,1,"[Converted] GM's Choice", ReferenceType.CORE,
                        "GameMaster's Guide",218,0,0,0,"",
                        0,0,0,"A23","container_chest",
                        1,1,1,1,1,0,0,
                        "",0,"",0,"",
                        0,0,0,0,0,0
                    )
                )
                MagicItemType.A23 -> listOf(
                    MagicItemTemplate( // Standard Weapon
                        -1,1,"[Converted] GM's Choice", ReferenceType.CORE,
                        "GameMaster's Guide",218,0,0,0,"",
                        0,0,0,"A21","container_chest",
                        1,1,1,1,1,0,0,
                        "",0,"",0,"",
                        0,0,0,0,0,0
                    )
                )
                else -> emptyList()
            }
        }

        viewModelScope.launch {

            val templates = listOf(repository.getBaseItemTempsByType(tableType),
                getSpecialTableTemplates()
            ).flatten()

            dialogItemTemplatesInfoLiveData.postValue(templates)
        }
    }

    fun replaceItemFromTemplateID(templateID: Int, targetItem: ViewableMagicItem) {

        viewModelScope.launch{

            setRunningAsync(true)

            val parentHoard = exposedHoardLiveData.value
            val generator = LootGeneratorAsync(repository)

            val newItem = generator.generateNewItemForChoiceSlot(
                targetItem.hoardID, targetItem.itemID, templateID)

            val newHoardEvent = HoardEvent(
                hoardID = targetItem.hoardID,
                timestamp = System.currentTimeMillis(),
                description = "${targetItem.name} [id: ${targetItem.itemID}|table: ${
                    targetItem.mgcItemType.name}] was resolved as ${newItem.name}.",
                tag = "modification|magic-item|choice"
            )

            if (parentHoard != null) {

                saveViewedItem(newItem.toViewableMagicItem(), parentHoard, newHoardEvent)
            }

            setRunningAsync(false)
        }
    }

    fun replaceItemAsGMChoice(targetItem: ViewableMagicItem) {

        @SuppressLint("SimpleDateFormat")
        fun ViewableMagicItem.convertToChoice(parentHoardName : String) : ViewableMagicItem {
            val newType = if (this.mgcItemType == MagicItemType.Map ||
                this.mgcItemType == MagicItemType.Mundane) {

                enumValues<MagicItemType>().filter{ it == MagicItemType.Map ||
                        it == MagicItemType.Mundane}.random()
            } else {
                this.mgcItemType
            }

            return ViewableMagicItem(
                    this.itemID,
                    this.hoardID,
                    "[Converted] GM's Choice",
                    this.subtitle,
                    this.creationTime,
                    when(newType){
                        MagicItemType.A2    -> "potion_empty"
                        MagicItemType.A3    -> "scroll_base"
                        MagicItemType.A4    -> "ring_gold"
                        MagicItemType.A5    -> "staff_ruby"
                        MagicItemType.A6    -> "staff_iron"
                        MagicItemType.A7    -> "wand_wood"
                        MagicItemType.A8    -> "book_normal"
                        MagicItemType.A9    -> "jewelry_box"
                        MagicItemType.A13   -> "container_full"
                        MagicItemType.A14   -> "dust_incense"
                        MagicItemType.A24   -> "artifact_box"
                        else                -> "container_chest"
                    },
                this.iFrameFlavor,
                "GameMaster’s Guide",
                213,
                0.0,
                0,
                UniqueItemType.MAGIC_ITEM,
                listOf("Pre-conversion info" to listOf(
                    PlainTextEntry("Was previously an instance of \"${this.originalName}\" " +
                            "(template #${this.mgcTemplateID})."),
                    PlainTextEntry("Was part of hoard \"${parentHoardName}\" (id:${
                        this.hoardID}) when converted."),
                    PlainTextEntry("Converted on ${
                        SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                        .format(System.currentTimeMillis())}."),
                    PlainTextEntry("Was worth ${this.gpValue} gp and ${this.xpValue} xp at time " +
                            "of conversion.")
                )
                ),
                "[Converted] GM's Choice",
                -1,
                newType,
                mapOf(
                    "Fighter" to true,
                    "Thief" to true,
                    "Cleric" to true,
                    "Magic-user" to true,
                    "Druid" to true),
                false,
                "",
                listOf("Pre-conversion info" to listOf(
                    "Was previously an instance of \"${this.originalName}\" (template #${
                        this.mgcTemplateID}).",
                    "Was part of hoard \"${parentHoardName}\" (id:${this.hoardID}) when converted.",
                    "Converted on ${SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                            .format(System.currentTimeMillis())}.",
                    "Was worth ${this.gpValue} gp and ${this.xpValue} xp at time of conversion.")),
                if (newType == MagicItemType.A2) {
                    listOf("red","green","blue")
                } else { emptyList() }
            )
        }

        viewModelScope.launch{

            setRunningAsync(true)

            val parentHoard = exposedHoardLiveData.value

            if (parentHoard != null) {

                val newItem = targetItem.convertToChoice(parentHoard.name)

                val newHoardEvent = HoardEvent(
                    hoardID = targetItem.hoardID,
                    timestamp = System.currentTimeMillis(),
                    description = "${targetItem.name} [id: ${targetItem.itemID}|type: ${
                        targetItem.mgcItemType.name}] was converted into a wildcard " +
                            "entry of type ${newItem.mgcItemType.name} (${
                                newItem.mgcItemType.tableLabel}).",
                    tag = "modification|magic-item|choice"
                )

                saveViewedItem(newItem, parentHoard, newHoardEvent)
            }

            setRunningAsync(false)
        }
    }

    fun replaceSpellFromDialog(entry: SimpleSpellEntry, targetSpCo: SpellCollection) {

        viewModelScope.launch{

            val parentHoard = exposedHoardLiveData.value

            val newSpellList = targetSpCo.spells.toMutableList()

            // Replace the entry in the copy list
            newSpellList[entry.spellsPos] =
                SpellEntry(entry.spellID,entry.level,0, entry.isUsed)

            val newHoardEvent = HoardEvent(
                hoardID = targetSpCo.hoardID,
                timestamp = System.currentTimeMillis(),
                description = (repository.getSpell(
                    targetSpCo.spells[entry.spellsPos].spellID)?.let {
                    it.name + " (Lv ${it.spellLevel}) "
                } ?: "Choice Spell ") +
                        "in ${targetSpCo.name} [id:${targetSpCo.sCollectID}] was changed to ${
                            entry.name}.",
                tag = "modification|spell-collection|choice"
            )

            if (parentHoard != null) {

                saveViewedItem(
                    targetSpCo.copy(
                        spells = newSpellList.toList(),
                        gpValue = SpellCollection.calculateGPValue(targetSpCo.type,
                            targetSpCo.augmentations,targetSpCo.pageCount,newSpellList.toList()),
                        xpValue = SpellCollection.calculateXPValue(targetSpCo.type,
                            newSpellList.toList())
                    )
                        .toViewableSpellCollection(repository),
                    parentHoard, newHoardEvent)
            }
        }
    }

    fun toggleArtObjectAuthenticity(targetArt: ViewableArtObject) {
        viewModelScope.launch {

            setRunningAsync(true)

            val parentHoard = exposedHoardLiveData.value

            val convertedArt = targetArt.toArtObject().copy(
                isForgery = targetArt.artIsForgery.not(),
                gpValue = if (targetArt.artIsForgery) {
                    // Remember, this is what the *new* value will be
                    LootMutator.convertArtValueToGP(targetArt.artValueLevel)
                } else {
                    0.0
                }
            )

            val event = HoardEvent(
                hoardID = convertedArt.hoardID,
                timestamp = System.currentTimeMillis(),
                description = "\"${convertedArt.name}\" [id:${convertedArt.artID}] was " +
                        "marked as " + (if (convertedArt.isForgery) {
                    "a worthless forgery" } else { "the genuine article" }) + ", changing its " +
                        "estimated value from to " +
                        DecimalFormat("#,##0.0#").format(targetArt.gpValue)
                            .removeSuffix(".0") + " gp to " +
                        DecimalFormat("#,##0.0#")
                            .format(convertedArt.gpValue).removeSuffix(".0") + " gp.",
                tag = "modification|art-object"
            )

            if (parentHoard != null) {

                saveViewedItem(
                    convertedArt.toViewableArtObject(parentHoard.effortRating),
                    parentHoard, event)
            }

            setRunningAsync(false)
        }
    }

    fun rerollArtObjectName(targetArt: ViewableArtObject) {
        viewModelScope.launch {

            setRunningAsync(true)

            val parentHoard = exposedHoardLiveData.value

            val newName = ArtObject.getRandomName(targetArt.artType,targetArt.artSubject)

            val convertedArt = targetArt.toArtObject().copy(
                name = newName,
                originalName = newName
            )

            val event = HoardEvent(
                hoardID = convertedArt.hoardID,
                timestamp = System.currentTimeMillis(),
                description = "\"${targetArt.name}\"" +
                        if (targetArt.name != targetArt.originalName) {
                            "(originally\" ${targetArt.originalName}\")"
                        } else { "" } + "[id:${targetArt.itemID}] had " +
                        "its name re-rolled to be $newName.",
                tag = "modification|art-object|reroll"
            )

            if (parentHoard != null) {

                saveViewedItem(
                    convertedArt.toViewableArtObject(parentHoard.effortRating),
                    parentHoard, event)
            }

            setRunningAsync(false)
        }
    }

    fun setItemAsHoardIcon(hoardID: Int, viewableItem: ViewableItem) {

        viewModelScope.launch{

            setRunningAsync(true)

            val targetHoard = repository.getHoardOnce(hoardID)

            if (targetHoard != null) {
                repository.updateHoard(targetHoard.copy(iconID = viewableItem.iconStr))

                val iconEvent = HoardEvent(
                    hoardID = hoardID,
                    timestamp = System.currentTimeMillis(),
                    description = "Changed hoard thumbnail to that of ${viewableItem.name} " +
                            "[id:${viewableItem.itemID}]",
                    tag = "modification" + when (viewableItem){
                        is ViewableGem              -> "|gemstone"
                        is ViewableArtObject        -> "|art-object"
                        is ViewableMagicItem        -> "|magic-item"
                        is ViewableSpellCollection  -> "|spell-collection"
                    }
                )

                repository.addHoardEvent(iconEvent)
            }

            setRunningAsync(false)
        }
    }

    fun rerollItem(viewableItem: ViewableItem){

        viewModelScope.launch {

            setRunningAsync(true)

            val generator = LootGeneratorAsync(repository)

            when (viewableItem){
                is ViewableGem -> {
                    repository.run {
                        val newItem = generator.createGem(viewableItem.hoardID)
                            .copy(gemID = viewableItem.itemID)

                        updateGems( listOf( newItem ) )

                        LootMutator.auditHoard(viewableItem.hoardID,this).also{
                            if (it != null) {
                                this.updateHoard(it)
                            }
                        }

                        val rerollEvent = HoardEvent(
                            hoardID = viewableItem.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Re-rolled \"${viewableItem.name}\" [id:${
                                viewableItem.itemID}]. It was replaced with \"${newItem.name}\"",
                            tag = "modification|gemstone|reroll"
                        )

                        this.addHoardEvent(rerollEvent)
                    }
                }
                is ViewableArtObject -> {
                    repository.run {
                        val newItem = generator.createArtObject(viewableItem.hoardID,
                            ArtRestrictions()
                        ).first.copy(artID = viewableItem.itemID)

                        updateArtObjects( listOf( newItem ) )

                        LootMutator.auditHoard(viewableItem.hoardID,this).also{
                            if (it != null) {
                                this.updateHoard(it)
                            }
                        }

                        val rerollEvent = HoardEvent(
                            hoardID = viewableItem.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Re-rolled \"${viewableItem.name}\" [id:${
                                viewableItem.itemID}]. It was replaced with \"${newItem.name}\"",
                            tag = "modification|art-object|reroll"
                        )

                        this.addHoardEvent(rerollEvent)
                    }
                }
                is ViewableMagicItem -> {
                    repository.run {
                        val newItem = generator.createMagicItemTuple(viewableItem.hoardID)
                            .magicItem.copy(mItemID = viewableItem.itemID)

                        updateMagicItems( listOf( newItem ) )

                        LootMutator.auditHoard(viewableItem.hoardID,this).also{
                            if (it != null) {
                                this.updateHoard(it)
                            }
                        }

                        val rerollEvent = HoardEvent(
                            hoardID = viewableItem.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Re-rolled \"${viewableItem.name}\" [id:${
                                viewableItem.itemID}]. It was replaced with \"${newItem.name}\"",
                            tag = "modification|magic-item|reroll"
                        )

                        this.addHoardEvent(rerollEvent)
                    }
                }
                is ViewableSpellCollection -> {
                    repository.run {
                        val newItem = generator.createSpellCollection(viewableItem.hoardID,
                            SpellCoRestrictions(allowedDisciplines = when (viewableItem.spCoDiscipline){
                                SpCoDiscipline.ARCANE -> AllowedDisciplines(true,false,false)
                                SpCoDiscipline.DIVINE -> AllowedDisciplines(false,true,false)
                                SpCoDiscipline.NATURAL -> AllowedDisciplines(false, false,true)
                                SpCoDiscipline.ALL_MAGIC -> AllowedDisciplines(true,true,false)
                            })
                        ).copy(sCollectID = viewableItem.itemID)

                        updateSpellCollections( listOf( newItem ) )

                        LootMutator.auditHoard(viewableItem.hoardID,this).also{
                            if (it != null) {
                                this.updateHoard(it)
                            }
                        }

                        val rerollEvent = HoardEvent(
                            hoardID = viewableItem.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = "Re-rolled \"${viewableItem.name}\" [id:${
                                viewableItem.itemID}]. It was replaced with \"${newItem.name}\"",
                            tag = "modification|spell-collection|reroll"
                        )

                        this.addHoardEvent(rerollEvent)
                    }
                }
            }

            loadItemArgs(viewableItem.itemID, viewableItem.itemType, viewableItem.hoardID)

            setRunningAsync(false)
        }
    }

    fun renameItem(viewableItem: ViewableItem, parentHoard: Hoard, newName : String, isRestore: Boolean){

        viewModelScope.launch {

            setRunningAsync(true)

            if (isRestore) {

                val restoreEvent = HoardEvent(
                    hoardID = viewableItem.hoardID,
                    timestamp = System.currentTimeMillis(),
                    description = "\"${viewableItem.name}\" [id:${
                        viewableItem.itemID}] was restored to it's original name, \"$newName\".",
                    tag="modification|" + when (viewableItem){
                        is ViewableGem -> "gemstone"
                        is ViewableArtObject -> "art-object"
                        is ViewableMagicItem -> "magic-item"
                        is ViewableSpellCollection -> "spell-collection"
                    }
                )

                val newItem = when (viewableItem){
                    is ViewableGem -> {
                        viewableItem.toGem().copy(name=viewableItem.originalName)
                            .toViewableGem(parentHoard.effortRating)
                    }
                    is ViewableArtObject -> {
                        viewableItem.toArtObject().copy(name=viewableItem.originalName)
                            .toViewableArtObject(parentHoard.effortRating)
                    }
                    is ViewableMagicItem -> {
                        viewableItem.toMagicItem().copy(name=viewableItem.originalName)
                            .toViewableMagicItem()
                    }
                    is ViewableSpellCollection -> {
                        viewableItem
                            .toSpellCollection().copy(name=viewableItem.originalName)
                            .toViewableSpellCollection(repository)
                    }
                }

                saveViewedItem(newItem, parentHoard, restoreEvent)

            } else {

                val renameEvent = HoardEvent(
                    hoardID = viewableItem.hoardID,
                    timestamp = System.currentTimeMillis(),
                    description = "\"${viewableItem.name}\" [id:${
                        viewableItem.itemID}] was re-named to \"$newName\".",
                    tag="modification|" + when (viewableItem){
                        is ViewableGem -> "gemstone"
                        is ViewableArtObject -> "art-object"
                        is ViewableMagicItem -> "magic-item"
                        is ViewableSpellCollection -> "spell-collection"
                    }
                )

                val newItem = when (viewableItem){
                    is ViewableGem -> {
                        viewableItem.toGem().copy(name=newName)
                            .toViewableGem(parentHoard.effortRating)
                    }
                    is ViewableArtObject -> {
                        viewableItem.toArtObject().copy(name=newName)
                            .toViewableArtObject(parentHoard.effortRating)
                    }
                    is ViewableMagicItem -> {
                        viewableItem.toMagicItem().copy(name=newName)
                            .toViewableMagicItem()
                    }
                    is ViewableSpellCollection -> {
                        viewableItem
                            .toSpellCollection().copy(name=newName)
                            .toViewableSpellCollection(repository)
                    }
                }

                saveViewedItem(newItem, parentHoard, renameEvent)
            }

            setRunningAsync(false)
        }
    }
}

class UniqueDetailsViewModelFactory(private val repository: HMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniqueDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniqueDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}