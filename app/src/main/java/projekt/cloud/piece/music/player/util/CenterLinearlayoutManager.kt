package projekt.cloud.piece.music.player.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterLinearlayoutManager(private val context: Context): LinearLayoutManager(context) {
    
    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        CenterSmoothScroller(context).also {
            it.targetPosition = position
            startSmoothScroll(it)
        }
    }
    
    private class CenterSmoothScroller(context: Context): LinearSmoothScroller(context) {
        
        companion object {
            private const val TIME_SPENT_SCROLLING = 400
        }
        
        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int) =
            (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    
        override fun calculateTimeForScrolling(dx: Int) = TIME_SPENT_SCROLLING
        
    }

}