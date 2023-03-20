package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsFeatures

class PromotionsFeaturesRVAdapter :  RecyclerView.Adapter<PromotionsFeaturesRVAdapter.FeaturesCardHolder>() {


    private lateinit var listener: OnItemClickListener
    private var listData = listOf<PromotionsFeatures>()

    interface OnItemClickListener {
        fun onItemClick(promotionsFeatures: PromotionsFeatures)
    }

    fun setListData(data:MutableList<PromotionsFeatures>){
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturesCardHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.featured_promotions_item, parent, false)

        return FeaturesCardHolder(v)
    }

    override fun onBindViewHolder(holder: FeaturesCardHolder, position: Int) {
        val featureCard = listData[position]
        holder.bindView(featureCard)
    }

    override fun getItemCount(): Int {
        return if(listData.isNotEmpty()){
            listData.size
        }else{
            0
        }
    }

    inner class FeaturesCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(promotionsFeatures: PromotionsFeatures) {


            val imgItemFeaturesHeader = itemView.findViewById<ImageView>(R.id.ivImageFeature)
            imgItemFeaturesHeader.setImageResource(promotionsFeatures.image)
            val nameItemFeaturesHeader = itemView.findViewById<TextView>(R.id.tvFeatureName)
            nameItemFeaturesHeader.text = promotionsFeatures.nameProduct
            val priceItemFeaturesHeader = itemView.findViewById<TextView>(R.id.tvFeaturePrice)
            priceItemFeaturesHeader.text = promotionsFeatures.priceProduct




            itemView.setOnClickListener {
                listener.onItemClick(promotionsFeatures)
            }
        }


    }
}