package app.skynight.musicplayer.pulse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import app.skynight.musicplayer.util.getPx
import kotlin.math.abs

/**
 * @File    : ElectronicCurrentPulse
 * @Author  : 1552980358
 * @Date    : 25 Aug 2019
 * @TIME    : 12:23 PM
 **/

@SuppressLint("ViewConstructor")
class ElectronicCurrentPulse(
    context: Context, width: Int, height: Int
) : BaseMusicVisiblePulse(context, width, height, Paint.Style.FILL, getPx(2).toFloat(), true) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isInitialized()) {
            canvas!!.drawLine(0f, mHeight, mWidth, mHeight, paint)
            return
        }

        var offsetX = 0f
        for (i in 0 until waveData.size - 1) {
            points[i * 4] = offsetX
            points[i * 4 + 1] = abs(waveData[i] * heightRatio)
            offsetX+=eachWidth
            points[i * 4 + 2] = offsetX
            offsetX+=eachWidth
            points[i * 4 + 3] = abs(waveData[i + 1] * heightRatio)
        }
        canvas!!.drawLines(points, paint)
    }
}