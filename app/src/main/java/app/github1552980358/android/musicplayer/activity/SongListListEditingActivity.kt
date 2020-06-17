package app.github1552980358.android.musicplayer.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.SongListListEditingRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongListInfo
import kotlinx.android.synthetic.main.activity_song_list_list_editing.recyclerView
import kotlinx.android.synthetic.main.activity_song_list_list_editing.toolbar
import lib.github1552980358.ktExtension.jvm.io.readObjectAs
import lib.github1552980358.ktExtension.jvm.io.writeObject
import java.io.File

/**
 * [SongListListEditingActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/6/7
 * @time    : 17:13
 **/

class SongListListEditingActivity: AppCompatActivity() {
    
    private lateinit var listInfo: SongListInfo
    
    private var songList = null as SongList?
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list_list_editing)
        setSupportActionBar(toolbar)
        
        listInfo = intent.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo
        val adapter = SongListListEditingRecyclerViewAdapter()
        
        File(getExternalFilesDir(SongListDir), listInfo.listTitle).readObjectAs<SongList>()?.apply {
            this@SongListListEditingActivity.songList = this
            if (listInfo.customSort) {
                adapter.setList(audioListCustom, true)
            } else {
                adapter.setList(audioList, false)
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        ItemTouchHelper(SongListListEditingRecyclerViewAdapter.Callback()).attachToRecyclerView(recyclerView)
    }
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBackPressed() {
        File(getExternalFilesDir(SongListDir), listInfo.listTitle).writeObject(songList)
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }
    
}