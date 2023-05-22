package projekt.cloud.piece.cloudy.storage.audio.view

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.io.File
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
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_ALBUM_TITLE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_ARTIST_NAME
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_DURATION
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_ID
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_SIZE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.METADATA_TITLE
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataUtil.VIEW_METADATA
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

    companion object AudioMetadataUtil {

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

        fun fromMediaItem(mediaItem: MediaItem): MetadataView {
            return fromMediaMetadata(mediaItem.mediaId, mediaItem.mediaMetadata)
        }

        private fun fromMediaMetadata(id: String, mediaMetadata: MediaMetadata): MetadataView {
            return fromMediaMetadataWithExtra(id, mediaMetadata, mediaMetadata.extras!!)
        }

        private fun fromMediaMetadataWithExtra(
            id: String, mediaMetadata: MediaMetadata, extras: Bundle
        ): MetadataView {
            return MetadataView(
                id = id,
                title = mediaMetadata.title.toString(),
                artist = extras.getString(METADATA_ARTIST).toString(),
                artistName = mediaMetadata.artist.toString(),
                album = extras.getString(METADATA_ALBUM).toString(),
                albumTitle = mediaMetadata.albumTitle.toString(),
                duration = extras.getLong(METADATA_DURATION),
                size = extras.getLong(METADATA_SIZE)
            )
        }

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

    /**
     * [MetadataView.audioUri]
     * @type [android.net.Uri]
     **/
    private val audioUri: Uri
        get() = Uri.parse(EXTERNAL_CONTENT_URI.toString() + File.separatorChar + id)

    /**
     * [MetadataView.albumUri]
     * @type [android.net.Uri]
     **/
    val albumUri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$album")

    /**
     * [MetadataView.mediaItem]
     * @type [androidx.media3.common.MediaItem]
     **/
    val mediaItem: MediaItem
        get() = buildMediaItem(audioUri)

    /**
     * [MetadataView.buildMediaItem]
     * @param audioUri [android.net.Uri]
     * @return [androidx.media3.common.MediaItem]
     *
     * Build [androidx.media3.common.MediaItem] with metadata stored in [MetadataView]
     **/
    private fun buildMediaItem(audioUri: Uri): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(audioUri)
            .setRequestMetadata(buildRequestMetadata(audioUri))
            .setMediaMetadata(buildMediaMetadata())
            .build()
    }

    /**
     * [MetadataView.buildRequestMetadata]
     * @param audioUri [android.net.Uri]
     * @return [androidx.media3.common.MediaItem.RequestMetadata]
     *
     * Build [androidx.media3.common.MediaItem.RequestMetadata] with [audioUri]
     **/
    private fun buildRequestMetadata(audioUri: Uri): RequestMetadata {
        return RequestMetadata.Builder()
            .setMediaUri(audioUri)
            .build()
    }

    /**
     * [MetadataView.buildMediaMetadata]
     * @return [androidx.media3.common.MediaMetadata]
     *
     * Build [androidx.media3.common.MediaMetadata] with metadata stored in [MetadataView]
     **/
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildMediaMetadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artistName)
            .setAlbumTitle(albumTitle)
            .setMediaType(MEDIA_TYPE_MUSIC)
            .setExtras(metadataExtra)
            .setIsBrowsable(true)
            .setIsPlayable(true)
            .build()
    }

    /**
     * [MetadataView.metadataExtra]
     * @type [android.os.Bundle]
     *
     * Extra metadata to be stored in [android.os.Bundle] for recovery
     **/
    private val metadataExtra: Bundle
        get() = bundleOf(
            METADATA_ARTIST to artist,
            METADATA_ALBUM to album,
            METADATA_DURATION to duration,
            METADATA_SIZE to size
        )

}