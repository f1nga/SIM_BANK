package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Movement
import com.google.firebase.firestore.Query
import javax.inject.Inject

class MovementRepository @Inject constructor(private val firebase: FirebaseClient){

    companion object {
        const val MOVEMENT_COLLECTION = "movements"
        const val TITLE_FIELD = "title"
        const val DATE_FIELD = "date"
        const val ISINCOME_FIELD = "isIncome"
        const val PRICE_FIELD = "price"
        const val OWNER_FIELD = "owner"
    }


    fun getMovements(owner: String) : LiveData<MutableList<Movement>> {
        val movementsList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENT_COLLECTION)
            .whereEqualTo(OWNER_FIELD, owner)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                    listData.add(
                        Movement(
                            document.getString(TITLE_FIELD)!!,
                            document.getTimestamp(DATE_FIELD)!!,
                            document.getDouble(PRICE_FIELD)!!,
                            document.getBoolean(ISINCOME_FIELD)!!,
                            document.getString(OWNER_FIELD)!!
                        )
                    )
                }
                movementsList.value = listData
            }
            .addOnFailureListener { exception ->
                Log.w("HOOOL", "Error getting documents: ", exception)
            }

        return movementsList
    }
}