package app.fokkusu.music.base.interfaces

import android.graphics.Bitmap
import java.io.Serializable

/* Listener as requesting for album cover image */
interface OnRequestAlbumCoverListener: Serializable {
    
    fun onResult(bitmap: Bitmap)
    
    fun onNullResult()
    
}