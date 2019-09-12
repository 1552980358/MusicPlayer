package app.skynight.musicplayer.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.fragment.activity_main.MainFragment
import app.skynight.musicplayer.fragment.activity_main.PlayListFragment
import app.skynight.musicplayer.util.FragmentPagerAdapter
import app.skynight.musicplayer.util.NotificationUtil
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log
import kotlinx.android.synthetic.main.activity_main.viewPager
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.tabLayout

class MainActivity : BaseSmallPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        log("MainActivity", "onCreate")
        if (Player.settings[Player.Theme] != Player.Theme_0) {
            setTheme(R.style.AppTheme_NoActionBar_Theme1)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.theme1_colorPrimary)
        } else {
            window.navigationBarColor = Color.WHITE
        }
        super.onCreate(savedInstanceState)
        val title = arrayOf(R.string.adb_main_main_title, R.string.adb_main_list_title)

        //log("MainActivity", "- setContentView")
        setContentView(R.layout.activity_main)
        setPlayerActivityFitsSystemWindows()

        NotificationUtil.getNotificationUtil

        //log("MainActivity", "supportActionBar")
        setSupportActionBar(toolbar)

        viewPager.apply {
            //log("MainActivity", "ViewPager Adapter")

            adapter = FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                arrayListOf(MainFragment(), PlayListFragment())
            )

            tabLayout.apply {
                log("MainActivity", "setupWithViewPager")
                setupWithViewPager(viewPager)
                getTabAt(0)!!.setIcon(R.drawable.ic_tab_music)
                getTabAt(1)!!.setIcon(R.drawable.ic_tab_play_list)
            }

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    toolbar.setTitle(title[position])
                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        log("MainActivity", "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        super.onOptionsItemSelected(item)
        log("MainActivity", "onOptionsItemSelected")
        when (item!!.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    override fun onResume() {
        log("MainActivity", "onResume")
        super.onResume()
    }

    override fun onBackPressed() {
        log("MainActivity", "onBackPressed")
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        })
    }
}
