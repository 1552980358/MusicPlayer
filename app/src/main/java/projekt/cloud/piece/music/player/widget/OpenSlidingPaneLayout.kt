package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class OpenSlidingPaneLayout(
    context: Context, attributeSet: AttributeSet?
): SlidingPaneLayout(context, attributeSet) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Disable intercepting when not opened
        return isOpen && super.onInterceptTouchEvent(ev)
    }

}