package app.fokkusu.music.base.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.fokkusu.music.base.interfaces.OnRequestAlbumCoverListener

/**
 * @File    : BasePlayerActivity
 * @Author  : 1552980358
 * @Date    : 2019/11/29
 * @TIME    : 18:07
 **/

@SuppressLint("Registered")
open class BasePlayerActivity: AppCompatActivity(), OnRequestAlbumCoverListener {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    /* onResult */
    /* Need to be override when inheriting PlayerActivity */
    override fun onResult(bitmap: Bitmap) {
    }
    
    /* onNullResult */
    /* Need to be override when inheriting PlayerActivity */
    override fun onNullResult() {
    }
}