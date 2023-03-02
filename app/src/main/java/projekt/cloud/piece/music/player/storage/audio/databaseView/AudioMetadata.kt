package projekt.cloud.piece.music.player.storage.audio.databaseView

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_ALBUM
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_ALBUM_TITLE
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_ARTIST
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_ARTIST_NAME
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_DURATION
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_ID
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_SIZE
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_TITLE
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_VIEW_NAME
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity.ArtistEntityConstants.ALBUM_COLUMN_ID
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity.ArtistEntityConstants.ALBUM_COLUMN_TITLE
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity.ArtistEntityConstants.ALBUM_TABLE_NAME
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_COLUMN_ID
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_COLUMN_NAME
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_TABLE_NAME
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_ALBUM
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_ARTIST
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_DURATION
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_ID
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_SIZE
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_COLUMN_TITLE
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity.AudioEntityConstants.AUDIO_TABLE_NAME

@DatabaseView(
    value = "SELECT " +
        /** audio.id => id **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_ID} as ${AUDIO_METADATA_ID}, " +
        /** audio.title => title **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_TITLE} as ${AUDIO_METADATA_TITLE}, " +
        /** audio.artist => artist **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_ARTIST} as ${AUDIO_METADATA_ARTIST}, " +
        /** artist.name => artistName **/
        "${ARTIST_TABLE_NAME}.${ARTIST_COLUMN_NAME} as ${AUDIO_METADATA_ARTIST_NAME}, " +
        /** audio.album => album **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_ALBUM} as ${AUDIO_METADATA_ALBUM}, " +
        /** album.title => albumTitle **/
        "${ALBUM_TABLE_NAME}.${ALBUM_COLUMN_TITLE} as ${AUDIO_METADATA_ALBUM_TITLE}, " +
        /** audio.duration => duration **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_DURATION} as ${AUDIO_METADATA_DURATION}, " +
        /** audio.size => size **/
        "${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_SIZE} as $AUDIO_METADATA_SIZE " +
        "FROM $AUDIO_TABLE_NAME " +
        "INNER JOIN $ARTIST_TABLE_NAME ON ${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_ARTIST} = ${ARTIST_TABLE_NAME}.${ARTIST_COLUMN_ID} " +
        "INNER JOIN $ALBUM_TABLE_NAME ON ${AUDIO_TABLE_NAME}.${AUDIO_COLUMN_ALBUM} = ${ALBUM_TABLE_NAME}.${ALBUM_COLUMN_ID} ",
    viewName = AUDIO_METADATA_VIEW_NAME
)
data class AudioMetadata(
    @ColumnInfo(name = AUDIO_METADATA_ID)
    val id: String,
    @ColumnInfo(name = AUDIO_METADATA_TITLE)
    val title: String,
    @ColumnInfo(name = AUDIO_METADATA_ARTIST)
    val artist: String,
    @ColumnInfo(name = AUDIO_METADATA_ARTIST_NAME)
    val artistName: String,
    @ColumnInfo(name = AUDIO_METADATA_ALBUM)
    val album: String,
    @ColumnInfo(name = AUDIO_METADATA_ALBUM_TITLE)
    val albumTitle: String,
    @ColumnInfo(name = AUDIO_METADATA_DURATION)
    val duration: Long,
    @ColumnInfo(name = AUDIO_METADATA_SIZE)
    val size: Long
) {

    companion object AudioMetadataConstant {
        const val AUDIO_METADATA_VIEW_NAME = "AudioMetadata"

        const val AUDIO_METADATA_ID = "id"
        const val AUDIO_METADATA_TITLE = "title"
        const val AUDIO_METADATA_ARTIST = "artist"
        const val AUDIO_METADATA_ARTIST_NAME = "artist_name"
        const val AUDIO_METADATA_ALBUM = "album"
        const val AUDIO_METADATA_ALBUM_TITLE = "album_title"
        const val AUDIO_METADATA_DURATION = "duration"
        const val AUDIO_METADATA_SIZE = "size"
    }

}