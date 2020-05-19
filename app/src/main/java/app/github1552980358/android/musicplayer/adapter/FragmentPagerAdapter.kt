package app.github1552980358.android.musicplayer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @file    : [FragmentPagerAdapter]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:40
 **/

class FragmentPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>):
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    
    /**
     * [getItem]
     * @param position [Int]
     * @return [Fragment]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
    
    /**
     * [getCount]
     * @return [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getCount(): Int {
        return fragments.size
    }
    
}