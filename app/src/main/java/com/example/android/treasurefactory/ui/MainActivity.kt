package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android.treasurefactory.R

private const val TAG = "MainActivity"

// Note the Callbacks implementation in MainActivity class

class MainActivity : AppCompatActivity(), HoardListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = HoardListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }
    }

    override fun onHoardSelected(view: View, hoardID: Int) {
        val action =
            HoardListFragmentDirections.hoardListToOverviewAction(hoardID)

        view.findNavController().navigate(action)
    }

}