package projekt.cloud.piece.music.player.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior

class BottomPlayBarBehavior<V: View>: HideBottomViewOnScrollBehavior<V> {
    
    constructor(): super()
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    private var isAllowMoving = false
    
    fun setEnableAllowMoving(child: V, requireSlideUp: Boolean) {
        if (!isAllowMoving) {
            isAllowMoving = true
            if (requireSlideUp) {
                // Reset state
                slideDown(child, false)
                // Show it
                slideUp(child)
            }
        }
    }
    
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout,
                                child: V,
                                target: View,
                                dxConsumed: Int,
                                dyConsumed: Int,
                                dxUnconsumed: Int,
                                dyUnconsumed: Int,
                                type: Int,
                                consumed: IntArray) {
        if (isAllowMoving) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        }
    }

}