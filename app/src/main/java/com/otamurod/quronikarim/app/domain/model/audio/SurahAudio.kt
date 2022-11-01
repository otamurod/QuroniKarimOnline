package com.otamurod.quronikarim.app.domain.model.audio

data class SurahAudio(
    val ayahs: List<AyahAudio>,
    val edition: EditionAudio,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
//    val revelationType: String
)