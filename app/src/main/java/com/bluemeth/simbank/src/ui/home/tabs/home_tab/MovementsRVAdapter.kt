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
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class MovementsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<MovementsRVAdapter.TransfersHolder>() {
    private lateinit var listener: OnItemClickListener
    private var listData = listOf<Movement>()

    interface OnItemClickListener {
        fun onItemClick(movement: Movement)
    }

    fun setListData(data: List<Movement>) {
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
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
        fun bindView(movementHolder: Movement) {
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val recordDate = itemView.findViewById<TextView>(R.id.tvRecordDate)
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            val arrow = itemView.findViewById<ImageView>(R.id.ivArrowIncome)
            val tvRemainingMoney = itemView.findViewById<TextView>(R.id.tvRemainingMoney)

            title.text = when(movementHolder.payment_type) {
                PaymentType.Bizum -> "Bizum"
                PaymentType.Transfer -> "Transferencia realizada"
                else -> movementHolder.beneficiary_name
            }

            recordDate.text = Methods.formateDateMovement(movementHolder.date.toDate())

            if (movementHolder.isIncome) {
                price.text = "+${Methods.formatMoney(movementHolder.amount)}"
                arrow.setImageResource(R.drawable.arrow_win)
            } else {
                price.text = "-${Methods.formatMoney(movementHolder.amount)}"
                arrow.setImageResource(R.drawable.arrow_lose)
            }
            tvRemainingMoney.text = Methods.formatMoney(movementHolder.remaining_money)

            itemView.setOnClickListener {
                listener.onItemClick(movementHolder)
            }
        }
    }
}