package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Ignore
import projekt.cloud.piece.music.player.database.audio.item.base.BaseTitledItem
import java.io.Serializable

@Entity(
    tableName = "audio",
    foreignKeys = [
        ForeignKey(
            entity = ArtistItem::class,
            parentColumns = ["id"],
            childColumns = ["artist"],
            onDelete = NO_ACTION
        ),
        ForeignKey(
            entity = AlbumItem::class,
            parentColumns = ["id"],
            childColumns = ["album"],
            onDelete = NO_ACTION
        )
    ]
)
class AudioItem(id: String,
                title: String,
                @ColumnInfo(name = "artist") val artist: String,
                @ColumnInfo(name = "album") val album: String): BaseTitledItem(id, title), Serializable {

    @Ignore
    lateinit var artistItem: ArtistItem
    @Ignore
    lateinit var albumItem: AlbumItem

    val artistName get() = artistItem.title
    val albumTitle get() = albumItem.title

}