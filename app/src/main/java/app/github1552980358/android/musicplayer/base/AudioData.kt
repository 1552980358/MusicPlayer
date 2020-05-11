package app.github1552980358.android.musicplayer.base

import java.io.Serializable

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
        var audioDataMap = HashMap<String, AudioData>()
        
        @JvmStatic
        var ignoredData = ArrayList<String>()
        
    }
    
    var title = ""
    var artist = ""
    var album = ""
    var id = ""
    var duration = 0L
    
}