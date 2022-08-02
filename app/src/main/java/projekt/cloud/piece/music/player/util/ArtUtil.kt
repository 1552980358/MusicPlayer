package projekt.cloud.piece.music.player.util

import android.content.Context
import java.io.File

object ArtUtil {
    
    const val SUFFIX_LARGE = "_large.png"
    const val SUFFIX_SMALL = "_small.png"
    
    const val TYPE_AUDIO = "audio"
    const val TYPE_ALBUM = "album"
    const val TYPE_ARTIST = "artist"
    
    fun Context.pathOf(type: String) =
        getExternalFilesDir(type)
    
    fun Context.storeArt(type: String, id: String, suffix: String) =
        File(pathOf(type), id + suffix).toString()
    
}