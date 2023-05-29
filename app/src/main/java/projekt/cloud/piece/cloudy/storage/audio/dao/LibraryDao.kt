package projekt.cloud.piece.cloudy.storage.audio.dao

import androidx.room.Dao
import androidx.room.Query
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

}