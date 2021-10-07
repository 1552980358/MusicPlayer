package sakuraba.saki.player.music.ui.album.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.util.MediaAlbum

class AlbumFragmentData {
    
    var mediaAlbumList: ArrayList<MediaAlbum>? = null
    
    var bitmapMap: MutableMap<Long, Bitmap?>? = null
    
    var hasData = false
    
}