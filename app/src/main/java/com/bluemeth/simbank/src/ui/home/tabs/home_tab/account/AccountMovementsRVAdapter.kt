package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account

import android.annotation.SuppressLint
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

class AccountMovementsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<AccountMovementsRVAdapter.TransfersHolder>() {
    private lateinit var listener: OnItemClickListener
    private var listData = mutableListOf<Movement>()

    interface OnItemClickListener {
        fun onItemClick(movement: Movement)
    }

    fun setListData(data: MutableList<Movement>) {
        listData = data
    }

    fun setMovement(data: Movement) {
        listData.add(data)
    }

    fun clearListData() {
        listData.clear()
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfersHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.account_movements_item, parent, false)

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
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(movementHolder: Movement) {
            val date = itemView.findViewById<TextView>(R.id.tvDate)
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val subject = itemView.findViewById<TextView>(R.id.tvSubject)
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            val arrow = itemView.findViewById<ImageView>(R.id.ivArrowIncome)
            val tvRemainingMoney = itemView.findViewById<TextView>(R.id.tvRemainingMoney)

            date.text = Methods.formateDateBizum(movementHolder.date.toDate())

            title.text = when(movementHolder.payment_type) {
                PaymentType.Bizum -> "Bizum"
                PaymentType.Transfer -> "Transferencia realizada"
                else -> movementHolder.beneficiary_name
            }

            if (movementHolder.isIncome) {
                price.text = "+${Methods.formatMoney(movementHolder.amount)}"
                arrow.setImageResource(R.drawable.arrow_win)
                tvRemainingMoney.text = Methods.formatMoney(movementHolder.beneficiary_remaining_money)
                subject.text =
                    if (movementHolder.subject != "") movementHolder.subject else "Recibido: sin concepto"
            } else {
                price.text = "-${Methods.formatMoney(movementHolder.amount)}"
                arrow.setImageResource(R.drawable.arrow_lose)
                tvRemainingMoney.text = Methods.formatMoney(movementHolder.remaining_money)
                subject.text =
                    if (movementHolder.subject != "") movementHolder.subject else "Enviado: sin concepto"
            }

            itemView.setOnClickListener {
                listener.onItemClick(movementHolder)
            }
        }
    }
}