package com.otamurod.quronikarim.app.domain.model.audio

data class AyahAudio(
    val audio: String?,
    val audioSecondary: List<String>?,
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
//    val sajda: Boolean,
    val text: String
)