package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable

@Entity(tableName = "audio")
data class AudioItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "pinyin") val pinyin: String = title.pinyin,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "album") val album: String,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "path") val path: String
): Serializable {
    
    @Ignore
    var index = 0
    
    @Ignore
    lateinit var artistItem: ArtistItem
    
    @Ignore
    lateinit var albumItem: AlbumItem
    
    val durationInt get() = duration.toInt()
    
}