package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.data.providers.firebase.NotificationsRepository
import com.bluemeth.simbank.src.utils.Methods
import timber.log.Timber
import javax.inject.Inject

class InsertTransferUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val notificationsRepository: NotificationsRepository
    ) {

    suspend operator fun invoke(iban: String, beneficiaryMoney: Double, beneficiaryIban: String, notification: Notification): Boolean {
        val transferSuccess = movementRepository.insertMovement(notification.movement!!)

        return if(transferSuccess) {
            notificationsRepository.insertNotification(notification)
            bankAccountRepository.makeMovement(iban, Methods.roundOffDecimal(notification.movement.remaining_money))
            bankAccountRepository.movementReceived(beneficiaryIban, beneficiaryMoney, notification.movement.amount)
            true
        } else {
            Timber.tag("ERROR").w("Transfer failed")
            false
        }
    }
}