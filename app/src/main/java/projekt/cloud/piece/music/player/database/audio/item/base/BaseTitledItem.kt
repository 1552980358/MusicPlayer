package projekt.cloud.piece.music.player.database.audio.item.base

import androidx.room.ColumnInfo
import androidx.room.Ignore
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable

open class BaseTitledItem(id: String, @ColumnInfo(name = "title") val title: String): BaseItem(id), Serializable {

    @Ignore
    constructor(id: Long, title: String): this(id.toString(), title)

    @ColumnInfo(name = "pinyin")
    var pinyin = title.pinyin

    @ColumnInfo(name = "nickname")
    var nickname: String? = null
        set(value) {
            field = value
            nicknamePinyin = value?.pinyin
        }

    @ColumnInfo(name = "nickname_pinyin")
    var nicknamePinyin: String? = null

}