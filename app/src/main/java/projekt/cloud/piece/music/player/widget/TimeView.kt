package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.StringUtil.withZero

class TimeView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    companion object {
        
        private const val DEFAULT_STR = "00:00"
        private const val COLON = ":"
        
        @JvmStatic
        @BindingAdapter("time")
        fun TimeView.setTime(time: Long?) {
            if (time != null) {
                this.time = time
            }
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
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.TimeView, 0, 0).let {
            with(paint) {
                textSize = it.getDimension(
                    R.styleable.TimeView_android_textSize,
                    resources.getDimension(R.dimen.time_view_text_size)
                )
                color = it.getColor(R.styleable.TimeView_android_textColor, BLACK)
            }
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
        
        val width = width
        
        val drawY = (measuredHeight - rect.height()) / 2F + rect.height()
        // Draw colon first
        canvas.drawText(COLON, (width - length) / 2, drawY, paint)
        var text = (time / 60000).withZero
        canvas.drawText(text, (width - length) / 2 - paint.measureText(text), drawY, paint)
        text = (time / 1000 % 60).withZero
        canvas.drawText(text, (width + length) / 2, drawY, paint)
    }
    
}