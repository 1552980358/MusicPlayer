package app.fokkusu.music.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
        
        /* Set StatusBar & NavBar Color */
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        setSupportActionBar(toolbar)
        
        startService(Intent(this, PlayService::class.java))
        relativeLayout_container.addView(
            ImageView(this).apply { imageView_album = this }, RelativeLayout.LayoutParams(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.widthPixels * 2 / 3
            )
        )
        
    }
}