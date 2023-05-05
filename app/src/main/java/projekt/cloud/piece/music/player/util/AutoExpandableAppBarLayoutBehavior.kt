package projekt.cloud.piece.music.player.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.android.material.appbar.AppBarLayout

class AutoExpandableAppBarLayoutBehavior(
    context: Context, attributeSet: AttributeSet?
): AppBarLayout.Behavior(context, attributeSet) {

    private var isContentExpanded = false
    fun setObserver(fragment: Fragment, isExpandedLiveData: LiveData<Boolean>) {
        isExpandedLiveData.observe(fragment.viewLifecycleOwner) { isExpanded ->
            isContentExpanded = isExpanded
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        abl.setExpanded(isContentExpanded)
    }

}