package com.otamurod.quronikarim.app.domain.model.translator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Translator(
    val englishName: String,
    val identifier: String,
    val language: String
):Parcelable