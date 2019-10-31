package app.fokkusu.music.base.interfaces

import android.graphics.Bitmap

/* Listener as requesting for album cover image */
interface OnRequestAlbumCoverListener {
    
    fun onResult(bitmap: Bitmap)
    
    fun onNullResult()
    
}