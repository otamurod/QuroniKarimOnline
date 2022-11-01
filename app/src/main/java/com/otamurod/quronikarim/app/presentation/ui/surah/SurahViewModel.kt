package com.otamurod.quronikarim.app.presentation.ui.surah

import androidx.lifecycle.*
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.detail.SurahDetail
import com.otamurod.quronikarim.app.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _surahDetail = MutableLiveData<SurahDetail>()
    val surahDetail: LiveData<SurahDetail> = _surahDetail

    private var _surahAudio = MutableLiveData<SurahAudio>()
    val surahAudio: LiveData<SurahAudio> = _surahAudio

    fun getSurahDetailCall(surahNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSurah(surahNumber).collectLatest {
                _surahDetail.postValue(it)
            }
        }
    }

    fun getSurahAudioCall(surahNumber: Int, identifier: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSurahAudio(surahNumber, identifier)
                .collectLatest {
                    _surahAudio.postValue(it)
                }
        }
    }
}