package com.otamurod.quronikarim.app.data.remote.dto.details

data class SurahDetailDto(
    val ayahs: List<AyahDto>,
    val edition: EditionDto,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String
)