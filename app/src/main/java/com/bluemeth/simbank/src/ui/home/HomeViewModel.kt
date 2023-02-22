package com.bluemeth.simbank.src.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
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

    fun getUserName(): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(prefs.getEmail()).observeForever {
            user.value = it
        }

        return user
    }

    fun splitName(name: String): String {
        return name.split(" ")[0]
    }
}