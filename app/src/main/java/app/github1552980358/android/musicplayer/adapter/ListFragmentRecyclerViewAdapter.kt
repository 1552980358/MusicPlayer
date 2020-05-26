package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.MainActivity
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataList
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.CurrentSongList
import app.github1552980358.android.musicplayer.base.Constant.Companion.FULL_LIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.IgnoredFile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.view_media_list.view.imageButtonOpts
import java.io.File
import java.io.ObjectOutputStream

/**
 * @file    : [ListFragmentRecyclerViewAdapter]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/10
 * @time    : 13:05
 **/

class ListFragmentRecyclerViewAdapter(
    private val bottomSheetBehavior: BottomSheetBehavior<View>,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val mainActivity: MainActivity
) :
    Adapter<ListFragmentRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [onCreateViewHolder]
     * @param parent [ViewGroup]
     * @param viewType [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_media_list, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @return [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return audioDataList.size + 1
    }
    
    /**
     * [onBindViewHolder]
     * @param holder [ViewHolder]
     * @param position [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        // 不显示
        if (audioDataList.isEmpty()){
            holder.relativeLayoutRoot.visibility = View.GONE
            return
        }
        if (position == audioDataList.size) {
            holder.relativeLayoutRoot.isClickable = false
            holder.imageButtonOpts.visibility = View.GONE
            return
        }
        
        holder.relativeLayoutRoot.visibility = View.VISIBLE
        
        // No need, automatically set when setting onClickListener
        // 不需要手动设置, 设置监听时会自动启动
        //holder.imageButtonOpts.isClickable = true

        holder.textViewNo.text = position.plus(1).toString()
        holder.textViewTitle.apply {
            text = audioDataList[position].title
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        holder.textViewSubtitle.apply {
            text = audioDataList[position].artist
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        
        holder.relativeLayoutRoot.setOnClickListener {
            // Remove action
            // 清除动作
            // holder.textViewTitle.ellipsize = TextUtils.TruncateAt.END
            // holder.textViewTitle.clearFocus()
            
            // Check if collapsing is required
            // 检测是否需要折叠
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return@setOnClickListener
            }
            
            mainActivity.mediaControllerCompat.transportControls.playFromMediaId(
                audioDataList[position].id,
                Bundle().apply { putString(CurrentSongList, FULL_LIST) }
            )
            
        }
        
        // Toast out full name
        // 弹出全名
        holder.relativeLayoutRoot.setOnLongClickListener {
            Toast.makeText(it.context, audioDataList[position].title, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        
        // Options
        // 选项
        holder.imageButtonOpts.setOnClickListener {
            PopupMenu(it.context, holder.imageButtonOpts.imageButtonOpts).apply {
                inflate(R.menu.menu_audio_opt)
                setOnMenuItemClickListener {
                    
                    File(mainActivity.getExternalFilesDir(AudioDataDir), IgnoredFile).appendText(audioDataList[position].id + "\n")

                    audioDataList.removeAt(position)
                    File(mainActivity.getExternalFilesDir(AudioDataDir), AudioDataListFile).apply {
                        //if (!exists()) {
                        //    createNewFile()
                        //}
                        //writeText("")
                        delete()
                        createNewFile()
        
                        // Write
                        // 写入
                        outputStream().use { os ->
                            ObjectOutputStream(os).use { oos ->
                                oos.writeObject(audioDataList)
                                oos.flush()
                            }
                            os.flush()
                        }
                    }

                    //mainActivity.updateList()
                    notifyDataSetChanged()
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
        
    }
    
    /**
     * [updateList]
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList() {
        notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }
    
    /**
     * [ViewHolder]
     * @param view [View]
     * @author 1552980358
     * @since 0.1
     **/
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * [textViewNo]
         * @author 1552980358
         * @since 0.1
         **/
        var textViewNo: TextView = view.findViewById(R.id.textViewNo)
        /**
         * [textViewTitle]
         * @author 1552980358
         * @since 0.1
         **/
        var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        /**
         * [textViewSubtitle]
         * @author 1552980358
         * @since 0.1
         **/
        var textViewSubtitle: TextView = view.findViewById(R.id.textViewSubtitle)
        /**
         * [relativeLayoutRoot]
         * @author 1552980358
         * @since 0.1
         **/
        var relativeLayoutRoot: RelativeLayout = view.findViewById(R.id.relativeLayoutRoot)
        /**
         * [imageButtonOpts]
         * @author 1552980358
         * @since 0.1
         **/
        var imageButtonOpts: ImageButton = view.findViewById(R.id.imageButtonOpts)
    }
    
}