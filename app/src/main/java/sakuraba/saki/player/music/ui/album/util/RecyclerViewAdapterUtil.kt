package sakuraba.saki.player.music.ui.album.util

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.util.MediaAlbum

class RecyclerViewAdapterUtil(data: MainActivityInterface, listener: (imageView: ImageView, textView: TextView, mediaAlbum: MediaAlbum) -> Unit) {
    
    private class AlbumViewHolder(view: View): RecyclerView.ViewHolder(view) {
        
        val imageView: ImageView = view.findViewById(R.id.image_view)
        
        val textView: TextView = view.findViewById(R.id.text_view)
        
        val linearLayout: LinearLayout = view.findViewById(R.id.linear_layout)
        
    }
    
    private class RecyclerViewAdapter(
        val mediaAlbumList: List<MediaAlbum>,
        val bitmapMap: Map<Long, Bitmap?>,
        private val listener: (imageView: ImageView, textView: TextView, mediaAlbum: MediaAlbum) -> Unit
    ): RecyclerView.Adapter<AlbumViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = AlbumViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.content_album, parent, false))
        
        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            holder.textView.text = mediaAlbumList[position].title
            holder.linearLayout.setOnClickListener {
                listener(holder.imageView, holder.textView, mediaAlbumList[position])
            }
            holder.imageView.transitionName = "${mediaAlbumList[position].albumId}_image"
            holder.textView.transitionName = "${mediaAlbumList[position].albumId}_text"
            val bitmap = bitmapMap[mediaAlbumList[position].albumId]
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            }
        }
        
        override fun getItemCount() = mediaAlbumList.size
        
    }
    
    private val adapter = RecyclerViewAdapter(data.albumList, data.bitmapMap, listener)
    
    fun setAdapterToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()
    
    val mediaAlbumList get() = adapter.mediaAlbumList as ArrayList
    
    val bitmapMap get() = adapter.bitmapMap as MutableMap
    
}