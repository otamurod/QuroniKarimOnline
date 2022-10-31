package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.details.EditionDto
import com.otamurod.quronikarim.app.domain.model.detail.Edition

fun EditionDto.toEdition(): Edition {
    return Edition(
        direction, englishName, format, identifier, language, name, type
    )
}