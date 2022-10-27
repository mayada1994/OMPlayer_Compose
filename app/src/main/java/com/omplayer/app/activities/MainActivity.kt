package com.omplayer.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.omplayer.app.R
import com.omplayer.app.databinding.ActivityMainBinding
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.BaseViewModel.BaseViewEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root)
    }

    fun handleBaseEvent(event: ViewEvent) {
        when (event) {
            is BaseViewEvent.Navigate -> navController.navigate(event.navDirections)
        }
    }
}