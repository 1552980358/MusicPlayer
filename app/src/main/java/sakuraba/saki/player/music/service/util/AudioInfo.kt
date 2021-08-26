package sakuraba.saki.player.music.service.util

import com.github.promeg.pinyinhelper.Pinyin
import java.io.Serializable

class AudioInfo(
    val audioId: String,
    val audioTitle: String,
    val audioTitlePinyin: String,
    val audioArtist: String,
    val audioArtistPinyin: String,
    val audioAlbum: String,
    val audioAlbumPinyin: String,
    val audioAlbumId: Long,
    val audioDuration: Long
    ): Serializable {
    constructor(audioId: String, audioTitle: String, audioArtist: String, audioAlbum: String, audioAlbumId: Long, audioDuration: Long):
        this(
            audioId,
            audioTitle,
            Pinyin.toPinyin(audioTitle, ""),
            audioArtist,
            Pinyin.toPinyin(audioArtist, ""),
            audioAlbum,
            Pinyin.toPinyin(audioAlbum, ""),
            audioAlbumId,
            audioDuration
        )
    
    companion object {
        private val URI = Uri.parse("content://media/external/audio/albumart")
    }
    
    private var isFetched = false
    private var bitmap: Bitmap? = null
    
    fun loadBitmap(context: Context) {
        if (!isFetched) {
            isFetched = !isFetched
            tryOnly { bitmap = BitmapFactory.decodeFileDescriptor(context.contentResolver.openFileDescriptor(ContentUris.withAppendedId(URI, audioAlbumId), "r")?.fileDescriptor) }
        }
    }
    
    fun getBitmap(context: Context): Bitmap? {
        loadBitmap(context)
        return bitmap
    }
    
}