package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.utils.Methods

data class NoteMovement(
    val id: String = Methods.generateToken(),
    val note: String,
    val movementId: String,
    val user_email: String
)
