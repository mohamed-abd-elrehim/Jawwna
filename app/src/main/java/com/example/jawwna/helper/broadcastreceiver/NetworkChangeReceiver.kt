import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.jawwna.helper.broadcastreceiver.NetworkStateChangeListener

class NetworkChangeReceiver(private val listener: NetworkStateChangeListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = isNetworkAvailable(context)
        listener.onNetworkStateChanged(isConnected)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo?.isConnected == true
        }
    }
}
