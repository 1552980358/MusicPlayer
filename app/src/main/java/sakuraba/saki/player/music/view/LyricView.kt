package sakuraba.saki.player.music.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import lib.github1552980358.ktExtension.android.view.getDimension
import lib.github1552980358.ktExtension.android.view.getString
import sakuraba.saki.player.music.R

class LyricView: View {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private val primaryTextHeight: Float
    private val secondaryTextHeight: Float
    val lyricList = arrayListOf<String>()
    val timeList = arrayListOf<Long>()
    private var currentIndex = -1
    @Suppress("JoinDeclarationAndAssignment")
    private val emptyListStr: String
    private val loadingStr: String

    private val primaryPaint = Paint()
    private val secondaryPaint = Paint()
    private val lineList = arrayListOf<String>()

    var isLoading = false
        set(value) {
            field = value
            currentIndex = -1
            postInvalidate()
        }

    init {
        emptyListStr = getString(R.string.lyric_empty_list)
        loadingStr = getString(R.string.lyric_loading)
        primaryPaint.apply {
            isAntiAlias = true
            textSize = getDimension(R.dimen.view_lyric_primary_text_size)
            fontMetrics.apply {
                primaryTextHeight = descent - ascent
            }
        }
        secondaryPaint.apply {
            isAntiAlias = true
            textSize = getDimension(R.dimen.view_lyric_secondary_text_size)
            fontMetrics.apply {
                secondaryTextHeight = descent - ascent
            }
        }
    }

    fun updatePosition(position: Long) {
        if (!isLoading) {
            currentIndex = findPosition(position)
            invalidate()
        }
    }

    private fun findPosition(position: Long): Int {
        if (timeList.isEmpty()) {
            return -1
        }
        if (position == 0L) {
            return 0
        }
        for (i in 1 until timeList.lastIndex) {
            if (position >= timeList[i - 1] && position < timeList[i]) {
                return i - 1
            }
        }
        if (position < timeList.first()) {
            return 0
        }
        return timeList.lastIndex
    }

    fun updatePrimaryColor(@ColorInt newColor: Int) {
        primaryPaint.color = newColor
        invalidate()
    }

    fun updateSecondaryColor(@ColorInt newColor: Int) {
        secondaryPaint.color = newColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        when {
            isLoading -> {
                canvas.drawText(loadingStr, (width - primaryPaint.measureText(emptyListStr)) / 2F, (height - primaryTextHeight) / 2F, primaryPaint)
                return
            }
            lyricList.isEmpty() || timeList.isEmpty() || currentIndex == -1 -> {
                canvas.drawText(emptyListStr, (width - primaryPaint.measureText(emptyListStr)) / 2F, (height - primaryTextHeight) / 2F, primaryPaint)
                return
            }
        }

        // Draw current index first
        var textHeight = calculateLyricHeight(lyricList[currentIndex].trim(), lineList, primaryTextHeight, primaryPaint)
        val startY =  (height - textHeight) / 2
        var offsetY = startY
        canvas.drawLyric(lineList, offsetY, primaryTextHeight, primaryPaint)
        var i: Int
        if (currentIndex < lyricList.lastIndex) {
            i = currentIndex
            offsetY += textHeight + secondaryTextHeight
            while (++i < lyricList.size && offsetY < height) {
                textHeight = calculateLyricHeight(lyricList[i].trim(), lineList, secondaryTextHeight, secondaryPaint)
                canvas.drawLyric(lineList, offsetY, secondaryTextHeight, secondaryPaint)
                offsetY += textHeight + secondaryTextHeight
            }
        }

        if (currentIndex > 0) {
            offsetY = startY
            i = currentIndex
            while (--i > -1 && offsetY > -1) {
                offsetY -= calculateLyricHeight(lyricList[i].trim(), lineList, secondaryTextHeight, secondaryPaint) + secondaryTextHeight
                canvas.drawLyric(lineList, offsetY, secondaryTextHeight, secondaryPaint)
            }
        }
    }

    private fun calculateOffset(lyric: String, offset: Int, paint: Paint): Int {
        var end = offset
        @Suppress("ControlFlowWithEmptyBody")
        while (++end < lyric.length && paint.measureText(lyric.substring(offset, end)) < width);
        return end
    }

    private fun calculateLyricLines(lyric: String, arrayList: ArrayList<String>, paint: Paint): Int {
        arrayList.clear()
        var offset = 0
        var end: Int
        val lastIndex = lyric.lastIndex
        while (offset < lastIndex) {
            end = calculateOffset(lyric, offset, paint)
            arrayList.add(lyric.substring(offset, end))
            offset = end
        }
        return arrayList.size
    }

    private fun calculateLyricHeight(lyric: String, arrayList: ArrayList<String>, textHeight: Float, paint: Paint) =
        calculateLyricLines(lyric, arrayList, paint) * textHeight

    private fun Canvas.drawLyric(arrayList: ArrayList<String>, offsetY: Float, textHeight: Float, paint: Paint) {
        arrayList.forEachIndexed { index, line ->
            drawText(line, (width - paint.measureText(line)) / 2, offsetY + index * textHeight, paint)
        }
    }

}