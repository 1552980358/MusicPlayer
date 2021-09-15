package sakuraba.saki.player.music.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import sakuraba.saki.player.music.R

class DurationView: View {
    
    companion object {
        private const val COLON = ":"
    }
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    private val paint = Paint()
    
    init {
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.textSize = resources.getDimension(R.dimen.view_duration_text_size)
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
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        val length = paint.measureText(COLON)
        val drawY = heightF - paint.fontMetrics.bottom
        // Draw colon first
        canvas.drawText(COLON, (widthF - length) / 2, drawY, paint)
        var text = (duration / 60000).addZero
        canvas.drawText(text, (widthF - length) / 2 - paint.measureText(text), drawY, paint)
        text = (duration / 1000 % 60).addZero
        canvas.drawText(text, (widthF + length) / 2, drawY, paint)
    }
    
    private val Long.addZero get() = if (this < 10) { "0$this" } else { this.toString() }
    
}