package app.skynight.musicplayer.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.base.BaseAppCompatActivity
import app.skynight.musicplayer.fragment.activity_settings.SettingsFragment
import com.google.android.material.appbar.AppBarLayout
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.AppCompatActivity
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.BROADCAST_APPLICATION_RESTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.util.makeToast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseAppCompatActivity() {
    //private lateinit var frameLayout: FrameLayout

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
                    popupTheme = R.style.AppTheme_PopupOverlay
                }, AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            })
            addView(FrameLayout(this@SettingsActivity).apply {
                //frameLayout = this
                id = View.generateViewId()
                supportFragmentManager.beginTransaction().add(id, SettingsFragment()).commit()
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Player.settings[Player.Theme] != Player.Theme_0) {
            setTheme(R.style.AppTheme_NoActionBar_Theme1_Settings)
        }
        log("SettingsActivity", "onCreate")
        super.onCreate(savedInstanceState)
        //setContentView(createView())
        setContentView(R.layout.activity_settings)
        toolbar.apply {
            navigationIcon =
                ContextCompat.getDrawable(this@SettingsActivity, R.drawable.ic_arrow_back).apply {
                    this!!.setTint(Player.ThemeTextColor)
                }
            setTitleTextColor(Player.ThemeTextColor)

            setSupportActionBar(this)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener {
                finish()
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, SettingsFragment()).commit()
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                startActivity(Intent().apply {
                    action = Intent.ACTION_MAIN
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    addCategory(Intent.CATEGORY_HOME)
                })
                finish()
            }
        }, IntentFilter(BROADCAST_APPLICATION_RESTART))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        log("SettingsActivity", "onBackPressed")
    }

}
