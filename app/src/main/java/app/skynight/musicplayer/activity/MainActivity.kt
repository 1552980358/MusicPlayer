package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import com.google.android.material.appbar.AppBarLayout

class MainActivity : BaseSmallPlayerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // startActivity(Intent(this, PlayerActivity::class.java))

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(AppBarLayout(this@MainActivity).apply {
                setTheme(R.style.AppTheme_AppBarOverlay)
                //stateListAnimator = null
                addView(Toolbar(this@MainActivity).apply {
                    popupTheme = R.style.AppTheme_PopupOverlay

                }, AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            })
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
