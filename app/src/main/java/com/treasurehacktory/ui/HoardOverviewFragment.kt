package com.treasurehacktory.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.treasurehacktory.LootMutator
import com.treasurehacktory.R
import com.treasurehacktory.TreasureHacktoryApplication
import com.treasurehacktory.capitalized
import com.treasurehacktory.databinding.HoardOverviewCoinageListItemBinding
import com.treasurehacktory.databinding.LayoutHoardOverviewBinding
import com.treasurehacktory.model.*
import com.treasurehacktory.viewmodel.HoardOverviewViewModel
import com.treasurehacktory.viewmodel.HoardOverviewViewModelFactory
import java.io.BufferedWriter
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import kotlin.io.path.bufferedWriter

class HoardOverviewFragment : Fragment() {

    // region [ Property declarations ]
    private lateinit var activeHoard: Hoard

    private var isNowFavorite = false

    private val safeArgs : HoardOverviewFragmentArgs by navArgs()

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

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimaryDark,typedValue,true)
        @ColorInt
        val newStatusBarColor = typedValue.data

        requireActivity().window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = newStatusBarColor
        }

        // Prepare the recycler view
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

            totalXPValue = liveXPTotal

            // Update value in detail view
            (NumberFormat.getNumberInstance().format(totalXPValue) +
                    " xp").also { binding.hoardOverviewExperienceInfo.text = it }
            updateDifficultyLabel()
        }

        hoardOverviewViewModel.reportInfoLiveData.observe(viewLifecycleOwner) { reportInfo ->

            if (reportInfo != null) {

                val fileName = "hoard_overview_report"
                sendCSVReport(reportInfo, fileName)

                hoardOverviewViewModel.clearReportInfo()
            }

        }
        // endregion

        // region [ Toolbar ]
        binding.hoardOverviewToolbar.apply {

            // Get themed color attribute for Toolbar's title
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorOnPrimary,typedValue,true)
            @ColorInt
            val colorOnPrimary = typedValue.data

            inflateMenu(R.menu.hoard_overview_toolbar_menu)
            title = getString(R.string.hoard_overview_fragment_title)
            setTitleTextColor(colorOnPrimary)
            setSubtitleTextColor(colorOnPrimary)
            navigationIcon?.apply {
                R.drawable.clipart_back_vector_icon
                setTint(colorOnPrimary)
                visibility = View.VISIBLE
            }
            overflowIcon?.apply{
                setTint(colorOnPrimary)
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.action_edit_hoard      -> {

                        val action =
                            HoardOverviewFragmentDirections.hoardOverviewEditBottomDialogAction(
                                activeHoard.hoardID
                            )
                        findNavController().navigate(action)

                        true
                    }

                    R.id.action_csv_report      -> {

                        val dialog = AlertDialog.Builder(requireContext()).setTitle("Generate & Share CSV Report")
                            .setMessage("This will generate an overview report of this treasure hoard. Proceed?\n\n" +
                                    "\t- The report will not be stored here after it is shared, so be sure to save it where you can view it later.\n" +
                                    "\t- If you are having trouble viewing the report, the item delimiter is \"|\" (U+007C) and the line separator is \"\\r\".")
                            .setPositiveButton(R.string.generate) { dialog, _ ->

                                Toast.makeText(context,"Generating report (This may take a moment)...", Toast.LENGTH_LONG).show()

                                hoardOverviewViewModel.fetchReportInfo(activeHoard.hoardID)

                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                            .create()

                        dialog.show()

                        true
                    }

                    R.id.action_view_history    -> {

                        val action =
                            HoardOverviewFragmentDirections.hoardOverviewToEventLogAction(
                                activeHoard.hoardID
                            )
                        findNavController().navigate(action)

                        true
                    }

                    R.id.action_edit_coinage    -> {

                        val action =
                            HoardOverviewFragmentDirections.hoardOverviewToCoinageDialogAction(
                                activeHoard.hoardID
                            )
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

        binding.hoardOverviewFavCheckbox.setOnCheckedChangeListener { _, isChecked ->

            isNowFavorite = isChecked
        }

        binding.hoardOverviewGemCard.setOnClickListener {
            val action =
                HoardOverviewFragmentDirections.hoardOverviewToUniqueListAction(
                    activeHoard.hoardID,
                    UniqueItemType.GEM
                )

            findNavController().navigate(action)
        }

        binding.hoardOverviewArtCard.setOnClickListener {
            val action =
                HoardOverviewFragmentDirections.hoardOverviewToUniqueListAction(
                    activeHoard.hoardID,
                    UniqueItemType.ART_OBJECT
                )

            findNavController().navigate(action)
        }

        binding.hoardOverviewMagicCard.setOnClickListener {
            val action =
                HoardOverviewFragmentDirections.hoardOverviewToUniqueListAction(
                    activeHoard.hoardID,
                    UniqueItemType.MAGIC_ITEM
                )

            findNavController().navigate(action)
        }

        binding.hoardOverviewSpellsCard.setOnClickListener {
            val action =
                HoardOverviewFragmentDirections.hoardOverviewToUniqueListAction(
                    activeHoard.hoardID, UniqueItemType.SPELL_COLLECTION
                )

            findNavController().navigate(action)
        }
    }

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
                    .removeSuffix(".0")} lb")
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

        // Set icon for hoard thumbnail
        try {

            binding.hoardOverviewItemframeIcon
                .setImageResource(resources
                    .getIdentifier(activeHoard.iconID,"drawable",view?.context?.packageName))

        } catch (e: Exception) {

            binding.hoardOverviewItemframeIcon
                .setImageResource(R.drawable.clipart_default_image)
        }

        // Set badge on hoard thumbnail
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

        // Update general information
        binding.apply{

            hoardOverviewNameLabel.text = activeHoard.name
            hoardOverviewDateInfo.text = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                .format(activeHoard.creationDate)
            "# ${activeHoard.hoardID}".also { hoardOverviewIdInfo.text = it }
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
                        .removeSuffix(".0")} lb")
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

    private fun updateDifficultyLabel() {

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
                effortRating >= 20.00  -> {
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

    /**
     * Attempts to generate a csv file that gives an overview of the given hoard and, if successful,
     * share it via a simple implicit send intent.
     */
    @SuppressLint("SimpleDateFormat")
    fun sendCSVReport(hoardBundle: Triple<Hoard, HoardUniqueItemBundle,List<HoardEvent>>,
                      fileName: String = "hoard_report") {

        try {

            val fileExtension = "csv"
            val fileUri = generateCSVFile(hoardBundle,fileName,fileExtension)

            if (fileUri != null) {

                val shareIntent = Intent().apply{
                    action = Intent.ACTION_SEND
                    setTypeAndNormalize("text/csv")
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent,"Share report using:"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  Takes a triple of a [Hoard], its [unique items][HoardUniqueItemBundle], and list of relevant
     *  [hoard events][HoardEvent], and writes a simple report as temporary csv file.
     *
     *  @return The [Uri] of the newly-generated csv file.
     */
    @SuppressLint("SimpleDateFormat")
    private fun generateCSVFile(hoardBundle: Triple<Hoard, HoardUniqueItemBundle, List<HoardEvent>>,
                                fileName: String, fileExtension: String): Uri? {

        val tempDir = requireContext().cacheDir

        // Delete any temp files in directory first
        tempDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            }
        }

        val filePath = kotlin.io.path.createTempFile(tempDir.toPath(), fileName, ".$fileExtension")

        val writer: BufferedWriter?

        try {

            writer = filePath.bufferedWriter()

            hoardBundle.let { (hoard, itemBundle, _) ->

                // region [ Simplified write functions ]
                fun List<String>.writeAsRow() {
                    writer.append(this.joinToString(separator = "|", postfix = "\r").replace("’","\'"))
                }

                fun List<List<String>>.writeAsRows() {
                    this.forEach { rowList -> rowList.writeAsRow() }
                }

                fun writeEmptyRow() {
                    writer.append("\r")
                }
                // endregion

                var grandGPTotal = 0.0
                var grandXPTotal = 0

                fun getDifficultyAsString() : String = when {
                    hoard.effortRating < 0.10  -> getString(R.string.difficulty_high_6)
                    hoard.effortRating >= 0.10 && hoard.effortRating < 1.00  -> getString(R.string.difficulty_high_5)
                    hoard.effortRating >= 1.00 && hoard.effortRating < 2.00  -> getString(R.string.difficulty_high_4)
                    hoard.effortRating >= 2.00 && hoard.effortRating < 3.00  -> getString(R.string.difficulty_high_3)
                    hoard.effortRating >= 3.00 && hoard.effortRating < 4.00  -> getString(R.string.difficulty_high_2)
                    hoard.effortRating >= 4.00 && hoard.effortRating < 5.00  -> getString(R.string.difficulty_high_1)
                    hoard.effortRating == 5.00  -> getString(R.string.difficulty_average)
                    hoard.effortRating > 5.00 && hoard.effortRating < 6.00  -> getString(R.string.difficulty_low_1)
                    hoard.effortRating >= 6.00 && hoard.effortRating < 7.00  -> getString(R.string.difficulty_low_2)
                    hoard.effortRating >= 7.00 && hoard.effortRating < 8.00  -> getString(R.string.difficulty_low_3)
                    hoard.effortRating >= 8.00 && hoard.effortRating < 10.00  -> getString(R.string.difficulty_low_4)
                    hoard.effortRating >= 10.00 && hoard.effortRating < 15.00  -> getString(R.string.difficulty_low_5)
                    hoard.effortRating >= 15.00 && hoard.effortRating < 20.00  -> getString(R.string.difficulty_low_6)
                    hoard.effortRating >= 20.00 -> getString(R.string.difficulty_low_7)
                    else -> "???"
                }

                // region [ Treasure category compiling functions ]

                fun compileCoinageForCSV(): List<List<String>> {
                    val gpTotal = hoard.getTotalCoinageValue()
                    val xpTotal = (hoard.getTotalCoinageValue() / hoard.effortRating).toInt()
                    val countTotal =
                        hoard.cp + hoard.sp + hoard.ep + hoard.gp + hoard.hsp + hoard.pp
                    val outputList = arrayListOf(
                        listOf(
                            "Abbrev.",
                            "Coinage by denomination",
                            "Total in GP",
                            "Total in XP",
                            "Count",
                            "Weight (in lb)",
                            "% By Value",
                            "% By Count"
                        )
                    )

                    fun addCoinCountToRowList(type: CoinType, count: Int) {
                        val coinList = arrayListOf(type.name.lowercase())
                        val subGpTotal = count * type.gpValue
                        val subXpTotal = (subGpTotal / hoard.effortRating).toInt()

                        coinList.add(type.longName)
                        coinList.add(
                            DecimalFormat("#,##0.0#")
                                .format(subGpTotal)
                                .removeSuffix(".0")
                        )
                        coinList.add(NumberFormat.getNumberInstance().format(subXpTotal))
                        coinList.add(NumberFormat.getNumberInstance().format(count))
                        coinList.add(
                            (DecimalFormat("#,##0.0#")
                                .format(count / 10.0)
                                .removeSuffix(".0")) + " lb"
                        )
                        coinList.add(
                            (DecimalFormat("##0.0#")
                                .format((subGpTotal / gpTotal ) * 100.0)
                                .removeSuffix(".0"))
                        )
                        coinList.add(
                            (DecimalFormat("##0.0#")
                                .format((count.toDouble() / countTotal.toDouble()) * 100.0)
                                .removeSuffix(".0"))
                        )

                        outputList.add(coinList.toList())
                    }

                    if (hoard.cp > 0) {
                        addCoinCountToRowList(CoinType.CP, hoard.cp)
                    }
                    if (hoard.sp > 0) {
                        addCoinCountToRowList(CoinType.SP, hoard.sp)
                    }
                    if (hoard.ep > 0) {
                        addCoinCountToRowList(CoinType.EP, hoard.ep)
                    }
                    if (hoard.gp > 0) {
                        addCoinCountToRowList(CoinType.GP, hoard.gp)
                    }
                    if (hoard.hsp > 0) {
                        addCoinCountToRowList(CoinType.HSP, hoard.hsp)
                    }
                    if (hoard.pp > 0) {
                        addCoinCountToRowList(CoinType.PP, hoard.pp)
                    }

                    grandGPTotal += gpTotal
                    grandXPTotal += xpTotal

                    outputList.add(
                        listOf(
                            "- - -", "COINAGE TOTAL",
                            (DecimalFormat("#,##0.0#").format(gpTotal).removeSuffix(".0")),
                            NumberFormat.getNumberInstance().format(xpTotal),
                            "- - -", "- - -", "- - -", "- - -"
                        )
                    )

                    return outputList.toList()
                }

                fun MagicItem.getShortTableName(): String {
                    return when (this.typeOfItem) {
                        MagicItemType.A2 -> "Potion"
                        MagicItemType.A3 -> "Scroll"
                        MagicItemType.A4 -> "Ring"
                        MagicItemType.A5 -> "Rod"
                        MagicItemType.A6 -> "Staff"
                        MagicItemType.A7 -> "Wand"
                        MagicItemType.A8 -> "Book"
                        MagicItemType.A9 -> "Jewelry"
                        MagicItemType.A10 -> "Robe, etc."
                        MagicItemType.A11 -> "Boots,etc."
                        MagicItemType.A12 -> "Hat, etc."
                        MagicItemType.A13 -> "Container"
                        MagicItemType.A14 -> "Dust, etc."
                        MagicItemType.A15 -> "Tools A15"
                        MagicItemType.A16 -> "Musical"
                        MagicItemType.A17 -> "Odd Stuff"
                        MagicItemType.A18 -> "Armor"
                        MagicItemType.A20 -> "Sp.Armor"
                        MagicItemType.A21 -> "Weapon"
                        MagicItemType.A23 -> "Sp.Weap."
                        MagicItemType.A24 -> "Artifact"
                        MagicItemType.Map -> "Tr. Map"
                        MagicItemType.Mundane -> "Mundane"
                    }
                }

                fun List<Gem>.compileForCSV(): List<List<String>> {

                    var gpTotal = 0.0
                    var xpTotal = 0
                    val outputList = arrayListOf(
                        listOf(
                            "ID",
                            "Gemstone / Jewel",
                            "GP Value",
                            "XP Value",
                            "Stone Type",
                            "Size",
                            "Quality",
                            "Original GP Value"
                        )
                    )

                    this.forEach { item ->
                        val itemList = arrayListOf(item.gemID.toString())

                        itemList.add(
                            item.name
                        )
                        item.currentGPValue.let { itemGPV ->
                            gpTotal += itemGPV
                            itemList.add(
                                DecimalFormat("#,##0.0#")
                                    .format(itemGPV)
                                    .removeSuffix(".0")
                            )
                        }

                        ((item.currentGPValue / hoard.effortRating).toInt()).let { itemXPV ->
                            xpTotal += itemXPV
                            itemList.add((NumberFormat.getNumberInstance().format(itemXPV)))
                        }
                        itemList.add(item.getTypeAsString().capitalized())
                        itemList.add(item.getSizeAsString().capitalized())
                        itemList.add(item.getQualityAsString().capitalized())
                        itemList.add(
                            DecimalFormat("#,##0.0#")
                                .format(LootMutator.convertGemValueToGP(item.getDefaultBaseValue()))
                                .removeSuffix(".0")
                        )

                        outputList.add(itemList.toList())
                    }

                    outputList.add(
                        listOf(
                            "- - -", "GEM TOTAL",
                            (DecimalFormat("#,##0.0#").format(gpTotal).removeSuffix(".0")),
                            NumberFormat.getNumberInstance().format(xpTotal),
                            "- - -", "- - -", "- - -", "- - -"
                        )
                    )

                    grandGPTotal += gpTotal
                    grandXPTotal += xpTotal

                    return outputList.toList()
                }

                fun List<ArtObject>.compileForCSV(): List<List<String>> {

                    var gpTotal = 0.0
                    var xpTotal = 0
                    val outputList = arrayListOf(
                        listOf(
                            "ID",
                            "Art Object",
                            "GP Value",
                            "XP Value",
                            "Medium",
                            "Size",
                            "Subject",
                            "Authenticity"
                        )
                    )

                    this.forEach { item ->
                        val itemList = arrayListOf(item.artID.toString())

                        itemList.add(
                            item.name
                        )
                        item.gpValue.let { itemGPV ->
                            gpTotal += itemGPV
                            itemList.add(
                                DecimalFormat("#,##0.0#")
                                    .format(itemGPV)
                                    .removeSuffix(".0")
                            )
                        }

                        ((item.gpValue / hoard.effortRating).toInt()).let { itemXPV ->
                            xpTotal += itemXPV
                            itemList.add((NumberFormat.getNumberInstance().format(itemXPV)))
                        }
                        itemList.add(item.getArtTypeAsString().capitalized())
                        itemList.add(item.getSizeAsString().capitalized())
                        itemList.add(item.getSubjectAsString().capitalized())
                        itemList.add(if (item.isForgery) "Forgery" else "Genuine")

                        outputList.add(itemList.toList())
                    }

                    outputList.add(
                        listOf(
                            "- - -", "ART OBJECT TOTAL",
                            (DecimalFormat("#,##0.0#").format(gpTotal).removeSuffix(".0")),
                            NumberFormat.getNumberInstance().format(xpTotal),
                            "- - -", "- - -", "- - -", "- - -"
                        )
                    )

                    grandGPTotal += gpTotal
                    grandXPTotal += xpTotal

                    return outputList.toList()
                }

                fun List<MagicItem>.compileForCSV(): List<List<String>> {

                    var gpTotal = 0.0
                    var xpTotal = 0
                    val outputList = arrayListOf(
                        listOf(
                            "ID",
                            "Magic Item",
                            "GP Value",
                            "XP Value",
                            "Table Type",
                            "TrH Identifier",
                            "Source",
                            "Cursed?"
                        )
                    )

                    this.forEach { item ->
                        val itemList = arrayListOf(item.mItemID.toString())

                        itemList.add(
                            item.name
                        )
                        item.gpValue.let { itemGPV ->
                            gpTotal += itemGPV
                            itemList.add(
                                DecimalFormat("#,##0.0#")
                                    .format(itemGPV)
                                    .removeSuffix(".0")
                            )
                        }

                        item.xpValue.let { itemXPV ->
                            xpTotal += itemXPV
                            itemList.add((NumberFormat.getNumberInstance().format(itemXPV)))
                        }
                        itemList.add(item.typeOfItem.name + "(${item.getShortTableName()})")
                        itemList.add("#${item.templateID}")
                        itemList.add(item.sourceText + ", pg ${item.sourcePage}")
                        itemList.add(if (item.isCursed) "YES" else "")

                        outputList.add(itemList.toList())
                    }

                    outputList.add(
                        listOf(
                            "- - -", "MAGIC ITEM TOTAL",
                            (DecimalFormat("#,##0.0#").format(gpTotal).removeSuffix(".0")),
                            NumberFormat.getNumberInstance().format(xpTotal),
                            "- - -", "- - -", "- - -", "- - -"
                        )
                    )

                    grandGPTotal += gpTotal
                    grandXPTotal += xpTotal

                    return outputList.toList()
                }

                fun List<SpellCollection>.compileForCSV(): List<List<String>> {

                    var gpTotal = 0.0
                    var xpTotal = 0
                    val outputList = arrayListOf(
                        listOf(
                            "ID",
                            "Spell Collection",
                            "GP Value",
                            "XP Value",
                            "Collection Type",
                            "Discipline",
                            "Spell Count",
                            "Cursed?"
                        )
                    )

                    this.forEach { item ->
                        val itemList = arrayListOf(item.sCollectID.toString())

                        itemList.add(
                            item.name
                        )
                        item.gpValue.let { itemGPV ->
                            gpTotal += itemGPV
                            itemList.add(
                                DecimalFormat("#,##0.0#")
                                    .format(itemGPV)
                                    .removeSuffix(".0")
                            )
                        }

                        item.xpValue.let { itemXPV ->
                            xpTotal += itemXPV
                            itemList.add((NumberFormat.getNumberInstance().format(itemXPV)))
                        }
                        itemList.add(
                            when (item.type) {
                                SpCoType.SCROLL -> "Spell Scroll"
                                SpCoType.BOOK -> "Spell Book"
                                SpCoType.ALLOTMENT -> "Chosen One Allotment"
                                SpCoType.RING -> "Ring of Spell Storing"
                                SpCoType.OTHER -> "? ? ?"
                            }
                        )
                        itemList.add(item.discipline.name.lowercase().capitalized())
                        itemList.add(item.spells.size.toString())
                        itemList.add(if (item.curse.isNotEmpty()) "YES" else "")

                        outputList.add(itemList.toList())
                    }

                    outputList.add(
                        listOf(
                            "- - -", "SPELL COLLECTION TOTAL",
                            (DecimalFormat("#,##0.0#").format(gpTotal).removeSuffix(".0")),
                            NumberFormat.getNumberInstance().format(xpTotal),
                            "- - -", "- - -", "- - -", "- - -"
                        )
                    )

                    grandGPTotal += gpTotal
                    grandXPTotal += xpTotal

                    return outputList.toList()
                }
                // endregion

                // Write the temp file
                writer.use {

                    writer.appendLine("sep=|")
                    listOf("", hoard.name, "[id:${hoard.hoardID}]").writeAsRow()
                    writeEmptyRow()
                    listOf(
                        "", "Creation date / time:",
                        SimpleDateFormat("MM/dd/yyyy")
                            .format(hoard.creationDate),
                        SimpleDateFormat("hh:mm:ss aaa z")
                            .format(hoard.creationDate)
                    ).writeAsRow()
                    listOf(
                        "", "Acquisition difficulty:", String.format("%.2f", hoard.effortRating),
                        "gp to 1 xp"
                    ).writeAsRow()
                    listOf(
                        "","",getDifficultyAsString()
                    ).writeAsRow()
                    writeEmptyRow()
                    listOf("[ ! ]", "PLEASE NOTE:").writeAsRow()
                    listOf("[ ! ]", getString(R.string.csv_no_formula_note)).writeAsRow()
                    listOf("[ ! ]", getString(R.string.csv_no_detail_note)).writeAsRow()
                    writeEmptyRow()

                    // Write coinage table
                    compileCoinageForCSV().writeAsRows()
                    writeEmptyRow()

                    // Write unique item table(s)
                    itemBundle.hoardGems.let { gems ->
                        if (gems.isNotEmpty()) {
                            gems.compileForCSV().writeAsRows()
                            writeEmptyRow()
                        }
                    }
                    itemBundle.hoardArt.let { artObjects ->
                        if (artObjects.isNotEmpty()) {
                            artObjects.compileForCSV().writeAsRows()
                            writeEmptyRow()
                        }
                    }
                    itemBundle.hoardItems.let { magicItems ->
                        if (magicItems.isNotEmpty()) {
                            magicItems.compileForCSV().writeAsRows()
                            writeEmptyRow()
                        }
                    }
                    itemBundle.hoardSpellCollections.let { spCos ->
                        if (spCos.isNotEmpty()) {
                            spCos.compileForCSV().writeAsRows()
                            writeEmptyRow()
                        }
                    }

                    // Write grand total
                    listOf(
                        "- - -", "HOARD GRAND TOTAL",
                        (DecimalFormat("#,##0.0#").format(grandGPTotal).removeSuffix(".0")),
                        NumberFormat.getNumberInstance().format(grandXPTotal),
                        "- - -", "- - -", "- - -", "- - -"
                    ).writeAsRow()
                }
            }

            val newFile = filePath.toFile()

            return FileProvider.getUriForFile(requireContext(),
                "com.treasurehacktory.fileprovider", newFile)

        } catch (e: IOException) {
            Toast.makeText(
                requireContext(),
                "Something went wrong when generating $fileName$fileExtension.",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()

            return null
        }
    }
    // endregion
}