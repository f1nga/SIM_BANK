package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentProfileBinding
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        setNames()


        binding.ivEdit.setOnClickListener {
            editUserNameFromDB(binding.inputEditNameText.text.toString())
        }

        return binding.root
    }

    private fun editUserNameFromDB(name: String) {
        homeViewModel.getUserName().observe(requireActivity()) {
            profileViewModel.updateNameFromDB(it.email, name)
            setNames()
        }
    }

    private fun setNames() {
        homeViewModel.getUserName().observe(requireActivity()) {
            binding.tvFullName.text = it.name
            binding.tvCircleName.text = Methods.splitNameProfile(it.name)
        }
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_profile)
    }

}