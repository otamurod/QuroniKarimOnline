package com.otamurod.quronikarim.app.presentation.utils

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackBar(@StringRes text: Int) {
    val snackbar = Snackbar.make(requireActivity().window.decorView, text, Snackbar.LENGTH_LONG)

    val snackbarLayout: View = snackbar.view
    val lp = LinearLayout.LayoutParams(
        requireActivity().window.decorView.width - 200,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    lp.setMargins(100, 800, 0, 0)
    snackbarLayout.layoutParams = lp

    return snackbar.show()
}
