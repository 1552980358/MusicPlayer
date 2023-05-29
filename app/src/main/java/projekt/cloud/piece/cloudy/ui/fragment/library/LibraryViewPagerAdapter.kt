package projekt.cloud.piece.cloudy.ui.fragment.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * [LibraryViewPagerAdapter]
 * @extends [androidx.viewpager2.adapter.FragmentStateAdapter]
 * @param fragment [androidx.fragment.app.Fragment]
 * @param fragments [List]<[androidx.fragment.app.Fragment]>
 **/
class LibraryViewPagerAdapter private constructor(
    fragment: Fragment,
    private val fragments: List<Fragment>
): FragmentStateAdapter(fragment) {

    companion object {

        /**
         * [LibraryViewPagerAdapter.libraryChildFragments]
         * @type [List]<[androidx.fragment.app.Fragment]>
         **/
        private val libraryChildFragments: List<Fragment>
            get() = listOf(
                // TODO: To be filled
            )

        /**
         * [LibraryViewPagerAdapter.extractLibraryChildFragments]
         * @param fragment [androidx.fragment.app.Fragment]
         * @return [List]<[androidx.fragment.app.Fragment]>
         *
         * Extract [BaseLibraryChildFragment]s and add missing [BaseLibraryChildFragment] from [fragment]
         **/
        private fun extractLibraryChildFragments(fragment: Fragment): List<Fragment> {
            // TODO: To be implemented
            return libraryChildFragments
        }

        /**
         * [LibraryViewPagerAdapter.newInstance]
         * @param fragment [androidx.fragment.app.Fragment]
         * @return [LibraryViewPagerAdapter]
         **/
        fun newInstance(fragment: Fragment): LibraryViewPagerAdapter {
            return LibraryViewPagerAdapter(
                fragment, extractLibraryChildFragments(fragment)
            )
        }

    }

    /**
     * [FragmentStateAdapter.createFragment]
     * @param position [Int]
     * @return [androidx.fragment.app.Fragment]
     **/
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    /**
     * [FragmentStateAdapter.getItemCount]
     * @return [Int]
     **/
    override fun getItemCount(): Int {
        return fragments.size
    }

}