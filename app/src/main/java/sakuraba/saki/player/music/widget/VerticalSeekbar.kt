package sakuraba.saki.player.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import sakuraba.saki.player.music.R

class VerticalSeekbar: View {
    
    private fun interface OnSeekChangeListener {
        fun onSeekChange(progress: Int, isUser: Boolean)
    }
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    private val paintRemain = Paint()
    private val paintPass = Paint()
    private val paintCircle = Paint()
    
    var isUser = false
    
    private var listener: OnSeekChangeListener? = null
    
    init {
        paintRemain.apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            color = ContextCompat.getColor(context, R.color.vertical_seekbar_remain_light)
        }
        paintPass.apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }
        paintCircle.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        @Suppress("ClickableViewAccessibility")
        setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    isUser = true
                    updateProgress(motionEvent.y)
                }
                ACTION_MOVE -> {
                    updateProgress(motionEvent.y)
                }
                ACTION_UP -> {
                    updateProgress(motionEvent.y)
                    isUser = false
                }
            }
            
            return@setOnTouchListener true
        }
    }
    
    private fun updateProgress(y: Float) {
        val padding = width / 2
        val progress = (max - (y - padding) / (heightF - padding) * max).toInt()
        this.progress = when {
            progress < min -> min
            progress > max -> max
            else -> progress
        }
    }
    
    fun updateColor(newColor: Int, isLight: Boolean) {
        paintPass.color = newColor
        paintCircle.color = newColor
        if (isLight) {
            paintRemain.color = ContextCompat.getColor(context, R.color.vertical_seekbar_remain_light)
        } else {
            paintRemain.color = ContextCompat.getColor(context, R.color.vertical_seekbar_remain_dark)
        }
        invalidate()
    }
    
    var min = 0
    var progress = 0
        set(value) {
            field = value
            listener?.onSeekChange(field, isUser)
            invalidate()
        }
    var max = 0
    
    fun initSettings(min: Int, progress: Int, max: Int) {
        this.min = min
        this.max = max
        this.progress = progress
    }
    
    fun setOnSeekChangeListener(listener: (progress: Int, isUser: Boolean) -> Unit) {
        this.listener = OnSeekChangeListener(listener)
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        canvas ?: return
        
        val cylinderWidth = widthF / 6
        val cylinderXLeft = (widthF - cylinderWidth) / 2
        val cylinderXRight = (widthF + cylinderWidth) / 2
        val radius = widthF / 4
        
        if (max == 0) {
            canvas.drawRect(cylinderXLeft, radius, cylinderXRight, heightF - radius, paintRemain)
            canvas.drawCircle(widthF / 2, heightF - radius, radius, paintCircle)
            return
        }
    
        val progressY = (heightF - radius * 2) * (max - progress) / max + radius
        canvas.drawRect(cylinderXLeft, radius, cylinderXRight, progressY, paintRemain)
        canvas.drawRect(cylinderXLeft, progressY, cylinderXRight, heightF - radius, paintPass)
        canvas.drawCircle(widthF / 2, progressY, radius, paintCircle)
    }
    
}