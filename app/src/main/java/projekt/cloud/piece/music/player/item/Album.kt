package projekt.cloud.piece.music.player.item

import android.net.Uri
import androidx.room.Entity
import java.io.Serializable
import projekt.cloud.piece.music.player.item.base.BaseTitledItem

@Entity
class Album(id: String, title: String): BaseTitledItem(id, title), Serializable {
    
    val uri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$id")

}