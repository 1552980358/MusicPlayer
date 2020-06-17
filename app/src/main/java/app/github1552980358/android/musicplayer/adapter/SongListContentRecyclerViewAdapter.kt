package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.SongListActivity
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Constant.Companion.CurrentSongList

/**
 * [SongListContentRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/26
 * @time    : 21:32
 **/

class SongListContentRecyclerViewAdapter(
    private val activity: SongListActivity,
    private val songListTitle: String,
    list: ArrayList<AudioData>
): Adapter<SongListContentRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [songList]
     * @author  : 1552980358
     * @since 0.1
     **/
    private var songList = list
    
    /**
     * [onCreateViewHolder]
     * @param parent [ViewGroup]
     * @param viewType [Int]
     * @author  : 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (activity.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list_content, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @return [Int]
     * @author  : 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return songList.size + 1
    }
    
    /**
     * [onBindViewHolder]
     * @param holder [ViewHolder]
     * @param position [Int]
     * @author  : 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (songList.isEmpty()) {
            holder.relativeLayoutRoot.visibility = View.GONE
            return
        }
        
        if (position == songList.size) {
            holder.imageButtonOpts.visibility = View.GONE
            holder.relativeLayoutRoot.isClickable = false
            return
        }
        
        // Reason refer to [app.github1552980358.android.musicplayer.adapter.ListFragmentRecyclerViewAdapter]
        // 原因请看 [app.github1552980358.android.musicplayer.adapter.ListFragmentRecyclerViewAdapter]
        holder.relativeLayoutRoot.visibility = View.VISIBLE
        
        holder.relativeLayoutRoot.setOnClickListener {
            Log.e("relativeLayoutRoot", "onClick")
            activity.mediaControllerCompat.transportControls.playFromMediaId(
                songList[position].id,
                Bundle().apply { putString(CurrentSongList, songListTitle) }
            )
        }
    
        holder.textViewNo.text = position.plus(1).toString()
        holder.textViewTitle.text = songList[position].title
        holder.textViewSubtitle.text = songList[position].artist
        holder.imageButtonOpts.setOnClickListener {
        
        }
    
    }
    
    /**
     * [updateSongList]
     * @param list [ArrayList]<[AudioData]>
     * @author  : 1552980358
     * @since 0.1
     **/
    fun updateSongList(list: ArrayList<AudioData>) {
        songList = list
        notifyDataSetChanged()
    }
    
    /**
     * [ViewHolder]
     * @param view [View]
     * @author  : 1552980358
     * @since 0.1
     **/
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        /**
         * [relativeLayoutRoot]
         * @author  : 1552980358
         * @since 0.1
         **/
        val relativeLayoutRoot: RelativeLayout = view.findViewById(R.id.relativeLayoutRoot)
        
        /**
         * [textViewNo]
         * @author  : 1552980358
         * @since 0.1
         **/
        val textViewNo: TextView = view.findViewById(R.id.textViewNo)
        
        /**
         * [textViewTitle]
         * @author  : 1552980358
         * @since 0.1
         **/
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        
        /**
         * [textViewSubtitle]
         * @author  : 1552980358
         * @since 0.1
         **/
        val textViewSubtitle: TextView = view.findViewById(R.id.textViewSubtitle)
        
        /**
         * [imageButtonOpts]
         * @author  : 1552980358
         * @since 0.1
         **/
        val imageButtonOpts: ImageButton = view.findViewById(R.id.imageButtonOpts)
    }
    
}