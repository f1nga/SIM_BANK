package com.bluemeth.simbank.src.ui.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentCardBinding
import com.bluemeth.simbank.src.data.adapters.CardRVAdapter
import com.bluemeth.simbank.src.data.adapters.RecyclerClickListener
import com.bluemeth.simbank.src.ui.viewmodels.CreditCardViewModel


class CardFragment : Fragment() {

    private lateinit var binding: FragmentCardBinding
    private lateinit var adapter: CardRVAdapter
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_card,container,false);
        setRecyclerView()
        observeCard()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.recyclerViewCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        adapter = CardRVAdapter(requireContext())
        cardRecyclerview.adapter = adapter
    }

    private fun observeCard() {
        creditCardViewModel.fetchCardData().observe(requireActivity(), Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }


}