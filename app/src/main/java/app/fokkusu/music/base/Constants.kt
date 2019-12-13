package app.fokkusu.music.base

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
        const val PlayService = "FokkisuPlayService"
    
        /*
        const val MusicServiceId = 1
        const val MusicServiceChannelId = "FokkusuMusic_MusicService"
        //const val MusicService = "MusicService"
        const val MusicServiceMusicInfoExtra = "MusicServiceMusicInfoExtra"
        const val MusicServiceInterface = "MusicServiceInterface"
        
         */
        
        /* General Constants */
        const val ERROR_CODE_STR = "ERROR_CODE_STR"
        const val ERROR_CODE_INT = -1
        const val PLAY_LIST = 0
        const val MUSIC_LIST = 1
        
        /* Service Owned */
        const val SERVICE_BROADCAST_PAUSE = "SERVICE_BROADCAST_PAUSE"
        const val SERVICE_BROADCAST_PLAY = "SERVICE_BROADCAST_PLAY"
        const val SERVICE_BROADCAST_CHANGED = "SERVICE_BROADCAST_CHANGED"
        const val SERVICE_BROADCAST_BITMAP_RESULT = "SERVICE_BROADCAST_BITMAP_RESULT"
        const val SERVICE_BROADCAST_BITMAP_CONTENT = "SERVICE_BROADCAST_BITMAP_CONTENT"
    
        /* User control */
        const val USER_BROADCAST_PLAY = "USER_BROADCAST_PLAY"
        const val USER_BROADCAST_PAUSE = "USER_BROADCAST_PAUSE"
        const val USER_BROADCAST_NEXT = "USER_BROADCAST_NEXT"
        const val USER_BROADCAST_LAST = "USER_BROADCAST_LAST"
        const val USER_BROADCAST_SELECTED = "USER_BROADCAST_SELECTED"
        
        /* Intent Extra */
        const val BROADCAST_EXTRA_MUSIC_SOURCE = "BROADCAST_EXTRA_MUSIC_SOURCE"
        const val BROADCAST_EXTRA_MUSIC_INDEX = "BROADCAST_EXTRA_MUSIC_INDEX"
        
        /* SERVICE_START */
        const val SERVICE_INTENT_CONTENT = "SERVICE_INTENT_CONTENT"
        const val SERVICE_INTENT_INIT = "SERVICE_INTENT_INIT"
        const val SERVICE_INTENT_PLAY = "SERVICE_INTENT_PLAY"
        const val SERVICE_INTENT_PAUSE = "SERVICE_INTENT_PAUSE"
        const val SERVICE_INTENT_LAST = "SERVICE_INTENT_LAST"
        const val SERVICE_INTENT_NEXT = "SERVICE_INTENT_NEXT"
        const val SERVICE_INTENT_PLAY_FORM = "SERVICE_INTENT_PLAY_FORM"
        const val SERVICE_INTENT_PLAY_FORM_CONTENT = "SERVICE_INTENT_PLAY_FORM_CONTENT"
        const val SERVICE_INTENT_CHANGE = "SERVICE_INTENT_CHANGE"
        const val SERVICE_INTENT_CHANGE_SOURCE = "SERVICE_INTENT_CHANGE_SOURCE"
        const val SERVICE_INTENT_CHANGE_SOURCE_LOC = "SERVICE_INTENT_CHANGE_SOURCE_LOC"
        const val SERVICE_INTENT_SEEK_CHANGE = "SERVICE_INTENT_SEEK_CHANGE"
        const val SERVICE_INTENT_SEEK_CHANGE_POSITION = "SERVICE_INTENT_SEEK_CHANGE_POSITION"
        
        /* Application */
        const val APPLICATION_MEDIA_SCAN_COMPLETE = "APPLICATION_MEDIA_SCAN_COMPLETE"
        
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
        
        /* Dirs */
        const val Dir_Lyric = "Lyric"
        const val Dir_Cover = "Cover"
        const val Ext_Lyric = ".lrc"
        //const val Ext_Cover_UPC = ".PNG"
        const val Ext_Cover = ".png"
        const val Hyphen_CHAR = '-'
        const val Hyphen_STR = Hyphen_CHAR.toString()
    
        const val SP_Player_UI = "sp_player_ui"
        const val SP_Pulse_Switch = "sp_pulse_switch"
        const val SP_Pulse_Style = "sp_pulse_style"
        const val SP_Play_Disrupt = "sp_play_disrupt"
        const val SP_Play_Disrupt_Check = "sp_play_disrupt_check"
        
        const val Save_Player_UI = "Save_Player_UI"
        const val Save_Player_UI_Def = '0'
        const val Save_Player_UI_Dyn = '1'
        const val Save_Pulse_Switch = "Save_Pulse_Switch"
        const val Save_Pulse_Style = "Save_Pulse_Style"
        const val Save_Pulse_Style_Cylinder = '0'
        const val Save_Pulse_Style_Wave = '1'
        const val Save_Play_Disrupt = "Save_Play_Disrupt"
        
        /* Known that will gain focus when start */
        val GamePackageName = arrayListOf(
            "com.miHoYo.enterprise.NGHSoD",     // 三蹦子 官服
            "com.tencent.tmgp.sgame"            // 亡者农药
        )
    }
    
    init {
        throw IllegalAccessException("Constants")
    }
}