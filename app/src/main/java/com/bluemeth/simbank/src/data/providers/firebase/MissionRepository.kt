package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MissionRepository @Inject constructor(private val firebase: FirebaseClient) {

    private companion object {
        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"
        const val EXP_FIELD = "exp"
        const val MISSIONS_COLLECTION = "missions"
    }


     fun getMissions(userMissionsComplete: List<String>): LiveData<MutableList<Mission>> {
        val missionsList = MutableLiveData<MutableList<Mission>>()


        firebase.db.collection(MISSIONS_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Mission>()

                for (document in documents) {
                    Log.i("1","${document.getString(ID_FIELD)}")
                    var done = true
                    for (mission in userMissionsComplete ){
                        Log.i("2","${document.getString(ID_FIELD)}")
                        if(document.getString(ID_FIELD) == mission){
                            done = true
                            break
                        }
                    }
                    listData.add(
                        Mission(
                            document.getString(ID_FIELD)!!,
                            document.getString(NAME_FIELD)!!,
                            document.getDouble(EXP_FIELD)!!.toInt(),
                            done
                        )
                    )
                }
                Log.i("3","${listData.toString()}")
                missionsList.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }

        return missionsList

    }

}