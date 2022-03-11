package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import projekt.cloud.piece.music.player.database.item.AudioItem

class PlayActivityInterface(val requestMetadata: () -> AudioItem,
                            val changePlayConfig: (Int) -> Unit) {

    lateinit var updateAudioItem: (AudioItem) -> Unit
    lateinit var updatePlayConfig: (Int) -> Unit
    lateinit var updateBitmap: (Bitmap) -> Unit
    lateinit var updateColor: (Int, Int) -> Unit
    lateinit var updateContrast: (Boolean) -> Unit
    lateinit var updateProgress: (Long) -> Unit
    lateinit var updatePlayState: (Boolean) -> Unit
    
    lateinit var transportControls: TransportControls
    
    fun setListener(updateAudioItem: (AudioItem) -> Unit,
                    updatePlayConfig: (Int) -> Unit,
                    updateBitmap: (Bitmap) -> Unit,
                    updateColor: (Int, Int) -> Unit,
                    updateContrast: (Boolean) -> Unit,
                    updateProgress: (Long) -> Unit,
                    updatePlayState: (Boolean) -> Unit) {
        this.updateAudioItem = updateAudioItem
        this.updatePlayConfig = updatePlayConfig
        this.updateBitmap = updateBitmap
        this.updateColor = updateColor
        this.updateContrast = updateContrast
        this.updateProgress = updateProgress
        this.updatePlayState = updatePlayState
    }

}