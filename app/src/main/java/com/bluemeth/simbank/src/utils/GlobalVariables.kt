package com.bluemeth.simbank.src.utils

import com.google.firebase.auth.FirebaseAuth

class GlobalVariables {
    companion object {
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email

    }
}