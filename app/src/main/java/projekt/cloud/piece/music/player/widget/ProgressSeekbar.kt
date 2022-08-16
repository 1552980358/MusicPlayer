package projekt.cloud.piece.music.player.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kotlin.math.min
import projekt.cloud.piece.music.player.R

class ProgressSeekbar(context: Context, attributeSet: AttributeSet? = null): View(context, attributeSet) {
    
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
        
    }
    
    private val corners = FloatArray(8)
    
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
    
    private var duration = 1L
        set(value) {
            field = value
            invalidate()
            graduallyMoveProgress(currentProgressPos, getWidthPosition(width.toFloat(), height.toFloat()))
        }
    
    private var circleRadius = 0F
    
    private var animator: ValueAnimator? = null
    private var currentProgressPos = 0F
    
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
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = min(    // Find min height
            resources.getDimensionPixelSize(R.dimen.progress_seekbar_height),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        (height / 2F).let { halfHeight ->
            repeat(8) {
                corners[it] = halfHeight
            }
        }
        
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, EXACTLY))
        
        durationPath.reset()
        durationPath.addRoundRect(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), corners, Path.Direction.CW)
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        val height = height.toFloat()
        val halfHeight = height / 2
        
        // Draw duration
        canvas.drawPath(durationPath, durationPaint)
        
        // Draw path
        progressPath.reset()
        progressPath.addRoundRect(0F, 0F, currentProgressPos + height, height, corners, Path.Direction.CW)
        canvas.drawPath(progressPath, progressPaint)
        
        canvas.drawCircle(currentProgressPos + halfHeight, halfHeight, circleRadius, circlePaint)
    }
    
    private fun getWidthPosition(width: Float, height: Float) =
        (width - height) * progress / duration
    
    private fun graduallyMoveProgress(from: Float, to: Float) {
        animator?.end()
        animator = ValueAnimator.ofFloat(from, to)
            .setDuration(200L)
            .apply {
                addUpdateListener {
                    currentProgressPos = it.animatedValue as Float
                    invalidate()
                }
                doOnEnd { animator = null }
            }
        animator?.start()
    }
    
}