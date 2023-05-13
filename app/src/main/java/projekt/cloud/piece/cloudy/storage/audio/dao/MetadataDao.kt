package projekt.cloud.piece.cloudy.storage.audio.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.VIEW_METADATA

@Dao
interface MetadataDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(audioEntity: AudioEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(artistEntity: ArtistEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(albumEntity: AlbumEntity)

    suspend fun insert(metadataView: MetadataView) {
        insert(AudioEntity(metadataView))
        insert(AlbumEntity(metadataView))
        insert(ArtistEntity(metadataView))
    }

    @Query("SELECT * FROM $VIEW_METADATA")
    suspend fun query(): List<MetadataView>

}