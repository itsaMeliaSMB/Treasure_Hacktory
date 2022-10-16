package com.example.android.treasurefactory.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardOverviewCoinageListItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardOverviewBinding
import com.example.android.treasurefactory.model.CoinType
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.model.HoardBadge
import com.example.android.treasurefactory.viewmodel.HoardOverviewViewModel
import com.example.android.treasurefactory.viewmodel.HoardOverviewViewModelFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

private const val ARG_HOARD_ID = "hoard_id"

class HoardOverviewFragment : Fragment() {

    // region [ Property declarations ]
    private lateinit var activeHoard: Hoard

    private var isNowFavorite = false

    val safeArgs : HoardOverviewFragmentArgs by navArgs()

    private var totalGemValue = 0.0
    private var totalArtValue = 0.0
    private var totalMagicValue = 0.0
    private var totalSpellValue = 0.0
    private var totalXPValue = 0

    private var _binding: LayoutHoardOverviewBinding? = null
    private val binding get() = _binding!!

    private var coinAdapter: CoinAdapter? = CoinAdapter(emptyList())

    private val hoardOverviewViewModel: HoardOverviewViewModel by viewModels {
        HoardOverviewViewModelFactory((activity?.application as TreasureHacktoryApplication).repository)
    }
    // endregion

    // region [ Overridden functions ]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = safeArgs.selectedHoardID

        hoardOverviewViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        _binding = LayoutHoardOverviewBinding.inflate(inflater, container, false)
        val view = binding.root

        // Give RecyclerView a Layout manager [required]
        binding.hoardOverviewCoinageList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coinAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // region [ Listeners ]
        hoardOverviewViewModel.hoardLiveData.observe(viewLifecycleOwner) { hoard ->

            hoard?.let {

                // Remove "New" status on load
                activeHoard = hoard.copy(isNew = false)

                isNowFavorite = activeHoard.isFavorite

                hoardOverviewViewModel.updateXPTotal(hoard.hoardID)

                updateUI()
            }
        }

        hoardOverviewViewModel.gemValueLiveData.observe(viewLifecycleOwner) { liveGemValue ->

            totalGemValue = liveGemValue

            // Update label on card
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalGemValue)
                .removeSuffix(".0")} gp")
                .also{ binding.hoardOverviewGemValue.text = it }
        }

        hoardOverviewViewModel.artValueLiveData.observe(viewLifecycleOwner) { liveArtValue ->

            totalArtValue = liveArtValue

            // Update label on card
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalArtValue)
                .removeSuffix(".0")} gp")
                .also{ binding.hoardOverviewArtValue.text = it }
        }

        hoardOverviewViewModel.magicValueLiveData.observe(viewLifecycleOwner) { liveMagicValue ->

            totalMagicValue = liveMagicValue

            // Update label on card
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalMagicValue)
                .removeSuffix(".0")} gp")
                .also{ binding.hoardOverviewMagicValue.text = it }
        }

        hoardOverviewViewModel.spellValueLiveData.observe(viewLifecycleOwner) { liveSpellValue ->

            totalSpellValue = liveSpellValue

            // Update label on card
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalSpellValue)
                .removeSuffix(".0")} gp")
                .also{ binding.hoardOverviewSpellsValue.text = it }
        }

        hoardOverviewViewModel.hoardTotalXPLiveData.observe(viewLifecycleOwner) { liveXPTotal ->

            val oldXPTotal = totalXPValue

            totalXPValue = liveXPTotal

            Log.d("HoardOverviewFragment | hoardTotalXPLiveData observer","totalXPValue " +
                    "change observed from $oldXPTotal to $totalXPValue.")

            // Update value in detail view
            (NumberFormat.getNumberInstance().format(totalXPValue) +
                    " xp").also { binding.hoardOverviewExperienceInfo.text = it }
            updateDifficultyLabel()
        }
        // endregion

        // region [ Toolbar ]
        binding.hoardOverviewToolbar.apply {

            // Get themed color attribute for Toolbar's title
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val titleTextColor = typedValue.data

            inflateMenu(R.menu.hoard_overview_toolbar_menu)
            title = getString(R.string.hoard_overview_fragment_title)
            setTitleTextColor(titleTextColor)
            setNavigationIcon(R.drawable.clipart_back_vector_icon)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.action_edit_hoard      -> {

                        val action = HoardOverviewFragmentDirections
                            .hoardOverviewEditBottomDialogAction(activeHoard.hoardID)
                        findNavController().navigate(action)

                        true
                    }

                    R.id.action_view_history    -> {

                        val action = HoardOverviewFragmentDirections
                            .hoardOverviewToEventLogAction(activeHoard.hoardID)
                        findNavController().navigate(action)

                        true
                    }

                    R.id.action_edit_coinage    -> {

                        val action = HoardOverviewFragmentDirections
                            .hoardOverviewToCoinageDialogAction(activeHoard.hoardID)
                        findNavController().navigate(action)

                        true
                    }

                    else    -> false
                }
            }
        }
        // endregion
    }

    override fun onStart() {
        super.onStart()

        binding.hoardOverviewFavCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            isNowFavorite = isChecked
        }

        binding.hoardOverviewGemCard.setOnClickListener {
            Toast.makeText(context, "Gem card clicked", Toast.LENGTH_SHORT).show()
        }

        binding.hoardOverviewArtCard.setOnClickListener {
            Toast.makeText(context, "Art card clicked", Toast.LENGTH_SHORT).show()
        }

        binding.hoardOverviewMagicCard.setOnClickListener {
            Toast.makeText(context, "Item card clicked", Toast.LENGTH_SHORT).show()
            //TODO start with magic items, I think
            // Sticky headers/collapsable nested recylcer views
            // Detail fragment
        }

        binding.hoardOverviewSpellsCard.setOnClickListener {
            Toast.makeText(context, "Spells card clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // NOTE TO SELF: Runs when back button is pressed or app is removed from active view
    override fun onStop() {
        super.onStop()
        hoardOverviewViewModel.saveHoard(activeHoard.copy(isFavorite = isNowFavorite))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    // region [ Inner classes ]
    private inner class CoinViewHolder(val binding: HoardOverviewCoinageListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinType: CoinType, coinCount: Int){

            // Set coin icon and label
            binding.apply{
                when (coinType) {
                    CoinType.CP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_copper)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.copper_pieces)
                    }
                    CoinType.SP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_silver)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.silver_pieces)
                    }
                    CoinType.EP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_electrum)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.electrum_pieces)
                    }
                    CoinType.GP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_gold)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.gold_pieces)
                    }
                    CoinType.HSP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_hardsilver)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.hard_silver_pieces)
                    }
                    CoinType.PP -> {
                        hoardOverviewCoinageItemIcon.setImageResource(R.drawable.item_coin_platinum)
                        hoardOverviewCoinageItemLabel.text = getString(R.string.platinum_pieces)
                    }
                }
            }

            // Set text for coin quantity
            ("x${
                DecimalFormat("#,##0")
                    .format(coinCount)}").also { binding.hoardOverviewCoinageItemColumnQty.text = it }

            // Set text for coin weight
            ("${
                DecimalFormat("#,##0.0#")
                    .format(coinCount / 10.0)
                    .removeSuffix(".0")} ib")
                .also { binding.hoardOverviewCoinageItemColumnWt.text = it }

            // Set text for coin value
            ("${
                DecimalFormat("#,##0.0#")
                    .format(coinCount * coinType.gpValue)
                    .removeSuffix(".0")} gp")
                .also { binding.hoardOverviewCoinageItemColumnGp.text = it }
        }
    }

    private inner class CoinAdapter(var coinList: List<Pair<CoinType,Int>>):
        RecyclerView.Adapter<CoinViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
            val binding = HoardOverviewCoinageListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
            return CoinViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

            // Bind data to list
            holder.bind(coinList[position].first,coinList[position].second)
        }

        override fun getItemCount(): Int = coinList.size
    }
    // endregion

    // region [ Helper functions ]
    @SuppressLint("SimpleDateFormat")
    fun updateUI() {

        // Update header information
        // Set icon for hoard
        try {

            binding.hoardOverviewItemframeIcon
                .setImageResource(resources
                    .getIdentifier(activeHoard.iconID,"drawable",view?.context?.packageName))

        } catch (e: Exception) {

            binding.hoardOverviewItemframeIcon
                .setImageResource(R.drawable.clipart_default_image)
        }

        if (activeHoard.badge != HoardBadge.NONE) {
            try{
                binding.hoardOverviewItemframeBadge.apply{
                    setImageResource(resources
                        .getIdentifier(activeHoard.badge.resString,
                            "drawable",view?.context?.packageName))
                    visibility = View.VISIBLE
                }
            } catch (e: Exception){
                binding.hoardOverviewItemframeBadge.apply{
                    setImageResource(R.drawable.badge_hoard_broken)
                    visibility = View.VISIBLE
                }
            }
        } else {
            binding.hoardOverviewItemframeBadge.visibility = View.INVISIBLE
        }

        binding.apply{

            hoardOverviewNameLabel.text = activeHoard.name
            hoardOverviewDateInfo.text = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                .format(activeHoard.creationDate)
            ("Worth ${DecimalFormat("#,##0.0#")
                .format(activeHoard.gpTotal)
                .removeSuffix(".0")} gp").also { binding.hoardOverviewValueInfo.text = it }
            hoardOverviewFavCheckbox.isChecked = activeHoard.isFavorite

        }

        // Generate coinList for coinAdapter
        getCoinList().let{ coinList ->

            if (coinList.isNotEmpty()) {
                coinAdapter = CoinAdapter(coinList)
                binding.hoardOverviewCoinageList.adapter = coinAdapter

                // Apply new values to coinage list footer
                binding.hoardOverviewCoinageFooter.apply {
                    val totalCoins = activeHoard.cp + activeHoard.sp + activeHoard.ep +
                            activeHoard.gp + activeHoard.hsp + activeHoard.pp
                    ("x${DecimalFormat("#,##0").format(totalCoins)}")
                        .also { hoardOverviewCoinageFooterColumnQty.text = it }
                    ("${DecimalFormat("#,##0.0#").format(totalCoins/ 10.0)
                        .removeSuffix(".0")} ib")
                        .also { hoardOverviewCoinageFooterColumnWt.text = it }
                    ("${
                        DecimalFormat("#,##0.0#")
                            .format(activeHoard.getTotalCoinageValue())
                            .removeSuffix(".0")} gp")
                        .also { hoardOverviewCoinageFooterColumnGp.text = it }
                }

                binding.hoardOverviewCoinageGroup.visibility = View.VISIBLE
            } else {
                // Hide coin group if hoard contains no coins
                binding.hoardOverviewCoinageGroup.visibility = View.GONE
            }
        }

        // Update unique item counts
        binding.apply{
            hoardOverviewGemQty.text = activeHoard.gemCount.toString()
            if (activeHoard.gemCount == 0) {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.gemPrimaryDesaturated,
                    requireContext().theme)

                hoardOverviewGemLayout.setBackgroundColor(cardBackgroundColor)

            } else {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.gemPrimary,
                    requireContext().theme)

                hoardOverviewGemLayout.setBackgroundColor(cardBackgroundColor)
            }
            hoardOverviewArtQty.text = activeHoard.artCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalArtValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewArtValue.text = it }
            if (activeHoard.artCount == 0) {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.artPrimaryDesaturated,
                    requireContext().theme)

                hoardOverviewArtLayout.setBackgroundColor(cardBackgroundColor)

            } else {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.artPrimary,
                    requireContext().theme)

                hoardOverviewArtLayout.setBackgroundColor(cardBackgroundColor)
            }
            hoardOverviewMagicQty.text = activeHoard.magicCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalMagicValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewMagicValue.text = it }
            if (activeHoard.magicCount == 0) {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.magicPrimaryDesaturated,
                    requireContext().theme)

                hoardOverviewMagicLayout.setBackgroundColor(cardBackgroundColor)

            } else {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.magicPrimary,
                    requireContext().theme)

                hoardOverviewMagicLayout.setBackgroundColor(cardBackgroundColor)
            }
            hoardOverviewSpellsQty.text = activeHoard.spellsCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalSpellValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewSpellsValue.text = it }
            if (activeHoard.spellsCount == 0) {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.spellPrimaryDesaturated,
                    requireContext().theme)

                hoardOverviewSpellsLayout.setBackgroundColor(cardBackgroundColor)

            } else {

                @ColorInt
                val cardBackgroundColor = resources.getColor(R.color.spellPrimary,
                    requireContext().theme)

                hoardOverviewSpellsLayout.setBackgroundColor(cardBackgroundColor)
            }
        }
    }

    fun updateDifficultyLabel() {

        val effortRating = activeHoard.effortRating

        ("${
            DecimalFormat("#0.0#")
                .format(effortRating)
                .removeSuffix(".0")} gp = 1 xp").also {
            binding.hoardOverviewExperienceRatio.text = it }

        binding.hoardOverviewExperienceDifficulty.apply{
            when {
                effortRating < 0.10  -> {
                    text= getString(R.string.difficulty_high_6)
                    setTextColor(resources.getColor(R.color.sanguine,context.theme))
                }
                effortRating >= 0.10 && effortRating < 1.00  -> {
                    text= getString(R.string.difficulty_high_5)
                    setTextColor(resources.getColor(R.color.cherry,context.theme))
                }
                effortRating >= 1.00 && effortRating < 2.00  -> {
                    text= getString(R.string.difficulty_high_4)
                    setTextColor(resources.getColor(R.color.scarlet,context.theme))
                }
                effortRating >= 2.00 && effortRating < 3.00  -> {
                    text= getString(R.string.difficulty_high_3)
                    setTextColor(resources.getColor(R.color.orange,context.theme))
                }
                effortRating >= 3.00 && effortRating < 4.00  -> {
                    text= getString(R.string.difficulty_high_2)
                    setTextColor(resources.getColor(R.color.gold,context.theme))
                }
                effortRating >= 4.00 && effortRating < 5.00  -> {
                    text= getString(R.string.difficulty_high_1)
                    setTextColor(resources.getColor(R.color.green,context.theme))
                }
                effortRating == 5.00  -> {
                    text= getString(R.string.difficulty_average)
                    setTextColor(resources.getColor(R.color.emerald,context.theme))
                }
                effortRating > 5.00 && effortRating < 6.00  -> {
                    text= getString(R.string.difficulty_low_1)
                    setTextColor(resources.getColor(R.color.turquoise,context.theme))
                }
                effortRating >= 6.00 && effortRating < 7.00  -> {
                    text= getString(R.string.difficulty_low_2)
                    setTextColor(resources.getColor(R.color.azure_blue,context.theme))
                }
                effortRating >= 7.00 && effortRating < 8.00  -> {
                    text= getString(R.string.difficulty_low_3)
                    setTextColor(resources.getColor(R.color.ultramarine,context.theme))
                }
                effortRating >= 8.00 && effortRating < 10.00  -> {
                    text= getString(R.string.difficulty_low_4)
                    setTextColor(resources.getColor(R.color.purple,context.theme))
                }
                effortRating >= 10.00 && effortRating < 15.00  -> {
                    text= getString(R.string.difficulty_low_5)
                    setTextColor(resources.getColor(R.color.plum,context.theme))
                }
                effortRating >= 15.00 && effortRating < 20.00  -> {
                    text= getString(R.string.difficulty_low_6)
                    setTextColor(resources.getColor(R.color.pewter,context.theme))
                }
                effortRating >= 7.00 && effortRating < 8.00  -> {
                    text= getString(R.string.difficulty_low_7)
                    setTextColor(resources.getColor(R.color.gray,context.theme))
                }
            }
        }
    }

    private fun getCoinList(): List<Pair<CoinType,Int>> {

        val coinArrayList = ArrayList<Pair<CoinType,Int>>()

        if (activeHoard.cp > 0) coinArrayList.add(CoinType.CP to activeHoard.cp)
        if (activeHoard.sp > 0) coinArrayList.add(CoinType.SP to activeHoard.sp)
        if (activeHoard.ep > 0) coinArrayList.add(CoinType.EP to activeHoard.ep)
        if (activeHoard.gp > 0) coinArrayList.add(CoinType.GP to activeHoard.gp)
        if (activeHoard.hsp > 0) coinArrayList.add(CoinType.HSP to activeHoard.hsp)
        if (activeHoard.pp > 0) coinArrayList.add(CoinType.PP to activeHoard.pp)

        return coinArrayList.toList()
    }
    // endregion
}