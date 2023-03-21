package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Bizum
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.utils.Methods
import timber.log.Timber
import javax.inject.Inject

class InsertBizumUseCase  @Inject constructor(
    private val movementRepository: MovementRepository,
    private val bankAccountRepository: BankAccountRepository
) {

    suspend operator fun invoke(iban: String, movement: Bizum, beneficiaryMoney: List<Double>): Boolean {
        val transferSuccess = movementRepository.insertMovement(movement)

        return if(transferSuccess) {
            bankAccountRepository.makeMovement(iban, Methods.roundOffDecimal(movement.remaining_money))

            for (iban in movement.beneficiary_iban) {
                bankAccountRepository.movementReceived(movement.beneficiary_iban, beneficiaryMoney, movement.amount)

            }
            true
        } else {
            Timber.tag("ERROR").w("Transfer failed")
            false
        }
    }
}