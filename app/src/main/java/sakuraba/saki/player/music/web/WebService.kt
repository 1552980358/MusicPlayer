package sakuraba.saki.player.music.web

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager.EXTRA_WIFI_STATE
import android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION
import android.net.wifi.WifiManager.WIFI_STATE_DISABLED
import android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.register
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.startService
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_START
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_STOP
import sakuraba.saki.player.music.util.ServiceUtil.isForegroundService
import sakuraba.saki.player.music.util.SettingUtil.getBooleanSetting
import sakuraba.saki.player.music.web.server.WebServer
import sakuraba.saki.player.music.web.util.NetworkUtil.hasConnection
import sakuraba.saki.player.music.web.util.NetworkUtil.ipAddress
import sakuraba.saki.player.music.web.util.NotificationUtil.createNotificationManager
import sakuraba.saki.player.music.web.util.NotificationUtil.getNotification
import sakuraba.saki.player.music.web.util.NotificationUtil.startForeground
import sakuraba.saki.player.music.web.util.NotificationUtil.updateNotification
import sakuraba.saki.player.music.web.util.WebControlUtil

class WebService: Service() {

    companion object {
        private const val DEFAULT_SERVER_PORT = 1552
        private const val DEFAULT_SERVER_PORT_STR = "$DEFAULT_SERVER_PORT"
    }

    private lateinit var webServer: WebServer
    private lateinit var notificationManager: NotificationManagerCompat

    private lateinit var connectivityManager: ConnectivityManager

    private var webControlUtil = WebControlUtil()

    private var serverPort = DEFAULT_SERVER_PORT

    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        intent ?: return@broadcastReceiver
        if (intent.action == WIFI_STATE_CHANGED_ACTION
            && intent.getIntExtra(EXTRA_WIFI_STATE, WIFI_STATE_UNKNOWN) == WIFI_STATE_DISABLED
            && getBooleanSetting(R.string.key_web_server_disconnect_enable)
            && webServer.isAlive) {
            startService(WebService::class.java) { putExtra(EXTRA_WEBSERVER, EXTRA_WEBSERVER_STOP) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = createNotificationManager
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        broadcastReceiver.register(this, WIFI_STATE_CHANGED_ACTION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra(EXTRA_WEBSERVER, EXTRA_WEBSERVER_STOP)) {
            EXTRA_WEBSERVER_START -> {
                if (!connectivityManager.hasConnection) {
                    stopForeground(true)
                }
                if (::webServer.isInitialized && webServer.isAlive) {
                    webServer.stop()
                }
                tryOnly {
                    serverPort = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.key_web_server_port), DEFAULT_SERVER_PORT_STR)
                        ?.toInt() ?: DEFAULT_SERVER_PORT
                }

                webServer = WebServer(serverPort, this, webControlUtil).apply { start() }
                when {
                    isForegroundService -> notificationManager.updateNotification(getNotification("${connectivityManager.ipAddress}:$serverPort"))
                    else -> startForeground(getNotification("${connectivityManager.ipAddress}:$serverPort"))
                }
            }
            EXTRA_WEBSERVER_STOP -> {
                stopForeground(true)
                if (::webServer.isInitialized && webServer.isAlive) {
                    webServer.stop()
                }
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}