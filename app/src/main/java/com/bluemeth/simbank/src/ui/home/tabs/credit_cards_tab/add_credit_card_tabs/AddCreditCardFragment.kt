package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentAddCreditCardBinding
import com.bluemeth.simbank.databinding.FragmentCreditCardBinding
import com.bluemeth.simbank.src.SimBankApp
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener


class AddCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCreditCardBinding
    private val addCreditCardViewModel : AddCreditCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddCreditCardBinding.inflate(inflater,container,false)

        setRecyclerView()
        observeCard()
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.addCardRecyclerView
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = addCreditCardViewModel.cardAdapter
        addCreditCardViewModel.cardAdapter.setItemListener(object : RecyclerClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(),"Cabolo" , Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeCard() {
        addCreditCardViewModel.setListData()
        addCreditCardViewModel.creditCardList.observe(requireActivity()) {
            addCreditCardViewModel.cardAdapter.setListData(it)

        }
    }



}