package com.otamurod.quronikarim.app.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.otamurod.quronikarim.app.presentation.ui.main.requestAllSurahs

@RequiresApi(Build.VERSION_CODES.N)
fun checkNetworkStatus(context: Context?): Boolean {
    var connection = false

    val conMgr =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
    val activeNetwork = conMgr.activeNetworkInfo;

    if (activeNetwork != null && activeNetwork.isConnected) {
        // notify user you are online
        Toast.makeText(context, "Network State: Online", Toast.LENGTH_SHORT).show()
        connection = true
    } else {
        // notify user you are not online
        Toast.makeText(context, "Network State: Offline", Toast.LENGTH_LONG).show()
        connection = false
    }

    networkChangeListener(context)

    return connection
}

@RequiresApi(Build.VERSION_CODES.N)
private fun networkChangeListener(context: Context?) {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.let {
        it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                //take action when network connection is gained
                Toast.makeText(context, "Back Online", Toast.LENGTH_SHORT).show()
                requestAllSurahs()
            }

            override fun onLost(network: Network) {
                //take action when network connection is lost
                Toast.makeText(context, "You are Offline", Toast.LENGTH_LONG).show()
            }
        })
    }
}