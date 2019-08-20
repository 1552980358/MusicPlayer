package app.skynight.musicplayer.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.UnitUtil.Companion.getTime
import app.skynight.musicplayer.util.log
import kotlinx.android.synthetic.main.activity_bottomlist.*
import java.io.File

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

        val info = Player.getCurrentMusicInfo()
        textView_path.text = info.path
        File(info.path).apply {
            textView_file.text = "$extension, ${length() / 1024}kB"
        }
        val duration = info.duration()
        textView_data.text = "${duration}s (${getTime(duration)}), ${info.bitRate() / 1000}kBit/s"
        textView_title.text = info.title()
        textView_artist.text = info.artist()
        textView_album.text = info.album()

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