package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import projekt.cloud.piece.music.player.database.item.AudioItem

class PlayActivityInterface(val requestMetadata: () -> AudioItem,
                            val changePlayConfig: (Int) -> Unit) {

    lateinit var loadMetadata: (AudioItem) -> Unit
    lateinit var updatePlayConfig: (Int) -> Unit
    lateinit var loadBitmap: (Bitmap) -> Unit
    lateinit var loadColor: (Int, Int) -> Unit
    lateinit var loadIsLight: (Boolean) -> Unit
    
    lateinit var transportControls: TransportControls
    
    fun setListener(loadMetadata: (AudioItem) -> Unit,
                    updatePlayConfig: (Int) -> Unit,
                    loadBitmap: (Bitmap) -> Unit,
                    loadColor: (Int, Int) -> Unit,
                    loadIsLight: (Boolean) -> Unit) {
        this.loadMetadata = loadMetadata
        this.updatePlayConfig = updatePlayConfig
        this.loadBitmap = loadBitmap
        this.loadColor = loadColor
        this.loadIsLight = loadIsLight
    }

}