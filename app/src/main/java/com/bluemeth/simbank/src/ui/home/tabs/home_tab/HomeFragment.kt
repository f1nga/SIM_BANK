package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.data.providers.HomeHeaderProvider
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeTabViewModel: HomeTabViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val movementsDetailsViewModel: MovementsDetailsViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        setTextViews()
        initListeners()
        setHeaderRecyclerView()
        setMovementsRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.clAccount.setOnClickListener { goToAccount() }

        binding.swipe.setOnRefreshListener {
//            Handler(Looper.getMainLooper()).postDelayed({
//                refreshScreen()
//            }, 1000)


            Methods.setTimeout({ refreshScreen() }, 1000)
        }
    }

    private fun setHeaderRecyclerView() {

        val headerRecyclerView = binding.rvHeader
        headerRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        headerRecyclerView.setHasFixedSize(true)
        headerRecyclerView.adapter = homeTabViewModel.headerAdapter

        homeTabViewModel.headerAdapter.setItemListener(object :
            HorizontalListRVAdapter.onItemClickListener {
            override fun onItemClick(creditCard: HomeHeader) {
                toast("Feature not implemented")
            }
        })

        observeHeader()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeHeader() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            homeTabViewModel.headerAdapter.setListData(HomeHeaderProvider.getListHeader(it))
            homeTabViewModel.headerAdapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMovementsRecyclerView() {

        val movementRecyclerView = binding.rvHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = homeTabViewModel.transfersAdapter

        homeTabViewModel.transfersAdapter.setItemListener(object :
            MovementsRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                movementsDetailsViewModel.setMovement(movement)
                when (movement.payment_type) {
                    PaymentType.Bizum -> goToBizumDetail()
                    else -> goToTransferDetail()
                }
            }
        })

        observeMovements()
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeMovements() {

        globalViewModel.getBankIban().observe(requireActivity()) {
            globalViewModel.getMovementsFromDB(globalViewModel.getUserAuth().email!!, it)
                .observe(requireActivity()) { movementsList ->

                    homeTabViewModel.transfersAdapter.setListData(movementsList)
                    homeTabViewModel.transfersAdapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.clHistorialLoading.isVisible = false
                        binding.clHistorial.isVisible = true
                    }, 300)

                }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshScreen() {
        setTextViews()
        observeMovements()
        observeHeader()
        binding.swipe.isRefreshing = false
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            binding.tvDineroCuenta.text = Methods.formatMoney(it)
            binding.tvMoneyTotal.text = Methods.formatMoney(it)
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
            binding.tvAccountNumber.text = "Cuenta *${Methods.formatShortIban(it)}"
            binding.tvShortNumber.text = "· ${Methods.formatShortIban(it)}"
        }

        globalViewModel.getUserName().observe(requireActivity()) {
            activity?.findViewById<TextView>(R.id.tvNameDrawer)?.text = Methods.splitName(it)
        }
    }

    private fun goToBizumDetail() {
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_bizumDetailAccountFragment)
    }

    private fun goToTransferDetail() {
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_transferDetailAccountFragment)
    }

    private fun goToAccount() {
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_accountFragment)
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        globalViewModel.getUserName().observe(this) {
            val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
            tvTitle.text = "Hola, ${Methods.splitName(it)}"
        }
    }
}