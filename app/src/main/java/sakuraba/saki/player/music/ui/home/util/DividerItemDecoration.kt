package sakuraba.saki.player.music.ui.home.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import sakuraba.saki.player.music.R

class DividerItemDecoration: RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(0, 0, 0, view.resources.getDimensionPixelSize(R.dimen.home_recycler_view_divider))
    }
    
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.childCount <= 0) {
            return
        }
        paint.strokeWidth = parent.resources.getDimension(R.dimen.home_recycler_view_divider)
        paint.color = ContextCompat.getColor(parent.context, R.color.divider_color)
        val startX = parent.resources.getDimension(R.dimen.home_recycler_view_height)
        repeat(parent.childCount - 1) { count ->
            parent.getChildAt(count).bottom.toFloat().also { bottom ->
                c.drawLine(startX, bottom, parent.resources.displayMetrics.widthPixels.toFloat(), bottom, paint)
            }
        }
    }
}