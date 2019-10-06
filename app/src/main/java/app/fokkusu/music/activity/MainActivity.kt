package app.fokkusu.music.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import app.fokkusu.music.Application
import app.fokkusu.music.base.activity.BaseAppCompatActivity
import app.fokkusu.music.R
import app.fokkusu.music.fragment.main.HomeFragment
import app.fokkusu.music.fragment.main.MusicFragment
import app.fokkusu.music.fragment.main.SettingFragment
import kotlinx.android.synthetic.main.activity_main.bottomPlayerView
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val fragments = arrayOf(SettingFragment(), HomeFragment(), MusicFragment())
        val titles = resources.getStringArray(R.array.array_main_tab)
        
        toolbar.title = titles[1]
        setSupportActionBar(toolbar)
        
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }
    
            override fun getCount(): Int {
                return fragments.size
            }
        }
        
        tabLayout.apply {
            setupWithViewPager(viewPager)
            getTabAt(0)!!.setIcon(R.drawable.ic_tab_setting)
            getTabAt(1)!!.setIcon(R.drawable.ic_tab_home)
            getTabAt(2)!!.setIcon(R.drawable.ic_tab_music)
        }
        viewPager.apply {
            currentItem = 1
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }
    
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }
    
                override fun onPageSelected(position: Int) {
                    toolbar.title = titles[position]
                }
            })
        }
    }
    
    override fun onResume() {
        super.onResume()
        bottomPlayerView.onResume()
    }
    
    override fun onDestroy() {
        bottomPlayerView.onDestroy()
        super.onDestroy()
    }
    
    override fun onPause() {
        super.onPause()
        bottomPlayerView.onPause()
    }
}
