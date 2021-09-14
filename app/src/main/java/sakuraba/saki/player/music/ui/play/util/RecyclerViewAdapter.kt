package sakuraba.saki.player.music.ui.play.util

import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.Constants.EXTRAS_INDEX

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var root: RelativeLayout = view.findViewById(R.id.relative_layout_root)
    val num: TextView = view.findViewById(R.id.text_view_num)
    val title: TextView = view.findViewById(R.id.text_view_title)
    val summary: TextView = view.findViewById(R.id.text_view_summary)
}

class RecyclerViewAdapter(private val listener: (pos: Int) -> Unit): RecyclerView.Adapter<ViewHolder>() {
    
    private var mediaItemList: List<MediaItem>? = null
    private var audioInfoList: List<AudioInfo>? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_play_recycler_view, parent, false))
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.root.setOnClickListener {
            val mediaItemList = mediaItemList
            val audioInfoList = audioInfoList
            when {
                mediaItemList != null -> listener(mediaItemList[position].description.extras!!.getInt(EXTRAS_INDEX))
                audioInfoList != null -> listener(audioInfoList[position].index)
            }
        }
        holder.num.text = position.plus(1).toString()
        val mediaItemList = mediaItemList
        val audioInfoList = audioInfoList
        when {
            mediaItemList != null -> updateWithMediaItem(holder, mediaItemList[position])
            audioInfoList != null -> updateWithAudioInfo(holder, audioInfoList[position])
        }
    }
    
    private fun updateWithMediaItem(holder: ViewHolder, mediaItem: MediaItem) {
        holder.title.text = mediaItem.description.title
        holder.summary.text = mediaItem.description.subtitle
    }
    
    private fun updateWithAudioInfo(holder: ViewHolder, audioInfo: AudioInfo) {
        holder.title.text = audioInfo.audioTitle
        holder.summary.text = audioInfo.audioArtist
    }
    
    override fun getItemCount() = mediaItemList?.size ?: audioInfoList?.size ?: 0
    
    fun updateMediaItemList(newList: List<MediaItem>) {
        audioInfoList = null
        mediaItemList = newList
        notifyDataSetChanged()
    }
    
    fun updateAudioInfoList(newList: List<AudioInfo>) {
        //audioInfoList = newList
        //mediaItemList = null
        //notifyDataSetChanged()
        
        val audioInfoList = audioInfoList
        val mediaItemList = mediaItemList
    
        this.audioInfoList = newList
        this.mediaItemList = null
        
        when {
            audioInfoList == null && mediaItemList == null -> notifyDataSetChanged()
            audioInfoList == null && mediaItemList != null -> {
                @Suppress("DuplicatedCode")
                if (mediaItemList.first().mediaId == newList.last().audioId) {
                    notifyItemRemoved(0)
                    notifyItemRangeChanged(0, newList.size)
                    return
                }
                notifyItemRangeRemoved(0, mediaItemList.indexOfFirst { mediaItem -> mediaItem.mediaId == newList.last().audioId })
                notifyItemRangeChanged(0, newList.size)
            }
            audioInfoList != null && mediaItemList == null -> {
                if (audioInfoList.first().audioId == newList.last().audioId) {
                    notifyItemRemoved(0)
                    notifyItemRangeChanged(0, newList.size)
                    return
                }
                notifyItemRangeRemoved(0, audioInfoList.indexOfFirst { audioInfo -> audioInfo.audioId == newList.last().audioId })
                notifyItemRangeChanged(0, newList.size)
            }
        }
    }
    
}