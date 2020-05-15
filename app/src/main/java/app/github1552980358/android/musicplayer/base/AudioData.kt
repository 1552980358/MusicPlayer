package app.github1552980358.android.musicplayer.base

import com.github.promeg.pinyinhelper.Pinyin
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * @file    : [AudioData]
 * @since   : 0.1
 * @author  : 1552980358
 * @date    : 2020/5/9
 * @time    : 12:24
 **/

class AudioData: Serializable {
    
    companion object {
        
        @JvmStatic
        var audioDataList = ArrayList<AudioData>()
        
        @JvmStatic
        var audioDataMap = mutableMapOf<String, AudioData>()
        
        //@JvmStatic
        //var ignoredData = ArrayList<String>()
        
    }
    
    var title = ""
        set(value) {
            titlePinYin = Pinyin.toPinyin(value, "").toUpperCase(Locale.ROOT)
            field = value
        }
    var titlePinYin = ""
    var artist = ""
    var album = ""
    var id = ""
    var duration = 0L
    
}