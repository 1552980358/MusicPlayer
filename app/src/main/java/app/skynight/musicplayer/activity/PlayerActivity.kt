package app.skynight.musicplayer.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
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
import app.skynight.musicplayer.util.UnitUtil.Companion.getTime
import app.skynight.musicplayer.view.MusicAlbumRoundedImageView
import kotlinx.android.synthetic.main.activity_player.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.app.ActivityCompat
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.util.Player

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
        overridePendingTransition(R.anim.anim_static, R.anim.anim_top2down)
        super.onCreate(savedInstanceState)
        setBackgroundProp()
        setContentView(R.layout.activity_player)

        relativeLayout.addView(MusicAlbumRoundedImageView(this).apply {
            albumPic = this
        }, RelativeLayout.LayoutParams(
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
                    this@PlayerActivity, R.color.player_subtitle
                )
            )
            setNavigationOnClickListener {
                finish()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        imageButton_playForm.setBackgroundResource(
            when (Player.getPlayer.getPlayingType()) {
                Player.Companion.PlayingType.CYCLE -> {
                    R.drawable.ic_player_cycle
                }
                Player.Companion.PlayingType.SINGLE -> {
                    R.drawable.ic_player_single
                }
                else -> {
                    R.drawable.ic_player_random
                }
            }
        )

        imageButton_playForm.setOnClickListener {
            when (Player.getPlayer.getPlayingType()) {
                Player.Companion.PlayingType.CYCLE -> {
                    Player.getPlayer.setPlayingType(Player.Companion.PlayingType.SINGLE)
                    imageButton_playForm.setBackgroundResource(R.drawable.ic_player_single)
                }
                Player.Companion.PlayingType.SINGLE -> {
                    Player.getPlayer.setPlayingType(Player.Companion.PlayingType.RANDOM)
                    imageButton_playForm.setBackgroundResource(R.drawable.ic_player_random)
                }
                Player.Companion.PlayingType.RANDOM -> {
                    Player.getPlayer.setPlayingType(Player.Companion.PlayingType.CYCLE)
                    imageButton_playForm.setBackgroundResource(R.drawable.ic_player_cycle)
                }
            }
        }

        imageButton_last.setOnClickListener { sendBroadcast(Intent(CLIENT_BROADCAST_LAST)) }
        checkBox_playControl.apply {
            setOnClickListener {
                sendBroadcast(Intent(if (isChecked) CLIENT_BROADCAST_ONSTART else CLIENT_BROADCAST_ONPAUSE))
            }
        }
        imageButton_next.setOnClickListener { sendBroadcast(Intent(CLIENT_BROADCAST_NEXT)) }
        imageButton_list.setOnClickListener {
            startActivity(
                Intent(
                    this, BottomListActivity::class.java
                )
            )
        }

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
        registerReceiver(if (::broadcastReceiver.isInitialized) {
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
                            onUpdateMusic()
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
        onUpdateMusic()
    }

    fun onUpdateMusic() {
        val musicInfo = Player.getCurrentMusicInfo()
        Thread {
            try {/*
                val pic = musicInfo.albumPic()
                val tmp = Bitmap.createScaledBitmap(
                    pic,
                    resources.displayMetrics.heightPixels / pic.height * pic.width,
                    resources.displayMetrics.heightPixels,
                    false
                )*//*
                val drawable = BitmapDrawable(
                    resources, blurBitmap(
                        this, Bitmap.createBitmap(
                            tmp,
                            if (tmp.width <= resources.displayMetrics.widthPixels) 0 else (tmp.width - resources.displayMetrics.widthPixels) / 2,
                            0,
                            resources.displayMetrics.widthPixels,
                            tmp.height,
                            null,
                            false
                        ), 25f
                    )
                )*/
/*
                val pic = musicInfo.albumPic()
                val width = resources.displayMetrics.heightPixels / pic.height * pic.width
                val height = resources.displayMetrics.heightPixels
                log("w*h", "w:$width h:$height ")
                val tmp = Bitmap.createScaledBitmap(
                    pic,
                    width,
                    height,
                    true
                )

 */

                val pic = musicInfo.albumPic()
                val tmp = Bitmap.createBitmap(pic, 0, 0, pic.width, pic.height, Matrix().apply {
                    val scale = resources.displayMetrics.heightPixels / pic.height.toFloat()
                    postScale(scale, scale)
                }, true)

                val drawable = BitmapDrawable(
                    resources, Bitmap.createBitmap(
                        tmp,
                        if (tmp.width <= resources.displayMetrics.widthPixels) 0 else (tmp.width - resources.displayMetrics.widthPixels) / 2,
                        0,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        null,
                        true
                    )
                )

                runOnUiThread { backgroundDrawerLayout.background = drawable }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
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
        overridePendingTransition(R.anim.anim_static, R.anim.anim_top2down)
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
