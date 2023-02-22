package com.bluemeth.simbank.src.ui.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.core.ex.setActivityTitle

class HomeFragment  : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)

        setActivityTitle("HOL")

        return binding.root
    }
}