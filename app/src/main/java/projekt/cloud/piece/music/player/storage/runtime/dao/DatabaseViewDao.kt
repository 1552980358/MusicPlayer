package projekt.cloud.piece.music.player.storage.runtime.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@Dao
interface DatabaseViewDao {

    @Query("SELECT * FROM $ARTIST_VIEW_NAME")
    suspend fun queryArtist(): List<ArtistView>

    @Query("SELECT * FROM $ARTIST_VIEW_NAME where $ARTIST_VIEW_COLUMN_ID = :id")
    suspend fun queryArtist(id: String): ArtistView

    @MapInfo(keyColumn = "artist", valueColumn = "album")
    @Query("SELECT " +
            "$AUDIO_METADATA_COLUMN_ARTIST AS artist, " +
            "$AUDIO_METADATA_COLUMN_ALBUM AS album " +
            "FROM $AUDIO_METADATA_TABLE_NAME")
    suspend fun queryAlbumsOfArtists(): Map<String, List<String>>

    @Query("SELECT * FROM $ALBUM_VIEW_NAME")
    suspend fun queryAlbum(): List<AlbumView>

    @Query("SELECT * FROM $ALBUM_VIEW_NAME where $ALBUM_VIEW_COLUMN_ID = :id")
    suspend fun queryAlbum(id: String): AlbumView

}