package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentAddCreditCardBinding
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.model.CreditCardInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCreditCardBinding
    private val addCreditCardViewModel : AddCreditCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCreditCardBinding.inflate(inflater,container,false)

        setRecyclerView()
        observeCard()
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.addCardRecyclerView
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = addCreditCardViewModel.cardAdapter
        addCreditCardViewModel.cardAdapter.setItemListener(object : AddCreditCardRVAdapter.OnItemClickListener {
            override fun onItemClick(creditCard: CreditCardInfo) {
                TODO("Not yet implemented")
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