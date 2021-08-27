package sakuraba.saki.player.music.service.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private val String.mediaUriStr get() =
    "${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}${File.separator}${this}"

private val String.parseAsUri get() = Uri.parse(this)

fun MediaPlayer.syncPlayAndPrepareMediaId(context: Context, mediaId: String, block: () -> Unit) {
    stop()
    reset()
    setDataSource(context, mediaId.mediaUriStr.parseAsUri)
    CoroutineScope(Dispatchers.IO).launch {
        @Suppress("BlockingMethodInNonBlockingContext")
        prepare()
        launch(Dispatchers.Main) {
            block()
            start()
        }
    }
}