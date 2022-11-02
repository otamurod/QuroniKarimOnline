package com.otamurod.quronikarim.app.data.remote

import com.otamurod.quronikarim.app.data.remote.dto.MainResponse
import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.data.remote.dto.details.SurahDetailDto
import com.otamurod.quronikarim.app.data.remote.dto.identifier.IdentifierDto
import com.otamurod.quronikarim.app.domain.model.identifier.Identifier
import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // get all surahs list
    // http://api.alquran.cloud/surah
    @GET("surah")
    suspend fun getSurahFromAPI(): Response<MainResponse<List<SurahDto>>>

    // get a single surah
    // http://api.alquran.cloud/surah/1
    @GET(value = "surah/{number}")
    suspend fun getSurahDetailFromAPI(@Path("number") number: Int): Response<MainResponse<SurahDetailDto>>

    // get surah audio
    // http://api.alquran.cloud/surah/1/ar.alafasy
    @GET(value = "surah/{number}/{identifier}")
    suspend fun getSurahAudioFromAPI(
        @Path("number") number: Int,
        @Path("identifier") identifier: String
    ): Response<MainResponse<SurahAudioDto>>

    // get translations
    // http://api.alquran.cloud/v1/edition/type/translation
    @GET("edition/type/translation")
    suspend fun getTranslationsFromAPI(): Response<MainResponse<List<IdentifierDto>>>
}