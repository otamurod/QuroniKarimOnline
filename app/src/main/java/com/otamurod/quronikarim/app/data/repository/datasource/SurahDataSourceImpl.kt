package com.otamurod.quronikarim.app.data.repository.datasource

import com.otamurod.quronikarim.app.data.remote.ApiService
import com.otamurod.quronikarim.app.data.remote.dto.MainResponse
import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.data.remote.dto.details.SurahDetailDto
import com.otamurod.quronikarim.app.data.remote.dto.identifier.IdentifierDto
import com.otamurod.quronikarim.app.domain.model.identifier.Identifier
import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import retrofit2.Response
import javax.inject.Inject

class SurahDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : SurahDataSource {
    override suspend fun getSurahList(): Response<MainResponse<List<SurahDto>>> {
        return apiService.getSurahFromAPI()
    }

    override suspend fun getSurahDetail(number: Int): Response<MainResponse<SurahDetailDto>> {
        return apiService.getSurahDetailFromAPI(number)
    }

    override suspend fun getSurahAudio(
        number: Int,
        identifier: String
    ): Response<MainResponse<SurahAudioDto>> {
        return apiService.getSurahAudioFromAPI(number, identifier)
    }

    override suspend fun getTranslations(): Response<MainResponse<List<IdentifierDto>>> {
        return apiService.getTranslationsFromAPI()
    }
}