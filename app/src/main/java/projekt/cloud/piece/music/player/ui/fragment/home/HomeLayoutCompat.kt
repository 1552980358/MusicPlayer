package projekt.cloud.piece.music.player.ui.fragment.home

import android.graphics.Rect
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.appbar.AppBarLayout
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.util.ViewUtil.canScrollUp

private interface HomeInterface {

    // Compat limited method
    fun setupRecyclerViewAction(fragment: Fragment) = Unit

}

open class HomeLayoutCompat: BaseLayoutCompat<FragmentHomeBinding>, HomeInterface {

    @Suppress("unused")
    constructor(): super(null)
    constructor(binding: FragmentHomeBinding): super(binding)

    override val compatImpl: KClass<*>
        get() = CompatImpl::class
    override val w600dpImpl: KClass<*>
        get() = W600dpImpl::class
    override val w1240dpImpl: KClass<*>
        get() = W1240dpImpl::class

    override val requireWindowInsets: Boolean
        get() = true

    private class CompatImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout!!

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            appBarLayout.updatePadding(top = insets.top)
        }

        override fun setupRecyclerViewAction(fragment: Fragment) {
            val homeViewModel: HomeViewModel by fragment.navGraphViewModels(R.id.nav_graph_main_host)
            var isIdle = false
            recyclerView.addOnScrollListener(
                object: OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        isIdle = newState == SCROLL_STATE_IDLE

                        val isOnTop = !recyclerView.canScrollUp
                        if (homeViewModel.isOnTop != isOnTop) {
                            homeViewModel.updateTopState(isOnTop)
                        }
                        if (isOnTop && isIdle) {
                            resetAppBarLayoutOffset()
                        }
                    }
                }
            )

            homeViewModel.observeScrollToTop(fragment.viewLifecycleOwner) { scrollToTop ->
                if (scrollToTop && recyclerView.canScrollUp && isIdle) {
                    recyclerView.smoothScrollToPosition(0)
                }
            }
        }

        private fun resetAppBarLayoutOffset() {
            appBarLayout.offsetTopAndBottom(-1)
        }

    }

    private class W600dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val coordinatorLayout: CoordinatorLayout
            get() = binding.coordinatorLayout

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            coordinatorLayout.updatePadding(bottom = insets.bottom)
        }

    }

    private class W1240dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val coordinatorLayout: CoordinatorLayout
            get() = binding.coordinatorLayout

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            coordinatorLayout.updatePadding(bottom = insets.bottom)
        }

    }

    protected val recyclerView: RecyclerView
        get() = binding.recyclerView

    fun setupRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    private var playMediaWithId: ((String) -> Unit)? = null
    fun setPlayMediaWithId(block: ((String) -> Unit)) {
        playMediaWithId = block
    }
    fun invokePlayWithId(id: String) {
        playMediaWithId?.invoke(id)
    }

}