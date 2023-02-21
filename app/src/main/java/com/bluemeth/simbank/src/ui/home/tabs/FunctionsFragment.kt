package com.bluemeth.simbank.src.ui.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFunctionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FunctionsFragment : Fragment() {
    private lateinit var binding: FragmentFunctionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_functions,container,false)
        setHasOptionsMenu(true)

        return binding.root
    }

}