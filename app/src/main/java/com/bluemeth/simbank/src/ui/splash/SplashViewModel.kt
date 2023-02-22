package com.bluemeth.simbank.src.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.response.LoginResult
import com.bluemeth.simbank.src.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val loginUseCase: LoginUseCase) : ViewModel() {
    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome


     fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                LoginResult.Error -> {
                    Timber.tag("splash").i("malament")
                }
                is LoginResult.Success -> {
                    if (result.verified) {
                        _navigateToHome.value = Event(true)
                    }
                }
            }
        }
    }
}