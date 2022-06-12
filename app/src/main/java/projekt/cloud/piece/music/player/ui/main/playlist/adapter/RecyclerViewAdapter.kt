package projekt.cloud.piece.music.player.ui.main.playlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.database.audio.extension.PlaylistWithAudio
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlaylistBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ioContext
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.FLAG_SMALL
import projekt.cloud.piece.music.player.util.ImageUtil.readPlaylistArt

/**
 * [RecyclerViewAdapter]
 **/
class RecyclerViewAdapter(recyclerView: RecyclerView) {

    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerPlaylistBinding): ViewHolder(binding.root), OnClickListener {

        private var job: Job? = null

        fun onBind(playlistWithCount: PlaylistWithAudio) {
            job?.cancel()
            job = ui {
                binding.appCompatImageViewAvatar.setImageBitmap(
                    ioContext {
                        binding.root.context.readPlaylistArt(playlistWithCount.playlistItem.id, FLAG_SMALL)
                    }
                )
                job = null
            }
            binding.playlistWithAudio = playlistWithCount
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            binding.playlistWithAudio?.let { onClick?.invoke(it) }
        }
    }
    
    /**
     * [RecyclerViewAdapter]
     * inherit to [RecyclerView.Adapter]
     *
     * Methods:
     * [onCreateViewHolder]
     * [onBindViewHolder]
     **/
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

    var playlistList: ArrayList<PlaylistWithAudio>? = null
        set(value) {
            field = value
            notifyUpdate()
        }

    private var onClick: ((PlaylistWithAudio) -> Unit)? = null

    init {
        recyclerView.adapter = adapter
    }

    fun setOnClick(onClick: (PlaylistWithAudio) -> Unit) {
        this.onClick = onClick
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyUpdate() =
        adapter.notifyDataSetChanged()

}