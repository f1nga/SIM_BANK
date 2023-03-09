package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BizumFragment : Fragment() {

    private lateinit var binding: FragmentBizumBinding
    private val bizumViewModel: BizumViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel : BizumFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        clearBizumForm()
        initListeners()
        initObservers()
        setMovementRecyclerView()
    }

    private fun initObservers() {
        binding.cvSend.setOnClickListener { bizumViewModel.onSendBizumSelected() }
        binding.cvRequest.setOnClickListener { bizumViewModel.onRequestBizumSelected() }
    }

    private fun initListeners() {
        bizumViewModel.navigateToRequestBizum.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToBizumForm("Solicitar dinero")
            }
        }

        bizumViewModel.navigateToSendBizum.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToBizumForm("Enviar dinero")
            }
        }
        binding.cvBizumsSended.setOnClickListener {
            filterBizumsSended()
        }
        binding.cvBizumsReceived.setOnClickListener {
            filterBizumsReceived()
        }
    }

    private fun setMovementRecyclerView() {

        val movementRecyclerView = binding.rvBizumHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = bizumViewModel.bizumHistoryRVAdapter

        bizumViewModel.bizumHistoryRVAdapter.setItemListener(object :
            BizumHistoryRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                toast("HOLHOL")
            }
        })

        observeMovements2(false)

    }

    private fun observeMovement() {
        globalViewModel.getMovementsByTypeFromDB(globalViewModel.getUserAuth().email!!, "Bizum").observe(requireActivity()) {
            bizumViewModel.bizumHistoryRVAdapter.setListData(it)
            bizumViewModel.bizumHistoryRVAdapter.notifyDataSetChanged()
        }
    }

    private fun observeMovements2(isIncome: Boolean) {
        globalViewModel.getMovementsByIsIncomeFromDB(globalViewModel.getUserAuth().email!!, "Bizum", isIncome).observe(requireActivity()) {
            bizumViewModel.bizumHistoryRVAdapter.setListData(it)
            bizumViewModel.bizumHistoryRVAdapter.notifyDataSetChanged()
        }
    }

    private fun filterBizumsReceived() {
        if(binding.tvBizumsReceived.currentTextColor == Color.parseColor("#C8BFBDBD")) {

            binding.tvBizumsReceived.setTextColor(Color.parseColor("#F7189EDC"))
            binding.tvBizumsSended.setTextColor(Color.parseColor("#C8BFBDBD"))
        }
        observeMovements2(true)
    }

    private fun filterBizumsSended() {
        if(binding.tvBizumsSended.currentTextColor == Color.parseColor("#C8BFBDBD")) {

            binding.tvBizumsSended.setTextColor(Color.parseColor("#F7189EDC"))
            binding.tvBizumsReceived.setTextColor(Color.parseColor("#C8BFBDBD"))
        }
        observeMovements2(false)
    }

    private fun clearBizumForm() {
        if(bizumFormViewModel.addressesRVAdapter.getListData().isNotEmpty())
            bizumFormViewModel.addressesRVAdapter.getListData().clear()
        bizumFormViewModel.bizumFormArguments?.clearArguments()
    }

    private fun goToBizumForm(formType: String) {
        val bundle = bundleOf("form_type" to formType)
        view?.findNavController()?.navigate(R.id.action_bizumFragment_to_bizumFormFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_bizum)
    }
}