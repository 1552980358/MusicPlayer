package projekt.cloud.piece.cloudy.ui.fragment.library.pager

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.databinding.LibraryPagerBinding
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

/**
 * [PagerLibraryFragment]
 * @extends [BaseFragment]
 *   @typeParam [LibraryPagerBinding]
 **/
class PagerLibraryFragment: BaseFragment<LibraryPagerBinding>() {

    /**
     * [BaseFragment.viewBindingInflater]
     * @return [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<LibraryPagerBinding>
        get() = LibraryPagerBinding::inflate

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [LibraryPagerBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: LibraryPagerBinding, savedInstanceState: Bundle?) {
        setupViewPager(binding.viewPager)
        setupTabLayout(binding.tabLayout, binding.viewPager)
    }

    /**
     * [PagerLibraryFragment.setupViewPager]
     * @param viewPager [androidx.viewpager2.widget.ViewPager2]
     **/
    private fun setupViewPager(viewPager: ViewPager2) {
        viewPager.adapter = PagerLibraryPagerAdapter(this)
    }

    /**
     * [PagerLibraryFragment.setupTabLayout]
     * @param tabLayout [com.google.android.material.tabs.TabLayout]
     * @param viewPager [androidx.viewpager2.widget.ViewPager2]
     **/
    private fun setupTabLayout(tabLayout: TabLayout, viewPager: ViewPager2) {
        setupTabs(
            tabLayout,
            viewPager,
            resources.getStringArray(R.array.library_tabs)
        )
    }

    /**
     * [PagerLibraryFragment.setupTabs]
     * @param tabLayout [com.google.android.material.tabs.TabLayout]
     * @param viewPager [androidx.viewpager2.widget.ViewPager2]
     * @param tabs [Array]{[String]}
     **/
    private fun setupTabs(tabLayout: TabLayout, viewPager: ViewPager2, tabs: Array<String>) {
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = tabs[index]
        }.attach()
    }

}