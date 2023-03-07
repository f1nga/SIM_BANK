package com.bluemeth.simbank.src.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.random.Random

class Methods {
    companion object {
        fun formatDateCard(date: Date): String {
            val newDate = if (date.month < 10) {
                "0${date.month + 1}"
            } else {
                (date.month + 1).toString()
            }
            val year = date.year + 1900

            return "$newDate/${year.toString()[2]}${year.toString()[3]}"
        }

        fun formatDateCardInfo(date: Date): String {
            val day = if (date.date < 10) {
                "0${date.date}"
            } else {
                (date.date).toString()
            }
            val newDate = if (date.month < 10) {
                "0${date.month + 1}"
            } else {
                (date.month + 1).toString()
            }
            val year = date.year + 1900

            return "$day/$newDate/$year"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formateDateMovement(date: Date): String {

            val currentTime = Timestamp.now().toDate()
            val dateMonth = if (date.month == 0) 12 else date.month + 1

            if (date.year + 1900 == currentTime.year + 1900 && dateMonth == currentTime.month + 1 && date.date == currentTime.date) {
                return "Hoy"
            } else if (date.year + 1900 == currentTime.year + 1900 && dateMonth == currentTime.month + 1 && date.date + 1 == currentTime.date) {
                return "Ayer"
            } else {
                val month = when (dateMonth) {
                    1 -> "Ene"
                    2 -> "Feb"
                    3 -> "Mar"
                    4 -> "Abr"
                    5 -> "May"
                    6 -> "Jun"
                    7 -> "Jul"
                    8 -> "Ago"
                    9 -> "Set"
                    10 -> "Oct"
                    11 -> "Nov"
                    else -> "Dic"
                }

                if (date.year + 1900 != currentTime.year + 1900) {
                    return "${date.date} $month ${date.year + 1900}"
                }

                return "${date.date} $month"
            }
        }

        fun formatCardNumber(cardNumber: String): String {
            var number = ""

            for (i in 0..3) {
                number += cardNumber[i]
            }

            number += " "

            for (i in 4..7) {
                number += cardNumber[i]
            }

            number += " "

            for (i in 8..11) {
                number += cardNumber[i]
            }

            number += " "

            for (i in 12..15) {
                number += cardNumber[i]
            }

            return number
        }

        fun formatMoney(money: Double): String {
            val firstMoney = money.toString().split(".")[0]
            val secondMoney = money.toString().split(".")[1]

            val newMoney = if (secondMoney.length == 1) {
                "$firstMoney,${secondMoney}0"
            } else {
                "$firstMoney,${secondMoney}"
            }

            var moneyText = ""

            if (firstMoney.toInt() > 9999) {
                moneyText += "${newMoney[0]}${newMoney[1]}."
                for (i in 2 until newMoney.length) {
                    moneyText += newMoney[i]
                }
                return "$moneyText€"

            } else if (firstMoney.toInt() > 999) {
                moneyText += "${newMoney[0]}."
                for (i in 1 until newMoney.length) {
                    moneyText += newMoney[i]
                }
                return "$moneyText€"
            }

            return "$newMoney€"
        }

        fun formatShortIban(iban: String): String {
            var bankiban = ""

            for (i in 20..23) {
                bankiban += iban[i]
            }

            return bankiban
        }

        fun formatPhoneNumber(phone: Int): String {
            var phoneNumber = ""

            for (i in 0..2) {
                phoneNumber += phone.toString()[i]
            }

            phoneNumber += " "

            for (i in 3..4) {
                phoneNumber += phone.toString()[i]
            }

            phoneNumber += " "

            for (i in 5..6) {
                phoneNumber += phone.toString()[i]
            }

            phoneNumber += " "

            for (i in 7..8) {
                phoneNumber += phone.toString()[i]
            }

            return "+34 $phoneNumber"
        }

        fun formatIbanNumber(iban: String): String {
            var newIban = formatCardNumber(iban)

            newIban += " "

            for (i in 16..19) {
                newIban += iban[i]
            }

            newIban += " "

            for (i in 20..23) {
                newIban += iban[i]
            }

            return newIban
        }



        fun generateMoneyBank() = Random.nextDouble(10000.0, 20000.0)
        fun generateMoneyCreditCard() = Random.nextDouble(1000.0, 5000.0)

        fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_DOWN
            return df.format(number).toDouble()
        }

        fun generateLongNumber(numberLength: Int): String {
            var finalNumber = ""

            for (i in 1..numberLength) {
                finalNumber += (0..9).random()
            }

            return finalNumber
        }

        fun splitName(name: String) = name.split(" ")[0]

        fun splitNameProfile(name: String): String {
            val firstName = name.split(" ")[0]
            val secondName = name.split(" ")[1]

            return "${firstName[0]}${secondName[0]}"
        }

        fun splitBeneficiaryName(name: String): String {
            if(name[0].isDigit()) return "#"
            if(name.split(" ").size  == 1) return "${name[0]}"

            val firstName = name.split(" ")[0]
            val secondName = name.split(" ")[1]

            return "${firstName[0]}${secondName[0]}"
        }

        fun splitEuro(money: String) : String {
            val firstMoney = money.split("€")[0]

            val thirdMoney = firstMoney.replace(".", "")

            return thirdMoney.split(",")[0]
        }

        fun splitEuroDouble(money: String) : Double {
            val firstMoney = money.split("€")[0]

            val thirdMoney = firstMoney.replace(".", "")

            return thirdMoney.split(",")[0].toDouble()
        }

        fun splitEuroWithDecimal(money: String): String {
            return money.split("€")[0]
        }

        fun generateToken() = UUID.randomUUID().toString()

    }
}