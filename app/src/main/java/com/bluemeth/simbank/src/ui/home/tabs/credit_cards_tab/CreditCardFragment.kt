package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentCreditCardBinding
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditCardFragment() : Fragment() {

    private lateinit var binding: FragmentCreditCardBinding
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditCardBinding.inflate(inflater,container,false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        setRecyclerView()
    }

    private fun initListeners() {
        binding.floatingActionButton.setOnClickListener(){
            goToAddCreditCard()
        }
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.recyclerViewCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = creditCardViewModel.cardAdapter
        creditCardViewModel.cardAdapter.setItemListener(object: CreditCardRVAdapter.OnItemClickListener {

            override fun onItemClick(creditCard: CreditCard) {
                creditCardViewModel.setCard(creditCard)
                view?.findNavController()?.navigate(R.id.action_cardFragment_to_infoCardFragment)
            }
        })

        observeCard()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCard() {
        globalViewModel.getBankIban().observe(requireActivity()) {
            creditCardViewModel.getCreditsCardsFromDB(it).observe(requireActivity()) { creditCardList ->
                creditCardViewModel.cardAdapter.setListData(creditCardList)
                creditCardViewModel.cardAdapter.notifyDataSetChanged()
                Methods.setTimeout(
                    {
                    binding.clCardsLoading.isVisible = false
                    binding.clCards.isVisible = true
                    }, 300
                )
            }

            creditCardViewModel.getNameUserCard(globalViewModel.getUserAuth().email!!).observe(requireActivity()) { user ->
                creditCardViewModel.cardAdapter.setUserName(user.name)
            }
        }
    }

    private fun goToAddCreditCard() {
        view?.findNavController()?.navigate(R.id.action_cardFragment_to_addCreditCardFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_cards)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_cardFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}