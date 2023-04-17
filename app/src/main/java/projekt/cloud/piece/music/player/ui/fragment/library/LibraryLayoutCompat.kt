package projekt.cloud.piece.music.player.ui.fragment.library

import android.content.res.Resources
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding

abstract class LibraryLayoutCompat(
    binding: FragmentLibraryBinding
): BaseLayoutCompat<FragmentLibraryBinding>(binding) {

    protected val tabLayout: TabLayout
        get() = binding.tabLayout
    private val viewPager2: ViewPager2
        get() = binding.viewPager2

    fun setupTabLayout(resources: Resources) {
        resources.getStringArray(R.array.library_tab_labels).let { label ->
            TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
                tab.text = label[pos]
            }.attach()
        }
    }

    fun setupViewPager2(fragment: Fragment) {
        viewPager2.adapter = LibraryFragmentStateAdapter(fragment)
    }

    @Keep
    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

    @Keep
    private class W600dpImpl(
        binding: FragmentLibraryBinding
    ): LibraryLayoutCompat(binding), SurfaceColorsInterface {

        override val requireSurface2Color: Boolean
            get() = true

        override fun onSurface2ColorObtained(color: Int) {
            tabLayout.setBackgroundColor(color)
        }

    }

    @Keep
    private class W1240dpImpl(binding: FragmentLibraryBinding): LibraryLayoutCompat(binding)

}