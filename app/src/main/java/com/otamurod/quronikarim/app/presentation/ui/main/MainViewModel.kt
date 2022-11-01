package com.otamurod.quronikarim.app.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.domain.model.surah.Surah
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
    private val repositoryImpl: RepositoryImpl
) : ViewModel() {
    private var _surahs = MutableLiveData<List<Surah>>()
    val surahs = _surahs

    private var _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit> = _error

    fun getSurahesCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.getAllSurah()
                .catch {
                    withContext(Dispatchers.Main) {
                        _error.call()
                    }
                }.collectLatest {
                    _surahs.postValue(it)
                }
        }
    }
}