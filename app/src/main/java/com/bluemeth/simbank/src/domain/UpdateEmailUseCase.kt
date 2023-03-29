package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.*
import timber.log.Timber
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val movementRepository: MovementRepository,
    private val noteMovementRepository: NoteMovementRepository,
    private val notificationsRepository: NotificationsRepository
) {

    suspend operator fun invoke(newEmail: String, iban: String, newUser: User, password: String) : Boolean{
        val oldEmail = authenticationRepository.getCurrentUser().email!!

        val emailChanged = authenticationRepository.updateEmail2(newEmail, password)

        return if(emailChanged) {
            userRepository.updateUserEmail(oldEmail, newUser)
            userRepository.updateEmailContacts(oldEmail, newEmail)
            bankAccountRepository.updateOwnerEmail(iban, newUser.email)
            movementRepository.updateMovementUserEmail(oldEmail, newEmail)
            noteMovementRepository.updateNoteMovementUserEmail(oldEmail, newEmail)
            notificationsRepository.updateUserEmail(oldEmail, newEmail)
            prefs.saveEmail(newEmail)
            true
        } else {
            Timber.tag("ERROR").w("Email doesn't changed")
            false
        }
    }
}