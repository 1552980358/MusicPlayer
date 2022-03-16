package projekt.cloud.piece.music.player.service.play

import android.net.Uri
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import java.io.File.separator

object MediaIdUtil {
    
    private val String.formUri get() = "$EXTERNAL_CONTENT_URI$separator$this"

    val String.parseAsUri get() = Uri.parse(formUri)

}