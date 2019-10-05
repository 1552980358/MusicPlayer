package app.fokkusu.music.base.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * @File    : BaseAppCompatActivity
 * @Author  : 1552980358
 * @Date    : 5 Oct 2019
 * @TIME    : 9:26 PM
 **/

@SuppressLint("Registered")
open class BaseAppCompatActivity: AppCompatActivity() {
    
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
    
        /* Set window flags */
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
    
        super.onCreate(savedInstanceState)
    }
}