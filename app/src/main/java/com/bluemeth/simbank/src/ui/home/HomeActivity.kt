package com.bluemeth.simbank.src.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityMainBinding
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    companion object {
        fun create(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)

        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.homeFragment || destination.id == R.id.functionsFragment || destination.id == R.id.profileFragment || destination.id == R.id.cardFragment) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE

            }
        }

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initObservers()
        iconDrawer()
        itemMenu()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    private fun iconDrawer(){
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_account_circle_24)
    }

    private fun itemMenu(){
        binding.navView.menu.findItem(R.id.signOut).setOnMenuItemClickListener {
            homeViewModel.onLogoutSelected()
            true
        }
    }

    private fun initObservers() {
        homeViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

}