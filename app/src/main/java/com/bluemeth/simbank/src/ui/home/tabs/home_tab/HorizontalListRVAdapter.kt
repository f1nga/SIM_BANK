package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class HorizontalListRVAdapter @Inject constructor() : RecyclerView.Adapter<HorizontalListRVAdapter.HomeHeaderHolder>(){
    private lateinit var listener: RecyclerClickListener
    private var listData = listOf<HomeHeader>()

    fun setListData(data:List<HomeHeader>){
        listData = data
    }

    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHeaderHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_header_item, parent, false)
        val cardHolder = HomeHeaderHolder(v)


        val card = cardHolder.itemView.findViewById<CardView>(R.id.card_view)
        card.setOnClickListener {
            listener.onItemClick(cardHolder.adapterPosition)
        }
        return cardHolder
    }

    override fun onBindViewHolder(holder: HomeHeaderHolder, position: Int) {
        val homeHeader = listData[position]
        holder.bindView(homeHeader)
    }

    override fun getItemCount(): Int {
        return if(listData.size > 0){
            listData.size
        }else{
            0
        }
    }

    inner class HomeHeaderHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(homeHeader: HomeHeader) {
            val moneyBank = itemView.findViewById<TextView>(R.id.txtMoneyBank)
            moneyBank.text = "${homeHeader.money.toString()}€"

            val subtitle = itemView.findViewById<TextView>(R.id.txtSubtitle)
            subtitle.text = homeHeader.subtitle

        }
    }
}