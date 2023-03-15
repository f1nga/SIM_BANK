package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.utils.Methods
import timber.log.Timber
import javax.inject.Inject

class InsertTransferUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository
    ) {

    suspend operator fun invoke(iban: String, movement: Movement, beneficiaryMoney: Double, beneficiaryIban: String): Boolean {
        val transferSuccess = movementRepository.insertMovement(movement)

        return if(transferSuccess) {
            bankAccountRepository.makeMovement(iban, Methods.roundOffDecimal(movement.remaining_money))
            bankAccountRepository.movementReceived(beneficiaryIban, beneficiaryMoney, movement.amount)
            true
        } else {
            Timber.tag("ERROR").w("Transfer failed")
            false
        }
    }
}