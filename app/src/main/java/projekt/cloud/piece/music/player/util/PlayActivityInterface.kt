package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.ui.play.PlayFragment

class PlayActivityInterface(val requestMetadata: () -> AudioItem,
                            val changePlayConfig: (Int) -> Unit) {

    lateinit var updateAudioItem: (AudioItem) -> Unit
    lateinit var updatePlayConfig: (Int) -> Unit
    lateinit var updateBitmap: (Bitmap) -> Unit
    lateinit var updateColor: (Int, Int) -> Unit
    lateinit var updateContrast: (Boolean) -> Unit
    lateinit var updateProgress: (Long) -> Unit
    lateinit var updatePlayState: (Boolean) -> Unit
    lateinit var updateAudioList: (List<AudioItem>) -> Unit
    
    lateinit var transportControls: TransportControls
    
    private var currentFragment: BasePlayFragment? = null
    fun isCurrent(fragment: BasePlayFragment) = currentFragment == fragment
    val isPlayFragment get() = currentFragment is PlayFragment
    
    fun setPlayFragmentListener(updatePlayConfig: (Int) -> Unit,
                                updateBitmap: (Bitmap) -> Unit,
                                updateContrast: (Boolean) -> Unit,
                                updatePlayState: (Boolean) -> Unit,
                                updateAudioList: (List<AudioItem>) -> Unit) {
        this.updatePlayConfig = updatePlayConfig
        this.updateBitmap = updateBitmap
        this.updateContrast = updateContrast
        this.updatePlayState = updatePlayState
        this.updateAudioList = updateAudioList
    }
    
    fun setCommonListener(fragment: BasePlayFragment,
                          updateAudioItem: (AudioItem) -> Unit,
                          updateColor: (Int, Int) -> Unit,
                          updateProgress: (Long) -> Unit) {
        this.currentFragment = fragment
        this.updateAudioItem = updateAudioItem
        this.updateColor = updateColor
        this.updateProgress = updateProgress
    }

}