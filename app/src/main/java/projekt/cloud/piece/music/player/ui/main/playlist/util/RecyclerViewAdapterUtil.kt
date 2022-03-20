package projekt.cloud.piece.music.player.ui.main.playlist.util

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
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlaylistBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val playlistMap: Map<String, Bitmap>,
                              private val defaultPlaylistArt: Bitmap,
                              private val onClick: (View, PlaylistItem) -> Unit) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerPlaylistBinding): ViewHolder(binding.root) {
        fun setTitle(title: String) {
            binding.title = title
        }
        fun setOnClick(playlistItem: PlaylistItem, onClick: (View, PlaylistItem) -> Unit) {
            binding.root.transitionName = playlistItem.id
            binding.root.setOnClickListener { onClick(it, playlistItem) }
        }
        fun setImage(bitmap: Bitmap) {
            binding.image = BitmapDrawable(binding.root.resources, bitmap)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_playlist, parent, false)
        )

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            playlistList?.get(position)?.let {
                holder.setTitle(it.title)
                holder.setOnClick(it, onClick)
                holder.setImage(playlistMap[it.id] ?: defaultPlaylistArt)
            }
        }

        override fun getItemCount() = playlistList?.size ?: 0

    }

    var playlistList: List<PlaylistItem>? = null
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