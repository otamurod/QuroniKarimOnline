package com.otamurod.apicallusingmvvmcoroutines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerList
import com.otamurod.apicallusingmvvmcoroutines.network.RetrofitInstance
import com.otamurod.apicallusingmvvmcoroutines.network.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query

class MainViewModel : ViewModel() {

    private lateinit var recyclerListLiveData: MutableLiveData<RecyclerList>

    init {
        recyclerListLiveData = MutableLiveData()
    }

    fun getRecyclerListObserver(): MutableLiveData<RecyclerList> {
        return recyclerListLiveData
    }

    fun makeApiCall(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val retrofitService = RetrofitInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retrofitService.getDataFromAPI(query)
            recyclerListLiveData.postValue(response)
        }
    }
}