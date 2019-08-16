package app.skynight.musicplayer.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log
import kotlinx.android.synthetic.main.activity_bottomlist.*

/**
 * @File    : BottomListActivity
 * @Author  : 1552980358
 * @Date    : 15 Aug 2019
 * @TIME    : 12:58 AM
 **/

class BottomListActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        log("BottomListActivity", "onCreate")
        overridePendingTransition(R.anim.anim_static, R.anim.anim_top2down)
        super.onCreate(savedInstanceState)
        setBackgroundProp()
        setContentView(R.layout.activity_bottomlist)

        upperView.setOnClickListener { onBackPressed() }
        imageButton_close.setOnClickListener { onBackPressed() }
        val array = intArrayOf(0, 0)
        linearLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (motionEvent.y > 0) {
                        linearLayout.scrollTo(array[0], -motionEvent.y.toInt())
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (motionEvent.y > linearLayout.height / 5 * 2) {
                        finish()
                        return@setOnTouchListener true
                    }
                    linearLayout.scrollTo(array[0], array[1])
                }
            }
            true
        }
        linearLayout.getLocationOnScreen(array)

        val info = Player.getCurrentMusicInfo()
        Thread {
            var path = info.path
            val stringBuilder = StringBuilder()
            if (path.isEmpty()) {
                return@Thread
            }
            for (i in path.lastIndex downTo 0) {
                if (path[i] == '.') {
                    break
                }
                stringBuilder.append(path[i])
            }
            path = stringBuilder.toString()
            stringBuilder.clear()
            for (i in path.lastIndex downTo 0) {
                stringBuilder.append(path[i])
            }
            runOnUiThread { textView_format.text = stringBuilder.toString() }
        }.start()
        textView_title.text = info.title()
        textView_artist.text = info.artist()
        textView_album.text = info.album()
        textView_bitrate.text = "${info.bitRate() / 1000} kBit/s"
        textView_path.text = info.path
    }

    private fun setBackgroundProp() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
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

}