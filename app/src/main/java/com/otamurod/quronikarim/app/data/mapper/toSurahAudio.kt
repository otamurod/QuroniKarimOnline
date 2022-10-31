package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio

fun SurahAudioDto.toSurahAudio(): SurahAudio {
    return SurahAudio(
        ayahs = ayahs.map { it.toAyahAudio() },
        edition = edition.toEditionAudio(),
        englishName,
        englishNameTranslation,
        name,
        number,
        numberOfAyahs,
        revelationType
    )
}