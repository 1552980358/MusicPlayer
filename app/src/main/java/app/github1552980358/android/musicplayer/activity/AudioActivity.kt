package app.github1552980358.android.musicplayer.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AmplifyDiminishInterface
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import kotlinx.android.synthetic.main.activity_audio.imageViewAnim
import kotlinx.android.synthetic.main.activity_audio.imageViewStatic
import java.io.File

/**
 * @file    : [AudioActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/11
 * @time    : 16:32
 **/

class AudioActivity: /*Base*/AppCompatActivity(), AmplifyDiminishInterface {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        imageViewStatic.apply {
            layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels, resources.displayMetrics.widthPixels)
        }.setImageBitmap(BitmapFactory.decodeStream(File(getExternalFilesDir(AlbumNormal), "40822").inputStream()).apply {
            imageViewAnim.setImageBitmap(this)
        })
    }
    
    override fun onResume() {
        super.onResume()
        zoom(imageViewAnim)
    }
    
    
}