package com.otamurod.apicallusingmvvmcoroutines.network

import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RetrofitInstance {
    companion object {
        val BASE_URL = "https://api.github.com/search/"

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}