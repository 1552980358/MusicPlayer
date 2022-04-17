package projekt.cloud.piece.music.player.ui.main.audio.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAudioBinding

class RecyclerViewAdapter(recyclerView: RecyclerView) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerAudioBinding): ViewHolder(binding.root) {
        fun bindView(audioItem: AudioItem) {
            binding.audio = audioItem
        }
    }

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