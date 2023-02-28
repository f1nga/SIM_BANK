package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UpdatePhoneViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository

) : ViewModel(){

    private companion object {
        const val PHONE_LENGTH = 9
    }

    private val _viewState = MutableStateFlow(UpdatePhoneViewState())
    val viewState: StateFlow<UpdatePhoneViewState>
        get() = _viewState

    private val _navigateToProfile = MutableLiveData<Event<Boolean>>()
    val navigateToProfile: LiveData<Event<Boolean>>
        get() = _navigateToProfile

    fun onChangeSelected(newPhone: String) {
        val viewState = toUpdatePhoneState(newPhone)

        if (viewState.phoneValidated() && newPhone.isNotEmpty()) {
            userRepository.updateUserPhone(authenticationRepository.getCurrentUser().email!!, newPhone.toInt())

            _navigateToProfile.value = Event(true)
        } else {
            onFieldsChanged(newPhone)
        }
    }

    fun onFieldsChanged(newPhone: String) {
        _viewState.value = toUpdatePhoneState(newPhone)
    }

    private fun isValidPhoneNumber(phone: String): Boolean =
        phone.length == PHONE_LENGTH || phone.isEmpty()

    private fun toUpdatePhoneState(newPhone: String): UpdatePhoneViewState {
        return UpdatePhoneViewState(
            isValidPhoneNumber = isValidPhoneNumber(newPhone)
        )
    }
}