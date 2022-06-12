package projekt.cloud.piece.music.player.service.play

import android.net.Uri
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import java.io.File.separator

/**
 * [AudioUtil]
 *
 * Methods:
 * [formUri]
 * [parseUri]
 **/
object AudioUtil {

    val String.formUri get() = "$EXTERNAL_CONTENT_URI$separator$this"

    val String.parseUri: Uri get() = Uri.parse(formUri)

}