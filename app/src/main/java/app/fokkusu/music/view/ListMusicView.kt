@file:Suppress("DuplicatedCode", "DuplicatedCode")

package app.fokkusu.music.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.MUSIC_LIST
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE_LOC
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.MusicUtil
import app.fokkusu.music.service.PlayService

/**
 * @File    : ListMusicView
 * @Author  : 1552980358
 * @Date    : 7 Oct 2019
 * @TIME    : 2:30 PM
 **/

class ListMusicView : ListView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    
    private lateinit var listViewAdapter: BaseAdapter
    
    init {
        setOnItemClickListener { _, _, position, _ ->
            context.startService(
                Intent(context, PlayService::class.java).putExtra(
                    SERVICE_INTENT_CONTENT, SERVICE_INTENT_CHANGE
                ).putExtra(
                    SERVICE_INTENT_CHANGE_SOURCE, MUSIC_LIST
                ).putExtra(SERVICE_INTENT_CHANGE_SOURCE_LOC, position)
            )
        }
        dividerHeight = 0
    }
    
    fun setUpAdapterWithMusicList(musicList: MutableList<MusicUtil>, page: Int) {
        adapter = BaseAdapter(context, musicList, page).apply { listViewAdapter = this }
    }
    
    fun updateMusic(musicList: MutableList<MusicUtil>) {
        listViewAdapter.updateMusicList(musicList)
    }
    
    fun getMusicList(): MutableList<MusicUtil> {
        return listViewAdapter.musicList
    }
    
    private open class BaseAdapter(
        private val context: Context,
        musicList: MutableList<MusicUtil>,
        private val page: Int
    ) : android.widget.BaseAdapter() {
        
        @SuppressLint("InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return if (page == 0) {
                LayoutInflater.from(context).inflate(R.layout.view_musicpage, null, false).apply {
                    findViewById<TextView>(R.id.textView_num).text = musicList[position].loc.plus(1).toString()
                    findViewById<TextView>(R.id.textView_title).text = musicList[position].title()
                    findViewById<TextView>(R.id.textView_subTitle).text =
                        musicList[position].artist().plus(" - ").plus(musicList[position].album())
                }
            } else {
                LayoutInflater.from(context).inflate(R.layout.view_searchpage, null, false).apply {
                    findViewById<TextView>(R.id.textView_title).text = musicList[position].title()
                    findViewById<TextView>(R.id.textView_subTitle).text =
                        musicList[position].artist().plus(" - ").plus(musicList[position].album())
                }
            }
        }
        
        override fun getItem(position: Int): Any {
            return position
        }
        
        @Suppress("JoinDeclarationAndAssignment")
        var musicList: MutableList<MusicUtil>
        
        init {
            this.musicList = musicList
        }
        
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        
        override fun getCount(): Int {
            return musicList.size
        }
        
        fun updateMusicList(musicList: MutableList<MusicUtil>) {
            this.musicList = musicList
            notifyDataSetChanged()
        }
    }
}