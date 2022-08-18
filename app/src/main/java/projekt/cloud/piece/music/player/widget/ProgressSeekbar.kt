package projekt.cloud.piece.music.player.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.DKGRAY
import android.graphics.Color.LTGRAY
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
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
        
        private val PAINT_START_POINT = PointF(0F, 0F)
        
        private const val ANIMATOR_DURATION_GRADUALLY = 200L
        private const val ANIMATOR_DURATION_RAPIDLY = 100L
    }
    
    private var duration = 1L
        set(value) {
            field = value
            updateProgress()
        }
    private var progress = 0L
        set(value) {
            field = value
            updateProgressAnimated()
        }
    private var touchProgress = 0L
        set(value) {
            field = value
            listener?.invoke(value, isReleased)
        }
    
    private val corners = FloatArray(CORNERS_SIZE)
    
    /** Draw Duration bar **/
    private val durationPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val durationPath = Path()
    
    private var circleRadius = 0F
    /** Draw Circle **/
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    
    /** Primary paint: user seeking / non-seeking progress **/
    private val primaryPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    /** Secondary paint: seeking progress **/
    private val secondaryPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val primaryPath = Path()
    private val secondaryPath = Path()
    
    private var primaryPos = 0F
    private var secondaryPos = 0F
    
    /** Flags **/
    private var isTouchMode = false
    private var isReleased = true
    private var isSecondaryResetting = false
    
    private var primaryAnimator: ValueAnimator? = null
    private var secondaryAnimator: ValueAnimator? = null
    
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
            
            primaryPaint.color = it.getColor(R.styleable.ProgressSeekbar_primaryColor, BLACK)
            secondaryPaint.color = it.getColor(R.styleable.ProgressSeekbar_secondaryColor, DKGRAY)
            durationPaint.color = it.getColor(R.styleable.ProgressSeekbar_durationColor, LTGRAY)
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
        durationPath.addRoundRect(PAINT_START_POINT.x, PAINT_START_POINT.y, measuredWidth.toFloat(), measuredHeight.toFloat(), corners, Path.Direction.CW)
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        // Draw duration
        canvas.drawPath(durationPath, durationPaint)
    
        val height = height.toFloat()
        val halfHeight = height / 2
        
        if (isTouchMode || isSecondaryResetting) {
            secondaryPath.reset()
            secondaryPath.addRoundRect(PAINT_START_POINT.x, PAINT_START_POINT.y, secondaryPos + height, height, corners, Path.Direction.CW)
            canvas.drawPath(secondaryPath, secondaryPaint)
        }
    
        primaryPath.reset()
        primaryPath.addRoundRect(PAINT_START_POINT.x, PAINT_START_POINT.y, primaryPos + height, height, corners, Path.Direction.CW)
        canvas.drawPath(primaryPath, primaryPaint)
        
        canvas.drawCircle(primaryPos + halfHeight, halfHeight, circleRadius, circlePaint)
    }
    
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val height = height.toFloat()
        val halfHeight = height / 2F
    
        val touchPos = event.getTouchedProgressPos(height, halfHeight)
        
        when (event.action) {
            ACTION_DOWN -> {
                isTouchMode = true
                isReleased = false
                // Update secondary
                secondaryPos = primaryPos
                
                // Update primary
                movePrimaryPaint(primaryPos, touchPos, ANIMATOR_DURATION_RAPIDLY)
                // Update touched progress
                touchProgress = (touchPos * duration / (width - height)).toLong()
            }
            ACTION_MOVE -> {
                primaryPos = touchPos
                invalidate()
            }
            ACTION_UP -> {
                isReleased = true
                // Update touched progress
                touchProgress = (touchPos * duration / (width - height)).toLong()
                moveSecondaryPaint(
                    secondaryPos,
                    primaryPos,
                    ANIMATOR_DURATION_RAPIDLY ,
                    onStart = { isSecondaryResetting = true },
                    onEnd = { isSecondaryResetting = false }
                )
                isTouchMode = false
            }
        }
        return true
    }
    
    fun setProgressControlledListener(listener: (Long, Boolean) -> Unit) {
        this.listener = listener
    }
    
    private fun updateProgress() {
        val width = width.toFloat()
        val height = height.toFloat()
        when {
            // Just move secondary
            isTouchMode -> secondaryPos = getProgressX(progress, duration, width, height)
            // Move primary
            else -> primaryPos = getProgressX(progress, duration, width, height)
        }
        invalidate()
    }
    
    private fun updateProgressAnimated() {
        val width = width.toFloat()
        val height = height.toFloat()
        if (isTouchMode) {
            // Just move secondary
            return moveSecondaryPaint(secondaryPos, getProgressX(progress, duration, width, height), ANIMATOR_DURATION_GRADUALLY)
        }
        // Move primary
        movePrimaryPaint(primaryPos, getProgressX(progress, duration, width, height), ANIMATOR_DURATION_GRADUALLY)
    }
    
    private fun getProgressX(progress: Long, duration: Long, width: Float, height: Float) =
        (width - height) * progress / duration
    
    private fun MotionEvent.getTouchedProgressPos(height: Float, halfHeight: Float) = when {
        x < halfHeight -> 0F
        x > width - halfHeight -> width - height
        else -> x - halfHeight
    }
    
    private fun moveSecondaryPaint(from: Float, to: Float, duration: Long, onStart: () -> Unit = {}, onEnd: () -> Unit = {}) {
        secondaryAnimator?.end()
        if (from == to) {
            secondaryPos = to
            return invalidate()
        }
        secondaryAnimator = ValueAnimator.ofFloat(from, to)
            .setDuration(duration)
            .apply {
                addUpdateListener {
                    secondaryPos = it.animatedValue as Float
                    invalidate()
                }
                doOnStart { onStart.invoke() }
                doOnEnd { onEnd.invoke() }
                start()
            }
    }
    
    private fun movePrimaryPaint(from: Float, to: Float, duration: Long, onEnd: () -> Unit = {}) {
        primaryAnimator?.end()
        if (from == to) {
            primaryPos = to
            return invalidate()
        }
        primaryAnimator = ValueAnimator.ofFloat(from, to)
            .setDuration(duration)
            .apply {
                addUpdateListener {
                    primaryPos = it.animatedValue as Float
                    invalidate()
                }
                doOnEnd { onEnd.invoke() }
                start()
            }
    }
    
}