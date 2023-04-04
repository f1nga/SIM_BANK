package com.bluemeth.simbank.src.ui.drawer.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.RequestedBizum
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.RequestedBizumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val movementRepository: MovementRepository,
    private val requestedBizumRepository: RequestedBizumRepository
) : ViewModel() {

    private val _notificationDeleted = MutableLiveData<Notification>()
    val notificationDeleted: LiveData<Notification>
        get() = _notificationDeleted

    private val _notificationUpdated = MutableLiveData<Notification>()
    val notificationUpdated: LiveData<Notification>
        get() = _notificationUpdated

    fun getNotificationsByEmailFromDB(email: String) : MutableLiveData<MutableList<Notification>> {
        val notificationsList = MutableLiveData<MutableList<Notification>>()

        viewModelScope.launch {
            notificationRepository.getNotificationsByEmail(email).observeForever {
                notificationsList.value = it
            }
        }

        return notificationsList
    }

    fun getMovementByIdFromDB(id: String): MutableLiveData<Movement> {
        val movement = MutableLiveData<Movement>()
        viewModelScope.launch {
            movementRepository.getMovementByID(id).observeForever {
                movement.value = it
            }
        }

        return movement
    }

    fun getRequestedBizumByIdFromDB(id: String): MutableLiveData<RequestedBizum> {
        val movement = MutableLiveData<RequestedBizum>()

        viewModelScope.launch {
            requestedBizumRepository.getRequestedBizumByID(id).observeForever {
                movement.value = it
            }
        }

        return movement
    }

    fun deleteNotificationFromDB(notification: Notification) {
        viewModelScope.launch {
            val notificationDeleted = notificationRepository.deleteNotification(notification.id)

            if(notificationDeleted) _notificationDeleted.value = notification
            else Timber.tag("Error").e("Notification not sended")
        }
    }

    fun updateReadedNotificationFromDB(notification: Notification, readed: Boolean) {
        viewModelScope.launch {
            val notificationUpdated = notificationRepository.updateReadedNotification(notification, readed)

            if(notificationUpdated) _notificationUpdated.value = notification
            else Timber.tag("Error").e("Notification not sended")
        }
    }
}