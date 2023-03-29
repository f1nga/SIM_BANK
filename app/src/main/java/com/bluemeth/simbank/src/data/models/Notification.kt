package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.Timestamp

data class Notification(
    val id: String = Methods.generateToken(),
    val title: String,
    val description: String,
    val date: Timestamp = Timestamp.now(),
    val type: NotificationType,
    val movement: Movement? = null,
    val readed: Boolean = false,
    val user_send_email: String? = null,
    val user_receive_email: String
)
