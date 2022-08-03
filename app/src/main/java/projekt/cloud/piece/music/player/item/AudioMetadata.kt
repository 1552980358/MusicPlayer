package projekt.cloud.piece.music.player.item

import androidx.room.Embedded
import androidx.room.Relation

class AudioMetadata(
    @Embedded val audio: Audio,
    @Relation(
        parentColumn = "artist",
        entityColumn = "id"
    )
    val artist: Artist,
    @Relation(
        parentColumn = "album",
        entityColumn = "id"
    )
    val album: Album
)