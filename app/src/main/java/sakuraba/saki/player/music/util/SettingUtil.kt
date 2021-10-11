package sakuraba.saki.player.music.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

object SettingUtil {
    
    const val KEY_AUDIO = "key_audio"
    const val KEY_PLAY = "key_play"
    
    const val KEY_AUDIO_FILTER_SIZE_ENABLE = "key_audio_filter_size_enable"
    const val KEY_AUDIO_FILTER_SIZE_VALUE = "key_audio_filter_size_value"
    const val KEY_AUDIO_FILTER_DURATION_ENABLE = "key_audio_filter_duration_enable"
    const val KEY_AUDIO_FILTER_DURATION_VALUE = "key_audio_filter_duration_value"
    const val KEY_PLAY_AUDIO_FOCUS_ENABLE = "key_play_audio_focus_enable"
    const val KEY_PLAY_FADE_IN_ENABLE = "key_play_fade_in_enable"
    const val KEY_PLAY_FADE_OUT_ENABLE = "key_play_fade_out_enable"
    
    val Context.defaultSharedPreference get() = PreferenceManager.getDefaultSharedPreferences(this)!!
    val Fragment.defaultSharedPreference get() = requireContext().defaultSharedPreference
    
    fun Context.getIntSetting(key: String): Int? {
        val sharedPreferenceManager = defaultSharedPreference
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)?.toInt()
    }
    
    fun Context.getIntSettingOrThrow(key: String): Int = getIntSetting(key) ?: throw Exception()
    
    fun Fragment.getIntSetting(key: String) = requireContext().getIntSetting(key)
    
    fun Fragment.getIntSettingOrThrow(key: String) = requireContext().getIntSettingOrThrow(key)
    
    /**************************************************************************************/
    
    fun Context.getStringSetting(key: String): String? {
        val sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)
    }
    
    fun Context.getStringSettingOrThrow(key: String): String = getStringSetting(key) ?: throw Exception()
    
    fun Fragment.getStringSetting(key: String) = requireContext().getStringSetting(key)
    
    fun Fragment.getStringSettingOrThrow(key: String) = requireContext().getStringSettingOrThrow(key)
    
    /**************************************************************************************/
    fun Context.getBooleanSetting(key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences.contains(key)) {
            return false
        }
        return sharedPreferences.getBoolean(key, false)
    }
    
    fun Fragment.getBooleanSetting(key: String) = requireContext().getBooleanSetting(key)
    
}