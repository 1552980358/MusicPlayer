package projekt.cloud.piece.music.player.ui.audioList.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerAudioListBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, private val onClick: (Int) -> Unit) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerAudioListBinding): ViewHolder(binding.root) {
        fun setIndex(index: Int) {
            binding.position = index.plus(1).toString()
        }
        fun setOnClick(position: Int, onClick: (Int) -> Unit) {
            binding.root.setOnClickListener { onClick(position) }
        }
        fun setAudioItem(audioItem: AudioItem) {
            binding.audioItem = audioItem
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_audio_list, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            holder.setIndex(position)
            holder.setOnClick(position, onClick)
            audioList?.get(position)?.let { holder.setAudioItem(it) }
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