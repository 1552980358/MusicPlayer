package app.github1552980358.android.musicplayer.base

import java.io.Serializable

/**
 * [SongListInfo]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 22:14
 **/

class SongListInfo : Serializable {
    
    companion object {
        
        /**
         * [songListInfoList]
         **/
        var songListInfoList = arrayListOf<SongListInfo>()
        
        @JvmStatic
        fun copy(songListInfo: SongListInfo): SongListInfo {
            return SongListInfo().apply {
                listTitle = songListInfo.listTitle
                listSize = songListInfo.listSize
                hasCoverImage = songListInfo.hasCoverImage
                description = songListInfo.description
                createDate = songListInfo.createDate
            }
        }
        
    }
    
    /**
     * [listTitle]
     **/
    var listTitle = ""
    
    /**
     * [listSize]
     **/
    var listSize = 0
    
    /**
     *
     **/
    var hasCoverImage = false
    
    /**
     *
     **/
    var description = ""
    
    /**
     *
     **/
    var createDate = 0L
    
}