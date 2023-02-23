package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card_tabs

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card_tabs.model.CreditCardInfo
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import org.w3c.dom.Text
import javax.inject.Inject

class AddCreditCardRVAdapter @Inject constructor() : RecyclerView.Adapter<AddCreditCardRVAdapter.InfoCardHolder>(){
    private lateinit var listener: RecyclerClickListener
    private var listData = listOf<CreditCardInfo>()

    fun setListData(data:List<CreditCardInfo>){
        listData = data
    }

    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoCardHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.add_credit_card_item, parent, false)
        val cardHolder = InfoCardHolder(v)


        val card = cardHolder.itemView.findViewById<CardView>(R.id.card_view)
        card.setOnClickListener {
            listener.onItemClick(cardHolder.adapterPosition)
        }
        return cardHolder
    }

    override fun onBindViewHolder(holder: InfoCardHolder, position: Int) {
        val infoCard = listData[position]
        holder.bindView(infoCard)
    }

    override fun getItemCount(): Int {
        return if(listData.size > 0){
            listData.size
        }else{
            0
        }
    }

    inner class InfoCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(infoCard: CreditCardInfo) {
            var imgCard = itemView.findViewById<TextView>(R.id.cardImageView)
            imgCard.text = infoCard.cardImg.toString()

            val typeCard = itemView.findViewById<TextView>(R.id.cardType)
            typeCard.text = infoCard.cardType

            val moreinfoCard = itemView.findViewById<TextView>(R.id.cardInfo)
            moreinfoCard.text = infoCard.cardInfo
        }
    }
}