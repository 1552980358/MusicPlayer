package projekt.cloud.piece.music.player.ui.main.home.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerHomeBinding

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val audioList: List<AudioItem>,
                              private val audioArtMap: Map<String, Bitmap>,
                              private val albumArtMap: Map<String, Bitmap>,
                              private val defaultArtBitmap: Bitmap) {

    private class RecyclerViewHolder(val binding: LayoutRecyclerHomeBinding): ViewHolder(binding.root) {
        fun setAudioItem(audioItem: AudioItem) {
            binding.audioItem = audioItem
        }
        fun setImageBitmap(imageBitmap: Bitmap) {
            binding.imageDrawable = BitmapDrawable(binding.root.context.resources, imageBitmap)
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_home, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioList[position].apply {
                holder.setAudioItem(this)
                holder.setImageBitmap(audioArtMap[id] ?: albumArtMap[album] ?: defaultArtBitmap)
            }
        }

        override fun getItemCount() = audioList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}