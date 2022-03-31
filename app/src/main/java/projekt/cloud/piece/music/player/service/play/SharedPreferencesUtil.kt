package projekt.cloud.piece.music.player.service.play

import android.content.SharedPreferences
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_AUDIO_FOCUS
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.SERVICE_CONFIG_FOREGROUND_SERVICE
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.service.play.Config.shl

object SharedPreferencesUtil {

    val DEFAULT_CONFIG = PLAY_CONFIG_REPEAT.shl or PLAY_CONFIG_AUDIO_FOCUS.shl
    
    private const val CONFIGS = "configs"

    val SharedPreferences.hasConfig get() = contains(CONFIGS)

    val SharedPreferences.readConfigs get() =
        getInt(CONFIGS, DEFAULT_CONFIG)

    fun SharedPreferences.writeConfigs(configs: Int) =
        edit().putInt(CONFIGS, configs.setConfig(SERVICE_CONFIG_FOREGROUND_SERVICE, false)).commit()
    
}