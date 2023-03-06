package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactManuallyBinding
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserBizum

class AddContactManuallyFragment : Fragment() {

    private lateinit var binding: FragmentAddContactManuallyBinding
    private val addContactManuallyViewModel: AddContactManuallyViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactManuallyBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnAddAddresse.setOnClickListener {
            addContactManuallyViewModel.onAddContactSelected("123")
        }
    }

    private fun initObservers() {
        addContactManuallyViewModel.navigateToBizumForm.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                bizumFormViewModel.addressesRVAdapter.setUserBizum(
                    UserBizum(
                        name = binding.inputPhoneText.text.toString()
                    )
                )
                goToBizumForm()
            }
        }
    }

    private fun goToBizumForm() {
        view?.findNavController()
            ?.navigate(R.id.action_addContactManuallyFragment_to_bizumFormFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_add_manually)
    }
}