package com.otamurod.quronikarim.app.domain.model.translator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Translator(
//    val direction: String,
    val englishName: String,
//    val format: String,
    val identifier: String,
    val language: String,
//    val name: String,
//    val type: String
):Parcelable