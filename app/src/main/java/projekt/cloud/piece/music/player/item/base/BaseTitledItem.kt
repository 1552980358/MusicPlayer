package projekt.cloud.piece.music.player.item.base

import androidx.room.ColumnInfo
import projekt.cloud.piece.c2.pinyin.C2Pinyin.pinyin

abstract class BaseTitledItem(id: String,
                              @ColumnInfo(name = "title") var title: String,
                              @ColumnInfo(name = "pinyin") var pinyin: String = title.pinyin): BaseItem(id) {
    
    fun updateTitle(newTitle: String) {
        title = newTitle
        pinyin = title.pinyin
    }

}