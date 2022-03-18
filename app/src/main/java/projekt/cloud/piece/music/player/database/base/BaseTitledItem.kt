package projekt.cloud.piece.music.player.database.base

import androidx.room.ColumnInfo
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable

open class BaseTitledItem(
    id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "pinyin") var pinyin: String = title.pinyin
): BaseItem(id), Serializable