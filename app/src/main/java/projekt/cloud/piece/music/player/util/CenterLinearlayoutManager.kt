package projekt.cloud.piece.music.player.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterLinearlayoutManager: LinearLayoutManager {
    
    private val context: Context
    
    constructor(context: Context): super(context) {
        this.context = context
    }
    
    constructor(context: Context, @RecyclerView.Orientation orientation: Int, reverseLayout: Boolean)
        : super(context, orientation, reverseLayout) {
        this.context = context
    }
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
        : super(context, attributeSet, defStyleAttr, defStyleRes) {
        this.context = context
    }
    
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