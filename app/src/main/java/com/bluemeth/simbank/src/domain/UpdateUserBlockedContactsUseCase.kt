package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import javax.inject.Inject

class UpdateUserBlockedContactsUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(user: User, blockedContact: User): Boolean {
        val userContactDeleted = userRepository.updateUserBlockedContacts(user)

        return if (userContactDeleted) {
            userRepository.updateUserBlockedContacts(blockedContact)
        } else {
            false
        }
    }
}