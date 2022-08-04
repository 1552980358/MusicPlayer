package projekt.cloud.piece.music.player.item

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

class AudioMetadata(
    @Embedded val audio: Audio,
    @Relation(parentColumn = "artist", entityColumn = "id") val artist: Artist,
    @Relation(parentColumn = "album", entityColumn = "id") val album: Album): Serializable {
    
    val id: String
        get() = audio.id
    
    val title: String
        get() = audio.title
    
    val pinyin: String
        get() = audio.pinyin
    
    val artistTitle: String
        get() = artist.title
    
    val albumTitle: String
        get() = album.title
    
}