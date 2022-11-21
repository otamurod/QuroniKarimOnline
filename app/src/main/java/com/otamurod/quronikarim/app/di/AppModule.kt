package com.otamurod.quronikarim.app.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.otamurod.quronikarim.app.data.remote.ApiClient
import com.otamurod.quronikarim.app.data.remote.ApiService
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSource
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSourceImpl
import com.otamurod.quronikarim.app.domain.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApiService(builder: OkHttpClient): ApiService {
        return ApiClient(builder).apiService
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val chuckInterceptor = ChuckerInterceptor.Builder(context)
            .maxContentLength(500_000L)
            .redactHeaders("Content-Type", "application/json")
            .alwaysReadResponseBody(true)
            .build()

        val builder = OkHttpClient.Builder()
            .addInterceptor(chuckInterceptor)
            .build()

        return builder
    }

    @Provides
    @Singleton
    fun provideSurahDataSource(apiService: ApiService): SurahDataSource {
        return SurahDataSourceImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideRepository(surahDataSource: SurahDataSource): Repository {
        return RepositoryImpl(surahDataSource)
    }
}