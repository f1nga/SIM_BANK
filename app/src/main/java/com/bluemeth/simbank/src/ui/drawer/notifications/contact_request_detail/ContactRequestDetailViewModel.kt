package com.bluemeth.simbank.src.ui.drawer.notifications.contact_request_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.domain.AcceptContactRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContactRequestDetailViewModel  @Inject constructor(
    private val acceptContactRequestUseCase: AcceptContactRequestUseCase,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private lateinit var _notification : Notification
    val notification : Notification get() = _notification

    private val _contactUpdated = MutableLiveData<Event<Boolean>>()
    val contactUpdated: LiveData<Event<Boolean>>
        get() = _contactUpdated

    private val _declineRequest = MutableLiveData<Event<Boolean>>()
    val declineRequest: LiveData<Event<Boolean>>
        get() = _declineRequest

    fun setNotification(notification: Notification) {
        _notification = notification
    }

    fun addNewUserContactToDB(currentUser: User, newContact: User) {
        viewModelScope.launch {
            currentUser.contacts.add(newContact.email)
            newContact.contacts.add(currentUser.email)
            val contactsUpdated = acceptContactRequestUseCase(currentUser, newContact, _notification)

            if(contactsUpdated) {
                _contactUpdated.value = Event(true)
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }

    fun declineContactRequest() {
        viewModelScope.launch {
            val notificationDeleted = notificationRepository.deleteNotification(_notification.id)

            if(notificationDeleted) {
                _declineRequest.value = Event(true)
            } else {
                Timber.tag("Error").e("Contact not added")
            }
        }
    }
}