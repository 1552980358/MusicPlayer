package app.github1552980358.android.musicplayer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.TimeExchange
import kotlin.math.abs

/**
 * [SeekingBar]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/6/28
 * @time    : 11:35
 **/

@Suppress("MemberVisibilityCanBePrivate")
class SeekingBar: View, TimeExchange {
    
    companion object {
        
        // const val DrawFull = 0
        // const val DrawRemain = 1
        
        const val TEXT_ZERO = "00:00"
    }
    
    constructor(context: Context): this(context, null)
    
    constructor(context: Context, attributeSet: AttributeSet?):
        this(context, attributeSet, 0)
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int):
        this(context, attributeSet, 0, 0)
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        super(context, attributeSet, defStyleAttr, defStyleRes)
    
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    
    var proc = 0
        set(value) {
            if (value < 0) {
                throw Exception()
            }
            field = value
            postInvalidate()
        }
    
    var max = 0
        set(value) {
            if (value < 0) {
                throw Exception()
            }
            field = value
            postInvalidate()
        }
    
    private var drawText = true
        set(value) {
            field = value
            postInvalidate()
        }
    
    //var textDrawMethod = DrawFull
    //set(value) {
    //    field = value
    //    postInvalidate()
    //}
    
    var textColor = Color.WHITE //ContextCompat.getColor(context, R.color.colorPrimaryDark)
        set(value) {
            field = value
            postInvalidate()
        }
    
    var textPadding = resources.getDimension(R.dimen.seekingBar_text_padding)
    
    var leftColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        set(value) {
            field = value
            postInvalidate()
        }
    
    var rightColor = ContextCompat.getColor(context, R.color.colorPrimary)
        set(value) {
            field = value
            postInvalidate()
        }
    
    var cursorColor = Color.WHITE
        set(value) {
            field = value
            postInvalidate()
        }
    
    var cursorThickness = resources.getDimension(R.dimen.seekingBar_cursor_thickness)
        set(value) {
            field = value
            postInvalidate()
        }
    
    var cursorProc = 0F
    
    private var baseline = 0F
    private var widthText = 0F
    
    init {
        val rect = Rect()
        paint.getTextBounds(TEXT_ZERO, 0, 5, rect)
        baseline = abs(paint.fontMetrics.ascent)
        widthText = rect.width().toFloat()
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        paint.style = Paint.Style.FILL_AND_STROKE
        
        cursorProc = proc.toFloat() * width.toFloat() / max.toFloat()
        
        if ((proc == 0 && max == 0) || cursorProc < cursorThickness / 2) {
            
            paint.color = rightColor
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
            
            paint.color = cursorColor
            canvas.drawRect(0F, 0F, cursorThickness, height.toFloat(), paint)
            
            paint.color = textColor
            canvas.drawText(getTimeText(proc), textPadding, (height + baseline) / 2, paint)
            canvas.drawText(
                getTimeText(max),
                width - textPadding - widthText,
                (height + baseline) / 2,
                paint
            )
            return
        }
        
        // Draw left side
        // 绘制左方
        paint.color = leftColor
        canvas.drawRect(0F, 0F, cursorProc, height.toFloat(), paint)
        
        // Draw right side
        // 绘制右方
        paint.color = rightColor
        canvas.drawRect(cursorProc, 0F, width.toFloat(), height.toFloat(), paint)
        
        // Draw cursor
        // 绘制位置
        paint.color = cursorColor
        canvas.drawRect(cursorProc - cursorThickness / 2, 0F, cursorProc + cursorThickness / 2, height.toFloat(), paint)
        
        // Draw text
        // 绘制文字
        paint.color = textColor
        canvas.drawText(getTimeText(proc), textPadding, (height + baseline) / 2, paint)
        canvas.drawText(
            getTimeText(max),
            width - textPadding - widthText,
            (height + baseline) / 2,
            paint
        )
        
    }
    
}