package com.otamurod.quronikarim.app.presentation.ui.surah

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otamurod.quronikarim.app.domain.repository.Repository

@Suppress("UNCHECKED_CAST")
class SurahViewModelFactory(private val repository: Repository, private val savedStateHandle: SavedStateHandle) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SurahViewModel(repository, savedStateHandle) as T
    }
}