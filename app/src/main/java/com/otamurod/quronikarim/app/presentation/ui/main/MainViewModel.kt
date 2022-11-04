package com.otamurod.quronikarim.app.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.translator.Translator
import com.otamurod.quronikarim.app.domain.model.surah.Surah
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
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private var _surahes = MutableLiveData<List<Surah>>()
    val surahes: LiveData<List<Surah>> = _surahes

    private var _translators = MutableLiveData<List<Translator>>()
    val translator: LiveData<List<Translator>> = _translators

    private var _reciters = MutableLiveData<List<Reciter>>()
    val reciter: LiveData<List<Reciter>> = _reciters

    private val langs = arrayListOf(
        "ar",
        "az",
        "bn",
        "cs",
        "de",
        "dv",
        "en",
        "es",
        "fa",
        "fr",
        "ha",
        "hi",
        "id",
        "it",
        "ja",
        "ko",
        "ku",
        "ml",
        "nl",
        "no",
        "pl",
        "pt",
        "ro",
        "ru",
        "sd",
        "so",
        "sq",
        "sv",
        "sw",
        "ta",
        "tg",
        "th",
        "tr",
        "tt",
        "ug",
        "ur",
        "uz"
    )

    private var _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit> = _error

    fun getSurahesCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllSurah().catch {
                withContext(Dispatchers.Main) {
                    _error.call()
                }
            }.collectLatest {
                _surahes.postValue(it)
            }
        }
    }

    fun getTranslatorsCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTranslations().catch {
                withContext(Dispatchers.Main) {
                    _error.call()
                }
            }.collectLatest {
                _translators.postValue(it)
            }
        }
    }

    fun getRecitersCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReciters("https://raw.githubusercontent.com/islamic-network/cdn/master/info/cdn_surah_audio.json").catch {
                withContext(Dispatchers.Main) {
                    _error.call()
                }
            }.collectLatest {
                _reciters.postValue(it)
            }
        }
    }

    fun getAllLanguagesCall(): ArrayList<String> {
        return langs
    }
}