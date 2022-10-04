package com.otamurod.apicallusingmvvmcoroutines.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        //        val BASE_URL = "https://api.github.com/search/"
        const val BASE_URL = "https://api.alquran.cloud/v1/"

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}