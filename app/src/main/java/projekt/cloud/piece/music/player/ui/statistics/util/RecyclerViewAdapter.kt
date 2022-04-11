package projekt.cloud.piece.music.player.ui.statistics.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.database.itemDao.PlayRecordItemDao.AudioItemCount
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerStatisticsBinding

class RecyclerViewAdapter(recyclerView: RecyclerView) {

    private class RecyclerViewHolder(private val binding: LayoutRecyclerStatisticsBinding): ViewHolder(binding.root) {
        fun onBind(audioItemCount: AudioItemCount) {
            binding.count = audioItemCount.count.toString()
            binding.title = audioItemCount.audio
            binding.artist = audioItemCount.artist
            binding.album = audioItemCount.album
        }
    }

    private inner class ViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(LayoutRecyclerStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            list?.get(position)?.let { holder.onBind(it) }
        }

        override fun getItemCount() = list?.size ?: 0

    }

    var list: List<AudioItemCount>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }

    private val adapter = ViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}