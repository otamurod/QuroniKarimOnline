package com.otamurod.quronikarim.app.domain.model.surah

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Surah(
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int
) : Parcelable