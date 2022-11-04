package com.otamurod.quronikarim.app.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
fun checkNetworkStatus(context: Context?): Boolean {
    val connection: Boolean
    val conMgr =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
    @Suppress("DEPRECATION") val activeNetwork = conMgr.activeNetworkInfo
    @Suppress("DEPRECATION")
    connection = activeNetwork != null && activeNetwork.isConnected
    networkChangeListener(context)
    return connection
}

@RequiresApi(Build.VERSION_CODES.N)
private fun networkChangeListener(context: Context?) {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.registerDefaultNetworkCallback(object :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            //take action when network connection is gained
            Toast.makeText(context, "Online", Toast.LENGTH_SHORT).show()
        }

        override fun onLost(network: Network) {
            //take action when network connection is lost
            Toast.makeText(context, "Offline", Toast.LENGTH_LONG).show()
        }
    })
}