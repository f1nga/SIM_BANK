package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import javax.inject.Inject

class DeleteUserContactUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(email: String, deleteUserEmail: String): Boolean {
        val userContactDeleted = userRepository.deleteUserContact(email, deleteUserEmail)

        return if (userContactDeleted) {
            userRepository.deleteUserContact(deleteUserEmail, email)
        } else {
            false
        }
    }
}