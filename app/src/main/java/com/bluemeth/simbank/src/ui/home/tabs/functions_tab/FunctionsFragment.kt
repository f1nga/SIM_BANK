package com.bluemeth.simbank.src.ui.home.tabs.functions_tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFunctionsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FunctionsFragment : Fragment() {

    private lateinit var binding: FragmentFunctionsBinding
    private val functionsViewModel: FunctionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFunctionsBinding.inflate(inflater,container,false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.clTransferencia.setOnClickListener {
            goToTransferFunction()
        }

        binding.clBizum.setOnClickListener {
            goToBizumFunction()
        }
    }

    private fun goToTransferFunction() {
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_transferFragment)
    }

    private fun goToBizumFunction() {
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_bizumFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_functions)
    }

}