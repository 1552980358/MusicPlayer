package projekt.cloud.piece.music.player.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterLinearlayoutManager: LinearLayoutManager {

    constructor(context: Context): super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean): super(context, orientation, reverseLayout)
    constructor(context: Context, attributeSet: AttributeSet, orientation: Int, defStyleRes: Int): super(context, attributeSet, orientation, defStyleRes)
    
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller = CenterSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
    
    private class CenterSmoothScroller(context: Context): LinearSmoothScroller(context) {
        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int) =
            (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }
    
}