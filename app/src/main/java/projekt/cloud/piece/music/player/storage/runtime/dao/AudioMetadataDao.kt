package projekt.cloud.piece.music.player.storage.runtime.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@Dao
interface AudioMetadataDao {

    suspend fun insert(audioMetadataList: List<AudioMetadata>) {
        audioMetadataList.map { AudioMetadataEntity(it) }
            .forEach { insert(it) }
    }

    @Insert
    suspend fun insert(audioMetadataEntity: AudioMetadataEntity)

    @Query("SELECT * FROM $AUDIO_METADATA_TABLE_NAME " +
            "WHERE $AUDIO_METADATA_COLUMN_ID = :id")
    suspend fun query(id: String): AudioMetadataEntity

    @Query("SELECT * FROM $AUDIO_METADATA_TABLE_NAME")
    suspend fun query(): List<AudioMetadataEntity>

    @Query("SELECT * FROM $AUDIO_METADATA_TABLE_NAME " +
            "WHERE $AUDIO_METADATA_COLUMN_ARTIST = :id")
    suspend fun queryForArtist(id: String): List<AudioMetadataEntity>

    @Query("SELECT * FROM $AUDIO_METADATA_TABLE_NAME " +
            "WHERE $AUDIO_METADATA_COLUMN_ALBUM = :id")
    suspend fun queryForAlbum(id: String): List<AudioMetadataEntity>

}