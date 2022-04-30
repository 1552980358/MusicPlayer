package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.ViewUtil.widthF

class TimeView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {

    companion object {
        private const val DEFAULT_STR = "00:00"
        private const val COLON = ":"

        @JvmStatic
        @BindingAdapter(value = ["time", "timeInt"], requireAll = false)
        fun TimeView.updateTime(duration: Long?, durationInt: Int?) {
            if (duration != null) {
                return setTimeLong(duration)
            }
            if (durationInt != null) {
                return setTimeInt(durationInt)
            }
        }

        @JvmStatic
        @BindingAdapter("textColor")
        fun TimeView.updateTextColor(color: Int) {
            setTextColor(color)
        }

    }

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val length: Float

    private val rect = Rect()

    private var time = 0L
        set(value) {
            field = value
            invalidate()
        }

    init {
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.TimeView, 0, 0).apply {
            paint.textSize = getDimension(
                R.styleable.TimeView_text_size,
                context.resources.getDimension(R.dimen.time_view_text_size)
            )
        }

        length = paint.measureText(COLON)
        paint.getTextBounds(DEFAULT_STR, 0, DEFAULT_STR.length, rect)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(rect.width(), EXACTLY),
            MeasureSpec.makeMeasureSpec(rect.height(), EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val drawY = (measuredHeight - rect.height()) / 2F + rect.height()
        // Draw colon first
        canvas.drawText(COLON, (widthF - length) / 2, drawY, paint)
        var text = (time / 60000).addZero
        canvas.drawText(text, (widthF - length) / 2 - paint.measureText(text), drawY, paint)
        text = (time / 1000 % 60).addZero
        canvas.drawText(text, (widthF + length) / 2, drawY, paint)
    }

    fun setTimeLong(duration: Long) {
        this.time = duration
    }

    fun setTimeInt(duration: Int) =
        setTimeLong(duration.toLong())

    fun setTextColor(@ColorInt newColor: Int) {
        paint.color = newColor
        invalidate()
    }

    private val Long.addZero get() = if (this < 10) { "0$this" } else { this.toString() }

}