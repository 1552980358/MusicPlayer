package sakuraba.saki.player.music.service.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat.Token
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.media.app.NotificationCompat.MediaStyle
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.BuildConfig
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt

private const val ChannelName = "PlayServiceNotification"

private const val ChannelId = "${BuildConfig.APPLICATION_ID}.PlayServiceNotification"

private const val NotificationId = 23333

val Context.createNotificationManager get(): NotificationManagerCompat = NotificationManagerCompat.from(this).createChannel

val NotificationManagerCompat.createChannel get(): NotificationManagerCompat = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(NotificationChannel(ChannelId, ChannelName, IMPORTANCE_HIGH))
    }
}

fun Service.startForeground(notification: Notification) = startForeground(NotificationId, notification)

fun Notification?.getNotification(context: Context, token: Token, audioInfo: AudioInfo) = if (this == null) {
    NotificationCompat.Builder(context, ChannelId)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setOngoing(false)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setStyle(MediaStyle().setMediaSession(token))
} else {
    NotificationCompat.Builder(context, this)
}.apply {
    setWhen(System.currentTimeMillis())
    setContentTitle(audioInfo.audioTitle)
    setContentText("${audioInfo.audioArtist} - ${audioInfo.audioAlbum}")
    var bitmap: Bitmap? = null
    tryOnly { bitmap = context.loadAlbumArt(audioInfo.audioAlbumId) }
    if (bitmap != null) {
        setLargeIcon(bitmap)
    } else {
        setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
    }
}.build()