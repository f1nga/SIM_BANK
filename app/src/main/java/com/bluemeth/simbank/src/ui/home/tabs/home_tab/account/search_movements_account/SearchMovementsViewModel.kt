package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchImportsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchMovementsViewModel : ViewModel(){

    private val _viewState = MutableStateFlow(SearchMovementsViewState())
    val viewState: StateFlow<SearchMovementsViewState>
        get() = _viewState

    private val _navigateToMovementsList = MutableLiveData<Event<Boolean>>()
    val navigateToMovementsList: LiveData<Event<Boolean>>
        get() = _navigateToMovementsList

    fun onSearchSelected(searchImportsModel: SearchImportsModel) {
        val viewState = searchImportsModel.toSearchMovementsViewState()

        if (viewState.importsValidated() && searchImportsModel.isNotEmpty()) {
            _navigateToMovementsList.value = Event(true)
        } else {
            onNameFieldsChanged(searchImportsModel)
        }
    }

    fun onNameFieldsChanged(searchImportsModel: SearchImportsModel) {
        _viewState.value = searchImportsModel.toSearchMovementsViewState()
    }

    private fun isValidImports(sinceImport: Double, untilImport: Double) = untilImport > sinceImport

    private fun SearchImportsModel.toSearchMovementsViewState(): SearchMovementsViewState {
        return SearchMovementsViewState(
            isValidSinceImport = isValidImports(sinceImport, untilImport),
            isValidUntilImport = isValidImports(sinceImport, untilImport),
        )
    }
}