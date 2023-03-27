package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.user_missions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Mission

class UserMissionsRVAdapter :  RecyclerView.Adapter<UserMissionsRVAdapter.MissionsCardHolder>() {

    private lateinit var listener: OnItemClickListener
    private var listData = listOf<Mission>()

    interface OnItemClickListener {
        fun onItemClick(mission: Mission)
    }

    fun setListData(data:MutableList<Mission>){
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionsCardHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mission_card, parent, false)

        return MissionsCardHolder(v)
    }

    override fun onBindViewHolder(holder: MissionsCardHolder, position: Int) {
        val missionCard = listData[position]
        holder.bindView(missionCard)
    }

    override fun getItemCount(): Int {
        return if(listData.isNotEmpty()){
            listData.size
        }else{
            0
        }
    }

    inner class MissionsCardHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bindView(mission: Mission) {

            val missionName = itemView.findViewById<TextView>(R.id.tvMissionName)
            missionName.text = mission.name
            val missionExp = itemView.findViewById<TextView>(R.id.tvMissionExp)
            missionExp.text = mission.exp.toString() + " EXP"
            val missionTick = itemView.findViewById<ImageView>(R.id.ivMissionTick)

            if(mission.done) missionTick.setImageResource(R.drawable.ic_tick)

            itemView.setOnClickListener {
                listener.onItemClick(mission)
            }
        }
    }
}