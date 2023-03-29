package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class HorizontalListRVAdapter @Inject constructor() :
    RecyclerView.Adapter<HorizontalListRVAdapter.HomeHeaderHolder>() {
    private lateinit var listener: onItemClickListener
    private var listData = listOf<HomeHeader>()

    interface onItemClickListener {
        fun onItemClick(creditCard: HomeHeader)
    }

    fun setListData(data: List<HomeHeader>) {
        listData = data
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHeaderHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_header, parent, false)

        return HomeHeaderHolder(v)
    }

    override fun onBindViewHolder(holder: HomeHeaderHolder, position: Int) {
        val homeHeader = listData[position]
        holder.bindView(homeHeader)
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            0
        }
    }

    inner class HomeHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(homeHeader: HomeHeader) {
            val moneyBank = itemView.findViewById<TextView>(R.id.txtMoneyBank)
            //moneyBank.text = "${homeHeader.money}€"
            moneyBank.text = Methods.formatMoney(homeHeader.money)
            val subtitle = itemView.findViewById<TextView>(R.id.txtSubtitle)
            subtitle.text = homeHeader.subtitle

            itemView.setOnClickListener {
                listener.onItemClick(homeHeader)
            }

        }
    }
}