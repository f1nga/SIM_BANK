package com.bluemeth.simbank.src.ui.steps.step4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bluemeth.simbank.databinding.FragmentStep4Binding
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.ui.home.HomeActivity

class Step4Fragment : Fragment() {
    private lateinit var binding: FragmentStep4Binding
    private val step4ViewModel: Step4ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentStep4Binding.inflate(inflater,container,false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnAdelante.setOnClickListener{
            step4ViewModel.insertDataToDB()
            prefs.saveSteps()
        }
    }

    private fun initObservers() {
        step4ViewModel.navigateToHome.observe(requireActivity()) {
            goToHome()
        }
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(requireContext()))
    }
}