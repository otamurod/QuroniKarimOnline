package com.otamurod.quronikarim.app.data.repository.datasource

import com.otamurod.quronikarim.app.data.remote.dto.MainResponse
import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.data.remote.dto.details.SurahDetailDto
import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import retrofit2.Response

interface SurahDataSource {
    suspend fun getSurahList(): Response<MainResponse<List<SurahDto>>>
    suspend fun getSurahDetail(number: Int): Response<MainResponse<SurahDetailDto>>
    suspend fun getSurahAudio(
        number: Int,
        identifier: String
    ): Response<MainResponse<SurahAudioDto>>
}