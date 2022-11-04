package com.otamurod.quronikarim.app.data.remote.dto.audio

data class AyahAudioDto(
    val audio: String?,
    val audioSecondary: List<String>?,
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
    val text: String
)