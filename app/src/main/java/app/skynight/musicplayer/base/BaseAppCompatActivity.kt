package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    fun setContentView(view: View?, params: RelativeLayout.LayoutParams?) {
        setContentView(view.apply {
            this!!.layoutParams = params
        })
    }
    override fun setContentView(layoutResID: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null, false))
    }
    override fun setContentView(view: View?) {
        super.setContentView(RelativeLayout(this).apply {
            background =
                if (MainApplication.customize) MainApplication.bgDrawable else ContextCompat.getDrawable(
                    this@BaseAppCompatActivity,
                    R.color.activity_background
                )
            addView((view as ViewGroup).apply {
                background =
                    ContextCompat.getDrawable(this@BaseAppCompatActivity, R.color.color_filter)
            })
        })
    }
}