package com.otamurod.quronikarim.app.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.otamurod.quronikarim.app.presentation.ui.main.MainFragment
import com.otamurod.quronikarim.app.presentation.ui.surah.SurahFragment

@RequiresApi(Build.VERSION_CODES.N)
fun checkNetworkStatus(context: Context?, fragment: String): Boolean {
    val connection: Boolean
    val conMgr =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
    val activeNetwork = conMgr.activeNetworkInfo;
    connection = activeNetwork != null && activeNetwork.isConnected
    networkChangeListener(context, fragment)
    return connection
}

@RequiresApi(Build.VERSION_CODES.N)
private fun networkChangeListener(context: Context?, fragmentName: String) {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.let {
        it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                //take action when network connection is gained
                Toast.makeText(context, "You are Online", Toast.LENGTH_SHORT).show()

                //TODO: Call API when back online
                when (fragmentName) {
                    "main" -> {
                        MainFragment().getInstance()?.makeApiCall()
                    }
                    "surah" -> {
                        SurahFragment().getInstance()?.requestTranslation()
                    }
                }
            }

            override fun onLost(network: Network) {
                //take action when network connection is lost
                Toast.makeText(context, "You are Offline", Toast.LENGTH_LONG).show()
            }
        })
    }
}