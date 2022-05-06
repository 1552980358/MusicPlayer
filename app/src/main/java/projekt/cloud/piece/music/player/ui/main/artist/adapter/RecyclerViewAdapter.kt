package projekt.cloud.piece.music.player.ui.main.artist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerArtistBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.FLAG_SMALL
import projekt.cloud.piece.music.player.util.ImageUtil.readArtistArt

class RecyclerViewAdapter(recyclerView: RecyclerView) {
    
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerArtistBinding): ViewHolder(binding.root), OnClickListener {
        private var job: Job? = null
        
        fun onBind(artistItem: ArtistItem) {
            binding.artistItem = artistItem
            binding.root.setOnClickListener(this)
            job = ui {
                binding.appCompatImageView.setImageBitmap(
                    withContext(io) {
                        binding.root.context.readArtistArt(artistItem.id, FLAG_SMALL)
                    }
                )
            }
        }
        
        override fun onClick(v: View?) {
            binding.artistItem?.let { onClick?.invoke(it) }
        }
    }
    
    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            artistList?.get(position)?.let { artistItem ->
                holder.onBind(artistItem)
            }
        }
    
        override fun getItemCount() = artistList?.size ?: 0
    }
    
    var artistList: List<ArtistItem>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    private val adapter = RecyclerViewAdapter()
    
    private var onClick: ((ArtistItem) -> Unit)? = null
    
    init {
        recyclerView.adapter = adapter
    }
    
    fun setOnClick(onClick: (ArtistItem) -> Unit) {
        this.onClick = onClick
    }
    
}