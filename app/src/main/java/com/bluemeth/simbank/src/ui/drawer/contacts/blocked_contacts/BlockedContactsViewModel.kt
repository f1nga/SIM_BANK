package com.bluemeth.simbank.src.ui.drawer.contacts.blocked_contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.domain.UpdateUserBlockedContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BlockedContactsViewModel @Inject constructor(
    private val updateUserBlockedContactsUseCase: UpdateUserBlockedContactsUseCase
) : ViewModel() {

    private val _contactDesblocked = MutableLiveData<User>()
    val contactDesblocked: LiveData<User>
        get() = _contactDesblocked

    private val _contactBlocked = MutableLiveData<User>()
    val contactBlocked: LiveData<User>
        get() = _contactBlocked

    fun desblockUserContactToDB(user: User, contactBlocked: User) {
        viewModelScope.launch {
            user.blocked_contacts.remove(contactBlocked.email)
            contactBlocked.blocked_contacts.remove(user.email)
            val userContactBlocked = updateUserBlockedContactsUseCase(user, contactBlocked)

            if(userContactBlocked) {
                _contactDesblocked.value = contactBlocked
            }
            else Timber.tag("Error").e("Notification not sended")
        }
    }

    fun undoDesblockContact(currentUser: User, contactBlocked: User) {
        viewModelScope.launch {
            currentUser.blocked_contacts.add(contactBlocked.email)
            contactBlocked.blocked_contacts.add(currentUser.email)
            val contactsUpdated = updateUserBlockedContactsUseCase(currentUser, contactBlocked)

            if(contactsUpdated) {
                _contactBlocked.value = contactBlocked
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }
}