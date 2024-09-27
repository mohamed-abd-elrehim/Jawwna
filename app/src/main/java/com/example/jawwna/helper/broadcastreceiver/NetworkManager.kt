package com.example.jawwna.helper.broadcastreceiver
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.jawwna.datasource.repository.Repository


class NetworkManager (private val application: Application) : INetworkManager{

    companion object {
        @Volatile
        private var INSTANCE: NetworkManager? = null

        fun getRepository(application: Application): NetworkManager {
            return INSTANCE ?: synchronized(this) {
                NetworkManager(application).also {
                    INSTANCE = it
                }
            }
        }
    }
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

}
