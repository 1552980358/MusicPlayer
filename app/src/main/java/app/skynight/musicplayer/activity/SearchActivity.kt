package app.skynight.musicplayer.activity

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.MusicClass
import app.skynight.musicplayer.util.MusicInfo
import app.skynight.musicplayer.util.PlayList
import app.skynight.musicplayer.util.Player.Companion.ERROR_CODE
import app.skynight.musicplayer.util.Player.Companion.EXTRA_LIST
import app.skynight.musicplayer.util.Player.Companion.LIST_ALL
import app.skynight.musicplayer.view.SearchMusicView
import kotlinx.android.synthetic.main.activity_search.*

/**
 * @File    : SearchActivity
 * @Author  : 1552980358
 * @Date    : 21 Aug 2019
 * @TIME    : 3:10 PM
 **/
class SearchActivity : BaseSmallPlayerActivity() {

    private lateinit var musicInfoList: MutableList<MusicInfo>
    private var code = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        Thread {
            code = try {
                intent.getIntExtra(EXTRA_LIST, ERROR_CODE)
            } catch (e: Exception) {
                ERROR_CODE
            }

            try {
                musicInfoList = when (code) {
                    LIST_ALL -> {
                        MusicClass.getMusicClass.fullList
                    }
                    ERROR_CODE -> {
                        throw Exception("")
                    }
                    else -> {
                        PlayList.playListList[code].getPlayList()
                    }
                }

            } catch (e: Exception) {
                //e.printStackTrace()
                onBackPressed()
            }
        }.start()

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        menu.apply {
            (this!!.findItem(R.id.menu_s).actionView as SearchView).apply {
                onActionViewExpanded()
                isSubmitButtonEnabled = false
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return onQueryTextSubmit(query)
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?: return false

                        linearLayout.removeAllViews()
                        if (newText.isEmpty()) {
                            return true
                        }
                        for ((j, i) in musicInfoList.withIndex()) {
                            if (i.title().contains(newText) || i.artist().contains(newText)) {
                                SearchMusicView(this@SearchActivity, code, j, i).apply {
                                    linearLayout.addView(this)
                                }
                            }
                        }
                        return true
                    }
                })
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

}