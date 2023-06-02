package projekt.cloud.piece.cloudy.ui.fragment.library.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import projekt.cloud.piece.cloudy.ui.fragment.library.BaseLibraryChildFragment
import projekt.cloud.piece.cloudy.ui.fragment.library.album.AlbumLibraryFragment
import projekt.cloud.piece.cloudy.ui.fragment.library.artist.ArtistLibraryFragment
import projekt.cloud.piece.cloudy.util.implementation.ListUtil.mutableList

/**
 * [PagerLibraryPagerAdapter]
 * @extends [androidx.viewpager2.adapter.FragmentStateAdapter]
 * @param fragment [androidx.fragment.app.Fragment]
 * @param fragments [List]{[androidx.fragment.app.Fragment]}
 **/
class PagerLibraryPagerAdapter private constructor(
    fragment: Fragment,
    private val fragments: List<Fragment>
): FragmentStateAdapter(fragment) {

    /**
     * [PagerLibraryPagerAdapter]
     * @param fragment [androidx.fragment.app.Fragment]
     **/
    constructor(fragment: Fragment): this(
        fragment, extractLibraryChildFragments(fragment)
    )

    private companion object {

        /**
         * [PagerLibraryPagerAdapter.extractLibraryChildFragments]
         * @param fragment [androidx.fragment.app.Fragment]
         * @return [List]{[androidx.fragment.app.Fragment]}
         *
         * Extract [BaseLibraryChildFragment]s and add missing [BaseLibraryChildFragment] from [fragment]
         **/
        private fun extractLibraryChildFragments(fragment: Fragment): List<Fragment> {
            val childFragments = filterBaseLibraryChildFragment(fragment)
            return when {
                childFragments.isEmpty() -> { libraryChildFragments }
                else -> combineLibraryChildFragments(childFragments)
            }
        }

        /**
         * [PagerLibraryPagerAdapter.libraryChildFragments]
         * @type [List]{[androidx.fragment.app.Fragment]}
         **/
        private val libraryChildFragments: List<Fragment>
            get() = listOf(
                ArtistLibraryFragment(),
                AlbumLibraryFragment()
            )

        /**
         * [PagerLibraryPagerAdapter.filterBaseLibraryChildFragment]
         * @param fragment
         * @return [List]{[BaseLibraryChildFragment]}
         **/
        private fun filterBaseLibraryChildFragment(
            fragment: Fragment
        ): List<BaseLibraryChildFragment<*>> {
            return fragment.childFragmentManager
                .fragments
                .filterIsInstance<BaseLibraryChildFragment<*>>()
        }

        /**
         * [PagerLibraryPagerAdapter.combineLibraryChildFragments]
         * @param existsChildFragments [List]{[BaseLibraryChildFragment]}
         * @return [List]{[Fragment]}
         **/
        private fun combineLibraryChildFragments(
            existsChildFragments: List<Fragment>
        ): List<Fragment> {
            return mutableList { mutableList ->
                mutableList += getLibraryChildFragment(existsChildFragments, ::ArtistLibraryFragment)
                mutableList += getLibraryChildFragment(existsChildFragments, ::AlbumLibraryFragment)
            }
        }

        /**
         * [PagerLibraryPagerAdapter.getLibraryChildFragment]
         *   @typeParam [T] extends [BaseLibraryChildFragment]
         * @param existsChildFragments [List]{[BaseLibraryChildFragment]}
         * @param creator [kotlin.jvm.functions.Function1]{[Unit], [T]}
         * @return [T]
         **/
        private inline fun <reified T: BaseLibraryChildFragment<*>> getLibraryChildFragment(
            existsChildFragments: List<Fragment>, creator: () -> T
        ): T {
            return getLibraryChildFragment(existsChildFragments) ?: creator.invoke()
        }

        /**
         * [PagerLibraryPagerAdapter.getLibraryChildFragment]
         *   @typeParam [T]{[BaseLibraryChildFragment]}
         * @param existsChildFragments [List] {[BaseLibraryChildFragment]}
         * @return [T]
         **/
        private inline fun <reified T: BaseLibraryChildFragment<*>> getLibraryChildFragment(
            existsChildFragments: List<Fragment>
        ): T? {
            return existsChildFragments.find { it is T } as? T
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