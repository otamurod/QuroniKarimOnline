package com.otamurod.apicallusingmvvmcoroutines.network

import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerList
import retrofit2.http.GET

interface RetrofitService {

    @GET(value = "surah")
    suspend fun getDataFromAPI(): RecyclerList
//    suspend fun getDataFromAPI(@Query("q") query: String): RecyclerList
}