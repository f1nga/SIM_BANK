package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.NoteMovement
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.NoteMovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovementsDetailsViewModel @Inject constructor(
    private val noteMovementRepository: NoteMovementRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private companion object {
        const val NOTE_MAX_LENGTH = 60
    }

    private lateinit var _movement: Movement
    val movement: Movement
        get() = _movement

    private val _noteChanged = MutableLiveData<String>()
    val noteChanged: LiveData<String>
        get() = _noteChanged

    fun setMovement(movement: Movement) {
        _movement = movement
    }

    fun onAcceptNoteSelected(note: String) {
        if (isValidNote(note)) {
            viewModelScope.launch {
                noteMovementRepository.noteExists(_movement.id, authenticationRepository.getCurrentUser().email!!)
                    .observeForever { noteFinded ->

                    if (noteFinded != null) {
                        if(note.isNotEmpty()) updateNoteFromDB(noteFinded.id, note)
                        else deleteNoteFromDB(noteFinded.id)
                    }
                    else {
                        if(note.isNotEmpty()) insertNoteFromDB(note)
                    }
                }
            }
        }
    }

    fun noteExists(): MutableLiveData<NoteMovement?> {
        val noteFinded: MutableLiveData<NoteMovement?> = MutableLiveData<NoteMovement?>()

        viewModelScope.launch {
            noteMovementRepository.noteExists(_movement.id, authenticationRepository.getCurrentUser().email!!).observeForever {
                noteFinded.value = it
            }
        }

        return noteFinded
    }

    private fun updateNoteFromDB(id: String, note: String) {
        viewModelScope.launch {
            val noteUpdated = noteMovementRepository.updateNoteMovement(
                id, note
            )

            if (noteUpdated) _noteChanged.value = note
            else Timber.tag("ERROR").w("Note not updated")
        }
    }

    private fun insertNoteFromDB(note: String) {
        viewModelScope.launch {
            val noteInserted = noteMovementRepository.insertNoteMovement(
                NoteMovement(
                    note = note,
                    movementId = _movement.id,
                    user_email = authenticationRepository.getCurrentUser().email!!
                )
            )

            if (noteInserted) _noteChanged.value = note
            else Timber.tag("ERROR").w("Note not inserted")
        }
    }

    private fun deleteNoteFromDB(id: String) {
        viewModelScope.launch {
            val noteDeleted = noteMovementRepository.deleteNoteMovement(id)

            if (noteDeleted) _noteChanged.value = "Añade tu nota aquí"
            else Timber.tag("ERROR").w("Note not deleted")
        }
    }

    private fun isValidNote(note: String) = note.length <= NOTE_MAX_LENGTH
}