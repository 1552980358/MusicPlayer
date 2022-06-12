package projekt.cloud.piece.music.player.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Configs
import projekt.cloud.piece.music.player.service.play.SharedPreferences.DEFAULT_CONFIG_PLAY_REPEAT
import projekt.cloud.piece.music.player.service.play.SharedPreferences.DEFAULT_CONFIG_PLAY_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.SharedPreferences.DEFAULT_CONFIG_PLAY_SHUFFLE
import projekt.cloud.piece.music.player.service.play.SharedPreferences.KEY_CONFIG_PLAY_REPEAT
import projekt.cloud.piece.music.player.service.play.SharedPreferences.KEY_CONFIG_PLAY_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.SharedPreferences.KEY_CONFIG_PLAY_SHUFFLE
import projekt.cloud.piece.music.player.util.CoroutineUtil.io

/**
 * [DataStore]
 *
 * Methods:
 * [settingsStore]
 * [writeConfig]
 **/
object DataStore {

    private const val SETTINGS_STORE = "settings"

    private val Context.settingsStore by preferencesDataStore(name = SETTINGS_STORE)

    fun Context.readConfigs(configs: Configs, onComplete: (Configs) -> Unit) = io {
        settingsStore.data.collect {
            configs[CONFIG_PLAY_REPEAT] = it[booleanPreferencesKey(KEY_CONFIG_PLAY_REPEAT)] ?: DEFAULT_CONFIG_PLAY_REPEAT
            configs[CONFIG_PLAY_REPEAT_ONE] = it[booleanPreferencesKey(KEY_CONFIG_PLAY_REPEAT_ONE)] ?: DEFAULT_CONFIG_PLAY_REPEAT_ONE
            configs[CONFIG_PLAY_SHUFFLE] = it[booleanPreferencesKey(KEY_CONFIG_PLAY_SHUFFLE)] ?: DEFAULT_CONFIG_PLAY_SHUFFLE
            onComplete.invoke(configs)
        }
    }

    fun Context.writeConfig(configs: Configs) = io {
        settingsStore.edit {
            it[booleanPreferencesKey(KEY_CONFIG_PLAY_REPEAT)] = configs.isTrue(CONFIG_PLAY_REPEAT)
            it[booleanPreferencesKey(KEY_CONFIG_PLAY_REPEAT_ONE)] = configs.isTrue(CONFIG_PLAY_REPEAT_ONE)
            it[booleanPreferencesKey(KEY_CONFIG_PLAY_SHUFFLE)] = configs.isTrue(CONFIG_PLAY_SHUFFLE)
        }
    }

}