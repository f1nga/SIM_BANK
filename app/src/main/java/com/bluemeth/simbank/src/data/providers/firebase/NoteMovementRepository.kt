package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.NoteMovement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteMovementRepository @Inject constructor(private val firebase: FirebaseClient) {

    private companion object {
        const val NOTE_MOVEMENTS_COLLECTION = "notes_movements"
        const val NOTE_FIELD = "note"
        const val ID_FIELD = "id"
        const val MOVEMENT_ID_FIELD = "movementId"
        const val USER_EMAIL_FIELD = "user_email"
    }

    suspend fun insertNoteMovement(noteMovement: NoteMovement) = runCatching {
        firebase.db
            .collection(NOTE_MOVEMENTS_COLLECTION)
            .document(noteMovement.id)
            .set(noteMovement)
            .await()

    }.isSuccess

    suspend fun updateNoteMovement(id: String, note: String) = runCatching {
        firebase.db
            .collection(NOTE_MOVEMENTS_COLLECTION)
            .document(id)
            .update(NOTE_FIELD, note)
            .await()
    }.isSuccess

    suspend fun deleteNoteMovement(id: String) = runCatching {
        firebase.db
            .collection(NOTE_MOVEMENTS_COLLECTION)
            .document(id)
            .delete()
            .await()
    }.isSuccess

    suspend fun noteExists(movementID: String, userEmail: String): MutableLiveData<NoteMovement?> {
        val noteFinded: MutableLiveData<NoteMovement?> = MutableLiveData<NoteMovement?>()

        firebase.db
            .collection(NOTE_MOVEMENTS_COLLECTION)
            .whereEqualTo(MOVEMENT_ID_FIELD, movementID)
            .whereEqualTo(USER_EMAIL_FIELD, userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() != 0)
                    noteFinded.value = NoteMovement(
                        id = documents.first().getString(ID_FIELD)!!,
                        note = documents.first().getString(NOTE_FIELD)!!,
                        movementId = documents.first().getString(MOVEMENT_ID_FIELD)!!,
                        user_email = documents.first().getString(USER_EMAIL_FIELD)!!
                    )
                else noteFinded.value = null
            }.await()

        return noteFinded
    }
}