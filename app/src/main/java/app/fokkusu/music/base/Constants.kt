package app.fokkusu.music.base

import app.fokkusu.music.service.PlayService

/**
 * @File    : Constants
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:35 PM
 **/

class Constants private constructor() {
    companion object {
        const val PlayServiceId = 0
        const val PlayServiceChannelId = "FokkusuMusic"
        const val PlayService = "PlayService"
        
        /* General Constants */
        const val ERROR_CODE_STR = "ERROR_CODE_STR"
        const val ERROR_CODE_INT = -1
        const val PLAY_LIST = 0
        const val MUSIC_LIST = 1
        
        /* Service Owned */
        const val SERVICE_BROADCAST_PAUSE = "SERVICE_BROADCAST_PAUSE"
        const val SERVICE_BROADCAST_PLAY = "SERVICE_BROADCAST_PLAY"
        const val SERVICE_BROADCAST_CHANGED = "SERVICE_BROADCAST_CHANGED"
        
        /* User control */
        const val USER_BROADCAST_PLAY = "USER_BROADCAST_PLAY"
        const val USER_BROADCAST_PAUSE = "USER_BROADCAST_PAUSE"
        const val USER_BROADCAST_MUSIC_NEXT = "USER_BROADCAST_MUSIC_NEXT"
        const val USER_BROADCAST_MUSIC_LAST = "USER_BROADCAST_MUSIC_LAST"
        const val USER_BROADCAST_MUSIC_SELECTED = "USER_BROADCAST_MUSIC_SELECTED"
        
        /* Intent Extra */
        const val BROADCAST_EXTRA_MUSIC_SOURCE = "BROADCAST_EXTRA_MUSIC_SOURCE"
        const val BROADCAST_EXTRA_MUSIC_INDEX = "BROADCAST_EXTRA_MUSIC_INDEX"
        
        /* SERVICE_START */
        const val SERVICE_INTENT_CONTENT = "SERVICE_INTENT_CONTENT"
        const val SERVICE_INTENT_PLAY = "SERVICE_INTENT_PLAY"
        const val SERVICE_INTENT_PAUSE = "SERVICE_INTENT_PAUSE"
        const val SERVICE_INTENT_CHANGE = "SERVICE_INTENT_CHANGE"
        
        const val SERVICE_INTENT_CHANGE_SOURCE = "SERVICE_INTENT_CHANGE_SOURCE"
        
        const val SERVICE_INTENT_CHANGE_SOURCE_LOC = "SERVICE_INTENT_CHANGE_SOURCE_LOC"
        
        const val Path = "Path"
        const val Id = "Id"
        const val Title = "Title"
        const val Artist = "Artist"
        const val Album = "Album"
        const val Duration = "Duration"
        const val BitRate = "BitRate"
        const val TitlePY = "TitlePY"
        const val ArtistPY = "ArtistPY"
        const val AlbumPY = "AlbumPY"
        const val AlbumCover = "AlbumCover"
    }
    
    init {
        throw IllegalAccessException("Constants")
    }
}