package com.bluemeth.simbank.src.core.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.bluemeth.simbank.databinding.DialogQuestionBinding

class QuestionDialog : DialogFragment() {

    private var title: String = ""
    private var description: String = ""
    private var helpAction: Action = Action.Empty
    private var isDialogCancelable: Boolean = true
    private var positiveAction: Action = Action.Empty
    private var negativeAction: Action = Action.Empty

    companion object {
        fun create(
            title: String = "",
            description: String = "",
            helpAction: Action = Action.Empty,
            isDialogCancelable: Boolean = true,
            positiveAction: Action = Action.Empty,
            negativeAction: Action = Action.Empty,
        ): QuestionDialog = QuestionDialog().apply {
            this.title = title
            this.description = description
            this.helpAction = helpAction
            this.isDialogCancelable = isDialogCancelable
            this.positiveAction = positiveAction
            this.negativeAction = negativeAction
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogQuestionBinding.inflate(requireActivity().layoutInflater)

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        if (negativeAction == Action.Empty) {
            binding.btnNegative.isGone = true
        } else {
            binding.btnNegative.text = negativeAction.text
            binding.btnNegative.setOnClickListener { negativeAction.onClickListener(this) }
        }
        binding.btnHelp.text = helpAction.text
        binding.btnHelp.setOnClickListener { helpAction.onClickListener(this) }
        binding.btnPositive.text = positiveAction.text
        binding.btnPositive.setOnClickListener { positiveAction.onClickListener(this) }
        isCancelable = isDialogCancelable

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(isDialogCancelable)
            .create()
    }

    data class Action(
        val text: String,
        val onClickListener: (QuestionDialog) -> Unit
    ) {
        companion object {
            val Empty = Action("") {}
        }
    }
}