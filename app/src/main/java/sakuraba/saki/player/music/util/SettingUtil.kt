package sakuraba.saki.player.music.util

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

object SettingUtil {
    
    const val KEY_AUDIO = "key_audio"
    
    const val KEY_AUDIO_FILTER_SIZE_ENABLE = "key_audio_filter_size_enable"
    const val KEY_AUDIO_FILTER_SIZE_VALUE = "key_audio_filter_size_value"
    const val KEY_AUDIO_FILTER_DURATION_ENABLE = "key_audio_filter_duration_enable"
    const val KEY_AUDIO_FILTER_DURATION_VALUE = "key_audio_filter_duration_value"
    
    val Activity.defaultSharedPreference get() = PreferenceManager.getDefaultSharedPreferences(this)!!
    val Fragment.defaultSharedPreference get() = requireActivity().defaultSharedPreference
    
    fun Activity.getIntSetting(key: String): Int? {
        val sharedPreferenceManager = defaultSharedPreference
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getInt(key, -1)
    }
    
    fun Activity.getIntSettingOrThrow(key: String): Int = getIntSetting(key) ?: throw Exception()
    
    fun Fragment.getIntSetting(key: String) = requireActivity().getIntSetting(key)
    
    fun Fragment.getIntSettingOrThrow(key: String) = getIntSetting(key) ?: throw Exception()
    
    /**************************************************************************************/
    
    fun Activity.getStringSetting(key: String): String? {
        val sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)
    }
    
    fun Activity.getStringSettingOrThrow(key: String): String = getStringSetting(key) ?: throw Exception()
    
    fun Fragment.getStringSetting(key: String) = requireActivity().getStringSetting(key)
    
    fun Fragment.getStringSettingOrThrow(key: String) = getStringSetting(key) ?: throw Exception()
    
}