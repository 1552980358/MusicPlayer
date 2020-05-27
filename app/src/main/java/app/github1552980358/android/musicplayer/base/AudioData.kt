package app.github1552980358.android.musicplayer.base

import com.github.promeg.pinyinhelper.Pinyin
import java.io.Serializable
import java.util.Locale

/**
 * @file    : [AudioData]
 * @since   : 0.1
 * @author  : 1552980358
 * @date    : 2020/5/9
 * @time    : 12:24
 **/

class AudioData: Serializable {
    
    /**
     * [title]
     * @author 1552980358
     * @since 0.1
     **/
    var title = ""
        set(value) {
            titlePinYin = Pinyin.toPinyin(value, "").toUpperCase(Locale.ROOT)
            field = value
        }
    
    /**
     * [titlePinYin]
     * @author 1552980358
     * @since 0.1
     **/
    var titlePinYin = ""
    
    /**
     * [artist]
     * @author 1552980358
     * @since 0.1
     **/
    var artist = ""
    
    /**
     * [album]
     * @author 1552980358
     * @since 0.1
     **/
    var album = ""
    
    /**
     * [id]
     * @author 1552980358
     * @since 0.1
     **/
    var id = ""
    
    /**
     * [duration]
     * @author 1552980358
     * @since 0.1
     **/
    var duration = 0L
    
}