package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdatePasswordViewState
import com.bluemeth.simbank.src.utils.GlobalVariables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }

    private val _viewPasswordState = MutableStateFlow(UpdatePasswordViewState())
    val viewPasswordState: StateFlow<UpdatePasswordViewState>
        get() = _viewPasswordState

    private val _goToNextForm = MutableLiveData<Event<Boolean>>()
    val goToNextForm: LiveData<Event<Boolean>>
        get() = _goToNextForm

    private val _navigateToProfile = MutableLiveData<Event<Boolean>>()
    val navigateToProfile: LiveData<Event<Boolean>>
        get() = _navigateToProfile

    private var _showPasswordsNotMatchDialog = MutableLiveData(false)
    val showPasswordsNotMatchDialog: LiveData<Boolean>
        get() = _showPasswordsNotMatchDialog

    private var _showCoincideOldPasswordDialog = MutableLiveData(false)
    val showCoincideOldPasswordDialog: LiveData<Boolean>
        get() = _showCoincideOldPasswordDialog

    private fun updatePasswordFromDB(email: String, password: String) {
        userRepository.updateUserPassword(email, password)
        authenticationRepository.updatePassword(password)
    }

    fun onNextSelected(currentPassword: String, newPassword: String) {
        val viewState = toCheckActualPasswordState(currentPassword, newPassword)

        if (viewState.isValidPassword && newPassword.isNotEmpty()) {
            _goToNextForm.value = Event(true)
        } else {
            _showPasswordsNotMatchDialog.value = true
        }
    }

    private fun isValidActualPassword(currentPassword: String, password: String) = currentPassword == password

    private fun toCheckActualPasswordState(currentPassword: String, password: String): UpdatePasswordViewState {
        return UpdatePasswordViewState(
            isValidPassword = isValidActualPassword(currentPassword, password)
        )
    }

    fun onChangeSelected(password: String, passwordConfirmation: String, oldPassword: String) {
        val viewState = toUpdatePasswordState(password, passwordConfirmation, oldPassword)

        if (viewState.passwordValidated() && password.isNotEmpty()) {
            if(viewState.passwordsCoincide()) {
                updatePasswordFromDB(GlobalVariables.userEmail!!, passwordConfirmation)
                _navigateToProfile.value = Event(true)

            } else {
                _showCoincideOldPasswordDialog.value = true
            }
        } else {
            _showPasswordsNotMatchDialog.value = true
        }
    }

    fun onFieldsChanged(password: String,  passwordConfirmation: String, oldPassword: String) {
        _viewPasswordState.value = toUpdatePasswordState(password, passwordConfirmation, oldPassword)
    }

    private fun isValidPasswordLength(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH || password.isEmpty()

    private fun isValidPasswordCoincide(currentPassword: String, password: String) = currentPassword != password


    private fun toUpdatePasswordState(password: String, passwordConfirmation: String, oldPassword: String): UpdatePasswordViewState {
        return UpdatePasswordViewState(
            isValidPasswordLength = isValidPasswordLength(password),
            isValidPassword = isValidActualPassword(password, passwordConfirmation),
            isValidPasswordConfirmation = isValidActualPassword(password, passwordConfirmation),
            isValidPasswordsCoincide = isValidPasswordCoincide(password, oldPassword)
        )
    }
}