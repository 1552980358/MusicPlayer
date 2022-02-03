package sakuraba.saki.player.music.util

import sakuraba.saki.player.music.BuildConfig

object Constants {
    
    const val EXTRAS_AUDIO_INFO_POS = "audio_info_pos"
    const val EXTRAS_AUDIO_INFO = "audio_info"
    const val EXTRAS_AUDIO_INFO_LIST = "audio_info_list"
    const val EXTRA_MEDIA_ID = "media_id"
    const val EXTRAS_STATUS = "status"
    const val EXTRAS_PROGRESS = "progress"
    const val EXTRAS_INDEX = "index"
    const val EXTRAS_DATA = "data"
    
    const val ACTION_REQUEST_STATUS = "request_status"
    const val ACTION_UPDATE_PLAY_MODE = "update_play_mode"
    const val ACTION_EXTRA = "action_extra"
    const val ACTION_REQUEST_AUDIO_LIST = "request_audio_list"
    
    const val EXTRAS_PLAY = "play"
    const val EXTRAS_PAUSE = "pause"

    const val ACTION_LOUDNESS_ENHANCER = "loudness_enhancer"
    const val LOUDNESS_ENHANCER_DISABLE = 0
    const val LOUDNESS_ENHANCER_ENABLE = 1
    const val LOUDNESS_ENHANCER_SET = 2

    const val ACTION_EQUALIZER = "equalizer"
    const val EQUALIZER_DISABLE = 0
    const val EQUALIZER_ENABLE = 1
    const val EQUALIZER_GET = 2
    const val EQUALIZER_SET = 3

    const val ACTION_AUDIO_SESSION = "audio_session"

    const val EXTRAS_PLAY_MODE = "play_mode"
    const val PLAY_MODE_SINGLE = 1
    const val PLAY_MODE_SINGLE_CYCLE = 2
    const val PLAY_MODE_LIST = 4
    const val PLAY_MODE_RANDOM = 8
    
    const val TRANSITION_IMAGE_VIEW = "image_view"
    
    const val FILTER_NOTIFICATION_PREV = "${BuildConfig.APPLICATION_ID}.NotificationBroadcast.Prev"
    const val FILTER_NOTIFICATION_PLAY = "${BuildConfig.APPLICATION_ID}.NotificationBroadcast.Play"
    const val FILTER_NOTIFICATION_PAUSE = "${BuildConfig.APPLICATION_ID}.NotificationBroadcast.Pause"
    const val FILTER_NOTIFICATION_NEXT = "${BuildConfig.APPLICATION_ID}.NotificationBroadcast.Next"

    const val EXTRA_WEBSERVER = "webserver"
    const val EXTRA_WEBSERVER_START = 1
    const val EXTRA_WEBSERVER_STOP = 0

    const val ANIMATION_DURATION = 500
    const val ANIMATION_DURATION_LONG = ANIMATION_DURATION.toLong()
    
}