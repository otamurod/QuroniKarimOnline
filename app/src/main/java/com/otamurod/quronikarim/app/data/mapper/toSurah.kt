package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import com.otamurod.quronikarim.app.domain.model.surah.Surah

fun SurahDto.toSurah(): Surah {
    return Surah(
        englishName, englishNameTranslation, name, number
    )
}