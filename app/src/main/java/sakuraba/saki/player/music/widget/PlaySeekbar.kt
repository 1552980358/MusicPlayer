package sakuraba.saki.player.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import sakuraba.saki.player.music.R

class PlaySeekbar: View {
    
    private fun interface OnSeekChangeListener {
        fun onSeekChange(progress: Long, isUser: Boolean, isReleased: Boolean)
    }
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    private val paint = Paint().apply { isAntiAlias = true }
    private val paintRemain = Paint().apply { isAntiAlias = true }
    
    var isUserTouched = false
    var isReleased = true
    
    private var listener: OnSeekChangeListener? = null
    
    init {
        @Suppress("ClickableViewAccessibility")
        setOnTouchListener { _, motionEvent ->
            Log.e("PlaySeekbar", motionEvent.action.toString())
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    isUserTouched = true
                    isReleased = false
                    updateProgress(motionEvent)
                }
                ACTION_MOVE -> updateProgress(motionEvent)
                ACTION_UP -> {
                    isUserTouched = true
                    isReleased = true
                    updateProgress(motionEvent)
                    isUserTouched = false
                }
            }
            return@setOnTouchListener true
        }
        paint.strokeWidth = resources.getDimension(R.dimen.play_seek_bar_indicator_thick)
        paint.style = Paint.Style.STROKE
        paintRemain.strokeWidth = resources.getDimension(R.dimen.play_seek_bar_indicator_thick)
        paintRemain.style = Paint.Style.STROKE
    }
    
    var progress = 1L
        @MainThread
        set(value) {
            field = value
            listener?.onSeekChange(value, isUserTouched, isReleased)
            invalidate()
        }
    
    var max = 100L
        @MainThread
        set(value) {
            field = value
            invalidate()
        }
    
    fun setProgressColor(@ColorInt colorInt: Int) {
        paint.color = colorInt
        invalidate()
    }

    fun setRemainColor(@ColorInt colorInt: Int) {
        paintRemain.color = colorInt
        invalidate()
    }
    
    private fun updateProgress(motionEvent: MotionEvent) {
        progress = when {
            motionEvent.x < 0 -> 0
            motionEvent.x > width -> max
            else -> (motionEvent.x / widthF * max).toLong()
        }
    }
    
    fun setOnSeekChangeListener(block: (progress: Long, isUser: Boolean, isReleased: Boolean) -> Unit) {
        listener = OnSeekChangeListener(block)
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        // Draw
        var drawX = progress * widthF / max
        when {
            drawX < paint.strokeWidth / 2 -> drawX = paint.strokeWidth / 2
            drawX >= width -> drawX = widthF - paint.strokeWidth / 2
        }

        val lineY = height / 2F
        canvas.drawLine(drawX, lineY, widthF, lineY, paintRemain)
        canvas.drawLine(0F, lineY, drawX, lineY, paint)
        canvas.drawLine(drawX, 0F, drawX, heightF, paint)
    }
    
}