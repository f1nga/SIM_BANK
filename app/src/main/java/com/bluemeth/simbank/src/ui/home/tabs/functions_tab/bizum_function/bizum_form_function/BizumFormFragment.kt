package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumFormBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserBizum
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BizumFormFragment : Fragment() {

    private lateinit var binding: FragmentBizumFormBinding
    private val bizumFormViewModel : BizumFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumFormBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setUserBizumRecyclerView()
        initListeners()
    }

    private fun initListeners() {
        binding.llAddFromAgenda.setOnClickListener {
            goToAddContactFromAgenda()
        }

        binding.llAddManually.setOnClickListener {
            goToAddContactManually()
        }
    }

    private fun setUserBizumRecyclerView() {

        val addressesRecycler = binding.rvAddresses
        addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        addressesRecycler.setHasFixedSize(true)
        addressesRecycler.adapter = bizumFormViewModel.addressesRVAdapter

        bizumFormViewModel.addressesRVAdapter.setItemListener(object :
            AddressesRVAdapter.OnItemClickListener {
            override fun onItemClick(userBizum: UserBizum) {
                bizumFormViewModel.addressesRVAdapter.deleteUserBizum(userBizum)
                bizumFormViewModel.addressesRVAdapter.notifyDataSetChanged()
            }
        })

        bizumFormViewModel.addressesRVAdapter.notifyDataSetChanged()
    }

    private fun goToAddContactManually() {
        view?.findNavController()?.navigate(R.id.action_bizumFormFragment_to_addContactManuallyFragment)
    }

    private fun goToAddContactFromAgenda() {
        view?.findNavController()?.navigate(R.id.action_bizumFormFragment_to_addContactFromAgendaFragment)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView
        tvTitle.text = if(arguments?.getString("form_type") == "Enviar dinero") {
            getString(R.string.toolbar_send_bizum)
        } else {
            getString(R.string.toolbar_request_bizum)
        }

        binding.tvTitleForm.text = arguments?.getString("form_type")
    }

}