package com.otamurod.quronikarim.app.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otamurod.quronikarim.app.domain.model.identifier.Identifier
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
    private var _surahs = MutableLiveData<List<Surah>>()
    val surahs: LiveData<List<Surah>> = _surahs

    private var _identifiers = MutableLiveData<List<Identifier>>()
    val identifier: LiveData<List<Identifier>> = _identifiers

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
                _surahs.postValue(it)
            }
        }
    }

    fun getIdentifiersCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTranslations().catch {
                withContext(Dispatchers.Main) {
                    _error.call()
                }
            }.collectLatest {
                _identifiers.postValue(it)
            }
        }
    }

    fun getAllLanguagesCall(): ArrayList<String> {
        return langs
    }
}