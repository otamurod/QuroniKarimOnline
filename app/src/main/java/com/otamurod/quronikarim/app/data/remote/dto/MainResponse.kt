package com.otamurod.quronikarim.app.data.remote.dto

data class MainResponse<T>(
    val code:Int,
    val status:String,
    val data:T
)
