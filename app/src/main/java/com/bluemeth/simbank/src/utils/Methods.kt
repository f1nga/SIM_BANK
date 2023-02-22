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
    }
}