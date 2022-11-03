package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.translator.TranslatorDto
import com.otamurod.quronikarim.app.domain.model.translator.Translator

fun TranslatorDto.toTranslator(): Translator {
    return Translator(englishName, identifier, language)
}