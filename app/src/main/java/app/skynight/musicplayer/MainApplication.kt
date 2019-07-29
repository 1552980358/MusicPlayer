package app.skynight.musicplayer

import android.app.Application
import android.content.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.BroadcastSignalList
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_CYCLE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_NEXT
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTOP
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_RANDOM
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_SINGLE
import app.skynight.musicplayer.util.Player
import java.io.File

/**
 * @FILE:   MainApplication
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   5:47 PM
 **/

@Suppress("unused")
class MainApplication : Application() {
    companion object {
        var playerForeground = false
        private var mainApplication: MainApplication? = null
        fun getMainApplication(): MainApplication {
            return mainApplication as MainApplication
        }
        fun sendBroadcast(broadcast: String) {
            mainApplication!!.sendBroadcast(Intent(broadcast))
        }
        val sharedPreferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { mainApplication!!.getSharedPreferences("user", Context.MODE_PRIVATE) }

        var customize = false
        var bgDrawable = null as Drawable?

    }

    override fun onCreate() {
        super.onCreate()
        Thread {
            mainApplication = this
            Player.getPlayer
            sharedPreferences
            customize = MainApplication.sharedPreferences.getBoolean("customize", false)
            bgDrawable = if (customize) {
                if (MainApplication.sharedPreferences.getBoolean("img", false)) {
                    BitmapDrawable(resources, cacheDir.toString() + File.separator + "bg.img")
                } else {
                    ColorDrawable(255 shl 24 + sharedPreferences.getString("R", "123")!!.toInt() shl 16 +
                            sharedPreferences.getString("G", "31")!!.toInt() shl 8 +
                            sharedPreferences.getString("B", "162")!!.toInt())
                }
            } else {
                ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
        }.start()

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?: return
                when (intent.action) {
                    PLAYER_BROADCAST_ONSTART -> {
                        Player.getPlayer.onStart()
                    }
                    PLAYER_BROADCAST_ONSTOP -> {
                        Player.getPlayer.onStop()
                    }
                    PLAYER_BROADCAST_ONPAUSE -> {
                        Player.getPlayer.onPause()
                    }
                    PLAYER_BROADCAST_LAST -> {
                        Player.getPlayer.playLast()
                    }
                    PLAYER_BROADCAST_NEXT-> {
                        Player.getPlayer.playNext()
                    }
                    PLAYER_BROADCAST_SINGLE -> {
                        Player.getPlayer.setPlayingType(Player.Companion.PlayingType.SINGLE)
                    }
                    PLAYER_BROADCAST_CYCLE -> {
                        Player.getPlayer.setPlayingType()
                    }
                    PLAYER_BROADCAST_RANDOM -> {
                        Player.getPlayer.setPlayingType(Player.Companion.PlayingType.RANDOM)
                    }
                }
            }
        }, IntentFilter().apply {
            BroadcastSignalList.forEach {
                addAction(it)
            }
        })
    }
}