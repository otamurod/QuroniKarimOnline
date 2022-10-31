package com.otamurod.quronikarim.app.presentation.ui.surah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.detail.SurahDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SurahViewModel(
    private val repositoryImpl: RepositoryImpl
) : ViewModel() {
    private var _surahDetail = MutableLiveData<SurahDetail>()
    val surahDetail: LiveData<SurahDetail> = _surahDetail

    private var _surahAudio = MutableLiveData<SurahAudio>()
    val surahAudio: LiveData<SurahAudio> = _surahAudio

    fun getSurahDetailCall(surahNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.getSurah(surahNumber).collectLatest {
                _surahDetail.postValue(it)
            }
        }
    }

    fun getSurahAudioCall(surahNumber: Int, identifier: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.getSurahAudio(surahNumber, identifier).collectLatest {
                _surahAudio.postValue(it)
            }
        }
    }
}