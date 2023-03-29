package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsHeader

class PromotionsHeaderRVAdapter :  RecyclerView.Adapter<PromotionsHeaderRVAdapter.PromotionsCardHolder>() {


    private lateinit var listener: OnItemClickListener
    private var listData = listOf<PromotionsHeader>()


    interface OnItemClickListener {
        fun onItemClick(promotionsHeader: PromotionsHeader)
    }

    fun setListData(data:MutableList<PromotionsHeader>){
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionsCardHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_info_promotions, parent, false)

        return PromotionsCardHolder(v)
    }

    override fun onBindViewHolder(holder: PromotionsCardHolder, position: Int) {
        val shopCard = listData[position]
        holder.bindView(shopCard)
    }

    override fun getItemCount(): Int {
        return if(listData.size > 0){
            listData.size
        }else{
            0
        }
    }

    inner class PromotionsCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(promotionsHeader: PromotionsHeader) {


            val imgItemPromotionsHeader = itemView.findViewById<ImageView>(R.id.ivCircle)
            imgItemPromotionsHeader.setImageResource(promotionsHeader.image)
            val nameItemPromotionsHeader = itemView.findViewById<TextView>(R.id.tvInfoName)
            nameItemPromotionsHeader.text = promotionsHeader.name



            itemView.setOnClickListener {
                listener.onItemClick(promotionsHeader)
            }
        }


    }
}