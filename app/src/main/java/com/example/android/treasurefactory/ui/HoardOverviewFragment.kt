package com.example.android.treasurefactory.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.TreasureHacktoryApplication
import com.example.android.treasurefactory.databinding.HoardOverviewCoinageListItemBinding
import com.example.android.treasurefactory.databinding.LayoutHoardOverviewBinding
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardOverviewViewModel
import com.example.android.treasurefactory.viewmodel.HoardOverviewViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat

private const val ARG_HOARD_ID = "hoard_id"

class HoardOverviewFragment : Fragment() {

    // region [ Property declarations ]
    private lateinit var activeHoard: Hoard

    private var totalGemValue = 0.0
    private var totalArtValue = 0.0
    private var totalMagicValue = 0.0
    private var totalSpellValue = 0.0

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

        val activeHoardID: Int = arguments?.getSerializable(ARG_HOARD_ID) as Int

        hoardOverviewViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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

        hoardOverviewViewModel.hoardLiveData.observe(viewLifecycleOwner) { hoard ->

            hoard?.let {
                activeHoard = hoard
                hoardOverviewViewModel.getTotalItemValues(hoard.hoardID).let{
                    totalGemValue = it[0]
                    totalArtValue = it[1]
                    totalMagicValue = it[2]
                    totalSpellValue = it[3]
                }
                updateUI()
            }
        }
    }

    // NOTE TO SELF: Runs when back button is pressed or app is removed from active view
    override fun onStop() {
        super.onStop()
        hoardOverviewViewModel.saveHoard(activeHoard)
    }
    // endregion

    // region [ Inner classes ]
    private enum class CoinType(val longName: String, val gpValue: Double) {
        CP("Copper pieces",0.01),
        SP("Silver pieces", 0.1),
        EP("Electrum pieces",0.5),
        GP("Gold pieces",1.0),
        HSP("Hard silver pieces",2.0),
        PP("Platinum pieces", 5.0)
    }

    private inner class CoinViewHolder(val binding: HoardOverviewCoinageListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinType: CoinType, coinCount: Int){

            // Set coin icon
            binding.hoardOverviewCoinageItemIcon.apply{
                when (coinType) {
                    CoinType.CP ->  this.setImageResource(R.drawable.item_coin_copper)
                    CoinType.SP ->  this.setImageResource(R.drawable.item_coin_silver)
                    CoinType.EP ->  this.setImageResource(R.drawable.item_coin_electrum)
                    CoinType.GP ->  this.setImageResource(R.drawable.item_coin_gold)
                    CoinType.HSP -> this.setImageResource(R.drawable.item_coin_hardsilver)
                    CoinType.PP ->  this.setImageResource(R.drawable.item_coin_platinum)
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

        //TODO left off here. Base, untested implementation of HoardEventLogFragment finished. Now,
        // finish HoardOverviewFragment implementation. Update nav graph, implement dummied-out
        // menu on toolbar, and test before moving onto written refactor checklist.

        //TODO Update header information

        // Set icon for hoard
        try {

            binding.hoardOverviewItemframeIcon
                .setImageResource(resources
                    .getIdentifier(activeHoard.iconID,"drawable",view?.context?.packageName))

        } catch (e: Exception) {

            binding.hoardOverviewItemframeIcon
                .setImageResource(R.drawable.clipart_default_image)
        }

        binding.apply{

            hoardOverviewNameLabel.text = activeHoard.name
            hoardOverviewDateInfo.text = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss aaa z")
                .format(activeHoard.creationDate)
            hoardOverviewTypeInfo.text = activeHoard.creationDesc
            ("Worth ${DecimalFormat("#,##0.0#")
                .format(activeHoard.gpTotal)
                .removeSuffix(".0")} gp").also { binding.hoardOverviewTypeInfo.text = it }
            //TODO update favorite status when checkbox changes
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
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalGemValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewGemValue.text = it }
            if (activeHoard.gemCount == 0) {
                //TODO update the color of the card of the background if there's an empty list
            }
            hoardOverviewArtQty.text = activeHoard.artCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalArtValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewArtValue.text = it }
            if (activeHoard.artCount == 0) {
                //TODO update the color of the card of the background if there's an empty list
            }
            hoardOverviewGemQty.text = activeHoard.magicCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalMagicValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewMagicValue.text = it }
            if (activeHoard.magicCount == 0) {
                //TODO update the color of the card of the background if there's an empty list
            }
            hoardOverviewSpellsQty.text = activeHoard.spellsCount.toString()
            ("Total Value: ${DecimalFormat("#,##0.0#")
                .format(totalSpellValue)
                .removeSuffix(".0")} gp").also{ hoardOverviewSpellsValue.text = it }
            if (activeHoard.spellsCount == 0) {
                //TODO update the color of the card of the background if there's an empty list
            }
        }

        //TODO left off here. Continue building Overview fragment, and don't forget to update navigation accordingly.
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

    companion object {

        // Call this instead of calling the constructor directly
        fun newInstance(hoardID: Int): HoardViewerFragment {

            val args = Bundle().apply{
                putSerializable(ARG_HOARD_ID, hoardID)
            }

            return HoardViewerFragment().apply{
                arguments = args
            }
        }
    }
}