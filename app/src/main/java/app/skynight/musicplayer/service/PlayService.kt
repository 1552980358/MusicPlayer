package app.skynight.musicplayer.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.PlayerActivity
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_NEXT
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log

/**
 * @FILE:   PlayService
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   10:21 AM
 **/

class PlayService : Service() {
    companion object {
        const val CHANNEL = "PlayService"
    }

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        log("PlayService", "~ onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL, "PlayerNotification", NotificationManager.IMPORTANCE_HIGH
            ).also {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                    createNotificationChannel(it)
                }
            }
        }
        startForeground(1, updateNotify())

        val notificationManager = NotificationManagerCompat.from(this)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                notificationManager.notify(1, updateNotify())
            }

        }.apply { broadcastReceiver = this }, IntentFilter().apply {
            addAction(SERVER_BROADCAST_MUSICCHANGE)
            addAction(SERVER_BROADCAST_ONPAUSE)
            addAction(SERVER_BROADCAST_ONSTART)
        })
    }

    private fun updateNotify(): Notification {
        System.gc()
        log("PlayService", "updateNotify ${Player.getPlayer.isPlaying()}")
        val musicInfo = Player.getCurrentMusicInfo()
        return androidx.core.app.NotificationCompat.Builder(this, CHANNEL)
            .apply {
                setLargeIcon(musicInfo.albumPic())
                setContentTitle(musicInfo.title())
                setContentText(musicInfo.artist())
                setSmallIcon(R.mipmap.ic_launcher)
                setStyle(NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setMediaSession(MediaSessionCompat(this@PlayService, "MediaSessionCompat").sessionToken)
                )
                addAction(
                    R.drawable.ic_noti_last,
                    CLIENT_BROADCAST_LAST,
                    PendingIntent.getBroadcast(this@PlayService, 0, Intent(CLIENT_BROADCAST_LAST), 0)
                )
                if (Player.getPlayer.isPlaying()) {
                    //setAutoCancel(true)
                    //setOngoing(true)
                    addAction(
                        R.drawable.ic_noti_pause, CLIENT_BROADCAST_ONPAUSE, PendingIntent.getBroadcast(
                            this@PlayService, 0, Intent(
                                CLIENT_BROADCAST_ONPAUSE
                            ), 0
                        )
                    )
                } else {
                    //setOngoing(false)
                    //setAutoCancel(true)
                    addAction(
                        R.drawable.ic_noti_play, CLIENT_BROADCAST_ONSTART, PendingIntent.getBroadcast(
                            this@PlayService, 0, Intent(
                                CLIENT_BROADCAST_ONSTART
                            ), 0
                        )
                    )
                }
                addAction(
                    R.drawable.ic_noti_next,
                    CLIENT_BROADCAST_NEXT,
                    PendingIntent.getBroadcast(this@PlayService, 0, Intent(CLIENT_BROADCAST_NEXT), 0)
                )
                setContentIntent(PendingIntent.getActivity(this@PlayService, 0, Intent(this@PlayService, PlayerActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
            }.build()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        super.onDestroy()
    }
}