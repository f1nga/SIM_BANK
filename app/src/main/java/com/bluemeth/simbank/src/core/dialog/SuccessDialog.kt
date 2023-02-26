package com.bluemeth.simbank.src.core.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bluemeth.simbank.databinding.DialogSuccessBinding

class SuccessDialog : DialogFragment() {

    private var title: String = ""
    private var description: String = ""
    private var btnAction: Action = Action.Empty

    companion object {
        fun create(
            title: String,
            description: String,
            btnAction: Action
        ) : SuccessDialog = SuccessDialog().apply {
            this.title = title
            this.description = description
            this.btnAction = btnAction
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
        val binding = DialogSuccessBinding.inflate(requireActivity().layoutInflater)

        binding.tvTitle.text = title
        binding.tvDescription.text = description

        binding.btnPositive.setOnClickListener {
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
        val onClickListener: (SuccessDialog) -> Unit
    ) {
        companion object {
            val Empty = Action("") {}
        }
    }

}