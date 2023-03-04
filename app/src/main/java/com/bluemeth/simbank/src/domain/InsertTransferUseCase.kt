package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.Transfer
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.TransferRepository
import javax.inject.Inject

class InsertTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
    private val bankAccountRepository: BankAccountRepository
    ) {

    suspend operator fun invoke(iban:String, money: Double, transfer: Transfer): Boolean {
        val transferSuccess = transferRepository.insertTransfer(transfer)

        return if(transferSuccess) {
            bankAccountRepository.makeTransfer(iban, money, transfer.amount)
            true
        } else {
            false
        }
    }
}