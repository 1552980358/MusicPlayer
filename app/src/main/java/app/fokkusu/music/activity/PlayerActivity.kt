package app.fokkusu.music.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import app.fokkusu.music.R
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.activity_player.relativeLayout_container
import kotlinx.android.synthetic.main.activity_player.toolbar

/**
 * @File    : PlayerActivity
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:01 PM
 **/

class PlayerActivity : AppCompatActivity() {
    private lateinit var imageView_album: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
    
        /* Set StatusBar & NavBar Color */
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    
        overridePendingTransition(R.anim.anim_bottom2top, R.anim.anim_stay)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        
        startService(Intent(this, PlayService::class.java))
        relativeLayout_container.addView(
            ImageView(this).apply { imageView_album = this }, RelativeLayout.LayoutParams(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.widthPixels * 2 / 3
            )
        )
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_top2bottom)
    }
}