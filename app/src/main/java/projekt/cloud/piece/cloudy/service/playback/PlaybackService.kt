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

        /**
         * [PlaybackService.playbackSessionClass]
         * @type [Class]
         **/
        private val playbackSessionClass: Class<PlaybackService>
            get() = PlaybackService::class.java

        /**
         * [PlaybackService.playbackServiceToken]
         * @type [androidx.media3.session.SessionToken]
         **/
        val Context.playbackServiceToken: SessionToken
            get() = SessionToken(this, ComponentName(this, playbackSessionClass))

    }

    /**
     * [PlaybackService.audioPlayer]
     * @type [AudioPlayer]
     **/
    private val audioPlayer = AudioPlayer()

    /**
     * [PlaybackService.mediaLibrarySessionHelper]
     * @type [MediaLibrarySessionHelper]
     **/
    private val mediaLibrarySessionHelper = MediaLibrarySessionHelper()

    /**
     * [PlaybackService]
     * @type [List]<[Releasable]>
     **/
    private val releasableList: List<Releasable>
        get() = listOf(audioPlayer, mediaLibrarySessionHelper)

    /**
     * [android.app.Service.onCreate]
     **/
    override fun onCreate() {
        super.onCreate()
        setupMediaLibrarySession(
            setupAudioPlayer()
        )
    }

    /**
     * [PlaybackService.setupMediaLibrarySession]
     * @param player [androidx.media3.common.Player]
     *
     * Setup [androidx.media3.session.MediaLibraryService.MediaLibrarySession]
     **/
    private fun setupMediaLibrarySession(player: Player) {
        mediaLibrarySessionHelper.setupMediaLibrarySession(this, player)
    }

    /**
     * [PlaybackService.setupAudioPlayer]
     * @return [androidx.media3.common.Player]
     *
     * Setup [androidx.media3.common.Player]
     **/
    private fun setupAudioPlayer(): Player {
        return audioPlayer.setupExoPlayer(this)
    }

    /**
     * [androidx.media3.session.MediaLibraryService.onGetSession]
     * @param controllerInfo [androidx.media3.session.MediaSession.ControllerInfo]
     * @return [androidx.media3.session.MediaLibraryService.MediaLibrarySession]
     **/
    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySessionHelper.onGetSession(this, controllerInfo)
    }

    /**
     * [android.app.Service.onDestroy]
     **/
    override fun onDestroy() {
        releasableList.forEach(::release)
        super.onDestroy()
    }

    /**
     * [PlaybackService.release]
     * @param releasable [Releasable]
     **/
    private fun release(releasable: Releasable) {
        releasable.release()
    }

}