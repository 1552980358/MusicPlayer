package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import projekt.cloud.piece.music.player.database.item.AudioItem

class PlayActivityInterface(val requestMetadata: () -> AudioItem) {

    lateinit var loadMetadata: (AudioItem) -> Unit
    lateinit var loadBitmap: (Bitmap) -> Unit
    
    fun setListener(loadMetadata: (AudioItem) -> Unit, loadBitmap: (Bitmap) -> Unit) {
        this.loadMetadata = loadMetadata
        this.loadBitmap = loadBitmap
    }

}