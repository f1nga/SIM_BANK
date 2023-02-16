package com.bluemeth.simbank.src.core.ex

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher

fun DialogFragment.show(launcher: DialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}
