package projekt.cloud.piece.music.player.ui.main.album

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAlbumBinding
import projekt.cloud.piece.music.player.item.Album
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_SMALL
import projekt.cloud.piece.music.player.util.ArtUtil.TYPE_ALBUM
import projekt.cloud.piece.music.player.util.ArtUtil.fileOf
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class RecyclerViewAdapter(recyclerView: RecyclerView, private val onClick: (Album) -> Unit) {
    
    private inner class RecyclerViewHolder private constructor(private val binding: LayoutRecyclerAlbumBinding)
        : ViewHolder(binding.root), View.OnClickListener {
        constructor(layoutInflater: LayoutInflater): this(LayoutRecyclerAlbumBinding.inflate(layoutInflater))
        
        @Volatile
        private var job: Job? = null
        
        fun bind(album: Album) {
            binding.album = album
            job?.cancel()
            job = io {
                val albumFile = binding.root.context.fileOf(TYPE_ALBUM, album.id, SUFFIX_SMALL)
                when {
                    albumFile.exists() -> albumFile.inputStream().use {  BitmapFactory.decodeStream(it) }.let { bitmap ->
                        ui { binding.appCompatImageView.setImageBitmap(bitmap) }
                    }
                    else -> ui { binding.appCompatImageView.setImageResource(R.drawable.ic_round_album_24) }
                }
                job = null
            }
        }
    
        override fun onClick(view: View?) {
            binding.album?.let(onClick)
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(LayoutInflater.from(parent.context))
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            albumList?.get(position)?.let { holder.bind(it) }
        }
    
        override fun getItemCount() = albumList?.size ?: 0
    
    }
    
    private val adapter = RecyclerViewAdapter()
    
    var albumList: ArrayList<Album>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    init {
        recyclerView.adapter = adapter
    }
    
}