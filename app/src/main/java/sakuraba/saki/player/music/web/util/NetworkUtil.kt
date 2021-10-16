package sakuraba.saki.player.music.web.util

import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.annotation.RequiresApi
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

object NetworkUtil {

    val ConnectivityManager.ipAddress get(): String? {
        if (!hasConnection) {
            return null
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getIpAddressApi23()
        }

        var ipv6: String? = null
        for (networkInterface in NetworkInterface.getNetworkInterfaces()) {
            for (inetAddress in networkInterface.inetAddresses) {
                if (!inetAddress.isLoopbackAddress) {
                    when (inetAddress) {
                        is Inet4Address -> return inetAddress.hostAddress
                        is Inet6Address -> ipv6 = inetAddress.hostAddress
                    }
                }
            }
        }
        return ipv6
    }

    val ConnectivityManager.hasConnection get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> hasConnectionApi23()
        else -> hasConnectionApi21()
    }

    @RequiresApi(23)
    private fun ConnectivityManager.getIpAddressApi23(): String? {
        getLinkProperties(activeNetwork)?.linkAddresses?.forEach { linkAddress ->
            val address = linkAddress?.address?.hostAddress
            // Filter IPv6
            if (address?.count { it == '.' } == 3 && address != "127.0.0.1") {
                return address
            }
        }
        return null
    }

    @RequiresApi(23)
    private fun ConnectivityManager.hasConnectionApi23() = getNetworkCapabilities(activeNetwork)?.run {
        hasTransport(TRANSPORT_WIFI) || hasTransport(TRANSPORT_CELLULAR) || hasTransport(TRANSPORT_ETHERNET)
    } == true

    @Suppress("Deprecation")
    private fun ConnectivityManager.hasConnectionApi21() = when (activeNetworkInfo?.type) {
        TYPE_WIFI, TYPE_MOBILE, TYPE_ETHERNET -> true
        else -> false
    }

}