package sakuraba.saki.player.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL_AND_STROKE
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import sakuraba.saki.player.music.R

class PlayProgressBar: View {
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    private val paint = Paint()
    
    init {
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.purple_500)
        paint.style = FILL_AND_STROKE
    }
    
    var progress = 0L
        set(value) {
            field = value
            invalidate()
        }
    
    var max = 1L
        set(value) {
            field = value
            invalidate()
        }
    
    fun setColor(@ColorInt color: Int) {
        paint.color = color
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        canvas.drawRect(0F, 0F, progress * widthF / max, heightF, paint)
    }
    
}