package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
//import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.fragment.activity_main.MainFragment
import app.skynight.musicplayer.fragment.activity_main.PlayListFragment
import app.skynight.musicplayer.util.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseSmallPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = arrayOf(R.string.adb_main_main_title, R.string.adb_main_list_title)

        setContentView(R.layout.activity_main)
        if (MainApplication.customize) {
            appBarLayout.stateListAnimator = null
        }

        setSupportActionBar(toolbar)

        viewPager.apply {
            adapter = FragmentPagerAdapter(supportFragmentManager, arrayListOf(MainFragment(), PlayListFragment()))
            tabLayout.apply {
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
                    /*
                    try {
                        (toolbar::class.java.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(toolbar) as TextView).setText(title[position])
                    } catch (e: Exception) {
                        //
                    }
                     */
                    toolbar.setTitle(title[position])
                }

            })
        }
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        val toolbar: Toolbar
        val title = arrayOf(R.string.adb_main_main_title, R.string.adb_main_list_title)
        super.onCreate(savedInstanceState)
        // startActivity(Intent(this, PlayerActivity::class.java))
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val tabLayout: TabLayout
            addView(AppBarLayout(this@MainActivity).apply {
                //setTheme(R.style.AppTheme_AppBarOverlay)
                if (MainApplication.customize) stateListAnimator = null

                addView(RelativeLayout(this@MainActivity).apply {

                    addView(Toolbar(this@MainActivity).apply {
                        toolbar = this
                        setTitle(R.string.adb_main_main_title)
                        setSupportActionBar(this)
                    }, RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

                    addView(TabLayout(context).apply {
                        tabLayout = this
                        tabMode = TabLayout.MODE_SCROLLABLE
                        setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.black
                            )
                        )
                    }, RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                        addRule(RelativeLayout.CENTER_HORIZONTAL)
                    })

                }, AppBarLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

/*
                addView(Toolbar(context).apply {
                    addView(TabLayout(context).apply {
                        tabLayout = this
                        tabMode = TabLayout.MODE_SCROLLABLE
                        setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.black
                            )
                        )
                    }, Toolbar.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                        gravity = Gravity.CENTER
                    })
                }, AppBarLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

 */
                /*
                addView(TabLayout(context).apply {
                    tabLayout = this
                    gravity = Gravity.CENTER
                    tabMode = TabLayout.MODE_SCROLLABLE
                    setSelectedTabIndicatorColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                }, AppBarLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))

                 */
            }, LinearLayout.LayoutParams(MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.toolbar_height)))

            addView(ViewPager(this@MainActivity).apply {
                //Log.e("MainActivity", "viewPager")
                val viewPager = this
                id = View.generateViewId()
                adapter = FragmentPagerAdapter(supportFragmentManager, arrayListOf(MainFragment(), PlayListFragment()))
                tabLayout.apply {
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
                        /*
                        try {
                            (toolbar::class.java.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(toolbar) as TextView).setText(title[position])
                        } catch (e: Exception) {
                            //
                        }
                         */
                        toolbar.setTitle(title[position])
                    }

                })
            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        })
        setFitSystemWindows()
    }
*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Log.e("MainActivity", "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        //Log.e("MainActivity", "onOptionsItemSelected")
        when (item!!.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    override fun onBackPressed() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        })
    }
}
