package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.account_transfer_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentTransferDetailAccountBinding

class TransferDetailAccountFragment : Fragment() {

    private lateinit var binding: FragmentTransferDetailAccountBinding
    private val transferDetailAccountViewModel: TransferDetailAccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferDetailAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_account_transfer_detail)
    }
}