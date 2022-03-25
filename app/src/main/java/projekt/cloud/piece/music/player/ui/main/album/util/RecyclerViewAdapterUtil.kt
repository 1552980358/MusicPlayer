package projekt.cloud.piece.music.player.ui.main.album.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAlbumBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val albumArtMap: Map<String, Bitmap>,
                              private val defaultCover: Bitmap,
                              private val onClick: (AlbumItem) -> Unit) {

    constructor(recyclerView: RecyclerView, albumArtMap: Map<String, Bitmap>, defaultCover: Bitmap,
                albumList: List<AlbumItem>, onClick: (AlbumItem) -> Unit) : this(recyclerView, albumArtMap, defaultCover, onClick) {
        this.albumList = albumList
    }

    private class RecyclerViewHolder(private val binding: LayoutRecyclerAlbumBinding): ViewHolder(binding.root) {
        fun setTitle(title: String) {
            binding.title = title
        }
        fun setOnClickListener(albumItem: AlbumItem, onClick: (AlbumItem) -> Unit) {
            binding.root.setOnClickListener { onClick(albumItem) }
        }
        fun setImage(image: Bitmap) {
            binding.image = BitmapDrawable(binding.root.context.resources, image)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_album, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            albumList?.get(position)?.let {
                holder.setTitle(it.title)
                holder.setOnClickListener(it, onClick)
                holder.setImage(albumArtMap[it.id] ?: defaultCover)
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

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}