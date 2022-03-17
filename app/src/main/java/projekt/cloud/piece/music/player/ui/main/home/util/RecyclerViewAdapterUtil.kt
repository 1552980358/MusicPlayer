package projekt.cloud.piece.music.player.ui.main.home.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
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
                              private val defaultArtBitmap: Bitmap,
                              private val onClick: (Int) -> Unit,
                              private val onOptionClick: (Int) -> Unit) {

    private class RecyclerViewHolder(val binding: LayoutRecyclerHomeBinding): ViewHolder(binding.root) {
        fun setAudioItem(audioItem: AudioItem) {
            binding.audioItem = audioItem
        }
        fun setImageBitmap(imageBitmap: Bitmap) {
            binding.imageDrawable = BitmapDrawable(binding.root.context.resources, imageBitmap)
        }
        fun setOnClickListener(position: Int, onClick: (Int) -> Unit, onOptionClick: (Int) -> Unit) {
            binding.root.setOnClickListener {
                onClick(position)
            }
            binding.root.setOnLongClickListener {
                onOptionClick(position)
                true
            }
            @Suppress("ClickableViewAccessibility")
            binding.imageViewMore.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    ACTION_DOWN -> {
                        (binding.root.background as RippleDrawable).setHotspot(
                            motionEvent.x + binding.imageViewMore.x,
                            motionEvent.y + binding.imageViewMore.y
                        )
                        binding.root.isPressed = true
                    }
                    ACTION_UP -> {
                        binding.root.isPressed = false
                        if (motionEvent.y in (0F .. binding.imageViewMore.bottom.toFloat())) {
                            onOptionClick(position)
                        }
                    }
                }
                true
            }
        }
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_recycler_home, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioList[position].apply {
                holder.setAudioItem(this)
                holder.setImageBitmap(audioArtMap[id] ?: albumArtMap[album] ?: defaultArtBitmap)
                holder.setOnClickListener(position, onClick, onOptionClick)
            }
        }

        override fun getItemCount() = audioList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}