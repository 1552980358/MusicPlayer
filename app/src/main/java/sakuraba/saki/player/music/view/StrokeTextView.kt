package sakuraba.saki.player.music.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.FILL_AND_STROKE
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import sakuraba.saki.player.music.R

class StrokeTextView: AppCompatTextView {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private var contentColor: Int
    private var strokeColor: Int

    init {
        contentColor = textColors.defaultColor
        strokeColor = WHITE
        paint.strokeWidth = resources.getDimension(R.dimen.stroke_text_view_stroke_width)
    }

    private var isDrawingStroke = false

    fun setContentColor(@ColorInt newColor: Int) {
        contentColor = newColor
        invalidate()
    }

    fun setStrokeColor(@ColorInt newColor: Int) {
        strokeColor = newColor
        invalidate()
    }

    override fun invalidate() {
        if (isDrawingStroke)
            return
        super.invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        if (!isDrawingStroke) {
            isDrawingStroke = true
            setTextColor(strokeColor)
            paint.style = FILL_AND_STROKE
            super.onDraw(canvas)
            setTextColor(contentColor)
            paint.style = FILL
            super.onDraw(canvas)
            isDrawingStroke = false
        }
    }

}