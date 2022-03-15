package projekt.cloud.piece.music.player.ui.play.util

import android.util.Log
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.MainActivityInterface
import java.io.Serializable

class FragmentManager(private val activityInterface: MainActivityInterface): Serializable {

    var defaultCoverImage = activityInterface.defaultAudioImage

    private var updatePlayCoverAudioList: ((List<AudioItem>) -> Unit)? = null
    var audioList: List<AudioItem>? = null
        set(value) {
            field = value
            value?.let { audioList -> updatePlayCoverAudioList?.let { it(audioList) } }
        }

    private var updatePlayAudioItem: ((AudioItem) -> Unit)? = null
    private var updatePlayCoverAudioItem: ((AudioItem) -> Unit)? = null
    private var updatePlayLyricAudioItem: ((AudioItem) -> Unit)? = null
    var audioItem = activityInterface.audioItem
        set(value) {
            field = value
            value?.let { audioItem ->
                updatePlayAudioItem?.let { it(audioItem) }
                updatePlayCoverAudioItem?.let { it(audioItem) }
                updatePlayLyricAudioItem?.let { it(audioItem) }
            }
        }

    private lateinit var updatePlayCoverProgress: (Long) -> Unit
    private var updatePlayLyricProgress: ((Long) -> Unit)? = null
    var progress = activityInterface.progress
        set(value) {
            field = value
            updatePlayCoverProgress(value)
            updatePlayLyricProgress?.let { it(value) }
        }

    private lateinit var updatePlayCoverColor: (Boolean, Int, Int, Int) -> Unit
    private var updatePlayLyricColor: ((Int, Int, Int) -> Unit)? = null
    fun updateColor(backgroundColor: Int, primaryColor: Int, secondaryColor: Int) {
        updatePlayCoverColor(backgroundColor.isLight, backgroundColor, primaryColor, secondaryColor)
        updatePlayLyricColor?.let { it(backgroundColor, primaryColor, secondaryColor) }
    }

    lateinit var updatePlayConfig: (Int) -> Unit
    var playConfig = activityInterface.playConfig
        set(value) {
            field = value
            updatePlayConfig(value)
        }
    fun requestUpdatePlayConfig(newConfig: Int) {
        Log.e("NEW", newConfig.toString())
        activityInterface.updateConfig(newConfig) {
            @Suppress("UNCHECKED_CAST")
            audioList = it.getSerializable(EXTRA_LIST) as List<AudioItem>
        }
    }

    lateinit var updatePlayState: (Boolean) -> Unit
    var isPlaying = activityInterface.isPlaying
        set(value) {
            field = value
            updatePlayState(value)
        }

    fun initial(updateAudioItem: (AudioItem) -> Unit) {
        updatePlayAudioItem = updateAudioItem
    }

    fun setUpPlayCover(updateAudioItem: (AudioItem) -> Unit,
                       updateAudioList: (List<AudioItem>) -> Unit,
                       updatePlayConfig: (Int) -> Unit,
                       updatePlayState: (Boolean) -> Unit,
                       updateProgress: (Long) -> Unit,
                       updateColor: (Boolean, Int, Int, Int) -> Unit) {
        this.updatePlayCoverAudioItem = updateAudioItem
        updatePlayCoverAudioList = updateAudioList
        this.updatePlayConfig = updatePlayConfig
        this.updatePlayState = updatePlayState
        updatePlayCoverProgress = updateProgress
        updatePlayCoverColor = updateColor
    }

    fun play() = activityInterface.play()
    fun pause() = activityInterface.pause()
    fun skipToPrevious() = activityInterface.skipToPrevious()
    fun skipToNext() = activityInterface.skipToNext()
    fun skipToQueueItem(item: Int) = activityInterface.skipToQueueItem(item)

    fun requestPlaylist() = activityInterface.requestPlaylist {
        @Suppress("UNCHECKED_CAST")
        audioList = it.getSerializable(EXTRA_LIST) as List<AudioItem>
    }

    init {
        activityInterface.setUpPlay(
            updateAudioItem = { audioItem = it },
            updatePlayState = { isPlaying = it },
            updateProgress = { progress = it },
            updatePlayConfig = { playConfig = it }
        )
    }

}