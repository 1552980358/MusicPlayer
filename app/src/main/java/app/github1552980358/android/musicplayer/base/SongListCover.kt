package app.github1552980358.android.musicplayer.base

import java.io.Serializable

/**
 * [SongListCover]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 22:17
 **/

class SongListCover: Serializable {
    
    /**
     * [image]
     * @author 1552980358
     * @since 0.1
     **/
    var image = byteArrayOf()
    
    /**
     * [backgroundColour]
     * @author 1552980358
     * @since 0.1
     **/
    var backgroundColour = 0
    
    /**
     * [primaryTextColour]
     * @author 1552980358
     * @since 0.1
     **/
    var primaryTextColour = 0
    
    /**
     * [secondaryTextColour]
     * @author 1552980358
     * @since 0.1
     **/
    var secondaryTextColour = 0
    
    /**
     * [isLight]
     * @author 1552980358
     * @since 0.1
     **/
    var isLight = true
    
}