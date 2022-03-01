package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.treasurefactory.R
import com.example.android.treasurefactory.model.Hoard
import com.example.android.treasurefactory.viewmodel.HoardDetailsViewModel

private const val ARG_HOARD_ID = "hoard_id"

class HoardViewerFragment : Fragment() {

    private lateinit var activeHoard: Hoard

    private val hoardDetailsViewModel: HoardDetailsViewModel by lazy {

        ViewModelProvider(this).get(HoardDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeHoard = Hoard()

        val activeHoardID: Int = arguments?.getSerializable(ARG_HOARD_ID) as Int

        hoardDetailsViewModel.loadHoard(activeHoardID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.layout_hoard_overview,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hoardDetailsViewModel.hoardLiveData.observe(viewLifecycleOwner, { hoard ->

                hoard?.let { //TODO Confirm, based on BNR pg 254, that this is correct
                    this.activeHoard = hoard
                    updateUI()
                }
            })
    }

    // NOTE TO SELF: Runs when back button is pressed or app is removed from active view
    override fun onStop() {
        super.onStop()
        hoardDetailsViewModel.saveHoard(activeHoard)
    }

    fun updateUI() {

        //TODO add UI update functions
    }

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