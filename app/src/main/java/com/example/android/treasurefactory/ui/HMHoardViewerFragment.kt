package com.example.android.treasurefactory.ui

import android.os.Bundle
import androidx.fragment.app.Fragment

private const val ARG_HOARD_ID = "hoard_id"

class HMHoardViewerFragment : Fragment() {

    //override fun onCreateView() {inflater: LayoutInflater, container: ViewGroup?, savedInstanceState?: Bundle}
    //override fun onStart() {}

    companion object {

        // Call this instead of calling the constructor directly
        fun newInstance(hoardID: Int): HMHoardViewerFragment {
            val args = Bundle().apply{
                putSerializable(ARG_HOARD_ID, hoardID)
            }
            return HMHoardViewerFragment().apply{
                arguments = args
            }
        }
    }
}