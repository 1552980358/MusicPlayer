package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongList.Companion.SongListInfo
import java.io.File
import java.io.ObjectOutputStream

/**
 * [SongListRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 11:01
 **/

class SongListRecyclerViewAdapter(arrayList: ArrayList<SongListInfo>):
    RecyclerView.Adapter<SongListRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [data]
     **/
    private var data = arrayList
    
    /**
     * [updateList]
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList(arrayList: ArrayList<SongListInfo>) {
        data = arrayList
        notifyDataSetChanged()
    }
    
    /**
     * [onCreateViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return data.size
    }
    
    /**
     * [onBindViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = data[position].listName
        holder.textViewTitle.ellipsize = TextUtils.TruncateAt.END
        holder.textViewSubtitle.text = data[position].listSize.toString()
        holder.relativeLayoutRoot.setOnClickListener {
        }
        holder.imageButtonOpts.setOnClickListener { imageButtonOpts ->
            PopupMenu(imageButtonOpts.context, imageButtonOpts).apply {
                inflate(R.menu.menu_song_list)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        
                        R.id.menu_edit_list -> {
                            //
                        }
                        
                        R.id.menu_delete_list -> {
                            File(imageButtonOpts.context.getExternalFilesDir(SongListDir), SongList.songListInfoList.removeAt(position).listName)
                                .delete()
                            File(imageButtonOpts.context.getExternalFilesDir(AudioDataDir), SongListFile).apply {
                                delete()
                                createNewFile()
                                outputStream().use { os ->
                                    ObjectOutputStream(os).use { oos ->
                                        oos.writeObject(SongList.songListInfoList)
                                        oos.flush()
                                    }
                                    os.flush()
                                }
                            }
                            notifyDataSetChanged()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
    }
    
    /**
     * [ViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)!!
        val textViewSubtitle = view.findViewById<TextView>(R.id.textViewSubtitle)!!
        val relativeLayoutRoot = view.findViewById<RelativeLayout>(R.id.relativeLayoutRoot)!!
        val imageButtonOpts = view.findViewById<ImageButton>(R.id.imageButtonOpts)!!
    }
    
}