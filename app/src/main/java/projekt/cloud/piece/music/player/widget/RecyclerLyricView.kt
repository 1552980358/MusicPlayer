package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Color.WHITE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.ViewRecyclerLyricBinding
import projekt.cloud.piece.music.player.util.Lyric
import projekt.cloud.piece.music.player.util.LyricItem

class RecyclerLyricView(context: Context, attributeSet: AttributeSet?): RecyclerView(context, attributeSet) {
    
    companion object {
    
        @JvmStatic
        @BindingAdapter("app:lyric")
        fun RecyclerLyricView.setLyric(lyric: Lyric?) {
            this.lyric = lyric
        }
        
        @JvmStatic
        @BindingAdapter("app:primaryColor")
        fun RecyclerLyricView.setPrimaryColor(colorInt: Int?) {
            if (colorInt != null) {
                primaryColor = colorInt
            }
        }
    
        @JvmStatic
        @BindingAdapter("app:secondaryColor")
        fun RecyclerLyricView.setSecondaryColor(colorInt: Int?) {
            if (colorInt != null) {
                secondaryColor = colorInt
            }
        }
        
        @JvmStatic
        @BindingAdapter("app:progress")
        fun RecyclerLyricView.setProgress(progress: Long?) {
            if (progress != null) {
                updateProgress(progress)
            }
        }
        
    }

    private class RecyclerViewHolder(private val binding: ViewRecyclerLyricBinding): ViewHolder(binding.root) {
        fun bindView(lyricItem: LyricItem, @ColorInt textColor: Int) {
            binding.textColor = textColor
            binding.lyric = lyricItem.toString()
        }
    }
    
    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_recycler_lyric, parent, false))
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            lyric?.let {
                holder.bindView(it[position], when (currentPosition) {
                    position -> primaryColor
                    previousPosition -> secondaryColor  // Remove color
                    else -> secondaryColor
                })
            }
        }
    
        override fun getItemCount() = lyric?.size ?: 0
    
    }
    
    private val adapter = RecyclerViewAdapter()
    
    var lyric: Lyric? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            currentPosition = -1
            previousPosition = -1
        }
    
    var primaryColor = WHITE
        set(value) {
            field = value
            lyric?.size?.let { adapter.notifyItemChanged(0, it) }
        }
    
    var secondaryColor = WHITE
        set(value) {
            field = value
            lyric?.size?.let { adapter.notifyItemChanged(0, it) }
        }
    
    private var previousPosition = -1
    private var currentPosition = -1
        set(value) {
            previousPosition = field
            field = value
            if (previousPosition != -1) {
                adapter.notifyItemChanged(previousPosition)
            }
            if (value != -1) {
                adapter.notifyItemChanged(value)
            }
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(value, height / 2)
        }
    
    fun updateProgress(progress: Long) {
        lyric?.indexOf(progress)?.also { currentPosition = it }
    }
    
    init {
        setAdapter(adapter)
    }

}