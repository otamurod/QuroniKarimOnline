package com.otamurod.quronikarim.app.data.repository.datasource

import com.otamurod.quronikarim.app.data.remote.ApiService
import com.otamurod.quronikarim.app.data.remote.dto.MainResponse
import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.data.remote.dto.reciter.ReciterDto
import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import com.otamurod.quronikarim.app.data.remote.dto.translator.TranslatorDto
import retrofit2.Response
import javax.inject.Inject

class SurahDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : SurahDataSource {
    override suspend fun getSurahList(): Response<MainResponse<List<SurahDto>>> {
        return apiService.getAllSurahesFromAPI()
    }

    override suspend fun getSurahAudio(
        number: Int,
        identifier: String
    ): Response<MainResponse<SurahAudioDto>> {
        return apiService.getSurahAudioFromAPI(number, identifier)
    }

    override suspend fun getTranslations(): Response<MainResponse<List<TranslatorDto>>> {
        return apiService.getTranslationsFromAPI()
    }

    override suspend fun getReciters(url:String): Response<List<ReciterDto>> {
        return apiService.getRecitersFromAPI(url)
    }
}