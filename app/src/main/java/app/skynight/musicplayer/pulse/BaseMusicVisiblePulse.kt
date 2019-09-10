package app.skynight.musicplayer.pulse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.graphics.Paint

/**
 * @File    : ElectronicCurrentPulse
 * @Author  : 1552980358
 * @Date    : 25 Aug 2019
 * @TIME    : 12:23 PM
 **/

@SuppressLint("ViewConstructor")
open class BaseMusicVisiblePulse(context: Context, width: Int, height: Int, type: Paint.Style, thickness: Float = 0F, private val point: Boolean) : View(context) {

    @Suppress("JoinDeclarationAndAssignment")
    var paint: Paint

    lateinit var waveData: ByteArray
    lateinit var points: FloatArray

    var mHeight = 0f
    var mWidth = 0f

    val eachWidth by lazy { mWidth / waveData.size }
    //val heightRatio by lazy { mHeight / 128 }
    val heightRatio by lazy { mHeight / 128 }

    var start = true

    init {
        this.mWidth = width.toFloat()
        this.mHeight = height.toFloat()
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = type
            if (thickness > 0) strokeWidth = thickness
        }
    }

    fun setData(data: ByteArray) {
        if (start) {
            waveData = data//BaseUtil.treatBytes(data)
            if (point && !::points.isInitialized) {
                points = FloatArray(waveData.size * 4)
            }
            postInvalidate()
        }
    }

    @Suppress("unused")
    fun setPaintThickness(thickness: Float) = run { paint.strokeWidth = thickness }

    @Synchronized
    fun setPaintColor(color: Int) {
        start = false
        paint.color = color
        postInvalidate()
        start = true
    }

    fun isInitialized(): Boolean {
        return ::waveData.isInitialized
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawColor(Color.TRANSPARENT)
    }
}