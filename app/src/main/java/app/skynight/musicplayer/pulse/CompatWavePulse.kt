package app.skynight.musicplayer.pulse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import app.skynight.musicplayer.util.getPx

/**
 * @File    : CompatWavePulse
 * @Author  : 1552980358
 * @Date    : 30 Aug 2019
 * @TIME    : 6:00 PM
 **/

@SuppressLint("ViewConstructor")
class CompatWavePulse(context: Context, width: Int, height: Int): BaseMusicVisiblePulse(context, width, height, Paint.Style.STROKE, getPx(2).toFloat()) {

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
        var tmp: Float
        for (i in waveData) {
            tmp = (mHeight - i.toInt()) / 2
            path.moveTo(offsetX, tmp)
            path.lineTo(offsetX, tmp + i)
            offsetX+=eachWidth
        }
        canvas!!.drawPath(path, paint)
    }
}