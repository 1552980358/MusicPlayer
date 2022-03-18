package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import projekt.cloud.piece.c2pinyin.pinyin
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import java.io.Serializable

@Entity(tableName = "audio")
class AudioItem(
    id: String,
    title: String,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "album") val album: String,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "path") val path: String
): BaseTitledItem(id, title, title.pinyin), Serializable {
    
    @Ignore
    var index = 0
    
    @Ignore
    lateinit var artistItem: ArtistItem
    
    @Ignore
    lateinit var albumItem: AlbumItem
    
    val durationInt get() = duration.toInt()
    
}