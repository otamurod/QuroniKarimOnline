package com.otamurod.quronikarim.app.data.remote

import com.otamurod.quronikarim.app.data.remote.dto.MainResponse
import com.otamurod.quronikarim.app.data.remote.dto.audio.SurahAudioDto
import com.otamurod.quronikarim.app.data.remote.dto.reciter.ReciterDto
import com.otamurod.quronikarim.app.data.remote.dto.surah.SurahDto
import com.otamurod.quronikarim.app.data.remote.dto.translator.TranslatorDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    // get all surahes list
    // http://api.alquran.cloud/surah
    @GET("surah")
    suspend fun getAllSurahesFromAPI(): Response<MainResponse<List<SurahDto>>>

    // get a surah with ayah-by-ayah audios
    // http://api.alquran.cloud/surah/1/ar.alafasy
    @GET(value = "surah/{number}/{identifier}")
    suspend fun getSurahAudioFromAPI(
        @Path("number") number: Int,
        @Path("identifier") identifier: String
    ): Response<MainResponse<SurahAudioDto>>

    // get list of translations
    // http://api.alquran.cloud/v1/edition/type/translation
    @GET("edition/type/translation")
    suspend fun getTranslationsFromAPI(): Response<MainResponse<List<TranslatorDto>>>

    // get list of reciters
    @GET
    suspend fun getRecitersFromAPI(@Url url: String): Response<List<ReciterDto>>
}