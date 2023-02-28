package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.models.UserNameUpdate
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdateNameViewState
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdatePhoneViewState
import com.bluemeth.simbank.src.utils.GlobalVariables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val bankAccountRepository: BankAccountRepository,
    ): ViewModel() {

    private companion object {
        const val MIN_NAME_LENGTH = 3
        const val PHONE_LENGTH = 9
    }

    private val _viewState = MutableStateFlow(UpdatePhoneViewState())
    val viewState: StateFlow<UpdatePhoneViewState>
        get() = _viewState

    private val _viewNameState = MutableStateFlow(UpdateNameViewState())
    val viewNameState: StateFlow<UpdateNameViewState>
        get() = _viewNameState

    private val _navigateToProfile = MutableLiveData<Event<Boolean>>()
    val navigateToProfile: LiveData<Event<Boolean>>
        get() = _navigateToProfile

    fun updateNameFromDB(email: String, name: String) {
        userRepository.updateUserName(email, name)
    }

    fun updateEmailFromDB(newUser: User, iban: String) {
        authenticationRepository.updateEmail(newUser.email)
        userRepository.updateUserEmail(GlobalVariables.userEmail!!, newUser)
        bankAccountRepository.updateOwnerEmail(iban, newUser.email)
    }

    fun updatePhoneFromDB(email: String, phone: Int) {
        userRepository.updateUserPhone(email, phone)
    }

    fun updatePasswordFromDB(email: String, name: String) {
        userRepository.updateUserName(email, name)
    }
    /// Update phone
    fun onChangeSelected(newPhone: String) {
        val viewState = toUpdatePhoneState(newPhone)

        if (viewState.phoneValidated() && newPhone.isNotEmpty()) {
            updatePhoneFromDB(GlobalVariables.userEmail!!, newPhone.toInt())
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

    /// Update name

    fun onChangeNameSelected(userUpdate: UserNameUpdate) {
        val viewState = userUpdate.toUpdateNameViewState()

        if (viewState.fullNameValidated() && userUpdate.isNotEmpty()) {
            val fullName = userUpdate.name + " " + userUpdate.lastName + " " + userUpdate.secondName
            updateNameFromDB(GlobalVariables.userEmail!!, fullName)
            _navigateToProfile.value = Event(true)
        } else {
            onNameFieldsChanged(userUpdate)
        }
    }

    fun onNameFieldsChanged(userUpdate: UserNameUpdate) {
        _viewNameState.value = userUpdate.toUpdateNameViewState()
    }

    private fun isValidName(name: String): Boolean =
        name.length >= MIN_NAME_LENGTH || name.isEmpty()

    private fun UserNameUpdate.toUpdateNameViewState(): UpdateNameViewState {
        return UpdateNameViewState(
            isValidName = isValidName(name),
            isValidLastName = isValidName(lastName),
            isValidSecondName = isValidName(secondName)
        )
    }
}