package app.skynight.musicplayer.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.os.PowerManager
import java.io.File
import java.io.FileWriter
import java.net.URL

/**
 * @FILE:   PlayService
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   10:21 AM
 **/

class PlayService : Service() {
    private lateinit var binder: PlayerBinder


    override fun onBind(intent: Intent?): IBinder? {
        return PlayerBinder().apply {
            binder = this
        }
    }

    override fun onCreate() {
        super.onCreate()
/*
        applicationContext.registerReceiver(, IntentFilter().apply {

        })*/
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {

        super.onDestroy()
    }

}