package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class NotificationRepository @Inject constructor(val firebase: FirebaseClient) {

    private companion object {
        const val NOTIFICATIONS_COLLECTION = "notifications"
        const val ID_FIELD = "id"
        const val DATE_FIELD = "date"
        const val DESCRIPTION_FIELD = "description"
        const val TITLE_FIELD = "title"
        const val READED_FIELD = "readed"
        const val TYPE_FIELD = "type"
        const val USER_RECEIVE_EMAIL_FIELD = "user_receive_email"
        const val USER_SEND_EMAIL_FIELD = "user_send_email"
        const val MOVEMENT_ID_FIELD = "movementID"

    }

    suspend fun getNotificationsByEmail(email: String): MutableLiveData<MutableList<Notification>> {
        val notificationsList = MutableLiveData<MutableList<Notification>>()

        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USER_RECEIVE_EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Notification>()

                for (document in documents) {
                    listData.add(
                        Notification(
                            id = document.getString(ID_FIELD)!!,
                            title = document.getString(TITLE_FIELD)!!,
                            description = document.getString(DESCRIPTION_FIELD)!!,
                            date = document.getTimestamp(DATE_FIELD)!!,
                            type = when (document.getString(TYPE_FIELD)!!) {
                                "BizumReceived" -> NotificationType.BizumReceived
                                "TransferReceived" -> NotificationType.TransferReceived
                                "BizumRequested" -> NotificationType.BizumRequested
                                else -> NotificationType.ContactRequest
                            },
                            movementID = document.getString(MOVEMENT_ID_FIELD),
                            readed = document.getBoolean(READED_FIELD)!!,
                            user_send_email = document.getString(USER_SEND_EMAIL_FIELD),
                            user_receive_email = document.getString(USER_RECEIVE_EMAIL_FIELD)!!
                        )
                    )
                }

                notificationsList.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }
            .await()

        return notificationsList
    }

    suspend fun isEveryNotificationReaded(email: String): MutableLiveData<Boolean> {
        val isEveryNotificationReaded = MutableLiveData<Boolean>()

        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USER_RECEIVE_EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                isEveryNotificationReaded.value = true

                for (document in documents) {
                    if(!document.getBoolean(READED_FIELD)!!) {
                        isEveryNotificationReaded.value = false
                    }
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }
            .await()

        return isEveryNotificationReaded
    }

    suspend fun insertNotification(notification: Notification) = runCatching {
        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .document(notification.id)
            .set(notification)
            .await()
    }.isSuccess

    suspend fun deleteNotification(id: String) = runCatching {
        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .document(id)
            .delete()
            .await()
    }.isSuccess

    suspend fun deleteNotificationByMovementID(movementId: String) = runCatching {
        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(MOVEMENT_ID_FIELD, movementId)
            .get()
            .addOnSuccessListener {
                firebase.db
                    .collection(NOTIFICATIONS_COLLECTION)
                    .document(it.first().id)
                    .delete()
            }
            .await()
    }.isSuccess

    suspend fun updateReadedNotification(notification: Notification, readed: Boolean) = runCatching {
        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .document(notification.id)
            .update(READED_FIELD, readed)
            .await()
    }.isSuccess

    fun updateUserEmail(oldEmail: String, newEmail: String) = runCatching {
        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USER_RECEIVE_EMAIL_FIELD, oldEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firebase.db
                        .collection(NOTIFICATIONS_COLLECTION)
                        .document(document.id)
                        .update(USER_RECEIVE_EMAIL_FIELD, newEmail)
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }

        firebase.db
            .collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USER_SEND_EMAIL_FIELD, oldEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firebase.db
                        .collection(NOTIFICATIONS_COLLECTION)
                        .document(document.id)
                        .update(USER_SEND_EMAIL_FIELD, newEmail)
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }
    }.isSuccess
}