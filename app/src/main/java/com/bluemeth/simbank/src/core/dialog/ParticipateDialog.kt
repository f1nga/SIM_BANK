package com.bluemeth.simbank.src.core.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.DialogParticipateBinding
import com.bluemeth.simbank.src.utils.Methods
import kotlinx.android.synthetic.main.dialog_participate.*
import kotlin.random.Random


class ParticipateDialog : DialogFragment() {

    private var btnAction: Action = Action.Empty

    companion object {
        fun create(
            btnAction: Action
        ): ParticipateDialog = ParticipateDialog().apply {
            this.btnAction = btnAction
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogParticipateBinding.inflate(requireActivity().layoutInflater)

        binding.lottieAnimation.setAnimation(R.raw.slot_number)

        val randomNumber = Random.nextInt(1, 100)
        fun checkWin() {
            if (randomNumber > 50) {
                binding.tvResult.text = "¡Felicidades, has ganado una participación!"
                Handler(Looper.getMainLooper()).postDelayed({
                    Methods.sendNotification(
                        "SIM BANK",
                        "Entrada Cupra Arena",
                        requireContext()
                    )
                }, 2500)
            } else {
                binding.tvResult.text = "Vaya, no has tenido suerte!"
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.lottieAnimation.isVisible = false
            binding.tvResult.isVisible = true
            checkWin()
            binding.tvYourNumber.text = randomNumber.toString()
            binding.tvYourNumber.isVisible = true
            binding.btnAction.isVisible = true
        }, 4000)

        binding.btnAction.setOnClickListener {
            btnAction.onClickListener(this)
            dismiss()
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(true)
            .create()


    }

    data class Action(
        val text: String,
        val onClickListener: (ParticipateDialog) -> Unit
    ) {
        companion object {
            val Empty = Action("") {}
        }
    }

}