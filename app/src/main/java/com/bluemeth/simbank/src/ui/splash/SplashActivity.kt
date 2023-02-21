package com.bluemeth.simbank.src.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservers()

        // prefs.clearPrefs()
        checkUserIsLogged()
    }

    private fun initObservers() {
        splashViewModel.navigateToHome.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }
    }

    private fun checkUserIsLogged() {
        if(prefs.getToken().isNotEmpty()) {
            splashViewModel.loginUser(prefs.getEmail(), prefs.getPassword())
        } else {
            goToLogin()
            finish()
        }
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(this))
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}