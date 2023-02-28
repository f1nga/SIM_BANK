package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentProfileBinding
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setPersonalData()
        initListeners()

        return binding.root
    }

    private fun setPersonalData() {
        val inputText = Editable.Factory.getInstance()
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            binding.inputProfileEmailText.text = inputText.newEditable(it.email)
            binding.inputProfilePhoneText.text = inputText.newEditable(Methods.formatPhoneNumber(it.phone))
            binding.inputProfilePasswordText.text = inputText.newEditable(it.password)

            binding.tvFullName.text = it.name
            binding.tvCircleName.text = Methods.splitNameProfile(it.name)
        }

    }

    private fun initListeners() {
        binding.ivEditTitular.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_updateNameFragment)
        }

        binding.ivEditEmail.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_updateEmailFragment)
        }

        binding.ivEditPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_updatePasswordFragment)
        }

        binding.ivEditPhone.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_updatePhoneFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_profile)
    }

}