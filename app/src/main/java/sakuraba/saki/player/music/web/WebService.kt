package sakuraba.saki.player.music.web

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_START
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_STOP
import sakuraba.saki.player.music.web.server.WebServer
import sakuraba.saki.player.music.web.util.NetworkUtil.hasConnection
import sakuraba.saki.player.music.web.util.NetworkUtil.ipAddress
import sakuraba.saki.player.music.web.util.NotificationUtil.createNotificationManager
import sakuraba.saki.player.music.web.util.NotificationUtil.getNotification
import sakuraba.saki.player.music.web.util.NotificationUtil.startForeground
import sakuraba.saki.player.music.web.util.WebControlUtil

class WebService: Service() {

    companion object {
        private const val SERVER_PORT = 1552
    }

    private lateinit var webServer: WebServer
    private lateinit var notificationManager: NotificationManagerCompat

    private lateinit var connectivityManager: ConnectivityManager

    private var webControlUtil = WebControlUtil()

    override fun onCreate() {
        super.onCreate()
        notificationManager = createNotificationManager
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        webServer = WebServer(SERVER_PORT, this, connectivityManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return super.onStartCommand(intent, flags, startId)

        when (intent.getIntExtra(EXTRA_WEBSERVER, EXTRA_WEBSERVER_STOP)) {
            EXTRA_WEBSERVER_START -> {
                if (!connectivityManager.hasConnection) {
                    stopForeground(true)
                }
                if (!webServer.isAlive) {
                    webServer.start()
                }
                startForeground(getNotification("${connectivityManager.ipAddress}:$SERVER_PORT"))
            }
            EXTRA_WEBSERVER_STOP -> {
                stopForeground(true)
                if (webServer.isAlive) {
                    webServer.stop()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

}