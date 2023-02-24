package com.bluemeth.simbank.src.utils

import java.util.*

class Methods {
    companion object {
         fun formatDate(date: Date): String {
             val newDate = if(date.month < 10) {
                 "0${date.month}"
             } else {
                 date.month.toString()
             }
            val year = date.year + 1900

            return "$newDate/${year.toString()[2]}${year.toString()[3]}"
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

            number += " "

            return number
        }

        fun splitName(name: String): String {
            return name.split(" ")[0]
        }

        fun formatMoney(money: Double): String {
            val firstMoney = money.toString().split(".")[0]
            val secondMoney = money.toString().split(".")[1]

            val newMoney = if(secondMoney.length == 1) {
                "$firstMoney,${secondMoney}0"
            } else {
                "$firstMoney,${secondMoney}"
            }

            var moneyText = ""

            if(firstMoney.toInt() > 9999) {
                moneyText += "${newMoney[0]}${newMoney[1]}."
                for (i in 2 until newMoney.length) {
                    moneyText += newMoney[i]
                }
                return "$moneyText€"

            } else if(firstMoney.toInt() > 999) {
                moneyText += "${newMoney[0]}."
                for (i in 1 until newMoney.length) {
                    moneyText += newMoney[i]
                }
                return "$moneyText€"
            }

            return "$newMoney€"
        }
    }
}