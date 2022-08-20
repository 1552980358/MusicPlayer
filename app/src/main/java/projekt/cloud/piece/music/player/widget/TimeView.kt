package projekt.cloud.piece.music.player.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.BindingAdapter
import java.lang.Math.abs
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.StringUtil.withZero

class TimeView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    companion object {
        
        private const val DEFAULT_STR = "00:00"
        private const val COLON = ":"
        
        @JvmStatic
        @BindingAdapter("time")
        fun TimeView.setTime(time: Long?) {
            if (time != null) {
                updateTime(time)
            }
        }
        
        private const val ANIMATION_DURATION = 400L
        
    }
    
    private class Time(time: Long = 0L) {
    
        var time = time
            private set
        var min = (time / 60000).withZero
            private set
        var sec = (time / 1000 % 60).withZero
            private set
        
        private fun update(time: Long) {
            this.time = time
            min = (time / 60000).withZero
            sec = (time / 1000 % 60).withZero
        }
        
        private fun update(time: Time) {
            this.time = time.time
            min = time.min
            sec = time.sec
        }
        
        fun update(time: Time, timeLong: Long) {
            time.update(this)
            update(timeLong)
        }
        
    }
    
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    
    private val length: Float
    
    private val rect = Rect()
    
    private var lastTime = Time()
    private var currentTime = Time()
    
    private val requireAnimator: Boolean
    private var isAnimating = false
    private var animator: ValueAnimator? = null
    private var offsetY = 0F
    
    init {
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.TimeView, 0, 0).let {
            with(paint) {
                textSize = it.getDimension(
                    R.styleable.TimeView_android_textSize,
                    resources.getDimension(R.dimen.time_view_text_size)
                )
                color = it.getColor(R.styleable.TimeView_android_textColor, BLACK)
            }
            requireAnimator = it.getBoolean(R.styleable.TimeView_requireAnimation, true)
        }
        
        length = paint.measureText(COLON)
        paint.getTextBounds(DEFAULT_STR, 0, DEFAULT_STR.length, rect)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(rect.width(), EXACTLY),
            MeasureSpec.makeMeasureSpec(rect.height(), EXACTLY)
        )
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        val width = width
        val textHeight = rect.height().toFloat()
        
        when {
            isAnimating -> drawTextAnimating(canvas, width, lastTime, currentTime, offsetY, textHeight)
            else -> drawTime(canvas, width, currentTime, textHeight)
        }
    }
    
    private fun drawTime(canvas: Canvas, width: Int, time: Time, drawY: Float) {
        // Draw colon first
        canvas.drawText(COLON, (width - length) / 2, drawY, paint)
        canvas.drawText(time.min, (width - length) / 2 - paint.measureText(time.min), drawY, paint)
        canvas.drawText(time.sec, (width + length) / 2, drawY, paint)
    }
    
    private fun drawTextAnimating(canvas: Canvas, width: Int, lastTime: Time, currentTime: Time, upperY: Float, textHeight: Float) {
        drawTime(canvas, width, lastTime, upperY)
        drawTime(canvas, width, currentTime, upperY + textHeight)
    }
    
    private fun startChangingAnimation(from: Float, @Suppress("SameParameterValue") to: Float) {
        animator?.end()
        animator = ValueAnimator.ofFloat(from, to).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                offsetY = it.animatedValue as Float
                invalidate()
            }
            doOnStart { isAnimating = true }
            doOnEnd {
                animator = null
                isAnimating = false
            }
        }
        animator?.start()
    }
    
    fun updateTime(time: Long) {
        currentTime.update(lastTime, time)
        when {
            requireAnimator && abs(lastTime.time - time) > 1000 -> startChangingAnimation(rect.height().toFloat(), 0F)
            else -> invalidate()
        }
    }
    
}