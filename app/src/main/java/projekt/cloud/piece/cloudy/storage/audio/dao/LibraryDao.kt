package projekt.cloud.piece.cloudy.storage.audio.dao

import androidx.room.Dao
import androidx.room.Query
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView.AlbumViewConstants.VIEW_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView.ArtistViewConstant.VIEW_ARTIST

@Dao
interface LibraryDao {

    /**
     * [LibraryDao.queryArtists]
     * @return [List]<[ArtistView]>
     **/
    @Query("SELECT * FROM $VIEW_ARTIST")
    suspend fun queryArtists(): List<ArtistView>

    /**
     * [LibraryDao.queryAlbums]
     * @return [List]<[AlbumView]>
     **/
    @Query("SELECT * FROM $VIEW_ALBUM")
    suspend fun queryAlbums(): List<AlbumView>

}