package projekt.cloud.piece.music.player.ui.main.artist.util

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
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerArtistBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val artistArtMap: Map<String, Bitmap>,
                              private val defaultArt: Bitmap,
                              private val onClick: (View, ArtistItem) -> Unit) {

    constructor(recyclerView: RecyclerView, artistArtMap: Map<String, Bitmap>, defaultCover: Bitmap,
                artistList: List<ArtistItem>, onClick: (View, ArtistItem) -> Unit) : this(recyclerView, artistArtMap, defaultCover, onClick) {
        this.artistList = artistList
    }

    private class RecyclerViewHolder(private val binding: LayoutRecyclerArtistBinding): ViewHolder(binding.root) {
        fun setTitle(title: String) {
            binding.title = title
        }
        fun setOnClickListener(artistItem: ArtistItem, onClick: (View, ArtistItem) -> Unit) {
            binding.root.transitionName = artistItem.id
            binding.root.setOnClickListener { onClick(it, artistItem) }
        }
        fun setImage(image: Bitmap) {
            binding.image = BitmapDrawable(binding.root.context.resources, image)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_artist, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            artistList?.get(position)?.let {
                holder.setTitle(it.title)
                holder.setOnClickListener(it, onClick)
                holder.setImage(artistArtMap[it.id] ?: defaultArt)
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

    init {
        recyclerView.adapter = adapter
    }

}