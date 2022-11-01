package com.otamurod.quronikarim.app.data.remote.dto.audio

data class SurahAudioDto(
    val ayahs: List<AyahAudioDto>,
    val edition: EditionAudioDto,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
//    val revelationType: String
)