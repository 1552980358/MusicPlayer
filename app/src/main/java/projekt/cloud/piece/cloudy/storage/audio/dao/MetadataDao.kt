package projekt.cloud.piece.cloudy.storage.audio.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.TABLE_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.TABLE_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView.AudioMetadataConstant.VIEW_METADATA

@Dao
interface MetadataDao {

    private companion object {
        private const val QUERY_VACUUM = "VACUUM"
        private val vacuumSQL = SimpleSQLiteQuery(QUERY_VACUUM)
    }

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

    @RawQuery
    suspend fun rawQuery(query: SupportSQLiteQuery): Int

    suspend fun clear() {
        clearAudio()
        clearArtist()
        clearAlbum()
        rawQuery(vacuumSQL)
    }

    @Query("DELETE FROM $TABLE_AUDIO")
    suspend fun clearAudio()

    @Query("DELETE FROM $TABLE_ARTIST")
    suspend fun clearArtist()

    @Query("DELETE FROM $TABLE_ALBUM")
    suspend fun clearAlbum()

}