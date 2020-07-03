package app.github1552980358.android.musicplayer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.TimeExchange

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
    
        const val DrawFull = 0
        const val DrawRemain = 1
    
        const val TEXT_ZERO = "00:00"
    
        interface OnTouchListener {
            /**
             * [onDown]
             * @param currentProgress
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onDown(currentProgress: Int): Boolean
    
            /**
             * [onMove]
             * @param currentProgress
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onMove(currentProgress: Int): Boolean
    
            /**
             * [onCancel]
             * @param currentProgress
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onCancel(currentProgress: Int): Boolean
        }
    
        fun interface OnProgressChangeListener {
            /**
             * [onChange]
             * @param new [Int]
             * @param isUser [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onChange(new: Int, isUser: Boolean)
        }
    
    }
    
    /** constructors **/
    /**
     * @param context [Context]
     **/
    constructor(context: Context): this(context, null)
    
    /**
     * @param context [Context]
     * @param attributeSet [AttributeSet]
     **/
    constructor(context: Context, attributeSet: AttributeSet?):
        this(context, attributeSet, 0)
    
    /**
     * @param context [Context]
     * @param attributeSet [AttributeSet]
     * @param defStyleAttr [Int]
     **/
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int):
        this(context, attributeSet, defStyleAttr, 0)
    
    /**
     * @param context [Context]
     * @param attributeSet [AttributeSet]
     * @param defStyleAttr [Int]
     * @param defStyleRes [Int]
     **/
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        super(context, attributeSet, defStyleAttr, defStyleRes)
    
    /**
     * [isUserTouching]
     * @author 1552980358
     * @since 0.1
     **/
    var isUserTouching = false
    
    /**
     * [listener]
     * @author 1552980358
     * @since 0.1
     **/
    var listener: OnProgressChangeListener? = null
    
    /**
     * [paint]
     * @author 1552980358
     * @since 0.1
     **/
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.seekingBar_paint_width)
        textSize = resources.getDimension(R.dimen.seekingBar_text_textSize)
        // textAlign = Paint.Align.CENTER
    }
    
    /**
     * [progress]
     * @author 1552980358
     * @since 0.1
     **/
    var progress = 0
        set(value) {
            if (value < 0) {
                throw Exception()
            }
            field = value
            postInvalidate()
            listener?.onChange(field, isUserTouching)
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
    
    /**
     * [textDrawMethod]
     * @author 1552980358
     * @since 0.1
     **/
    var textDrawMethod = DrawFull
        set(value) {
            field = value
            postInvalidate()
        }
    
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
     * [thumbProgress]
     * @author 1552980358
     * @since 0.1
     **/
    var thumbProgress = 0F
    
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
        Rect().apply {
            paint.getTextBounds(TEXT_ZERO, 0, 5, this)
            widthText = width().toFloat()
            baseline = height().toFloat()
        }
    
        // Applied for the parameter `isUser` of `OnProgressChangeListener`
        // 为了让`OnProgressChangeListener`的参数 `isUser` 能正确获取
        setOnTouchListener(null as OnTouchListener?)
    }
    
    /**
     * [onDraw]
     * @param canvas [Canvas]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
    
        paint.style = Paint.Style.STROKE
    
        thumbProgress =
            (progress * (width - ((widthText + textPadding + textPadding) * 2))) / maximum + (widthText + textPadding + textPadding)
    
        /*
        if ((progress == 0 && maximum == 0) || (thumbProgress < paint.strokeWidth * 2)) {
        
            paint.color = indeterminateColor
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        
            drawText(canvas)
            return
        }
        
        if ((progress == maximum) || (width - thumbProgress < paint.strokeWidth * 2)) {
            paint.color = progressColor
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
            drawText(canvas)
            return
        }
    
        // Draw left side
        // 绘制左方
        paint.color = progressColor
        canvas.drawRect(0F, 0F, thumbProgress - paint.strokeWidth / 2, height.toFloat(), paint)
    
        // Draw right side
        // 绘制右方
        paint.color = indeterminateColor
        canvas.drawRect(thumbProgress + paint.strokeWidth / 2, 0F, width.toFloat(), height.toFloat(), paint)
        */
    
        paint.color = indeterminateColor
        canvas.drawRect(thumbProgress, 0F, width.toFloat(), height.toFloat(), paint)
    
        paint.color = progressColor
        canvas.drawRect(0F, 0F, thumbProgress, height.toFloat(), paint)
    
        drawText(canvas)
    
    }
    
    /**
     * [drawText]
     * @param canvas [Canvas]
     * @author 1552980358
     * @since 0.1
     **/
    private fun drawText(canvas: Canvas) {
        // Not to draw text
        // 不绘制文字
        if (!drawText) {
            return
        }
    
        paint.style = Paint.Style.FILL
        // Draw text
        // 绘制文字
        paint.color = progressColor
        canvas.drawText(getTimeText(progress), textPadding, (height + baseline) / 2, paint)
    
        paint.color = indeterminateColor
        when (textDrawMethod) {
            DrawFull -> {
                canvas.drawText(
                    getTimeText(maximum),
                    width - textPadding - widthText,
                    (height + baseline) / 2,
                    paint
                )
            }
            DrawRemain -> {
                canvas.drawText(
                    getTimeText(maximum - progress),
                    width - textPadding - widthText,
                    (height + baseline) / 2,
                    paint
                )
            }
        }
    }
    
    /**
     * [setOnProgressChangeListener]
     * @param l [OnProgressChangeListener]
     * @author 1552980358
     * @since 0.1
     **/
    fun setOnProgressChangeListener(l: OnProgressChangeListener?) {
        this.listener = l
    }
    
    /**
     * [setOnTouchListener]
     * @param l [OnTouchListener]
     * @author 1552980358
     * @since 0.1
     **/
    fun setOnTouchListener(l: OnTouchListener?) {
        isUserTouching = false
        super.setOnTouchListener { _, motion ->
            @Suppress("LABEL_NAME_CLASH")
            return@setOnTouchListener when (motion.action) {
                MotionEvent.ACTION_DOWN -> {
                    // logE("setOnTouchListener", "ACTION_DOWN")
                    isUserTouching = true
                    // progress = (motion.x / width * maximum).toInt()
                    progress = when {
                        motion.x < widthText + textPadding -> 0
                        motion.x > (width - textPadding - widthText) -> maximum
                        else -> ((motion.x - (textPadding + widthText)) /
                            (width - 2 * (textPadding + widthText + textPadding)) * maximum).toInt()
                    }
                    l?.onDown(progress) ?: true
                }
                MotionEvent.ACTION_MOVE -> {
                    // logE("setOnTouchListener", "ACTION_MOVE")
                    // progress = (motion.x / width * maximum).toInt()
                    progress = when {
                        motion.x < widthText + textPadding -> 0
                        motion.x > (width - textPadding - widthText) -> maximum
                        else -> ((motion.x - (textPadding + widthText)) /
                            (width - 2 * (textPadding + widthText + textPadding)) * maximum).toInt()
                    }
                    l?.onMove(progress) ?: true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // logE("setOnTouchListener", "ACTION_UP")
                    // progress = (motion.x / width * maximum).toInt()
                    progress = when {
                        motion.x < widthText + textPadding -> 0
                        motion.x > (width - textPadding - widthText) -> maximum
                        else -> ((motion.x - (textPadding + widthText)) /
                            (width - 2 * (textPadding + widthText + textPadding)) * maximum).toInt()
                    }
                    isUserTouching = false
                    l?.onCancel(progress) ?: true
                }
                else -> true
            }
        }
    }
    
}