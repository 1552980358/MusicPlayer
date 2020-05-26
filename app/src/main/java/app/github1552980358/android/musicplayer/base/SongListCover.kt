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
    
    var image = byteArrayOf()
    
    var backgroundColour = 0
    
    var primaryTextColour = 0
    
    var secondaryTextColour = 0
    
    var isLight = true
    
}