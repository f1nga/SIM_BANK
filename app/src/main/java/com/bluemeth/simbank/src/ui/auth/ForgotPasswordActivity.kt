package com.bluemeth.simbank.src.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bluemeth.simbank.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        findViewById<Button>(R.id.btnResetPassw).setOnClickListener {
            val email = findViewById<EditText>(R.id.inputEmailForgot).text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo enviado!", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Log.i("El teu cap","hol")
                }
            }
        }

    }
}