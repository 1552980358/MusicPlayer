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
        this(context, attributeSet, defStyleAttr, 0)
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        super(context, attributeSet, defStyleAttr, defStyleRes)
    
    /**
     * [paint]
     * @author 1552980358
     * @since 0.1
     **/
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    
    /**
     * [process]
     * @author 1552980358
     * @since 0.1
     **/
    var process = 0
        set(value) {
            if (value < 0) {
                throw Exception()
            }
            field = value
            postInvalidate()
        }
    
    /**
     * [maximum]
     * @author 1552980358
     * @since 0.1
     **/
    var maximum = 0
        set(value) {
            if (value < 0) {
                throw Exception()
            }
            field = value
            postInvalidate()
        }
    
    /**
     * [drawText]
     * @author 1552980358
     * @since 0.1
     **/
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
    
    /**
     * [textColor]
     * @author 1552980358
     * @since 0.1
     **/
    var textColor = Color.WHITE //ContextCompat.getColor(context, R.color.colorPrimaryDark)
        set(value) {
            field = value
            postInvalidate()
        }
    
    /**
     * [textPadding]
     * @author 1552980358
     * @since 0.1
     **/
    private var textPadding = resources.getDimension(R.dimen.seekingBar_text_padding)
    
    /**
     * [progressColor]
     * @author 1552980358
     * @since 0.1
     **/
    var progressColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        set(value) {
            field = value
            postInvalidate()
        }
    
    /**
     * [indeterminateColor]
     * @author 1552980358
     * @since 0.1
     **/
    var indeterminateColor = ContextCompat.getColor(context, R.color.colorPrimary)
        set(value) {
            field = value
            postInvalidate()
        }
    
    /**
     * [thumbColor]
     * @author 1552980358
     * @since 0.1
     **/
    var thumbColor = Color.WHITE
        set(value) {
            field = value
            postInvalidate()
        }
    
    /**
     * [thumbThickness]
     * @author 1552980358
     * @since 0.1
     **/
    var thumbThickness = resources.getDimension(R.dimen.seekingBar_cursor_thickness)
        set(value) {
            field = value
            postInvalidate()
        }
    
    /**
     * [thumbProc]
     * @author 1552980358
     * @since 0.1
     **/
    var thumbProc = 0F
    
    /**
     * [baseline]
     * @author 1552980358
     * @since 0.1
     **/
    private var baseline = 0F
    
    /**
     * [widthText]
     * @author 1552980358
     * @since 0.1
     **/
    private var widthText = 0F
    
    init {
        val rect = Rect()
        paint.getTextBounds(TEXT_ZERO, 0, 5, rect)
        baseline = abs(paint.fontMetrics.ascent)
        widthText = rect.width().toFloat()
    }
    
    /**
     * [onDraw]
     * @param canvas [Canvas]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        paint.style = Paint.Style.FILL_AND_STROKE
        
        thumbProc = process.toFloat() * width.toFloat() / maximum.toFloat()
        
        if ((process == 0 && maximum == 0) || thumbProc < thumbThickness / 2) {
            
            paint.color = indeterminateColor
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
            
            paint.color = thumbColor
            canvas.drawRect(0F, 0F, thumbThickness, height.toFloat(), paint)
            
            paint.color = textColor
            canvas.drawText(getTimeText(process), textPadding, (height + baseline) / 2, paint)
            canvas.drawText(
                getTimeText(maximum),
                width - textPadding - widthText,
                (height + baseline) / 2,
                paint
            )
            return
        }
        
        // Draw left side
        // 绘制左方
        paint.color = progressColor
        canvas.drawRect(0F, 0F, thumbProc, height.toFloat(), paint)
        
        // Draw right side
        // 绘制右方
        paint.color = indeterminateColor
        canvas.drawRect(thumbProc, 0F, width.toFloat(), height.toFloat(), paint)
        
        // Draw cursor
        // 绘制位置
        paint.color = thumbColor
        canvas.drawRect(thumbProc - thumbThickness / 2, 0F, thumbProc + thumbThickness / 2, height.toFloat(), paint)
        
        // Not to draw text
        // 不绘制文字
        if (!drawText) {
            return
        }
        
        // Draw text
        // 绘制文字
        paint.color = textColor
        canvas.drawText(getTimeText(process), textPadding, (height + baseline) / 2, paint)
        canvas.drawText(
            getTimeText(maximum),
            width - textPadding - widthText,
            (height + baseline) / 2,
            paint
        )
        
    }
    
}