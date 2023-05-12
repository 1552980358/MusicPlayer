package projekt.cloud.piece.music.player.service.playback

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import java.io.Closeable
import projekt.cloud.piece.music.player.util.CoroutineUtil.mainBlocking
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
        return mainBlocking {
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
        }
    }

    suspend fun play() {
        return mainBlocking {
            exoPlayer.play()
        }
    }

    suspend fun pause() {
        return mainBlocking {
            exoPlayer.pause()
        }
    }

    suspend fun isPlaying(): Boolean {
        return mainBlocking {
            exoPlayer.isPlaying
        }
    }
    
    suspend fun seekTo(position: Long) {
        return mainBlocking {
            exoPlayer.seekTo(position)
        }
    }

    suspend fun currentPosition(): Long {
        return mainBlocking {
            exoPlayer.currentPosition
        }
    }

    override fun close() {
        exoPlayer.release()
    }

}