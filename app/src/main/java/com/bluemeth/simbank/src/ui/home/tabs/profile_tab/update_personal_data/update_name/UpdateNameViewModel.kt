package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.domain.UpdateNameUseCase
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_name.model.UserNameUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateNameViewModel @Inject constructor(
    private val updateNameUseCase: UpdateNameUseCase
) : ViewModel() {

    private companion object {
        const val MIN_NAME_LENGTH = 3
    }

    private val _viewNameState = MutableStateFlow(UpdateNameViewState())
    val viewNameState: StateFlow<UpdateNameViewState>
        get() = _viewNameState

    private val _navigateToProfile = MutableLiveData<Event<Boolean>>()
    val navigateToProfile: LiveData<Event<Boolean>>
        get() = _navigateToProfile

    fun changeNameFromDB(fullName: String, oldName: String) {
        viewModelScope.launch {
            updateNameUseCase(fullName, oldName)
        }
    }

    fun onChangeNameSelected(userUpdate: UserNameUpdate, oldName: String) {
        val viewState = userUpdate.toUpdateNameViewState()

        if (viewState.fullNameValidated() && userUpdate.isNotEmpty()) {
            val fullName = userUpdate.name + " " + userUpdate.lastName + " " + userUpdate.secondName

            changeNameFromDB(fullName, oldName)

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