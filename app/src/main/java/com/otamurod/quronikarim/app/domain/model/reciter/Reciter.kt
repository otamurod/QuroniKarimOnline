package com.otamurod.quronikarim.app.domain.model.reciter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reciter(
    val englishName: String,
    val identifier: String
) : Parcelable