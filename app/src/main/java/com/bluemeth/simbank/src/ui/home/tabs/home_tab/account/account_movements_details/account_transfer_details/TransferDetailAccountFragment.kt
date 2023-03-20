package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.account_transfer_details

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentTransferDetailAccountBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import com.bluemeth.simbank.src.utils.Methods
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferDetailAccountFragment : Fragment() {

    private lateinit var binding: FragmentTransferDetailAccountBinding
    private val movementsDetailsViewModel: MovementsDetailsViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private lateinit var movement: Movement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferDetailAccountBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        movement = movementsDetailsViewModel.movement
        setTextViews()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.ivPencil.setOnClickListener { addNoteVisible() }
    }

    private fun initObservers() {
        movementsDetailsViewModel.noteChanged.observe(requireActivity()) {
            binding.tvAddNote.text = it
        }
    }

    private fun addNoteVisible() {
        val note = binding.tvAddNote.text
        binding.llNote.isVisible = false
        binding.tvAddNote.isVisible = false

        requireActivity().findViewById<LinearLayout>(R.id.llAddNote).isVisible = true

        val inputNota = requireActivity().findViewById<TextInputEditText>(R.id.inputNotaText)
        val inputText = Editable.Factory.getInstance()
        inputNota.text = if(note != "Añade aquí tu nota") inputText.newEditable(note) else inputText.newEditable("")

        requireActivity().findViewById<CardView>(R.id.cvCancel).setOnClickListener { hintEditText() }
        requireActivity().findViewById<CardView>(R.id.cvAccept).setOnClickListener { acceptNote() }
    }

    private fun acceptNote() {
        val inputNota = requireActivity().findViewById<TextInputEditText>(R.id.inputNotaText)
        movementsDetailsViewModel.onAcceptNoteSelected(inputNota.text.toString())

        view?.dismissKeyboard()

        hintEditText()
    }

    private fun hintEditText() {
        binding.llNote.isVisible = true
        binding.tvAddNote.isVisible = true

        requireActivity().findViewById<LinearLayout>(R.id.llAddNote).isVisible = false
    }

    private fun setTextViews() {

        with(binding) {
            tvDate.text = Methods.formateDateBizum(movement.date.toDate())

            if (movement.isIncome) {
                tvTitle.text = "Transferencia recibida"
                tvSubject.text =
                    if (movement.subject != "") "Recibido: ${movement.subject}" else "Recibido: sin concepto"
                tvPrice.text = "+${Methods.formatMoney(movement.amount)}"
                ivArrowIncome.setImageResource(R.drawable.arrow_win)
                tvRemainingMoney.text = Methods.formatMoney(movement.beneficiary_remaining_money)
                tvCategory.text = "Transferencia recibida"
                ivImageCategory.setImageResource(R.drawable.ic_income_bizum)

            } else {
                tvTitle.text = "Transferencia realizada"
                tvSubject.text =
                    if (movement.subject != "") "Enviado: ${movement.subject}" else "Enviado: sin concepto"
                tvPrice.text = "-${Methods.formatMoney(movement.amount)}"
                ivArrowIncome.setImageResource(R.drawable.arrow_lose)
                tvRemainingMoney.text = Methods.formatMoney(movement.remaining_money)
                tvCategory.text = "Transferencias"
                ivImageCategory.setImageResource(R.drawable.ic_bank)
            }

            tvFechaCargo.text = Methods.formateFechaCargo(movement.date.toDate())
            tvFechaAbono.text = Methods.formateDateBizum(movement.date.toDate())
            tvBeneficiary.text = movement.beneficiary_name
            tvBeneficiaryAccount.text = movement.beneficiary_iban

            globalViewModel.getUserByEmail(movement.user_email).observe(requireActivity()) {
                tvOrdenante.text = it.name
            }

            movementsDetailsViewModel.noteExists().observe(requireActivity()) {
                tvAddNote.text = it?.note ?: "Añade aquí tu nota"
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_account_transfer_detail)
    }
}