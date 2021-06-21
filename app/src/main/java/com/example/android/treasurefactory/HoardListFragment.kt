package com.example.android.treasurefactory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

private const val TAG = "HoardListFragment"

class HoardListFragment : Fragment() {

    private val hoardListViewModel: HoardListViewModel by lazy {
        ViewModelProvider(this).get(HoardListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    companion object{
        fun newInstance(): HoardListFragment {
            return HoardListFragment()
        }
    }
}