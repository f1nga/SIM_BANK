package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class TransferHistoryRVAdapter @Inject constructor() :
    RecyclerView.Adapter<TransferHistoryRVAdapter.TransferHolder>() {
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

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_transfer, parent, false)

        return TransferHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransferHolder, position: Int) {
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

    inner class TransferHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(transferHolder: Movement) {
            val date = itemView.findViewById<TextView>(R.id.tvDate)
            val beneficiary = itemView.findViewById<TextView>(R.id.tvBeneficiary)
            val iban = itemView.findViewById<TextView>(R.id.tvIban)
            val import = itemView.findViewById<TextView>(R.id.tvImport)
            val subject = itemView.findViewById<TextView>(R.id.tvSubject)

            date.text = Methods.formatLongDate(transferHolder.date.toDate())
            beneficiary.text = transferHolder.beneficiary_name
            iban.text = transferHolder.beneficiary_iban
            subject.text = if(transferHolder.subject != "") transferHolder.subject else "sin concepto"
            import.text = "-${Methods.formatMoney(transferHolder.amount)}"

            itemView.setOnClickListener {
                listener.onItemClick(transferHolder)
            }
        }
    }
}