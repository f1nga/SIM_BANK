package com.bluemeth.simbank.src.ui.welcome

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bluemeth.simbank.databinding.ActivityWelcomeBinding
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.auth.signin.SignInActivity

class WelcomeActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, WelcomeActivity::class.java)
    }

    private lateinit var binding: ActivityWelcomeBinding
    private val welcomeViewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()

        binding.btnIniciarSesion.setOnClickListener {
            welcomeViewModel.onLogInSelected()
        }

        binding.btnCrearCuenta.setOnClickListener {
            welcomeViewModel.onSignInSelected()
        }


    }

    private fun initObservers() {
        welcomeViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        welcomeViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

    private fun goToSignIn(){
        startActivity(SignInActivity.create(this))
    }


}