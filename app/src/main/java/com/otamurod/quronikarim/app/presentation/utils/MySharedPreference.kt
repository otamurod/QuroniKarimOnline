//package com.otamurod.quronikarim.app.presentation.utils
//
//import android.content.Context
//import android.content.SharedPreferences
//
//object MySharedPreference {
//
//    private const val NAME = "singleton"
//    private const val MODE = Context.MODE_PRIVATE
//    lateinit var sharedPreferences: SharedPreferences
//
//    fun init(context: Context) {
//        sharedPreferences = context.getSharedPreferences(NAME, MODE)
//    }
//
//    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
//        val editor = edit()
//        operation(editor)
//        editor.apply()
//    }
//
//    var translator: String?
//        get() = sharedPreferences.getString("translator", "")
//        set(value) = sharedPreferences.edit {
//            if (value != null) {
//                it.putString("translator", value)
//            }
//        }
//
//    var reciter: String?
//        get() = sharedPreferences.getString("reciter", "")
//        set(value) = sharedPreferences.edit {
//            if (value != null) {
//                it.putString("reciter", value)
//            }
//        }
//}