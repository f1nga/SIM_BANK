package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Transfer
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class TransfersRVAdapter @Inject constructor() :
    RecyclerView.Adapter<TransfersRVAdapter.TransfersHolder>() {
    private lateinit var listener: onItemClickListener
    private var listData = listOf<Transfer>()
    private var reaminingMoney: Double = 0.0

    interface onItemClickListener {
        fun onItemClick(creditCard: Transfer)
    }

    fun setRemainingMoney(money: Double) {
        reaminingMoney = money
    }

    fun setListData(data: List<Transfer>) {
        listData = data
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfersHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.movement_item, parent, false)

        return TransfersHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransfersHolder, position: Int) {
        val movement = listData[position]
        holder.bindView(movement)
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            0
        }
    }

    inner class TransfersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(transferHolder: Transfer) {
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val recordDate = itemView.findViewById<TextView>(R.id.tvRecordDate)
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            val arrow = itemView.findViewById<ImageView>(R.id.ivArrowIncome)
            val tvRemainingMoney = itemView.findViewById<TextView>(R.id.tvRemainingMoney)

            title.text = transferHolder.beneficiary_name
            recordDate.text = Methods.formateDateMovement(transferHolder.date.toDate())

            if (transferHolder.isIncome) {
                price.text = "+${Methods.formatMoney(transferHolder.amount)}"
                arrow.setImageResource(R.drawable.arrow_win)
//                tvRemainingMoney.text =
//                    "${Methods.formatMoney(reaminingMoney + transferHolder.amount)}"
//                reaminingMoney += transferHolder.amount
            } else {
                price.text = "-${Methods.formatMoney(transferHolder.amount)}"
//                tvRemainingMoney.text =
//                    "${Methods.formatMoney(reaminingMoney - transferHolder.amount)}"
//                reaminingMoney -= transferHolder.amount
            }
            tvRemainingMoney.text = Methods.formatMoney(transferHolder.remaining_money)

            itemView.setOnClickListener {
                listener.onItemClick(transferHolder)
            }
        }
    }
}