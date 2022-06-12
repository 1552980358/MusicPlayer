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
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerArtistBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ioContext
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.FLAG_SMALL
import projekt.cloud.piece.music.player.util.ImageUtil.readArtistArt

/**
 * [RecyclerViewAdapter]
 *
 * Variables:
 * [artistList]
 * [adapter]
 * [onClick]
 *
 * Methods:
 * [setOnClick]
 *
 **/
class RecyclerViewAdapter(recyclerView: RecyclerView) {
    
    /**
     * [RecyclerViewHolder]
     * inherit to [ViewHolder]
     * implement to [OnClickListener]
     *
     * Variable:
     * [job]
     *
     * Methods:
     * [onBind]
     * [onClick]
     **/
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerArtistBinding): ViewHolder(binding.root), OnClickListener {
        private var job: Job? = null
        
        fun onBind(artistItem: ArtistItem) {
            binding.artistItem = artistItem
            binding.root.setOnClickListener(this)
            job = ui {
                when (val bitmap = ioContext { binding.root.context.readArtistArt(artistItem.id, FLAG_SMALL) }) {
                    null -> binding.appCompatImageView.setImageResource(R.drawable.ic_round_artist_circle_24)
                    else -> binding.appCompatImageView.setImageBitmap(bitmap)
                }
            }
        }
        
        override fun onClick(v: View?) {
            binding.artistItem?.let { onClick?.invoke(it) }
        }
    }
    
    /**
     * [RecyclerViewAdapter]
     * inherit to [RecyclerView.Adapter]
     *
     * Methods:
     * [onCreateViewHolder]
     * [onBindViewHolder]
     * [getItemCount]
     **/
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