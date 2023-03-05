package com.bluemeth.simbank.src.ui.splash

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.home.HomeActivity
import com.bluemeth.simbank.src.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservers()
//        splashViewModel.insertCreditCardToDB(
//            CreditCard(
//            "3029384510238534",
//            2832.32,
//            2342,
//            652,
//                Timestamp.now(),
//                CreditCardType.Debito,
//                "ES3350330479580716102854"
//            )
//        )

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
        if(prefs.getSteps().isNotEmpty() && prefs.getEmail().isNotEmpty()) {
            splashViewModel.loginUser(prefs.getEmail(), prefs.getPassword())
        } else if(prefs.getToken().isNotEmpty()) {
            goToLogin()
            finish()
        } else {
            goToWelcome()
            finish()
        }
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(this))
    }

    private fun goToWelcome(){
        startActivity(WelcomeActivity.create(this))
    }

    private fun goToLogin(){
        startActivity(LoginActivity.create(this))
    }
}