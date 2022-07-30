package projekt.cloud.piece.music.player.item

import android.net.Uri
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.File
import java.io.Serializable
import projekt.cloud.piece.music.player.item.base.BaseTitledItem

@Entity
class Audio(id: String,
            title: String,
            @ColumnInfo(name = "artist") var artist: String,
            @ColumnInfo(name = "album") var album: String,
            @ColumnInfo(name = "duration") var duration: String,
            @ColumnInfo(name = "size") var size: Long,
            @ColumnInfo(name = "path") var path: String): BaseTitledItem(id, title), Serializable {
    
    val uri: Uri
        get() = Uri.parse("${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}${File.separator}$id")
    
}