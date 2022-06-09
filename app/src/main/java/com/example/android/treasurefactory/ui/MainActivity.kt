package com.example.android.treasurefactory.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val action = HoardListFragmentDirections.actionHoardListFragmentToHoardViewerFragment().apply{
            selectedHoardID = hoardID
        }

        Log.d("MainActivity","$hoardID passed via " + resources.getResourceName(view.id) +
            " for fragment navigation.")
        Toast.makeText(this,"Hoard [id: $hoardID] selected.",Toast.LENGTH_SHORT).show()
        // TODO view.findNavController().navigate(action)
        /* Dummied out until new implementation confirmed to work.
        val fragment = HoardViewerFragment.newInstance(hoardID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)                   // See page 248 of BNR
            .commit() */
    }
}