package com.bluemeth.simbank.src.ui.home.tabs.functions_tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFunctionsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FunctionsFragment : Fragment() {
    private lateinit var binding: FragmentFunctionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_functions,container,false)
        setHasOptionsMenu(true)




        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_functions)
    }


}