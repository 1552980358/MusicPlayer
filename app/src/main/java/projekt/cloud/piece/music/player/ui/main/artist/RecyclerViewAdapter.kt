package projekt.cloud.piece.music.player.ui.main.artist

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerArtistBinding
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_SMALL
import projekt.cloud.piece.music.player.util.ArtUtil.TYPE_ARTIST
import projekt.cloud.piece.music.player.util.ArtUtil.fileOf
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class RecyclerViewAdapter(recyclerView: RecyclerView, private val onClick: (Artist) -> Unit) {
    
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerArtistBinding)
        : RecyclerView.ViewHolder(binding.root), OnClickListener {
        
        @Volatile
        private var job: Job? = null
        
        fun bind(artist: Artist) {
            job?.cancel()
            job = io {
                val artistFile = binding.root.context.fileOf(TYPE_ARTIST, artist.id, SUFFIX_SMALL)
                when {
                    artistFile.exists() -> artistFile.inputStream().use { BitmapFactory.decodeStream(it) }.let { bitmap ->
                        ui { binding.appCompatImageView.setImageBitmap(bitmap) }
                    }
                    else -> ui { binding.appCompatImageView.setImageResource(R.drawable.ic_round_artist_24) }
                }
                job = null
            }
            binding.artist = artist
            binding.root.setOnClickListener(this)
        }
    
        override fun onClick(v: View?) {
            binding.artist?.let { onClick.invoke(it) }
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerArtistBinding.inflate(LayoutInflater.from(parent.context))
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            artistList?.get(position)?.let {
                holder.bind(it)
            }
        }
    
        override fun getItemCount() = artistList?.size ?: 0
        
    }
    
    private val adapter = RecyclerViewAdapter()
    
    var artistList: ArrayList<Artist>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    init {
        recyclerView.adapter = adapter
    }
    
}