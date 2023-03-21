package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.info_account

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.NoteMovement
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoAccountViewModel @Inject constructor(
    private val bankAccountRepository: BankAccountRepository
) : ViewModel(){

    private companion object {
        const val MIN_ALIAS_LENGTH = 4
    }

    private val _aliasChanged = MutableLiveData<String>()
    val aliasChanged: LiveData<String>
        get() = _aliasChanged

    fun onAcceptAliasSelected(context: Context, iban:String, alias: String) {
        if (isValidAlias(alias)) {
            viewModelScope.launch {
                val aliasUpdated = bankAccountRepository.updateAlias(iban, alias)

                if (aliasUpdated) _aliasChanged.value = alias
                else Timber.tag("ERROR").w("Note not updated")
            }
        } else {
            Toast.makeText(context, "El alias debe contener mínimo 4 caracteres", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidAlias(note: String) = note.length >= MIN_ALIAS_LENGTH
}