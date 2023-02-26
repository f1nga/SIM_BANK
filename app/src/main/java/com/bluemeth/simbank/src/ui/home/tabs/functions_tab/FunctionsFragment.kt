package com.bluemeth.simbank.src.ui.home.tabs.functions_tab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFunctionsBinding
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.ui.steps.StepsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FunctionsFragment : Fragment() {
    private lateinit var binding: FragmentFunctionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFunctionsBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)



        binding.imageViewTransferencia.setOnClickListener(){
            val intent = Intent(requireContext(), StepsActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_functions)
    }


}