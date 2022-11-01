package com.otamurod.quronikarim.app.di

import com.otamurod.quronikarim.app.data.remote.ApiClient
import com.otamurod.quronikarim.app.data.remote.ApiService
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSource
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSourceImpl
import com.otamurod.quronikarim.app.domain.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
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