package com.bluemeth.simbank.src.ui.auth.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.domain.SendEmailVerificationUseCase
import com.bluemeth.simbank.src.domain.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _navigateToVerifyAccount = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyAccount: LiveData<Event<Boolean>>
        get() = _navigateToVerifyAccount

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    init {
        viewModelScope.launch { sendEmailVerificationUseCase() }
        viewModelScope.launch {

            verifyEmailUseCase()
                .catch {
                    Timber.i("Verification error: ${it.message}")
                }
                .collect { verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }

        }
    }

    fun onGoToDetailSelected() {
        _navigateToVerifyAccount.value = Event(true)
    }
}