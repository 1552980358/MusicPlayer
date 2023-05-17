package projekt.cloud.piece.cloudy.storage.audio.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.ALBUM_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.ALBUM_TITLE
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.TABLE_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_NAME
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.TABLE_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_DURATION
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_SIZE
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_TITLE
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_ALBUM_TITLE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_ARTIST_NAME
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_DURATION
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_ID
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_SIZE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.METADATA_TITLE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.VIEW_METADATA
import projekt.cloud.piece.cloudy.util.DurationFormat.DurationFormatUtil.format
import projekt.cloud.piece.cloudy.util.DurationFormat.SHORT

@DatabaseView(
    value = "SELECT " +
            /** audio.id => id **/
            "$TABLE_AUDIO.$AUDIO_ID AS $METADATA_ID, " +
            /** audio.title => title **/
            "$TABLE_AUDIO.$AUDIO_TITLE AS $METADATA_TITLE, " +
            /** audio.artist => artist **/
            "$TABLE_AUDIO.$AUDIO_ARTIST AS $METADATA_ARTIST, " +
            /** artist.name => artist_name **/
            "$TABLE_ARTIST.$ARTIST_NAME AS $METADATA_ARTIST_NAME, " +
            /** audio.album => album **/
            "$TABLE_AUDIO.$AUDIO_ALBUM AS $METADATA_ALBUM," +
            /** album.title => album_title **/
            "$TABLE_ALBUM.$ALBUM_TITLE AS $METADATA_ALBUM_TITLE, " +
            /** audio.duration => duration **/
            "$TABLE_AUDIO.$AUDIO_DURATION AS $METADATA_DURATION, " +
            /** audio.size => size **/
            "$TABLE_AUDIO.$AUDIO_SIZE AS $METADATA_SIZE " +
            "FROM $TABLE_AUDIO " +
            /** artist: audio.artist = artist.id **/
            "INNER JOIN $TABLE_ARTIST ON $TABLE_AUDIO.$AUDIO_ARTIST = $TABLE_ARTIST.$ARTIST_ID " +
            /** album: audio.album = album.id **/
            "INNER JOIN $TABLE_ALBUM ON $TABLE_AUDIO.$AUDIO_ALBUM = $TABLE_ALBUM.$ALBUM_ID",
    viewName = VIEW_METADATA
)
class MetadataView(
    @ColumnInfo(METADATA_ID)
    val id: String,
    @ColumnInfo(METADATA_TITLE)
    val title: String,
    @ColumnInfo(METADATA_ARTIST)
    val artist: String,
    @ColumnInfo(METADATA_ARTIST_NAME)
    val artistName: String,
    @ColumnInfo(METADATA_ALBUM)
    val album: String,
    @ColumnInfo(METADATA_ALBUM_TITLE)
    val albumTitle: String,
    @ColumnInfo(METADATA_DURATION)
    val duration: Long,
    @ColumnInfo(METADATA_SIZE)
    val size: Long
) {

    companion object AudioMetadataConstant {

        const val VIEW_METADATA = "metadata"

        const val METADATA_ID = "id"
        const val METADATA_TITLE = "title"
        const val METADATA_ARTIST = "artist"
        const val METADATA_ARTIST_NAME = "artist_name"
        const val METADATA_ALBUM = "album"
        const val METADATA_ALBUM_TITLE = "album_title"
        const val METADATA_DURATION = "duration"
        const val METADATA_SIZE = "size"

        private const val SUBTITLE_DIVIDER = " - "
    }

    /**
     * [MetadataView.subtitle]
     * @type [String]
     *
     * Subtitle of listing metadata
     **/
    val subtitle: String
        get() = artistName + SUBTITLE_DIVIDER + albumTitle

    /**
     * [MetadataView.durationShortText]
     * @type [String]
     *
     * Formatted short duration text in [0:00]
     **/
    val durationShortText: String
        get() = duration.format(SHORT)

}