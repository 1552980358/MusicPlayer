package app.skynight.musicplayer.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @FILE:   ViewPagerAdapter
 * @AUTHOR: 1552980358
 * @DATE:   22 Jul 2019
 * @TIME:   10:45 AM
 **/

class FragmentPagerAdapter(fragmentManager: FragmentManager,private val fragmentList: ArrayList<Fragment>): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}