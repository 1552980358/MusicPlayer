package app.skynight.musicplayer.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import kotlinx.android.synthetic.main.activity_musiclist.*
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.Player.Companion.ERROR_CODE
import app.skynight.musicplayer.util.Player.Companion.EXTRA_LIST
import app.skynight.musicplayer.util.Player.Companion.LIST_ALL
import app.skynight.musicplayer.util.makeToast
import app.skynight.musicplayer.view.MusicView

class MusicListActivity : BaseSmallPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musiclist)
        Thread {
            try {
                val code = intent.getIntExtra(
                    EXTRA_LIST, ERROR_CODE
                )
                val list = when (code) {
                    LIST_ALL -> {
                        runOnUiThread {  }
                        toolbar.setTitle(R.string.abc_musicList_full)
                        textView_title.setText(R.string.abc_musicList_full)
                        while (!Player.prepareDone) {
                            try {
                                Thread.sleep(20)
                            } catch (e: Exception) {
                                //e.printStackTrace()
                            }
                        }
                        Player.musicList
                    }
                    ERROR_CODE -> {
                        throw Exception()
                    }
                    else -> {
                        Player.getPlayList(code).apply {
                            runOnUiThread {
                                toolbar.title = playListName
                                textView_title.text = playListName
                                textView_subTitle.text = date
                            }
                            while (!isInitialCompleted) {
                                try {
                                    Thread.sleep(20)
                                } catch (e: Exception) {
                                    //e.printStackTrace()
                                }
                            }
                        }.getPlayList()
                    }
                }

                runOnUiThread {
                    setSupportActionBar(toolbar)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                }

                for ((index, musicInfo) in list.withIndex()) {
                    val info = MusicView(this, code, index, musicInfo)
                    runOnUiThread { linearLayout_container.addView(info) }
                }

            } catch (e: Exception) {
                makeToast(R.string.abc_musicList_unExpected_intent)
                finish()
            }
        }.start()
        try {
            toolbar.setNavigationOnClickListener { finish() }
            toolbar.overflowIcon = ContextCompat.getDrawable(
                this,
                if (MainApplication.customize) R.drawable.ic_more_cust else R.drawable.ic_more_def
            )

            (toolbar::class.java.getDeclaredField("mTitleTextView").apply {
                isAccessible = true
            }.get(toolbar) as TextView).apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_musiclist, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
