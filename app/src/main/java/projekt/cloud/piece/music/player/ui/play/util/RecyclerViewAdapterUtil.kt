package projekt.cloud.piece.music.player.ui.play.util

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlayBinding

class RecyclerViewAdapterUtil(private val recyclerView: RecyclerView,
                              list: List<AudioItem>,
                              private val clickListener: (Int) -> Unit) {
    
    private inner class RecyclerViewHolder(private val binding: LayoutRecyclerPlayBinding): ViewHolder(binding.root) {
        
        fun bind(index: Int, audioItem: AudioItem) {
            binding.index = "${index + 1}"
            binding.audioItem = audioItem
            binding.relativeLayout.setOnClickListener { clickListener(audioItem.index) }
        }
        
    }
    
    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(from(parent.context), R.layout.layout_recycler_play, parent, false))
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) =
            holder.bind(position, audioList[position])
    
        override fun getItemCount() = audioList.size
        
    }
    
    private val adapter = RecyclerViewAdapter()
    var audioList = list
        set(value) {
            compareAndNotifyListUpdate(field, value)
            field = value
        }
    
    var hasShuffledUpdated = false
    
    init {
        recyclerView.adapter = adapter
    }
    
    private fun compareAndNotifyListUpdate(originList: List<AudioItem>, newList: List<AudioItem>) {
        if (hasShuffledUpdated) {
            hasShuffledUpdated = false
            @Suppress("NotifyDataSetChanged")
            adapter.notifyItemRangeRemoved(0, audioList.size)
            return adapter.notifyItemRangeInserted(0, audioList.size)
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