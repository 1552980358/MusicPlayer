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
import projekt.cloud.piece.cloudy.util.Releasable

class MediaLibrarySessionHelper: Releasable, MediaLibrarySession.Callback {

    private companion object {
        private const val PENDING_INTENT_FLAGS = FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    }

    private var _mediaLibrarySession: MediaLibrarySession? = null
    private val mediaLibrarySession: MediaLibrarySession
        get() = _mediaLibrarySession!!

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

    private fun setupMediaLibrarySession(
        mediaLibraryService: MediaLibraryService, mediaLibrarySession: MediaLibrarySession
    ) {
        _mediaLibrarySession = mediaLibrarySession
        mediaLibraryService.addSession(mediaLibrarySession)
    }

    private fun buildMediaLibrarySession(
        mediaLibraryService: MediaLibraryService,
        player: Player
    ): MediaLibrarySession {
        return MediaLibrarySession.Builder(mediaLibraryService, player, this)
            .setSessionActivity(createSessionActivityIntent(mediaLibraryService))
            .build()
    }

    private fun createSessionActivityIntent(context: Context): PendingIntent {
        return TaskStackBuilder.create(context)
            .addNextIntent(context.mainActivityIntent)
            .getPendingIntent(0, PENDING_INTENT_FLAGS)!!
    }

    fun onGetSession(context: Context, controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession.takeIf {
            controllerInfo.packageName == context.packageName
        }
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        return Futures.immediateFuture(setupMediaItemsUri(mediaItems))
    }

    private fun setupMediaItemsUri(mediaItems: List<MediaItem>): MutableList<MediaItem> {
        return setupMediaItemsUri(ArrayList(), mediaItems)
    }

    private fun setupMediaItemsUri(
        mutableList: MutableList<MediaItem>, mediaItems: List<MediaItem>
    ): MutableList<MediaItem> {
        mediaItems.forEach { mediaItem ->
            mutableList += setupMediaItemUri(mediaItem)
        }
        return mutableList
    }

    private fun setupMediaItemUri(mediaItem: MediaItem): MediaItem {
        return mediaItem.buildUpon()
            .setUri(mediaItem.requestMetadata.mediaUri)
            .build()
    }

    override fun release() {
        _mediaLibrarySession?.release()
        _mediaLibrarySession = null
    }

}