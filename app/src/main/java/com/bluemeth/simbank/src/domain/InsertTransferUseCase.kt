package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import javax.inject.Inject

class InsertTransferUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository
    ) {

    suspend operator fun invoke(iban:String, movement: Movement): Boolean {
        val transferSuccess = movementRepository.insertMovement(movement)

        return if(transferSuccess) {
            bankAccountRepository.makeTransfer(iban, movement.remaining_money)
            true
        } else {
            false
        }
    }
}