package com.bluemeth.simbank.src.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityHomeBinding
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val creditCardViewModel: CreditCardViewModel by viewModels()

    companion object {
        fun create(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityHomeBinding
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)

        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.homeFragment || destination.id == R.id.functionsFragment || destination.id == R.id.profileFragment || destination.id == R.id.cardFragment) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE

            }
        }
        builder = AlertDialog.Builder(this)
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initObservers()
        iconDrawer()
        itemMenu()

        saveUserIban()
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
        val navDrawer = binding.navView.menu
        navDrawer.findItem(R.id.profileFragment)
        navDrawer.findItem(R.id.settingsFragment)
        navDrawer.findItem(R.id.privacyPolicyFragment)
        navDrawer.findItem(R.id.signOut).setOnMenuItemClickListener {
            logOut()
            true
        }
    }

    private fun logOut(){
        builder.setTitle("¡Vaya!")
            .setMessage("Estas seguro de que quieres salir?")
            .setCancelable(true)
            .setPositiveButton("Si"){ _, it ->
                homeViewModel.onLogoutSelected()
            }.setNegativeButton("No"){dialogInterface, it ->
                dialogInterface.cancel()
            }.setNeutralButton("Ayuda"){_, it ->
                Toast.makeText(this,"Estas apunto de cerrar tu sesion, necesitarás volver a iniciar sesión" , Toast.LENGTH_SHORT).show()
            }
        builder.show()
    }

    private fun initObservers() {
        homeViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }
    }

    private fun saveUserIban() {
        creditCardViewModel.getBankAccount().observe(this) { bankAccount ->
            prefs.saveUserIban(bankAccount.iban)
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

}