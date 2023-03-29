package com.bluemeth.simbank.src.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

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

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initObservers()
        itemMenu()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.right_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsIcon -> navController.navigate(R.id.settingsFragment)
            R.id.notificationsIcon -> navController.navigate(R.id.notificationsFragment)
            android.R.id.home -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun itemMenu(){
        val navDrawer = binding.navView.menu

        navDrawer.findItem(R.id.signOut).setOnMenuItemClickListener {
            showQuestionDialog()
            true
        }

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun showQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_oops),
            description = getString(R.string.dialog_error_sure),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast("Estas apunto de cerrar tu sesion, necesitarás volver a iniciar sesión", Toast.LENGTH_LONG)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                homeViewModel.onLogoutSelected()
            }
        ).show(dialogLauncher, this)
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
    }

    private fun setCustomToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}