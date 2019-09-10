package app.skynight.musicplayer.pulse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import app.skynight.musicplayer.util.getPx
import kotlin.math.abs

/**
 * @File    : VerticalColumnPulse
 * @Author  : 1552980358
 * @Date    : 31 Aug 2019
 * @TIME    : 11:18 AM
 **/

@SuppressLint("ViewConstructor")
class VerticalColumnPulse(context: Context, width: Int, height: Int):
    BaseMusicVisiblePulse(context, width, height, Paint.Style.STROKE, getPx(2).toFloat(), false) {

    @Suppress("JoinDeclarationAndAssignment")
    private var path: Path

    init {
        path = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!isInitialized()) {
            canvas!!.drawLine(0f, mHeight / 2, mWidth, mHeight / 2, paint)
            return
        }

        if (paint.strokeWidth != eachWidth) {
            paint.strokeWidth = eachWidth
        }

        var offsetX = 0F
        path.reset()
        for (i in waveData) {
            path.moveTo(offsetX, mHeight - abs(i * heightRatio))
            path.lineTo(offsetX, mHeight)
            offsetX += eachWidth
        }
        canvas!!.drawPath(path, paint)
    }
}