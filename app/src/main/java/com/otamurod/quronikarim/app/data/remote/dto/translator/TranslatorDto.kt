package com.otamurod.quronikarim.app.data.remote.dto.translator

data class TranslatorDto(
    val direction: String,
    val englishName: String,
    val format: String,
    val identifier: String,
    val language: String,
    val name: String,
    val type: String
)