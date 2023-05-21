package projekt.cloud.piece.cloudy.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import projekt.cloud.piece.cloudy.service.playback.PlaybackService.PlaybackServiceUtil.playbackServiceToken

class MediaControllerHelper: DefaultLifecycleObserver, Releasable {

    private var listenableFuture: ListenableFuture<MediaController>? = null
    val mediaController: MediaController?
        get() = listenableFuture?.get()

    fun setupWithLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        setupMediaController(getContext(owner))
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    private fun getContext(lifecycleOwner: LifecycleOwner): Context {
        return when (lifecycleOwner) {
            is Fragment -> lifecycleOwner.requireContext()
            is Context -> lifecycleOwner
            else -> throw IllegalAccessException("Host class of 'LifecycleOwner' should be able to gain 'Context'.")
        }
    }

    private fun setupMediaController(context: Context) {
        listenableFuture = MediaController.Builder(context, context.playbackServiceToken)
            .buildAsync()
    }

    inline fun requireMediaController(block: (MediaController) -> Unit) {
        mediaController?.also(block)
    }

    override fun release() {
        listenableFuture?.cancel(true)
    }

}