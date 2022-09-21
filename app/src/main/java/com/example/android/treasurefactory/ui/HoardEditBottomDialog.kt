package com.example.android.treasurefactory.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.DialogBottomHoardInfoEditBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardEditViewModel
import com.example.android.treasurefactory.viewmodel.HoardEditViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    private val IconDropdownArraysPair by lazy { getIconItemArrays() }

    //TODO make holders for drawable resources for most valuable items' icons

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

            hoardLiveData.observe(viewLifecycleOwner) { hoard ->

                //TODO Also consider, instead of using a listener, getting this information one-time.

                hoard?.let {

                    activeHoard = hoard

                    this.getMostValuableItemStrings()
                }
            }

            iconsReadyLiveData.observe(viewLifecycleOwner) { iconsAreReady ->

                if (iconsAreReady){

                    // Update dropdown options
                }
            }
        }
    }

    // region [ Inner classes ]

    private data class IconDropdownItem(@DrawableRes val drawableID: Int, val iconIDString: String,
                                    val label: String = "Option"){

        override fun toString(): String {
            return label
        }
    }

    /**
     * Extended [ArrayAdapter]<[IconDropdownItem]> that disables filtering, keeps track of last selected item, and has holder array for enabled items.
     *
     * @param _enabledItemsArray [BooleanArray] that should be the same size as [values]. If it is not or is null, all items will be initially enabled.
     */
    private inner class IconDropdownAdapter<IconDropdownItem>(context: Context, layout: Int,
                                                var values: Array<IconDropdownItem>,
                                                _enabledItemsArray: BooleanArray?) :
        ArrayAdapter<IconDropdownItem>(context, layout, values) {

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

    // endregion

    // region [ Helper functions ]

    private fun getIconItemArrays() : Pair<Array<IconDropdownItem>,Array<Boolean>> {

        val valuesArrayList = ArrayList<IconDropdownItem>()

        valuesArrayList.add(IconDropdownItem(
                resources.getIdentifier(
                    activeHoard.iconID,"drawable",view?.context?.packageName),
                activeHoard.iconID, "Keep current icon"))

        val enabledArrayList = ArrayList<Boolean>().apply { add(true) }

        hoardEditViewModel.let{ vm ->

            vm.preferredIconStr.let{ iconStr ->

                valuesArrayList.add(IconDropdownItem(
                    resources.getIdentifier(
                        iconStr,"drawable",view?.context?.packageName),
                    iconStr, "Recommended icon"))
                enabledArrayList.add(true)
            }

            vm.coinMixStr.let{ iconStr ->

                if (iconStr.isNotBlank()) {

                    valuesArrayList.add(IconDropdownItem(
                        resources.getIdentifier(
                            iconStr,"drawable",view?.context?.packageName),
                        iconStr, "Best spell collection"))

                    enabledArrayList.add(iconStr == "loot_lint")
                }
            }

            vm.mostValuableGemStr.let{ iconStr ->

                if (iconStr.isNotBlank()) {

                    valuesArrayList.add(IconDropdownItem(
                        resources.getIdentifier(
                            iconStr,"drawable",view?.context?.packageName),
                        iconStr, "Best gemstone"))

                    enabledArrayList.add(iconStr == "loot_lint")
                }
            }

            vm.mostValuableArtStr.let{ iconStr ->

                if (iconStr.isNotBlank()) {

                    valuesArrayList.add(IconDropdownItem(
                        resources.getIdentifier(
                            iconStr,"drawable",view?.context?.packageName),
                        iconStr, "Best art object"))

                    enabledArrayList.add(iconStr == "loot_lint")
                }
            }

            vm.mostValuableMagicStr.let{ iconStr ->

                if (iconStr.isNotBlank()) {

                    valuesArrayList.add(IconDropdownItem(
                        resources.getIdentifier(
                            iconStr,"drawable",view?.context?.packageName),
                        iconStr, "Best magic item"))

                    enabledArrayList.add(iconStr == "loot_lint")
                }
            }

            vm.mostValuableSpellStr.let{ iconStr ->

                if (iconStr.isNotBlank()) {

                    valuesArrayList.add(IconDropdownItem(
                        resources.getIdentifier(
                            iconStr,"drawable",view?.context?.packageName),
                        iconStr, "Best spell collection"))

                    enabledArrayList.add(iconStr == "loot_lint")
                }
            }
        }

        if (activeHoard.gpTotal > 0.0) {
            valuesArrayList.apply{
                add(IconDropdownItem(resources.getIdentifier(
                    "container_chest","drawable",view?.context?.packageName),
                "container_chest", "Treasure chest")
                )

                add(IconDropdownItem(resources.getIdentifier(
                    "container_strongbox","drawable",view?.context?.packageName),
                    "container_strongbox", "Strongbox")
                )

                add(IconDropdownItem(resources.getIdentifier(
                    "container_full","drawable",view?.context?.packageName),
                    "container_chest", "Adventurer's Backpack")
                )

                add(IconDropdownItem(resources.getIdentifier(
                    "container_beltpouch","drawable",view?.context?.packageName),
                    "container_beltpouch", "Belt pouch")
                )
            }
            enabledArrayList.addAll(arrayOf(true,true,true,true))
        }

        return valuesArrayList.toTypedArray() to enabledArrayList.toTypedArray()
    }

    fun updateUI() {

        // TODO Left off here. Probably should finish the AddHoardEvent stuff before finishing this.
        //  Still, finish it, save for the badge implementation, and the AddHoardEvent
        //  functionality. The sooner the refactor gets done, the sooner everything else can
        //  proceed. Once first two points are done, at least apply for the dev account.

        // TODO also, don't forget to cancel freepik subscription.

    }

    private fun AutoCompleteTextView.initializeAsDropdown(
        dropdownAdapter:IconDropdownAdapter<IconDropdownItem>, defaultPos: Int,
        enabledArray : Array<Boolean>?
    ) {
        val defaultItem = (adapter as IconDropdownAdapter<IconDropdownItem>).values[defaultPos]
        val defaultIcon = getDrawable(resources,defaultItem.drawableID,context?.theme)
            .boundForLabel()
        this.setAdapter(dropdownAdapter)
        this.setText(defaultItem.label,false)
        (adapter as IconDropdownAdapter<*>).setSelectedPosition(defaultPos)
        this.setCompoundDrawablesRelative(defaultIcon,null,null,null)
        if (enabledArray != null ) {
            (adapter as IconDropdownAdapter<*>).setEnabledByArray(enabledArray.toBooleanArray())
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