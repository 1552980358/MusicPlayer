package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R

/**
 * @FILE:   BaseAppCompatActivity
 * @AUTHOR: 1552980358
 * @DATE:   25 Jul 2019
 * @TIME:   10:04 PM
 **/
 
@SuppressLint("Registered")
open class BaseAppCompatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flags = if (MainApplication.customize) {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var relativeLayout: RelativeLayout

    override fun setContentView(view: View?) {
        super.setContentView(DrawerLayout(this).apply {
            drawerLayout = this
            background = MainApplication.bgDrawable!!
            addView(
                RelativeLayout(this@BaseAppCompatActivity).apply {
                    relativeLayout = this
                    addView(
                        RelativeLayout(this@BaseAppCompatActivity).apply {
                            background = ColorDrawable(if (MainApplication.customize) ContextCompat.getColor(this@BaseAppCompatActivity, R.color.transparent) else ContextCompat.getColor(this@BaseAppCompatActivity, app.skynight.musicplayer.R.color.activity_background))
                            addView(view!!, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                        }, RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ))
                }, DrawerLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ))
        })
    }
}