package app.github1552980358.android.musicplayer.base

import java.io.Serializable
import java.util.ArrayList

/**
 * [SongListInfo]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 22:14
 **/

class SongListInfo: Serializable {
    
    companion object {
        
        /**
         * [songListInfoList]
         * @author 1552980358
         * @since 0.1
         **/
        var songListInfoList = ArrayList<SongListInfo>()
        
        /**
         * [copy]
         * @param songListInfo [SongListInfo]
         * @return [SongListInfo]
         * @author 1552980358
         * @since 0.1
         **/
        @JvmStatic
        fun copy(songListInfo: SongListInfo) = SongListInfo().apply {
            listTitle = songListInfo.listTitle
            listSize = songListInfo.listSize
            hasCoverImage = songListInfo.hasCoverImage
            description = songListInfo.description
            createDate = songListInfo.createDate
            customSort = songListInfo.customSort
        }
        
    }
    
    /**
     * [customSort]
     * @author 1552980358
     * @since 0.1
     **/
    var customSort = false
    
    /**
     * [listTitle]
     * @author 1552980358
     * @since 0.1
     **/
    var listTitle = ""
    
    /**
     * [listSize]
     * @author 1552980358
     * @since 0.1
     **/
    var listSize = 0
    
    /**
     * [hasCoverImage]
     * @author 1552980358
     * @since 0.1
     **/
    var hasCoverImage = false
    
    /**
     * [description]
     * @author 1552980358
     * @since 0.1
     **/
    var description = ""
    
    /**
     * [createDate]
     * @author 1552980358
     * @since 0.1
     **/
    var createDate = 0L
    
}