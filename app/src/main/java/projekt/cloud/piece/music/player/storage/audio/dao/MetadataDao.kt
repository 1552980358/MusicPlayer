package projekt.cloud.piece.music.player.storage.audio.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata.AudioMetadataConstant.AUDIO_METADATA_VIEW_NAME
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity

@Dao
interface MetadataDao {

    suspend fun insert(id: String, title: String,
                    artistId: String, artistName: String,
                    albumId: String, albumTitle: String,
                    duration: Long, size: Long) {
        insert(AudioEntity(id, title, artistId, albumId, duration, size))
        insert(ArtistEntity(artistId, artistName))
        insert(AlbumEntity(albumId, albumTitle))
    }

    @Insert(onConflict = REPLACE)
    suspend fun insert(audioEntity: AudioEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(artistEntity: ArtistEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(albumEntity: AlbumEntity)

    @Query("SELECT * FROM $AUDIO_METADATA_VIEW_NAME")
    suspend fun query(): List<AudioMetadata>

}