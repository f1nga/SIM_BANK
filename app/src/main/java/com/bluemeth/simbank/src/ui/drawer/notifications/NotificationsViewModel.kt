package com.bluemeth.simbank.src.ui.drawer.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.providers.firebase.NotificationsRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    fun getNotificationsByEmailFromDB(email: String) : MutableLiveData<List<Notification>> {
        val notificationsList = MutableLiveData<List<Notification>>()

        viewModelScope.launch {
            notificationsRepository.getNotificationsByEmail(email).observeForever {
                notificationsList.value = it
            }
        }

        return notificationsList
    }
}