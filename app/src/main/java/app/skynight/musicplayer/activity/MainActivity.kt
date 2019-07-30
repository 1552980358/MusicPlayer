package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.fragment.activity_main.MusicFragment
import app.skynight.musicplayer.fragment.activity_main.PlayListFragment
import app.skynight.musicplayer.util.FragmentPagerAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

class MainActivity : BaseSmallPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // startActivity(Intent(this, PlayerActivity::class.java))
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val tabLayout: TabLayout
            addView(AppBarLayout(this@MainActivity).apply {
                setTheme(R.style.AppTheme_AppBarOverlay)
                //stateListAnimator = null
                /*
                addView(Toolbar(this@MainActivity).apply {
                    popupTheme = R.style.AppTheme_PopupOverlay

                }, AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                 */
                addView(TabLayout(context).apply {
                    tabLayout = this
                    gravity = Gravity.CENTER
                    tabMode = TabLayout.MODE_SCROLLABLE
                    setSelectedTabIndicatorColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.tab_indicator_selected
                        )
                    )
                }, AppBarLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addView(ViewPager(this@MainActivity).apply {
                val viewPager = this
                id = View.generateViewId()
                adapter = FragmentPagerAdapter(supportFragmentManager, arrayListOf(MusicFragment(), PlayListFragment()))
                tabLayout.apply {
                    setupWithViewPager(viewPager)
                    getTabAt(0)!!.setIcon(R.drawable.ic_tab_music)
                    getTabAt(1)!!.setIcon(R.drawable.ic_tab_play_list)
                }
            }, LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        })
    }

    override fun onBackPressed() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        })
    }
}
