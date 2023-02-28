package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.domain.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToWelcome = MutableLiveData<Event<Boolean>>()
    val navigateToWelcome: LiveData<Event<Boolean>>
        get() = _navigateToWelcome

    private val _showDialogLogout = MutableLiveData<Boolean>()
    val showDialogLogout: LiveData<Boolean>
        get() = _showDialogLogout

    private val _showDialogDeleteAccount = MutableLiveData<Boolean>()
    val showDialogDeleteAccount: LiveData<Boolean>
        get() = _showDialogDeleteAccount

    fun onLogoutSelected() {
        _showDialogLogout.value = true
    }

     fun logout() {
        authenticationRepository.logout()

        _navigateToLogin.value = Event(true)

        changePrefs()
    }

    private fun changePrefs() {
        prefs.clearPrefs()
        prefs.saveToken()
        prefs.saveSteps()
    }

    fun onDeleteAccountSelected() {
        _showDialogDeleteAccount.value = true
    }

    fun deleteAccountFromDB(iban: String) {
        viewModelScope.launch {
            val deletedAccount = deleteAccountUseCase(authenticationRepository.getCurrentUser().email!!, iban)

            if(deletedAccount) {
                _navigateToWelcome.value = Event(true)

                prefs.clearPrefs()
            } else {
                Timber.tag("Error").e("Account not deleted")
            }

        }

    }
}