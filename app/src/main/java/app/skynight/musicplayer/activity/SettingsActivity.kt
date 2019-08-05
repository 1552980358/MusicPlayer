package app.skynight.musicplayer.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.widget.Toolbar
import app.skynight.musicplayer.base.BaseAppCompatActivity
import app.skynight.musicplayer.fragment.activity_settings.SettingsFragment
import com.google.android.material.appbar.AppBarLayout
import app.skynight.musicplayer.R

class SettingsActivity : BaseAppCompatActivity() {
    private lateinit var frameLayout: FrameLayout

    private fun createView(): View {
        return LinearLayout(this).apply {
            fitsSystemWindows = true
            orientation = VERTICAL
            addView(AppBarLayout(this@SettingsActivity).apply {
                addView(Toolbar(this@SettingsActivity).apply {
                    setTitle(R.string.abc_settings_title)
                    setSupportActionBar(this)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    setNavigationOnClickListener {
                        finish()
                    }
                }, AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            })
            addView(FrameLayout(this@SettingsActivity).apply {
                frameLayout = this
                id = View.generateViewId()
                supportFragmentManager.beginTransaction().add(id, SettingsFragment()).commit()
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }
}
