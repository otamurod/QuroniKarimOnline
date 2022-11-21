package com.otamurod.quronikarim.app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient(builder: OkHttpClient) {
    companion object {
        private const val BASE_URL = "https://api.alquran.cloud/v1/"
        private fun getRetrofitInstance(builder: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    val apiService: ApiService = getRetrofitInstance(builder).create(ApiService::class.java)
}