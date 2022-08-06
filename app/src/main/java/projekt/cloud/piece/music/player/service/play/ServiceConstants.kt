package projekt.cloud.piece.music.player.service.play

import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID

object ServiceConstants {
    
    /** Service Start Command Actions **/
    const val ACTION_START_COMMAND = "${APPLICATION_ID}.PlayService.start"
    const val ACTION_START_COMMAND_PLAY = "${APPLICATION_ID}.PlayService.play"
    const val ACTION_START_COMMAND_PAUSE = "${APPLICATION_ID}.PlayService.pause"
    
    /** Extra Keys **/
    const val EXTRA_AUDIO_METADATA_LIST = "audio_metadata_list"
    
}