package projekt.cloud.piece.music.player.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

abstract class BasePagerViewModel<F: BaseFragment>: ViewModel(), List<F> {

    @Suppress("LeakingThis")
    private val fragmentList = listOf(*setFragments())

    abstract fun setFragments(): Array<F>

    override val size: Int = fragmentList.size

    override fun contains(element: F) = fragmentList.contains(element)

    override fun containsAll(elements: Collection<F>) = fragmentList.containsAll(elements)

    override fun get(index: Int) = fragmentList[index]

    override fun indexOf(element: F) = fragmentList.indexOf(element)

    override fun isEmpty() = fragmentList.isEmpty()

    override fun iterator() = fragmentList.iterator()

    override fun lastIndexOf(element: F) = fragmentList.lastIndexOf(element)

    override fun listIterator() = fragmentList.listIterator()

    override fun listIterator(index: Int) = fragmentList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<F> {
        throw IllegalAccessError("BasePagerViewModel: subList() is not allowed")
    }

    fun setUpViewPager2(fragment: Fragment, viewPager2: ViewPager2) {
        viewPager2.adapter = object: FragmentStateAdapter(fragment) {
            override fun getItemCount() = size
            override fun createFragment(position: Int) = fragmentList[position]
        }
        fragmentList.isEmpty()
    }


}