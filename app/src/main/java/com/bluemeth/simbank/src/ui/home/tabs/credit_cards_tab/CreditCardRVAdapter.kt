package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.CreditCard
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CreditCardRVAdapter @Inject constructor() : RecyclerView.Adapter<CreditCardRVAdapter.CreditCardHolder>(){

    private lateinit var listener: RecyclerClickListener
    private var listData = mutableListOf<CreditCard>()
    private lateinit var userCardName: String

    fun setListData(data:MutableList<CreditCard>){
        listData = data
    }

    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }

    fun setUserName(userCardName: String) {
        this.userCardName = userCardName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        val cardHolder = CreditCardHolder(v)


        val card = cardHolder.itemView.findViewById<CardView>(R.id.card_view)
        card.setOnClickListener {
            listener.onItemClick(cardHolder.adapterPosition)
        }
        return cardHolder
    }

    override fun onBindViewHolder(holder: CreditCardHolder, position: Int) {
        val creditCard = listData[position]
        holder.bindView(creditCard)
    }

    override fun getItemCount(): Int {
        return if(listData.size > 0){
            listData.size
        }else{
            0
        }
    }

    inner class CreditCardHolder(itemView: View):ViewHolder(itemView){
        val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
        fun bindView(creditCard: CreditCard) {
            val cardNumber = itemView.findViewById<TextView>(R.id.txtCardNumber)
            cardNumber.text = formatCardNumber(creditCard.number)

            val cardCaducity = itemView.findViewById<TextView>(R.id.txtCardCaducity)
            cardCaducity.text = "${creditCard.caducity.toDate().month}/${creditCard.caducity.toDate().year + 1900}"

            val userName = itemView.findViewById<TextView>(R.id.tvUser)
            userName.text = userCardName
        }
    }

    private fun formatCardNumber(cardNumber: String): String {
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