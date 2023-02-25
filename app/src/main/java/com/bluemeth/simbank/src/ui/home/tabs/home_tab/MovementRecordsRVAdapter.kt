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
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class MovementRecordsRVAdapter @Inject constructor() : RecyclerView.Adapter<MovementRecordsRVAdapter.MovementHolder>(){
    private lateinit var listener: onItemClickListener
    private var listData = listOf<Movement>()
    private var reaminingMoney: Double = 0.0

    interface onItemClickListener {
        fun onItemClick(creditCard: Movement)
    }

    fun setRemainingMoney(money: Double) {
        reaminingMoney = money
    }

    fun setListData(data:List<Movement>){
        listData = data
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.movement_item, parent, false)

        return MovementHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MovementHolder, position: Int) {
        val movement = listData[position]
        holder.bindView(movement)
    }

    override fun getItemCount(): Int {
        return if(listData.isNotEmpty()){
            listData.size
        }else{
            0
        }
    }

    inner class MovementHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(movementHolder: Movement) {
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val recordDate = itemView.findViewById<TextView>(R.id.tvRecordDate)
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            val arrow = itemView.findViewById<ImageView>(R.id.ivArrowIncome)
            val tvRemainingMoney = itemView.findViewById<TextView>(R.id.tvRemainingMoney)

            title.text = movementHolder.title
            recordDate.text = Methods.formateDateMovement(movementHolder.date.toDate())

            if(movementHolder.isIncome) {
                price.text = "+${Methods.formatMoney(movementHolder.price)}"
                arrow.setImageResource(R.drawable.arrow_win)
                tvRemainingMoney.text = "${Methods.formatMoney(reaminingMoney + movementHolder.price)}"
                reaminingMoney += movementHolder.price
            } else {
                price.text = "-${Methods.formatMoney(movementHolder.price)}"
                tvRemainingMoney.text = "${Methods.formatMoney(reaminingMoney - movementHolder.price)}"
                reaminingMoney -= movementHolder.price
            }

            itemView.setOnClickListener {
                listener.onItemClick(movementHolder)
            }
        }
    }
}