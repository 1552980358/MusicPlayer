package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import java.lang.Exception

/**
 * @FILE:   BaseAppCompatActivity
 * @AUTHOR: 1552980358
 * @DATE:   25 Jul 2019
 * @TIME:   10:04 PM
 **/

@SuppressLint("Registered")
open class BaseAppCompatActivity : AppCompatActivity() {

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
            relativeLayout = this
            background =
                if (MainApplication.customize) MainApplication.bgDrawable else ContextCompat.getDrawable(
                    this@BaseAppCompatActivity, R.color.activity_background
                )
            addView((view as ViewGroup).apply {
                background =
                    ContextCompat.getDrawable(this@BaseAppCompatActivity, R.color.color_filter)
            })
        })
    }

    private lateinit var relativeLayout: RelativeLayout
    fun setBaseActivityFitsSystemWindow() {
        try {
            relativeLayout.fitsSystemWindows = true
        } catch (e: Exception) {
            throw Exception("LayoutNotInitException")
        }
    }
}