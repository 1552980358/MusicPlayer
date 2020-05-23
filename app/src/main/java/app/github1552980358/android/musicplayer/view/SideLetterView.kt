package app.github1552980358.android.musicplayer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.DEFAULT_VALUE_FLOAT

/**
 * [SideLetterView]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/5/21
 * @time    : 19:42
 **/

class SideLetterView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {
    
    companion object {

        /**
         * [OnTouchEventListener]
         * @author 1552980358
         * @since 0.1
         **/
        fun interface OnTouchEventListener {

            /**
             * [onNewCharSelected]
             * @param newChar [Char]
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onNewCharSelected(newChar: Char): Boolean
            
        }
        
    }
    
    /**
     * [letterHeight]
     * @author 1552980358
     * @since 0.1
     **/
    private var letterHeight = DEFAULT_VALUE_FLOAT
    
    /**
     * [blockHeight]
     * @author 1552980358
     * @since 0.1
     **/
    private var blockHeight = DEFAULT_VALUE_FLOAT
    
    /**
     * [drawY]
     * @author 1552980358
     * @since 0.1
     **/
    private var drawY = DEFAULT_VALUE_FLOAT

    /**
     * [letters]
     * @author 1552980358
     * @since 0.1
     **/
    private val letters = arrayOf(
        '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    )

    /**
     * [selected]
     * @author 1552980358
     * @since 0.1
     **/
    private var selected = letters.first()
    
    /**
     * [selectedPaint]
     * @author 1552980358
     * @since 0.1
     **/
    private val selectedPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        // 15sp
        textSize = resources.getDimension(R.dimen.sideLetterView_textSize)
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    /**
     * [strokePaint]
     * @author 1552980358
     * @since 0.1
     **/
    private val strokePaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        // 15sp
        textSize = resources.getDimension(R.dimen.sideLetterView_textSize)
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }
    
    /**
     * [onDraw]
     * @param canvas [Canvas]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        
        /**
         * A white space is found between semicircle and cylinder
         * 以下绘制方法会导致半圆与柱体间有一道白色缝隙
         *
         * // Arc at top
         * // 顶部弧度
         * var rectF = RectF(0F, 0F, width.toFloat(), width.toFloat())
         * canvas.drawArc(rectF, 0F, -180F, false, paint)
         * // Cylinder in center
         * // 中间柱体
         * canvas.drawRect(0F, width.toFloat() / 2, width.toFloat(), height.toFloat() - width.toFloat() / 2, paint)
         * // Arc at bottom
         * // 底部弧度
         * rectF = RectF(0F, height.toFloat() - width.toFloat(), width.toFloat(), height.toFloat())
         * canvas.drawArc(rectF, 0F, 180F, false, paint)
         **/
        /**
         * canvas.drawBitmap(
         *   context.getDrawable(R.drawable.bg_side_letter_view)?.toBitmap(width, height)!!,
         *   null,
         *   RectF(0F, 0F, width.toFloat(), height.toFloat()),
         *   paint
         * )
         **/

        // Height of each letter
        if (letterHeight == DEFAULT_VALUE_FLOAT) {
            val rect = Rect()
            selectedPaint.getTextBounds(letters[1].toString(), 0, 0, rect)
            letterHeight = rect.bottom.toFloat() - rect.top
        }
    
        // Block height of each block containing each letter
        // 每个字符占用高度
        if (blockHeight == DEFAULT_VALUE_FLOAT) {
            blockHeight = height.toFloat() / letters.size
        }
    
        // Start point of y-axis pixel of drawing letter
        // 开始绘制字符的y轴像素格
        if (drawY == DEFAULT_VALUE_FLOAT) {
            drawY = (blockHeight - letterHeight) / 2
        }

        // Draw content
        // 绘制内容
        for ((i, j) in letters.withIndex()) {
            if (selected == j) {
                // Draw selected letter
                // 绘制已选中字母
                canvas.drawText(j.toString(), (width - selectedPaint.measureText(j.toString())) / 2, blockHeight * i + drawY, selectedPaint)
                continue
            }

            // Draw un-selected letter
            // 绘制未选择字母
            canvas.drawText(j.toString(), (width - strokePaint.measureText(j.toString())) / 2, blockHeight * i + drawY, strokePaint)
        }
    }
    
    /**
     * [setOnTouchListener]
     * @param listener [View.OnTouchListener]
     * @author 1552980358
     * @since 0.1
     **/
    fun setOnTouchListener(listener: OnTouchEventListener) {
        super.setOnTouchListener { _, motionEvent ->
            Log.e("motionEvent", motionEvent.y.toString())
            Log.e("motionEvent", blockHeight.toString())
            @Suppress("LABEL_NAME_CLASH")
            return@setOnTouchListener listener.onNewCharSelected(
                letters[(motionEvent.y / blockHeight).run { if (this <= letters.lastIndex) this else letters.lastIndex }.toInt()]
            )
        }
    }
    
    /**
     * [updatePosition]
     * @param newLetter [Char]
     * @author 1552980358
     * @since 0.1
     **/
    fun updatePosition(newLetter: Char) {
        selected = newLetter
        postInvalidate()
    }
    
}