package app.skynight.musicplayer.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseAppCompatActivity
import app.skynight.musicplayer.fragment.activity_intro.FirstFragment
import app.skynight.musicplayer.fragment.activity_intro.LastFragment
import app.skynight.musicplayer.fragment.activity_intro.SecondFragment
import app.skynight.musicplayer.util.FragmentPagerAdapter
import app.skynight.musicplayer.util.PageTransformer
import app.skynight.musicplayer.util.log

/**
 * @FILE:   IntroActivity
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   10:17 AM
 **/

class IntroActivity : BaseAppCompatActivity() {
    private lateinit var viewPager: ViewPager
    @Suppress("LocalVariableName")
    private fun createView(): View {
        return DrawerLayout(this).apply {
            background =
                ColorDrawable(ContextCompat.getColor(this@IntroActivity, R.color.colorPrimaryDark))
            val fragmentArrayList =
                arrayListOf(FirstFragment(), SecondFragment(), LastFragment().apply {
                    activity = this@IntroActivity
                })

            addView(RelativeLayout(this@IntroActivity).apply {
                fitsSystemWindows = true
                val relativeLayout: RelativeLayout
                val textView_next: AppCompatTextView
                val textView_last: AppCompatTextView
                addView(
                    RelativeLayout(this@IntroActivity).apply {
                        relativeLayout = this
                        id = View.generateViewId()

                        addView(AppCompatTextView(this@IntroActivity).apply {
                            textView_next = this
                            isClickable = true
                            isFocusable = true
                            textSize = resources.getDimension(R.dimen.introActivity_indicator_size)
                            setTextColor(
                                ContextCompat.getColor(
                                    this@IntroActivity,
                                    android.R.color.white
                                )
                            )
                            background = ContextCompat.getDrawable(
                                this@IntroActivity,
                                R.drawable.ripple_effect
                            )
                            for (i in 0 until fragmentArrayList.size) {
                                append("▶")
                            }
                            setPadding(resources.getDimensionPixelSize(R.dimen.introActivity_indicator_padding))
                        }, LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                            addRule(ALIGN_PARENT_END)
                            addRule(CENTER_VERTICAL)
                        })
                        addView(AppCompatTextView(this@IntroActivity).apply {
                            textView_last = this
                            isClickable = true
                            isFocusable = true
                            textSize = resources.getDimension(R.dimen.introActivity_indicator_size)
                            setTextColor(
                                ContextCompat.getColor(
                                    this@IntroActivity,
                                    android.R.color.white
                                )
                            )
                            background = ContextCompat.getDrawable(
                                this@IntroActivity,
                                R.drawable.ripple_effect
                            )
                            setPadding(resources.getDimensionPixelSize(R.dimen.introActivity_indicator_padding))
                        }, LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                            addRule(CENTER_VERTICAL)
                        })
                    },
                    LayoutParams(
                        MATCH_PARENT,
                        resources.getDimensionPixelSize(R.dimen.introActivity_bottomBar_height)
                    ).apply {
                        addRule(ALIGN_PARENT_BOTTOM)
                    })

                addView(ViewPager(this@IntroActivity).apply {
                    id = View.generateViewId()
                    background = ColorDrawable(
                        ContextCompat.getColor(
                            this@IntroActivity, R.color.colorPrimary
                        )
                    )
                    viewPager = this
                    fitsSystemWindows = true
                    adapter = FragmentPagerAdapter(
                        supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArrayList
                    )
                    textView_next.setOnClickListener {
                        if (viewPager.currentItem != fragmentArrayList.size) {
                            viewPager.currentItem = viewPager.currentItem + 1
                        } else {
                            //
                        }
                    }
                    textView_last.setOnClickListener {
                        if (viewPager.currentItem != 0) {
                            viewPager.currentItem = viewPager.currentItem - 1
                        }
                    }
                    setPageTransformer(true, PageTransformer())
                    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {
                        }

                        override fun onPageScrolled(
                            position: Int, positionOffset: Float, positionOffsetPixels: Int
                        ) {
                        }

                        override fun onPageSelected(position: Int) {
                            textView_next.text = ""
                            textView_last.text = ""
                            for (i in 0 until fragmentArrayList.size - position) {
                                textView_next.append("▶")
                            }
                            for (i in 0 until position) {
                                textView_last.append("◀")
                            }
                        }
                    })
                }, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                    addRule(ABOVE, relativeLayout.id)
                })
            }, DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        log("IntroActivity", "- onCreate")
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }
}