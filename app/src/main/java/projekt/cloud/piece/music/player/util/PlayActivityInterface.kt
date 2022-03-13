package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import projekt.cloud.piece.music.player.base.BaseMediaControlActivity
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.ui.play.PlayFragment

class PlayActivityInterface(baseMediaControlActivity: BaseMediaControlActivity,
                            val requestMetadata: () -> AudioItem,
                            val changePlayConfig: (Int) -> Unit,
                            val requestColors: () -> String?) {

    private lateinit var playUpdateAudioItem: (AudioItem) -> Unit
    private var lyricPlayUpdateAudioItem: ((AudioItem) -> Unit)? = null
    
    private lateinit var playUpdateColor: (Int, Int) -> Unit
    private var lyricPlayUpdateColor: ((Int, Int) -> Unit)? = null
    
    private lateinit var playUpdateProgress: (Long) -> Unit
    private var lyricPlayUpdateProgress: ((Long) -> Unit)? = null
    
    lateinit var updatePlayConfig: (Int) -> Unit
    lateinit var updateBitmap: (Bitmap) -> Unit
    lateinit var updateContrast: (Boolean) -> Unit
    lateinit var updatePlayState: (Boolean) -> Unit
    lateinit var updateAudioList: (List<AudioItem>) -> Unit
    
    lateinit var transportControls: TransportControls
    
    private var currentFragment: BasePlayFragment? = null
    val isPlayFragment get() = currentFragment is PlayFragment
    
    fun updateAudioItem(audioItem: AudioItem) {
        playUpdateAudioItem(audioItem)
        lyricPlayUpdateAudioItem?.let { it((audioItem)) }
    }
    
    fun updateColor(primaryColor: Int, secondaryColor: Int) {
        playUpdateColor(primaryColor, secondaryColor)
        lyricPlayUpdateColor?.let { it(primaryColor, secondaryColor) }
    }
    
    fun updateProgress(progress: Long) {
        playUpdateProgress(progress)
        lyricPlayUpdateProgress?.let { it(progress) }
    }
    
    fun setPlayFragmentListener(updatePlayConfig: (Int) -> Unit,
                                updateBitmap: (Bitmap) -> Unit,
                                updateContrast: (Boolean) -> Unit,
                                updatePlayState: (Boolean) -> Unit,
                                updateAudioList: (List<AudioItem>) -> Unit,
                                updateAudioItem: (AudioItem) -> Unit,
                                updateColor: (Int, Int) -> Unit,
                                updateProgress: (Long) -> Unit) {
        this.updatePlayConfig = updatePlayConfig
        this.updateBitmap = updateBitmap
        this.updateContrast = updateContrast
        this.updatePlayState = updatePlayState
        this.updateAudioList = updateAudioList
        
        playUpdateAudioItem = updateAudioItem
        playUpdateColor = updateColor
        playUpdateProgress = updateProgress
    }
    
    fun setLyricPlayFragmentListener(updateAudioItem: (AudioItem) -> Unit,
                                     updateColor: (Int, Int) -> Unit,
                                     updateProgress: (Long) -> Unit) {
        lyricPlayUpdateAudioItem = updateAudioItem
        lyricPlayUpdateColor = updateColor
        lyricPlayUpdateProgress = updateProgress
    }
    
    fun setCurrentFragment(fragment: BasePlayFragment) {
        currentFragment = fragment
    }
    
    private lateinit var pickLyricAction: (Uri) -> Unit
    private var pickLyric = baseMediaControlActivity.registerForActivityResult(GetContent()) {
        pickLyricAction(it)
    }
    
    fun getLyric(block: (uri: Uri) -> Unit) {
        pickLyricAction = block
        pickLyric.launch("*/*")
    }

}