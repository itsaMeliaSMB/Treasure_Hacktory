package com.example.android.treasurefactory.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogBottomHoardInfoEditBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.HoardBadge
import com.example.android.treasurefactory.model.HoardEvent
import com.example.android.treasurefactory.viewmodel.HoardEditViewModel
import com.example.android.treasurefactory.viewmodel.HoardEditViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class HoardEditBottomDialog() : BottomSheetDialogFragment() {

    // TODO https://medium.com/androiddevelopers/navigation-component-dialog-destinations-bfeb8b022759

    private lateinit var activeHoard: Hoard

    private val labelDrawableDPDimen = 24f

    private val labelDrawablePxDimen by lazy {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            labelDrawableDPDimen, resources.displayMetrics).toInt()
    }

    val safeArgs : HoardEditBottomDialogArgs by navArgs()

    private val hoardEditViewModel: HoardEditViewModel by viewModels {
        HoardEditViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }

    private var _binding: DialogBottomHoardInfoEditBinding? = null
    private val binding get() = _binding!!

    private var iconDropdownArray = Array<SelectableItemData>(1) {
        SelectableItemData("Loading...",false,
            null,"clipart_default_vector_icon")
    }
    private var badgeDropdownArray = Array<SelectableItemData>(1) {
        SelectableItemData("Loading...",false,
            null,"clipart_default_vector_icon")
    }

    //region [ Overriden functions ]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = safeArgs.activeHoardID

        hoardEditViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = DialogBottomHoardInfoEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observers
        hoardEditViewModel.apply {

            hoardLiveData.observe(this@HoardEditBottomDialog) { hoard ->

                hoard?.let {

                    activeHoard = hoard

                    val initialEffortString = activeHoard.effortRating.toString()

                    binding.hoardEditGpEdit.text = Editable.Factory.getInstance()
                        .newEditable(initialEffortString)

                    this.getMostValuableItemStrings()
                }
            }

            iconsReadyLiveData.observe(this@HoardEditBottomDialog) { iconsAreReady ->

                if (iconsAreReady){

                    iconDropdownArray = getIconDropdownAdapterArray()
                    badgeDropdownArray = getBadgeDropdownAdapterArray()

                    val iconEnabledList = iconDropdownArray.map { it.enabled }
                    val badgeEnabledList = badgeDropdownArray.map { it.enabled }

                    val iconAdapter = DropdownAdapter(
                        requireContext(), R.layout.dropdown_menu_item,
                        iconDropdownArray.map { it.menuLabel }.toTypedArray(),
                        iconEnabledList.toBooleanArray())
                    val badgeAdapter = DropdownAdapter(
                        requireContext(), R.layout.dropdown_menu_item,
                        badgeDropdownArray.map { it.menuLabel }.toTypedArray(),
                        badgeEnabledList.toBooleanArray())
                    val initBadgePos = activeHoard.badge.ordinal

                    // Update dropdown options
                    binding.hoardEditIconAuto.apply{
                        initializeAsDropdown(iconAdapter,0,iconEnabledList.toTypedArray())
                        isEnabled = true
                    }
                    binding.hoardEditBadgeAuto.apply {
                        initializeAsDropdown(badgeAdapter, initBadgePos,
                            badgeEnabledList.toTypedArray())
                        isEnabled = true
                    }

                    // Hide progress indicator
                    binding.hoardEditProgressIndicator.visibility = View.GONE

                    // Set preview
                    setPreviewIcon(iconDropdownArray[0].cachedDrawable)
                    setPreviewBadge(badgeDropdownArray[initBadgePos].cachedDrawable)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Listeners
        binding.apply {

            hoardEditIconAuto.setOnItemClickListener { _, _, position, _ ->

                if (position in (hoardEditIconAuto.adapter as DropdownAdapter<*>).values.indices &&
                    position in iconDropdownArray.indices &&
                    (hoardEditIconAuto.adapter as DropdownAdapter<*>).isEnabled(position)) {

                    (hoardEditIconAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)
                    hoardEditViewModel.iconDropdownPos = position

                    setPreviewIcon(iconDropdownArray[position].cachedDrawable)
                }
            }

            hoardEditBadgeAuto.setOnItemClickListener { _, _, position, _ ->

                if (position in (hoardEditBadgeAuto.adapter as DropdownAdapter<*>).values.indices &&
                    position in badgeDropdownArray.indices &&
                    (hoardEditBadgeAuto.adapter as DropdownAdapter<*>).isEnabled(position)) {

                    (hoardEditBadgeAuto.adapter as DropdownAdapter<*>).setSelectedPosition(position)
                    hoardEditViewModel.badgeDropdownPos = position

                    setPreviewBadge(badgeDropdownArray[position].cachedDrawable)
                }
            }

            hoardEditGpEdit.addTextChangedListener { input ->

                val oldEffortRating = hoardEditViewModel.newEffortValue

                val roundedValue = (100 * (input.toString().toDoubleOrNull() ?: 0.00)).roundToInt() / 100.00

                // Set error message if applicable
                hoardEditGpInput.error = when {
                    roundedValue <= 0.00    -> "Too low"
                    roundedValue > 20.00    -> "Too high"
                    else                    -> null
                }

                if (hoardEditGpEdit.error == null) {
                    hoardEditViewModel.newEffortValue = roundedValue
                    Log.d("hoardEditGpEdit.addTextChangedListener",
                        "hoardEditViewModel.newEffortValue = $oldEffortRating -> " +
                                "${hoardEditViewModel.newEffortValue}")
                }
            }

            hoardEditCancelButton.setOnClickListener {
                dialog?.cancel()
            }

            hoardEditSaveButton.setOnClickListener {

                if (hoardEditViewModel.iconsReadyLiveData.value == true) {

                    val newHoardName = hoardEditNameEdit.text.toString()
                        .takeIf{ it.isNotBlank() } ?: activeHoard.name
                    val newHoardIconString = iconDropdownArray[hoardEditViewModel.iconDropdownPos]
                        .imageString
                    val newHoardBadge = HoardBadge.values()[hoardEditViewModel.badgeDropdownPos]
                    var newEffortValue = hoardEditViewModel.newEffortValue

                    if (newHoardName != activeHoard.name ||
                        newHoardIconString != activeHoard.iconID ||
                        newHoardBadge != activeHoard.badge ||
                        (newEffortValue != activeHoard.effortRating && binding.hoardEditGpEdit.error == null)
                    ) {

                        val eventStringBuilder = StringBuilder()

                        eventStringBuilder.append("Hoard modified by user.")

                        if (newHoardName != activeHoard.name) {
                            eventStringBuilder.append("\n\t- Name changed from " +
                                    "\"${activeHoard.name}\" to \"$newHoardName\"")
                        }
                        if (newHoardIconString != activeHoard.iconID) {
                            eventStringBuilder.append("\n\t- Thumbnail changed: ${
                                iconDropdownArray[hoardEditViewModel.iconDropdownPos].menuLabel}")
                        }
                        if (newHoardBadge != activeHoard.badge) {
                            eventStringBuilder.append("\n\t- Badge changed: ${
                                badgeDropdownArray[hoardEditViewModel.badgeDropdownPos].menuLabel}")
                        }

                        if (newEffortValue != activeHoard.effortRating && binding.hoardEditGpEdit.error == null) {

                            eventStringBuilder.append("\n\t- Difficulty ratio changed from " +
                                    "${activeHoard.effortRating} gp = 1 xp to " +
                                    "$newEffortValue gp = 1 xp")
                            Log.d("hoardEditSaveButton.setOnClickListener",
                                "activeHoard.effortRating = ${activeHoard.effortRating}, " +
                                        "newEffortValue = $newEffortValue")
                        } else {
                            newEffortValue = activeHoard.effortRating
                            Log.d("hoardEditSaveButton.setOnClickListener",
                                "No change in effortRating detected, or an error prevented it.")
                        }

                        val editEvent = HoardEvent(
                            hoardID = activeHoard.hoardID,
                            timestamp = System.currentTimeMillis(),
                            description = eventStringBuilder.toString(),
                            tag = "modification"
                        )

                        hoardEditViewModel.saveHoard(activeHoard.copy(name = newHoardName,
                            iconID = newHoardIconString, badge = newHoardBadge,
                            effortRating = newEffortValue), editEvent)
                    }
                    dialog?.dismiss()
                }
            }
        }
    }

    // endregion

    // region [ Inner classes ]
    private inner class DropdownAdapter<String>(context: Context, layout: Int,
                                                var values: Array<String>,
                                                _enabledItemsArray: BooleanArray?) :
        ArrayAdapter<String>(context, layout, values) {

        //https://rmirabelle.medium.com/there-is-no-material-design-spinner-for-android-3261b7c77da8

        private val emptyFilter = object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = values
                results.count = values.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }

        private var selectedPosition = 0

        private var enabledItemsArray : BooleanArray =
            if ((_enabledItemsArray != null) && (_enabledItemsArray.size == values.size)) {
                _enabledItemsArray
            } else {
                BooleanArray(values.size) { true }
            }

        fun getSelectedPosition() = selectedPosition

        fun setSelectedPosition(newPosition: Int) {

            if ((newPosition in values.indices) && (selectedPosition != newPosition)) {

                selectedPosition = newPosition
            }
        }

        fun getEnabledItemsArray() = enabledItemsArray

        fun getSelectedItem() = values[selectedPosition]

        override fun getFilter(): Filter = emptyFilter

        override fun isEnabled(position: Int): Boolean {
            return enabledItemsArray[position]
        }

        fun setEnabled(position: Int, newValue: Boolean) {

            if (position in values.indices){

                enabledItemsArray[position] = newValue
                notifyDataSetChanged()

            } else {

                Log.e("DropdownAdapter","Position $position is out of bounds, cannot set to $newValue.")
            }
        }

        fun setEnabledByArray(newValueArray: BooleanArray?) {

            if (newValueArray != null){

                if ((newValueArray.size == values.size)){

                    enabledItemsArray = newValueArray
                    notifyDataSetChanged()
                }
            }
        }
    }

    private data class SelectableItemData(val menuLabel : String, val enabled : Boolean,
        val cachedDrawable : Drawable?, val imageString : String)

    // endregion

    // region [ Helper functions ]

    private fun getIconDropdownAdapterArray() : Array<SelectableItemData> {

        val itemArrayList = ArrayList<SelectableItemData>()
        val activeHoardNotWorthless = (activeHoard.gpTotal > 0.0)

        // "Keep Current Icon" option
        itemArrayList.add(
            SelectableItemData(getString(R.string.dropdown_keep_current_icon),
                true,
                getNullableDrawable(activeHoard.iconID),
                activeHoard.iconID
            )
        )

        // "Best of" options
        hoardEditViewModel.let{ vm ->

            // Recommended thumbnail
            itemArrayList.add(
                SelectableItemData(getString(R.string.dropdown_recommended_icon),
                    true,
                    getNullableDrawable(vm.preferredIconStr),
                    vm.preferredIconStr
                )
            )

            vm.coinMixStr.let{ iconStr ->

                val isEnabled = (iconStr.isNotBlank() && iconStr != "loot_lint")

                itemArrayList.add(SelectableItemData(
                    (if (isEnabled) "" else "   \u20E0 ") + getString(R.string.dropdown_coins),
                    isEnabled,
                    getNullableDrawable(iconStr),
                    iconStr))
            }

            vm.mostValuableGemStr.let{ iconStr ->

                val isEnabled = (iconStr.isNotBlank() && iconStr != "loot_lint")

                itemArrayList.add(SelectableItemData(
                    (if (isEnabled) "" else "   \u20E0 ") + getString(R.string.dropdown_best_gem),
                    isEnabled,
                    getNullableDrawable(iconStr),
                    iconStr
                ))
            }

            vm.mostValuableArtStr.let{ iconStr ->

                val isEnabled = (iconStr.isNotBlank() && iconStr != "loot_lint")

                itemArrayList.add(SelectableItemData(
                    (if (isEnabled) "" else "   \u20E0 ") + getString(R.string.dropdown_best_art_object),
                    isEnabled,
                    getNullableDrawable(iconStr),
                    iconStr
                ))
            }

            vm.mostValuableMagicStr.let{ iconStr ->

                val isEnabled = (iconStr.isNotBlank() && iconStr != "loot_lint")

                itemArrayList.add(SelectableItemData(
                    (if (isEnabled) "" else "   \u20E0 ") + getString(R.string.dropdown_best_magic_item),
                    isEnabled,
                    getNullableDrawable(iconStr),
                    iconStr
                ))
            }

            vm.mostValuableSpellStr.let{ iconStr ->

                val isEnabled = (iconStr.isNotBlank() && iconStr != "loot_lint")

                itemArrayList.add(SelectableItemData(
                    (if (isEnabled) "" else "   \u20E0 ") + getString(R.string.dropdown_best_spell_collection),
                    isEnabled,
                    getNullableDrawable(iconStr),
                    iconStr
                ))
            }
        }

        // Generic options
        itemArrayList.addAll(
            arrayOf(
                SelectableItemData(
                    (if (activeHoardNotWorthless) "" else "   \u20E0 ") + getString(R.string.dropdown_treasure_chest),
                    activeHoardNotWorthless,
                    getNullableDrawable("container_chest"),
                    "container_chest"),
                SelectableItemData(
                    (if (activeHoardNotWorthless) "" else "   \u20E0 ") + getString(R.string.dropdown_strongbox),
                    activeHoardNotWorthless,
                    getNullableDrawable("container_strongbox"),
                    "container_strongbox"),
                SelectableItemData(
                    (if (activeHoardNotWorthless) "" else "   \u20E0 ") + getString(R.string.dropdown_backpack),
                    activeHoardNotWorthless,
                    getNullableDrawable("container_full"),
                    "container_full"),
                SelectableItemData(
                    (if (activeHoardNotWorthless) "" else "   \u20E0 ") + getString(R.string.dropdown_beltpouch),
                    activeHoardNotWorthless,
                    getNullableDrawable("container_beltpouch"),
                    "container_beltpouch")
            )
        )

        return itemArrayList.toTypedArray()
    }

    private fun getBadgeDropdownAdapterArray() : Array<SelectableItemData> {

        val itemArrayList = ArrayList<SelectableItemData>()

        HoardBadge.values().forEach {

            if (it.resString != null) {

                itemArrayList.add(
                    SelectableItemData(
                        resources.getString(
                                resources.getIdentifier(it.resString,"string",view?.context?.packageName)),
                        true,
                        getNullableDrawable(it.resString),
                        it.resString
                    )
                )

            } else {

                itemArrayList.add(
                    SelectableItemData("None", true, null,
                        "clipart_default_vector_icon"
                    )
                )
            }
        }

        return itemArrayList.toTypedArray()
    }

    private fun setPreviewIcon(newDrawable: Drawable?) {

        binding.hoardEditPreviewIcon.setImageDrawable(newDrawable)
    }

    private fun setPreviewBadge(newDrawable: Drawable?) {

        binding.hoardEditPreviewBadge.setImageDrawable(newDrawable)
    }

    private fun getNullableDrawable(resString: String) : Drawable? {

        return try {

            ResourcesCompat.getDrawable(resources,
                resources.getIdentifier(resString,"drawable",
                    view?.context?.packageName), context?.theme)

        } catch (e: Resources.NotFoundException) {

            null
        }

    }

    private fun AutoCompleteTextView.initializeAsDropdown(
        dropdownAdapter:DropdownAdapter<*>, defaultPos: Int,
        enabledArray : Array<Boolean>?
    ) {
        this.setAdapter(dropdownAdapter)
        this.setText((adapter as DropdownAdapter<*>).values[defaultPos].toString(),false)
        (adapter as DropdownAdapter<*>).setSelectedPosition(defaultPos)
        if (enabledArray != null ) {
            (adapter as DropdownAdapter<*>).setEnabledByArray(enabledArray.toBooleanArray())
        }
    }

    /** Sets the bounds of the provided drawable so that it can be used as a compound drawable. */
    private fun Drawable?.boundForLabel() : Drawable? {
        return if (this != null) {
            val newDrawable = this

            newDrawable.setBounds(0,0,labelDrawablePxDimen,labelDrawablePxDimen)

            newDrawable
        } else this
    }

    // endregion
}