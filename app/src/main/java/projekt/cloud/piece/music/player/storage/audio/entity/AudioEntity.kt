package projekt.cloud.piece.music.player.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AudioEntity(
    @PrimaryKey
    @ColumnInfo(name = AUDIO_COLUMN_ID)
    val id: String,
    @ColumnInfo(name = AUDIO_COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = AUDIO_COLUMN_ARTIST)
    val artist: String,
    @ColumnInfo(name = AUDIO_COLUMN_ALBUM)
    val album: String,
    @ColumnInfo(name = AUDIO_COLUMN_DURATION)
    val duration: Long,
    @ColumnInfo(name = AUDIO_COLUMN_SIZE)
    val size: Long
) {

    companion object AudioEntityConstants {
        const val AUDIO_COLUMN_ID = "id"
        const val AUDIO_COLUMN_TITLE = "title"
        const val AUDIO_COLUMN_ARTIST = "artist"
        const val AUDIO_COLUMN_ALBUM = "album"
        const val AUDIO_COLUMN_DURATION = "duration"
        const val AUDIO_COLUMN_SIZE = "size"
    }

}