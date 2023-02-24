package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class MovementRecordsRVAdapter @Inject constructor() : RecyclerView.Adapter<MovementRecordsRVAdapter.MovementHolder>(){
    private lateinit var listener: RecyclerClickListener
    private var listData = listOf<Movement>()

    fun setListData(data:List<Movement>){
        listData = data
    }

    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.movement_item, parent, false)
        val movementHolder = MovementHolder(v)


        val movement = movementHolder.itemView.findViewById<CardView>(R.id.card_viewMovement)
        movement.setOnClickListener {
            listener.onItemClick(movementHolder.adapterPosition)
        }
        return movementHolder
    }

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
        fun bindView(movementHolder: Movement) {
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            title.text = movementHolder.title
            val recordDate = itemView.findViewById<TextView>(R.id.recordDate)
            recordDate.text = Methods.formatDate(movementHolder.date.toDate())
            val price = itemView.findViewById<TextView>(R.id.tvPrice)
            price.text = Methods.formatMoney(movementHolder.price)

        }
    }
}