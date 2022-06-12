package projekt.cloud.piece.music.player.database.audio.item

import android.graphics.Color
import android.graphics.Color.WHITE
import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.io.Serializable
import projekt.cloud.piece.music.player.database.audio.item.base.BaseItem
import java.lang.System.currentTimeMillis

/**
 * [ColorItem]
 * inherit to [BaseItem]
 * implement to [Serializable]
 *
 * Variables:
 * [background]
 * [primary]
 * [secondary]
 * [audio]
 * [album]
 * [playlist]
 **/
@Entity(tableName = "color", inheritSuperIndices = true)
class ColorItem(@ColumnInfo(name = "background") @ColorInt var background: Int = Color.parseColor("#6750A4"),
                @ColumnInfo(name = "primary") @ColorInt var primary: Int = WHITE,
                @ColumnInfo(name = "secondary") @ColorInt var secondary: Int = WHITE
                ): BaseItem(currentTimeMillis()), Serializable {
    
    @ColumnInfo(name = "audio")
    var audio: String? = null
    
    @ColumnInfo(name = "album")
    var album: String? = null

    @ColumnInfo(name = "playlist")
    var playlist: String? = null
    
    @Ignore
    constructor(background: Int, primary: Int, secondary: Int, audio: String? = null, album: String? = null, playlist: String? = null): this(background, primary, secondary) {
        audio?.let {
            this.audio = it
        }
        album?.let {
            this.album = it
        }
        playlist?.let {
            this.playlist = it
        }
    }
    
}