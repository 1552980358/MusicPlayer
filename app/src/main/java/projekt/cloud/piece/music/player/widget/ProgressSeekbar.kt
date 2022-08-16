package projekt.cloud.piece.music.player.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kotlin.math.min
import projekt.cloud.piece.music.player.R

class ProgressSeekbar(context: Context, attributeSet: AttributeSet? = null): View(context, attributeSet), View.OnTouchListener {
    
    companion object {
        
        @JvmStatic
        @BindingAdapter("progress")
        fun ProgressSeekbar.setProgress(progress: Long?) {
            if (progress != null && progress >= 0) {
                this.progress = progress
            }
        }
    
        @JvmStatic
        @BindingAdapter("duration")
        fun ProgressSeekbar.setDuration(duration: Long?) {
            if (duration != null && duration > 0) {
                this.duration = duration
            }
        }
        
        private const val CORNERS_SIZE = 8
        
        private const val PAINT_START_POINT_X_Y = 0F
        
        private const val ANIMATOR_DURATION_GRADUALLY = 200L
        private const val ANIMATOR_DURATION_RAPIDLY = 100L
    }
    
    private val corners = FloatArray(CORNERS_SIZE)
    
    private val durationPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val durationPath = Path()
    
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val progressPath = Path()
    
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    
    private var progress = 0L
        set(value) {
            field = value
            graduallyMoveProgress(currentProgressPos, getWidthPosition(width.toFloat(), height.toFloat()))
        }
    
    private var touchProgress = 0L
        set(value) {
            field = value
            listener?.invoke(value, isReleased)
        }
    
    private var duration = 1L
        set(value) {
            field = value
            graduallyMoveProgress(currentProgressPos, getWidthPosition(width.toFloat(), height.toFloat()))
        }
    
    private var circleRadius = 0F
    
    private var animator: ValueAnimator? = null
    private var currentProgressPos = 0F
    private var touchedProgressPos = 0F
    
    private var isTouchMode = false
    private var isReleased = false
    
    private var listener: ((Long, Boolean) -> Unit)? = null
    
    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.ProgressSeekbar).use {
            circleRadius = it.getDimension(
                R.styleable.ProgressSeekbar_circleRadius,
                resources.getDimension(R.dimen.progress_seekbar_circle_radius)
            )
            
            circlePaint.color = it.getColor(R.styleable.ProgressSeekbar_circleColor, WHITE)
            circlePaint.strokeWidth = it.getDimension(
                R.styleable.ProgressSeekbar_circleWidth,
                resources.getDimension(R.dimen.progress_seekbar_circle_width)
            )
    
            progressPaint.color = it.getColor(R.styleable.ProgressSeekbar_progressColor, BLACK)
    
            durationPaint.color = it.getColor(
                R.styleable.ProgressSeekbar_durationColor,
                ContextCompat.getColor(context, R.color.progress_bar_duration_color)
            )
        }
        
        setOnTouchListener(this)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = min(    // Find min height
            resources.getDimensionPixelSize(R.dimen.progress_seekbar_height),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        (height / 2F).let { halfHeight ->
            repeat(CORNERS_SIZE) {
                corners[it] = halfHeight
            }
        }
        
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, EXACTLY))
        
        durationPath.reset()
        durationPath.addRoundRect(PAINT_START_POINT_X_Y, PAINT_START_POINT_X_Y, measuredWidth.toFloat(), measuredHeight.toFloat(), corners, Path.Direction.CW)
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        val height = height.toFloat()
        val halfHeight = height / 2
        
        // Draw duration
        canvas.drawPath(durationPath, durationPaint)
        
        // Check pos
        val progressPos = when {
            isTouchMode -> touchedProgressPos
            currentProgressPos == 0F && progress > 0 && animator == null -> getWidthPosition(width.toFloat(), height)
            else -> currentProgressPos
        }
    
        // Draw path
        progressPath.reset()
        progressPath.addRoundRect(PAINT_START_POINT_X_Y, PAINT_START_POINT_X_Y, progressPos + height, height, corners, Path.Direction.CW)
        canvas.drawPath(progressPath, progressPaint)
        
        canvas.drawCircle(progressPos + halfHeight, halfHeight, circleRadius, circlePaint)
    }
    
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val height = height.toFloat()
        val halfHeight = height / 2F
        when (event.action) {
            ACTION_DOWN -> {
                isTouchMode = true
                isReleased = false
                val target = event.getTouchedProgressPos(height, halfHeight)
                rapidlyMoveProgress(currentProgressPos, target)
                touchProgress = (target / (width - height) * duration).toLong()
            }
            ACTION_MOVE -> {
                touchedProgressPos = event.getTouchedProgressPos(height, halfHeight)
                invalidate()
                touchProgress = (touchedProgressPos / (width - height) * duration).toLong()
            }
            ACTION_UP -> {
                isReleased = true
                touchedProgressPos = event.getTouchedProgressPos(height, halfHeight)
                invalidate()
                touchProgress = (touchedProgressPos / (width - height) * duration).toLong()
                isTouchMode = false
            }
        }
        return true
    }
    
    private fun getWidthPosition(width: Float, height: Float) =
        (width - height) * progress / duration
    
    private fun MotionEvent.getTouchedProgressPos(height: Float, halfHeight: Float) = when {
        x < halfHeight -> 0F
        x > width - halfHeight -> width - height
        else -> x - halfHeight
    }
    
    private fun graduallyMoveProgress(from: Float, to: Float) =
        moveProgress(from, to, ANIMATOR_DURATION_GRADUALLY) {
            currentProgressPos = it.animatedValue as Float
            invalidate()
        }
    
    private fun rapidlyMoveProgress(from: Float, to: Float) =
        moveProgress(from, to, ANIMATOR_DURATION_RAPIDLY) {
            touchedProgressPos = it.animatedValue as Float
            invalidate()
        }
    
    private fun moveProgress(from: Float, to: Float, duration: Long, listener: ValueAnimator.AnimatorUpdateListener) {
        animator?.end()
        if (from == to) {
            animator = null
            return invalidate()
        }
        animator = ValueAnimator.ofFloat(from, to)
            .setDuration(duration)
            .apply {
                addUpdateListener(listener)
                doOnEnd { animator = null }
            }
        animator?.start()
    }
    
    fun setProgressControlledListener(listener: (Long, Boolean) -> Unit) {
        this.listener = listener
    }
    
}