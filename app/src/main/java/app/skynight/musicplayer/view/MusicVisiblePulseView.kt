package app.skynight.musicplayer.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint
import android.graphics.Rect
import app.skynight.musicplayer.util.BaseUtil
import me.bogerchan.niervisualizer.util.clear
import kotlin.math.abs

/**
 * @File    : MusicVisiblePulseView
 * @Author  : 1552980358
 * @Date    : 25 Aug 2019
 * @TIME    : 12:23 PM
 **/

@SuppressLint("ViewConstructor")
class MusicVisiblePulseView(
    context: Context,
    width: Int,
    height: Int,
    paintColor: Int = Color.WHITE
) : View(context) {

    @Suppress("JoinDeclarationAndAssignment")
    private var paint: Paint

    private lateinit var waveData: ByteArray

    private var mHeight = 0f
    private var mWidth = 0f

    private val eachWidth by lazy { mWidth / waveData.size }

    var start = true

    init {
        this.mWidth = width.toFloat()
        this.mHeight = height.toFloat()
        paint = Paint().apply {
            isAntiAlias = true
            color = paintColor
            style = Paint.Style.FILL
        }
    }

    fun setWaveData(data: ByteArray) {
        if (start) {
            waveData = BaseUtil.treatBytes(data)
            postInvalidate()
        }
    }

    @Synchronized
    fun setPaintColor(color: Int) {
        start = false
        paint.color = color
        postInvalidate()
        start = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawColor(Color.TRANSPARENT)
        if (!::waveData.isInitialized) {
            return
        }

        var offsetX = 0f
        for (i in waveData) {
            val hei = abs(if (i.toInt() == -128) 0 else i.toInt())
            canvas.drawRect(offsetX, mHeight - hei, offsetX + eachWidth, mHeight, paint)
            offsetX+=eachWidth
        }
    }
}