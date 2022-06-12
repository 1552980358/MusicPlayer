package projekt.cloud.piece.music.player.ui.main.album.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.audio.item.AlbumItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAlbumBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.ioContext
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.FLAG_SMALL
import projekt.cloud.piece.music.player.util.ImageUtil.readAlbumArt

/**
 * [RecyclerViewAdapter]
 *
 * Variables:
 * [albumList]
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
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerAlbumBinding): ViewHolder(binding.root), OnClickListener {
        private var job: Job? = null
        fun onBind(albumItem: AlbumItem) {
            binding.albumItem = albumItem
            binding.root.setOnClickListener(this)
            job?.cancel()
            job = ui {
                when (val bitmap = ioContext { binding.root.context.readAlbumArt(albumItem.id, FLAG_SMALL) }) {
                    null -> binding.appCompatImageView.setImageResource(R.drawable.ic_round_album_24)
                    else -> binding.appCompatImageView.setImageBitmap(bitmap)
                }
                job = null
            }
        }
        
        override fun onClick(v: View?) {
            binding.albumItem?.let { onClick?.invoke(it) }
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
            LayoutRecyclerAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            albumList?.get(position)?.let {
                holder.onBind(it)
            }
        }
    
        override fun getItemCount() = albumList?.size ?: 0
    }
    
    var albumList: List<AlbumItem>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    private var onClick: ((AlbumItem) -> Unit)? = null
    
    private val adapter = RecyclerViewAdapter()
    
    init {
        recyclerView.adapter = adapter
    }
    
    fun setOnClick(onClick: (AlbumItem) -> Unit) {
        this.onClick = onClick
    }
}