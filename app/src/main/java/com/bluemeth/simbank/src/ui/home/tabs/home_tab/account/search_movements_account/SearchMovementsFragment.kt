package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentSearchMovementsBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.account_movements_filtered.MovementsFilteredListViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchImportsModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchMovementsModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchMovementsType
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchMovementsFragment : Fragment() {

    private lateinit var binding: FragmentSearchMovementsBinding
    private val movementsFilteredListViewModel: MovementsFilteredListViewModel by activityViewModels()
    private val searchMovementsViewModel: SearchMovementsViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchMovementsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        initListeners()
        initObservers()
        setSpinner()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        with(binding) {
            inputImportSinceText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputImportSinceText.setOnFocusChangeListener { _, hasFocus ->
                updateInputImports(
                    hasFocus
                )
            }

            inputImportUntilText.setOnFocusChangeListener { _, hasFocus ->
                updateInputImports(
                    hasFocus
                )
            }
            inputImportUntilText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)

            llDateSince.setOnClickListener { showCalendar(setSinceDate) }
            llDateUntil.setOnClickListener { showCalendar(setUntilDate) }
            btnSearch.setOnClickListener {
                    searchMovementsViewModel.onSearchSelected(
                        SearchImportsModel(
                            inputImportSinceText.text.toString(),
                            inputImportUntilText.text.toString()
                        )
                    )
                }

        }
    }

    private fun updateInputImports(hasFocus: Boolean) {
        if (!binding.inputImportSinceText.text.isNullOrBlank()
            && !binding.inputImportUntilText.text.isNullOrBlank()
        ) {
            searchMovementsViewModel.onNameFieldsChanged(
                SearchImportsModel(
                    binding.inputImportSinceText.text.toString(),
                    binding.inputImportUntilText.text.toString()
                )
            )
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            searchMovementsViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        searchMovementsViewModel.navigateToMovementsList.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                searchMovements()
                goToMovementsFiltered()
            }
        }
    }

    private fun updateUI(viewState: SearchMovementsViewState) {
        binding.inputImportSince.error =
            if (viewState.isValidSinceImport) null else {
                "El importe no puede ser superior a ${binding.inputImportUntilText.text}€"
            }

        binding.inputImportUntil.error =
            if (viewState.isValidUntilImport) null else {
                "El importe no puede ser inferior a ${binding.inputImportSinceText.text}€"
            }
    }

    private val setSinceDate =
        DatePickerDialog.OnDateSetListener { calendar, year, month, dayOfMonth ->
            setTextStyle()
            calendar.updateDate(year, month + 1, dayOfMonth)
            binding.tvSinceDate.text = "$dayOfMonth/${month + 1}/$year"
        }

    private val setUntilDate =
        DatePickerDialog.OnDateSetListener { calendar, year, month, dayOfMonth ->
            calendar.updateDate(year, month + 1, dayOfMonth)
            binding.tvUntilDate.text = "$dayOfMonth/${month + 1}/$year"
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar(listener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
            requireContext(),
            listener,
            LocalDate.now().year,
            LocalDate.now().monthValue - 1,
            LocalDate.now().dayOfMonth
        ).show()
    }

    private fun setSpinner() {
        val spinner = binding.spinnerType

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.movement_type_array,
            R.layout.item_spinner_text
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
            spinner.adapter = adapter
        }
    }

    private fun searchMovements() {
        with(binding) {
            movementsFilteredListViewModel.setSearchMovementsModel(
                SearchMovementsModel(
                    text = if (!inputTextText.text.isNullOrEmpty()) inputTextText.text.toString() else null,
                    sinceDate = formatDate(tvSinceDate.text.toString()),
                    untilDate = formatDate(tvUntilDate.text.toString())!!,
                    type = when (spinnerType.selectedItem) {
                        "Solo ingresos" -> SearchMovementsType.INCOME
                        "Solo gastos" -> SearchMovementsType.SPENT
                        else -> SearchMovementsType.ALL
                    },
                    sinceImport = if (!inputImportSinceText.text.isNullOrEmpty()) inputImportSinceText.text.toString()
                        .toDouble() else null,
                    untilImport = if (!inputImportUntilText.text.isNullOrEmpty()) inputImportUntilText.text.toString()
                        .toDouble() else null
                )
            )
        }
    }

    private fun setTextStyle() {
        binding.tvSinceDate.apply {
            setTextColor(Color.parseColor("#FFFFFF"))
            textSize = 18F
            setTypeface(binding.tvSinceDate.typeface, Typeface.ITALIC)
            setTypeface(binding.tvSinceDate.typeface, Typeface.BOLD)
        }
    }

    private fun formatDate(date: String): Date? {
        date.split("/").also {
            if (it.size == 1) return null
            return Date(it[2].toInt(), it[1].toInt() - 1, it[0].toInt())
        }
    }

    private fun goToMovementsFiltered() {
        view?.findNavController()
            ?.navigate(R.id.action_searchMovementsFragment_to_accountMovementsFilteredFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_search_movements)
    }
}