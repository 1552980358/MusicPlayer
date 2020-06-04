package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.os
import app.github1552980358.android.musicplayer.dialog.AddToSongListDialogFragment
import app.github1552980358.android.musicplayer.dialog.CreateSongListDialogFragment
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * [AddToSongListDialogRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/27
 * @time    : 21:55
 **/

class AddToSongListDialogRecyclerViewAdapter(
    private val fragment: Fragment,
    private val audioData: AudioData,
    list: ArrayList<SongListInfo>
): Adapter<AddToSongListDialogRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [songListInfoList]
     * @author 1552980358
     * @since 0.1
     **/
    private var songListInfoList = list
    
    /**
     * [onCreateViewHolder]
     * @param parent [ViewGroup]
     * @param viewType [Int]
     * @return [ViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (fragment.requireContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_add_to_song_list_dialog_content, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @return [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return songListInfoList.size + 1
    }
    
    /**
     * [onBindViewHolder]
     * @param holder [ViewHolder]
     * @param position [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.imageViewCover.setImageResource(R.drawable.ic_add_song_list)
            holder.textViewTitle.setText(R.string.addToSongListDialog_create_new)
            holder.textViewSubtitle.visibility = View.GONE
            holder.relativeLayoutRoot.setOnClickListener {
                CreateSongListDialogFragment().apply { setFragment(fragment) }.showNow(fragment.childFragmentManager)
            }
            return
        }
        
        holder.relativeLayoutRoot.setOnClickListener {
            
            // Add to song list
            // 添加到歌单
            File(fragment.requireContext().getExternalFilesDir(SongListDir), songListInfoList[position - 1].listTitle).apply {
                
                val songList: SongList
                
                if (!exists()) {
                    songList = SongList().apply { listName = songListInfoList[position - 1].listTitle }
                } else {
                    inputStream().use { `is` ->
                        ObjectInputStream(`is`).use { ois ->
                            songList = ois.readObject() as SongList
                        }
                    }
                }
                
                songList.audioList.forEach { audioData ->
                    if (audioData.id == this@AddToSongListDialogRecyclerViewAdapter.audioData.id) {
                        return@setOnClickListener
                    }
                }
    
                songList.add(audioData)
    
                delete()
                createNewFile()
    
                outputStream().os { os ->
                    ObjectOutputStream(os).os { oos ->
                        oos.writeObject(songList)
                    }
                }
            }
            
            // Update size of song list
            // 更新歌单歌曲数量
            songListInfoList[position - 1].listSize++
            File(fragment.requireContext().getExternalFilesDir(AudioDataDir), SongListFile).apply {
                if (exists()) {
                    delete()
                }
                createNewFile()
    
                outputStream().os { os ->
                    ObjectOutputStream(os).os { oos ->
                        oos.writeObject(songListInfoList)
                    }
                }
            }
            
            (fragment as AddToSongListDialogFragment).dismiss()
            
        }
        
        if (songListInfoList[position - 1].hasCoverImage) {
            File(fragment.requireContext().getExternalFilesDir(SongListCoverDir), songListInfoList[position].listTitle).apply {
                inputStream().use { `is` ->
                    ObjectInputStream(`is`).use { ois ->
                        (ois.readObject() as SongListCover).apply {
                            holder.imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                        }
                    }
                }
            }
        } else {
            holder.imageViewCover.setImageResource(R.drawable.ic_launcher_foreground)
        }
        holder.textViewTitle.text = songListInfoList[position - 1].listTitle
        holder.textViewSubtitle.text = songListInfoList[position - 1].listSize.toString()
        
    }
    
    /**
     * [updateList]
     * @param songListInfoList [ArrayList]<[SongListInfo]>
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList(songListInfoList: ArrayList<SongListInfo>) {
        this.songListInfoList = songListInfoList
        notifyDataSetChanged()
    }
    
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        /**
         * [relativeLayoutRoot]
         * @author 1552980358
         * @since 0.1
         **/
        val relativeLayoutRoot: RelativeLayout = view.findViewById(R.id.relativeLayoutRoot)
        /**
         * [imageViewCover]
         * @author 1552980358
         * @since 0.1
         **/
        val imageViewCover: ImageView = view.findViewById(R.id.imageViewCover)
        /**
         * [textViewTitle]
         * @author 1552980358
         * @since 0.1
         **/
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        /**
         * [textViewSubtitle]
         * @author 1552980358
         * @since 0.1
         **/
        val textViewSubtitle: TextView = view.findViewById(R.id.textViewSubtitle)
    }
    
}