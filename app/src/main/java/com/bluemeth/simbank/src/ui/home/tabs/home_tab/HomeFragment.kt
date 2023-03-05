package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.TargetPay
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.HomeHeaderProvider
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeTabViewModel: HomeTabViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setTextViews()

        setHeaderRecyclerView()
        observeHeader()

//        setMovementRecyclerView()
//        observeMovement()

        setTransferRecyclerView()
        observeTransfer()

        setDrawerHeaderName()
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
    }

    private fun observeHeader() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            homeTabViewModel.headerAdapter.setListData(HomeHeaderProvider.getListHeader(it))
            homeTabViewModel.headerAdapter.notifyDataSetChanged()
        }
    }

    private fun setMovementRecyclerView() {

        val movementRecyclerView = binding.rvHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = homeTabViewModel.movementAdapter

        homeTabViewModel.movementAdapter.setItemListener(object :
            MovementRecordsRVAdapter.onItemClickListener {
            override fun onItemClick(creditCard: TargetPay) {
                toast("HOLHOL")
            }
        })

        globalViewModel.getBankMoney().observe(requireActivity()) {
            homeTabViewModel.movementAdapter.setRemainingMoney(it)
        }
    }

    private fun observeMovement() {
        homeTabViewModel.getMovementsFromDB("ES33 4920 4829 0293 3849 3810")
            .observe(requireActivity()) {
                homeTabViewModel.movementAdapter.setListData(it)
                homeTabViewModel.movementAdapter.notifyDataSetChanged()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.clHistorialLoading.isVisible = false
                    binding.clHistorial.isVisible = true
                }, 300)
            }
    }

    private fun setTransferRecyclerView() {

        val movementRecyclerView = binding.rvHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = homeTabViewModel.transfersAdapter

        homeTabViewModel.transfersAdapter.setItemListener(object :
            TransfersRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                toast("HOLHOL")
            }
        })
    }

    private fun observeTransfer() {

        homeTabViewModel.getTransfersFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {
//            var newMovementList = mutableListOf<Movement>()
//
//            for (movement in it) {
//                if(movement.payment_type == PaymentType.Transfer) {
//                   newMovementList.add(movement)
//                }
//            }

            homeTabViewModel.transfersAdapter.setListData(it)
            homeTabViewModel.transfersAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.clHistorialLoading.isVisible = false
                binding.clHistorial.isVisible = true
            }, 300)

        }
    }

    private fun setTextViews() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            binding.tvDineroCuenta.text = Methods.formatMoney(it)
            binding.tvMoneyTotal.text = Methods.formatMoney(it)
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
            binding.tvAccountNumber.text = "Cuenta *${Methods.formatShortIban(it)}"
            binding.tvShortNumber.text = "· ${Methods.formatShortIban(it)}"
        }
    }

    private fun setDrawerHeaderName() {
        globalViewModel.getUserName().observe(requireActivity()) {
            activity?.findViewById<TextView>(R.id.tvNameDrawer)?.text = Methods.splitName(it)
        }
    }

    override fun onStart() {
        super.onStart()
        globalViewModel.getUserName().observe(this) {
            val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView
            tvTitle.text = "Hola, ${Methods.splitName(it)}"
        }
    }
}