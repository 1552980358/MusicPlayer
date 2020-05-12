package app.github1552980358.android.musicplayer.activity

import android.app.Activity
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.text.TextUtils
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import app.github1552980358.android.musicplayer.R
import kotlinx.android.synthetic.main.activity_audio.imageView
import kotlinx.android.synthetic.main.activity_audio.textViewTitle

/**
 * @file    : [AudioActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/11
 * @time    : 16:32
 **/

class AudioActivity: /*Base*/AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
    
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
    
        window.navigationBarColor = TRANSPARENT
        window.statusBarColor =TRANSPARENT
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        
        imageView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, resources.displayMetrics.widthPixels)
    
        textViewTitle.isSingleLine = true
        textViewTitle.ellipsize = TextUtils.TruncateAt.END
        
    }
    
    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }
    
}