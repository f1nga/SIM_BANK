package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.model.CreditCardInfo
import javax.inject.Inject

class AddCreditCardRVAdapter @Inject constructor() : RecyclerView.Adapter<AddCreditCardRVAdapter.InfoCardHolder>(){
    private lateinit var listener: OnItemClickListener
    private var listData = listOf<CreditCardInfo>()

    interface OnItemClickListener {
        fun onItemClick(creditCard: CreditCardInfo)
    }

    fun setListData(data:List<CreditCardInfo>){
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoCardHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_credit_card_item, parent, false)

        return InfoCardHolder(v)
    }

    override fun onBindViewHolder(holder: InfoCardHolder, position: Int) {
        val infoCard = listData[position]
        holder.bindView(infoCard)
    }

    override fun getItemCount(): Int {
        return if(listData.isNotEmpty()){
            listData.size
        }else{
            0
        }
    }

    inner class InfoCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(infoCard: CreditCardInfo) {
            val imgCard = itemView.findViewById<ImageView>(R.id.cardImageView)
            imgCard.setImageResource(infoCard.cardImg)

            val typeCard = itemView.findViewById<TextView>(R.id.cardType)
            typeCard.text = infoCard.cardType

            val moreinfoCard = itemView.findViewById<TextView>(R.id.cardInfo)
            moreinfoCard.text = infoCard.cardInfo

            val descriptionCard = itemView.findViewById<TextView>(R.id.cardDescription)
            descriptionCard.text = infoCard.cardDescripton

            val btnContratar = itemView.findViewById<TextView>(R.id.btnContratar)

            itemView.setOnClickListener {
                listener.onItemClick(infoCard)
            }
            btnContratar.setOnClickListener {
                listener.onItemClick(infoCard)
            }
        }
    }
}