package projekt.cloud.piece.music.player.util

import android.net.Uri
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import java.io.File

object UriUtil {

    val String.audioUri: Uri
        get() = Uri.parse("${EXTERNAL_CONTENT_URI}${File.separator}$this")

    val String.albumArtUri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$this")

}