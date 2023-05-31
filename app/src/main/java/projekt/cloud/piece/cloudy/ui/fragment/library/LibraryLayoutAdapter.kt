package projekt.cloud.piece.cloudy.ui.fragment.library

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentLibraryBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM

typealias LibraryLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentLibraryBinding, LibraryLayoutAdapter>
private typealias LibraryLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentLibraryBinding, LibraryLayoutAdapter>

/**
 * [LibraryLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [FragmentLibraryBinding]
 * @param binding [FragmentLibraryBinding]
 *
 * @impl [LibraryLayoutAdapter.CompatImpl], [LibraryLayoutAdapter.W600dpImpl], [LibraryLayoutAdapter.W1240dpImpl]
 **/
abstract class LibraryLayoutAdapter(
    binding: FragmentLibraryBinding
): BaseLayoutAdapter<FragmentLibraryBinding>(binding) {

    companion object {

        /**
         * [LibraryLayoutAdapter.builder]
         * @type [LibraryLayoutAdapterBuilder]
         **/
        val builder: LibraryLayoutAdapterBuilder
            get() = ::builder

        /**
         * [LibraryLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [LibraryLayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): LibraryLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                MEDIUM -> ::W600dpImpl
                EXPANDED -> ::W1240dpImpl
            }
        }

    }

    /**
     * [LibraryLayoutAdapter.tabs]
     * @type [com.google.android.material.tabs.TabLayout]
     * @layout [R.layout.fragment_library]
     * @id [R.id.tab_layout]
     **/
    private val tabs: TabLayout
        get() = binding.tabLayout
    /**
     * [LibraryLayoutAdapter.viewPager]
     * @type [androidx.viewpager2.widget.ViewPager2]
     * @layout [R.layout.fragment_library]
     * @id [R.id.view_pager]
     **/
    private val viewPager: ViewPager2
        get() = binding.viewPager

    /**
     * [LibraryLayoutAdapter.setupViewPager]
     * @param fragment [androidx.fragment.app.Fragment]
     *
     * Setup [R.id.view_pager] with [LibraryViewPagerAdapter]
     **/
    fun setupViewPager(fragment: Fragment) {
        viewPager.adapter = LibraryViewPagerAdapter.newInstance(fragment)
    }

    /**
     * [LibraryLayoutAdapter.setupTabs]
     * @param resources [android.content.res.Resources]
     *
     * Setup [R.id.tab_layout]
     **/
    fun setupTabs(resources: Resources) {
        setupTabLabels(
            resources.getStringArray(R.array.library_tabs)
        )
    }

    /**
     * [LibraryLayoutAdapter.setupTabLabels]
     * @param tabLabels [Array]<[String]>
     *
     * Implement of [LibraryLayoutAdapter.setupTabs]
     **/
    private fun setupTabLabels(tabLabels: Array<String>) {
        TabLayoutMediator(tabs, viewPager) { tab, index ->
            tab.text = tabLabels[index]
        }.attach()
    }

    /**
     * [LibraryLayoutAdapter.CompatImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

    /**
     * [LibraryLayoutAdapter.W600dpImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W600dpImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

    /**
     * [LibraryLayoutAdapter.W1240dpImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W1240dpImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

}