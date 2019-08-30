package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_musiclist.*
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.util.*
import app.skynight.musicplayer.util.Player.Companion.ERROR_CODE
import app.skynight.musicplayer.util.Player.Companion.EXTRA_LIST
import app.skynight.musicplayer.util.Player.Companion.LIST_ALL
import app.skynight.musicplayer.view.MusicView

class MusicListActivity : BaseSmallPlayerActivity() {
    private var code = -999

    override fun onCreate(savedInstanceState: Bundle?) {
        log("MusicListActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musiclist)
        code = try {
            intent.getIntExtra(EXTRA_LIST, ERROR_CODE)
        } catch (e: Exception) {
            ERROR_CODE
        }
        Thread {
            try {
                val list = when (code) {
                    LIST_ALL -> {
                        log("MusicListActivity", "LIST_ALL")
                        MusicClass.getMusicClass.fullList
                    }
                    ERROR_CODE -> {
                        throw Exception()
                    }
                    else -> {
                        log("MusicListActivity", code)
                        PlayList.playListList[code].apply {
                            runOnUiThread { textView_size.text = getPlayList().size.toString() }
                        }.getPlayList()
                    }
                }

                log("MusicListActivity", "add MusicInfo")
                //val listViewList = mutableListOf<Map<String, String>>()
                for ((index, musicInfo) in list.withIndex()) {
                    val info = MusicView(this, code, index, musicInfo)
                    runOnUiThread { linearLayout_container.addView(info) }
                    //log("listViewList", "${index}")
                    //listViewList.add(mapOf("INDEX" to index.plus(1).toString(), "TITLE" to musicInfo.title(), "ARTIST" to musicInfo.artist()))
                }
                /*
                                val simpleAdapter = SimpleAdapter(
                                    this,
                                    listViewList,
                                    R.layout.layout_listview,
                                    arrayOf("INDEX", "TITLE", "ARTIST"),
                                    intArrayOf(R.id.listView_index, R.id.listView_title, R.id.listView_artist)
                                )*/
                /*
                                runOnUiThread {
                                    listView.apply {
                                        onItemClickListener =
                                            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                                                sendBroadcast(
                                                    Intent(BroadcastBase.CLIENT_BROADCAST_CHANGE).putExtra(BroadcastBase.BROADCAST_INTENT_PLAYLIST, code)
                                                        .putExtra(BroadcastBase.BROADCAST_INTENT_MUSIC, p2)
                                                )
                                                startActivity(Intent(this@MusicListActivity, PlayerActivity::class.java))
                                            }
                                        adapter = simpleAdapter
                                    }
                                }*/

                log("MusicListActivity", "load to Layout")
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    linearLayout_container.visibility = View.VISIBLE
                    //listView.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { makeToast(R.string.abc_musicList_unExpected_intent) }
            }
        }.start()

        try {
            when (code) {
                LIST_ALL -> {
                    toolbar.setTitle(R.string.abc_musicList_full)
                    toolbar.subtitle = MusicClass.getMusicClass.fullList.size.toString()
                    textView_title.setText(R.string.abc_musicList_full)
                }
                ERROR_CODE -> {
                    //
                }
                else -> {
                    PlayList.playListList[code].apply {
                        toolbar.title = playListName
                        toolbar.subtitle = getPlayList().size.toString()
                        textView_title.text = playListName
                        textView_subTitle.text = date
                    }
                }
            }

            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                log("MusicListActivity", "setNavigationOnClickListener")
                onBackPressed()
            }
            toolbar.overflowIcon = ContextCompat.getDrawable(
                this@MusicListActivity, R.drawable.ic_toolbar_more
            )
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
        setPlayerActivityFitsSystemWindows()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_musiclist, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> startActivity(
                Intent(this, SearchActivity::class.java).putExtra(
                    EXTRA_LIST, LIST_ALL
                )
            )
            R.id.menu_inTitle -> {
                Thread {
                    Player.changeSort("TITLE")
                    when (code) {
                        LIST_ALL -> {
                            for ((index, musicInfo) in MusicClass.getMusicClass.fullList.withIndex()) {
                                val info = MusicView(this, code, index, musicInfo)
                                runOnUiThread { linearLayout_container.addView(info) }
                            }
                        }
                    }
                }.start()
                linearLayout_container.removeAllViews()
            }
            R.id.menu_inArtist -> {
                    Thread {
                        Player.changeSort("ARTIST")
                        when (code) {
                            LIST_ALL -> {
                                for ((index, musicInfo) in MusicClass.getMusicClass.fullList.withIndex()) {
                                    val info = MusicView(this, code, index, musicInfo)
                                    runOnUiThread { linearLayout_container.addView(info) }
                                }
                            }
                        }
                    }.start()
                linearLayout_container.removeAllViews()
            }
            R.id.menu_inAlbum -> {
                Thread {
                    Player.changeSort("ALBUM")
                    when (code) {
                        LIST_ALL -> {
                            for ((index, musicInfo) in MusicClass.getMusicClass.fullList.withIndex()) {
                                val info = MusicView(this, code, index, musicInfo)
                                runOnUiThread { linearLayout_container.addView(info) }
                            }
                        }
                    }
                }.start()
                linearLayout_container.removeAllViews()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
