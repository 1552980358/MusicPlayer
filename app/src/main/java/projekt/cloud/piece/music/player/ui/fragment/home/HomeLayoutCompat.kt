package projekt.cloud.piece.music.player.ui.fragment.home

import android.graphics.Rect
import androidx.annotation.Keep
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.appbar.AppBarLayout
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.ui.fragment.mainHost.MainHostViewModel
import projekt.cloud.piece.music.player.util.ViewUtil.canScrollUp

abstract class HomeLayoutCompat(binding: FragmentHomeBinding): BaseLayoutCompat<FragmentHomeBinding>(binding) {

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

    // Compat limited method
    open fun setupRecyclerViewAction(fragment: Fragment) = Unit

    // Compat limited method
    open fun setupRecyclerViewBottomMargin(fragment: Fragment) = Unit

    @Keep
    private class CompatImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout!!

        override val requireWindowInsets: Boolean
            get() = true

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

        override fun setupRecyclerViewBottomMargin(fragment: Fragment) {
            val mainHostViewModel: MainHostViewModel by fragment.navGraphViewModels(
                R.id.nav_graph_main_host
            )
            mainHostViewModel.bottomMargin.observe(fragment.viewLifecycleOwner) { bottomInsets ->
                recyclerView.updatePadding(bottom = bottomInsets)
            }
        }

    }

    @Keep
    private class W600dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

    }

    @Keep
    private class W1240dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

    }

}