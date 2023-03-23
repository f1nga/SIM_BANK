package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.account_movements_filtered

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentMovementsFilteredListBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.AccountMovementsRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchMovementsModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchMovementsType
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MovementsFilteredListFragment : Fragment() {

    private lateinit var binding: FragmentMovementsFilteredListBinding
    private val movementsFilteredListViewModel: MovementsFilteredListViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val adapter: AccountMovementsRVAdapter = AccountMovementsRVAdapter()
    private lateinit var searchMovementsModel: SearchMovementsModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovementsFilteredListBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        setRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRecyclerView() {
        val movementsRecyclerView = binding.rvMovements
        movementsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementsRecyclerView.setHasFixedSize(true)
        movementsRecyclerView.adapter = adapter

        adapter.setItemListener(object :
            AccountMovementsRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                when (movement.payment_type) {
                    PaymentType.Bizum -> {
                        goToBizumDetail()
                    }
                    else -> {
                        goToTransferDetail()
                    }
                }
            }
        })

        observeMovement()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeMovement() {
        searchMovementsModel = movementsFilteredListViewModel.searchMovementsModel

        when (searchMovementsModel.type) {
            SearchMovementsType.ALL -> {
                globalViewModel.getBankIban().observe(requireActivity()) {
                    globalViewModel.getMovementsFromDB(globalViewModel.getUserAuth().email!!, it)
                        .observe(requireActivity()) { movementsList ->
                            addToAdapterList(movementsList)
                        }
                }

            }
            SearchMovementsType.INCOME -> {
                globalViewModel.getReceivedMovementsFromDB()
                    .observe(requireActivity()) { movementsList ->
                        addToAdapterList(movementsList)
                    }

            }
            else -> {
                globalViewModel.getSendedMovementsFromDB()
                    .observe(requireActivity()) { movementsList ->
                        addToAdapterList(movementsList)
                    }
            }
        }
    }

    private fun addToAdapterList(movementsList: List<Movement>) {
        for (movement in movementsList) {
            if (movementCoincide(movement)) adapter.setMovement(movement)
        }
        adapter.notifyDataSetChanged()
    }

    private fun movementCoincide(movement: Movement): Boolean {
        with(searchMovementsModel) {
            return (datesCoincide(sinceDate, untilDate, movement.date.toDate())
                    && textCoincide(text, movement)
                    && importCoincide(movement.amount, sinceImport, untilImport))
        }
    }

    private fun datesCoincide(sinceDate: Date?, untilDate: Date, movementDate: Date): Boolean {
        if (sinceDate == null) return true

        movementDate.year += 1900

        if (movementDate.year in sinceDate.year + 1 until untilDate.year) return true
        else if (movementDate.year == sinceDate.year && movementDate.year == untilDate.year) {
            if (movementDate.month in sinceDate.month + 1 until untilDate.month) return true
            else if (movementDate.month == sinceDate.month && movementDate.month != untilDate.month) {
                if (movementDate.date >= sinceDate.date) return true
            } else if (movementDate.month != sinceDate.month && movementDate.month == untilDate.month) {
                if (movementDate.date <= untilDate.date) return true
            } else if (movementDate.month == sinceDate.month && movementDate.month == untilDate.month) {
                if (movementDate.date in sinceDate.date..untilDate.date) return true
            }
        } else if (movementDate.year == sinceDate.year && movementDate.year != untilDate.year) {
            if (movementDate.month >= sinceDate.month) {
                if (movementDate.date >= sinceDate.date) return true
            }
        } else if (movementDate.year != sinceDate.year && movementDate.year == untilDate.year) {
            if (movementDate.month <= untilDate.month) {
                if (movementDate.date <= untilDate.date) return true
            }
        }

        return false
    }

    private fun textCoincide(text: String?, movement: Movement): Boolean {
        if (text == null) return true

        return (movement.payment_type.toString().lowercase(Locale.ROOT)
            .startsWith(text.lowercase(Locale.ROOT))
                || movement.subject.lowercase(Locale.ROOT).startsWith(text.lowercase(Locale.ROOT))
                || "transferencia realizada".startsWith(text.lowercase(Locale.ROOT))
                )
    }

    private fun importCoincide(
        movementImport: Double,
        sinceImport: Double?,
        untilImport: Double?
    ): Boolean {
        if (sinceImport == null && untilImport == null) return true

        return if (sinceImport != null && untilImport == null) movementImport >= sinceImport
        else if (sinceImport == null && untilImport != null) movementImport <= untilImport
        else movementImport in sinceImport!!..untilImport!!

    }

    private fun goToBizumDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_accountMovementsFilteredFragment_to_bizumDetailAccountFragment)
    }

    private fun goToTransferDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_accountMovementsFilteredFragment_to_transferDetailAccountFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_movements_list)
    }

//    private fun datesCoincide(sinceDate: Date, untilDate: Date, movementDate: Date): Boolean {
//        movementDate.year += 1900
//        log("year", "${movementDate.year} -- ${sinceDate.year} -- ${untilDate.year}")
//        log("month", "${movementDate.month} -- ${sinceDate.month} -- ${untilDate.month}")
//
//        if (movementDate.year in sinceDate.year +1 until untilDate.year) {
//            log("hool","1")
//
//            return true
//        }
//        else if(movementDate.year == sinceDate.year && movementDate.year == untilDate.year) {
//            log("hool","2")
//
//            if(movementDate.month in sinceDate.month +1 until untilDate.month) {
//                log("hool","3")
//                return true
//            } else if(movementDate.month == sinceDate.month && movementDate.month != untilDate.month) {
//                log("hool","4")
//                if(movementDate.date >= sinceDate.date) {
//                    log("hool","5")
//                    return true
//                }
//            } else if(movementDate.month != sinceDate.month && movementDate.month == untilDate.month) {
//                log("hool","6")
//                if(movementDate.date <= untilDate.date) {
//                    log("hool","7")
//                    return true
//                }
//            } else if(movementDate.month == sinceDate.month && movementDate.month == untilDate.month) {
//                log("hool","8")
//                if(movementDate.date in sinceDate.date  .. untilDate.date) {
//                    log("hool","9")
//                    return true
//                }
//            }
//
//        }
//
//        else if(movementDate.year == sinceDate.year && movementDate.year != untilDate.year) {
//            log("hool","10")
//            if(movementDate.month >= sinceDate.month) {
//                log("hool","11")
//                if(movementDate.date >= sinceDate.date){
//                    log("hool","12")
//                    return true
//                }
//            }
//        } else if(movementDate.year != sinceDate.year && movementDate.year == untilDate.year) {
//            log("hool","13")
//            if(movementDate.month <= untilDate.month) {
//                log("hool","14")
//                if(movementDate.date <= untilDate.date) {
//                    log("hool","15")
//                    return true
//                }
//            }
//        }
//
//        return false
//    }

}
