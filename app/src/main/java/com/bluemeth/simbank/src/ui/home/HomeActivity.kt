package com.bluemeth.simbank.src.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityHomeBinding
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val creditCardViewModel: CreditCardViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    companion object {
        fun create(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = this.findNavController(R.id.myNavHostFragment)

        drawerLayout = binding.drawerLayout

        toolbar = binding.toolbar

        hideBottomBar()
        setHeaderDrawer()
        setCustomToolbar()
        setUserNameInToolbar()

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initObservers()
        //iconDrawer()
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

        navDrawer.findItem(R.id.drawer_header)
        navDrawer.findItem(R.id.settingsFragment).setOnMenuItemClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            navController.navigate(R.id.settingsFragment)
            true
        }
        navDrawer.findItem(R.id.privacyPolicyFragment).setOnMenuItemClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            navController.navigate(R.id.privacyPolicyFragment)
            true
        }
        navDrawer.findItem(R.id.signOut).setOnMenuItemClickListener {
            logOut()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
            return true
        }
         return true
    }

    private fun logOut(){
        val builder = AlertDialog.Builder(this)

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

    private fun hideBottomBar() {
        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.homeFragment || destination.id == R.id.functionsFragment || destination.id == R.id.profileFragment || destination.id == R.id.cardFragment) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE

            }
        }

    }

    private fun setHeaderDrawer() {
        val navView = findViewById<NavigationView>(R.id.navView)
        val headerView: View = layoutInflater.inflate(R.layout.drawer_header, navView, false)
        navView.addHeaderView(headerView)

        headerView.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setUserNameInToolbar() {
        homeViewModel.getUserName().observe(this) {
            binding.tvNameBar.text = "Hola, ${homeViewModel.splitName(it.name)}"
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