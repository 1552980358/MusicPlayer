package app.skynight.musicplayer

import android.app.Application
import android.content.*
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.BROADCAST_INTENT_MUSIC
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.BROADCAST_INTENT_PLAYLIST
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.BroadcastSignalList
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_CHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_CYCLE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_NEXT
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTOP
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_RANDOM
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_SINGLE
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.Player.Companion.ERROR_CODE
import java.io.File
import java.lang.Exception

/**
 * @FILE:   MainApplication
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   5:47 PM
 **/

@Suppress("unused")
class MainApplication : Application() {
    companion object {
        //const val TAG = "MainApplication"
        var playerForeground = false
        private var mainApplication: MainApplication? = null
        fun getMainApplication(): MainApplication {
            return mainApplication as MainApplication
        }
        fun sendBroadcast(broadcast: String) {
            mainApplication!!.sendBroadcast(Intent(broadcast))
        }
        val sharedPreferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { mainApplication!!.getSharedPreferences("user", Context.MODE_PRIVATE)!! }

        var customize = false
        var bgDrawable = null as Drawable?
    }

    override fun onCreate() {
        super.onCreate()
        Thread {
            mainApplication = this
            Player.getPlayer
            sharedPreferences
            customize = sharedPreferences.getBoolean("customize", false)
            bgDrawable = if (customize) {
                setTheme(R.style.AppTheme_NoActionBar_Customize)
                if (MainApplication.sharedPreferences.getBoolean("img", false)) {
                    BitmapDrawable(resources, cacheDir.toString() + File.separator + "bg.img")
                } else {
                    ColorDrawable(Color.parseColor(sharedPreferences.getString("RGB", "#7b1fa2")))
                }
            } else {
                ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
        }.start()

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?: return
                //Log.e(TAG, intent.action)
                when (intent.action) {
                    CLIENT_BROADCAST_ONSTART -> { Player.getPlayer.onStart() }
                    CLIENT_BROADCAST_ONSTOP -> { Player.getPlayer.onStop() }
                    CLIENT_BROADCAST_ONPAUSE -> { Player.getPlayer.onPause() }
                    CLIENT_BROADCAST_LAST -> { Player.getPlayer.playLast() }
                    CLIENT_BROADCAST_NEXT-> { Player.getPlayer.playNext() }
                    CLIENT_BROADCAST_CHANGE -> {
                        Player.getPlayer.playChange(intent
                            .getIntExtra(BROADCAST_INTENT_PLAYLIST, ERROR_CODE),
                            intent.getIntExtra(BROADCAST_INTENT_MUSIC, ERROR_CODE)
                        )
                    }
                    CLIENT_BROADCAST_SINGLE -> { Player.getPlayer.setPlayingType(Player.Companion.PlayingType.SINGLE) }
                    CLIENT_BROADCAST_CYCLE -> { Player.getPlayer.setPlayingType() }
                    CLIENT_BROADCAST_RANDOM -> { Player.getPlayer.setPlayingType(Player.Companion.PlayingType.RANDOM) }
                }
            }
        }, IntentFilter().apply {
            BroadcastSignalList.forEach {
                addAction(it)
            }
        })
    }
}