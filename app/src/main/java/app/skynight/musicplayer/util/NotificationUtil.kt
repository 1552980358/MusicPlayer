package app.skynight.musicplayer.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.PlayerActivity
import app.skynight.musicplayer.broadcast.BroadcastBase

/**
 * @File    : NotificationUtil
 * @Author  : 1552980358
 * @Date    : 18 Aug 2019
 * @TIME    : 2:32 PM
 **/

class NotificationUtil private constructor() {
    private var broadcastReceiver: BroadcastReceiver
    private var notificationManagerCompat: NotificationManagerCompat

    companion object {
        val getNotificationUtil by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NotificationUtil() }
        const val CHANNEL = "Play"
    }

    init {
        log("PlayerNotification", "create")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL, "PlayerNotification", NotificationManager.IMPORTANCE_LOW
            ).also {
                (MainApplication.getMainApplication().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                    createNotificationChannel(it)
                }
            }
        }

        notificationManagerCompat =
            NotificationManagerCompat.from(MainApplication.getMainApplication())

        MainApplication.getMainApplication().registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                notificationManagerCompat.notify(1, updateNotify())
            }

        }.apply { broadcastReceiver = this }, IntentFilter().apply {
            addAction(BroadcastBase.SERVER_BROADCAST_MUSICCHANGE)
            addAction(BroadcastBase.SERVER_BROADCAST_ONPAUSE)
            addAction(BroadcastBase.SERVER_BROADCAST_ONSTART)
        })
    }

    private fun updateNotify(): Notification {
        System.gc()

        val musicInfo = Player.getCurrentMusicInfo()

        return androidx.core.app.NotificationCompat.Builder(
            MainApplication.getMainApplication(),
            CHANNEL
        ).apply {
            setLargeIcon(musicInfo.albumPic())
            setContentTitle(musicInfo.title())
            setContentText(musicInfo.artist())
            setSmallIcon(R.mipmap.ic_launcher)
            setStyle(
                NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                    //.setShowCancelButton(true)
                    .setMediaSession(
                        MediaSessionCompat(
                            MainApplication.getMainApplication(),
                            "MediaSessionCompat"
                        ).sessionToken
                    )
            )
            addAction(
                R.drawable.ic_noti_last,
                BroadcastBase.CLIENT_BROADCAST_LAST,
                PendingIntent.getBroadcast(
                    MainApplication.getMainApplication(),
                    0,
                    Intent(BroadcastBase.CLIENT_BROADCAST_LAST),
                    0
                )
            )
            if (Player.getPlayer.isPlaying()) {
                setOngoing(true)
                setAutoCancel(false)
                addAction(
                    R.drawable.ic_noti_pause,
                    BroadcastBase.CLIENT_BROADCAST_ONPAUSE,
                    PendingIntent.getBroadcast(
                        MainApplication.getMainApplication(), 0, Intent(
                            BroadcastBase.CLIENT_BROADCAST_ONPAUSE
                        ), 0
                    )
                )
            } else {
                setOngoing(false)
                setAutoCancel(true)
                addAction(
                    R.drawable.ic_noti_play,
                    BroadcastBase.CLIENT_BROADCAST_ONSTART,
                    PendingIntent.getBroadcast(
                        MainApplication.getMainApplication(), 0, Intent(
                            BroadcastBase.CLIENT_BROADCAST_ONSTART
                        ), 0
                    )
                )
            }
            addAction(
                R.drawable.ic_noti_next,
                BroadcastBase.CLIENT_BROADCAST_NEXT,
                PendingIntent.getBroadcast(
                    MainApplication.getMainApplication(),
                    0,
                    Intent(BroadcastBase.CLIENT_BROADCAST_NEXT),
                    0
                )
            )
            setContentIntent(
                PendingIntent.getActivity(
                    MainApplication.getMainApplication(),
                    0,
                    Intent(MainApplication.getMainApplication(), PlayerActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }.build()
    }

    fun onDestroy() {
        MainApplication.getMainApplication().unregisterReceiver(broadcastReceiver)
        notificationManagerCompat.cancel(1)
    }
}