package projekt.cloud.piece.music.player.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_SERVER
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_STOP_SERVER
import projekt.cloud.piece.music.player.service.web.NetworkHelper
import projekt.cloud.piece.music.player.service.web.NotificationHelper
import projekt.cloud.piece.music.player.service.web.WebServer
import projekt.cloud.piece.music.player.service.web.WebServer.Companion.SERVER_PORT

class WebService: Service() {
    
    private lateinit var webServer: WebServer
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var networkHelper: NetworkHelper
    
    override fun onCreate() {
        super.onCreate()
        webServer = WebServer(this)
        notificationHelper = NotificationHelper(this)
        networkHelper = NetworkHelper(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVER -> {
                webServer.isLaunched = true
                notificationHelper.startForeground(this, "${networkHelper.ipAddress}:$SERVER_PORT")
            }
            ACTION_STOP_SERVER -> {
                webServer.isLaunched = false
                stopForeground(true)
            }
        }
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
}