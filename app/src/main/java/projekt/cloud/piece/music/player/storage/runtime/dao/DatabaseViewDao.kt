package projekt.cloud.piece.music.player.storage.runtime.dao

import androidx.room.Dao
import androidx.room.Query
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_NAME

@Dao
interface DatabaseViewDao {

    @Query("SELECT * FROM $ARTIST_VIEW_NAME")
    suspend fun queryArtist(): List<ArtistView>

    @Query("SELECT * FROM $ALBUM_VIEW_NAME")
    suspend fun queryAlbum(): List<AlbumView>

}