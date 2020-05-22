package app.github1552980358.android.musicplayer.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import app.github1552980358.android.musicplayer.R

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
         * [DEFAULT_VALUE]
         * @author 1552980358
         * @since 0.1
         **/
        const val DEFAULT_VALUE = -1F
    
        /**
         * [OnTouchListener]
         * @author 1552980358
         * @since 0.1
         **/
        fun interface OnTouchListener {
            /**
             * [onTouch]
             * @param letter [String]
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            fun onTouch(letter: String): Boolean
        }
        
    }
    
    /**
     * [letterHeight]
     * @author 1552980358
     * @since 0.1
     **/
    private var letterHeight = DEFAULT_VALUE
    /**
     * [blockHeight]
     * @author 1552980358
     * @since 0.1
     **/
    private var blockHeight = DEFAULT_VALUE
    
    /**
     * [drawY]
     * @author 1552980358
     * @since 0.1
     **/
    private var drawY = DEFAULT_VALUE
    
    /**
     * [letters]
     * @author 1552980358
     * @since 0.1
     **/
    private val letters = arrayOf(
        "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    
    /**
     * [paint]
     * @author 1552980358
     * @since 0.1
     **/
    private val paint = Paint().apply {
        isAntiAlias = true
        //typeface = Typeface.DEFAULT_BOLD
        // 15sp
        textSize = resources.getDimension(R.dimen.sideLetterView_textSize)
        style = Paint.Style.FILL
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
        // Draw background
        // 绘制背景
        paint.color = Color.BLACK
        
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
        
        canvas.drawBitmap(
            context.getDrawable(R.drawable.bg_side_letter_view)?.toBitmap(width, height)!!,
            null,
            RectF(0F, 0F, width.toFloat(), height.toFloat()),
            paint
        )
        
        // height of letters
        // 字符高度
        if (letterHeight == DEFAULT_VALUE) {
            val rect = Rect()
            paint.getTextBounds(letters[1], 0, 0, rect)
            letterHeight = rect.bottom.toFloat() - rect.top
        }
        
        // Block height of each block containing each letter
        // 每个字符占用高度
        if (blockHeight == DEFAULT_VALUE) {
            blockHeight = height.toFloat() / letters.size
        }
        
        // Starting y-axis pixel of drawing letter
        // 开始绘制字符的y轴像素格
        if (drawY == DEFAULT_VALUE) {
            drawY = (blockHeight - letterHeight) / 2
        }
        
        // Draw letters
        // 绘制字母
        paint.color = Color.WHITE
        for ((i, j) in letters.withIndex()) {
            canvas.drawText(j, (width - paint.measureText(j)) / 2, blockHeight * i + drawY, paint)
        }
        
    }
    
    /**
     * [setOnTouchListener]
     * @param l [OnTouchListener]
     * @author 1552980358
     * @since 0.1
     **/
    fun setOnTouchListener(l: OnTouchListener) {
        super.setOnTouchListener { _, motionEvent ->
            @Suppress("LABEL_NAME_CLASH")
            return@setOnTouchListener l.onTouch(letters[(motionEvent.y / letterHeight).toInt()])
        }
    }
    
    /**
     * [setOnTouchListener]
     * @param l [View.OnTouchListener]
     * @author 1552980358
     * @since 0.1
     **/
    override fun setOnTouchListener(l: View.OnTouchListener?) {
        throw IllegalAccessError()
    }
    
}