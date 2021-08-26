package sakuraba.saki.player.music.ui.home.util

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val image: ImageView = view.findViewById(R.id.image_view)
    val title: TextView = view.findViewById(R.id.text_view_title)
    val summary: TextView = view.findViewById(R.id.text_view_summary)
    val background: RelativeLayout = view.findViewById(R.id.relative_layout_root)
}

class RecyclerViewAdapter(private val selection: (pos: Int) -> Unit): RecyclerView.Adapter<ViewHolder>() {
    
    private var audioInfoList: List<AudioInfo>? = null
    private var bitmaps: Map<Long, Bitmap?>? = null
    
    fun setAudioInfoList(audioInfoList: List<AudioInfo>) {
        this.audioInfoList = audioInfoList
        notifyDataSetChanged()
    }
    
    fun setBitmaps(bitmapList: Map<Long, Bitmap?>) {
        this.bitmaps = bitmapList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_home_recycler_view, parent, false))
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioInfoList = audioInfoList
        val bitmaps = bitmaps
        if (audioInfoList != null) {
            val audioInfo = audioInfoList[position]
            audioInfo.apply {
                holder.title.text = audioTitle
                @Suppress("SetTextI18n")
                holder.summary.text = "$audioArtist - $audioAlbum"
                // holder.image.setImageBitmap(getBitmap(holder.image.context))
                if (bitmaps != null) {
                    val bitmap = bitmaps[audioAlbumId]
                    if (bitmap != null) {
                        holder.image.setImageBitmap(bitmap)
                    }
                }
            }
        }
        holder.background.setOnClickListener { selection(position) }
    }
    
    override fun getItemCount() = audioInfoList?.size ?: 0
    
}