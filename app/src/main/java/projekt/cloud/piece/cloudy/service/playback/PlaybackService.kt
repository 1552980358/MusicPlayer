package projekt.cloud.piece.cloudy.service.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.SessionToken
import projekt.cloud.piece.cloudy.base.BaseLifecycleMediaLibraryService
import projekt.cloud.piece.cloudy.util.Releasable

class PlaybackService: BaseLifecycleMediaLibraryService(), MediaLibrarySession.Callback {

    companion object PlaybackServiceUtil {

        private val playbackSessionClass: Class<PlaybackService>
            get() = PlaybackService::class.java

        val Context.playbackServiceToken: SessionToken
            get() = SessionToken(this, ComponentName(this, playbackSessionClass))

    }

    private val audioPlayer = AudioPlayer()
    private val mediaLibrarySessionHelper = MediaLibrarySessionHelper()

    private val releasableList: List<Releasable>
        get() = listOf(audioPlayer, mediaLibrarySessionHelper)

    override fun onCreate() {
        super.onCreate()
        setupMediaLibrarySession(
            setupAudioPlayer()
        )
    }

    private fun setupMediaLibrarySession(player: Player) {
        mediaLibrarySessionHelper.setupMediaLibrarySession(this, player)
    }

    private fun setupAudioPlayer(): Player {
        return audioPlayer.setupExoPlayer(this)
    }

    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySessionHelper.onGetSession(this, controllerInfo)
    }

    override fun onDestroy() {
        releasableList.forEach(::release)
        super.onDestroy()
    }

    private fun release(releasable: Releasable) {
        releasable.release()
    }

}