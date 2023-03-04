package com.bluemeth.simbank.src.ui.auth.forgot_password

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.domain.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val forgotPasswordUseCase: ForgotPasswordUseCase) :
    ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    fun onSendEmailSelected(email: String) {
        if (isValidEmail(email)) {
            sendEmail(email)
        } else {
            Timber.tag("EMAIL INCORRECT").i("MEGAHOL")
        }
    }

    private fun sendEmail(email: String) {
        viewModelScope.launch {
            val accountCreated = forgotPasswordUseCase(email)

            if (accountCreated) {
                _navigateToLogin.value = Event(true)
            } else {
                Timber.tag("FALLO").i("MELO")
            }
        }
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()
}