package com.bluemeth.simbank.src.ui.drawer.settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsHeader

class SettingsRVAdapter :  RecyclerView.Adapter<SettingsRVAdapter.SettingsCardHolder>() {


    private var listData = listOf<String>()



    fun setListData(data:List<String>){
        listData = data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsCardHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false)

        return SettingsCardHolder(v)
    }

    override fun onBindViewHolder(holder: SettingsCardHolder, position: Int) {
        val settingCard = listData[position]
        holder.bindView(settingCard)
    }

    override fun getItemCount(): Int {
        return if(listData.size > 0){
            listData.size
        }else{
            0
        }
    }

    inner class SettingsCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(name: String) {

            val nameSetting = itemView.findViewById<TextView>(R.id.tvTitle)
            nameSetting.text = name

        }
    }
}