package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import javax.inject.Inject

class InsertCreditCardUseCase @Inject constructor(private val creditCardRepository: CreditCardRepository) {

    suspend operator fun invoke(creditCard: CreditCard) = creditCardRepository.insertCreditCard(creditCard)
}