package com.bluemeth.simbank.src.ui.drawer.contacts.add_contact

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.domain.AcceptContactRequestUseCase
import com.bluemeth.simbank.src.domain.UpdateUserBlockedContactsUseCase
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsRVAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.E

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val updateUserBlockedContactsUseCase: UpdateUserBlockedContactsUseCase,
    private val acceptContactRequestUseCase: AcceptContactRequestUseCase,
    val adapter: ContactsRVAdapter

) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _notificationDeleted = MutableLiveData<Event<Boolean>>()
    val notificationDeleted: LiveData<Event<Boolean>>
        get() = _notificationDeleted

    private val _contactUpdated = MutableLiveData<User>()
    val contactUpdated: LiveData<User>
        get() = _contactUpdated

    fun getUsersFromDB(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        viewModelScope.launch {
            userRepository.getUsers(email).observeForever {
                mutableData.value = it
            }
        }

        return mutableData
    }

    fun insertNotificationToDB(notification: Notification) {
        viewModelScope.launch {
            val notificationSended = notificationRepository.insertNotification(notification)

            if (notificationSended) {
                _navigateToHome.value = Event(true)
            } else {
                Timber.tag("Error").e("Notification not sended")
            }
        }
    }

    fun undoBlockContact(currentUser: User, blockedContact: User) {
        viewModelScope.launch {
            currentUser.blocked_contacts.remove(blockedContact.email)
            blockedContact.blocked_contacts.remove(currentUser.email)
            val contactsUpdated = acceptContactRequestUseCase(currentUser, blockedContact, null)

            if (contactsUpdated) {
                updateUserBlockedContactsUseCase(currentUser, blockedContact)
                _contactUpdated.value = blockedContact
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }

    fun getContactRequestFromDB(email: String, contactEmail: String): MutableLiveData<Notification> {
        val notification = MutableLiveData<Notification>()

        viewModelScope.launch {
            notificationRepository.getContactRequests(email, contactEmail).observeForever {
                notification.value = it
            }
        }

        return notification
    }

    fun deleteNotificationFromDB(id: String): MutableLiveData<Notification> {
        val notification = MutableLiveData<Notification>()

        viewModelScope.launch {
            val notificationDeleted = notificationRepository.deleteNotification(id)

            if(notificationDeleted) {
                _notificationDeleted.value = Event(true)
            } else {
                Log.w("ERROR", "Cannot delete de notification")
            }
        }

        return notification
    }

}