package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.google.firebase.Timestamp
import java.util.*

class UserInitData {

    companion object {
        private lateinit var user : User
        private val PREFIX_BANK = "ES33"
        val money = (10000..20000).random().toDouble()
        val iban = createIban()

        fun registerData(userSignIn: UserSignIn): User {
             user = User(userSignIn.email, userSignIn.nickName,userSignIn.phoneNumber.toInt())
             return user
        }

        fun createBankAccount(): BankAccount {
            return BankAccount(iban, money, user.email)
        }

        fun createCreditCard(): CreditCard {
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }

            val pin = (1000..9999).random()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, Timestamp.now().toDate().month, Timestamp.now().toDate().day))

            return CreditCard(creditCardNumber, "Tarj. Debito *0386" , money, pin, cvv, caducityTime, CreditCardType.Debito, iban)
        }

        fun createIban(): String {
            var bankNumber = PREFIX_BANK

            for (i in 1..20) {
                bankNumber += (0..9).random()
            }

            return bankNumber
        }
    }
}