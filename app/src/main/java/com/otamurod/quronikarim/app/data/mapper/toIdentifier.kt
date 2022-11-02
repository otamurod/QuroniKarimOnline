package com.otamurod.quronikarim.app.data.mapper

import com.otamurod.quronikarim.app.data.remote.dto.identifier.IdentifierDto
import com.otamurod.quronikarim.app.domain.model.identifier.Identifier

fun IdentifierDto.toIdentifier(): Identifier {
    return Identifier(englishName, identifier, language)
}