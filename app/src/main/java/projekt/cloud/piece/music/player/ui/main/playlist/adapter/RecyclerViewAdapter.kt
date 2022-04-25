package projekt.cloud.piece.music.player.ui.main.playlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.database.audio.refs.PlaylistWithCountRef.PlaylistWithCount
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlaylistBinding

class RecyclerViewAdapter(recyclerView: RecyclerView) {

    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerPlaylistBinding): ViewHolder(binding.root), OnClickListener {
        fun onBind(playlistWithCount: PlaylistWithCount) {
            binding.playlistWithCount = playlistWithCount
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            binding.playlistWithCount?.let { onClick?.invoke(it.playlistItem.id) }
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            playlistList?.get(position)
                ?.let { holder.onBind(it) }
        }

        override fun getItemCount() = playlistList?.size ?: 0
    }

    private val adapter = RecyclerViewAdapter()

    var playlistList: List<PlaylistWithCount>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }

    private var onClick: ((String) -> Unit)? = null

    init {
        recyclerView.adapter = adapter
    }

    fun setOnClick(onClick: (String) -> Unit) {
        this.onClick = onClick
    }

}