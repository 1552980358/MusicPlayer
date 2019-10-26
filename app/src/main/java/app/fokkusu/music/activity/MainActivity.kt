package app.fokkusu.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import app.fokkusu.music.base.activity.BaseAppCompatActivity
import app.fokkusu.music.R
import app.fokkusu.music.fragment.main.HomeFragment
import app.fokkusu.music.fragment.main.MusicFragment
import app.fokkusu.music.fragment.main.SettingFragment
import kotlinx.android.synthetic.main.activity_main.bottomPlayerView
import kotlinx.android.synthetic.main.activity_main.fab
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
        
        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
        
        val onClickListener0 = View.OnClickListener {
            startActivity(Intent(this, SearchMusicActivity::class.java))
        }
        
        val onClickListener1 = View.OnClickListener {
            viewPager.setCurrentItem(1, true)
        }
        
        viewPager.apply {
            currentItem = 1
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    if (positionOffset == 0.0f && (position == 2 || position == 0)) {
                        fab.alpha = 1F
                        return
                    }
                    if (position == 1) {
                        fab.alpha = positionOffset
                        return
                    }
                    fab.alpha = 1 - positionOffset
                }
                
                @SuppressLint("RestrictedApi")
                override fun onPageSelected(position: Int) {
                    title = titles[position]
                    
                    if (position == 2) {
                        fab.setImageResource(R.drawable.ic_fab_search)
                        fab.setOnClickListener(onClickListener0)
                        return
                    }
                    if (position == 0) {
                        fab.setImageResource(R.drawable.ic_fab_right)
                        fab.setOnClickListener(onClickListener1)
                        return
                    }
                    
                    fab.setImageDrawable(null)
                }
            })
        }
        
        bottomPlayerView.setParentActivity(this)
    }
    
    override fun onBackPressed() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        })
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
