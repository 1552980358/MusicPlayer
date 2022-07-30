package projekt.cloud.piece.music.player.item.base

import androidx.room.ColumnInfo
import projekt.cloud.piece.c2.pinyin.C2Pinyin.pinyin

abstract class BaseTitledItem(id: String,
                     title: String,
                     pinyin: String = title.pinyin): BaseItem(id) {
    
    @ColumnInfo(name = "title")
    var title: String = title
        private set
    
    @ColumnInfo(name = "pinyin")
    var pinyin: String = pinyin
        private set
    
    fun updateTitle(newTitle: String) {
        title = newTitle
        pinyin = title.pinyin
    }

}