package app.skynight.musicplayer.activity

import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.text.TextUtils
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_HORIZONTAL
import android.widget.RelativeLayout.CENTER_VERTICAL
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.view.MusicAlbumRoundedImageView
import kotlinx.android.synthetic.main.activity_player.*
import android.widget.SeekBar.OnSeekBarChangeListener

/**
 * @FILE:   PlayerActivity
 * @AUTHOR: 1552950358
 * @DATE:   18 Jul 2019
 * @TIME:   7:23 PM
 **/

class PlayerActivity : AppCompatActivity() {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var albumPic: MusicAlbumRoundedImageView
    private var thread: Thread? = null
    private var seekBarOnTouched = false

    private fun setBackgroundProp() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        log("PlayerActivity", "onCreate")
        overridePendingTransition(R.anim.anim_player_down2top, R.anim.anim_last_down2top)
        super.onCreate(savedInstanceState)
        setBackgroundProp()
        setContentView(R.layout.activity_player)

        relativeLayout.addView(
            MusicAlbumRoundedImageView(this).apply {
                albumPic = this
            },
            RelativeLayout.LayoutParams(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.widthPixels * 2 / 3
            ).apply {
                addRule(CENTER_HORIZONTAL)
                addRule(CENTER_VERTICAL)
            })

        toolbar.apply {
            setSupportActionBar(this)
            setTitleTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.player_title))
            setSubtitleTextColor(
                ContextCompat.getColor(
                    this@PlayerActivity,
                    R.color.player_subtitle
                )
            )
            setNavigationOnClickListener {
                finish()
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

        try {
            (toolbar.javaClass.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(
                toolbar
            ) as TextView).apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                textView_timePass.text = getTime(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarOnTouched = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Player.getPlayer.onSeekChange(seekBar!!.progress)
                seekBarOnTouched = false
            }
        })
        startThread()
    }

    private fun startThread() {
        textView_timePass.text = getTime(Player.getPlayer.getCurrent())
        seekBar.progress = Player.getPlayer.getCurrent()
        thread = Thread {
            while (Player.getPlayer.isPlaying()) {
                runOnUiThread {
                    if (!seekBarOnTouched) {
                        textView_timePass.text = getTime(Player.getPlayer.getCurrent())
                        seekBar.progress = Player.getPlayer.getCurrent()
                    }
                }

                try {
                    Thread.sleep(500)
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }.apply { start() }
    }

    private fun registerReceiver() {
        registerReceiver(
            if (::broadcastReceiver.isInitialized) {
                broadcastReceiver
            } else {
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        intent ?: return
                        when (intent.action) {
                            SERVER_BROADCAST_ONSTART -> {
                                checkBox_playControl.isChecked = true
                                startThread()
                            }
                            SERVER_BROADCAST_ONPAUSE -> {
                                try {
                                    thread!!.interrupt()
                                    thread = null
                                } catch (e: Exception) {
                                    //e.printStackTrace()
                                }
                                checkBox_playControl.isChecked = false
                            }
                            SERVER_BROADCAST_MUSICCHANGE -> {
                                val musicInfo = Player.getCurrentMusicInfo()
                                toolbar.title = musicInfo.title()
                                toolbar.subtitle = musicInfo.artist()
                                albumPic.setImageBitmap(musicInfo.albumPic())
                                textView_timeTotal.text = getTime(musicInfo.duration())
                                checkBox_playControl.isChecked = true
                                startThread()
                            }
                        }
                    }

                }.apply { broadcastReceiver = this }
            }, IntentFilter().apply {
                addAction(SERVER_BROADCAST_ONSTART)
                addAction(SERVER_BROADCAST_ONPAUSE)
                addAction(SERVER_BROADCAST_MUSICCHANGE)
            })
    }

    private fun unregisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        log("PlayerActivity", "onResume")
        super.onResume()
        registerReceiver()
        val musicInfo = Player.getCurrentMusicInfo()
        toolbar.title = musicInfo.title()
        toolbar.subtitle = musicInfo.artist()
        albumPic.setImageBitmap(musicInfo.albumPic())
        textView_timeTotal.text = getTime(musicInfo.duration())
        seekBar.max = musicInfo.duration()
        checkBox_playControl.isChecked = Player.getPlayer.isPlaying()
        startThread()
    }

    override fun onPause() {
        log("PlayerActivity", "onPause")
        super.onPause()
        unregisterReceiver()
    }

    override fun onBackPressed() {
        log("PlayerActivity", "onBackPressed")
        finish()
    }

    override fun finish() {
        log("PlayerActivity", "finish")
        super.finish()
        overridePendingTransition(R.anim.anim_last_top2down, R.anim.anim_player_top2down)
    }

    override fun onDestroy() {
        log("PlayerActivity", "onDestroy")
        try {
            unregisterReceiver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
