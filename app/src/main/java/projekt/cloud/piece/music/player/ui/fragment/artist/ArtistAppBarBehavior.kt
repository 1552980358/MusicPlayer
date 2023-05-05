package projekt.cloud.piece.music.player.ui.fragment.artist

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.android.material.appbar.AppBarLayout

class ArtistAppBarBehavior(
    context: Context, attributeSet: AttributeSet?
): AppBarLayout.Behavior(context, attributeSet) {

    private var isAppBarContentExpanded = false

    fun setupIsAppBarContentExpandedListener(
        fragment: Fragment, isAppBarContentExpandedLiveData: LiveData<Boolean>
    ) {
        isAppBarContentExpandedLiveData.observe(fragment.viewLifecycleOwner) { isAppBarContentExpanded ->
            this.isAppBarContentExpanded = isAppBarContentExpanded
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        abl.setExpanded(isAppBarContentExpanded)
    }

}