package app.skynight.musicplayer.activity

import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_NEXT
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.UnitUtil.Companion.getTime
import kotlinx.android.synthetic.main.activity_player.*

/**
 * @FILE:   PlayerActivity
 * @AUTHOR: 1552950358
 * @DATE:   18 Jul 2019
 * @TIME:   7:23 PM
 **/

class PlayerActivity : AppCompatActivity() {

    private fun setBackgroundProp() {
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
    
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("PlayerActivity", "onCreate")
        super.onCreate(savedInstanceState)
        //setContentView(createView())
        setBackgroundProp()
        setContentView(R.layout.activity_player)

        toolbar.apply {
            setSupportActionBar(this)
            setTitleTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.player_title))
            setSubtitleTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.player_subtitle))
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        imageButton_last.setOnClickListener { sendBroadcast(Intent(CLIENT_BROADCAST_LAST)) }
        checkBox_playControl.apply {
            setOnClickListener {
                sendBroadcast(Intent(if (isChecked) CLIENT_BROADCAST_ONSTART else CLIENT_BROADCAST_ONPAUSE))
            }
        }
        imageButton_next.setOnClickListener { sendBroadcast(Intent(CLIENT_BROADCAST_NEXT)) }

        registerReceiver()
        try {
            (toolbar.javaClass.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(toolbar) as TextView).apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }
    }
    
    private fun registerReceiver() {
        registerReceiver(if (::broadcastReceiver.isInitialized) {broadcastReceiver} else {object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?:return
                when (intent.action) {
                    SERVER_BROADCAST_ONSTART -> { checkBox_playControl.isChecked = true }
                    SERVER_BROADCAST_ONPAUSE -> { checkBox_playControl.isChecked = false }
                    SERVER_BROADCAST_MUSICCHANGE -> {
                        val musicInfo = Player.musicList[Player.currentMusic]
                        toolbar.title = musicInfo.title
                        toolbar.subtitle = musicInfo.artist
                        //musicAlbum.setImageBitmap(musicInfo.albumPic())
                        textView_timeTotal.text = getTime(musicInfo.duration)
                    }
                }
            }
        }.apply { broadcastReceiver = this }}, IntentFilter().apply {
            addAction(SERVER_BROADCAST_ONSTART)
            addAction(SERVER_BROADCAST_ONPAUSE)
            addAction(SERVER_BROADCAST_MUSICCHANGE)
        })
    }

    private fun unregisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        //Log.e("PlayerActivity", "onResume")
        registerReceiver()
    }

    override fun onPause() {
        //Log.e("PlayerActivity", "onPause")
        super.onPause()
        unregisterReceiver()
    }

    override fun onBackPressed() {
        //Log.e("PlayerActivity", "onBackPressed")
        //overridePendingTransition(0, R.anim.anim_top2down)
        //MainApplication.playerForeground = false
        //startActivity(Intent(this, MainActivity::class.java))
        //super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_no_action, R.anim.anim_top2down)
    }

    override fun onDestroy() {
        //Log.e("PlayerActivity", "onDestroy")
        try {
            unregisterReceiver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
