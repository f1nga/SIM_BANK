package com.bluemeth.simbank.src.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityMainBinding
import com.bluemeth.simbank.src.ui.auth.signin.SignInActivity

class HomeActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val navController = this.findNavController(R.id.myNavHostFragment)

        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.homeFragment || destination.id == R.id.functionsFragment || destination.id == R.id.profileFragment || destination.id == R.id.cardFragment) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE

            }
        }

        NavigationUI.setupActionBarWithNavController(this,navController)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

}