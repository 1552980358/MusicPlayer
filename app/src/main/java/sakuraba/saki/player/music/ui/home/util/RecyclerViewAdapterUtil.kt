package sakuraba.saki.player.music.ui.home.util

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(selection: (pos: Int) -> Unit) {
    
    private class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_view)
        val title: TextView = view.findViewById(R.id.text_view_title)
        val summary: TextView = view.findViewById(R.id.text_view_summary)
        val background: RelativeLayout = view.findViewById(R.id.relative_layout_root)
    }
    
    private class RecyclerViewAdapter(val audioInfoList: List<AudioInfo>, val bitmapMap: MutableMap<Long, Bitmap?>, private val selection: (pos: Int) -> Unit): RecyclerView.Adapter<ViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_home_recycler_view, parent, false))
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindHolder {
            val audioInfo = audioInfoList[position]
            audioInfo.apply {
                title.text = audioTitle
                @Suppress("SetTextI18n")
                summary.text = "$audioArtist - $audioAlbum"
                // holder.image.setImageBitmap(getBitmap(holder.image.context))
                image.setImageBitmap(bitmapMap[audioAlbumId] ?: holder.background.context.getDrawable(R.drawable.ic_music)?.toBitmap())
            }
            background.setOnClickListener { selection(position) }
        }
        
        override fun getItemCount() = audioInfoList.size
        
    }
    
    private val adapter = RecyclerViewAdapter(arrayListOf(), mutableMapOf(), selection)
    
    fun setAdapterToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }
    
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()
    
    val audioInfoList get() = adapter.audioInfoList as ArrayList
    val bitmapMap get() = adapter.bitmapMap
    
}