package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.reciter.ReciterDto
import com.otamurod.quronikarim.app.data.remote.dto.translator.TranslatorDto
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.translator.Translator

fun ReciterDto.toReciter(): Reciter {
    return Reciter(englishName, identifier)
}