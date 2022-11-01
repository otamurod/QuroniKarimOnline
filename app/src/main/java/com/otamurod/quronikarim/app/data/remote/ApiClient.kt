package com.otamurod.quronikarim.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        const val BASE_URL = "https://api.alquran.cloud/v1/"

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val apiService = getRetrofitInstance().create(ApiService::class.java)
    }
}