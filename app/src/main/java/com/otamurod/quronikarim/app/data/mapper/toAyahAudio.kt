package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.audio.AyahAudioDto
import com.otamurod.quronikarim.app.domain.model.audio.AyahAudio

fun AyahAudioDto.toAyahAudio(): AyahAudio {
    return AyahAudio(
        audio, audioSecondary, hizbQuarter, juz, manzil, number, numberInSurah, page, ruku,
//        sajda,
        text
    )
}