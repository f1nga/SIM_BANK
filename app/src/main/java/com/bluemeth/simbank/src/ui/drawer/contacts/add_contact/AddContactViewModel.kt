package com.bluemeth.simbank.src.ui.drawer.contacts.add_contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

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

            if(notificationSended) {
                _navigateToHome.value = Event(true)
            } else {
                Timber.tag("Error").e("Notification not sended")
            }
        }
    }

}