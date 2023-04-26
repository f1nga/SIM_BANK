package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_details.BizumDetailViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BizumFragment : Fragment() {

    private lateinit var binding: FragmentBizumBinding
    private val bizumViewModel: BizumViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val bizumDetailViewModel: BizumDetailViewModel by activityViewModels()

    private companion object {
        const val COLOR_SELECTED = "#F7189EDC"
        const val COLOR_UNSELECTED = "#C8BFBDBD"
    }

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
                goToBizumForm(Constants.REQUEST_MONEY)
            }
        }

        bizumViewModel.navigateToSendBizum.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToBizumForm(Constants.SEND_MONEY)
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
                bizumDetailViewModel.setMovement(movement)
                goToBizumDetails()
            }
        })

        observeMovements(false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeMovements(isIncome: Boolean) {
        bizumViewModel.bizumHistoryRVAdapter.clearListData()
        bizumViewModel.bizumHistoryRVAdapter.notifyDataSetChanged()

        if (isIncome) {
            globalViewModel.getReceivedMovementsFromDB()
                .observe(requireActivity()) { movementsList ->
                    for(movement in movementsList) {
                        if(movement.payment_type == PaymentType.Bizum)  {
                            bizumViewModel.bizumHistoryRVAdapter.setMovement(movement)
                        }
                    }
                    bizumViewModel.bizumHistoryRVAdapter.notifyDataSetChanged()
                }
        } else {
            globalViewModel.getSendedMovementsFromDB()
                .observe(requireActivity()) { movementsList ->
                    for(movement in movementsList) {
                        if(movement.payment_type == PaymentType.Bizum)  {
                            bizumViewModel.bizumHistoryRVAdapter.setMovement(movement)
                        }
                    }
                    bizumViewModel.bizumHistoryRVAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun filterBizumsSended() {
        if (binding.tvBizumsReceived.currentTextColor == Color.parseColor(COLOR_SELECTED)) {

            binding.tvBizumsReceived.setTextColor(Color.parseColor(COLOR_UNSELECTED))
            binding.tvBizumsSended.setTextColor(Color.parseColor(COLOR_SELECTED))
            observeMovements(false)
        }

    }

    private fun filterBizumsReceived() {
        if (binding.tvBizumsSended.currentTextColor == Color.parseColor(COLOR_SELECTED)) {

            binding.tvBizumsSended.setTextColor(Color.parseColor(COLOR_UNSELECTED))
            binding.tvBizumsReceived.setTextColor(Color.parseColor(COLOR_SELECTED))
            observeMovements(true)
        }
    }

    private fun clearBizumForm() {
        with(bizumFormViewModel) {
            if (addressesRVAdapter.getListData().isNotEmpty())
                addressesRVAdapter.getListData().clear()
            bizumFormArgument?.import = ""
            bizumFormArgument?.subject = ""
            bizumFormArgument?.addresse = null
            reUseBizumArgument?.import= ""
            reUseBizumArgument?.subject = ""
            bizumFormMdel?.import = ""
            bizumFormMdel?.subject = ""
            bizumFormMdel?.addresse = null
        }
    }

    private fun goToBizumForm(formType: String) {
        val bundle = bundleOf(Constants.FORM_TYPE to formType, Constants.REUSE to false)
        view?.findNavController()?.navigate(R.id.action_bizumFragment_to_bizumFormFragment, bundle)
    }

    private fun goToBizumDetails() {
        view?.findNavController()?.navigate(R.id.action_bizumFragment_to_bizumDetailFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_bizum)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_bizumFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}