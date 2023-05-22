package projekt.cloud.piece.cloudy.service.playback

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ControllerInfo
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import projekt.cloud.piece.cloudy.ui.activity.main.MainActivity.MainActivityUtil.mainActivityIntent
import projekt.cloud.piece.cloudy.util.implementation.Releasable

class MediaLibrarySessionHelper: Releasable, MediaLibrarySession.Callback {

    private companion object {

        /**
         * [MediaLibrarySessionHelper.PENDING_INTENT_FLAGS]
         * @type [Int]
         **/
        private const val PENDING_INTENT_FLAGS = FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT

    }

    /**
     * [MediaLibrarySessionHelper.mediaLibrarySession]
     * @type [androidx.media3.session.MediaLibraryService.MediaLibrarySession]
     **/
    private var _mediaLibrarySession: MediaLibrarySession? = null
    private val mediaLibrarySession: MediaLibrarySession
        get() = _mediaLibrarySession!!

    /**
     * [MediaLibrarySessionHelper.setupMediaLibrarySession]
     * @param mediaLibraryService [androidx.media3.session.MediaLibraryService]
     * @param player [androidx.media3.common.Player]
     *
     * Setup [mediaLibrarySession] with [androidx.media3.session.MediaLibraryService]
     * and [androidx.media3.common.Player]
     **/
    fun setupMediaLibrarySession(
        mediaLibraryService: MediaLibraryService,
        player: Player
    ) {
        setupMediaLibrarySession(
            mediaLibraryService,
            buildMediaLibrarySession(
                mediaLibraryService, player
            )
        )
    }

    /**
     * [MediaLibrarySessionHelper.setupMediaLibrarySession]
     * @param mediaLibraryService [androidx.media3.session.MediaLibraryService]
     * @param mediaLibrarySession [androidx.media3.session.MediaLibraryService.MediaLibrarySession]
     **/
    private fun setupMediaLibrarySession(
        mediaLibraryService: MediaLibraryService, mediaLibrarySession: MediaLibrarySession
    ) {
        _mediaLibrarySession = mediaLibrarySession
        mediaLibraryService.addSession(mediaLibrarySession)
    }

    /**
     * [MediaLibrarySessionHelper.buildMediaLibrarySession]
     * @param mediaLibraryService [androidx.media3.session.MediaLibraryService]
     * @param player [androidx.media3.common.Player]
     **/
    private fun buildMediaLibrarySession(
        mediaLibraryService: MediaLibraryService,
        player: Player
    ): MediaLibrarySession {
        return MediaLibrarySession.Builder(mediaLibraryService, player, this)
            .setSessionActivity(createSessionActivityIntent(mediaLibraryService))
            .build()
    }

    /**
     * [MediaLibrarySessionHelper.createSessionActivityIntent]
     * @param context [android.content.Context]
     * @return [android.app.PendingIntent]
     **/
    private fun createSessionActivityIntent(context: Context): PendingIntent {
        return TaskStackBuilder.create(context)
            .addNextIntent(context.mainActivityIntent)
            .getPendingIntent(0, PENDING_INTENT_FLAGS)!!
    }

    /**
     * [MediaLibrarySessionHelper.onGetSession]
     * @param context [android.content.Context]
     * @param controllerInfo [androidx.media3.session.MediaSession.ControllerInfo]
     * @return [androidx.media3.session.MediaLibraryService.MediaLibrarySession]
     *
     * Implementation of [androidx.media3.session.MediaLibraryService.onGetSession]
     **/
    fun onGetSession(context: Context, controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession.takeIf {
            controllerInfo.packageName == context.packageName
        }
    }

    /**
     * [androidx.media3.session.MediaLibraryService.MediaLibrarySession.Callback.onAddMediaItems]
     * @param mediaSession [androidx.media3.session.MediaSession]
     * @param controller [androidx.media3.session.MediaSession.ControllerInfo]
     * @param mediaItems [MutableList]<[]>
     **/
    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        return Futures.immediateFuture(setupMediaItemsUri(mediaItems))
    }

    /**
     * [MediaLibrarySessionHelper.setupMediaItemsUri]
     * @param mediaItems
     * @return [MutableList]<[androidx.media3.common.MediaItem]>
     *
     * set up uris for [mediaItems]
     **/
    private fun setupMediaItemsUri(mediaItems: List<MediaItem>): MutableList<MediaItem> {
        return setupMediaItemsUri(ArrayList(), mediaItems)
    }

    /**
     * [MediaLibrarySessionHelper.setupMediaItemsUri]
     * @param mutableList [MutableList]<[androidx.media3.common.MediaItem]>
     * @param mediaItems [List]
     * @return [MutableList]<[androidx.media3.common.MediaItem]>
     *
     * Implementation of setting up uris for [mediaItems]
     **/
    private fun setupMediaItemsUri(
        mutableList: MutableList<MediaItem>, mediaItems: List<MediaItem>
    ): MutableList<MediaItem> {
        mediaItems.forEach { mediaItem ->
            mutableList += setupMediaItemUri(mediaItem)
        }
        return mutableList
    }

    /**
     * [MediaLibrarySessionHelper.setupMediaItemUri]
     * @param mediaItem [androidx.media3.common.MediaItem]
     * @return [androidx.media3.common.MediaItem]
     *
     * Setup uri from [mediaItem]
     **/
    private fun setupMediaItemUri(mediaItem: MediaItem): MediaItem {
        return mediaItem.buildUpon()
            .setUri(mediaItem.requestMetadata.mediaUri)
            .build()
    }

    /**
     * [Releasable.release]
     **/
    override fun release() {
        _mediaLibrarySession?.release()
        _mediaLibrarySession = null
    }

}