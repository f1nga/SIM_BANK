package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.info_account

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentInfoAccountBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoAccountFragment : Fragment() {

    private lateinit var binding: FragmentInfoAccountBinding
    private val adapter : CreditCardRVAdapter = CreditCardRVAdapter()
    private val globalViewModel : GlobalViewModel by viewModels()
    private val infoAccountViewModel : InfoAccountViewModel by viewModels()
    private val creditCardViewModel : CreditCardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoAccountBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
        setTextViews()
        setRecyclerView()
    }

    private fun initListeners() {
        binding.ivPencil.setOnClickListener { addNoteVisible() }
        binding.ivCopy.setOnClickListener { copyToClipboard() }
    }

    private fun initObservers() {
        infoAccountViewModel.aliasChanged.observe(requireActivity()) {
            binding.tvAlias.text = it
        }
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.rvCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = adapter

        adapter.setItemListener(object: CreditCardRVAdapter.OnItemClickListener {
            override fun onItemClick(creditCard: CreditCard) {
                creditCardViewModel.setCard(creditCard)
                view?.findNavController()?.navigate(R.id.action_infoAccountFragment_to_infoCardFragment)
            }
        })

        creditCardViewModel.getNameUserCard(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {
            adapter.setUserName(it.name)
        }

        observeCard()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCard() {
        globalViewModel.getBankIban().observe(requireActivity()) {
            creditCardViewModel.getCreditsCardsFromDB(it).observe(requireActivity()) { creditCardList ->
                adapter.setListData(creditCardList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun addNoteVisible() {
        val alias = binding.tvAlias.text

        binding.llAccount.isVisible = false
        binding.tvAliasTitle.isVisible = false

        requireActivity().findViewById<LinearLayout>(R.id.llAddNote).isVisible = true

        val inputAlias = requireActivity().findViewById<TextInputEditText>(R.id.inputAliasText)
        val inputText = Editable.Factory.getInstance()
        inputAlias.text = inputText.newEditable(alias)

        requireActivity().findViewById<CardView>(R.id.cvCancel).setOnClickListener { hintEditText() }
        requireActivity().findViewById<CardView>(R.id.cvAccept).setOnClickListener { acceptNote() }
    }

    private fun acceptNote() {
        val inputNota = requireActivity().findViewById<TextInputEditText>(R.id.inputAliasText)
        globalViewModel.getBankIban().observe(requireActivity()) {
            infoAccountViewModel.onAcceptAliasSelected(requireContext(), it, inputNota.text.toString())

            view?.dismissKeyboard()

            hintEditText()
        }
    }

    private fun hintEditText() {
        binding.llAccount.isVisible = true
        binding.tvAliasTitle.isVisible = true

        requireActivity().findViewById<LinearLayout>(R.id.llAddNote).isVisible = false
    }

    private fun copyToClipboard() {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("IBAN", binding.tvIban.text)
        clipboard.setPrimaryClip(clip)
        toast("Texto copiado al portapapeles")
    }

    private fun setTextViews() {
        globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
            binding.tvAlias.text = it.alias
            binding.tvIban.text = it.iban
        }

        globalViewModel.getUserName().observe(requireActivity()) {
            binding.tvTitular.text = it
        }
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_info_account)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_infoAccountFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}