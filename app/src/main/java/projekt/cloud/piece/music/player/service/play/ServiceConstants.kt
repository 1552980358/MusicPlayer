package projekt.cloud.piece.music.player.service.play

import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID

object ServiceConstants {
    
    /** Service Start Command Actions **/
    const val ACTION_START_COMMAND = "${APPLICATION_ID}.PlayService.start"
    const val ACTION_START_COMMAND_PLAY = "${APPLICATION_ID}.PlayService.play"
    const val ACTION_START_COMMAND_PAUSE = "${APPLICATION_ID}.PlayService.pause"
    
    /** Custom Action **/
    const val CUSTOM_ACTION_REPEAT_MODE = "${APPLICATION_ID}.PlayService.repeat"
    const val CUSTOM_ACTION_SHUFFLE_MODE = "${APPLICATION_ID}.PlayService.shuffle"
    
    /** Extra Keys **/
    const val EXTRA_AUDIO_METADATA_LIST = "audio_metadata_list"
    
}