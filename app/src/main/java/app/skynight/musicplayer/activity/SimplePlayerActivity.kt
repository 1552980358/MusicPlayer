package app.skynight.musicplayer.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.skynight.musicplayer.R
import app.skynight.musicplayer.broadcast.BroadcastBase
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.getTime
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.util.makeToast
import kotlinx.android.synthetic.main.activity_simpleplayer.pass
import kotlinx.android.synthetic.main.activity_simpleplayer.total
import kotlinx.android.synthetic.main.activity_simpleplayer.relativeLayout_root
import kotlinx.android.synthetic.main.activity_simpleplayer.toolbar

/**
 * @File    : SimplePlayerActivity
 * @Author  : 1552980358
 * @Date    : 9 Sep 2019
 * @TIME    : 7:06 PM
 **/

class SimplePlayerActivity : AppCompatActivity() {

    private val broadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent ?: return
                when (intent.action) {
                    BroadcastBase.SERVER_BROADCAST_ONSTART -> {
                        startThread()
                    }
                    BroadcastBase.SERVER_BROADCAST_ONPAUSE -> {
                        try {
                            thread!!.interrupt()
                            thread = null
                        } catch (e: Exception) {
                            //e.printStackTrace()
                        }
                    }
                    BroadcastBase.SERVER_BROADCAST_MUSICCHANGE -> {
                        try {
                            thread!!.interrupt()
                            thread = null
                        } catch (e: Exception) {
                            //e.printStackTrace()
                        }
                        onUpdateMusic()
                        startThread()
                    }
                }
            }

        }
    }
    private var thread: Thread? = null

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        overridePendingTransition(R.anim.anim_static, R.anim.anim_top2down)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.WHITE

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simpleplayer)

        Typeface.createFromAsset(assets, "front/DIGITAL-Regular.ttf").apply {
            pass.typeface = this
            total.typeface = this
        }

        val lastDivide = resources.displayMetrics.widthPixels / 3f
        val stopDivide = lastDivide * 2
        val width = resources.displayMetrics.widthPixels / 5
        val height = resources.displayMetrics.heightPixels / 5
        val gestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    if (e!!.x <= lastDivide) {
                        sendBroadcast(Intent(BroadcastBase.CLIENT_BROADCAST_LAST))
                        return true
                    }
                    if (e.x in lastDivide..stopDivide) {
                        sendBroadcast(Intent(if (Player.getPlayer.isPlaying()) BroadcastBase.CLIENT_BROADCAST_ONPAUSE else BroadcastBase.CLIENT_BROADCAST_ONSTART))
                        return true
                    }
                    sendBroadcast(Intent(BroadcastBase.CLIENT_BROADCAST_NEXT))
                    return true
                }

                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float
                ): Boolean {
                    try {
                        if (e1!!.x - e2!!.x < -width) {
                            sendBroadcast(Intent(BroadcastBase.CLIENT_BROADCAST_LAST))
                            return true
                        }
                        if (e1.x - e2.x > width) {
                            sendBroadcast(Intent(BroadcastBase.CLIENT_BROADCAST_NEXT))
                            return true
                        }

                        if (e1.y - e2.y < -height) {
                            onBackPressed()
                            return true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return false
                }

                override fun onLongPress(e: MotionEvent?) {
                    super.onLongPress(e)
                    when (Player.getPlayer.getPlayingType()) {
                        Player.Companion.PlayingType.CYCLE -> {
                            Player.getPlayer.setPlayingType(Player.Companion.PlayingType.SINGLE)
                            makeToast("单曲循环")
                        }
                        Player.Companion.PlayingType.SINGLE -> {
                            Player.getPlayer.setPlayingType(Player.Companion.PlayingType.RANDOM)
                            makeToast("随机播放")
                        }
                        Player.Companion.PlayingType.RANDOM -> {
                            Player.getPlayer.setPlayingType(Player.Companion.PlayingType.CYCLE)
                            makeToast("列表循环")
                        }
                    }
                }
            })

        relativeLayout_root.setOnTouchListener { _, motionEvent ->
            return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
        }

        toolbar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                finish()
            }
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            try {
                (this.javaClass.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(
                    this
                ) as TextView).apply {
                    setHorizontallyScrolling(true)
                    marqueeRepeatLimit = -1
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                }
                (this.javaClass.getDeclaredField("mSubtitleTextView").apply { isAccessible = true }.get(
                    this
                ) as TextView).apply {
                    gravity = Gravity.CENTER
                }
            } catch (e: Exception) {
                //e.printStackTrace()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        onUpdateMusic()
        registerReceiver()
        startThread()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    @Suppress("unused")
    fun startThread() {
        try {
            thread!!.interrupt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Player.getPlayer.getCurrent().apply {
            if (this != -1) {
                log("Player.getPlayer.getCurrent", this)
                pass.text = getTime(this)
            } else {
                pass.text = getTime(0)
            }
        }

        thread = Thread {

            while (Player.getPlayer.isPlaying()) {
                runOnUiThread { pass.text = getTime(Player.getPlayer.getCurrent()) }

                try {
                    Thread.sleep(500)
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }.apply { start() }
    }

    @Suppress("unused")
    fun onUpdateMusic() {
        val musicInfo = Player.getCurrentMusicInfo()
        toolbar.apply {
            title = musicInfo.title()
            subtitle = musicInfo.artist()
        }
        total.text = getTime(musicInfo.duration())
    }

    private fun registerReceiver() {
        registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction(BroadcastBase.SERVER_BROADCAST_ONSTART)
            addAction(BroadcastBase.SERVER_BROADCAST_ONPAUSE)
            addAction(BroadcastBase.SERVER_BROADCAST_MUSICCHANGE)
        })
    }

    private fun unregisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }
}