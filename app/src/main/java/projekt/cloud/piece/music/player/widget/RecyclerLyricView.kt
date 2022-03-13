package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Color.WHITE
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.ViewRecyclerLyricBinding
import projekt.cloud.piece.music.player.util.Lyric

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
        fun setTextStyle(textStyle: Int) {
            binding.textStyle = textStyle
        }
        fun setTextColor(textColor: Int) {
            binding.textColor = textColor
        }
        fun setLyric(lyric: String) {
            binding.lyric = lyric
        }
        fun setTopPadding(padding: Int) {
            binding.relativeLayout.setPadding(0, padding, 0, 0)
        }
        fun setBottomPadding(padding: Int) {
            binding.relativeLayout.setPadding(0, 0, 0, padding)
        }
        fun setNoPadding() {
            binding.relativeLayout.setPadding(0, 0, 0, 0)
        }
    }
    
    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_recycler_lyric, parent, false))
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            lyric?.let {
                when (position) {
                    0 -> holder.setTopPadding(paddings)
                    it.size - 1 -> holder.setBottomPadding(paddings)
                    else -> holder.setNoPadding()
                }
                if (position == currentPosition) {
                    holder.setTextColor(primaryColor)
                    holder.setTextStyle(BOLD)
                } else {
                    holder.setTextColor(secondaryColor)
                    holder.setTextStyle(NORMAL)
                }
                holder.setLyric(it[position].toString())
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
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, resources.displayMetrics.heightPixels / 2)
        }
    
    var primaryColor = WHITE
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            lyric?.size?.let { adapter.notifyDataSetChanged() }
        }
    
    var secondaryColor = WHITE
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            lyric?.size?.let { adapter.notifyDataSetChanged() }
        }
    
    private var previousPosition = -1
    private var currentPosition = -1
        set(value) {
            if (field == value) {
                return
            }
            previousPosition = field
            field = value
            if (previousPosition > -1) {
                adapter.notifyItemChanged(previousPosition)
            }
            if (value > -1) {
                adapter.notifyItemChanged(value)
            }
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(value, resources.displayMetrics.heightPixels / 2)
        }
    
    fun updateProgress(progress: Long) = lyric?.indexOf(progress)?.let { currentPosition = it }
    
    var paddings = 0
    
    init {
        setAdapter(adapter)
    }

}