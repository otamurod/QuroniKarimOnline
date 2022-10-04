package com.otamurod.apicallusingmvvmcoroutines.models

//data class RecyclerList(val items: ArrayList<RecyclerData>)
//data class RecyclerData(val name: String, val description: String, val owner: Owner)
//data class Owner(val avatar_url: String)

data class RecyclerList(val data: ArrayList<RecyclerData>)
data class RecyclerData(
    val number: Int,
    val name: String,
    val englishName: String,
    val ayahs: Ayahs,
    val edition: Edition
)

data class Ayahs(
    val number: Int,
    val text: String
)

data class Edition(
    val identifier: String,
    val language: String,
    val name: String
)