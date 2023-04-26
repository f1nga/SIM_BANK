package com.bluemeth.simbank.src.ui.drawer.contacts.view_contact_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewContactProfileViewModel @Inject constructor(
    private val movementRepository: MovementRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel(){

    private lateinit var _contact : User
    val contact : User get() = _contact

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    fun setContact(contact: User) {
        _contact = contact
    }

    fun getTotalMovementsByUserFromDB(email: String, name: String): MutableLiveData<Int> {
        val totalMovements = MutableLiveData<Int>()

        viewModelScope.launch {
            movementRepository.getTotalMovementsByUser(email, name).observeForever {
                totalMovements.value = it
            }
        }

        return totalMovements
    }

    fun getCommonUserContactsFromDB(currentUserEmail: String, contactEmail: String): MutableLiveData<Int> {
        val totalMovements = MutableLiveData<Int>()

        viewModelScope.launch {
            userRepository.getCommonUserContacts(currentUserEmail, contactEmail).observeForever {
                totalMovements.value = it
            }
        }

        return totalMovements
    }

    fun insertNotificationToDB(notification: Notification) {
        viewModelScope.launch {
            val notificationSended = notificationRepository.insertNotification(notification)

            if(notificationSended) {
                _navigateToHome.value = Event(true)
            } else {
                Timber.tag("Error").e("Notification not sended")
            }
        }
    }
}