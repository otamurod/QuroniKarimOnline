package com.otamurod.apicallusingmvvmcoroutines.network

import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET(value = "repositories")
    suspend fun getDataFromAPI(@Query("q") query: String): RecyclerList
}