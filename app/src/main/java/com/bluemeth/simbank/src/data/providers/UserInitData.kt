package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.google.firebase.Timestamp
import java.util.*

class UserInitData {

    companion object {
        private lateinit var user : User
        private lateinit var bankNumber : String
        private val PREFIX_BANK = "ES33"

         fun registerData(userSignIn: UserSignIn): User {
             user = User(userSignIn.email, userSignIn.nickName,userSignIn.phoneNumber.toInt())
             return user
        }

        fun createBankAccount(): BankAccount {
            bankNumber = PREFIX_BANK

            for (i in 1..20) {
                bankNumber += (0..9).random()
            }

            val money = (10000..20000).random().toDouble()

            return BankAccount(bankNumber, money, user.email)
        }

        fun createCreditCard(): CreditCard {
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }

            val money = (1000..3000).random().toDouble()
            val pin = (1000..9999).random()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))

            return CreditCard(creditCardNumber, money, pin, cvv, caducityTime, bankNumber)
        }
    }
}