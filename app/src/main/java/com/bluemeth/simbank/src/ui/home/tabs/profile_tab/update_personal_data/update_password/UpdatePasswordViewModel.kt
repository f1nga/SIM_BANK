package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.domain.UpdatePasswordUseCase
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_password.model.UserPasswordUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val updatePasswordUseCase: UpdatePasswordUseCase
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

    fun onNextSelected(currentPassword: String, newPassword: String) {
        val viewState = toCheckActualPasswordState(currentPassword, newPassword)

        if (viewState.isValidPassword && newPassword.isNotEmpty()) {
            _goToNextForm.value = Event(true)
        } else {
            _showPasswordsNotMatchDialog.value = true
        }
    }

    private fun isValidPasswordsCoincide(currentPassword: String, password: String) = currentPassword == password

    private fun toCheckActualPasswordState(currentPassword: String, password: String): UpdatePasswordViewState {
        return UpdatePasswordViewState(
            isValidPassword = isValidPasswordsCoincide(currentPassword, password)
        )
    }

    fun onChangeSelected(userPasswordUpdate: UserPasswordUpdate, oldPassword: String) {
        val viewState = userPasswordUpdate.toUpdatePasswordState()

        if (viewState.passwordValidated() && userPasswordUpdate.isNotEmpty()) {
            if(userPasswordUpdate.password != oldPassword) {
                viewModelScope.launch {
                    val passwordUpdated =
                        updatePasswordUseCase(authenticationRepository.getCurrentUser().email!!, userPasswordUpdate.password)

                    if(passwordUpdated) {
                        _navigateToProfile.value = Event(true)
                    } else {
                        Timber.tag("Error").e("Password not updated")
                    }
                }
            } else {
                _showCoincideOldPasswordDialog.value = true
            }
        } else {
            onFieldsChanged(userPasswordUpdate)
        }
    }

    fun onFieldsChanged(userPasswordUpdate: UserPasswordUpdate) {
        _viewPasswordState.value = userPasswordUpdate.toUpdatePasswordState()
    }

    private fun isValidPasswordLength(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH || password.isEmpty()

    private fun UserPasswordUpdate.toUpdatePasswordState(): UpdatePasswordViewState {
        return UpdatePasswordViewState(
            isValidPassword = isValidPasswordLength(password),
            isValidPasswordConfirmation = isValidPasswordsCoincide(password, passwordConfirm),
        )
    }
}