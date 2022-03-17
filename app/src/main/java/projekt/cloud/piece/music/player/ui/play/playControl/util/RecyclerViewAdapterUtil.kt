package projekt.cloud.piece.music.player.ui.play.playControl.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlayControlBinding

class RecyclerViewAdapterUtil(private val recyclerView: RecyclerView, private val onClick: (Int) -> Unit) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerPlayControlBinding): ViewHolder(binding.root) {
        fun setIndex(index: Int) {
            binding.position = index.plus(1).toString()
        }
        fun setAudioItem(audioItem: AudioItem) {
            binding.audioItem = audioItem
        }
        fun setOnClick(position: Int, onClick: (Int) -> Unit) {
            binding.root.setOnClickListener { onClick(position) }
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_play_control, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            playlist?.let {
                holder.setIndex(position)
                holder.setAudioItem(it[position])
            }
            holder.setOnClick(position, onClick)
        }

        override fun getItemCount() = playlist?.size ?: 0

    }

    var playlist: List<AudioItem>? = null
        set(value) {
            field.let {
                if (it == null) {
                    field = value
                    @Suppress("NotifyDataSetChanged")
                    return adapter.notifyDataSetChanged()
                }
                value?.let { value -> comparePlaylist(it, value) }
            }
            field = value
        }

    private val adapter = RecyclerViewAdapter()

    private var hasShuffled = false

    init {
        recyclerView.adapter = adapter
    }

    private fun comparePlaylist(originList: List<AudioItem>, newList: List<AudioItem>) {
        if (hasShuffled) {
            hasShuffled = false
            @Suppress("NotifyDataSetChanged")
            return adapter.notifyItemRangeChanged(0 ,newList.size)
        }
        val originListLastItem = originList.last()
        val newListLastItem = newList.last()
        if (originListLastItem.id != newListLastItem.id) {
            if (originList.first().id == newListLastItem.id) {
                adapter.notifyItemRemoved(0)
                adapter.notifyItemInserted(newList.lastIndex)
                return adapter.notifyItemRangeChanged(0, newList.size)
            }
            if (originList.size > 1) {
                if (originListLastItem.id == newList.first().id) {
                    adapter.apply {
                        notifyItemRemoved(originList.lastIndex)
                        notifyItemInserted(0)
                        notifyItemRangeChanged(0, newList.size)
                    }
                    return recyclerView.scrollToPosition(0)
                }
                adapter.notifyItemRangeRemoved(0, originList.indexOfFirst { audioItem -> audioItem.id == newListLastItem.id })
                adapter.notifyItemRangeChanged(0, newList.size)
            }
        }
    }

}