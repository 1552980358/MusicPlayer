package projekt.cloud.piece.music.player.storage.runtime.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@Entity(tableName = AUDIO_METADATA_TABLE_NAME)
data class AudioMetadataEntity(
    @PrimaryKey
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_ID)
    val id: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_ARTIST)
    val artist: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_ARTIST_NAME)
    val artistName: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_ALBUM)
    val album: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_ALBUM_TITLE)
    val albumTitle: String,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_DURATION)
    val duration: Long,
    @ColumnInfo(name = AUDIO_METADATA_COLUMN_SIZE)
    val size: Long
) {

    constructor(audioMetadata: AudioMetadata) : this(
        audioMetadata.id,
        audioMetadata.title,
        audioMetadata.artist,
        audioMetadata.artistName,
        audioMetadata.album,
        audioMetadata.albumTitle,
        audioMetadata.duration,
        audioMetadata.size
    )

    companion object AudioMetadataEntityUtil {
        const val AUDIO_METADATA_TABLE_NAME = "audio_metadata"
        const val AUDIO_METADATA_COLUMN_ID = "id"
        const val AUDIO_METADATA_COLUMN_TITLE = "title"
        const val AUDIO_METADATA_COLUMN_ARTIST = "artist"
        const val AUDIO_METADATA_COLUMN_ARTIST_NAME = "artist_name"
        const val AUDIO_METADATA_COLUMN_ALBUM = "album"
        const val AUDIO_METADATA_COLUMN_ALBUM_TITLE = "album_title"
        const val AUDIO_METADATA_COLUMN_DURATION = "duration"
        const val AUDIO_METADATA_COLUMN_SIZE = "size"

    }


    override operator fun equals(other: Any?): Boolean {
        if (other !is AudioMetadataEntity) {
            return false
        }
        return id == other.id &&
                title == other.title &&
                artist == other.artist &&
                artistName == other.artistName &&
                album == other.album &&
                albumTitle == other.albumTitle &&
                duration == other.duration &&
                size == other.size
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + albumTitle.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

}