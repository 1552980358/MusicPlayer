package projekt.cloud.piece.music.player.ui.play.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlayerBinding
import projekt.cloud.piece.music.player.item.AudioMetadata

class PlayingQueueAdapter(recyclerView: RecyclerView) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerPlayerBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        
        fun bindViewHolder(audioMetadata: AudioMetadata) {
            binding.audioMetadata = audioMetadata
        }
    
        override fun onClick(v: View?) {
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioMetadataList?.get(position)?.let { holder.bindViewHolder(it) }
        }
    
        override fun getItemCount() = audioMetadataList?.size ?: 0
        
    }
    
    private var audioMetadataList: List<AudioMetadata>? = null
    
    private var adapter = RecyclerViewAdapter()
    
    init {
        recyclerView.adapter = adapter
    }
    
    fun updateAudioMetadataList(audioMetadataList: List<AudioMetadata>) {
        this.audioMetadataList = audioMetadataList
        notifyUpdateDataSet()
    }
    
    @Suppress("NotifyDataSetChanged")
    private fun notifyUpdateDataSet() = adapter.notifyDataSetChanged()

}