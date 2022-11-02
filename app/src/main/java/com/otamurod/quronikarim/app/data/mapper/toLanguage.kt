package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.language.LanguageDto
import com.otamurod.quronikarim.app.domain.model.language.Language

fun LanguageDto.toLanguage(): Language {
    return Language(data)
}