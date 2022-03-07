package sakuraba.saki.player.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
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
    
    private val paintProgress = Paint().apply {
        isAntiAlias = true
        style = FILL
    }
    private val paintCircle = Paint().apply {
        isAntiAlias = true
        style = STROKE
    }
    private val paintRemain = Paint().apply {
        isAntiAlias = true
    }

    private val radius by lazy { heightF / 2 }
    private val lineRadius by lazy { resources.getDimension(R.dimen.play_seek_bar_indicator_thick) }
    
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
        paintRemain.strokeWidth = lineRadius
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        paintCircle.strokeWidth = MeasureSpec.getSize(heightMeasureSpec) / 6F
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
        paintProgress.color = colorInt
        invalidate()
    }

    fun setCircleColor(@ColorInt colorInt: Int) {
        paintCircle.color = colorInt
        invalidate()
    }

    fun setRemainColor(@ColorInt colorInt: Int) {
        paintRemain.color = colorInt
        invalidate()
    }
    
    private fun updateProgress(motionEvent: MotionEvent) {
        progress = when {
            motionEvent.x < 0 -> 0
            motionEvent.x > width - heightF -> max
            else -> (motionEvent.x / (widthF - heightF) * max).toLong()
        }
    }
    
    fun setOnSeekChangeListener(block: (progress: Long, isUser: Boolean, isReleased: Boolean) -> Unit) {
        listener = OnSeekChangeListener(block)
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        // Draw
        var drawX = progress * (widthF - heightF) / max + radius
        when {
            drawX < radius -> drawX = radius
            drawX > width - radius -> drawX = width - radius
        }

        paintRemain.style = STROKE
        canvas.drawLine(0F, height / 2F, width - radius - lineRadius, height / 2F, paintRemain)
        paintRemain.style = FILL
        canvas.drawArc(width - radius - lineRadius * 2, (height - lineRadius) / 2, width - radius, (height + lineRadius) / 2, -90F, 180F, true, paintRemain)

        canvas.drawArc(0F, 0F, heightF, heightF, 90F, 180F, true, paintProgress)
        if (drawX > radius) {
            canvas.drawRect(radius - 1, 0F, drawX + 1, heightF, paintProgress)
        }
        canvas.drawArc(drawX - radius - 1, 0F, drawX + radius, heightF, -90F, 180F, true, paintProgress)
        canvas.drawCircle(drawX, height / 2F, radius / 2, paintCircle)
    }

    fun updateProgressDataBinding(progress: Long) {
        if (!isUserTouched) {
            this.progress = progress
        }
    }
    
}