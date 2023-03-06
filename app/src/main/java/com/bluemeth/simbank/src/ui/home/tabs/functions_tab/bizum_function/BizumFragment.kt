package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function

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
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumBinding
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel

class BizumFragment : Fragment() {

    private lateinit var binding: FragmentBizumBinding
    private val bizumViewModel: BizumViewModel by viewModels()
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