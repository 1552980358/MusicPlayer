package sakuraba.saki.player.music.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.view.View
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF

class VisualizerView: View {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private var isDrawing = false

    var visibleData: FloatArray? = null
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint()

    init {
        paint.apply {
            color = BLACK
            style = FILL
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        if (isDrawing) {
            return
        }
        val data = visibleData ?: return
        isDrawing = true

        val xDiff = widthF / data.size
        val yDiff = heightF / Byte.MAX_VALUE

        var drawY: Float

        data.forEachIndexed { index, value ->
            drawY = yDiff * value
            canvas.drawRect(index * xDiff, (height - drawY) / 2, (index + 1) * xDiff, (height + drawY) / 2, paint)
        }

        isDrawing = false
    }

}