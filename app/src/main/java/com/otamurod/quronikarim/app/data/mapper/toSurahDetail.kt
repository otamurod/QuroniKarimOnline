package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.details.SurahDetailDto
import com.otamurod.quronikarim.app.domain.model.detail.SurahDetail

fun SurahDetailDto.toSurahDetail(): SurahDetail {
    return SurahDetail(
        ayahs = ayahs.map { it.toAyah() },
        edition = edition.toEdition(),
        englishName,
        englishNameTranslation,
        name,
        number,
        numberOfAyahs,
        revelationType
    )
}