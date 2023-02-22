package com.bluemeth.simbank.src.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event

class WelcomeViewModel: ViewModel(){

    private val _navigateToSignIn = MutableLiveData<Event<Boolean>>()
    val navigateToSignIn: LiveData<Event<Boolean>>
        get() = _navigateToSignIn

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin


    fun onSignInSelected() {
        _navigateToSignIn.value = Event(true)
    }

    fun onLogInSelected(){
        _navigateToLogin.value = Event(true)
    }
}