package projekt.cloud.piece.cloudy.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView

@Entity(TABLE_AUDIO)
data class AudioEntity(
    @PrimaryKey
    @ColumnInfo(AUDIO_ID)
    val id: String,
    @ColumnInfo(AUDIO_TITLE)
    val title: String,
    @ColumnInfo(AUDIO_ARTIST)
    val artist: String,
    @ColumnInfo(AUDIO_ALBUM)
    val album: String,
    @ColumnInfo(AUDIO_DURATION)
    val duration: Long,
    @ColumnInfo(AUDIO_SIZE)
    val size: Long
) {

    companion object AudioEntityConstant {

        const val TABLE_AUDIO = "audio"

        const val AUDIO_ID = "id"
        const val AUDIO_TITLE = "title"
        const val AUDIO_ARTIST = "artist"
        const val AUDIO_ALBUM = "album"
        const val AUDIO_DURATION = "duration"
        const val AUDIO_SIZE = "size"

    }

    @Ignore
    constructor(metadataView: MetadataView): this(
        metadataView.id,
        metadataView.title,
        metadataView.artist,
        metadataView.album,
        metadataView.duration,
        metadataView.size
    )

}