package projekt.cloud.piece.cloudy.util.helper

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player.Listener
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import projekt.cloud.piece.cloudy.service.playback.PlaybackService.PlaybackServiceUtil.playbackServiceToken
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.requireContext
import projekt.cloud.piece.cloudy.util.implementation.Releasable

class MediaControllerHelper: DefaultLifecycleObserver, Releasable {

    private var _listenableFuture: ListenableFuture<MediaController>? = null
    val mediaController: MediaController?
        get() = _listenableFuture?.get()

    /**
     * [MediaControllerHelper.requireMediaController]
     * @param block [kotlin.jvm.functions.Function1]<[androidx.media3.session.MediaController], [Unit]>
     *
     * Require [androidx.media3.session.MediaController] in a safer way
     **/
    inline fun requireMediaController(block: (MediaController) -> Unit) {
        mediaController?.also(block)
    }

    /**
     * [MediaControllerHelper.setupWithLifecycleOwner]
     * @param lifecycleOwner [androidx.lifecycle.LifecycleOwner]
     * @param listener [androidx.media3.common.Player.Listener]
     **/
    fun setupWithLifecycleOwner(
        lifecycleOwner: LifecycleOwner,
        /**
         * Try casting into [androidx.media3.common.Player.Listener]
         **/
        listener: Listener? = lifecycleOwner as? Listener
    ) {
        lifecycleOwner.lifecycle.addObserver(this)
        setupMediaController(lifecycleOwner.requireContext(), listener)
    }

    /**
     * [MediaControllerHelper.setupWithLifecycleOwner]
     * @param context [android.content.Context]
     * @param listener [androidx.media3.common.Player.Listener]
     **/
    private fun setupMediaController(context: Context, listener: Listener?) {
        setupMediaController(
            buildMediaControllerListenableFuture(context),
            listener
        )
    }

    /**
     * [MediaControllerHelper.buildMediaControllerListenableFuture]
     * @param context [android.content.Context]
     * @return [com.google.common.util.concurrent.ListenableFuture]<[androidx.media3.session.MediaController]>
     *
     * Build [androidx.media3.session.MediaController] in async with [com.google.common.util.concurrent.ListenableFuture]
     **/
    private fun buildMediaControllerListenableFuture(context: Context): ListenableFuture<MediaController> {
        return MediaController.Builder(context, context.playbackServiceToken)
            .buildAsync()
    }

    /**
     * [MediaControllerHelper.setupMediaController]
     * @param listenableFuture [com.google.common.util.concurrent.ListenableFuture]<[androidx.media3.session.MediaController]>
     * @param listener [androidx.media3.common.Player.Listener]
     *
     * Setup [androidx.media3.session.MediaController] with assigning [androidx.media3.common.Player.Listener],
     * and store [com.google.common.util.concurrent.ListenableFuture] instance
     **/
    private fun setupMediaController(listenableFuture: ListenableFuture<MediaController>, listener: Listener?) {
        listener?.let {
            listenableFuture.addListener(
                { listenableFuture.get()?.addListener(listener) },
                MoreExecutors.directExecutor()
            )
        }
        _listenableFuture = listenableFuture
    }

    /**
     * [androidx.lifecycle.DefaultLifecycleObserver.onDestroy]
     * @param owner [androidx.lifecycle.LifecycleOwner]
     **/
    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    /**
     * [Releasable.release]
     **/
    override fun release() {
        _listenableFuture?.cancel(true)
    }

}