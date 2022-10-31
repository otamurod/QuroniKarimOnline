package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.details.AyahDto
import com.otamurod.quronikarim.app.domain.model.detail.Ayah

fun AyahDto.toAyah(): Ayah {
    return Ayah(
        hizbQuarter, juz, manzil, number, numberInSurah, page, ruku, sajda, text
    )
}