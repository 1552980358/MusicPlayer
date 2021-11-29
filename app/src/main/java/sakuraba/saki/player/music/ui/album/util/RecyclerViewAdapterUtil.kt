package sakuraba.saki.player.music.ui.album.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lib.github1552980358.ktExtension.android.kotlin.toBitmap
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.util.MediaAlbum

class RecyclerViewAdapterUtil(data: MainActivityInterface, listener: (imageView: ImageView, textView: TextView, mediaAlbum: MediaAlbum) -> Unit) {
    
    private class AlbumViewHolder(view: View): RecyclerView.ViewHolder(view) {

        init {
            setIsRecyclable(false)
        }
        
        val imageView: ImageView = view.findViewById(R.id.image_view)
        
        val textView: TextView = view.findViewById(R.id.text_view)
        
        val linearLayout: LinearLayout = view.findViewById(R.id.linear_layout)
        
    }
    
    private class RecyclerViewAdapter(
        val mediaAlbumList: List<MediaAlbum>,
        val bitmapMap: Map<Long, ByteArray>,
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
            val byteArray = bitmapMap[mediaAlbumList[position].albumId]
            if (byteArray != null) {
                io {
                    val bitmap = byteArray.toBitmap()
                    ui { holder.imageView.setImageBitmap(bitmap) }
                }
            }
        }
        
        override fun getItemCount() = mediaAlbumList.size

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }
    
    private val adapter = RecyclerViewAdapter(data.albumList, data.byteArrayMap, listener)
    
    fun setAdapterToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()
    
    val mediaAlbumList get() = adapter.mediaAlbumList as ArrayList
    
    val bitmapMap get() = adapter.bitmapMap as MutableMap
    
}