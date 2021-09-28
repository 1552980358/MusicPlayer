package sakuraba.saki.player.music.ui.album.util

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.MediaAlbum

class RecyclerViewAdapter(
    private val mediaAlbumList: List<MediaAlbum>,
    private val listener: (imageView: ImageView, textView: TextView, mediaAlbum: MediaAlbum) -> Unit
): Adapter<AlbumViewHolder>() {
    
    private var bitmapMap: Map<Long, Bitmap>? = null
    
    fun updateBitmapMap(bitmapMap: Map<Long, Bitmap>? = null) {
        if (bitmapMap != null) {
            this.bitmapMap = bitmapMap
        }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = AlbumViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.content_album, parent, false))
    
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.textView.text = mediaAlbumList[position].title
        holder.linearLayout.setOnClickListener {
            listener(holder.imageView, holder.textView, mediaAlbumList[position])
        }
        holder.imageView.transitionName = "${mediaAlbumList[position].albumId}_image"
        holder.textView.transitionName = "${mediaAlbumList[position].albumId}_text"
        val bitmapList = bitmapMap
        val bitmap: Bitmap?
        if (bitmapList != null) {
            bitmap = bitmapList[mediaAlbumList[position].albumId]
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            }
        }
    }
    
    override fun getItemCount() = mediaAlbumList.size
    
}

class AlbumViewHolder(view: View): ViewHolder(view) {
    
    val imageView: ImageView = view.findViewById(R.id.image_view)
    
    val textView: TextView = view.findViewById(R.id.text_view)
    
    val linearLayout: LinearLayout = view.findViewById(R.id.linear_layout)
    
}