package projekt.cloud.piece.music.player.ui.fragment.library

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNull
import projekt.cloud.piece.music.player.util.KotlinUtil.to
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

        private const val INDEX_ARTIST_LIBRARY = 0
        private const val INDEX_ALBUM_LIBRARY = INDEX_ARTIST_LIBRARY + 1

    }

    private val tabLayout: TabLayout
        get() = binding.tabLayout
    protected val viewPager2: ViewPager2
        get() = binding.viewPager2

    override val requireSurface2Color: Boolean
        get() = true

    override fun onSurface2ColorObtained(color: Int) {
        tabLayout.setBackgroundColor(color)
    }

    open fun setupReturnAnimation(fragment: Fragment) = Unit

    fun setupTabLayout(resources: Resources) {
        resources.getStringArray(R.array.library_tab_labels).let { label ->
            TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
                tab.text = label[pos]
            }.attach()
        }
    }

    fun setupViewPager2(fragment: Fragment) {
        viewPager2.adapter.ifNull {
            LibraryFragmentStateAdapter(fragment).let { adapter ->
                viewPager2.adapter = adapter
                viewPager2.offscreenPageLimit = adapter.itemCount
            }
            if (viewPager2.isNotEmpty()) {
                viewPager2.getChildAt(0)
                    .tryTo<RecyclerView>()
                    ?.overScrollMode = OVER_SCROLL_NEVER
            }
        }
    }

    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding) {

        private val root: ConstraintLayout
            get() = binding.constraintLayoutRoot

        override fun onSurface2ColorObtained(color: Int) {
            super.onSurface2ColorObtained(color)
            root.setBackgroundColor(color)
        }

        override fun setupReturnAnimation(fragment: Fragment) {
            setupReturnAnimation(fragment, fragment.getString(R.string.library_transition))
        }

        private fun setupReturnAnimation(fragment: Fragment, requestKey: String) {
            fragment.setFragmentResultListener(requestKey) { _, bundle ->
                // Pause transition
                fragment.postponeEnterTransition()

                when (bundle.getString(requestKey)) {
                    fragment.getString(R.string.library_transition_artist) -> {
                        setupReturnAnimation(fragment, INDEX_ARTIST_LIBRARY, bundle)
                    }
                    fragment.getString(R.string.library_transition_album) -> {
                        setupReturnAnimation(fragment, INDEX_ALBUM_LIBRARY, bundle)
                    }
                }

                // Clear listener
                fragment.clearFragmentResultListener(requestKey)
            }
        }

        private fun setupReturnAnimation(fragment: Fragment, index: Int, bundle: Bundle) {
            fragment.setExitSharedElementCallback(
                object: SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: MutableList<String>, sharedElements: MutableMap<String, View>
                    ) {
                        // Clear self first
                        fragment.setExitSharedElementCallback(null)
                        // Map view
                        if (names.isNotEmpty()) {
                            names.first().let { transitionName ->
                                sharedElements[transitionName] = getItemView(
                                    index, fragment, bundle, transitionName
                                )
                            }
                        }
                    }
                }
            )
        }

        private fun getItemView(
            index: Int, fragment: Fragment, bundle: Bundle, transitionName: String
        ): View {
            return getLibraryFragment(index)
                .findItemViewOfPos(getItemPos(bundle, fragment))
                .also { view -> view.transitionName = transitionName }
        }

        private fun getLibraryFragment(index: Int): BaseLibraryObjectFragment<*> {
            return viewPager2.adapter
                .to<LibraryFragmentStateAdapter>()[index]
        }

        private fun getItemPos(bundle: Bundle, fragment: Fragment): Int {
            return bundle.getInt(fragment.getString(R.string.library_transition_pos))
        }

    }

    private class W600dpImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

    private class W1240dpImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

}