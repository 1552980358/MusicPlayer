package projekt.cloud.piece.music.player.ui.play.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlayerBinding
import projekt.cloud.piece.music.player.item.AudioMetadata

class PlayingQueueAdapter(recyclerView: RecyclerView, private val clickListener: (AudioMetadata) -> Unit) {

    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerPlayerBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        
        fun bindViewHolder(audioMetadata: AudioMetadata, isPlaying: Boolean) {
            binding.audioMetadata = audioMetadata
            binding.isPlaying = isPlaying
            binding.root.setOnClickListener(this)
        }
    
        override fun onClick(v: View?) {
            binding.audioMetadata?.let { clickListener.invoke(it) }
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioMetadataList?.get(position)?.let { holder.bindViewHolder(it, position == playingPosition) }
        }
    
        override fun getItemCount() = audioMetadataList?.size ?: 0
        
    }
    
    private var audioMetadataList: List<AudioMetadata>? = null
    
    private var adapter = RecyclerViewAdapter()
    
    private var playingPosition = 0
    
    init {
        recyclerView.adapter = adapter
    }
    
    fun updateAudioMetadataList(audioMetadataList: List<AudioMetadata>) {
        this.audioMetadataList = audioMetadataList
        notifyUpdateDataSet()
    }
    
    fun updatePlayingPosition(position: Int) {
        val last = playingPosition
        playingPosition = position
        adapter.notifyItemChanged(last)
        adapter.notifyItemChanged(playingPosition)
    }
    
    @Suppress("NotifyDataSetChanged")
    private fun notifyUpdateDataSet() = adapter.notifyDataSetChanged()

}