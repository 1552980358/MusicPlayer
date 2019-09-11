package app.skynight.musicplayer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import app.skynight.musicplayer.util.getPx
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.log
import java.lang.Exception

/**
 * @File    : LrcView
 * @Author  : 1552980358
 * @Date    : 10 Sep 2019
 * @TIME    : 3:27 PM
 **/

class LrcView(context: Context) : View(context) {

    private var initDone = false
    private val paintCurrent by lazy { Paint() }
    private val paintOther by lazy { Paint() }
    private val rect by lazy { Rect() }
    private var arrayList = arrayListOf<String>()
    //private val map = mapOf<String, Any>(CurrentLine to " ", NextLine to " ", NextTime to -1)
    private val lines = arrayListOf<String>()
    private val times = arrayListOf<Int>()
    private var pointer = 0

    init {
        paintCurrent.apply {
            textSize = resources.getDimension(R.dimen.playerActivity_lyric_size)
            isAntiAlias = true
            color = Color.WHITE
        }
        paintOther.apply {
            textSize = resources.getDimension(R.dimen.playerActivity_lyric_size)
            isAntiAlias = true
            color = Color.parseColor("#80FFFFFF")
        }
    }

    @Synchronized
    fun updateLrc(list: ArrayList<String>) {
        initDone = false
        log("updateLrc", list)
        arrayList = arrayListOf<String>().apply {
            list.forEach {
                if (it.isNotEmpty()) this.add(it)
            }
        }

        var first = -1
        for ((i, j) in list.withIndex()) {
            if (j[1] in 'a'..'z' || j[1] in 'A'..'Z' || j[0] != '[') continue

            first = i
            break
        }
        if (first == -1) return

        lines.clear()
        times.clear()
        for (i in first..list.lastIndex) {
            val line = list[i]
            if (line.isEmpty() || !line.startsWith('[')) continue

            lines.add(line.substring(line.lastIndexOf(']') + 1))
            times.add((line.substring(1, 3).toInt() * 60 * 1000).apply { } + (line.substring(
                4,
                6
            ).toInt() * 1000) + (line.substring(7, 9).toInt() * 10))
        }
        initDone = true
        postInvalidate()
    }

    fun removeAllLines() {
        lines.clear()
        times.clear()
        postInvalidate()
    }

    @Synchronized
    fun checkTime(current: Int) {
        if (!initDone) return

        for ((i, j) in times.withIndex()) {
            if (current > j) continue

            pointer = i - 1
            break
        }

        postInvalidate()
    }

    fun updateCurrentColor(color: Int) {
        paintCurrent.color = color
        postInvalidate()
    }

    fun updateOtherColor(color: Int) {
        paintOther.color = color
        postInvalidate()
    }

    fun updateColors(current: Int, other: Int) {
        paintCurrent.color = current
        paintOther.color = other
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas!!)

        if (lines.isEmpty()) return
        if (!initDone) return

        var height = 0
        if (pointer <= 0) {
            paintOther.getTextBounds(" ", 0, 1, rect)
            canvas.drawText(
                " ",
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat(),
                paintOther
            )

            height += rect.height().plus(getPx(2))

            paintCurrent.getTextBounds(lines[0], 0, lines[0].length, rect)
            canvas.drawText(
                lines[0],
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat() + height,
                paintCurrent
            )

            height += rect.height().plus(getPx(2))
            paintOther.getTextBounds(lines[1], 0, lines[1].length, rect)
            canvas.drawText(
                lines[1],
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat() + height,
                paintCurrent
            )

            return
        }

        try {
            paintOther.getTextBounds(lines[pointer - 1], 0, lines[pointer - 1].length, rect)
            canvas.drawText(
                lines[pointer - 1],
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat(),
                paintOther
            )

            height += rect.height().plus(getPx(2))

            paintCurrent.getTextBounds(lines[pointer], 0, lines[pointer].length, rect)
            canvas.drawText(
                lines[pointer],
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat() + height,
                paintCurrent
            )

            height += rect.height().plus(getPx(2))
            paintOther.getTextBounds(lines[pointer + 1], 0, lines[pointer + 1].length, rect)
            canvas.drawText(
                lines[pointer + 1],
                (resources.displayMetrics.widthPixels - rect.width()) / 2F,
                rect.height().toFloat() + height,
                paintOther
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}