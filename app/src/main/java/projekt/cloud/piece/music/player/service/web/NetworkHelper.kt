package projekt.cloud.piece.music.player.service.web

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.annotation.RequiresApi
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

class NetworkHelper(context: Context) {
    
    companion object {
        private const val REGEX_IP_STR = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!\$)|\$)){4}\$"
        private val REGEX_IP get() = REGEX_IP_STR.toRegex()
        private const val LOCALHOST_IP = "127.0.0.1"
    }

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    
    val ipAddress: String?
        get() {
            if (!hasConnection) {
                return null
            }
            if (SDK_INT >= M) {
                ipAddressApi23Impl?.also { return it }
            }
            return ipAddressApi21Impl
        }
    
    val hasConnection get() = when {
        SDK_INT > M -> hasConnectionApi23Impl
        else -> hasConnectionApi21Impl
    }
    
    private val hasConnectionApi23Impl
        @RequiresApi(M)
        get() = activeNetworkCapabilities?.run {
            hasTransport(TRANSPORT_WIFI) || hasTransport(TRANSPORT_CELLULAR) || hasTransport(TRANSPORT_ETHERNET)
        } == true
    
    @Suppress("DEPRECATION")
    private val hasConnectionApi21Impl
        get() = activeNetworkInfo?.type in (TYPE_WIFI .. TYPE_MOBILE)
    
    @Suppress("DEPRECATION")
    private val activeNetworkInfo get() = connectivityManager.activeNetworkInfo
    
    private val ipAddressApi23Impl: String?
        @RequiresApi(M)
        get() = activityNetworkLinkAddresses?.find { it.address.hostAddress.checkIpAddress }?.address?.hostAddress
    
    private val activityNetwork: Network?
        @RequiresApi(M) get() = connectivityManager.activeNetwork
    
    private val activeNetworkCapabilities: NetworkCapabilities?
        @RequiresApi(M) get() = connectivityManager.getNetworkCapabilities(activityNetwork)
    
    private val activityNetworkLinkProperties: LinkProperties?
        @RequiresApi(M) get() = connectivityManager.getLinkProperties(activityNetwork)
    
    private val activityNetworkLinkAddresses
        @RequiresApi(M) get() = activityNetworkLinkProperties?.linkAddresses
    
    private val String?.checkIpAddress get() =
        this != null && this.matches(REGEX_IP) && this != LOCALHOST_IP
    
    private val ipAddressApi21Impl: String?
        get() {
            for (networkInterface in NetworkInterface.getNetworkInterfaces()) {
                for (inetAddress in networkInterface.inetAddresses) {
                    if (!inetAddress.isLoopbackAddress) {
                        if (inetAddress is Inet4Address || inetAddress is Inet6Address) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            }
            return null
        }
    
}