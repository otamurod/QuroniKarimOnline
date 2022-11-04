package com.otamurod.quronikarim.app.presentation.ui.surah

import androidx.lifecycle.*
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.repository.Repository
import com.otamurod.quronikarim.app.presentation.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val repository: Repository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _surahAudio = MutableLiveData<SurahAudio>()
    val surahAudio: LiveData<SurahAudio> = _surahAudio

    private var _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit> = _error

    fun getSurahTranslation(surahNumber: Int, identifier: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSurahAudio(surahNumber, identifier).catch {
                withContext(Dispatchers.Main) {
                    _error.call()
                }
            }.collectLatest {
                _surahAudio.postValue(it)
            }
        }
    }
}