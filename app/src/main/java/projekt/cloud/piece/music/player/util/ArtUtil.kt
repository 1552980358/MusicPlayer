package projekt.cloud.piece.music.player.util

import android.content.Context
import java.io.File
import projekt.cloud.piece.music.player.util.UnitUtil.toPx

object ArtUtil {
    
    const val SUFFIX_LARGE = "_large.png"
    const val SUFFIX_SMALL = "_small.png"
    
    const val TYPE_AUDIO = "audio"
    const val TYPE_ALBUM = "album"
    const val TYPE_ARTIST = "artist"
    
    private fun Context.dirOf(type: String) =
        getExternalFilesDir(type)
    
    fun Context.pathOf(type: String, id: String, suffix: String) =
        fileOf(type, id, suffix).toString()
    
    fun Context.fileOf(type: String, id: String, suffix: String) =
        File(dirOf(type), id + suffix)
    
    val SMALL_IMAGE_PIXEL_SIZE = 40.toPx.toInt()
    
}