package projekt.cloud.piece.music.player.ui.play.playDetail.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerPlayDetailBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, list: List<DetailItem>) {

    private class RecyclerViewHolder(val binding: LayoutRecyclerPlayDetailBinding): ViewHolder(binding.root) {
        fun setItem(item: DetailItem) {
            binding.content = item.content
            binding.title = item.title
            binding.linearLayout.setOnClickListener { item.onClick?.let { it() } }
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_play_detail, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            holder.setItem(itemList[position])
        }

        override fun getItemCount() = itemList.size

    }

    private val adapter = RecyclerViewAdapter()

    var itemList = list
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }

    init {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyUpdate() = adapter.notifyDataSetChanged()

}