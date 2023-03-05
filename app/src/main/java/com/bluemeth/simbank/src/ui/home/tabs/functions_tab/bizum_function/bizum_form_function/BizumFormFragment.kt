package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.databinding.FragmentBizumFormBinding

class BizumFormFragment : Fragment() {

    private lateinit var binding: FragmentBizumFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumFormBinding.inflate(inflater, container, false)

        binding.tvTitleForm.text = arguments?.getString("form_type")

        return binding.root
    }

}