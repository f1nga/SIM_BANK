package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.NotificationsRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import javax.inject.Inject

class AcceptContactRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationsRepository: NotificationsRepository
) {

    suspend operator fun invoke(currentUser: User, newContact: User, notification: Notification): Boolean {
        val contactsUpdated = userRepository.updateUserContacts(currentUser.email, currentUser.contacts)

        return if (contactsUpdated) {
            userRepository.updateUserContacts(newContact.email, newContact.contacts)
            notificationsRepository.deleteNotification(notification)
        } else {
            false
        }
    }
}