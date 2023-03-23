package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.user_missions

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.data.providers.MissionsProvider

class UserMissionsViewModel:ViewModel() {

    fun getMissions(missionsComplete : List<String>): MutableList<Mission> {
        val listMissions = MissionsProvider.getListMissions()

        listMissions.forEach {mission ->
            for(userMission in missionsComplete) {
                if(userMission == mission.name){
                    mission.done = true
                }
            }
        }

        return listMissions
    }
}