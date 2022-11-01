package com.otamurod.quronikarim.app.data.repository

import android.util.Log
import com.otamurod.quronikarim.app.data.mapper.toSurah
import com.otamurod.quronikarim.app.data.mapper.toSurahAudio
import com.otamurod.quronikarim.app.data.mapper.toSurahDetail
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSourceImpl
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.detail.SurahDetail
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "RepositoryImpl"

class RepositoryImpl @Inject constructor(
    private val dataSourceImpl: SurahDataSourceImpl
) : Repository {
    override suspend fun getAllSurah(): Flow<List<Surah>> = flow {
        val response = dataSourceImpl.getSurahList()
        emit(response.body()!!.data.map { it.toSurah() })
    }

    override suspend fun getSurah(surahNumber: Int): Flow<SurahDetail> = flow {
        try {
            val response = dataSourceImpl.getSurahDetail(surahNumber)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val data = body.data
                    emit(data.toSurahDetail())
                }
            } else {
                Log.d(TAG, "getSurah: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getSurah: ${e.message}")
        }
    }

    override suspend fun getSurahAudio(
        number: Int,
        identifier: String
    ): Flow<SurahAudio> = flow {
        try {
            val response = dataSourceImpl.getSurahAudio(number, identifier)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val data = body.data
                    emit(data.toSurahAudio())
                }
            } else {
                Log.d(TAG, "getSurahAudio: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getSurahAudio: ${e.message}")
        }
    }
}