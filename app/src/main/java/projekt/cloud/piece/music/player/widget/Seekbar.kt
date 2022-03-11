package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.getMode
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import projekt.cloud.piece.music.player.R

class Seekbar(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    private var listener: ((Long, Boolean) -> Unit)? = null
    
    companion object {
        const val DEFAULT_STR = "00:00"
    }
    
    private val paintProgress = Paint().apply {
        isAntiAlias = true
        style = FILL
    }
    private val paintCircle = Paint().apply {
        isAntiAlias = true
        style = FILL
    }
    private val paintRemain = Paint().apply {
        isAntiAlias = true
        style = FILL
    }
    private val rect = Rect()
    
    private val radius by lazy { heightF / 2 }
    private val lineRadius by lazy { resources.getDimension(R.dimen.play_progressbar_indicator_thick) }
    
    private var isUserTouched = false
    private var isReleased = true
    
    init {
        @Suppress("ClickableViewAccessibility")
        setOnTouchListener { _, motionEvent ->
            Log.e("PlaySeekbar", motionEvent.action.toString())
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                    isUserTouched = true
                    isReleased = false
                    updateProgress(motionEvent)
                }
                MotionEvent.ACTION_MOVE -> updateProgress(motionEvent)
                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    isUserTouched = true
                    isReleased = true
                    updateProgress(motionEvent)
                    isUserTouched = false
                }
            }
            return@setOnTouchListener true
        }
        paintRemain.strokeWidth = lineRadius
    
        paintProgress.textSize = resources.getDimension(R.dimen.view_duration_text_size)
        paintProgress.getTextBounds(DEFAULT_STR, 0, DEFAULT_STR.length, rect)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (getMode(heightMeasureSpec) != EXACTLY) {
            return setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), resources.getDimensionPixelSize(R.dimen.play_seekbar_wrap_height))
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    
    var progress = 1L
        @MainThread
        set(value) {
            field = value
            invalidate()
        }
    
    private var touchProgress = 1L
        @MainThread
        set(value) {
            field = value
            listener?.let { it(value, isReleased) }
            invalidate()
        }
    
    private fun updateProgress(motionEvent: MotionEvent) {
        touchProgress = when {
            motionEvent.x < 0 -> 0
            motionEvent.x > width - heightF -> duration
            else -> (motionEvent.x / (widthF - heightF) * duration).toLong()
        }
    }
    
    var duration = 100L
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
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        // Draw
        var drawX =
            (if (isUserTouched) touchProgress else progress) * (widthF - heightF) / duration + radius
        
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
    
    fun setOnSeekChangeListener(block: (progress: Long, isUser: Boolean) -> Unit) {
        listener = block
    }

}