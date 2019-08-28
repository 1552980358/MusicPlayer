package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import app.skynight.musicplayer.MainApplication

/**
 * @File    : AppCompatActivity
 * @Author  : 1552980358
 * @Date    : 10 Aug 2019
 * @TIME    : 8:23 PM
 **/

@SuppressLint("Registered")
open class AppCompatActivity: androidx.appcompat.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flags = if (MainApplication.customize) {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.WHITE
    }
}