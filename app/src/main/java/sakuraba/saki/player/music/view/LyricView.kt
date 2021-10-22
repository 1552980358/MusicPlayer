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
    private lateinit var lyricList: List<String>
    private lateinit var timeList: List<Long>
    private var currentIndex = -1
    @Suppress("JoinDeclarationAndAssignment")
    private val emptyListStr: String

    private val primaryPaint = Paint()
    private val secondaryPaint = Paint()

    init {
        emptyListStr = getString(R.string.lyric_empty_list)
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
        currentIndex = findPosition(position)
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

        if (currentIndex == -1) {
            canvas.drawText(emptyListStr, (width - primaryPaint.measureText(emptyListStr)) / 2F, (height - primaryTextHeight) / 2F, primaryPaint)
            return
        }

        // Draw current index first
        var posY = (height - primaryTextHeight) / 2F
        var lyricText = lyricList[currentIndex]
        canvas.drawText(lyricText, (width - primaryPaint.measureText(lyricText)) / 2F, posY, primaryPaint)

        // Draw upper
        var i: Int
        if (currentIndex != 0) {
            i = currentIndex
            while (--i > 0 && posY >= 0) {
                lyricText = lyricList[i]
                posY -= (primaryTextHeight * 2)
                canvas.drawText(lyricText, (width - primaryPaint.measureText(lyricText)) / 2F, posY, secondaryPaint)
            }
        }

        if (currentIndex != lyricList.lastIndex) {
            posY = (height - primaryTextHeight) / 2F
            i = currentIndex
            while (++i < lyricList.size && posY < height) {
                lyricText = lyricList[i]
                posY += (primaryTextHeight * 2)
                canvas.drawText(lyricText, (width - primaryPaint.measureText(lyricText)) / 2F, posY, secondaryPaint)
            }
        }

    }

}