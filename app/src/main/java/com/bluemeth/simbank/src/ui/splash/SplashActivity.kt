package com.bluemeth.simbank.src.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}