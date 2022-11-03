package com.otamurod.quronikarim.app.presentation.utils

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackBar(@StringRes text: Int) =
    Snackbar.make(requireActivity().window.decorView, text, Snackbar.LENGTH_LONG).show()