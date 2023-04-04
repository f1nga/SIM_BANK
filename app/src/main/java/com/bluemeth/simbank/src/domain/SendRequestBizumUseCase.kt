package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.RequestedBizum
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.RequestedBizumRepository
import javax.inject.Inject

class SendRequestBizumUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val requestedBizumRepository: RequestedBizumRepository
) {

    suspend operator fun invoke(notification: Notification, requestedBizum: RequestedBizum): Boolean {
        val notificationInserted = notificationRepository.insertNotification(notification)

        return if (notificationInserted) {
            requestedBizumRepository.insertRequestedBizum(requestedBizum)
        } else {
            false
        }
    }
}