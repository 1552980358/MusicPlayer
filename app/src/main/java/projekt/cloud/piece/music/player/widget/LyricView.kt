package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.ContentViewLyricBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_INT
import projekt.cloud.piece.music.player.widget.lyric.Lyric
import projekt.cloud.piece.music.player.widget.lyric.LyricItem

class LyricView(context: Context, attributeSet: AttributeSet?): RecyclerView(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("textSize")
        fun LyricView.setTextSize(textSize: String) {
            this.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                textSize.toFloat(),
                resources.displayMetrics
            )
        }

        @JvmStatic
        @BindingAdapter("colorItem")
        fun LyricView.setColorItem(colorItem: ColorItem?) {
            this.colorItem = colorItem
        }

        @JvmStatic
        @BindingAdapter("lyric")
        fun LyricView.setLyric(lyric: Lyric?) {
            this.lyric = lyric
        }

        @JvmStatic
        @BindingAdapter("position")
        fun LyricView.setPosition(position: Long?) {
            position?.let { pos ->
                lyric?.indexOf(pos)?.let {
                    current = it
                }
            }
        }

    }

    private inner class LyricViewHolder(val binding: ContentViewLyricBinding): ViewHolder(binding.root), OnClickListener {
        private lateinit var lyricItem: LyricItem

        fun onBind(lyricItem: LyricItem) {
            this.lyricItem = lyricItem
            binding.lyric = lyricItem.toString()
            binding.root.setOnClickListener(this)
            binding.textSize = textSize
        }

        fun setTextStyle(textColor: Int, textStyle: Int) {
            binding.textColor = textColor
            binding.textStyle = textStyle
        }

        fun setTopPadding(padding: Int) =
            binding.relativeLayout.setPadding(0, padding, 0, textSize.toInt() / 2)

        fun setBottomPadding(padding: Int) =
            binding.relativeLayout.setPadding(0, textSize.toInt() / 2, 0, padding)

        fun setNoPadding() =
            binding.relativeLayout.setPadding(0, textSize.toInt() / 2, 0, textSize.toInt() / 2)

        override fun onClick(v: View?) {
        }
    }

    private inner class LyricAdapter: Adapter<LyricViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LyricViewHolder(
            ContentViewLyricBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBindViewHolder(holder: LyricViewHolder, position: Int) {
            lyric?.let { lyric ->
                holder.onBind(lyric[position])
                colorItem?.let {
                    when (position) {
                        current -> holder.setTextStyle(it.primary, BOLD)
                        else -> holder.setTextStyle(it.secondary, NORMAL)
                    }
                }

                when (position) {
                    0 -> holder.setTopPadding(measuredHeight / 2)
                    lyric.lastIndex -> holder.setBottomPadding(measuredHeight / 2)
                    else -> holder.setNoPadding()
                }
            }
        }

        override fun getItemCount() = lyric?.size ?: 0
    }

    private class CenterLinearlayoutManager(private val context: Context) : LinearLayoutManager(context) {

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: State?, position: Int) {
            CenterSmoothScroller(context).also {
                it.targetPosition = position
                startSmoothScroll(it)
            }
        }

        private class CenterSmoothScroller(context: Context): LinearSmoothScroller(context) {
            private companion object {
                const val TIME_SPENT_SCROLLING = ANIMATION_DURATION_INT
            }

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int) =
                (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)

            override fun calculateTimeForScrolling(dx: Int) = TIME_SPENT_SCROLLING
        }

    }

    private val adapter = LyricAdapter()

    private var lyric: Lyric? = null
        set(value) {
            field = value
            current = -1
            previous = -1
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }

    private var textSize = resources.getDimension(R.dimen.lyric_view_default_text_size)
        set(value) {
            field = value
            lyric?.let { adapter.notifyItemRangeChanged(0, it.size) }
        }

    private var colorItem: ColorItem? = null
        set(value) {
            if (field != value) {
                field = value
                lyric?.let { adapter.notifyItemRangeChanged(0, it.size) }
            }
        }

    private var previous = -1
    private var current = -1
        set(value) {
            if (field == value) {
                return
            }
            previous = field
            field = value
            if (previous > -1) {
                adapter.notifyItemChanged(previous)
            }
            when (value) {
                -1 -> smoothScrollToPosition(0)
                else -> {
                    adapter.notifyItemChanged(value)
                    smoothScrollToPosition(value)
                }
            }
        }

    init {
        setAdapter(adapter)
        layoutManager = CenterLinearlayoutManager(context)
    }

}