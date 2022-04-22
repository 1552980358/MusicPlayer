package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.ViewUtil.heightF
import projekt.cloud.piece.music.player.util.ViewUtil.widthF

class ProgressBar(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("circleColor")
        fun ProgressBar.setCircleColor(@ColorInt colorInt: Int?) {
            colorInt?.let {
                paintCircle.color = it
                invalidate()
            }
        }

        @JvmStatic
        @BindingAdapter("progressColor")
        fun ProgressBar.setProgressColor(@ColorInt colorInt: Int?) {
            colorInt?.let {
                paintProgress.color = it
                invalidate()
            }
        }

        @JvmStatic
        @BindingAdapter("durationColor")
        fun ProgressBar.setDurationColor(@ColorInt colorInt: Int?) {
            colorInt?.let {
                paintDuration.color = it
                invalidate()
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["progress", "progressInt"], requireAll = false)
        fun ProgressBar.setProgress(progress: Long?, progressInt: Int?) {
            progress?.let {
                this.progress = it
                return
            }
            progressInt?.let {
                this.progress = it.toLong()
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["duration", "durationInt"], requireAll = false)
        fun ProgressBar.setDuration(duration: Long?, durationInt: Int?) {
            duration?.let {
                this.duration = it
                return
            }
            durationInt?.let {
                this.duration = it.toLong()
            }
        }

    }

    private val paintCircle = Paint().apply {
        isAntiAlias = true
        style = STROKE
    }

    private val paintProgress = Paint().apply {
        isAntiAlias = true
        style = FILL
    }

    private val paintDuration = Paint().apply {
        isAntiAlias = true
        style = STROKE
        strokeCap = ROUND
    }

    private var circleRadius: Float

    private var progress = 0L
        @MainThread
        set(value) {
            field = value
            invalidate()
        }

    private var progressControlled = 0L
        @MainThread
        set(value) {
            field = value
            onProgressChanged?.invoke(value, isReleased)
            invalidate()
        }

    private var duration = 1L
        @MainThread
        set(value) {
            field = value
            invalidate()
        }

    private var onProgressChanged: ((Long, Boolean) -> Unit)? = null

    private var isControlled = false
    private var isReleased = true

    init {
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar, 0, 0).apply {
            circleRadius = getDimension(
                R.styleable.ProgressBar_circleRadius,
                resources.getDimension(R.dimen.progress_bar_default_radius)
            )
            paintCircle.strokeWidth = getDimension(
                R.styleable.ProgressBar_circleStrokeWidth,
                resources.getDimension(R.dimen.progress_bar_default_circle_stroke_width)
            )
            paintDuration.strokeWidth = getDimension(
                R.styleable.ProgressBar_remain_thick,
                resources.getDimension(R.dimen.progress_bar_default_remain_thick)
            )
        }

        @Suppress("ClickableViewAccessibility")
        setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                    isControlled = true
                    isReleased = false
                    updateProgress(motionEvent)
                }
                ACTION_MOVE -> updateProgress(motionEvent)
                ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    isControlled = false
                    updateProgress(motionEvent)
                    isReleased = true
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MeasureSpec.getMode(heightMeasureSpec) != EXACTLY) {
            return setMeasuredDimension(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(
                    resources.getDimensionPixelSize(R.dimen.progress_bar_default_height),
                    AT_MOST
                )
            )
        }

        circleRadius = MeasureSpec.getSize(heightMeasureSpec).run {
            (this / 2F) - (paintCircle.strokeWidth * 4)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val heightF = heightF
        val halfHeight = heightF / 2
        val progress = (widthF - height) *
            (if (isControlled) progressControlled else progress) /
            duration + halfHeight

        // Draw remain first
        canvas.drawLine(
            progress, halfHeight, width - paintProgress.strokeWidth / 2, halfHeight, paintDuration
        )

        // Draw start edge
        canvas.drawArc(0F, 0F, heightF, heightF, 90F, 180F, true, paintProgress)

        // Draw progress rect
        canvas.drawRect(halfHeight, 0F, progress, heightF, paintProgress)

        // Draw end edge
        canvas.drawArc(
            progress - heightF, 0F, progress + heightF, heightF, 270F, 180F, true, paintProgress
        )

        // Draw circle
        canvas.drawCircle(progress, halfHeight, circleRadius, paintCircle)
    }

    private fun updateProgress(motionEvent: MotionEvent) {
        progressControlled = when {
            motionEvent.x < 0 -> 0
            motionEvent.x > width - height -> duration
            else -> ((motionEvent.x - height / 2) * duration / (width - height / 2)).toLong()
        }
    }

    fun setOnProgressChanged(onProgressChanged: (Long, Boolean) -> Unit) {
        this.onProgressChanged = onProgressChanged
    }

}