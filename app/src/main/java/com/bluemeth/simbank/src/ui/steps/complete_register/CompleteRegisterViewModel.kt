package com.bluemeth.simbank.src.ui.steps.complete_register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.bluemeth.simbank.src.ui.steps.complete_register.model.UserCompleteRegister
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteRegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private companion object {
        const val MIN_NAME_LENGTH = 3
        const val PHONE_LENGTH = 9
    }

    private val _navigateToSteps = MutableLiveData<Event<Boolean>>()
    val navigateToSteps: LiveData<Event<Boolean>>
        get() = _navigateToSteps

    private var _showErrorDialog = MutableLiveData<Boolean>()
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _viewState = MutableStateFlow(UserCompleteRegisterViewState())
    val viewState: StateFlow<UserCompleteRegisterViewState>
        get() = _viewState

    fun onCompleteSelected(userCompleteRegister: UserCompleteRegister) {
        val viewState = userCompleteRegister.toUpdateCompleteRegister()

        if (userCompleteRegister.isNotEmpty() && viewState.isValidUser()) {
            viewModelScope.launch {

                val userTableCreated = userRepository.createUserTable(
                    UserSignIn(
                        nickName = "${userCompleteRegister.name} ${userCompleteRegister.lastName} ${userCompleteRegister.secondName}",
                        email = authenticationRepository.getCurrentUser().email!!,
                        password = "",
                        passwordConfirmation = "",
                        phoneNumber = userCompleteRegister.phoneNumber,
                    )
                )

                if (userTableCreated) {
                    _navigateToSteps.value = Event(true)
                    prefs.saveCompleteRegister()
                } else _showErrorDialog.value = true

            }
        } else {
            onNameFieldsChanged(userCompleteRegister)
        }

    }

    fun onNameFieldsChanged(userCompleteRegister: UserCompleteRegister) {
        _viewState.value = userCompleteRegister.toUpdateCompleteRegister()
    }

    private fun isValidName(name: String): Boolean =
        name.length >= MIN_NAME_LENGTH || name.isEmpty()

    private fun isValidPhoneNumber(phone: String): Boolean =
        phone.length == PHONE_LENGTH || phone.isEmpty()

    private fun UserCompleteRegister.toUpdateCompleteRegister(): UserCompleteRegisterViewState {
        return UserCompleteRegisterViewState(
            isValidName = isValidName(name),
            isValidLastName = isValidName(lastName),
            isValidSecondName = isValidName(secondName),
            isValidPhoneNumber = isValidPhoneNumber(phoneNumber)
        )
    }
}