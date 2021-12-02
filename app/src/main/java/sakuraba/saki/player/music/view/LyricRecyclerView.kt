package sakuraba.saki.player.music.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lib.github1552980358.ktExtension.android.view.getColor
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.Lyric
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class LyricRecyclerView: RecyclerView {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private var lyric: Lyric? = null
    private var primaryColor: Int
    private var secondaryColor: Int
    private var strokeColor: Int

    private var lastLyric = -2
    private var currentLyric = -2
    private var isUpdating = false
    private val scrollingDistance: Int

    private class LyricViewHolder(view: View): ViewHolder(view) {
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textView: StrokeTextView = view.findViewById(R.id.text_view)
    }

    private inner class LyricAdapter: Adapter<LyricViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LyricViewHolder(LayoutInflater.from(context).inflate(R.layout.view_lyric_recycler, parent, false))
        override fun onBindViewHolder(holder: LyricViewHolder, position: Int) = holder.bindHolder {
            if (lyric == null || lyric!!.size < 0) {
                return@bindHolder
            }
            lyric?.let { textView.text = it.lyricList[position] }

            textView.setContentColor(
                    if (position == currentLyric) {
                        textView.setTypeface(null, BOLD)
                        primaryColor
                    } else {
                        textView.setTypeface(null, NORMAL)
                        secondaryColor
                    }
            )

            textView.setStrokeColor(strokeColor)

            when (position) {
                0 -> relativeLayout.setPadding(0, scrollingDistance, 0, 0)
                lyric?.lastIndex -> relativeLayout.setPadding(0, 0, 0, scrollingDistance)
                else -> relativeLayout.setPadding(0, 0, 0, 0)
            }
        }
        override fun getItemCount() = lyric?.size ?: 0
    }

    private val lyricAdapter get() = adapter as LyricAdapter

    init {
        primaryColor = getColor(R.color.black)
        secondaryColor = getColor(R.color.gray)
        strokeColor = BLACK
        layoutManager = LinearLayoutManager(context)
        adapter = LyricAdapter()
        // padding = resources.displayMetrics.widthPixels / 2
        TypedValue().apply {
            (context as Activity).theme.resolveAttribute(android.R.attr.actionBarSize, this, true)
            scrollingDistance = (resources.displayMetrics.widthPixels -
                    TypedValue.complexToDimensionPixelSize(data, resources.displayMetrics) -
                    getDimensionPixelSize(R.dimen.layout_lyric_lyric_view_margin_bottom)) / 2
        }
        overScrollMode = OVER_SCROLL_NEVER
    }

    @Synchronized
    fun updatePrimaryColor(@ColorInt newColor: Int) {
        primaryColor = newColor
        lyricAdapter.notifyItemRangeChanged(0, lyric?.size ?: 0)
    }

    @Synchronized
    fun updateSecondaryColor(@ColorInt newColor: Int) {
        secondaryColor = newColor
        lyricAdapter.notifyItemRangeChanged(0, lyric?.size ?: 0)
    }

    @Synchronized
    fun updateStrokeColor(@ColorInt newColor: Int) {
        strokeColor = newColor
        lyricAdapter.notifyItemRangeChanged(0, lyric?.size ?: 0)
    }

    @Synchronized
    fun updatePosition(mils: Long) {
        if (!isUpdating) {
            val index = findPosition(mils)
            if (index != currentLyric && index > -1) {
                tryOnly {
                    adapter?.notifyItemChanged(currentLyric)
                    adapter?.notifyItemChanged(index)
                    currentLyric = index
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentLyric, scrollingDistance)
                }
            }
        }
    }

    private fun findPosition(position: Long): Int {
        val timeList = lyric?.timeList ?: return -2
        if (timeList.isEmpty()) {
            return -2
        }
        if (position == 0L) {
            return 0
        }
        try {
            for (i in 1 until timeList.lastIndex) {
                if (position >= timeList[i - 1] && position < timeList[i]) {
                    return i - 1
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            return -2
        }
        if (position < timeList.first()) {
            return 0
        }
        return timeList.lastIndex
    }

    @Synchronized
    fun updateLyric(lyric: Lyric) {
        if (!isUpdating) {
            isUpdating = true
            this.lyric = lyric
            lastLyric = -2
            currentLyric = lastLyric
            @Suppress("NotifyDataSetChanged")
            tryOnly { lyricAdapter.notifyDataSetChanged() }
            isUpdating = false
        }
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
    }

}