package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.domain.UpdateEmailUseCase
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email.model.UserEmailUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdateEmailViewModel @Inject constructor(
    private val updateEmailUseCase: UpdateEmailUseCase
) : ViewModel(){

    private val _viewState = MutableStateFlow(UpdateEmailViewState())
    val viewState: StateFlow<UpdateEmailViewState>
        get() = _viewState

    private var _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean>
        get() = _showDialog

    fun onChangeSelected(userEmailUpdate: UserEmailUpdate, newUser: User, iban: String) {
        val viewState = userEmailUpdate.toUpdateEmailState()

        if (viewState.emailValidated() && userEmailUpdate.isNotEmpty()) {
            viewModelScope.launch {
                val user = User(userEmailUpdate.email, newUser.password, newUser.name, newUser.phone)
                val emailUpdated = updateEmailUseCase(userEmailUpdate.email, iban, user)

                if(emailUpdated) {
                    _showDialog.value = true
                } else {
                    Timber.tag("ERROR",) .e("Email not updated" )
                }
            }
        } else {
            onFieldsChanged(userEmailUpdate)
        }
    }

    fun onFieldsChanged(userEmailUpdate: UserEmailUpdate) {
        _viewState.value = userEmailUpdate.toUpdateEmailState()
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()


    private fun isValidEmailsCoincide(email: String, newEmailConfirm: String) =
        email == newEmailConfirm

    private fun UserEmailUpdate.toUpdateEmailState(): UpdateEmailViewState {
        return UpdateEmailViewState(
            isValidEmail = isValidOrEmptyEmail(email),
            isValidEmailConfirm = isValidEmailsCoincide(email, emailConfirm)
        )
    }


}