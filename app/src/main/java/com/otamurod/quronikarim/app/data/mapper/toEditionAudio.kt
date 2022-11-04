package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.audio.EditionAudioDto
import com.otamurod.quronikarim.app.domain.model.audio.EditionAudio

fun EditionAudioDto.toEditionAudio(): EditionAudio {
    return EditionAudio(identifier, language)
}