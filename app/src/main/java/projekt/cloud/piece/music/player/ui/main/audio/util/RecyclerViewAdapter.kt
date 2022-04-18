package projekt.cloud.piece.music.player.ui.main.audio.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAudioBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.readAlbumArtLarge

/**
 * Class [RecyclerViewAdapter]
 *
 * Final Variable:
 *  [adapter]
 *   @type [RecyclerViewAdapter]
 *   @default [RecyclerViewAdapter]
 *
 * Variable:
 *  [audioList]
 *   @type [List]<[AudioItem]>
 *   @default null
 *   @setter
 *
 * Inner class [RecyclerViewHolder]
 * Inner class [RecyclerViewAdapter]
 *
 **/
class RecyclerViewAdapter(recyclerView: RecyclerView) {

    /**
     * Inner class [RecyclerViewHolder], inherit to [ViewHolder]
     *
     * Variables:
     *  [job]
     *   @type [Job]?
     *   @default null
     *
     * Methods:
     *  [bindView]
     *
     **/
    private class RecyclerViewHolder(private val binding: LayoutRecyclerAudioBinding): ViewHolder(binding.root) {
        private var job: Job? = null
        fun bindView(audioItem: AudioItem) {
            job?.cancel()
            job = io {
                val imageBitmap = binding.root.context.readAlbumArtLarge(audioItem.album)
                ui { binding.appCompatImageViewAvatar.setImageBitmap(imageBitmap) }
            }
            binding.audio = audioItem
        }
    }

    /**
     * Inner class [RecyclerViewAdapter], inherit to [Adapter]<[RecyclerViewHolder]>
     *
     * Methods:
     *  [onCreateViewHolder]
     *  [onBindViewHolder]
     *  [getItemCount]
     **/
    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioList?.get(position)?.let { audioItem ->
                holder.bindView(audioItem)
            }
        }

        override fun getItemCount() = audioList?.size ?: 0

    }

    var audioList: List<AudioItem>? = null
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