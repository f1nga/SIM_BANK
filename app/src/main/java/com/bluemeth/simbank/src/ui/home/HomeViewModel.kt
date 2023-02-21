package com.bluemeth.simbank.src.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    ) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    fun onLogoutSelected() {
        logoutUser()
    }

    private fun logoutUser() {
        authenticationRepository.logout()
        _navigateToLogin.value = Event(true)
        prefs.clearPrefs()
    }
}