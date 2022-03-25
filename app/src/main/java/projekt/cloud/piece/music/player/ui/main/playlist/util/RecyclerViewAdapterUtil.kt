package projekt.cloud.piece.music.player.ui.main.playlist.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlaylistBinding
import projekt.cloud.piece.music.player.util.Constant.PLAYLIST_LIKES

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val playlistMap: Map<String, Bitmap>,
                              private val defaultPlaylistArt: Bitmap,
                              private val onClick: (PlaylistItem) -> Unit) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerPlaylistBinding): ViewHolder(binding.root) {
        fun setTitle(title: String) {
            binding.title = title
        }
        fun setTitle(@StringRes resId: Int) =
            setTitle(binding.root.resources.getString(resId))

        fun setOnClick(playlistItem: PlaylistItem, onClick: (PlaylistItem) -> Unit) {
            binding.root.transitionName = playlistItem.id
            binding.root.setOnClickListener { onClick(playlistItem) }
        }
        fun setImage(bitmap: Bitmap) {
            binding.image = BitmapDrawable(binding.root.resources, bitmap)
        }
        fun setImage(@DrawableRes resId: Int) {
            binding.image = getDrawable(binding.root.context.resources, resId, null)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_playlist, parent, false)
        )

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            playlistList?.get(position)?.let {
                when (it.id) {
                    PLAYLIST_LIKES -> {
                        holder.setTitle(R.string.playlist_likes)
                        holder.setImage(R.drawable.ic_heart_default)
                    }
                    else -> {
                        holder.setTitle(it.title)
                        holder.setImage(playlistMap[it.id] ?: defaultPlaylistArt)
                    }
                }
                holder.setOnClick(it, onClick)
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