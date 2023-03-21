package projekt.cloud.piece.music.player.service.playback

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import java.io.Closeable
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.LifecycleProperty.LifecyclePropertyUtil.lifecycleProperty

class AudioPlayer(lifecycleOwner: LifecycleOwner): Closeable {

    private var exoPlayer: ExoPlayer by lifecycleOwner.lifecycleProperty()

    fun setupPlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context)
            .build()
    }

    fun setListener(listener: Listener) {
        exoPlayer.addListener(listener)
    }

    suspend fun prepareUri(uri: Uri) {
        return withContext(main) {
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
        }
    }

    suspend fun play() {
        return withContext(main) {
            exoPlayer.play()
        }
    }

    suspend fun pause() {
        return withContext(main) {
            exoPlayer.pause()
        }
    }

    suspend fun isPlaying(): Boolean {
        return withContext(main) {
            exoPlayer.isPlaying
        }
    }
    
    suspend fun seekTo(position: Long) {
        return withContext(main) {
            exoPlayer.seekTo(position)
        }
    }

    suspend fun currentPosition(): Long {
        return withContext(main) {
            exoPlayer.currentPosition
        }
    }

    override fun close() {
        exoPlayer.release()
    }

}