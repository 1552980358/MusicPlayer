package app.skynight.musicplayer.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_HEADSET_PLUG
import android.content.IntentFilter
import android.media.AudioManager
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTART
import java.lang.Exception

/**
 * @File    : PlayingControlUtil
 * @Author  : 1552980358
 * @Date    : 19 Aug 2019
 * @TIME    : 4:58 PM
 **/
class PlayingControlUtil private constructor() {
    private var headsetPlugInReceiver: BroadcastReceiver
    private var headsetPlugOutReceiver: BroadcastReceiver

    companion object {
        val getPlayingControlUtil by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PlayingControlUtil() }
    }

    init {
        MainApplication.getMainApplication().apply {
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    p1 ?: return
                    try {
                        when (p1.action) {
                            ACTION_HEADSET_PLUG -> {
                                when (p1.getIntExtra("state", -1)) {
                                    1 -> {
                                        if (Player.settings[Player.WiredPlugIn]!! as Boolean) {
                                            return
                                        }
                                        sendBroadcast(Intent(CLIENT_BROADCAST_ONPAUSE))
                                    }
                                    else -> return
                                }
                            }
                        }
                    } catch (e: Exception) {
                        //e.printStackTrace()
                    }
                }
            }.apply { headsetPlugInReceiver = this }, IntentFilter().apply {
                addAction(ACTION_HEADSET_PLUG)
                addAction(AudioManager.ACTION_HEADSET_PLUG)
            })
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    p1 ?: return
                    when (p1.action) {
                        AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                            sendBroadcast(Intent(if (Player.settings[Player.WiredPullOut]!! as Boolean) CLIENT_BROADCAST_ONSTART else CLIENT_BROADCAST_ONPAUSE))
                        }
                        BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                            if (bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_DISCONNECTED && Player.getPlayer.isPlaying()) {
                                sendBroadcast(Intent(if (Player.settings[Player.WirelessDis]!! as Boolean) CLIENT_BROADCAST_ONSTART else CLIENT_BROADCAST_ONPAUSE))
                            }
                        }
                    }
                }
            }.apply { headsetPlugOutReceiver = this }, IntentFilter().apply {
                addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            })
        }
    }
}