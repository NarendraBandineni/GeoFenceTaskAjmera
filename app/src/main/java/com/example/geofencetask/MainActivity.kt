package com.example.geofencetask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.geofencetask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null
    private val fragment: MapsFragment by lazy { MapsFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager?.beginTransaction()

        fragmentTransaction?.apply {
            add(binding.fragmentContainerView.id, fragment)
            addToBackStack(fragment.javaClass.simpleName)
            commit();
        }

    }
}