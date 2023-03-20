package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function

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
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class BizumHistoryRVAdapter @Inject constructor() :
    RecyclerView.Adapter<BizumHistoryRVAdapter.BizumHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BizumHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bizum_item, parent, false)

        return BizumHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BizumHolder, position: Int) {
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

    inner class BizumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(bizumHolder: Movement) {
            val date = itemView.findViewById<TextView>(R.id.tvDate)
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            val subject = itemView.findViewById<TextView>(R.id.tvSubject)
            val isIncome = itemView.findViewById<ImageView>(R.id.ivArrowIncome)

            date.text = Methods.formateDateBizum(bizumHolder.date.toDate())
            title.text = if(bizumHolder.isIncome) {
                 "Recibido de ${Methods.splitNameAndCapitalsSurnames(bizumHolder.beneficiary_name)}"
            } else {
                "Enviado a ${Methods.splitNameAndCapitalsSurnames(bizumHolder.beneficiary_name)}"
            }

            price.text = Methods.formatMoney(bizumHolder.amount)
            subject.text = bizumHolder.subject
            if (bizumHolder.isIncome) {
                price.text = "+${Methods.formatMoney(bizumHolder.amount)}"
                isIncome.setImageResource(R.drawable.arrow_win)
            } else {
                price.text = "-${Methods.formatMoney(bizumHolder.amount)}"
                isIncome.setImageResource(R.drawable.arrow_lose)
            }

            itemView.setOnClickListener {
                listener.onItemClick(bizumHolder)
            }
        }
    }
}