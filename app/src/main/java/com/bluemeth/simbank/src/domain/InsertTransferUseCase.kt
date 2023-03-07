package com.bluemeth.simbank.src.domain

import android.util.Log
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class InsertTransferUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository
    ) {

    suspend operator fun invoke(iban: String, movement: Movement, beneficiaryMoney: Double, beneficiaryIban: String): Boolean {
        Log.i("hool", "7")
        val transferSuccess = movementRepository.insertMovement(movement)
        Log.i("hool", "9")

        return if(transferSuccess) {
            Log.i("hool", "10")
            bankAccountRepository.makeMovement(iban, Methods.roundOffDecimal(movement.remaining_money))
            Log.i("hool", "11")
            bankAccountRepository.movementReceived(beneficiaryIban, beneficiaryMoney, movement.amount)
            true
        } else {

            false
        }
    }
}