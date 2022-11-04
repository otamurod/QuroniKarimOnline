package com.otamurod.quronikarim.app.domain.repository

import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.domain.model.translator.Translator
import kotlinx.coroutines.flow.Flow

// do not change it
interface Repository {
    suspend fun getAllSurah(): Flow<List<Surah>>
    suspend fun getSurahAudio(number: Int, identifier: String): Flow<SurahAudio>
    suspend fun getTranslations(): Flow<List<Translator>>
    suspend fun getReciters(url:String): Flow<List<Reciter>>
}