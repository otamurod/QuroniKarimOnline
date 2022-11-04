package com.otamurod.quronikarim.app.data.repository

import android.util.Log
import com.otamurod.quronikarim.app.data.mapper.toReciter
import com.otamurod.quronikarim.app.data.mapper.toSurah
import com.otamurod.quronikarim.app.data.mapper.toSurahAudio
import com.otamurod.quronikarim.app.data.mapper.toTranslator
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSource
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.domain.model.translator.Translator
import com.otamurod.quronikarim.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "RepositoryImpl"

class RepositoryImpl @Inject constructor(
    private val surahDataSource: SurahDataSource
) : Repository {
    override suspend fun getAllSurah(): Flow<List<Surah>> = flow {
        val response = surahDataSource.getSurahList()
        emit(response.body()!!.data.map { it.toSurah() })
    }

    override suspend fun getSurahAudio(
        number: Int,
        identifier: String
    ): Flow<SurahAudio> = flow {
        try {
            val response = surahDataSource.getSurahAudio(number, identifier)
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

    override suspend fun getTranslations(): Flow<List<Translator>> = flow {
        try {
            val response = surahDataSource.getTranslations()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val data = body.data
                    emit(data.map { it.toTranslator() })
                }
            } else {
                Log.d(TAG, "getTranslations: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getTranslations: ${e.message}")
        }
    }

    override suspend fun getReciters(url: String): Flow<List<Reciter>> = flow {
        try {
            val response = surahDataSource.getReciters(url)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(body.map { it.toReciter() })
                }
            } else {
                Log.d(TAG, "getReciters: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getReciters: ${e.message}")
        }
    }
}