package projekt.cloud.piece.music.player.ui.main.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiContext
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerListBinding
import projekt.cloud.piece.music.player.item.AudioMetadata

class RecyclerViewAdapter(recyclerView: RecyclerView, private val onClick: (AudioMetadata) -> Unit) {
    
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerListBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        constructor(@UiContext context: Context): this(LayoutRecyclerListBinding.inflate(LayoutInflater.from(context)))
        
        fun bind(number: Int, audioMetadata: AudioMetadata) =
            bind(number.toString(), audioMetadata)
        
        fun bind(number: String, audioMetadata: AudioMetadata) {
            binding.audioMetadata = audioMetadata
            binding.number = number
            binding.root.setOnClickListener(this)
        }
    
        override fun onClick(v: View?) {
            binding.audioMetadata?.let(onClick)
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(parent.context)
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioMetadataList?.get(position)?.let { holder.bind(position + 1, it) }
        }
    
        override fun getItemCount() = audioMetadataList?.size ?: 0
        
    }
    
    var audioMetadataList: ArrayList<AudioMetadata>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    private val adapter = RecyclerViewAdapter()
    
    init {
        recyclerView.adapter = adapter
    }
    
}