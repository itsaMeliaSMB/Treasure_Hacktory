package com.example.android.treasurefactory.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.treasurefactory.R

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), HoardListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = HoardGeneratorFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }
    }

    override fun onHoardSelected(selectedHoardID: Int) {
        val fragment = HoardViewerFragment.newInstance(selectedHoardID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)                   // See page 248 of BNR
            .commit()
    }
}