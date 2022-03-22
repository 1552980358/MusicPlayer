package projekt.cloud.piece.music.player.ui.addToPlaylist.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAddToPlaylistBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val playlistArtMap: Map<String, Bitmap>,
                              private val defaultArt: Bitmap,
                              private val onClick: (PlaylistItem) -> Unit) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerAddToPlaylistBinding): ViewHolder(binding.root) {
        fun setOnClick(playlistItem: PlaylistItem, onClick: (PlaylistItem) -> Unit) {
            binding.title = playlistItem.title
            binding.root.setOnClickListener { onClick(playlistItem) }
        }
        fun setImage(imageBitmap: Bitmap) {
            binding.image = BitmapDrawable(binding.root.resources, imageBitmap)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.layout_recycler_add_to_playlist, parent, false
            )
        )

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            playlistList?.get(position)?.let {
                holder.setOnClick(it, onClick)
                holder.setImage(playlistArtMap[it.id] ?: defaultArt)
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

    fun notifyDataSetChanged() {
        @Suppress("NotifyDataSetChanged")
        adapter.notifyDataSetChanged()
    }

}