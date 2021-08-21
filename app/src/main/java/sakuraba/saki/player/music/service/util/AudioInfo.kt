package sakuraba.saki.player.music.service.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.promeg.pinyinhelper.Pinyin
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly

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
    ) {
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