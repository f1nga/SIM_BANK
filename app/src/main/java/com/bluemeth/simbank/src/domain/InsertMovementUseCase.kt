package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.data.providers.firebase.RequestedBizumRepository
import com.bluemeth.simbank.src.utils.Methods
import timber.log.Timber
import javax.inject.Inject

class InsertMovementUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val notificationRepository: NotificationRepository,
    private val requestedBizumRepository: RequestedBizumRepository
    ) {

    suspend operator fun invoke(iban: String, movement: Movement, beneficiaryMoney: Double, beneficiaryIban: String, notification: Notification): Boolean {
        val transferSuccess = movementRepository.insertMovement(movement)

        return if(transferSuccess) {
            notificationRepository.insertNotification(notification)
            notificationRepository.deleteNotificationByMovementID(movement.id)
            requestedBizumRepository.deleteRequestedBizum(movement.id)
            bankAccountRepository.makeMovement(iban, Methods.roundOffDecimal(movement.remaining_money))
            bankAccountRepository.movementReceived(beneficiaryIban, beneficiaryMoney, movement.amount)
            true
        } else {
            Timber.tag("ERROR").w("Transfer failed")
            false
        }
    }
}