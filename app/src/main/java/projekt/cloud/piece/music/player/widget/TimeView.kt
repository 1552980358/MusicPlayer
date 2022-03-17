package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import lib.github1552980358.ktExtension.android.view.widthF
import projekt.cloud.piece.music.player.R

class TimeView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    companion object {
        private const val DEFAULT_STR = "00:00"
        private const val COLON = ":"
    
        @JvmStatic
        @BindingAdapter(value = ["app:time", "app:timeInt"], requireAll = false)
        fun TimeView.setTime(duration: Long?, durationInt: Int?) {
            if (duration != null) {
                this.duration = duration
            }
            if (durationInt != null) {
                this.duration = durationInt.toLong()
            }
        }
    
        @JvmStatic
        @BindingAdapter("app:color")
        fun TimeView.setColor(color: Int) {
            updateTextColor(color)
        }
        
    }
    
    private val paint = Paint()
    private val length: Float
    
    private val rect = Rect()
    
    init {
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.textSize = resources.getDimension(R.dimen.view_duration_text_size)
        length = paint.measureText(COLON)
        paint.getTextBounds(DEFAULT_STR, 0, DEFAULT_STR.length, rect)
    }
    
    var duration = 0L
        set(value) {
            field = value
            invalidate()
        }
    
    fun updateTextColor(@ColorInt newColor: Int) {
        paint.color = newColor
        invalidate()
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val margin = resources.getDimensionPixelSize(R.dimen.view_duration_text_margin)
        // Add 0.5dp to 4 sides
        setMeasuredDimension(rect.width() + margin, rect.height() + margin)
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        val drawY = (measuredHeight - rect.height()) / 2F + rect.height()
        // Draw colon first
        canvas.drawText(COLON, (widthF - length) / 2, drawY, paint)
        var text = (duration / 60000).addZero
        canvas.drawText(text, (widthF - length) / 2 - paint.measureText(text), drawY, paint)
        text = (duration / 1000 % 60).addZero
        canvas.drawText(text, (widthF + length) / 2, drawY, paint)
    }
    
    private val Long.addZero get() = if (this < 10) { "0$this" } else { this.toString() }
    
}