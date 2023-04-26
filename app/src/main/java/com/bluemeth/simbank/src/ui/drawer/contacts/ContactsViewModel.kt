package com.bluemeth.simbank.src.ui.drawer.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.domain.AcceptContactRequestUseCase
import com.bluemeth.simbank.src.domain.UpdateUserBlockedContactsUseCase
import com.bluemeth.simbank.src.domain.DeleteUserContactUseCase
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val deleteUserContactUseCase: DeleteUserContactUseCase,
    private val updateUserBlockedContactsUseCase: UpdateUserBlockedContactsUseCase,
    private val acceptContactRequestUseCase: AcceptContactRequestUseCase,
    val adapter: ContactsRVAdapter

) : ViewModel() {

    private val _contactDeleted = MutableLiveData<User>()
    val contactDeleted: LiveData<User>
        get() = _contactDeleted

    private val _contactBlocked = MutableLiveData<User>()
    val contactBlocked: LiveData<User>
        get() = _contactBlocked

    private val _contactUpdated = MutableLiveData<User>()
    val contactUpdated: LiveData<User>
        get() = _contactUpdated

    fun getUserContactsFromDB(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        viewModelScope.launch {
            userRepository.getUserContacts(email).observeForever {

                mutableData.value = it
            }
        }

        return mutableData
    }

    fun deleteUserContactFromDB(email: String, deleteUser: User) {
        viewModelScope.launch {
            val userContactDeleted = deleteUserContactUseCase(email, deleteUser.email)

            if(userContactDeleted) _contactDeleted.value = deleteUser
            else Timber.tag("Error").e("Notification not sended")
        }
    }

    fun addUserBlockedContactToDB(user: User, contactBlocked: User) {
        viewModelScope.launch {
            user.blocked_contacts.add(contactBlocked.email)
            contactBlocked.blocked_contacts.add(user.email)
            val userContactBlocked = updateUserBlockedContactsUseCase(user, contactBlocked)

            if(userContactBlocked) {
                deleteUserContactUseCase(user.email, contactBlocked.email)
                _contactBlocked.value = contactBlocked
            }
            else Timber.tag("Error").e("Notification not sended")
        }
    }

    fun undoDeleteContact(currentUser: User, newContact: User) {
        viewModelScope.launch {
            currentUser.contacts.add(newContact.email)
            val contactsUpdated = acceptContactRequestUseCase(currentUser, newContact, null)

            if(contactsUpdated) {
                _contactUpdated.value = newContact
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }

    fun undoBlockContact(currentUser: User, blockedContact: User) {
        viewModelScope.launch {
            currentUser.contacts.add(blockedContact.email)
            currentUser.blocked_contacts.remove(blockedContact.email)
            blockedContact.blocked_contacts.remove(currentUser.email)
            val contactsUpdated = acceptContactRequestUseCase(currentUser, blockedContact, null)

            if(contactsUpdated) {
                updateUserBlockedContactsUseCase(currentUser, blockedContact)
                _contactUpdated.value = blockedContact
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }

    fun getUserContactsFromDB2(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        viewModelScope.launch {
            userRepository.getUsers(email).observeForever { listContacts ->
                userRepository.getUserRecord(email).observeForever { user ->
                    val contactAgendaList = mutableListOf<User>()

                    for (contact in listContacts) {
                        for (emailUserContact in user.contacts) {
                            if (contact.email == emailUserContact) {
                                contactAgendaList.add(
                                    contact
                                )
                            }

                        }
                    }

                    mutableData.value = contactAgendaList
                }

            }
        }

        return mutableData
    }
}