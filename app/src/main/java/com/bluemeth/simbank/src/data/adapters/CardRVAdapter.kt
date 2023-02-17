package com.bluemeth.simbank.src.data.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import com.bluemeth.simbank.src.data.models.CreditCard
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bluemeth.simbank.R

class CardRVAdapter(private val context: Context) : RecyclerView.Adapter<CardRVAdapter.CreditCardHolder>(){

    private lateinit var listener: RecyclerClickListener

    private var listData = mutableListOf<CreditCard>()

    fun setListData(data:MutableList<CreditCard>){
        listData = data
    }

    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
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
        var creditCard = listData[position]
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
        fun bindView(creditCard: CreditCard) {
            val cardNumber = itemView.findViewById<TextView>(R.id.txtCardNumber)
            cardNumber.text = creditCard.number
            val cardCaducity = itemView.findViewById<TextView>(R.id.txtCardCaducity)
            cardCaducity.text = creditCard.caducity.toDate().toString()
        }
    }
}