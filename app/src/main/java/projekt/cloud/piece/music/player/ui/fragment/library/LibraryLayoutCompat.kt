package projekt.cloud.piece.music.player.ui.fragment.library

import android.content.res.Resources
import android.view.View.OVER_SCROLL_NEVER
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding
import projekt.cloud.piece.music.player.ui.fragment.mainHost.MainHostViewModel
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM

abstract class LibraryLayoutCompat(
    binding: FragmentLibraryBinding
): BaseLayoutCompat<FragmentLibraryBinding>(binding), SurfaceColorsInterface {

    companion object LibraryLayoutCompatUtil {

        fun inflate(screenDensity: ScreenDensity, binding: FragmentLibraryBinding): LibraryLayoutCompat {
            return when (screenDensity) {
                COMPACT -> CompatImpl(binding)
                MEDIUM -> W600dpImpl(binding)
                EXPANDED -> W1240dpImpl(binding)
            }
        }

    }

    private val tabLayout: TabLayout
        get() = binding.tabLayout
    private val viewPager2: ViewPager2
        get() = binding.viewPager2

    override val requireSurface2Color: Boolean
        get() = true

    override fun onSurface2ColorObtained(color: Int) {
        tabLayout.setBackgroundColor(color)
    }

    /**
     * [setupRootBottomMargin]
     * @param fragment [Fragment]
     * For Compat only
     **/
    open fun setupRootBottomMargin(fragment: Fragment) = Unit

    fun setupTabLayout(resources: Resources) {
        resources.getStringArray(R.array.library_tab_labels).let { label ->
            TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
                tab.text = label[pos]
            }.attach()
        }
    }

    fun setupViewPager2(fragment: Fragment) {
        viewPager2.adapter = LibraryFragmentStateAdapter(fragment)
        if (viewPager2.isNotEmpty()) {
            viewPager2.getChildAt(0)
                .tryTo<RecyclerView>()
                ?.overScrollMode = OVER_SCROLL_NEVER
        }
    }

    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding) {

        private val root: ConstraintLayout
            get() = binding.constraintLayoutRoot

        override fun onSurface2ColorObtained(color: Int) {
            super.onSurface2ColorObtained(color)
            root.setBackgroundColor(color)
        }

        override fun setupRootBottomMargin(fragment: Fragment) {
            val mainHostViewModel: MainHostViewModel by fragment.navGraphViewModels(
                R.id.nav_graph_main_host
            )
            mainHostViewModel.bottomMargin.observe(fragment.viewLifecycleOwner) { bottomInsets ->
                root.updatePadding(bottom = bottomInsets)
            }
        }

    }

    private class W600dpImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

    private class W1240dpImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

}