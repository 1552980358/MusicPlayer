package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import projekt.cloud.piece.music.player.R

class ProgressBar(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    private val paint = Paint().apply {
        isAntiAlias = true
        style = FILL
    }
    
    var progress = 0L
        set(value) {
            Log.e("ProgressBar", "progress" + value.toString())
            field = value
            invalidate()
        }
    
    var duration = 0L
        set(value) {
            Log.e("ProgressBar", "DURATION" + value.toString())
            field = value
            invalidate()
        }
    
    fun setColor(@ColorInt color: Int) {
        paint.color = color
        invalidate()
    }
    
    init {
        paint.color = getColor(context, R.color.progress_bar)
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        if (progress != 0L && duration != 0L) {
            canvas.drawRect(0F, 0F, progress * widthF / duration, heightF, paint)
        }
    }
    
}